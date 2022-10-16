package mybatis.executor;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import mybatis.executor.statement.StatementHandler;
import mybatis.mapping.BoundSql;
import mybatis.mapping.MappedStatement;
import mybatis.session.Configuration;
import mybatis.session.ResultHandler;
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
	protected <E> List<E> doQuery(MappedStatement ms, Object param, ResultHandler resultHandler, BoundSql boundSql) {
		try {
			Configuration configuration = ms.getConfiguration();
			StatementHandler statementHandler = configuration.newStatementHandler(this, ms, param, resultHandler,
					boundSql);
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
