package mybatis.datasource;

import java.util.Properties;

import javax.sql.DataSource;

/**
 * @Description 数据源工厂
 * @Author jiyang.li
 * @Date 2022/9/20 14:50
 **/
public interface DataSourceFactory {
    void setProperties(Properties properties);

    DataSource getDataSource();
}
