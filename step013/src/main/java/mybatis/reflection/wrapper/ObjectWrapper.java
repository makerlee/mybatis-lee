package mybatis.reflection.wrapper;

import java.util.List;

import mybatis.reflection.MetaObject;
import mybatis.reflection.factory.ObjectFactory;
import mybatis.reflection.property.PropertyTokenizer;

/**
 * @Description 对象包装器接口
 * @Author jiyang.li
 * @Date 2022/9/26 09:43
 **/
public interface ObjectWrapper {
	Object get(PropertyTokenizer propertyTokenizer);

	void set(PropertyTokenizer tokenizer, Object value);

	String findProperty(String name, boolean useCamelCaseMapping);

	String[] getGetterNames();

	String[] getSetterNames();

	// 取得setter的类型
	Class<?> getSetterType(String name);

	// 取得getter的类型
	Class<?> getGetterType(String name);

	// 是否有指定的setter
	boolean hasSetter(String name);

	// 是否有指定的getter
	boolean hasGetter(String name);

	// 实例化属性
	MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

	// 是否是集合
	boolean isCollection();

	// 添加属性
	void add(Object element);

	// 添加属性
	<E> void addAll(List<E> element);
}
