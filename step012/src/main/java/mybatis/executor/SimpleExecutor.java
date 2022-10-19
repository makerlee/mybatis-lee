package mybatis.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import mybatis.executor.statement.StatementHandler;
import mybatis.mapping.BoundSql;
import mybatis.mapping.MappedStatement;
import mybatis.session.Configuration;
import mybatis.session.ResultHandler;
import mybatis.session.RowBounds;
import mybatis.transaction.Transaction;

/**
 * @Description 简单执行器
 * @Author jiyang.li
 * @Date 2022/9/23 21:26
 **/
public class SimpleExecutor extends BaseExecutor {

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
    	Statement statement = null;
    	try {
			Configuration configuration = ms.getConfiguration();
			StatementHandler statementHandler = configuration.newStatementHandler(this, ms, parameter, RowBounds.DEFAULT, null, null);
			statement = prepareStatement(statementHandler);
			return statementHandler.update(statement);
		} finally {
    		closeStatement(statement);
		}
    }

	private Statement prepareStatement(StatementHandler handler) throws SQLException {
        Statement stmt;
        Connection connection = transaction.getConnection();
        // 准备语句
        stmt = handler.prepare(connection);
        handler.parameterize(stmt);
        return stmt;
	}

	@Override
    protected <E> List<E> doQuery(MappedStatement ms, Object param, RowBounds rowBounds, ResultHandler resultHandler,
                                  BoundSql boundSql) {
        try {
            Configuration configuration = ms.getConfiguration();
            StatementHandler statementHandler = configuration.newStatementHandler(this, ms, param, rowBounds,
                    resultHandler, boundSql);
            Connection connection = transaction.getConnection();
            Statement statement = statementHandler.prepare(connection);
            statementHandler.parameterize(statement);
            return statementHandler.query(statement, resultHandler);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
