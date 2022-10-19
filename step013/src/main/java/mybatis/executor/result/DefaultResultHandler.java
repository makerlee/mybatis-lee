package mybatis.executor.result;

import java.util.ArrayList;
import java.util.List;

import mybatis.reflection.factory.ObjectFactory;
import mybatis.session.ResultContext;
import mybatis.session.ResultHandler;

/**
 * @Description 结果处理器默认实现类
 * @Author jiyang.li
 * @Date 2022/10/17 10:57
 **/
public class DefaultResultHandler implements ResultHandler {
    private final List<Object> list;

    public DefaultResultHandler() {
        this.list = new ArrayList<>();
    }

    /**
     * 通过 ObjectFactory 反射工具类，产生特定的 List
     */
    @SuppressWarnings("unchecked")
    public DefaultResultHandler(ObjectFactory objectFactory) {
        this.list = objectFactory.create(List.class);
    }

    @Override
    public void handleResult(ResultContext context) {
        list.add(context.getResultObject());
    }

    public List<Object> getResultList() {
        return list;
    }
}
