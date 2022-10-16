package mybatis.datasource.pooled;

import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.slf4j.LoggerFactory;

import mybatis.datasource.unpooled.UnPooledDataSource;

/**
 * @Description 池化的数据源
 * @Author jiyang.li
 * @Date 2022/9/21 16:59
 **/
public class PooledDataSource implements DataSource {
	private org.slf4j.Logger logger = LoggerFactory.getLogger(PooledDataSource.class);

	// 池状态
	private final PoolState state = new PoolState(this);

	public PoolState getState() {
		return state;
	}

	// 直接使用非池化的实现
	private final UnPooledDataSource unPooledDataSource;

	// 最大活跃连接数
	protected int poolMaxActiveConnections = 9;

	// 最大空闲连接数
	protected int poolMaxIdleConnections = 5;

	// 在被强制返回之前,池中连接被检查的时间
	protected int poolMaximumCheckoutTime = 10000;

	// 这是给连接池一个打印日志状态机会的低层次设置,还有重新尝试获得连接, 这些情况下往往需要很长时间
	// 为了避免连接池没有配置时静默失败)。
	protected int poolTimeToWait = 10000;

	// 发送到数据的侦测查询,用来验证连接是否正常工作,并且准备 接受请求。默认是“NO PING QUERY SET” ,
	// 这会引起许多数据库驱动连接由一个错误信息而导致失败
	protected String poolPingQuery = "NO PING QUERY SET";

	// 开启或禁用侦测查询
	protected boolean poolPingEnabled = false;

	// 用来配置 poolPingQuery 多次时间被用一次
	protected int poolPingConnectionsNotUsedFor = 0;

	private int expectedConnectionTypeCode;

	public PooledDataSource() {
		this.unPooledDataSource = new UnPooledDataSource();
	}

	// 回收连接
	public void pushConnection(PooledConnection connection) throws SQLException {
		synchronized (state) {
			state.activeConnections.remove(connection);
			if (connection.isValid()) {
				if (state.idleConnections.size() < poolMaxIdleConnections
						&& connection.getConnectionTypeCode() == expectedConnectionTypeCode) {
					state.accumulatedCheckoutTime += connection.getCheckoutTime();
					if (!connection.getRealConnection().getAutoCommit()) {
						connection.getRealConnection().rollback();
					}
					// 实例化一个新的连接 加入idle列表
					PooledConnection newConn = new PooledConnection(this, connection.getRealConnection());
					state.idleConnections.add(newConn);
					newConn.setCreatedTimestamp(connection.getCreatedTimestamp());
					newConn.setLastUsedTimestamp(connection.getLastUsedTimestamp());
					connection.invalidate();
					logger.info("Returned connection " + newConn.getRealHashCode() + " to pool.");

					// 通知其他线程可以来抢DB连接了
					state.notifyAll();
				} else {// 空闲连接充足，直接close
					state.accumulatedCheckoutTime += connection.getCheckoutTime();
					if (!connection.getRealConnection().getAutoCommit()) {
						connection.getRealConnection().rollback();
					}
					// 将connection关闭
					connection.getRealConnection().close();
					logger.info("Closed connection " + connection.getRealHashCode() + ".");
					connection.invalidate();
				}
			} else {
				logger.info("A bad connection (" + connection.getRealHashCode()
						+ ") attempted to return to the pool, discarding connection.");
				state.badConnectionCount++;
			}
		}
	}

	// 从池中获取连接
	private PooledConnection popConnection(String username, String password) throws SQLException {
		PooledConnection pooledConnection = null;
		boolean countedWait = false;
		long t = System.currentTimeMillis();
		int localBadConnectionCount = 0;

		while (pooledConnection == null) {
			synchronized (state) {
				// 有空闲连接 返回第一个
				if (!state.idleConnections.isEmpty()) {
					pooledConnection = state.idleConnections.remove(0);
					logger.info("checkout connection " + pooledConnection.getRealHashCode() + " from pool");
				} else {// 无空闲连接
					// 活跃连接数没有达到最大值，新建
					if (state.activeConnections.size() < poolMaxActiveConnections) {
						pooledConnection = new PooledConnection(this, unPooledDataSource.getConnection());
						logger.info("created new connection" + pooledConnection.getRealHashCode());
					} else {
						PooledConnection oldestActiveConn = state.activeConnections.get(0);
						long oldestActiveConnCheckoutTime = oldestActiveConn.getCheckoutTime();
						long checkoutTime = oldestActiveConn.getCheckoutTime();
						// checkout时间过长，标记为已过期
						if (checkoutTime > poolMaximumCheckoutTime) {
							state.claimedOverdueConnectionCount++;
							state.accumulatedCheckoutTimeOfOverdueConnections += oldestActiveConnCheckoutTime;
							state.accumulatedCheckoutTime += oldestActiveConnCheckoutTime;
							// 从active列表移除
							state.activeConnections.remove(oldestActiveConn);
							// 事务没有提交，手动回滚
							if (!oldestActiveConn.getRealConnection().getAutoCommit()) {
								oldestActiveConn.getRealConnection().rollback();
							}
							// 实例化一个新的
							pooledConnection = new PooledConnection(this, oldestActiveConn.getRealConnection());
							oldestActiveConn.invalidate();
							logger.info("Claimed overdue connection " + pooledConnection.getRealHashCode() + ".");
						}
						// 如果checkout超时时间不够长，则等待
						else {
							try {
								if (!countedWait) {
									state.hadToWaitCount++;
									countedWait = true;
								}
								logger.info("Waiting as long as " + poolTimeToWait + " milliseconds for connection.");
								long wt = System.currentTimeMillis();
								state.wait(poolTimeToWait);
								state.accumulatedWaitTime += System.currentTimeMillis() - wt;
							} catch (InterruptedException e) {
								break;
							}
						}
					}
				}

				if (pooledConnection != null) {
					if (pooledConnection.isValid()) {
						if (!pooledConnection.getRealConnection().getAutoCommit()) {
							pooledConnection.getRealConnection().rollback();
						}
						pooledConnection.setConnectionTypeCode(
								assembleConnectionTypeCode(unPooledDataSource.getUrl(), username, password));
						pooledConnection.setCheckoutTimestamp(System.currentTimeMillis());
						pooledConnection.setLastUsedTimestamp(System.currentTimeMillis());

						state.activeConnections.add(pooledConnection);
						state.requestCount++;
						state.accumulatedRequestTime += System.currentTimeMillis() - t;
					} else {
						logger.info("A bad connection (" + pooledConnection.getRealHashCode()
								+ ") was returned from the pool, getting another connection.");
						// 如果没拿到，统计信息：失败链接 +1
						state.badConnectionCount++;
						localBadConnectionCount++;
						pooledConnection = null;
						// 失败次数较多，抛异常
						if (localBadConnectionCount > (poolMaxIdleConnections + 3)) {
							logger.debug("PooledDataSource: Could not get a good connection to the database.");
							throw new SQLException(
									"PooledDataSource: Could not get a good connection to the database.");
						}
					}
				}
			}
		}
		return pooledConnection;
	}

	public void forceCloseAll() {
		synchronized (state) {
			expectedConnectionTypeCode = assembleConnectionTypeCode(unPooledDataSource.getUrl(),
					unPooledDataSource.getUsername(), unPooledDataSource.getPasswd());
			for (int i = state.activeConnections.size(); i > 0; i--) {
				try {
					PooledConnection connection = state.activeConnections.remove(i - 1);
					connection.invalidate();
					Connection realConnection = connection.getRealConnection();
					realConnection.close();
					if (!realConnection.getAutoCommit()) {
						realConnection.rollback();
					}
				} catch (SQLException e) {
					// ignore
				}
			}
			for (int i = state.idleConnections.size(); i > 0; i--) {
				try {
					PooledConnection connection = state.activeConnections.remove(i - 1);
					connection.invalidate();
					Connection realConnection = connection.getRealConnection();
					realConnection.close();
					if (!realConnection.getAutoCommit()) {
						realConnection.rollback();
					}
				} catch (SQLException e) {
					// ignore
				}
			}
			logger.info("datasource force closing all connection successfully");
		}
	}

	private int assembleConnectionTypeCode(String url, String username, String password) {
		return ("" + url + username + password).hashCode();
	}

	@Override
	public Connection getConnection() throws SQLException {
		return popConnection(unPooledDataSource.getUsername(), unPooledDataSource.getPasswd()).getProxyConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return popConnection(username, password).getProxyConnection();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException(getClass().getName() + " is not a wrapper.");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return DriverManager.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		DriverManager.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		DriverManager.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return DriverManager.getLoginTimeout();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	}

	protected boolean pingConnection(PooledConnection connection) {
		boolean result = true;
		try {
			result = !connection.getRealConnection().isClosed();
		} catch (SQLException e) {
			result = false;
			e.printStackTrace();
		}
		if (!result || !poolPingEnabled) {
			return result;
		}
		if (poolPingConnectionsNotUsedFor > 0
				&& connection.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor) {
			try {
				logger.info("Testing conn " + connection.getRealHashCode() + "...");
				Connection realConnection = connection.getRealConnection();
				Statement statement = realConnection.createStatement();
				ResultSet resultSet = statement.executeQuery(poolPingQuery);
				resultSet.close();
				if (!realConnection.getAutoCommit()) {
					realConnection.rollback();
				}
				result = true;
				logger.info("Connection " + connection.getRealHashCode() + " is alive!");
			} catch (Exception e) {
				try {
					connection.getRealConnection().close();
				} catch (SQLException ignore) {
				}
				result = false;
				logger.warn("Connection " + connection.getRealHashCode() + " is bad:" + e.getMessage());
			}
		}
		return result;
	}

	// 获取被代理的connection
	public static Connection unwrapConnection(Connection connection) {
		if (Proxy.isProxyClass(connection.getClass())) {
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(connection);
			if (invocationHandler instanceof PooledConnection) {
				return ((PooledConnection) invocationHandler).getRealConnection();
			}
		}
		return connection;
	}

	public void setDriver(String driver) {
		unPooledDataSource.setDriver(driver);
		forceCloseAll();
	}

	public void setUrl(String url) {
		unPooledDataSource.setUrl(url);
		forceCloseAll();
	}

	public void setUsername(String username) {
		unPooledDataSource.setUsername(username);
		forceCloseAll();
	}

	public void setPassword(String password) {
		unPooledDataSource.setPasswd(password);
		forceCloseAll();
	}


	public void setDefaultAutoCommit(boolean defaultAutoCommit) {
		unPooledDataSource.setAutoCommit(defaultAutoCommit);
		forceCloseAll();
	}

	public int getPoolMaxActiveConnections() {
		return poolMaxActiveConnections;
	}

	public void setPoolMaxActiveConnections(int poolMaxActiveConnections) {
		this.poolMaxActiveConnections = poolMaxActiveConnections;
	}

	public int getPoolMaxIdleConnections() {
		return poolMaxIdleConnections;
	}

	public void setPoolMaxIdleConnections(int poolMaxIdleConnections) {
		this.poolMaxIdleConnections = poolMaxIdleConnections;
	}

	public int getPoolMaximumCheckoutTime() {
		return poolMaximumCheckoutTime;
	}

	public void setPoolMaximumCheckoutTime(int poolMaximumCheckoutTime) {
		this.poolMaximumCheckoutTime = poolMaximumCheckoutTime;
	}

	public int getPoolTimeToWait() {
		return poolTimeToWait;
	}

	public void setPoolTimeToWait(int poolTimeToWait) {
		this.poolTimeToWait = poolTimeToWait;
	}

	public String getPoolPingQuery() {
		return poolPingQuery;
	}

	public void setPoolPingQuery(String poolPingQuery) {
		this.poolPingQuery = poolPingQuery;
	}

	public boolean isPoolPingEnabled() {
		return poolPingEnabled;
	}

	public void setPoolPingEnabled(boolean poolPingEnabled) {
		this.poolPingEnabled = poolPingEnabled;
	}

	public int getPoolPingConnectionsNotUsedFor() {
		return poolPingConnectionsNotUsedFor;
	}

	public void setPoolPingConnectionsNotUsedFor(int poolPingConnectionsNotUsedFor) {
		this.poolPingConnectionsNotUsedFor = poolPingConnectionsNotUsedFor;
	}

	public int getExpectedConnectionTypeCode() {
		return expectedConnectionTypeCode;
	}

	public void setExpectedConnectionTypeCode(int expectedConnectionTypeCode) {
		this.expectedConnectionTypeCode = expectedConnectionTypeCode;
	}
}
