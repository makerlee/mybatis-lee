package mybatis.session.impl;


import mybatis.binding.MapperRegistry;
import mybatis.session.SqlSession;

/**
 * @Description sqlSession默认实现
 * @Author jiyang.li
 * @Date 2022/9/17 16:18
 **/
public class DefaultSqlSession implements SqlSession {

    private MapperRegistry mapperRegistry;

    public DefaultSqlSession(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public <T> T selectOne(String statementId) {
        return (T) ("代理:" + statementId);
    }

    @Override
    public <T> T selectOne(String statementId, Object parameter) {
        return (T) ("代理:" + statementId + ",参数:" + parameter);
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return mapperRegistry.getMapper(type, this);
    }
}
