package mybatis.mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mybatis.session.Configuration;

/**
 * @Description https://mybatis.org/mybatis-3/zh/sqlmap-xml.html#Result_Maps
 * @Author jiyang.li
 * @Date 2022/10/16 21:28
 **/
public class ResultMap {
	private String id;
	private Class<?> type;
	private List<ResultMapping> resultMappings;
	private Set<String> mappedColumns;

	private ResultMap() {
	}

	public static class Builder {
		private ResultMap resultMap = new ResultMap();

		public Builder(Configuration configuration, String id, Class<?> type, List<ResultMapping> resultMappings) {
			resultMap.id = id;
			resultMap.type = type;
			resultMap.resultMappings = resultMappings;
		}

		public ResultMap build() {
			resultMap.mappedColumns = new HashSet<>();
			return resultMap;
		}
	}

	public String getId() {
		return id;
	}

	public Class<?> getType() {
		return type;
	}

	public List<ResultMapping> getResultMappings() {
		return resultMappings;
	}

	public Set<String> getMappedColumns() {
		return mappedColumns;
	}
}
