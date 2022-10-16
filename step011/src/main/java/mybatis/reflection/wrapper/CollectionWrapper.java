package mybatis.reflection.wrapper;

import java.util.Collection;
import java.util.List;

import mybatis.reflection.MetaObject;
import mybatis.reflection.factory.ObjectFactory;
import mybatis.reflection.property.PropertyTokenizer;

/**
 * @Description collection包装器
 * @Author jiyang.li
 * @Date 2022/9/27 14:48
 **/
public class CollectionWrapper implements ObjectWrapper {
    // 被包装的对象
    private Collection<Object> collection;

    public CollectionWrapper(MetaObject metaObject, Collection<Object> collection) {
        this.collection = collection;
    }

    // get set都是不允许的；只能添加元素
    @Override
	public Object get(PropertyTokenizer propertyTokenizer) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(PropertyTokenizer tokenizer, Object value) {
        throw new UnsupportedOperationException();
	}

	@Override
	public String findProperty(String name, boolean useCamelCaseMapping) {
        throw new UnsupportedOperationException();
	}

	@Override
	public String[] getGetterNames() {
        throw new UnsupportedOperationException();
	}

	@Override
	public String[] getSetterNames() {
        throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> getSetterType(String name) {
        throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> getGetterType(String name) {
        throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasSetter(String name) {
        throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasGetter(String name) {
        throw new UnsupportedOperationException();
	}

	@Override
	public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCollection() {
		return true;
	}

	@Override
	public void add(Object element) {
        collection.add(element);
	}

	@Override
	public <E> void addAll(List<E> element) {
        collection.addAll(element);
	}
}
