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
     * Execute an insert statement with the given parameter object. Any generated
     * autoincrement values or selectKey entries will modify the given parameter
     * object properties. Only the number of rows affected will be returned.
     * 插入记录，容许传入参数。
     *
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return int The number of rows affected by the insert. 注意返回的是受影响的行数
     */
    int insert(String statement, Object parameter);

    /**
     * Execute an update statement. The number of rows affected will be returned.
     * 更新记录
     *
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return int The number of rows affected by the update. 返回的是受影响的行数
     */
    int update(String statement, Object parameter);

    /**
     * Execute a delete statement. The number of rows affected will be returned.
     * 删除记录
     *
     * @param statement Unique identifier matching the statement to execute.
     * @param parameter A parameter object to pass to the statement.
     * @return int The number of rows affected by the delete. 返回的是受影响的行数
     */
    Object delete(String statement, Object parameter);

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
     * 以下是事务控制方法 commit,rollback
     * Flushes batch statements and commits database connection.
     * Note that database connection will not be committed if no updates/deletes/inserts were called.
     */
    void commit();

    /**
     * 获取配置
     * @return configuration
     */
    Configuration getConfiguration();
}
