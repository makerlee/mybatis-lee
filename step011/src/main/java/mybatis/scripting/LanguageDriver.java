package mybatis.scripting;

import mybatis.executor.parameter.ParameterHandler;
import mybatis.mapping.BoundSql;
import mybatis.mapping.MappedStatement;
import mybatis.mapping.SqlSource;
import mybatis.session.Configuration;
import org.dom4j.Element;

/**
 * @Description 脚本语言驱动
 * @Author jiyang.li
 * @Date 2022/10/10 11:32
 **/
public interface LanguageDriver {

    SqlSource createSqlSource(Configuration configuration, Element script, Class<?> paramType);

    ParameterHandler createParameterHandler(MappedStatement ms, Object paramObject, BoundSql boundSql);
}
