package mybatis.reflection.wrapper;

import mybatis.reflection.MetaObject;

/**
 * @Description ObjectWrapper工厂接口
 * @Author jiyang.li
 * @Date 2022/9/26 10:25
 **/
public interface ObjectWrapperFactory {
    boolean hasWrapperFor(Object o);

    ObjectWrapper getWrapperFor(MetaObject metaObject, Object ori);
}
