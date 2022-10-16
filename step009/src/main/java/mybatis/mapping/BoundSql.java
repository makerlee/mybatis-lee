package mybatis.mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mybatis.reflection.MetaObject;
import mybatis.session.Configuration;

/**
 * @Description 准备就绪的SQL:SELECT * FROM t_user WHERE ID = ?;还有传入参数值map
 * @Author jiyang.li
 * @Date 2022/9/20 09:44
 **/
public class BoundSql {
	private String sql;
	private List<ParameterMapping> parameterMappings;
	private Object paraObject;
	private Map<String, Object> additionalParameters;
	private MetaObject metaParameters;

	public BoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings,
			Object paramObject) {
		this.sql = sql;
		this.parameterMappings = parameterMappings;
		this.paraObject = paramObject;
		this.additionalParameters = new HashMap<>();
		this.metaParameters = configuration.newMetaObject(additionalParameters);
	}

	public String getSql() {
		return sql;
	}

	public boolean hasAdditionalParameter(String name) {
		return additionalParameters.containsKey(name);
	}

	public List<ParameterMapping> getParameterMappings() {
		return parameterMappings;
	}

	public Object getParaObject() {
		return paraObject;
	}

	public void setAdditionalParameters(Map<String, Object> additionalParameters) {
		this.additionalParameters = additionalParameters;
	}

	public Map<String, Object> getAdditionalParameters() {
		return additionalParameters;
	}
}
