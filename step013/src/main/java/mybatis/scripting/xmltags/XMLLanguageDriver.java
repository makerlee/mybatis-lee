package mybatis.scripting.xmltags;

import mybatis.builder.StaticSqlSource;
import mybatis.scripting.defaults.RawSqlSource;
import org.dom4j.Element;

import mybatis.executor.parameter.ParameterHandler;
import mybatis.mapping.BoundSql;
import mybatis.mapping.MappedStatement;
import mybatis.mapping.SqlSource;
import mybatis.scripting.LanguageDriver;
import mybatis.scripting.defaults.DefaultParameterHandler;
import mybatis.session.Configuration;

/**
 * @Description XML语言驱动器
 * @Author jiyang.li
 * @Date 2022/10/12 11:06
 **/
public class XMLLanguageDriver implements LanguageDriver {

    @Override
    public SqlSource createSqlSource(Configuration configuration, Element script, Class<?> paramType) {
        XMLScriptBuilder scriptBuilder = new XMLScriptBuilder(configuration, script, paramType);
        return scriptBuilder.parseScriptNode();
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> paramType) {
        // 暂时不解析动态sql
        return new RawSqlSource(configuration, script, paramType);
    }

    @Override
    public ParameterHandler createParameterHandler(MappedStatement ms, Object paramObject, BoundSql boundSql) {
        return new DefaultParameterHandler(ms, paramObject, boundSql);
    }
}
