package mybatis.session.impl;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import mybatis.mapping.BoundSql;
import mybatis.mapping.Environment;
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
		try {
			MappedStatement ms = configuration.getStatement(statementId);
			Environment environment = configuration.getEnvironment();
			Connection connection = environment.getDataSource().getConnection();

			BoundSql boundSql = ms.getBoundSql();
			PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
			preparedStatement.setInt(1, Integer.parseInt(((Object[]) parameter)[0].toString()));

			ResultSet resultSet = preparedStatement.executeQuery();
			List<T> result = resultSet2Obj(resultSet, Class.forName(boundSql.getResultType()));

			return result.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private <T> List<T> resultSet2Obj(ResultSet resultSet, Class<?> resultType) {
		List<T> list = new ArrayList<>();
		try {
			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (resultSet.next()) {
				T obj = (T) resultType.newInstance();
				for (int i = 1; i < columnCount; i++) {
					Object value = resultSet.getObject(i);
					String colName = metaData.getColumnName(i);
					String setMethod = "set" + colName.substring(0, 1).toUpperCase() + colName.substring(1);
					Method method;
					if (value instanceof Timestamp) {
						method = resultType.getMethod(setMethod, Date.class);
					} else {
						method = resultType.getMethod(setMethod, value.getClass());
					}
					method.invoke(obj, value);
				}
				list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
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
