package mybatis.builder.xml;

import java.util.Locale;

import org.dom4j.Element;

import mybatis.builder.BaseBuilder;
import mybatis.mapping.MappedStatement;
import mybatis.mapping.SqlCommandType;
import mybatis.mapping.SqlSource;
import mybatis.scripting.LanguageDriver;
import mybatis.session.Configuration;

/**
 * @Description xml语句构造器
 * @Author jiyang.li
 * @Date 2022/10/10 10:48
 **/
public class XmlStatementBuilder extends BaseBuilder {
	private String currentNamespace;
	private Element element;

	public XmlStatementBuilder(Configuration configuration, Element element, String namespace) {
		super(configuration);
		this.element = element;
		this.currentNamespace = namespace;
	}

	// <select
	// id="selectPerson"
	// parameterType="int"
	// parameterMap="deprecated"
	// resultType="hashmap"
	// resultMap="personResultMap"
	// flushCache="false"
	// useCache="true"
	// timeout="10000"
	// fetchSize="256"
	// statementType="PREPARED"
	// resultSetType="FORWARD_ONLY">
	// SELECT * FROM PERSON WHERE ID = #{id}
	// </select>
	public void parseStatementNode() {
		String id = element.attributeValue("id");
		// 参数类型
		String paramType = element.attributeValue("parameterType");
		Class<?> paramTypeClass = resolveAlias(paramType);
		// 返回值类型
		String resultType = element.attributeValue("resultType");
		Class<?> resultTypeClazz = resolveAlias(resultType);
		// SQL类型
		String name = element.getName();
		SqlCommandType sqlCommandType = SqlCommandType.valueOf(name.toUpperCase(Locale.ENGLISH));

		// 获取默认语言驱动器
		Class<?> langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
		LanguageDriver languageDriver = configuration.getLanguageRegistry().getDriver(langClass);
		SqlSource sqlSource = languageDriver.createSqlSource(configuration, element, paramTypeClass);

		MappedStatement ms = new MappedStatement.Builder(configuration, currentNamespace + "." + id, sqlCommandType,
				sqlSource, resultTypeClazz).build();

		configuration.addMappedStatement(ms);
	}
}
