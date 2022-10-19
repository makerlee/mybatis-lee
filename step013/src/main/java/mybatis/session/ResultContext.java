package mybatis.session;

/**
 * @Description 执行结果context
 * @Author jiyang.li
 * @Date 2022/10/16 22:32
 **/
public interface ResultContext {
    /**
     * 获取结果
     */
    Object getResultObject();

    /**
     * 获取记录数
     */
    int getResultCount();
}
