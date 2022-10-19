package mybatis.builder;

import java.util.ArrayList;
import java.util.List;

import mybatis.mapping.MappedStatement;
import mybatis.mapping.ResultMap;
import mybatis.mapping.SqlCommandType;
import mybatis.mapping.SqlSource;
import mybatis.scripting.LanguageDriver;
import mybatis.session.Configuration;

/**
 * @Description MapperBuilder辅助类
 * @Author jiyang.li
 * @Date 2022/10/16 22:40
 **/
public class MapperBuilderAssistant extends BaseBuilder {
	private String currentNamespace;
	private String resource;

	public MapperBuilderAssistant(Configuration configuration, String resource) {
		super(configuration);
		this.resource = resource;
	}

	public String getCurrentNamespace() {
		return currentNamespace;
	}

	public void setCurrentNamespace(String currentNamespace) {
		this.currentNamespace = currentNamespace;
	}

	public String applyCurrentNamespace(String id, boolean isReference) {
		if (id == null) {
			return null;
		}
		if (isReference) {
			if (id.contains(".")) {
				return id;
			}
		}
		return currentNamespace + "." + id;
	}

	public MappedStatement addMappedStatement(String id, SqlSource sqlSource, SqlCommandType sqlCommandType,
			Class<?> parameterType, String resultMap, Class<?> resultType, LanguageDriver languageDriver) {
		// 给id加上namespace前缀
		id = applyCurrentNamespace(id, false);
		MappedStatement.Builder builder = new MappedStatement.Builder(configuration, id, sqlCommandType, sqlSource,
				resultType);
		// 结果映射
		setStatementResultMap(resultMap, resultType, builder);

		MappedStatement mappedStatement = builder.build();
		configuration.addMappedStatement(mappedStatement);
		return mappedStatement;
	}

	// <select id="selectUsers" resultType="map">
	// select id, username, hashedPassword from some_table where id = #{id}
	// </select>
	private void setStatementResultMap(String resultMap, Class<?> resultType, MappedStatement.Builder builder) {
		List<ResultMap> resultMaps = new ArrayList<>();
		resultMap = applyCurrentNamespace(resultMap, true);
		if (resultMap != null) {

		} else if (resultType != null) {
			ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, builder.id() + "-Inline",
					resultType, new ArrayList<>());
			resultMaps.add(resultMapBuilder.build());
		}
		builder.resultMaps(resultMaps);
	}

}
