package mybatis.reflection;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import mybatis.reflection.Invoker.GetFieldInvoker;
import mybatis.reflection.Invoker.Invoker;
import mybatis.reflection.Invoker.MethodInvoker;
import mybatis.reflection.Invoker.SetFiledInvoker;
import mybatis.reflection.property.PropertyNamer;

/**
 * @Description 一个对象对应一个reflector
 * @Author jiyang.li
 * @Date 2022/9/27 17:29
 **/
public class Reflector {
	private static boolean classCacheEnable = true;

	private static final String[] EMPTY_STR_ARRAY = new String[0];
	// 线程安全的缓存
	private static final Map<Class<?>, Reflector> REFLECTOR_MAP = new ConcurrentHashMap<>();

	private Class<?> type;
	// get属性
	private String[] readablePropertyNames = EMPTY_STR_ARRAY;
	// set属性
	private String[] writeablePropertyNames = EMPTY_STR_ARRAY;
	// get方法列表
	private Map<String, Invoker> getMethods = new HashMap<>();
	// set方法列表
	private Map<String, Invoker> setMethods = new HashMap<>();
	// get类型列表
	private Map<String, Class<?>> getTypes = new HashMap<>();
	// set类型列表
	private Map<String, Class<?>> setTypes = new HashMap<>();
	// 构造函数
	private Constructor<?> defaultConstructor;

	private Map<String, String> caseInsensitivePropertyMap = new HashMap<>();

	public Reflector(Class<?> clazz) {
		this.type = clazz;
		addDefaultConstructor(clazz);
		addGetMethods(clazz);
		addSetMethods(clazz);

		addFields(clazz);

		readablePropertyNames = getMethods.keySet().toArray(new String[getMethods.keySet().size()]);
		writeablePropertyNames = setMethods.keySet().toArray(new String[setMethods.keySet().size()]);
		for (String propName : readablePropertyNames) {
			caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
		}
		for (String propName : writeablePropertyNames) {
			caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
		}
	}

	public static Reflector forClass(Class<?> clazz) {
		if (classCacheEnable) {
			Reflector cached = REFLECTOR_MAP.get(clazz);
			if (cached == null) {
				cached = new Reflector(clazz);
				REFLECTOR_MAP.put(clazz, cached);
			}
			return cached;
		} else {
			return new Reflector(clazz);
		}
	}

	public static boolean isClassCacheEnabled() {
		return classCacheEnable;
	}

	public static void setClassEnabled(boolean enable) {
		Reflector.classCacheEnable = enable;
	}

	private void addFields(Class<?> clazz) {
		Field[] declaredFields = clazz.getDeclaredFields();
		for (Field field : declaredFields) {
			if (canAccessPrivateMethods()) {
				try {
					field.setAccessible(true);
				} catch (Exception ignore) {
					// ignore
				}
			}
			if (field.isAccessible()) {
				if (!setMethods.containsKey(field.getName())) {
					int modifiers = field.getModifiers();
					if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers)) {
						addSetField(field);
					}
				}

				if (!getMethods.containsKey(field.getName())) {
					addGetField(field);
				}
			}
		}
		// 递归父类
		if (clazz.getSuperclass() != null) {
			addFields(clazz.getSuperclass());
		}
	}

	private void addGetField(Field field) {
		if (isValidPropertyName(field.getName())) {
			getMethods.put(field.getName(), new GetFieldInvoker(field));
			getTypes.put(field.getName(), field.getType());
		}
	}

	private void addSetField(Field field) {
		if (isValidPropertyName(field.getName())) {
			setMethods.put(field.getName(), new SetFiledInvoker(field));
			setTypes.put(field.getName(), field.getType());
		}
	}

	private void addSetMethods(Class<?> clazz) {
		Map<String, List<Method>> conflictSetters = new HashMap<>();
		Method[] methods = getClassMethod(clazz);
		for (Method method : methods) {
			String name = method.getName();
			if (name.startsWith("set") && name.length() > 3 && method.getParameterTypes().length == 1) {
				name = PropertyNamer.methodToProperty(name);
				addMethodConflict(conflictSetters, name, method);
			}
		}
		resolveSetterConflict(conflictSetters);
	}

	private void resolveSetterConflict(Map<String, List<Method>> conflictSetters) {
		for (String propName : conflictSetters.keySet()) {
			List<Method> setters = conflictSetters.get(propName);
			Method firstMethod = setters.get(0);
			if (setters.size() == 1) {
				addSetMethod(propName, firstMethod);
			} else {
				Class<?> exceptedType = getTypes.get(propName);
				if (exceptedType == null) {
					throw new RuntimeException("Illegal overloaded setter method with ambiguous type for property "
							+ propName + " in class " + firstMethod.getDeclaringClass()
							+ ".  This breaks the JavaBeans " + "specification and can cause unpredicatble results.");
				} else {
					Iterator<Method> setterIte = setters.iterator();
					Method setter = null;
					while (setterIte.hasNext()) {
						Method tmp = setterIte.next();
						if (tmp.getParameterTypes().length == 1 && exceptedType.equals(tmp.getParameterTypes()[0])) {
							setter = tmp;
							break;
						}
					}
					if (setter == null) {
						throw new RuntimeException("Illegal overloaded setter method with ambiguous type for property "
								+ propName + " in class " + firstMethod.getDeclaringClass()
								+ ".  This breaks the JavaBeans "
								+ "specification and can cause unpredicatble results.");
					}
					addSetMethod(propName, setter);
				}
			}
		}
	}

	private void addSetMethod(String propName, Method method) {
		if (isValidPropertyName(propName)) {
			setMethods.put(propName, new MethodInvoker(method));
			setTypes.put(propName, method.getParameterTypes()[0]);
		}
	}

	private void addGetMethods(Class<?> clazz) {
		Map<String, List<Method>> conflictMethod = new HashMap<>();
		Method[] methods = getClassMethod(clazz);
		for (Method method : methods) {
			String name = method.getName();
			if (name.startsWith("get") && name.length() > 3) {
				if (method.getParameterTypes().length == 0) {
					name = PropertyNamer.methodToProperty(name);
					addMethodConflict(conflictMethod, name, method);
				}
			}
			if (name.startsWith("is") && name.length() > 2) {
				if (method.getParameterTypes().length == 0) {
					name = PropertyNamer.methodToProperty(name);
					addMethodConflict(conflictMethod, name, method);
				}
			}
		}
		resolveGetterConflict(conflictMethod);
	}

	// 需要研究
	private void resolveGetterConflict(Map<String, List<Method>> conflictingGetters) {
		for (String propName : conflictingGetters.keySet()) {
			List<Method> getters = conflictingGetters.get(propName);
			Iterator<Method> iterator = getters.iterator();
			Method firstMethod = iterator.next();
			if (getters.size() == 1) {
				addGetMethod(propName, firstMethod);
			} else {
				Method getter = firstMethod;
				Class<?> getterType = firstMethod.getReturnType();
				while (iterator.hasNext()) {
					Method method = iterator.next();
					Class<?> methodType = method.getReturnType();
					if (methodType.equals(getterType)) {
						throw new RuntimeException("Illegal overloaded getter method with ambiguous type for property "
								+ propName + " in class " + firstMethod.getDeclaringClass()
								+ ".  This breaks the JavaBeans "
								+ "specification and can cause unpredicatble results.");
					} else if (methodType.isAssignableFrom(getterType)) {
						// OK getter type is descendant
					} else if (getterType.isAssignableFrom(methodType)) {
						getter = method;
						getterType = methodType;
					} else {
						throw new RuntimeException("Illegal overloaded getter method with ambiguous type for property "
								+ propName + " in class " + firstMethod.getDeclaringClass()
								+ ".  This breaks the JavaBeans "
								+ "specification and can cause unpredicatble results.");
					}
				}
				addGetMethod(propName, getter);
			}
		}
	}

	private void addGetMethod(String propName, Method firstMethod) {
		if (isValidPropertyName(propName)) {
			getMethods.put(propName, new MethodInvoker(firstMethod));
			getTypes.put(propName, firstMethod.getReturnType());
		}
	}

	private boolean isValidPropertyName(String propName) {
		return !propName.startsWith("$") && !"serialVersionUID".equals(propName) && !"class".equals(propName);
	}

	private void addMethodConflict(Map<String, List<Method>> conflictMethod, String name, Method method) {
		List<Method> methods = conflictMethod.computeIfAbsent(name, k -> new ArrayList<>());
		methods.add(method);
	}

	private Method[] getClassMethod(Class<?> clazz) {
		Map<String, Method> uniqueMethod = new HashMap<>();
		Class<?> currentClazz = clazz;
		while (currentClazz != null) {
			// 添加方法到map集合；递归父类
			addUniqueMethod(uniqueMethod, currentClazz.getDeclaredMethods());
			// we also need to look for interface method,because this class may be abstract
			for (Class<?> anInterface : clazz.getInterfaces()) {
				addUniqueMethod(uniqueMethod, anInterface.getMethods());
			}

			currentClazz = currentClazz.getSuperclass();
		}
		Collection<Method> methods = uniqueMethod.values();
		return methods.toArray(new Method[methods.size()]);
	}

	private void addUniqueMethod(Map<String, Method> uniqueMethod, Method[] declaredMethods) {
		for (Method method : declaredMethods) {
			if (method.isBridge()) {
				continue;
			}
			// 取得方法签名
			String signature = getSignature(method);
			if (!uniqueMethod.containsKey(signature)) {
				if (canAccessPrivateMethods()) {
					try {
						method.setAccessible(true);
					} catch (Exception e) {
						// ignore
					}
				}
				uniqueMethod.put(signature, method);
			}
		}
	}

	// void#addGetMethods:java.lang.Class,java.lang.String
	private String getSignature(Method method) {
		StringBuilder sb = new StringBuilder();
		Class<?> returnType = method.getReturnType();
		if (returnType != null) {
			sb.append(returnType.getName()).append('#');
		}
		sb.append(method.getName());
		Class<?>[] parameters = method.getParameterTypes();
		for (int i = 0; i < parameters.length; i++) {
			if (i == 0) {
				sb.append(':');
			} else {
				sb.append(',');
			}
			sb.append(parameters[i].getName());
		}
		return sb.toString();
	}

	private void addDefaultConstructor(Class<?> clazz) {
		Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
		for (Constructor<?> constructor : declaredConstructors) {
			if (constructor.getParameterCount() == 0) {
				if (canAccessPrivateMethods()) {
					try {
						constructor.setAccessible(true);
					} catch (Exception e) {
						// ignore.
					}
				}
			}
			if (constructor.isAccessible()) {
				this.defaultConstructor = constructor;
			}
		}
	}

	private static boolean canAccessPrivateMethods() {
		try {
			SecurityManager securityManager = System.getSecurityManager();
			if (null != securityManager) {
				securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
			}
		} catch (SecurityException e) {
			return false;
		}
		return true;
	}

	public Class<?> getGetterType(String propertyName) {
		Class<?> clazz = getTypes.get(propertyName);
		if (clazz == null) {
			throw new RuntimeException(
					"There is no getter for property named '" + propertyName + "' in '" + type + "'");
		}
		return clazz;
	}

	public String findPropertyName(String name) {
		return caseInsensitivePropertyMap.get(name.toUpperCase(Locale.ENGLISH));
	}

	public String[] getGetablePropertyNames() {
		return readablePropertyNames;
	}

	public String[] getSetablePropertyNames() {
		return writeablePropertyNames;
	}

	public Class<?> getSetterType(String propName) {
		Class<?> clazz = setTypes.get(propName);
		if (clazz == null) {
			throw new RuntimeException("There is no setter for property named '" + propName + "' in '" + type + "'");
		}
		return clazz;
	}

	public Invoker getGetInvoker(String propertyName) {
		Invoker method = getMethods.get(propertyName);
		if (method == null) {
			throw new RuntimeException(
					"There is no getter for property named '" + propertyName + "' in '" + type + "'");
		}
		return method;
	}

	public Invoker getSetInvoker(String propertyName) {
		Invoker method = setMethods.get(propertyName);
		if (method == null) {
			throw new RuntimeException(
					"There is no setter for property named '" + propertyName + "' in '" + type + "'");
		}
		return method;
	}

	public boolean hasSetter(String name) {
		return setMethods.containsKey(name);
	}

	public boolean hasGetter(String name) {
		return getMethods.containsKey(name);
	}

	public boolean hasDefaultConstructor() {
		return defaultConstructor != null;
	}
}
