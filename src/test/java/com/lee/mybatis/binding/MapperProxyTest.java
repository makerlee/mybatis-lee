package com.lee.mybatis.binding;

import com.lee.mybatis.binding.mappers.ISchoolMapper;
import com.lee.mybatis.session.SqlSession;
import com.lee.mybatis.session.SqlSessionFactory;
import com.lee.mybatis.session.impl.DefaultSqlSessionFactory;
import org.junit.Assert;
import org.junit.Test;

public class MapperProxyTest {

    @Test
    public void test_sqlSession() {
        MapperRegistry registry = new MapperRegistry();
        registry.addMappers("com.lee.mybatis.binding.mappers");

        SqlSessionFactory factory = new DefaultSqlSessionFactory(registry);
        SqlSession sqlSession = factory.openSession();

        ISchoolMapper mapper = sqlSession.getMapper(ISchoolMapper.class);
        String result = mapper.querySchoolName("123");
        System.out.println(result);

        Assert.assertNotNull(result);
    }
}