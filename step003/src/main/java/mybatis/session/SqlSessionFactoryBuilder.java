package mybatis.session;

import mybatis.builder.xml.XmlConfigBuilder;
import mybatis.session.impl.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * mybatis入口
 *
 * @Description 构建SqlSessionFactory的工厂
 * @Author jiyang.li
 * @Date 2022/9/18 15:15
 **/
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) {
        XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder(reader);
        return build(xmlConfigBuilder.parse());
    }

    public SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }
}
