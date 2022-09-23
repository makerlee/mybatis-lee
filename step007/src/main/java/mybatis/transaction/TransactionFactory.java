package mybatis.transaction;

import mybatis.session.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;

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
