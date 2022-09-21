package mybatis;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description JDBC类型枚举
 * @Author jiyang.li
 * @Date 2022/9/19 20:51
 **/
public enum JdbcType {
    INTEGER(Types.INTEGER),
    FLOAT(Types.FLOAT),
    DOUBLE(Types.DOUBLE),
    DECIMAL(Types.DECIMAL),
    VARCHAR(Types.VARCHAR),
    TIMESTAMP(Types.TIMESTAMP);

    private final int TYPE_CODE;
    private static Map<Integer, JdbcType> codeLookup = new HashMap<>();

    static {
        for (JdbcType jdbcType : codeLookup.values()) {
            codeLookup.put(jdbcType.TYPE_CODE, jdbcType);
        }
    }

    JdbcType(int TYPE_CODE) {
        this.TYPE_CODE = TYPE_CODE;
    }

    public static JdbcType forCode(int code) {
        return codeLookup.get(code);
    }
}
