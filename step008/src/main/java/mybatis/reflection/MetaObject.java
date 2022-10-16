package mybatis.reflection;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import mybatis.reflection.factory.ObjectFactory;
import mybatis.reflection.property.PropertyTokenizer;
import mybatis.reflection.wrapper.*;

/**
 * @Description 元对象
 * @Author jiyang.li
 * @Date 2022/9/25 22:06
 **/
public class MetaObject {
	private Object originalObject;

	private ObjectWrapper objectWrapper;

	private ObjectFactory objectFactory;

	private ObjectWrapperFactory wrapperFactory;

	public MetaObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory wrapperFactory) {
		this.originalObject = object;
		this.objectFactory = objectFactory;
		this.wrapperFactory = wrapperFactory;

		if (object instanceof ObjectWrapper) {
			// 如果对象本身已经是Wrapper了，直接赋给objectWrapper
			this.objectWrapper = (ObjectWrapper) object;
		} else if(wrapperFactory.hasWrapperFor(object)){
			this.objectWrapper = wrapperFactory.getWrapperFor(this, object);
		} else if (object instanceof Map) {
			this.objectWrapper = new MapWrapper(this, (Map) object);
		} else if (object instanceof Collection) {
			this.objectWrapper = new CollectionWrapper(this, (Collection<Object>) object);
		} else {
			this.objectWrapper = new BeanWrapper(this, object);
		}
	}

	public static MetaObject forObject(Object object, ObjectFactory defaultObjectFactory,
			ObjectWrapperFactory defaultObjectWrapperFactory) {
		if (object == null) {
			return SystemMetaObject.NULL_META_OBJECT;
		} else {
			return new MetaObject(object, defaultObjectFactory, defaultObjectWrapperFactory);
		}
	}

	public Object getOriginalObject() {
		return originalObject;
	}

	public ObjectFactory getObjectFactory() {
		return objectFactory;
	}

	public ObjectWrapperFactory getWrapperFactory() {
		return wrapperFactory;
	}

	/** 以下方法都是委派给ObjectWrapper来 */
	// 查找属性
	public String findProperty(String propName, boolean useCamelCaseMapping) {
		return objectWrapper.findProperty(propName, useCamelCaseMapping);
	}

	// 取得getter的名字列表
	public String[] getGetterNames() {
		return objectWrapper.getGetterNames();
	}

	// 取得setter的名字列表
	public String[] getSetterNames() {
		return objectWrapper.getSetterNames();
	}

	// 取得setter的类型列表
	public Class<?> getSetterType(String name) {
		return objectWrapper.getSetterType(name);
	}

	// 取得getter的类型列表
	public Class<?> getGetterType(String name) {
		return objectWrapper.getGetterType(name);
	}

	// 是否有指定的setter
	public boolean hasSetter(String name) {
		return objectWrapper.hasSetter(name);
	}

	// 是否有指定的getter
	public boolean hasGetter(String name) {
		return objectWrapper.hasGetter(name);
	}

	public Object getValue(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaObject metaValue = metaObjectForProperty(prop.getIndexName());
			if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
				// 如果上层就是null了，那就结束，返回null
				return null;
			} else {
				// 否则继续看下一层，递归调用getValue
				return metaValue.getValue(prop.getChildren());
			}
		} else {
			return objectWrapper.get(prop);
		}
	}

	public void setValue(String name, Object value) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaObject metaValue = metaObjectForProperty(prop.getIndexName());
			if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
				if (value == null && prop.getChildren() != null) {
					return;
				} else {
					metaValue = objectWrapper.instantiatePropertyValue(name, prop, objectFactory);
				}
			}
			metaValue.setValue(prop.getChildren(), value);
		} else {
			// 到最后一层了
			objectWrapper.set(prop, value);
		}
	}

	public MetaObject metaObjectForProperty(String name) {
		Object value = getValue(name);
		return MetaObject.forObject(value, objectFactory, wrapperFactory);
	}

	public ObjectWrapper getObjectWrapper() {
		return objectWrapper;
	}

	// 是否是集合
	public boolean isCollection() {
		return objectWrapper.isCollection();
	}

	// 添加属性
	public void add(Object element) {
		objectWrapper.add(element);
	}

	// 添加属性
	public <E> void addAll(List<E> list) {
		objectWrapper.addAll(list);
	}

}
