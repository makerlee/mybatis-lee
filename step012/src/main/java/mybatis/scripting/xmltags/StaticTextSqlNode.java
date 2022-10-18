package mybatis.scripting.xmltags;

/**
 * @Description 静态文本SQL
 * @Author jiyang.li
 * @Date 2022/10/12 21:05
 **/
public class StaticTextSqlNode implements SqlNode {
    private String text;

    public StaticTextSqlNode(String text) {
        this.text = text;
    }

    @Override
    public boolean apply(DynamicContext context) {
        // 讲文本加入context
        context.appendSql(text);
        return true;
    }
}
