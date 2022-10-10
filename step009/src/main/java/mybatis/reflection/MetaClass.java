package mybatis.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import mybatis.reflection.Invoker.GetFieldInvoker;
import mybatis.reflection.Invoker.Invoker;
import mybatis.reflection.Invoker.MethodInvoker;
import mybatis.reflection.property.PropertyTokenizer;

/**
 * @Description 元类
 * @Author jiyang.li
 * @Date 2022/9/27 14:55
 **/
public class MetaClass {
	private Reflector reflector;

	private MetaClass(Class<?> type) {
		this.reflector = Reflector.forClass(type);
	}

	public static MetaClass forClass(Class<?> clazz) {
		return new MetaClass(clazz);
	}

	public static boolean isClassCacheEnabled() {
		return Reflector.isClassCacheEnabled();
	}

	public static void setClassCacheEnable(boolean enable) {
		Reflector.setClassEnabled(enable);
	}

	public MetaClass metaClassForProperty(String name) {
		Class<?> propType = reflector.getGetterType(name);
		return MetaClass.forClass(propType);
	}

	public String findProperty(String name) {
		StringBuilder prop = buildProperty(name, new StringBuilder());
		return prop.length() > 0 ? prop.toString() : null;
	}

	// 驼峰 下划线
	public String findProperty(String name, boolean useCamelCaseMapping) {
		if (useCamelCaseMapping) {
			name = name.replace("_", "");
		}
		return findProperty(name);
	}

	public String[] getGetterNames() {
		return reflector.getGetablePropertyNames();
	}

	public String[] getSetterNames() {
		return reflector.getSetablePropertyNames();
	}

	public Class<?> getSetterType(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaClass propMetaClass = metaClassForProperty(prop.getName());
			return propMetaClass.getSetterType(prop.getChildren());
		}
		return reflector.getSetterType(prop.getName());
	}

	public Class<?> getGetterType(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaClass propMetaClass = metaClassForProperty(prop);
			return propMetaClass.getGetterType(prop.getChildren());
		}
		return getGetterType(prop);
	}

	private MetaClass metaClassForProperty(PropertyTokenizer prop) {
		Class<?> propType = getGetterType(prop);
		return MetaClass.forClass(propType);
	}

	private Class<?> getGetterType(PropertyTokenizer prop) {
		Class<?> getterType = reflector.getGetterType(prop.getName());
		if (prop.getIndex() != null && Collection.class.isAssignableFrom(getterType)) {
			Type returnType = getGenericGetterType(prop.getName());
			if (returnType instanceof ParameterizedType) {
				Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
				if (actualTypeArguments != null && actualTypeArguments.length == 1) {
					returnType = actualTypeArguments[0];
					if (returnType instanceof Class) {
						getterType = (Class<?>) returnType;
					} else if (returnType instanceof ParameterizedType) {
						getterType = (Class<?>) ((ParameterizedType) returnType).getRawType();
					}
				}
			}
		}
		return getterType;
	}

	private Type getGenericGetterType(String propertyName) {
		try {
			Invoker invoker = reflector.getGetInvoker(propertyName);
			if (invoker instanceof MethodInvoker) {
				Field _method = MethodInvoker.class.getDeclaredField("method");
				_method.setAccessible(true);
				Method method = (Method) _method.get(invoker);
				return method.getGenericReturnType();
			} else if (invoker instanceof GetFieldInvoker) {
				Field _field = GetFieldInvoker.class.getDeclaredField("field");
				_field.setAccessible(true);
				Field field = (Field) _field.get(invoker);
				return field.getGenericType();
			}
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	public boolean hasSetter(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			if (reflector.hasSetter(prop.getName())) {
				MetaClass metaClass = metaClassForProperty(prop.getName());
				return metaClass.hasSetter(prop.getChildren());
			} else {
				return false;
			}
		} else {
			return reflector.hasSetter(prop.getName());
		}
	}

	public boolean hasGetter(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			if (reflector.hasGetter(prop.getName())) {
				MetaClass metaClass = metaClassForProperty(prop);
				return metaClass.hasGetter(prop.getChildren());
			} else {
				return false;
			}
		} else {
			return reflector.hasGetter(prop.getName());
		}
	}

	public Invoker getGetInvoker(String name) {
		return reflector.getGetInvoker(name);
	}

	public Invoker getSetInvoker(String name) {
		return reflector.getSetInvoker(name);
	}

	private StringBuilder buildProperty(String name, StringBuilder builder) {
		PropertyTokenizer tokenizer = new PropertyTokenizer(name);
		if (tokenizer.hasNext()) {
			String propName = reflector.findPropertyName(tokenizer.getName());
			if (propName != null) {
				builder.append(propName);
				builder.append(".");
				MetaClass metaClass = metaClassForProperty(propName);
				metaClass.buildProperty(tokenizer.getChildren(), builder);
			}
		} else {
			String propertyName = reflector.findPropertyName(name);
			if (propertyName != null) {
				builder.append(propertyName);
			}
		}
		return builder;
	}

	public boolean hasDefaultConstructor() {
		return reflector.hasDefaultConstructor();
	}
}
