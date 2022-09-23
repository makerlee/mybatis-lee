package mybatis.session.impl;


import mybatis.session.Configuration;
import mybatis.session.SqlSession;
import mybatis.session.SqlSessionFactory;

/**
 * @Description sqlSession创建工厂，默认实现
 * @Author jiyang.li
 * @Date 2022/9/18 14:38
 **/
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private final Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
