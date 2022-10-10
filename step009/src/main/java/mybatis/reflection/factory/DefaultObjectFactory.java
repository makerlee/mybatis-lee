package mybatis.reflection.factory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @Description 默认的对象工厂，所有对象都由工厂创建
 * @Author jiyang.li
 * @Date 2022/9/26 11:00
 **/
public class DefaultObjectFactory implements ObjectFactory, Serializable {

	@Override
	public void setProperties(Properties properties) {
		// 默认无属性设置
	}

	@Override
	public <T> T create(Class<T> type) {
		return create(type, null, null);
	}

	@Override
	public <T> T create(Class<T> type, List<Class<?>> constructorArgsType, List<Object> constructorArgs) {
		Class<?> classToCreate = resolveInterface(type);
		return (T) instantiateClass(type, constructorArgsType, constructorArgs);
	}

	private <T> Object instantiateClass(Class<T> type, List<Class<?>> constructorArgsType,
			List<Object> constructorArgs) {
		try {
			Constructor<T> constructor;
			if (constructorArgs == null || constructorArgsType == null) {
				constructor = type.getDeclaredConstructor();
				if (!constructor.isAccessible()) {
					constructor.setAccessible(true);
				}
				return constructor.newInstance();
			}
			// 带参数的
			constructor = type
					.getDeclaredConstructor(constructorArgsType.toArray(new Class[constructorArgsType.size()]));
			if (!constructor.isAccessible()) {
				constructor.setAccessible(true);
			}
			return constructor.newInstance(constructorArgs.toArray(new Object[constructorArgs.size()]));
		} catch (Exception e) {
			// 如果出错，包装一下，重新抛出自己的异常
			StringBuilder argTypes = new StringBuilder();
			if (constructorArgsType != null) {
				for (Class<?> argType : constructorArgsType) {
					argTypes.append(argType.getSimpleName());
					argTypes.append(",");
				}
			}
			StringBuilder argValues = new StringBuilder();
			if (constructorArgs != null) {
				for (Object argValue : constructorArgs) {
					argValues.append(argValue);
					argValues.append(",");
				}
			}
			throw new RuntimeException("Error instantiating " + type + " with invalid types (" + argTypes
					+ ") or values (" + argValues + "). Cause: " + e, e);
		}
	}

	// 处理集合类型
	private Class<?> resolveInterface(Class<?> type) {
		Class<?> classToCreate;
		if (type == List.class || type == Collection.class || type == Iterable.class) {
			classToCreate = ArrayList.class;
		} else if (type == Map.class) {
			classToCreate = HashMap.class;
		} else if (type == SortedSet.class) {
			classToCreate = TreeSet.class;
		} else if (type == Set.class) {
			classToCreate = HashSet.class;
		} else {
			classToCreate = type;
		}
		return classToCreate;
	}

	@Override
	public <T> boolean isCollection(Class<T> type) {
		return Collection.class.isAssignableFrom(type);
	}
}
