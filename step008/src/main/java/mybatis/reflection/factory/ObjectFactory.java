package mybatis.reflection.factory;

import java.util.List;
import java.util.Properties;

/**
 * @Description 对象工厂接口
 * @Author jiyang.li
 * @Date 2022/9/26 10:07
 **/
public interface ObjectFactory {
    void setProperties(Properties properties);

    <T> T create(Class<T> type);

    <T> T create(Class<T> type, List<Class<?>> constructorArgsType, List<Object> constructorArgs);

    <T> boolean isCollection(Class<T> type);
}
