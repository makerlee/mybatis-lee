package mybatis.session;

import java.io.IOException;
import java.io.Reader;

import mybatis.io.Resources;
import mybatis.session.dao.IUserDao;
import org.junit.Assert;
import org.junit.Test;

public class ConfigurationTest {

    @Test
    public void test_selectOne() throws IOException {
        // 获取sqlSession
        Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 获取mapper代理
        IUserDao mapper = sqlSession.getMapper(IUserDao.class);

        // 验证
        String user = mapper.queryUserInfoById("1000");
        System.out.println(user);
        Assert.assertNotNull(user);
    }

}