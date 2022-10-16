package mybatis.scripting.defaults;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import mybatis.executor.parameter.ParameterHandler;
import mybatis.mapping.BoundSql;
import mybatis.mapping.MappedStatement;
import mybatis.mapping.ParameterMapping;
import mybatis.reflection.MetaObject;
import mybatis.session.Configuration;
import mybatis.type.JdbcType;
import mybatis.type.TypeHandler;
import mybatis.type.TypeHandlerRegistry;

/**
 * @Description 参数处理器默认实现
 * @Author jiyang.li
 * @Date 2022/10/14 10:51
 **/
public class DefaultParameterHandler implements ParameterHandler {
	private Logger logger = LoggerFactory.getLogger(DefaultParameterHandler.class);

	private final TypeHandlerRegistry typeHandlerRegistry;

	private final MappedStatement ms;
	private final Object paramObject;
	private Configuration configuration;
	private BoundSql boundSql;

	public DefaultParameterHandler(MappedStatement ms, Object paramObject, BoundSql boundSql) {
		this.ms = ms;
		this.paramObject = paramObject;
		this.boundSql = boundSql;

		this.configuration = ms.getConfiguration();
		this.typeHandlerRegistry = ms.getConfiguration().getTypeHandlerRegistry();
	}

	@Override
	public Object getParameterObject() {
		return paramObject;
	}

	@Override
	public void setParameters(PreparedStatement ps) throws SQLException {
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		if (null == parameterMappings) {
			return;
		}
		for (int i = 0; i < parameterMappings.size(); i++) {
			ParameterMapping pm = parameterMappings.get(i);
			String propertyName = pm.getProperty();
			Object value;
			if (typeHandlerRegistry.hasTypeHandler(paramObject.getClass())) {
				value = paramObject;
			} else {
				MetaObject metaObject = configuration.newMetaObject(paramObject);
				value = metaObject.getValue(propertyName);
			}
			JdbcType jdbcType = pm.getJdbcType();

			logger.info("根据每个ParameterMapping中的TypeHandler设置对应的参数信息 value：{}", JSON.toJSONString(value));
			// 设置参数
			TypeHandler typeHandler = pm.getTypeHandler();
			typeHandler.setParameter(ps, i + 1, value, jdbcType);
		}
	}
}
