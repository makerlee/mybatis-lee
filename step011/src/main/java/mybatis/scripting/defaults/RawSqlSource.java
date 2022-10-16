package mybatis.scripting.defaults;

import mybatis.builder.SqlSourceBuilder;
import mybatis.mapping.BoundSql;
import mybatis.mapping.SqlSource;
import mybatis.scripting.xmltags.DynamicContext;
import mybatis.scripting.xmltags.SqlNode;
import mybatis.session.Configuration;

import java.util.HashMap;

/**
 * @Description 原始SQL源码，比DynamicSqlSource动态SQL处理快
 * @Author jiyang.li
 * @Date 2022/10/10 13:59
 **/
public class RawSqlSource implements SqlSource  {
    private SqlSource sqlSource;

    public RawSqlSource(Configuration configuration, SqlNode rootSqlNode, Class<?> paramType) {
        this(configuration, getSql(configuration, rootSqlNode), paramType);
    }

    public RawSqlSource(Configuration configuration, String sql, Class<?> parameterType) {
        SqlSourceBuilder sqlSourceBuilder = new SqlSourceBuilder(configuration);
        Class<?> clazz = parameterType == null ? Object.class : parameterType;
        sqlSource = sqlSourceBuilder.parse(sql, clazz, new HashMap<>());
    }

    @Override
    public BoundSql getBoundSql(Object paramObject) {
        return sqlSource.getBoundSql(paramObject);
    }

    private static String getSql(Configuration configuration, SqlNode rootSqlNode) {
        DynamicContext context = new DynamicContext(configuration, null);
        rootSqlNode.apply(context);
        return context.getSql();
    }
}
