package mybatis.scripting;

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
}
