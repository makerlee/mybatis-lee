package mybatis.reflection.wrapper;

import java.util.List;
import java.util.Map;

import mybatis.reflection.MetaObject;
import mybatis.reflection.property.PropertyTokenizer;

/**
 * @Description 对象包装器抽象基类，提供一些工具方法
 * @Author jiyang.li
 * @Date 2022/9/26 10:38
 **/
public abstract class BaseWrapper implements ObjectWrapper {
	protected static final Object[] NO_ARGS = new Object[0];
	protected MetaObject metaObject;

	public BaseWrapper(MetaObject metaObject) {
		this.metaObject = metaObject;
	}

	protected Object resolveCollection(PropertyTokenizer tokenizer, Object object) {
		if ("".equals(tokenizer.getName())) {
			return object;
		} else {
			return metaObject.getValue(tokenizer.getName());
		}
	}

	protected Object getCollectionValue(PropertyTokenizer prop, Object collection) {
		if (collection instanceof Map) {
			// map['name']
			return ((Map) collection).get(prop.getIndex());
		} else {
			int i = Integer.parseInt(prop.getIndex());
			if (collection instanceof List) {
				return ((List) collection).get(i);
			} else if (collection instanceof Object[]) {
				return ((Object[]) collection)[i];
			} else if (collection instanceof char[]) {
				return ((char[]) collection)[i];
			} else if (collection instanceof boolean[]) {
				return ((boolean[]) collection)[i];
			} else if (collection instanceof byte[]) {
				return ((byte[]) collection)[i];
			} else if (collection instanceof double[]) {
				return ((double[]) collection)[i];
			} else if (collection instanceof float[]) {
				return ((float[]) collection)[i];
			} else if (collection instanceof int[]) {
				return ((int[]) collection)[i];
			} else if (collection instanceof long[]) {
				return ((long[]) collection)[i];
			} else if (collection instanceof short[]) {
				return ((short[]) collection)[i];
			} else {
				throw new RuntimeException(
						"The '" + prop.getName() + "' property of " + collection + " is not a List or Array.");
			}
		}
	}

	/**
	 * 设集合的值
	 * 中括号有2个意思，一个是Map，一个是List或数组
	 */
	protected void setCollectionValue(PropertyTokenizer prop, Object collection, Object value) {
		if (collection instanceof Map) {
			((Map) collection).put(prop.getIndex(), value);
		} else {
			int i = Integer.parseInt(prop.getIndex());
			if (collection instanceof List) {
				((List) collection).set(i, value);
			} else if (collection instanceof Object[]) {
				((Object[]) collection)[i] = value;
			} else if (collection instanceof char[]) {
				((char[]) collection)[i] = (Character) value;
			} else if (collection instanceof boolean[]) {
				((boolean[]) collection)[i] = (Boolean) value;
			} else if (collection instanceof byte[]) {
				((byte[]) collection)[i] = (Byte) value;
			} else if (collection instanceof double[]) {
				((double[]) collection)[i] = (Double) value;
			} else if (collection instanceof float[]) {
				((float[]) collection)[i] = (Float) value;
			} else if (collection instanceof int[]) {
				((int[]) collection)[i] = (Integer) value;
			} else if (collection instanceof long[]) {
				((long[]) collection)[i] = (Long) value;
			} else if (collection instanceof short[]) {
				((short[]) collection)[i] = (Short) value;
			} else {
				throw new RuntimeException("The '" + prop.getName() + "' property of " + collection + " is not a List or Array.");
			}
		}
	}
}