package mybatis.builder.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

import mybatis.builder.BaseBuilder;
import mybatis.datasource.DataSourceFactory;
import mybatis.io.Resources;
import mybatis.mapping.BoundSql;
import mybatis.mapping.Environment;
import mybatis.mapping.MappedStatement;
import mybatis.mapping.SqlCommandType;
import mybatis.session.Configuration;
import mybatis.transaction.TransactionFactory;

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
			// 解析environment
			environmentsElement(root.element("environments"));

			// 解析mapper
			mapperElement(root.element("mappers"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configuration;
	}

	/**
	 * <environments default="development"> <environment id="development">
	 * <transactionManager type="JDBC"> <property name="自定义属性" value="自定义value"/>
	 * </transactionManager> <dataSource type="POOLED">
	 * <property name="driver" value="${driver}"/>
	 * <property name="url" value="${url}"/>
	 * <property name="username" value="${username}"/>
	 * <property name="password" value="${password}"/> </dataSource> </environment>
	 * </environments>
	 */
	private void environmentsElement(Element environments) throws Exception {
		String defaultEnv = environments.attributeValue("default");
		List<Element> environmentNode = environments.elements("environment");
		for (Element env : environmentNode) {
			String id = env.attributeValue("id");
			if (!id.equals(defaultEnv)) {
				continue;
			}
			String transManagerName = env.element("transactionManager").attributeValue("type");
			TransactionFactory transactionFactory = (TransactionFactory) typeAliasRegistry
					.resolveAlia(transManagerName.toUpperCase(Locale.ENGLISH)).newInstance();

			Element dsNode = env.element("dataSource");
			DataSourceFactory dataSourceFactory = (DataSourceFactory) typeAliasRegistry
					.resolveAlia(dsNode.attributeValue("type")).newInstance();
			List<Element> propertyNodes = dsNode.elements("property");
			Properties dsProps = new Properties();
			for (Element element : propertyNodes) {
				dsProps.put(element.attributeValue("name"), element.attributeValue("value"));
			}
			dataSourceFactory.setProperties(dsProps);
			DataSource dataSource = dataSourceFactory.getDataSource();

			// 构建环境
			Environment environment = new Environment.Builder(defaultEnv).transactionFactory(transactionFactory)
					.dataSource(dataSource).build();

			configuration.setEnvironment(environment);
		}
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

				BoundSql boundSql = new BoundSql(sql, parameter, parameterType, resultType);

				MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType,
						boundSql).build();

				configuration.addMappedStatement(mappedStatement);
			}
			configuration.addMapper(Resources.classForName(namespace));
		}
	}
}
