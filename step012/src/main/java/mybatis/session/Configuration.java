package mybatis.session;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mybatis.binding.MapperRegistry;
import mybatis.datasource.druid.DruidDataSourceFactory;
import mybatis.datasource.pooled.PooledDataSourceFactory;
import mybatis.datasource.unpooled.UnPooledDataSourceFactory;
import mybatis.executor.Executor;
import mybatis.executor.SimpleExecutor;
import mybatis.executor.parameter.ParameterHandler;
import mybatis.executor.resultset.DefaultResultSetHandler;
import mybatis.executor.resultset.ResultSetHandler;
import mybatis.executor.statement.PrepareStatementHandler;
import mybatis.executor.statement.StatementHandler;
import mybatis.mapping.BoundSql;
import mybatis.mapping.Environment;
import mybatis.mapping.MappedStatement;
import mybatis.reflection.MetaObject;
import mybatis.reflection.factory.DefaultObjectFactory;
import mybatis.reflection.factory.ObjectFactory;
import mybatis.reflection.wrapper.DefaultObjectWrapperFactory;
import mybatis.reflection.wrapper.ObjectWrapperFactory;
import mybatis.scripting.LanguageDriver;
import mybatis.scripting.LanguageDriverRegistry;
import mybatis.scripting.xmltags.XMLLanguageDriver;
import mybatis.transaction.Transaction;
import mybatis.transaction.jdbc.JdbcTransactionFactory;
import mybatis.type.TypeAliasRegistry;
import mybatis.type.TypeHandlerRegistry;

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
	protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();

	// 类型处理器注册机
	protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();

	// 对象工厂、对象包装器工厂
	protected ObjectFactory objectFactory = new DefaultObjectFactory();
	protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

	private final Set<String> loadedResource = new HashSet<>();

	protected String databaseId;

	public Configuration() {
		typeAliasRegistry.registerAlia("JDBC", JdbcTransactionFactory.class);
		typeAliasRegistry.registerAlia("DRUID", DruidDataSourceFactory.class);
		typeAliasRegistry.registerAlia("POOLED", PooledDataSourceFactory.class);
		typeAliasRegistry.registerAlia("UNPOOLED", UnPooledDataSourceFactory.class);

		languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
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

	/**
	 * 执行器
	 */
	public Executor newExecutor(Transaction tx) {
		return new SimpleExecutor(this, tx);
	}

	/**
	 * 创建结果集处理器
	 */
	public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds,
			ResultHandler resultHandler, BoundSql boundSql) {
		return new DefaultResultSetHandler(executor, mappedStatement, resultHandler, rowBounds, boundSql);
	}

	/**
	 * 创建语句处理器
	 */
	public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter,
			RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
		return new PrepareStatementHandler(executor, mappedStatement, parameter, rowBounds, resultHandler, boundSql);
	}

	public boolean isResourceLoaded(String resource) {
		return loadedResource.contains(resource);
	}

	public void addLoadedResource(String resource) {
		loadedResource.add(resource);
	}

	public LanguageDriverRegistry getLanguageRegistry() {
		return languageRegistry;
	}

	public MetaObject newMetaObject(Object object) {
		return MetaObject.forObject(object, objectFactory, objectWrapperFactory);
	}

	public Object getDatabaseId() {
		return databaseId;
	}

	public TypeHandlerRegistry getTypeHandlerRegistry() {
		return typeHandlerRegistry;
	}

	public ParameterHandler newParameterHandler(MappedStatement ms, Object paramObject, BoundSql boundSql) {
		ParameterHandler parameterHandler = ms.getLang().createParameterHandler(ms, paramObject, boundSql);
		// 插件的一些参数也在这里 暂不实现

		return parameterHandler;
	}

	public LanguageDriver getDefaultScriptingLangInstance() {
		return languageRegistry.getDefaultDriver();
	}

	public ObjectFactory getObjectFactory() {
		return objectFactory;
	}
}
