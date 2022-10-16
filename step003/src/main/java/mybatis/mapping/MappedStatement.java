package mybatis.mapping;

import java.util.Map;

import mybatis.session.Configuration;

/**
 * @Description 映射语句类
 * @Author jiyang.li
 * @Date 2022/9/18 15:21
 **/
public class MappedStatement {
	private Configuration configuration;
	private String id;
	private SqlCommandType sqlCommandType;

	private String parameterType;
	private String resultType;
	private String sql;
	private Map<Integer, String> parameter;

	// disable
	MappedStatement() {
	}

	public static class Builder {
		private MappedStatement statement = new MappedStatement();

		public Builder(Configuration configuration, String id, SqlCommandType commandType, String parameterType,
				String resultType, String sql, Map<Integer, String> parameter) {
            statement.configuration = configuration;
            statement.id = id;
            statement.sqlCommandType = commandType;
            statement.parameterType = parameterType;
            statement.resultType = resultType;
            statement.sql = sql;
            statement.parameter = parameter;
		}

		public MappedStatement build() {
		    assert statement.configuration != null;
		    assert statement.id != null;
			return statement;
		}
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SqlCommandType getSqlCommandType() {
		return sqlCommandType;
	}

	public void setSqlCommandType(SqlCommandType sqlCommandType) {
		this.sqlCommandType = sqlCommandType;
	}

	public String getParameterType() {
		return parameterType;
	}

	public void setParameterType(String parameterType) {
		this.parameterType = parameterType;
	}

	public String getResultType() {
		return resultType;
	}

	public void setResultType(String resultType) {
		this.resultType = resultType;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Map<Integer, String> getParameter() {
		return parameter;
	}

	public void setParameter(Map<Integer, String> parameter) {
		this.parameter = parameter;
	}
}
