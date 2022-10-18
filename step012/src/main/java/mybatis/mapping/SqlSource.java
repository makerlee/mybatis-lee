package mybatis.mapping;

/**
 * @Description SQL源码
 * @Author jiyang.li
 * @Date 2022/10/10 13:55
 **/
public interface SqlSource {

    BoundSql getBoundSql(Object paramObject);
}
