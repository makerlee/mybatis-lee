package mybatis.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description Long类型处理器
 * @Author jiyang.li
 * @Date 2022/10/13 11:43
 **/
public class IntTypeHandler extends BaseTypeHandler<Integer> {

    @Override
    protected Integer getNullableResult(ResultSet resultSet, String colName) throws SQLException {
        return resultSet.getInt(colName);
    }

    @Override
    protected void setNullParameter(PreparedStatement ps, int i, Integer param, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, param);
    }
}
