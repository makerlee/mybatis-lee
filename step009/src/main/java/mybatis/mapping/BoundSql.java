package mybatis.mapping;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description 准备就绪的SQL:SELECT * FROM t_user WHERE ID = ?;还有传入参数值map
 * @Author jiyang.li
 * @Date 2022/9/20 09:44
 **/
public class BoundSql {
    private String sql;
    private Map<Integer, String> paramMapping = new HashMap<>();
    private String paramType;
    private String resultType;

    public BoundSql(String sql, Map<Integer, String> paramMapping, String paramType, String resultType) {
        this.sql = sql;
        this.paramMapping = paramMapping;
        this.paramType = paramType;
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public Map<Integer, String> getParamMapping() {
        return paramMapping;
    }

    public String getParamType() {
        return paramType;
    }

    public String getResultType() {
        return resultType;
    }
}
