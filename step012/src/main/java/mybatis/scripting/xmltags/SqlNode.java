package mybatis.scripting.xmltags;

/**
 * @Description SQL节点
 * @Author jiyang.li
 * @Date 2022/10/12 14:46
 **/
public interface SqlNode {
    boolean apply(DynamicContext context);
}
