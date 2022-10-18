package mybatis.executor.resultset;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mybatis.executor.Executor;
import mybatis.executor.result.DefaultResultContext;
import mybatis.executor.result.DefaultResultHandler;
import mybatis.mapping.BoundSql;
import mybatis.mapping.MappedStatement;
import mybatis.mapping.ResultMap;
import mybatis.mapping.ResultMapping;
import mybatis.reflection.MetaClass;
import mybatis.reflection.MetaObject;
import mybatis.reflection.factory.ObjectFactory;
import mybatis.session.Configuration;
import mybatis.session.ResultHandler;
import mybatis.session.RowBounds;
import mybatis.type.TypeHandler;
import mybatis.type.TypeHandlerRegistry;

/**
 * @Description 默认map结果集处理器
 * @Author jiyang.li
 * @Date 2022/9/24 08:40
 **/
public class DefaultResultSetHandler implements ResultSetHandler {
	private final Configuration configuration;
	private final MappedStatement ms;
	private final RowBounds rowBounds;
	private final ResultHandler resultHandler;
	private final BoundSql boundSql;
	private final TypeHandlerRegistry typeHandlerRegistry;
	private final ObjectFactory objectFactory;

	public DefaultResultSetHandler(Executor executor, MappedStatement ms, ResultHandler resultHandler,
			RowBounds rowBounds, BoundSql boundSql) {
		this.ms = ms;
		this.configuration = ms.getConfiguration();
		this.boundSql = boundSql;
		this.rowBounds = rowBounds;
		this.resultHandler = resultHandler;
		this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
		this.objectFactory = configuration.getObjectFactory();
	}

	@Override
	public List<Object> handleResultSet(Statement statement) throws SQLException {
		List<Object> multipleResults = new ArrayList<>();
		int resultSetCount = 0;
		ResultSetWrapper wrapper = new ResultSetWrapper(configuration, statement.getResultSet());

		List<ResultMap> resultMaps = ms.getResultMaps();
		while (wrapper != null && resultMaps.size() > resultSetCount) {
			ResultMap resultMap = resultMaps.get(resultSetCount);
			handleResultSet(wrapper, resultMap, multipleResults, null);
			resultSetCount++;
		}
		return multipleResults.size() == 1 ? (List<Object>) multipleResults.get(0) : multipleResults;
	}

	private void handleResultSet(ResultSetWrapper wrapper, ResultMap resultMap, List<Object> multiResultSet,
			ResultMapping parentMapping) throws SQLException {
		// 1.创建结果处理器
		DefaultResultHandler defaultResultHandler = new DefaultResultHandler(objectFactory);
		// 2.封装数据
		handleRowValueForSimpleResultMap(wrapper, resultMap, defaultResultHandler, rowBounds, null);
		// 3.保存结果
		multiResultSet.add(defaultResultHandler.getResultList());
	}

	private void handleRowValueForSimpleResultMap(ResultSetWrapper wrapper, ResultMap resultMap,
			DefaultResultHandler resultHandler, RowBounds rowBounds, ResultMapping resultMapping)
			throws SQLException {
		DefaultResultContext resultContext = new DefaultResultContext();
		// limit
		while (resultContext.getResultCount() < rowBounds.getLimit() && wrapper.getResultSet().next()) {
			Object rowValue = getRowValue(wrapper, resultMap);
			callResultHandler(resultHandler, resultContext, rowValue);
		}
	}

	private void callResultHandler(ResultHandler resultHandler, DefaultResultContext resultContext, Object rowValue) {
		resultContext.nextResultObject(rowValue);
		resultHandler.handleResult(resultContext);
	}

	/**
	 * 获取一行的数据
	 *
	 * @param wrapper
	 *            ResultSetWrapper
	 * @param resultMap
	 *            ResultMap
	 * @return
	 */
	private Object getRowValue(ResultSetWrapper wrapper, ResultMap resultMap) throws SQLException {
		// 根据返回类型，实例化对象
		Object resultObject = createResultObject(wrapper, resultMap, null);
		if (resultObject != null && !typeHandlerRegistry.hasTypeHandler(resultMap.getType())) {
			MetaObject metaObject = configuration.newMetaObject(resultObject);
			applyAutomaticMapping(wrapper, resultMap, metaObject, null);
		}
		return resultObject;
	}

	private boolean applyAutomaticMapping(ResultSetWrapper resultSetWrapper, ResultMap resultMap, MetaObject metaObject,
			String columnPrefix) throws SQLException {
		List<String> unmappedColumnNames = resultSetWrapper.getUnmappedColumnNames(resultMap, columnPrefix);
		boolean foundValues = false;
		for (String column : unmappedColumnNames) {
			String propertyName = column;
			// 如果已经包含前缀
			if (columnPrefix != null && !columnPrefix.isEmpty()) {
				if (column.toUpperCase(Locale.ENGLISH).startsWith(columnPrefix)) {
					propertyName = column.substring(columnPrefix.length());
				} else {
					continue;
				}
			}
			final String property = metaObject.findProperty(propertyName, false);
			if (property != null && metaObject.hasSetter(property)) {
				Class<?> propertyType = metaObject.getSetterType(property);
				if(typeHandlerRegistry.hasTypeHandler(propertyType)) {
					TypeHandler<?> typeHandler = resultSetWrapper.getTypeHandler(propertyType, column);
					Object value = typeHandler.getResult(resultSetWrapper.getResultSet(), column);
					if(value != null) {
						foundValues = true;
					}
					if (value != null || !propertyType.isPrimitive()) {
						metaObject.setValue(property, value);
					}
				}
			}
		}
		return foundValues;
	}

	private Object createResultObject(ResultSetWrapper wrapper, ResultMap resultMap, String columnPrefix) {
		final List<Class<?>> constructorArgTypes = new ArrayList<>();
		final List<Object> constructorArgs = new ArrayList<>();
		return createResultObject(wrapper, resultMap, constructorArgTypes, constructorArgs, columnPrefix);
	}

	private Object createResultObject(ResultSetWrapper wrapper, ResultMap resultMap, List<Class<?>> constructorArgTypes,
			List<Object> constructorArgs, String columnPrefix) {
		Class<?> resultType = resultMap.getType();
		MetaClass metaClass = MetaClass.forClass(resultType);
		if (resultType.isInterface() || metaClass.hasDefaultConstructor()) {
			return objectFactory.create(resultType);
		}
		throw new RuntimeException("don't know how to create instance of " + resultType);
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
