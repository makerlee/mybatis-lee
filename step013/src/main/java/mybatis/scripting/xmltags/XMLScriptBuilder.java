package mybatis.scripting.xmltags;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Element;

import mybatis.builder.BaseBuilder;
import mybatis.mapping.SqlSource;
import mybatis.scripting.defaults.RawSqlSource;
import mybatis.session.Configuration;

/**
 * @Description xml脚本
 * @Author jiyang.li
 * @Date 2022/10/12 11:07
 **/
public class XMLScriptBuilder extends BaseBuilder {
    private Element element;
    private boolean isDynamic;
    private Class<?> parameterType;

    public XMLScriptBuilder(Configuration configuration, Element element, Class<?> parameterType) {
        super(configuration);
        this.element = element;
        this.parameterType = parameterType;
    }

    public SqlSource parseScriptNode() {
        List<SqlNode> contents = parseDynamicTags(element);
        MixedSqlNode rootSqlNode = new MixedSqlNode(contents);
        return new RawSqlSource(configuration, rootSqlNode, parameterType);
    }

    private List<SqlNode> parseDynamicTags(Element element) {
        List<SqlNode> sqlNodes = new ArrayList<>();
        // 拿到SQL语句
        String text = element.getText();
        sqlNodes.add(new StaticTextSqlNode(text));
        return sqlNodes;
    }
}
