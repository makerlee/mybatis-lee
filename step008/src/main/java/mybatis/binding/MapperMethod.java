package mybatis.binding;

import java.lang.reflect.Method;

import mybatis.mapping.MappedStatement;
import mybatis.mapping.SqlCommandType;
import mybatis.session.Configuration;
import mybatis.session.SqlSession;

/**
 * @Description TODO
 * @Author jiyang.li
 * @Date 2022/9/19 11:02
 **/
public class MapperMethod {
    private final SqlCommand sqlCommand;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration) {
        this.sqlCommand = new SqlCommand(configuration, mapperInterface, method);
    }

    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result = null;
        switch (sqlCommand.getType()) {
            case DELETE:
                break;
            case INSERT:
                break;
            case UPDATE:
                break;
            case SELECT:
                result = sqlSession.selectOne(sqlCommand.getName(), args);
                break;
            default:
                throw new RuntimeException("unknown execution method for " + sqlCommand.getName());
        }
        return result;
    }

    public static class SqlCommand {
        private final String name;
        private final SqlCommandType type;

        public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
            String statementName = mapperInterface.getName() + "." + method.getName();
            MappedStatement statement = configuration.getStatement(statementName);
            name = statement.getId();
            type = statement.getSqlCommandType();
        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }
    }
}
