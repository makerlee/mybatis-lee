package mybatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @Description 数据源工厂
 * @Author jiyang.li
 * @Date 2022/9/20 14:50
 **/
public interface DataSourceFactory {
    void setProperties(Properties properties);

    DataSource getDataSource();
}
