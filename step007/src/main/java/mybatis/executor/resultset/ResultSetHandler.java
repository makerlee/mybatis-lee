package mybatis.executor.resultset;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @Description 结果集处理器
 * @Author jiyang.li
 * @Date 2022/9/23 21:41
 **/
public interface ResultSetHandler {
    <E> List<E> handleResultSet(Statement statement) throws SQLException;
}
