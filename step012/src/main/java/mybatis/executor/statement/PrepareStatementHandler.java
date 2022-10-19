package mybatis.executor.statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import mybatis.executor.Executor;
import mybatis.mapping.BoundSql;
import mybatis.mapping.MappedStatement;
import mybatis.session.ResultHandler;
import mybatis.session.RowBounds;

/**
 * @Description 预处理语句处理器(prepareStatement)
 * @Author jiyang.li
 * @Date 2022/9/23 22:09
 **/
public class PrepareStatementHandler extends BaseStatementHandler {

	public PrepareStatementHandler(Executor executor, MappedStatement mappedStatement, Object paramObject,
			RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
		super(executor, mappedStatement, paramObject, rowBounds, resultHandler, boundSql);
	}

	@Override
	protected Statement instantiateStatement(Connection connection) throws SQLException {
		String sql = boundSql.getSql();
		return connection.prepareStatement(sql);
	}

	@Override
	public void parameterize(Statement statement) throws SQLException {
		// PreparedStatement ps = (PreparedStatement) statement;
		// ps.setLong(1, Long.parseLong(((Object[]) paramObject)[0].toString()));
		parameterHandler.setParameters((PreparedStatement) statement);
	}

	@Override
	public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
		PreparedStatement ps = (PreparedStatement) statement;
		ps.execute();

		return resultSetHandler.handleResultSet(ps);
	}

	@Override
	public int update(Statement statement) throws SQLException {
		PreparedStatement ps = (PreparedStatement) statement;
		ps.execute();
		return ps.getUpdateCount();
	}
}
