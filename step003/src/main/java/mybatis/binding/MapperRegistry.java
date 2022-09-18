package mybatis.binding;

import cn.hutool.core.lang.ClassScanner;
import mybatis.session.SqlSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Description Mapper注册表
 * @Author jiyang.li
 * @Date 2022/9/17 16:21
 **/
public class MapperRegistry {

    /**
     * 将已添加的映射器代理加入到 HashMap
     * 注意: value并不是MapperProxy（因为MapperProxy只是个InvocationHandler，并不是直接生产的代理类）
     * 所以需要通过MapperProxyFactory来生成代理类
     */
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    public Map<Class<?>, MapperProxyFactory<?>> getKnownMappers() {
        return knownMappers;
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new RuntimeException("Type " + type + "is not known to the MapperRegistry.");
        }
        return mapperProxyFactory.newInstance(sqlSession);
    }

    public <T> void addMapper(Class<T> mapper) {
        if (!mapper.isInterface()) {
            return;
        }
        //重复添加 抛异常
        if (hasMapper(mapper)) {
            throw new RuntimeException("Type " + mapper + "is already known to the MapperRegistry.");
        }

        knownMappers.put(mapper, new MapperProxyFactory<>(mapper));
    }

    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }


    public void addMappers(String packageName) {
        //hutool的工具包
        Set<Class<?>> mapperSet = ClassScanner.scanPackage(packageName);
        for (Class<?> mapperClass : mapperSet) {
            addMapper(mapperClass);
        }
    }
}
