package mybatis.scripting.xmltags;

import java.util.List;

/**
 * @Description 混合SQL节点
 * @Author jiyang.li
 * @Date 2022/10/12 22:00
 **/
public class MixedSqlNode implements SqlNode {
	// 组合模式
    private List<SqlNode> contents;

    public MixedSqlNode(List<SqlNode> contents) {
        this.contents = contents;
    }

    @Override
	public boolean apply(DynamicContext context) {
        // 一次调用
        contents.forEach(node -> node.apply(context));
		return true;
	}
}
