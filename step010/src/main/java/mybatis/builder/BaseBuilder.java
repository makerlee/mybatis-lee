package mybatis.builder;

import mybatis.session.Configuration;
import mybatis.type.TypeAliasRegistry;
import mybatis.type.TypeHandlerRegistry;

/**
 * @Description 建造者
 * @Author jiyang.li
 * @Date 2022/9/19 09:13
 **/
public class BaseBuilder {
	protected final Configuration configuration;
	protected final TypeAliasRegistry typeAliasRegistry;
	protected final TypeHandlerRegistry typeHandlerRegistry;

	public BaseBuilder(Configuration configuration) {
		this.configuration = configuration;
		this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
		this.typeHandlerRegistry = this.configuration.getTypeHandlerRegistry();
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	protected Class<?> resolveAlias(String alias) {
		return typeAliasRegistry.resolveAlia(alias);
	}
}
