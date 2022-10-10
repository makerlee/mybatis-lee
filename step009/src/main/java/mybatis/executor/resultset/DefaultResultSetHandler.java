package mybatis.executor.resultset;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mybatis.executor.Executor;
import mybatis.mapping.BoundSql;
import mybatis.mapping.MappedStatement;

/**
 * @Description 默认map结果集处理器
 * @Author jiyang.li
 * @Date 2022/9/24 08:40
 **/
public class DefaultResultSetHandler implements ResultSetHandler {
	private final BoundSql boundSql;

	public DefaultResultSetHandler(Executor executor, MappedStatement ms, BoundSql boundSql) {
		this.boundSql = boundSql;
	}

	@Override
	public <E> List<E> handleResultSet(Statement statement) throws SQLException {
		ResultSet resultSet = statement.getResultSet();
		try {
			return resultSetToObject(resultSet, Class.forName(boundSql.getResultType()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private <E> List<E> resultSetToObject(ResultSet resultSet, Class<?> resultType) {
		List<E> list = new ArrayList<>();
		try {
			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnCount = metaData.getColumnCount();
			while (resultSet.next()) {
				E obj = (E) resultType.newInstance();
				for (int i = 1; i < columnCount; i++) {
					String columnName = metaData.getColumnName(i);
					Object colValue = resultSet.getObject(i);
					String setMethodName = "set" + columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
					Method method;
					if (colValue instanceof Timestamp) {
						method = resultType.getMethod(setMethodName, Date.class);
					} else {
						method = resultType.getMethod(setMethodName, colValue.getClass());
					}
					method.invoke(obj, colValue);
				}
				list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}
