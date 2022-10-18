package mybatis.builder;

import java.util.List;

import mybatis.mapping.BoundSql;
import mybatis.mapping.ParameterMapping;
import mybatis.mapping.SqlSource;
import mybatis.session.Configuration;

/**
 * @Description 静态SQL
 * @Author jiyang.li
 * @Date 2022/10/11 11:37
 **/
public class StaticSqlSource implements SqlSource {
    private String sql;
    private List<ParameterMapping> parameterMappings;
    private Configuration configuration;

    public StaticSqlSource(String sql, List<ParameterMapping> parameterMappings, Configuration configuration) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.configuration = configuration;
    }

    @Override
    public BoundSql getBoundSql(Object paramObject) {
        return new BoundSql(configuration, sql, parameterMappings, paramObject);
    }
}
