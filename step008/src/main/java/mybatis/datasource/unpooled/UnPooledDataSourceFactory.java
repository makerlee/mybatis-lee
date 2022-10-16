package mybatis.datasource.unpooled;

import mybatis.datasource.DataSourceFactory;
import mybatis.reflection.MetaObject;
import mybatis.reflection.SystemMetaObject;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @Description 无池化数据源工厂
 * @Author jiyang.li
 * @Date 2022/9/21 15:26
 **/
public class UnPooledDataSourceFactory implements DataSourceFactory {
    protected DataSource dataSource;

    public UnPooledDataSourceFactory() {
        this.dataSource = new UnPooledDataSource();
    }

    @Override
    public void setProperties(Properties properties) {
        MetaObject metaObject = SystemMetaObject.forObject(dataSource);
        for (Object key : properties.keySet()) {
            String propertyName = (String) key;
            if (metaObject.hasSetter(propertyName)){
                String value = (String) properties.get(key);
                Object convertedVal = convertValue(metaObject, propertyName, value);
                metaObject.setValue(propertyName, convertedVal);
            }
        }
    }

    private Object convertValue(MetaObject metaObject, String propertyName, String value) {
        Object converted = value;
        Class<?> setterType = metaObject.getSetterType(propertyName);
        if (setterType == Integer.class || setterType == int.class) {
            converted = Integer.valueOf(value);
        } else if (setterType == Long.class || setterType == long.class) {
            converted = Long.valueOf(value);
        } else if (setterType == Boolean.class || setterType == boolean.class) {
            converted = Boolean.valueOf(value);
        }
        return converted;
    }


    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
