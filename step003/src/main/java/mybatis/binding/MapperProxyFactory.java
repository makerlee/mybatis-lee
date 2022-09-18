package mybatis.binding;


import mybatis.session.SqlSession;

import java.lang.reflect.Proxy;

/**
 * @Description mapper代理类工厂
 * @Author jiyang.li
 * @Date 2022/9/16 22:17
 **/
public class MapperProxyFactory<T> {
    private final Class<T> mapperInterface;

    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public T newInstance(SqlSession sqlSession) {
        MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(),
                new Class[]{mapperInterface}, mapperProxy);
    }
}
