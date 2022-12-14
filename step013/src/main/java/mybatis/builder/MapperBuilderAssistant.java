package mybatis.builder;

import java.util.ArrayList;
import java.util.List;

import mybatis.mapping.*;
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
			} else {
				if (id.startsWith(currentNamespace + ".")) {
					return id;
				}
				if (id.contains(".")) {
					throw new RuntimeException("Dots are not allowed in element names, please remove it from " + id);
				}
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
			String[] resultMapNames = resultMap.split(",");
			for (String resultMapName : resultMapNames) {
				resultMaps.add(configuration.getResultMap(resultMapName.trim()));
			}
		}
		/*
		 * 通常使用 resultType 即可满足大部分场景 <select id="queryUserInfoById"
		 * resultType="cn.xxx.mybatis.test.po.User"> 使用 resultType 的情况下，Mybatis
		 * 会自动创建一个 ResultMap，基于属性名称映射列到 JavaBean 的属性上。
		 */
		else if (resultType != null) {
			ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, builder.id() + "-Inline",
					resultType, new ArrayList<>());
			resultMaps.add(resultMapBuilder.build());
		}
		builder.resultMaps(resultMaps);
	}

	public ResultMap addResultMap(String rmId, Class<?> returnType, List<ResultMapping> resultMappings) {
		ResultMap.Builder inlineBuilder = new ResultMap.Builder(configuration, rmId, returnType, resultMappings);
		ResultMap resultMap = inlineBuilder.build();
		configuration.addResultMap(resultMap);
		return resultMap;
	}
}
