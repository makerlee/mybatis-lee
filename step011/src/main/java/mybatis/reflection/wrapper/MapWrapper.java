package mybatis.reflection.wrapper;

import java.util.List;
import java.util.Map;

import mybatis.reflection.MetaObject;
import mybatis.reflection.SystemMetaObject;
import mybatis.reflection.factory.ObjectFactory;
import mybatis.reflection.property.PropertyTokenizer;

/**
 * @Description Map类型的包装器
 * @Author jiyang.li
 * @Date 2022/9/26 21:44
 **/
public class MapWrapper extends BaseWrapper {
	// 原来的对象
	private Map<String, Object> map;

	public MapWrapper(MetaObject metaObject, Map<String, Object> map) {
		super(metaObject);
		this.map = map;
	}

	@Override
	public Object get(PropertyTokenizer prop) {
		if (prop.getIndex() != null) {
			Object collection = resolveCollection(prop, map);
			return getCollectionValue(prop, collection);
		} else {
			return map.get(prop.getName());
		}
	}

	@Override
	public void set(PropertyTokenizer prop, Object value) {
		if (prop.getIndex() != null) {
			Object collection = resolveCollection(prop, map);
			setCollectionValue(prop, collection, value);
		} else {
			map.put(prop.getName(), value);
		}
	}

	@Override
	public String findProperty(String name, boolean useCamelCaseMapping) {
		return name;
	}

	@Override
	public String[] getGetterNames() {
		return map.keySet().toArray(new String[map.keySet().size()]);
	}

	@Override
	public String[] getSetterNames() {
		return map.keySet().toArray(new String[map.keySet().size()]);
	}

	@Override
	public Class<?> getSetterType(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexName());
			if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
				return Object.class;
			} else {
				return metaValue.getSetterType(prop.getChildren());
			}
		} else {
			if (map.get(name) != null) {
				return map.get(name).getClass();
			} else {
				return Object.class;
			}
		}
	}

	@Override
	public Class<?> getGetterType(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaObject metaValue = this.metaObject.metaObjectForProperty(prop.getIndexName());
			if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
				return Object.class;
			} else {
				return metaValue.getGetterType(prop.getChildren());
			}
		} else {
			if (map.get(name) != null) {
				return map.get(name).getClass();
			} else {
				return Object.class;
			}
		}
	}

	@Override
	public boolean hasSetter(String name) {
		return false;
	}

	@Override
	public boolean hasGetter(String name) {
		return true;
	}

	@Override
	public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
		return null;
	}

	@Override
	public boolean isCollection() {
		return false;
	}

	@Override
	public void add(Object element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <E> void addAll(List<E> element) {
		throw new UnsupportedOperationException();
	}
}
