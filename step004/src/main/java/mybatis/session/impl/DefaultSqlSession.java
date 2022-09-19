package mybatis.session.impl;

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

	public DefaultSqlSession(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public <T> T selectOne(String statementId) {
		return (T) ("代理:" + statementId);
	}

	@Override
	public <T> T selectOne(String statementId, Object parameter) {
		MappedStatement statement = configuration.getStatement(statementId);

		return (T) ("代理:" + statement.getId() + ",参数:" + parameter + ",sql:" + statement.getSql());
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
