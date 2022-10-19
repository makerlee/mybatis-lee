package mybatis.mapping;

import mybatis.scripting.LanguageDriver;
import mybatis.session.Configuration;

import java.util.List;

/**
 * @Description 映射语句类
 * @Author jiyang.li
 * @Date 2022/9/18 15:21
 **/
public class MappedStatement {
	private Configuration configuration;
	private String id;
	private SqlCommandType sqlCommandType;
	private SqlSource sqlSource;
	Class<?> resultType;
	private LanguageDriver lang;
	private List<ResultMap> resultMaps;

	// disable
	MappedStatement() {
	}

	public List<ResultMap> getResultMaps() {
		return resultMaps;
	}

	public static class Builder {
		private MappedStatement statement = new MappedStatement();

		public Builder(Configuration configuration, String id, SqlCommandType commandType, SqlSource sqlSource,
				Class<?> resultType) {
			statement.configuration = configuration;
			statement.id = id;
			statement.sqlCommandType = commandType;
			statement.sqlSource = sqlSource;
			statement.resultType = resultType;
			statement.lang = configuration.getDefaultScriptingLangInstance();
		}

		public MappedStatement build() {
			assert statement.configuration != null;
			assert statement.id != null;
			return statement;
		}

		public String id() {
			return statement.getId();
		}

		public Builder resultMaps(List<ResultMap> resultMaps) {
			statement.resultMaps = resultMaps;
			return this;
		}
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public String getId() {
		return id;
	}

	public SqlCommandType getSqlCommandType() {
		return sqlCommandType;
	}

	public SqlSource getSqlSource() {
		return sqlSource;
	}

	public Class<?> getResultType() {
		return resultType;
	}

	public LanguageDriver getLang() {
		return lang;
	}
}
