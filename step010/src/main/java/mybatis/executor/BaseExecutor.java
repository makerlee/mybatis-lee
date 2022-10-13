package mybatis.executor;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mybatis.mapping.BoundSql;
import mybatis.mapping.MappedStatement;
import mybatis.session.Configuration;
import mybatis.session.ResultHandler;
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
	public <E> List<E> query(MappedStatement ms, Object param, ResultHandler resultHandler, BoundSql boundSql) {
		if (closed) {
			throw new RuntimeException("Executor was closed.");
		}
		return doQuery(ms, param, resultHandler, boundSql);
	}

	// 子类实现
	protected abstract <E> List<E> doQuery(MappedStatement ms, Object param, ResultHandler resultHandler,
			BoundSql boundSql);

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
}
