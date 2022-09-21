package mybatis.session;

import java.util.HashMap;
import java.util.Map;

import mybatis.TypeAliasRegistry;
import mybatis.binding.MapperRegistry;
import mybatis.datasource.druid.DruidDataSourceFactory;
import mybatis.mapping.Environment;
import mybatis.mapping.MappedStatement;
import mybatis.transaction.jdbc.JdbcTransactionFactory;

/**
 * @Description 贯穿整个mybatis生命周期的配置
 * @Author jiyang.li
 * @Date 2022/9/18 15:18
 **/
public class Configuration {
	// 环境
	protected Environment environment;

	// 注入configuration
	protected MapperRegistry mapperRegistry = new MapperRegistry(this);

	// 映射语句:key是接口方法的全路径名
	protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

	// 类型别名注册器
	protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

	public Configuration() {
		typeAliasRegistry.registerAlia("JDBC", JdbcTransactionFactory.class);
		typeAliasRegistry.registerAlia("DRUID", DruidDataSourceFactory.class);
	}

	public void addMappers(String packageName) {
		mapperRegistry.addMappers(packageName);
	}

	public <T> void addMapper(Class<T> type) {
		mapperRegistry.addMapper(type);
	}

	public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
		return mapperRegistry.getMapper(type, sqlSession);
	}

	public boolean hasMapper(Class<?> type) {
		return mapperRegistry.hasMapper(type);
	}

	public void addMappedStatement(MappedStatement statement) {
		mappedStatements.put(statement.getId(), statement);
	}

	public MappedStatement getStatement(String id) {
		return mappedStatements.get(id);
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public TypeAliasRegistry getTypeAliasRegistry() {
		return typeAliasRegistry;
	}
}
