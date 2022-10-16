package mybatis.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mybatis.mapping.ParameterMapping;
import mybatis.mapping.SqlSource;
import mybatis.parsing.GenericTokenParser;
import mybatis.parsing.TokenHandler;
import mybatis.reflection.MetaClass;
import mybatis.reflection.MetaObject;
import mybatis.session.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description SqlSource 构建器
 * @Author jiyang.li
 * @Date 2022/10/10 14:04
 **/
public class SqlSourceBuilder extends BaseBuilder {
	private static Logger logger = LoggerFactory.getLogger(SqlSourceBuilder.class);

	private static final String parameterProperties = "javaType,jdbcType,mode,numericScale,resultMap,typeHandler,jdbcTypeName";

	public SqlSourceBuilder(Configuration configuration) {
		super(configuration);
	}

	public SqlSource parse(String originalSql, Class<?> paramType, Map<String, Object> additionalParams) {
		ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler(configuration, paramType,
				additionalParams);
		GenericTokenParser parser = new GenericTokenParser("#{", "}", tokenHandler);
		String parsedSql = parser.parse(originalSql);
		// 返回静态sql
		return new StaticSqlSource(parsedSql, tokenHandler.getParameterMappings(), configuration);
	}

	private static class ParameterMappingTokenHandler extends BaseBuilder implements TokenHandler {
		private List<ParameterMapping> parameterMappings = new ArrayList<>();
		private Class<?> parameterType;
		private MetaObject metaParameters;

		public ParameterMappingTokenHandler(Configuration configuration, Class<?> parameterType,
				Map<String, Object> additionalParams) {
			super(configuration);
			this.parameterType = parameterType;
			this.metaParameters = configuration.newMetaObject(additionalParams);
		}

		public List<ParameterMapping> getParameterMappings() {
			return parameterMappings;
		}

		@Override
		public String handleToken(String content) {
			parameterMappings.add(buildParameterMapping(content));
			return "?";
		}

		// 构建参数映射
		private ParameterMapping buildParameterMapping(String content) {
			// 先解析参数映射,就是转化成一个 HashMap | #{favouriteSection,jdbcType=VARCHAR}
			Map<String, String> propertyMap = new ParameterExpression(content);
			String property = propertyMap.get("property");
			Class<?> propertyType;
			if (typeHandlerRegistry.hasTypeHandler(parameterType)) {
				propertyType = parameterType;
			} else if (property != null) {
				MetaClass metaClass = MetaClass.forClass(parameterType);
				if (metaClass.hasGetter(property)) {
					propertyType = metaClass.getGetterType(property);
				} else {
					propertyType = Object.class;
				}
			} else {
				propertyType = Object.class;
			}

			logger.info("构建参数映射 property：{} propertyType：{}", property, propertyType);
			ParameterMapping.Builder builder = new ParameterMapping.Builder(configuration, property, propertyType);
			return builder.build();
		}
	}
}
