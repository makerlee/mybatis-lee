package mybatis.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Description 参数处理器
 * @Author jiyang.li
 * @Date 2022/10/14 10:01
 **/
public interface ParameterHandler {
	Object getParameterObject();

	void setParameters(PreparedStatement ps) throws SQLException;
}
