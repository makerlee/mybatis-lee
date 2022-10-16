package mybatis.session;

import java.util.HashMap;
import java.util.Map;

import mybatis.binding.MapperRegistry;
import mybatis.mapping.MappedStatement;

/**
 * @Description 贯穿整个mybatis生命周期的配置
 * @Author jiyang.li
 * @Date 2022/9/18 15:18
 **/
public class Configuration {
	// 注入configuration
	protected MapperRegistry mapperRegistry = new MapperRegistry(this);

	// 映射语句:key是接口方法的全路径名
	private final Map<String, MappedStatement> mappedStatements = new HashMap<>();

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
}
