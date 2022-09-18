package mybatis.session.impl;


import mybatis.binding.MapperRegistry;
import mybatis.session.SqlSession;
import mybatis.session.SqlSessionFactory;

/**
 * @Description sqlSession创建工厂，默认实现
 * @Author jiyang.li
 * @Date 2022/9/18 14:38
 **/
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private MapperRegistry mapperRegistry;

    public DefaultSqlSessionFactory(MapperRegistry mapperRegistry) {
        this.mapperRegistry = mapperRegistry;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(mapperRegistry);
    }
}
