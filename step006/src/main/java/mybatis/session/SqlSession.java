package mybatis.session;

/**
 * @Description 用来执行SQL；获取mapper interface对应的代理对象(MapperProxy)
 * @Author jiyang.li
 * @Date 2022/9/17 16:07
 **/
public interface SqlSession {
    /**
     * 根据指定的sql id 返回一个PO对象
     * @param statementId xml中的SQL语句id
     * @param <T> 表对应的PO类
     * @return PO对象
     */
    <T> T selectOne(String statementId);


    /**
     * 根据指定的sql id 返回一个PO对象
     * @param statementId xml中的SQL语句id
     * @param <T> 表对应的PO类
     * @param parameter sql传入的参数
     * @return PO对象
     */
    <T> T selectOne(String statementId, Object parameter);

    /**
     * Retrieves a mapper.
     * 得到映射器，这个巧妙的使用了泛型，使得类型安全
     *
     * @param <T>  the mapper type
     * @param type Mapper interface class
     * @return a mapper bound to this SqlSession
     */
    <T> T getMapper(Class<T> type);

    /**
     * 获取配置
     * @return configuration
     */
    Configuration getConfiguration();
}
