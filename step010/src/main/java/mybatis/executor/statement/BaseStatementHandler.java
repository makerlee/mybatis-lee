package mybatis.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import mybatis.executor.Executor;
import mybatis.executor.parameter.ParameterHandler;
import mybatis.executor.resultset.ResultSetHandler;
import mybatis.mapping.BoundSql;
import mybatis.mapping.MappedStatement;
import mybatis.session.Configuration;
import mybatis.session.ResultHandler;

/**
 * @Description 基类
 * @Author jiyang.li
 * @Date 2022/9/23 21:37
 **/
public abstract class BaseStatementHandler implements StatementHandler {
	protected final Configuration configuration;
	protected final Executor executor;
	protected final MappedStatement mappedStatement;

	protected final Object paramObject;
	protected final ResultSetHandler resultSetHandler;
	protected final ParameterHandler parameterHandler;

	protected final BoundSql boundSql;

	public BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object paramObject,
			ResultHandler resultHandler, BoundSql boundSql) {
		this.configuration = mappedStatement.getConfiguration();
		this.executor = executor;
		this.mappedStatement = mappedStatement;
		this.boundSql = boundSql;

		this.paramObject = paramObject;
		this.parameterHandler = configuration.newParameterHandler(mappedStatement, paramObject, boundSql);
		this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, boundSql);
	}

	@Override
	public Statement prepare(Connection connection) throws SQLException {
		Statement statement = instantiateStatement(connection);

		// todo 参数配置 可以抽取出来
		statement.setQueryTimeout(500);
		statement.setFetchSize(1000);
		return statement;
	}

	protected abstract Statement instantiateStatement(Connection connection) throws SQLException;
}
