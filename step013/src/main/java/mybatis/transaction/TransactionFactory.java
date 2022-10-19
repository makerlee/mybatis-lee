package mybatis.transaction;

import java.sql.Connection;

import javax.sql.DataSource;

import mybatis.session.TransactionIsolationLevel;

/**
 * 事务工厂
 */
public interface TransactionFactory {
    /**
     * 根据connection 创建事务
     * @param connection database connection
     * @return Transaction
     */
    Transaction newTransaction(Connection connection);

    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit);
}
