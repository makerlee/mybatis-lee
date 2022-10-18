package mybatis.executor.result;

import mybatis.session.ResultContext;

/**
 * @Description ResultContext默认实现
 * @Author jiyang.li
 * @Date 2022/10/17 10:50
 **/
public class DefaultResultContext implements ResultContext {
	private Object resultObject;
	private int count;

	public DefaultResultContext() {
		this.resultObject = null;
		this.count = 0;
	}

	@Override
	public Object getResultObject() {
		return resultObject;
	}

	@Override
	public int getResultCount() {
		return count;
	}

	public void nextResultObject(Object resultObject) {
		count++;
		this.resultObject = resultObject;
	}
}
