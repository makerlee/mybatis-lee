package mybatis.builder.xml;

import java.io.InputStream;
import java.util.List;

import mybatis.io.Resources;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import mybatis.builder.BaseBuilder;
import mybatis.session.Configuration;

/**
 * @Description xml映射器构建
 * @Author jiyang.li
 * @Date 2022/10/10 09:55
 **/
public class XmlMapperBuilder extends BaseBuilder {
	private Element element;
	private String resource;
	private String currentNamespace;

	public XmlMapperBuilder(InputStream inputStream, Configuration configuration, String resource) throws DocumentException {
        this(new SAXReader().read(inputStream), configuration, resource);
    }

	private XmlMapperBuilder(Document document, Configuration configuration, String resource) {
		super(configuration);
		this.element = document.getRootElement();
		this.resource = resource;
	}

    /**
     * 解析
     */
    public void parse() throws Exception {
        if (!configuration.isResourceLoaded(resource)) {
			configurationElement(element);
			// 标记已加载
			configuration.addLoadedResource(resource);
			// 绑定到namespace
			configuration.addMapper(Resources.classForName(currentNamespace));
        }
    }

	// 配置mapper元素
	// <mapper namespace="org.mybatis.example.BlogMapper">
	//   <select id="selectBlog" parameterType="int" resultType="Blog">
	//    select * from Blog where id = #{id}
	//   </select>
	// </mapper>
	private void configurationElement(Element element) {
    	currentNamespace = element.attributeValue("namespace");
    	if (currentNamespace == null || "".equals(currentNamespace)) {
			throw new RuntimeException("Mapper's namespace cannot be empty.");
		}

    	// 解析select insert update delete
		buildStatementFromContext(element.elements("select"));
	}

	private void buildStatementFromContext(List<Element> elements) {
		for (Element element : elements) {
			XmlStatementBuilder builder = new XmlStatementBuilder(configuration, element, currentNamespace);
			builder.parseStatementNode();
		}
	}
}
