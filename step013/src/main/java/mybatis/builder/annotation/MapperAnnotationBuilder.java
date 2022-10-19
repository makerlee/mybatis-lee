package mybatis.builder.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;

import mybatis.annotations.Delete;
import mybatis.annotations.Insert;
import mybatis.annotations.Select;
import mybatis.annotations.Update;
import mybatis.binding.MapperMethod;
import mybatis.builder.MapperBuilderAssistant;
import mybatis.mapping.SqlCommandType;
import mybatis.mapping.SqlSource;
import mybatis.scripting.LanguageDriver;
import mybatis.session.Configuration;
import mybatis.session.ResultHandler;
import mybatis.session.RowBounds;

/**
 * @Description 注解配置构建器
 * @Author jiyang.li
 * @Date 2022/10/19 09:06
 **/
public class MapperAnnotationBuilder {
	private final Set<Class<? extends Annotation>> sqlAnnotationTypes = new HashSet<>();

	private Configuration configuration;
	private MapperBuilderAssistant assistant;
	private Class<?> type;

	public MapperAnnotationBuilder(Configuration configuration, Class<?> type) {
		String resource = type.getName().replace(".", "/" + ".java(best guess)");
		this.assistant = new MapperBuilderAssistant(configuration, resource);

		this.configuration = configuration;
		this.type = type;
		sqlAnnotationTypes.add(Insert.class);
		sqlAnnotationTypes.add(Delete.class);
		sqlAnnotationTypes.add(Select.class);
		sqlAnnotationTypes.add(Update.class);
	}

	public void parse() {
		String resource = type.toString();
		if (!configuration.isResourceLoaded(resource)) {
			assistant.setCurrentNamespace(type.getName());
			Method[] methods = type.getMethods();
			for (Method method : methods) {
				if (!method.isBridge()) {
					// 解析语句
					parseStatement(method);
				}
			}
		}
	}

	private void parseStatement(Method method) {
		Class<?> parameterType = getParameterType(method);
		LanguageDriver languageDriver = getLanguageDriver(method);
		SqlSource sqlSource = getSqlSourceFromAnnotation(method, parameterType, languageDriver);

		if (sqlSource != null) {
			String msId = type.getName() + "." + method.getName();
			SqlCommandType sqlCommandType = getSqlCommandType(method);
			boolean isSelect = sqlCommandType == SqlCommandType.SELECT;

			String resultMapId = null;
			if (isSelect) {
				resultMapId = parseResultMap(method);
			}

			assistant.addMappedStatement(msId, sqlSource, sqlCommandType, parameterType, resultMapId,
					getReturnType(method), languageDriver);
		}
	}

	private String parseResultMap(Method method) {
		StringBuilder suffix = new StringBuilder();
		for (Class<?> clazz : method.getParameterTypes()) {
			suffix.append("-");
			suffix.append(clazz.getSimpleName());
		}
		if (suffix.length() < 1) {
			suffix.append("-void");
		}
		String rmId = type.getName() + "." + method.getName() + suffix;

		// 添加resultMap
		Class<?> returnType = getReturnType(method);
		assistant.addResultMap(rmId, returnType, new ArrayList<>());
		return rmId;
	}

	/**
	 * 重点：获取mapper方法的返回类型，若为List, 则需要获取集合中的对象类型
	 */
	private Class<?> getReturnType(Method method) {
		Class<?> returnType = method.getReturnType();
		if (Collection.class.isAssignableFrom(returnType)) {
			Type genericReturnType = method.getGenericReturnType();
			// 是否是泛型List<User>
			if (genericReturnType instanceof ParameterizedType) {
				Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
				if (actualTypeArguments != null && actualTypeArguments.length == 1) {
					genericReturnType = actualTypeArguments[0];
					if (genericReturnType instanceof Class) {
						returnType = (Class<?>) genericReturnType;
					} else if (genericReturnType instanceof ParameterizedType) {
						returnType = (Class<?>) ((ParameterizedType) genericReturnType).getRawType();
					} else if (genericReturnType instanceof GenericArrayType) {
						Class<?> componentType = (Class<?>) ((GenericArrayType) genericReturnType)
								.getGenericComponentType();
						// (issue #525) support List<byte[]>
						returnType = Array.newInstance(componentType, 0).getClass();
					}
				}
			}
		}
		return returnType;
	}

	private SqlCommandType getSqlCommandType(Method method) {
		Class<? extends Annotation> sqlAnnotationType = getSqlAnnotationType(method);
		if (sqlAnnotationType == null) {
			return SqlCommandType.UNKNOWN;
		}
		return SqlCommandType.valueOf(sqlAnnotationType.getSimpleName().toUpperCase(Locale.ENGLISH));
	}

	private SqlSource getSqlSourceFromAnnotation(Method method, Class<?> parameterType, LanguageDriver languageDriver) {
		try {
			Class<? extends Annotation> sqlAnnotationType = getSqlAnnotationType(method);
			if (sqlAnnotationType != null) {
				Annotation annotation = method.getAnnotation(sqlAnnotationType);
				final String[] strings = (String[]) annotation.getClass().getMethod("value").invoke(annotation);
				return buildSqlSourceFromStrings(strings, parameterType, languageDriver);
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException("Could not find value method on SQL annotation.  Cause: " + e);
		}
	}

	private SqlSource buildSqlSourceFromStrings(String[] strings, Class<?> parameterType,
			LanguageDriver languageDriver) {
		StringBuilder sql = new StringBuilder();
		for (String fragment : strings) {
			sql.append(fragment);
			sql.append(" ");
		}
		return languageDriver.createSqlSource(configuration, sql.toString(), parameterType);
	}

	private Class<? extends Annotation> getSqlAnnotationType(Method method) {
		for (Class<? extends Annotation> annotationType : sqlAnnotationTypes) {
			Annotation annotation = method.getAnnotation(annotationType);
			if (annotation != null) {
				return annotationType;
			}
		}
		return null;
	}

	private LanguageDriver getLanguageDriver(Method method) {
		Class<?> defaultDriverClass = configuration.getLanguageRegistry().getDefaultDriverClass();
		return configuration.getLanguageRegistry().getDriver(defaultDriverClass);
	}

	private Class<?> getParameterType(Method method) {
		Class<?> parameterType = null;
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (Class<?> clazz : parameterTypes) {
			if (RowBounds.class.isAssignableFrom(clazz) || ResultHandler.class.isAssignableFrom(clazz)) {
				continue;
			}
			if (parameterType == null) {
				parameterType = clazz;
			} else {
				parameterType = MapperMethod.ParamMap.class;
			}
		}
		return parameterType;
	}
}
