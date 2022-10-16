package mybatis.builder.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import mybatis.builder.BaseBuilder;
import mybatis.io.Resources;
import mybatis.mapping.MappedStatement;
import mybatis.mapping.SqlCommandType;
import mybatis.session.Configuration;

/**
 * @Description xml格式配置建造器
 * @Author jiyang.li
 * @Date 2022/9/19 09:20
 **/
public class XmlConfigBuilder extends BaseBuilder {
	private Element root;

	public XmlConfigBuilder(Reader reader) {
		// 1.调用父类初始化
		super(new Configuration());

		// 2.解析xml
		SAXReader saxReader = new SAXReader();
		try {
			Document document = saxReader.read(new InputSource(reader));
			root = document.getRootElement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Configuration parse() {
		try {
			mapperElement(root.element("mappers"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configuration;
	}

	private void mapperElement(Element mappers) throws IOException, DocumentException, ClassNotFoundException {
		List<Element> mapperList = mappers.elements("mapper");
		for (Element mapper : mapperList) {
			String resource = mapper.attributeValue("resource");
			Reader reader = Resources.getResourceAsReader(resource);
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(new InputSource(reader));
			Element rootElement = document.getRootElement();
			// 命名空间
			String namespace = rootElement.attributeValue("namespace");
			// select标签
			List<Element> selectNodes = rootElement.elements("select");
			for (Element selectNode : selectNodes) {
				String id = selectNode.attributeValue("id");
				String parameterType = selectNode.attributeValue("parameterType");
				String resultType = selectNode.attributeValue("resultType");
				String sql = selectNode.getText();

				// ?配置
				Map<Integer, String> parameter = new HashMap<>();
				Pattern pattern = Pattern.compile("(#\\{(.*?)})");
				Matcher matcher = pattern.matcher(sql);
				for (int i = 1; matcher.find(); i++) {
					String g1 = matcher.group(1);
					String g2 = matcher.group(2);
					parameter.put(i, g2);
					sql = sql.replace(g1, "?");
				}

				String msId = namespace + "." + id;
				SqlCommandType sqlCommandType = SqlCommandType
						.valueOf(selectNode.getName().toUpperCase(Locale.ENGLISH));
				MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType,
						parameterType, resultType, sql, parameter).build();

				configuration.addMappedStatement(mappedStatement);
			}
			configuration.addMapper(Resources.classForName(namespace));
		}
	}
}
