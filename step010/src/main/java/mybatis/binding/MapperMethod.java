package mybatis.binding;

import java.lang.reflect.Method;
import java.util.*;

import mybatis.mapping.MappedStatement;
import mybatis.mapping.SqlCommandType;
import mybatis.session.Configuration;
import mybatis.session.SqlSession;

/**
 * @Description 对应Mapper接口里的一个方法
 * @Author jiyang.li
 * @Date 2022/9/19 11:02
 **/
public class MapperMethod {
	private final SqlCommand sqlCommand;
	private final MethodSignature method;

	public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration) {
		this.sqlCommand = new SqlCommand(configuration, mapperInterface, method);
		this.method = new MethodSignature(configuration, method);
	}

	public Object execute(SqlSession sqlSession, Object[] args) {
		Object result = null;
		switch (sqlCommand.getType()) {
			case DELETE :
				break;
			case INSERT :
				break;
			case UPDATE :
				break;
			case SELECT :
				Object param = method.convertArgsToSqlCommandParam(args);
				result = sqlSession.selectOne(sqlCommand.getName(), param);
				break;
			default :
				throw new RuntimeException("unknown execution method for " + sqlCommand.getName());
		}
		return result;
	}

	public static class SqlCommand {
		private final String name;
		private final SqlCommandType type;

		public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
			String statementName = mapperInterface.getName() + "." + method.getName();
			MappedStatement statement = configuration.getStatement(statementName);
			name = statement.getId();
			type = statement.getSqlCommandType();
		}

		public String getName() {
			return name;
		}

		public SqlCommandType getType() {
			return type;
		}
	}

	public static class MethodSignature {
		private SortedMap<Integer, String> params;

		public MethodSignature(Configuration configuration, Method method) {
			this.params = Collections.unmodifiableSortedMap(getParams(method));
		}

		public Object convertArgsToSqlCommandParam(Object[] args) {
			final int paramCount = params.size();
			if (args == null || paramCount == 0) {
				// 没有参数
				return null;
			} else if (paramCount == 1) {
				return args[params.keySet().iterator().next().intValue()];
			} else {
				// 否则返回一个ParamMap,
				final Map<String, Object> paramMap = new ParamMap<>();
				int i = 0;
				for (Map.Entry<Integer, String> entry : params.entrySet()) {
					paramMap.put(entry.getValue(), args[entry.getKey().intValue()]);

					final String genericName = "param" + (i + 1);
					if (!paramMap.containsKey(genericName)) {
						paramMap.put(genericName, args[entry.getKey()]);
					}
					i++;
				}
				return paramMap;
			}
		}

		private SortedMap<Integer, String> getParams(Method method) {
			// 用一个TreeMap，这样就保证还是按参数的先后顺序
			final SortedMap<Integer, String> params = new TreeMap<Integer, String>();
			final Class<?>[] argTypes = method.getParameterTypes();
			for (int i = 0; i < argTypes.length; i++) {
				String paramName = String.valueOf(params.size());
				params.put(i, paramName);
			}
			return params;
		}
	}

	/**
	 * 参数map，静态内部类,更严格的get方法，如果没有相应的key，报错
	 */
	public static class ParamMap<V> extends HashMap<String, V> {
		@Override
		public V get(Object key) {
			if (!super.containsKey(key)) {
				throw new RuntimeException("Parameter '" + key + "' not found. Available parameters are " + keySet());
			}
			return super.get(key);
		}
	}
}
