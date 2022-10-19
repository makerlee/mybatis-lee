package mybatis.session;

/**
 * @Description 记录限制
 * @Author jiyang.li
 * @Date 2022/10/16 22:28
 **/
public class RowBounds {
    public static final int NO_OFFSET = 0;
    public static final int NO_LIMIT = Integer.MAX_VALUE;
    public static final RowBounds DEFAULT = new RowBounds();

    private int offset;
    private int limit;

    public RowBounds() {
        this.offset = NO_OFFSET;
        this.limit = NO_LIMIT;
    }

    public RowBounds(int offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
}
