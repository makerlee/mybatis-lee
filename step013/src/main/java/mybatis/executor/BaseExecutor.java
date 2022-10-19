package mybatis.executor;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mybatis.mapping.BoundSql;
import mybatis.mapping.MappedStatement;
import mybatis.session.Configuration;
import mybatis.session.ResultHandler;
import mybatis.session.RowBounds;
import mybatis.transaction.Transaction;

/**
 * @Description 执行器基类
 * @Author jiyang.li
 * @Date 2022/9/23 21:07
 **/
public abstract class BaseExecutor implements Executor {
	private Logger logger = LoggerFactory.getLogger(BaseExecutor.class);

	protected Configuration configuration;
	protected Transaction transaction;
	protected Executor wrapper;

	private boolean closed;

	public BaseExecutor(Configuration configuration, Transaction transaction) {
		this.configuration = configuration;
		this.transaction = transaction;
		this.wrapper = this;
	}

	@Override
	public int update(MappedStatement ms, Object parameter) throws SQLException {
		return doUpdate(ms, parameter);
	}

	@Override
	public <E> List<E> query(MappedStatement ms, Object param, RowBounds rowBounds, ResultHandler resultHandler,
			BoundSql boundSql) {
		if (closed) {
			throw new RuntimeException("Executor was closed.");
		}
		return doQuery(ms, param, rowBounds, resultHandler, boundSql);
	}

	// 子类实现
	protected abstract int doUpdate(MappedStatement ms, Object parameter) throws SQLException;

	// 子类实现
	protected abstract <E> List<E> doQuery(MappedStatement ms, Object param, RowBounds rowBounds,
			ResultHandler resultHandler, BoundSql boundSql);

	@Override
	public Transaction getTransaction() {
		if (closed) {
			throw new RuntimeException("Executor was closed");
		}
		return transaction;
	}

	@Override
	public void commit(boolean required) throws SQLException {
		if (closed) {
			throw new RuntimeException("cant commit, transaction was already closed");
		}
		if (required) {
			transaction.commit();
		}
	}

	@Override
	public void rollback(boolean required) throws SQLException {
		if (!closed && required) {
			transaction.rollback();
		}
	}

	@Override
	public void close(boolean force) {
		try {
			try {
				rollback(force);
			} finally {
				transaction.close();
			}
		} catch (SQLException e) {
			logger.warn("Unexpected exception on closing transaction.  Cause: \" + e");
		} finally {
			transaction = null;
			closed = true;
		}
	}

	protected void closeStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException ignore) {
			}
		}
	}
}
