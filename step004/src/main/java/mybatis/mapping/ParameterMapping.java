package mybatis.mapping;

import mybatis.session.Configuration;
import mybatis.JdbcType;

/**
 * @Description 参数映射 #{property,javaType=int,jdbcType=NUMERIC}
 * @Author jiyang.li
 * @Date 2022/9/20 09:58
 **/
public class ParameterMapping {
    private Configuration configuration;

    private String property;
    private Class<?> javaType = Object.class;
    private JdbcType jdbcType;

    private ParameterMapping() {
    }

    public static class Builder {
        private ParameterMapping parameterMapping = new ParameterMapping();

        public Builder(Configuration configuration, String property) {
            parameterMapping.configuration = configuration;
            parameterMapping.property = property;
        }

        public Builder javaType(Class<?> type) {
            parameterMapping.javaType = type;
            return this;
        }

        public Builder jdbcType(JdbcType type) {
            parameterMapping.jdbcType = type;
            return this;
        }

        public ParameterMapping build() {
            return parameterMapping;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getProperty() {
        return property;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }
}
