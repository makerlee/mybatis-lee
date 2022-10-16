package mybatis.reflection.property;

import java.util.Iterator;

/**
 * @Description 属性分解标记
 * @Author jiyang.li
 * @Date 2022/9/26 09:48
 **/
public class PropertyTokenizer implements Iterable<PropertyTokenizer>, Iterator<PropertyTokenizer> {
	// class[0].student.grade
	// class
	private String name;
	// class[0]
	private String indexName;
	// 0
	private String index;
	// student.grade
	private String children;

	public PropertyTokenizer(String fullName) {
		int delim = fullName.indexOf(".");
		if (delim > -1) {
			name = fullName.substring(0, delim);
			children = fullName.substring(delim + 1);
		} else {
			name = fullName;
			children = null;
		}
		indexName = name;
		delim = name.indexOf('[');
		if (delim > -1) {
			index = name.substring(delim + 1, name.length() - 1);
			name = name.substring(0, delim);
		}
	}

	public String getName() {
		return name;
	}

	public String getIndexName() {
		return indexName;
	}

	public String getIndex() {
		return index;
	}

	public String getChildren() {
		return children;
	}

	@Override
	public Iterator<PropertyTokenizer> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return children != null;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove operation is not supported.");
	}

	@Override
	public PropertyTokenizer next() {
		return new PropertyTokenizer(children);
	}
}
