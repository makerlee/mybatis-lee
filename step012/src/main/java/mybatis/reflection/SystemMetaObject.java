package mybatis.reflection;

import mybatis.reflection.factory.DefaultObjectFactory;
import mybatis.reflection.factory.ObjectFactory;
import mybatis.reflection.wrapper.DefaultObjectWrapperFactory;
import mybatis.reflection.wrapper.ObjectWrapperFactory;

/**
 * @Description 系统级的元对象
 * @Author jiyang.li
 * @Date 2022/9/26 14:24
 **/
public class SystemMetaObject {
	public static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
	public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
	public static final MetaObject NULL_META_OBJECT = MetaObject.forObject(NullObject.class, DEFAULT_OBJECT_FACTORY,
			DEFAULT_OBJECT_WRAPPER_FACTORY);

	private SystemMetaObject() {
		// Prevent Instantiation of Static Class
	}

	/**
	 * 空对象
	 */
	private static class NullObject {
	}

	public static MetaObject forObject(Object object) {
		return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);
	}
}
