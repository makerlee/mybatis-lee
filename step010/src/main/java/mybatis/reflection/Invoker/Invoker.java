package mybatis.reflection.Invoker;

/**
 * @Description 调用者
 * @Author jiyang.li
 * @Date 2022/9/27 14:56
 **/
public interface Invoker {
    Object invoke(Object target, Object[] args) throws Exception;

    Class<?> getType();
}
