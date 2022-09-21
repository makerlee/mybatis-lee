package mybatis.mapping;

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

	private BoundSql boundSql;

	// disable
	MappedStatement() {
	}

	public static class Builder {
		private MappedStatement statement = new MappedStatement();

		public Builder(Configuration configuration, String id, SqlCommandType commandType, BoundSql boundSql) {
			statement.configuration = configuration;
			statement.id = id;
			statement.sqlCommandType = commandType;
			statement.boundSql = boundSql;
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

	public String getId() {
		return id;
	}

	public SqlCommandType getSqlCommandType() {
		return sqlCommandType;
	}

	public BoundSql getBoundSql() {
		return boundSql;
	}
}
