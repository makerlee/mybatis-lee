package mybatis.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description statement参数处理，resultSet参数处理
 * @Author jiyang.li
 * @Date 2022/10/11 14:46
 **/
public interface TypeHandler<T> {
    /**
     * 设置参数
     * @param ps
     * @param i
     * @param param
     * @param jdbcType
     */
    void setParameter(PreparedStatement ps, int i, T param, JdbcType jdbcType) throws SQLException;


    /**
     * 获取结果
     */
    T getResult(ResultSet resultSet, String colName) throws SQLException;
}
