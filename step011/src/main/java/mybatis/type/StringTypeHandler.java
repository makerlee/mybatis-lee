package mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Description String类型处理器
 * @Author jiyang.li
 * @Date 2022/10/13 11:48
 **/
public class StringTypeHandler extends BaseTypeHandler<String> {
    @Override
    protected void setNullParameter(PreparedStatement ps, int i, String param, JdbcType jdbcType) throws SQLException {
        ps.setString(i, param);
    }
}
