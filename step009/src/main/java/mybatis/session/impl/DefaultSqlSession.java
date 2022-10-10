package mybatis.session.impl;

import java.util.List;

import mybatis.executor.Executor;
import mybatis.mapping.MappedStatement;
import mybatis.session.Configuration;
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
		return (T) ("代理:" + statementId);
	}

	@Override
	public <T> T selectOne(String statementId, Object parameter) {
		try {
			MappedStatement ms = configuration.getStatement(statementId);
			List<T> result = executor.query(ms, parameter, Executor.NO_RESULT_HANDLER, ms.getBoundSql());
			return result.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public <T> T getMapper(Class<T> type) {
		return configuration.getMapper(type, this);
	}

	@Override
	public Configuration getConfiguration() {
		return configuration;
	}
}
