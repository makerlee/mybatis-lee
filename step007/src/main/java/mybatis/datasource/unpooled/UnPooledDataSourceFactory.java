package mybatis.datasource.unpooled;

import mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @Description 无池化数据源工厂
 * @Author jiyang.li
 * @Date 2022/9/21 15:26
 **/
public class UnPooledDataSourceFactory implements DataSourceFactory {
    protected Properties properties;

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource getDataSource() {
        UnPooledDataSource unPooledDataSource = new UnPooledDataSource();
        unPooledDataSource.setDriver(properties.getProperty("driver"));
        unPooledDataSource.setUrl(properties.getProperty("url"));
        unPooledDataSource.setUsername(properties.getProperty("username"));
        unPooledDataSource.setPasswd(properties.getProperty("password"));
        return unPooledDataSource;
    }
}
