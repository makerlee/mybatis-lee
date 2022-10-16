package mybatis.scripting;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 脚本语言注册器
 * @Author jiyang.li
 * @Date 2022/10/10 11:30
 **/
public class LanguageDriverRegistry {
	private final Map<Class<?>, LanguageDriver> DRIVER_MAP = new HashMap<>();
	private Class<?> defaultDriverClass;

	public void register(Class<?> diverClass) {
		if (diverClass == null) {
			throw new IllegalArgumentException("language driver class cannot be null");
		}
		if (!LanguageDriver.class.isAssignableFrom(diverClass)) {
			throw new RuntimeException(diverClass.getName() + "does not implement " + LanguageDriver.class.getName());
		}
		// 如果没注册过，再去注册
		LanguageDriver driver = DRIVER_MAP.get(diverClass);
		if (driver == null) {
			try {
				// 单例模式，即一个Class只有一个对应的LanguageDriver
				driver = (LanguageDriver) diverClass.newInstance();
				DRIVER_MAP.put(diverClass, driver);
			} catch (Exception ex) {
				throw new RuntimeException("Failed to load language driver for " + diverClass.getName(), ex);
			}
		}
	}

	public Class<?> getDefaultDriverClass() {

		return defaultDriverClass;
	}

	public LanguageDriver getDriver(Class<?> langClass) {
		return DRIVER_MAP.get(langClass);
	}

	public LanguageDriver getDefaultDriver() {
		return getDriver(getDefaultDriverClass());
	}

	// Configuration()有调用，默认的为XMLLanguageDriver
	public void setDefaultDriverClass(Class<?> defaultDriverClass) {
		register(defaultDriverClass);
		this.defaultDriverClass = defaultDriverClass;
	}
}
