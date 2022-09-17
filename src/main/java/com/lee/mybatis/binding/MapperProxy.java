package com.lee.mybatis.binding;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Description mapper代理类
 * @Author jiyang.li
 * @Date 2022/9/16 22:09
 **/
public class MapperProxy<T> implements InvocationHandler, Serializable {
    private static final long serialVersion = -6424540398559729838L;

    private Map<String, String> sqlSession;
    private final Class<T> mapperInterface;

    public MapperProxy(Map<String, String> sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(args);
        } else {
            return "执行代理方法:" + sqlSession.get(String.format("%s.%s", mapperInterface.getName(), method.getName()));
        }
    }
}
