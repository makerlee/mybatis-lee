package mybatis.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import mybatis.session.ResultHandler;

/**
 * @Description 语句处理器
 * @Author jiyang.li
 * @Date 2022/9/23 21:32
 **/
public interface StatementHandler {
	/** 准备语句 */
	Statement prepare(Connection connection) throws SQLException;

	/** 参数化 */
	void parameterize(Statement statement) throws SQLException;

	/** 执行查询 */
	<E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;

	/** 执行更新 */
	int update(Statement statement) throws SQLException;
}
