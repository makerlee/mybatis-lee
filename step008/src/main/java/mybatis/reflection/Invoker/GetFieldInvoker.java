package mybatis.reflection.Invoker;

import java.lang.reflect.Field;

/**
 * @Description getter调用者
 * @Author jiyang.li
 * @Date 2022/9/27 14:58
 **/
public class GetFieldInvoker implements Invoker{
    private Field field;

    public GetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        return field.get(target);
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
