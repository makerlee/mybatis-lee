package mybatis.reflection.wrapper;

import java.util.List;

import mybatis.reflection.MetaClass;
import mybatis.reflection.MetaObject;
import mybatis.reflection.SystemMetaObject;
import mybatis.reflection.Invoker.Invoker;
import mybatis.reflection.factory.ObjectFactory;
import mybatis.reflection.property.PropertyTokenizer;

/**
 * @Description Bean包装器
 * @Author jiyang.li
 * @Date 2022/9/27 14:53
 **/
public class BeanWrapper extends BaseWrapper {
    // 原始对象
    private Object object;
    // 元class
    private MetaClass metaClass;

    public BeanWrapper(MetaObject metaObject, Object object) {
        super(metaObject);
        this.object = object;
        this.metaClass = MetaClass.forClass(object.getClass());
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        // 如果有[],说明是集合
        if (prop.getIndex() != null) {
            Object collection = resolveCollection(prop, object);
            return getCollectionValue(prop, collection);
        } else {
            return getBeanProperty(prop, object);
        }
    }

    private Object getBeanProperty(PropertyTokenizer prop, Object object) {
        try {
            Invoker getInvoker = metaClass.getGetInvoker(prop.getName());
            return getInvoker.invoke(object, NO_ARGS);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException("Could not get property '" + prop.getName() + "' from " + object.getClass()
                    + ".  Cause: " + t.toString(), t);
        }
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        if (prop.hasNext()) {
            Object collection = resolveCollection(prop, object);
            setCollectionValue(prop, collection, value);
        } else {
            setBeanProperty(prop, object, value);
        }
    }

    private void setBeanProperty(PropertyTokenizer prop, Object object, Object value) {
        Invoker setInvoker = metaClass.getSetInvoker(prop.getName());
        Object[] param = {value};
        try {
            setInvoker.invoke(object, param);
        } catch (Throwable t) {
            throw new RuntimeException("Could not set property '" + prop.getName() + "' of '" + object.getClass()
                    + "' with value '" + value + "' Cause: " + t.toString(), t);
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return metaClass.findProperty(name, useCamelCaseMapping);
    }

    @Override
    public String[] getGetterNames() {
        return metaClass.getGetterNames();
    }

    @Override
    public String[] getSetterNames() {
        return metaClass.getSetterNames();
    }

    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaValue = this.metaObject.metaObjectForProperty(prop.getName());
            if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                return metaClass.getSetterType(name);
            } else {
                return metaValue.getSetterType(prop.getChildren());
            }
        } else {
            return metaClass.getSetterType(name);
        }
    }

    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaObject metaObject = this.metaObject.metaObjectForProperty(name);
            if (metaObject == SystemMetaObject.NULL_META_OBJECT) {
                return metaClass.getGetterType(name);
            } else {
                return metaObject.getGetterType(prop.getChildren());
            }
        } else {
            return metaClass.getGetterType(name);
        }
    }

    @Override
    public boolean hasSetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (metaClass.hasSetter(prop.getIndexName())) {
                MetaObject metaValue = this.metaObject.metaObjectForProperty(prop.getIndexName());
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return metaClass.hasSetter(name);
                } else {
                    return metaValue.hasSetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return metaClass.hasSetter(name);
        }
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (metaClass.hasGetter(name)) {
                MetaObject metaValue = this.metaObject.metaObjectForProperty(prop.getIndexName());
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return metaClass.hasGetter(name);
                } else {
                    return metaObject.hasGetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return metaClass.hasGetter(name);
        }
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        Class<?> setterType = getSetterType(name);
        Object newObject = objectFactory.create(setterType);
        MetaObject metaObject = MetaObject.forObject(newObject, objectFactory, this.metaObject.getWrapperFactory());
		set(prop, metaObject);
        return metaObject;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public void add(Object element) {
		throw new UnsupportedOperationException("add operation is not supported");
    }

    @Override
    public <E> void addAll(List<E> element) {
		throw new UnsupportedOperationException();
    }
}
