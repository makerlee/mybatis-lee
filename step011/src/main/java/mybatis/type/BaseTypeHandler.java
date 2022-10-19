package mybatis.type;

import mybatis.session.Configuration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Description 类型处理器基类
 * @Author jiyang.li
 * @Date 2022/10/13 11:00
 **/
public abstract class BaseTypeHandler<T> implements TypeHandler<T> {
    protected Configuration configuration;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void setParameter(PreparedStatement ps, int i, T param, JdbcType jdbcType) throws SQLException {
        setNullParameter(ps, i, param, jdbcType);
    }

    @Override
    public T getResult(ResultSet resultSet, String colName) throws SQLException {
        return getNullableResult(resultSet, colName);
    }

    protected abstract T getNullableResult(ResultSet resultSet, String colName) throws SQLException;

    protected abstract void setNullParameter(PreparedStatement ps, int i, T param, JdbcType jdbcType) throws SQLException;
}
