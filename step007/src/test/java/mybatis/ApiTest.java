package mybatis;

import com.alibaba.fastjson.JSON;
import mybatis.mapper.IUserMapper;
import mybatis.po.User;
import mybatis.session.SqlSession;
import org.junit.Test;

import mybatis.io.Resources;
import mybatis.session.SqlSessionFactory;
import mybatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

/**
 * @Description 测试
 * @Author jiyang.li
 * @Date 2022/9/24 10:06
 **/
public class ApiTest {
	@Test
	public void test_Executor() throws IOException {
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
				.build(Resources.getResourceAsReader("mybatis-config.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();

        IUserMapper mapper = sqlSession.getMapper(IUserMapper.class);
        User user = mapper.queryUserInfoById(1);
        System.out.println(JSON.toJSONString(user));
    }
}
