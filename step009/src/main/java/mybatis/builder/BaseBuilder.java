package mybatis.builder;

import mybatis.session.Configuration;
import mybatis.type.TypeAliasRegistry;

/**
 * @Description 建造者
 * @Author jiyang.li
 * @Date 2022/9/19 09:13
 **/
public class BaseBuilder {
	protected final Configuration configuration;
	protected final TypeAliasRegistry typeAliasRegistry;

	public BaseBuilder(Configuration configuration) {
		this.configuration = configuration;
		this.typeAliasRegistry = this.configuration.getTypeAliasRegistry();
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	protected Class<?> resolveAlias(String alias) {
		return typeAliasRegistry.resolveAlia(alias);
	}
}
