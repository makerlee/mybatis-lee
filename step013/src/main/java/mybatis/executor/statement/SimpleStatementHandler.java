package mybatis.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import mybatis.executor.Executor;
import mybatis.mapping.BoundSql;
import mybatis.mapping.MappedStatement;
import mybatis.session.ResultHandler;
import mybatis.session.RowBounds;

/**
 * @Description 简单语句处理器(statement)
 * @Author jiyang.li
 * @Date 2022/9/23 21:59
 **/
public class SimpleStatementHandler extends BaseStatementHandler {

	public SimpleStatementHandler(Executor executor, MappedStatement mappedStatement, Object paramObject,
			RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
		super(executor, mappedStatement, paramObject, rowBounds, resultHandler, boundSql);
	}

	@Override
	protected Statement instantiateStatement(Connection connection) throws SQLException {
		return connection.createStatement();
	}

	@Override
	public void parameterize(Statement statement) throws SQLException {
		// no implement yet
	}

	@Override
	public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
		String sql = boundSql.getSql();
		statement.execute(sql);
		return resultSetHandler.handleResultSet(statement);
	}

	@Override
	public int update(Statement statement) throws SQLException {
		String sql = boundSql.getSql();
		statement.execute(sql);
		return statement.getUpdateCount();
	}
}
