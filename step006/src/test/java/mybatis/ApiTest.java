package mybatis;

import java.io.IOException;

import mybatis.datasource.pooled.PooledDataSource;
import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import mybatis.io.Resources;
import mybatis.mapper.IUserMapper;
import mybatis.po.User;
import mybatis.session.SqlSession;
import mybatis.session.SqlSessionFactory;
import mybatis.session.SqlSessionFactoryBuilder;

import javax.sql.DataSource;

/**
 * @Description 真实dataSource查询
 * @Author jiyang.li
 * @Date 2022/9/21 10:21
 **/
public class ApiTest {
	@Test
	public void test_selectOneFromDB_withDruidDataSource() throws IOException {
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
				.build(Resources.getResourceAsReader("mybatis-config.xml"));

        SqlSession sqlSession = sqlSessionFactory.openSession();
        DataSource dataSource = sqlSession.getConfiguration().getEnvironment().getDataSource();
        IUserMapper mapper = sqlSession.getMapper(IUserMapper.class);
        for (int i=0; i< 30; i++){
            User user = mapper.queryUserInfoById(1);
            System.out.println(JSONObject.toJSONString(user));
            System.out.println("===" + ((PooledDataSource)dataSource).getState());
        }
    }
}
