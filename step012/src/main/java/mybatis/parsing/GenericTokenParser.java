package mybatis.parsing;

/**
 * @Description 普通记号解析器，处理#{}和${}参数
 * @Author jiyang.li
 * @Date 2022/10/10 14:09
 **/
public class GenericTokenParser {
    private final String openToken;
    private final String closeToken;

    private final TokenHandler tokenHandler;

    public GenericTokenParser(String openToken, String closeToken, TokenHandler tokenHandler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.tokenHandler = tokenHandler;
    }

    public String parse(String text) {
        StringBuilder builder = new StringBuilder();
        if (text == null || text.isEmpty()) {
            return builder.toString();
        }

        char[] src = text.toCharArray();
        int offset = 0;
        int start = text.indexOf(openToken, offset);
        while (start > -1) {
            if (start > 0 && src[start - 1] == '\\') {
                builder.append(src, offset, start - offset -1).append(openToken);
                offset = start + openToken.length();
            } else {
                int end = text.indexOf(closeToken, start);
                if (end == -1) {
                    builder.append(src, offset, src.length - offset);
                    offset = src.length;
                } else {
                    builder.append(src, offset, start - offset);
                    offset = start + openToken.length();
                    String content = new String(src, offset, end - offset);
                    // 得到一对大括号里的字符串后，调用handler.handleToken,比如替换变量这种功能
                    builder.append(tokenHandler.handleToken(content));
                    offset = end + closeToken.length();
                }
            }
            start = text.indexOf(openToken, offset);
        }
        if (offset < src.length) {
            builder.append(src, offset, src.length - offset);
        }

        return builder.toString();
    }
}
