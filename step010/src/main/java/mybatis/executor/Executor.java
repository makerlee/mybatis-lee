package mybatis.executor;

import java.sql.SQLException;
import java.util.List;

import mybatis.mapping.BoundSql;
import mybatis.mapping.MappedStatement;
import mybatis.session.ResultHandler;
import mybatis.transaction.Transaction;

/**
 * @Description 执行器
 * @Author jiyang.li
 * @Date 2022/9/23 20:56
 **/
public interface Executor {
	ResultHandler NO_RESULT_HANDLER = null;

	<E> List<E> query(MappedStatement mappedStatement, Object param, ResultHandler resultHandler, BoundSql boundSql);

	Transaction getTransaction();

	void commit(boolean required) throws SQLException;

	void rollback(boolean required) throws SQLException;

	void close(boolean force);
}
