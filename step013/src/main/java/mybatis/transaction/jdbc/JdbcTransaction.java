package mybatis.transaction.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import mybatis.session.TransactionIsolationLevel;
import mybatis.transaction.Transaction;

/**
 * @Description JDBC事务
 * @Author jiyang.li
 * @Date 2022/9/19 21:22
 **/
public class JdbcTransaction implements Transaction {
    protected Connection connection;
    protected DataSource dataSource;
    protected TransactionIsolationLevel isolationLevel = TransactionIsolationLevel.NONE;
    protected boolean autoCommit;

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    public JdbcTransaction(DataSource dataSource, TransactionIsolationLevel isolationLevel, boolean autoCommit) {
        this.dataSource = dataSource;
        this.isolationLevel = isolationLevel;
        this.autoCommit = autoCommit;
    }

    @Override
    public Connection getConnection() throws SQLException {
        connection = dataSource.getConnection();
        connection.setTransactionIsolation(isolationLevel.getLevel());
        connection.setAutoCommit(autoCommit);
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.close();
        }
    }
}
