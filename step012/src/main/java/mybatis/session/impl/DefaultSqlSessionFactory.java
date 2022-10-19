package mybatis.session.impl;

import java.sql.SQLException;

import mybatis.executor.Executor;
import mybatis.mapping.Environment;
import mybatis.session.Configuration;
import mybatis.session.SqlSession;
import mybatis.session.SqlSessionFactory;
import mybatis.session.TransactionIsolationLevel;
import mybatis.transaction.Transaction;
import mybatis.transaction.TransactionFactory;

/**
 * @Description sqlSession创建工厂，默认实现
 * @Author jiyang.li
 * @Date 2022/9/18 14:38
 **/
public class DefaultSqlSessionFactory implements SqlSessionFactory {
	private final Configuration configuration;

	public DefaultSqlSessionFactory(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public SqlSession openSession() {
		Transaction tx = null;
		try {
			Environment environment = configuration.getEnvironment();
			final TransactionFactory transactionFactory = environment.getTransactionFactory();
			tx = transactionFactory.newTransaction(environment.getDataSource(),
					TransactionIsolationLevel.READ_COMMITTED, false);
			Executor executor = configuration.newExecutor(tx);
			return new DefaultSqlSession(configuration, executor);
		} catch (Exception e) {
			if (tx != null) {
				try {
					tx.close();
				} catch (SQLException exception) {
				}
			}
			throw new RuntimeException("Error open session:" + e);
		}
	}
}
