package mybatis.executor.resultset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import mybatis.io.Resources;
import mybatis.mapping.ResultMap;
import mybatis.session.Configuration;
import mybatis.type.JdbcType;
import mybatis.type.TypeHandler;
import mybatis.type.TypeHandlerRegistry;

/**
 * @Description ResultSet包装器
 * @Author jiyang.li
 * @Date 2022/10/17 11:28
 **/
public class ResultSetWrapper {
	private final ResultSet resultSet;
	private final TypeHandlerRegistry typeHandlerRegistry;
	private final List<String> columnNames = new ArrayList<>();
	private final List<String> classNames = new ArrayList<>();
	private final List<JdbcType> jdbcTypes = new ArrayList<>();

	private final Map<String, Map<Class<?>, TypeHandler<?>>> typeHandlerMap = new HashMap<>();
	private Map<String, List<String>> mappedColumnNamesMap = new HashMap<>();
	private Map<String, List<String>> unMappedColumnNamesMap = new HashMap<>();

	public ResultSetWrapper(Configuration configuration, ResultSet resultSet) throws SQLException {
		super();
		this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
		this.resultSet = resultSet;

		final ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			columnNames.add(metaData.getColumnName(i));
			classNames.add(metaData.getColumnClassName(i));
			jdbcTypes.add(JdbcType.forCode(metaData.getColumnType(i)));
		}
	}

	public ResultSet getResultSet() {
		return resultSet;
	}

	public List<String> getColumnNames() {
		return columnNames;
	}

	public List<String> getClassNames() {
		return Collections.unmodifiableList(classNames);
	}

	public TypeHandler<?> getTypeHandler(Class<?> propertyType, String columnName) {
		TypeHandler<?> typeHandler = null;
		Map<Class<?>, TypeHandler<?>> classTypeHandlerMap = typeHandlerMap.get(columnName);
		if (classTypeHandlerMap == null) {
			classTypeHandlerMap = new HashMap<>();
			typeHandlerMap.put(columnName, classTypeHandlerMap);
		} else {
			typeHandler = classTypeHandlerMap.get(propertyType);
		}
		if (typeHandler == null) {
			typeHandler = typeHandlerRegistry.getTypeHandler(propertyType, null);
			classTypeHandlerMap.put(propertyType, typeHandler);
		}
		return typeHandler;
	}

	private Class<?> resolveClass(String className) {
		try {
			return Resources.classForName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	private String getMapKey(ResultMap resultMap, String columnPrefix) {
		return resultMap.getId() + ":" + columnPrefix;
	}

	public List<String> getMappedColumnNames(ResultMap resultMap, String columnPrefix) {
		List<String> mappedColumnNames = mappedColumnNamesMap.get(getMapKey(resultMap, columnPrefix));
		if (mappedColumnNames == null) {
			loadMappedAndUnmapped(resultMap, columnPrefix);
			mappedColumnNames = mappedColumnNamesMap.get(getMapKey(resultMap, columnPrefix));
		}
		return mappedColumnNames;
	}

    public List<String> getUnmappedColumnNames(ResultMap resultMap, String columnPrefix) throws SQLException {
        List<String> unMappedColumnNames = unMappedColumnNamesMap.get(getMapKey(resultMap, columnPrefix));
        if (unMappedColumnNames == null) {
            loadMappedAndUnmapped(resultMap, columnPrefix);
            unMappedColumnNames = unMappedColumnNamesMap.get(getMapKey(resultMap, columnPrefix));
        }
        return unMappedColumnNames;
    }

	private void loadMappedAndUnmapped(ResultMap resultMap, String columnPrefix) {
		List<String> mappedColumnNames = new ArrayList<>();
		List<String> unmappedColumnNames = new ArrayList<>();
		final String upperColumnPrefix = columnPrefix == null ? null : columnPrefix.toUpperCase(Locale.ENGLISH);
		final Set<String> mappedColumns = prependPrefixes(resultMap.getMappedColumns(), upperColumnPrefix);
		for (String col : columnNames) {
            if (mappedColumns.contains(col)) {
                mappedColumnNames.add(col);
            } else {
                unmappedColumnNames.add(col);
            }
		}
		mappedColumnNamesMap.put(getMapKey(resultMap, columnPrefix), mappedColumnNames);
		unMappedColumnNamesMap.put(getMapKey(resultMap, columnPrefix), unmappedColumnNames);
	}

	private Set<String> prependPrefixes(Set<String> columnNames, String prefix) {
		if (columnNames == null || columnNames.isEmpty() || prefix == null || prefix.length() == 0) {
			return columnNames;
		}
		final Set<String> prefixed = new HashSet<String>();
		for (String columnName : columnNames) {
			prefixed.add(prefix + columnName);
		}
		return prefixed;
	}
}
