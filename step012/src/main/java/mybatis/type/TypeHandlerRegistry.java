package mybatis.type;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description 类型处理器注册表
 * @Author jiyang.li
 * @Date 2022/10/11 15:04
 **/
public final class TypeHandlerRegistry {
	private final Map<JdbcType, TypeHandler<?>> JDBC_TYPE_HANDLER_MAP = new EnumMap<>(JdbcType.class);
	private final Map<Type, Map<JdbcType, TypeHandler<?>>> TYPE_HANDLER_MAP = new HashMap();
	private final Map<Class<?>, TypeHandler<?>> ALL_TYPE_HANDLERS_MAP = new HashMap<>();

	public TypeHandlerRegistry() {
		register(Long.class, new LongTypeHandler());
		register(long.class, new LongTypeHandler());

		register(Integer.class, new IntTypeHandler());
		register(int.class, new IntTypeHandler());

		register(String.class, new StringTypeHandler());
		register(String.class, JdbcType.VARCHAR, new StringTypeHandler());
		register(String.class, JdbcType.CHAR, new StringTypeHandler());
	}

	private <T> void register(Type javaType, TypeHandler<? extends T> typeHandler) {
		register(javaType, null, typeHandler);
	}

	private void register(Type javaType, JdbcType jdbcType, TypeHandler<?> handler) {
		if (null != javaType) {
			Map<JdbcType, TypeHandler<?>> map = TYPE_HANDLER_MAP.computeIfAbsent(javaType, k -> new HashMap<>());
			map.put(jdbcType, handler);
		}
		ALL_TYPE_HANDLERS_MAP.put(handler.getClass(), handler);
	}

	public <T> TypeHandler<T> getTypeHandler(Class<T> type, JdbcType jdbcType) {
		return getTypeHandler((Type) type, jdbcType);
	}

	public boolean hasTypeHandler(Class<?> javaType) {
		return hasTypeHandler(javaType, null);
	}

	public boolean hasTypeHandler(Class<?> javaType, JdbcType jdbcType) {
		return javaType != null && getTypeHandler(javaType, jdbcType) != null;
	}

	private <T> TypeHandler<T> getTypeHandler(Type type, JdbcType jdbcType) {
		Map<JdbcType, TypeHandler<?>> jdbcTypeTypeHandlerMap = TYPE_HANDLER_MAP.get(type);
		TypeHandler<?> typeHandler = null;
		if (jdbcTypeTypeHandlerMap != null) {
			typeHandler = jdbcTypeTypeHandlerMap.get(jdbcType);
			if (typeHandler == null) {
				typeHandler = jdbcTypeTypeHandlerMap.get(null);
			}
		}
		return (TypeHandler<T>) typeHandler;
	}
}
