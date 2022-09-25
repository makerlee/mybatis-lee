package mybatis.transaction.jdbc;

import mybatis.session.TransactionIsolationLevel;
import mybatis.transaction.Transaction;
import mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @Description JdbcTransaction工厂
 * @Author jiyang.li
 * @Date 2022/9/19 21:30
 **/
public class JdbcTransactionFactory implements TransactionFactory {
    @Override
    public Transaction newTransaction(Connection connection) {
        return new JdbcTransaction(connection);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }
}
