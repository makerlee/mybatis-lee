package mybatis.session;

/**
 * @Description 结果处理器
 * @Author jiyang.li
 * @Date 2022/9/23 21:04
 **/
public interface ResultHandler {
    /**
     * 处理结果
     */
    void handleResult(ResultContext context);
}
