package mybatis.scripting.xmltags;

import java.util.HashMap;
import java.util.Map;

import mybatis.reflection.MetaObject;
import mybatis.session.Configuration;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

/**
 * @Description 动态上下文
 * @Author jiyang.li
 * @Date 2022/10/12 14:47
 **/
public class DynamicContext {
	private static final String PARAM_OBJECT_KEY = "_parameter";
	private static final String DATABASE_ID_KEY = "_databaseId";

	static {
		// 定义属性->getter方法映射，ContextMap到ContextAccessor的映射，注册到ognl运行时
		// 参考http://commons.apache.org/proper/commons-ognl/developer-guide.html
		OgnlRuntime.setPropertyAccessor(ContextMap.class, new ContextAccessor());
		// 将传入的参数对象统一封装为ContextMap对象（继承了HashMap对象），
		// 然后Ognl运行时环境在动态计算sql语句时，
		// 会按照ContextAccessor中描述的Map接口的方式来访问和读取ContextMap对象，获取计算过程中需要的参数。
		// ContextMap对象内部可能封装了一个普通的POJO对象，也可以是直接传递的Map对象，当然从外部是看不出来的，因为都是使用Map的接口来读取数据。
	}

	private final ContextMap bindings;
	private final StringBuilder sqlBuilder = new StringBuilder();
	private int uniqueNumber;

	public DynamicContext(Configuration configuration, Object parameterObject) {
		if (parameterObject != null && !(parameterObject instanceof Map)) {
			MetaObject metaObject = configuration.newMetaObject(parameterObject);
			bindings = new ContextMap(metaObject);
		} else {
			bindings = new ContextMap(null);
		}
		bindings.put(PARAM_OBJECT_KEY, parameterObject);
		bindings.put(DATABASE_ID_KEY, configuration.getDatabaseId());
	}

	public Map<String, Object> getBindings() {
		return bindings;
	}

	public void bind(String name, Object value) {
		bindings.put(name, value);
	}

	public void appendSql(String sql) {
		sqlBuilder.append(sql);
		sqlBuilder.append(" ");
	}

	public String getSql() {
		return sqlBuilder.toString().trim();
	}

	public int getUniqueNumber() {
		return uniqueNumber++;
	}

	static class ContextMap extends HashMap<String, Object> {
		private MetaObject parameterMetaObject;

		public ContextMap(MetaObject metaObject) {
			this.parameterMetaObject = metaObject;
		}

		@Override
		public Object get(Object key) {
			String strKey = (String) key;
			// 先去map找
			if (super.containsKey(strKey)) {
				return super.get(strKey);
			}
			// map里没有,在用ognl表达式
			// 如person[0].birthdate.year
			if (parameterMetaObject != null) {
				return parameterMetaObject.getValue(strKey);
			}
			return null;
		}
	}

	static class ContextAccessor implements PropertyAccessor {

		@Override
		public Object getProperty(Map context, Object target, Object name) throws OgnlException {
			Map map = (Map) target;

			Object result = map.get(name);
			if (result != null) {
				return result;
			}

			Object parameterObject = map.get(PARAM_OBJECT_KEY);
			if (parameterObject instanceof Map) {
				return ((Map) parameterObject).get(name);
			}
			return null;
		}

		@Override
		public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
			Map<Object, Object> map = (Map<Object, Object>) target;
			map.put(name, value);
		}

		@Override
		public String getSourceAccessor(OgnlContext ognlContext, Object o, Object o1) {
			return null;
		}

		@Override
		public String getSourceSetter(OgnlContext ognlContext, Object o, Object o1) {
			return null;
		}
	}

}
