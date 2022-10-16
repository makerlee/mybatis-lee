package mybatis.reflection.wrapper;

import mybatis.reflection.MetaObject;

/**
 * @Description 对象包装器工厂 默认实现类
 * @Author jiyang.li
 * @Date 2022/9/26 10:34
 **/
public class DefaultObjectWrapperFactory implements ObjectWrapperFactory {

	@Override
	public boolean hasWrapperFor(Object o) {
		return false;
	}

	@Override
	public ObjectWrapper getWrapperFor(MetaObject metaObject, Object ori) {
		throw new RuntimeException(
				"The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
	}
}
