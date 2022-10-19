package mybatis.scripting;

import org.dom4j.Element;

import mybatis.executor.parameter.ParameterHandler;
import mybatis.mapping.BoundSql;
import mybatis.mapping.MappedStatement;
import mybatis.mapping.SqlSource;
import mybatis.session.Configuration;

/**
 * @Description 脚本语言驱动
 * @Author jiyang.li
 * @Date 2022/10/10 11:32
 **/
public interface LanguageDriver {

	SqlSource createSqlSource(Configuration configuration, Element script, Class<?> paramType);

	// 创建SQL源码：注解方式
	SqlSource createSqlSource(Configuration configuration, String script, Class<?> paramType);

	ParameterHandler createParameterHandler(MappedStatement ms, Object paramObject, BoundSql boundSql);
}
