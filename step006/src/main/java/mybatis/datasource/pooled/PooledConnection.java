package mybatis.datasource.pooled;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Description 池化连接 代理
 * @Author jiyang.li
 * @Date 2022/9/21 17:24
 **/
public class PooledConnection implements InvocationHandler {
    private static final Class<?>[] IFACES = new Class<?>[]{Connection.class};
    private static final String CLOSE = "close";

    private int connHashCode = 0;
    private PooledDataSource pooledDataSource;
    // 真实的连接
    private Connection connection;
    // 代理的连接
    private Connection proxyConnection;

    private long checkoutTimestamp;
    private long createdTimestamp;
    private long lastUsedTimestamp;
    private int connectionTypeCode;
    private boolean valid;

    public PooledConnection(PooledDataSource pooledDataSource, Connection connection) {
        this.pooledDataSource = pooledDataSource;
        this.connection = connection;
        this.connHashCode = connection.hashCode();
        this.createdTimestamp = System.currentTimeMillis();
        this.lastUsedTimestamp = System.currentTimeMillis();
        this.valid = true;
        this.proxyConnection = (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), IFACES, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        if (methodName.equals(CLOSE)) {
            pooledDataSource.pushConnection(this);
            return null;
        } else {
            if (!Object.class.equals(method.getDeclaringClass())) {
                checkConnection();
            }
            return method.invoke(connection, args);
        }
    }

    private void checkConnection() throws SQLException {
        if (!valid) {
            throw new SQLException("Error accessing PooledConnection. Connection is invalid.");
        }
    }

    public void invalidate() {
        valid = false;
    }

    public boolean isValid() {
        return valid && connection != null && pooledDataSource.pingConnection(this);
    }

    public Connection getRealConnection() {
        return connection;
    }

    public Connection getProxyConnection() {
        return proxyConnection;
    }

    public int getRealHashCode() {
        return connection == null ? 0 : connection.hashCode();
    }

    public int getConnectionTypeCode() {
        return connectionTypeCode;
    }

    public void setConnectionTypeCode(int connectionTypeCode) {
        this.connectionTypeCode = connectionTypeCode;
    }

    public long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(long createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public long getLastUsedTimestamp() {
        return lastUsedTimestamp;
    }

    public void setLastUsedTimestamp(long lastUsedTimestamp) {
        this.lastUsedTimestamp = lastUsedTimestamp;
    }

    public long getTimeElapsedSinceLastUse() {
        return System.currentTimeMillis() - lastUsedTimestamp;
    }

    public long getAge() {
        return System.currentTimeMillis() - createdTimestamp;
    }

    public long getCheckoutTimestamp() {
        return checkoutTimestamp;
    }

    public void setCheckoutTimestamp(long timestamp) {
        this.checkoutTimestamp = timestamp;
    }

    public long getCheckoutTime() {
        return System.currentTimeMillis() - checkoutTimestamp;
    }

    @Override
    public int hashCode() {
        return connHashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PooledConnection) {
            return connection.hashCode() == (((PooledConnection) obj).connection.hashCode());
        } else if (obj instanceof Connection) {
            return connHashCode == obj.hashCode();
        } else {
            return false;
        }
    }
}
