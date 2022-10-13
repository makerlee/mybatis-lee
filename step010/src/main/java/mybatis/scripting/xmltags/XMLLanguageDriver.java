package mybatis.scripting.xmltags;

import mybatis.mapping.SqlSource;
import mybatis.scripting.LanguageDriver;
import mybatis.session.Configuration;
import org.dom4j.Element;

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
}
