package mybatis.session.impl;

import java.sql.SQLException;
import java.util.List;

import mybatis.executor.Executor;
import mybatis.mapping.MappedStatement;
import mybatis.session.Configuration;
import mybatis.session.RowBounds;
import mybatis.session.SqlSession;

/**
 * @Description sqlSession默认实现
 * @Author jiyang.li
 * @Date 2022/9/17 16:18
 **/
public class DefaultSqlSession implements SqlSession {

	private Configuration configuration;
	private Executor executor;

	public DefaultSqlSession(Configuration configuration, Executor executor) {
		this.configuration = configuration;
		this.executor = executor;
	}

	@Override
	public <T> T selectOne(String statementId) {
		return this.selectOne(statementId, null);
	}

	@Override
	public <T> T selectOne(String statementId, Object parameter) {
		try {
			MappedStatement ms = configuration.getStatement(statementId);
			List<T> result = executor.query(ms, parameter, RowBounds.DEFAULT, Executor.NO_RESULT_HANDLER,
					ms.getSqlSource().getBoundSql(parameter));
			return result.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public int insert(String statement, Object parameter) {
		// 在mybatis中，insert调用的是update
		return update(statement, parameter);
	}

	@Override
	public int update(String statement, Object parameter) {
		MappedStatement ms = configuration.getStatement(statement);
		try {
			return executor.update(ms, parameter);
		} catch (SQLException e) {
			throw new RuntimeException("Error updating database. Cause:" + e);
		}
	}

	@Override
	public Object delete(String statement, Object parameter) {
		return update(statement, parameter);
	}

	@Override
	public <T> T getMapper(Class<T> type) {
		return configuration.getMapper(type, this);
	}

	@Override
	public void commit() {
		try {
			executor.commit(true);
		} catch (SQLException e) {
			throw new RuntimeException("Error committing transaction.  Cause: " + e);
		}
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}
}
