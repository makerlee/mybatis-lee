package mybatis.reflection.Invoker;

import java.lang.reflect.Field;

/**
 * @Description set方法执行器
 * @Author jiyang.li
 * @Date 2022/9/27 15:11
 **/
public class SetFiledInvoker implements Invoker {
    private Field field;

    public SetFiledInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        field.set(target, args);
        return null;
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
