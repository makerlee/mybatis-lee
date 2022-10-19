package mybatis.mapping;

import mybatis.session.Configuration;
import mybatis.type.JdbcType;
import mybatis.type.TypeHandler;

/**
 * @Description https://mybatis.org/mybatis-3/zh/sqlmap-xml.html#Result_Maps
 * @Author jiyang.li
 * @Date 2022/10/16 21:28
 **/
public class ResultMapping {
    private Configuration configuration;
    private String property;
    private String column;
    private Class<?> javaType;
    private JdbcType jdbcType;
    private TypeHandler<?> typeHandler;

    ResultMapping() {
    }

    public static class Builder {
        private ResultMapping mapping = new ResultMapping();

    }
}
