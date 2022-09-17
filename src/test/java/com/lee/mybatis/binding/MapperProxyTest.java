package com.lee.mybatis.binding;

import com.lee.mybatis.binding.mappers.IUserMapper;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class MapperProxyTest {

    @Test
    public void test_mapperProxyFactory() {
        MapperProxyFactory<IUserMapper> factory = new MapperProxyFactory(IUserMapper.class);

        Map<String, String> sqlSession = new HashMap<>();
        sqlSession.put("com.lee.mybatis.binding.mappers.IUserMapper.queryUserName", "mock invoke sqlSession method");

        IUserMapper iUserMapper = factory.newInstance(sqlSession);

        String s = iUserMapper.queryUserName("123");

        Assert.assertEquals("执行代理方法:mock invoke sqlSession method", s);
    }
}