package mybatis.reflection.Invoker;

import java.lang.reflect.Method;

/**
 * @Description 方法执行器
 * @Author jiyang.li
 * @Date 2022/9/27 15:13
 **/
public class MethodInvoker implements Invoker {
    private Class<?> type;
    private Method method;

    public MethodInvoker(Method method) {
        this.method = method;
        if (method.getParameterTypes().length == 1) {
            type = method.getParameterTypes()[0];
        } else {
            type = method.getReturnType();
        }
    }

    @Override
	public Object invoke(Object target, Object[] args) throws Exception {
		return method.invoke(target, args);
	}

	@Override
	public Class<?> getType() {
		return type;
	}
}
