package mybatis.datasource.pooled;

import mybatis.datasource.unpooled.UnPooledDataSourceFactory;

import javax.sql.DataSource;

/**
 * @Description 有连接池的数据源工厂
 * @Author jiyang.li
 * @Date 2022/9/21 15:25
 **/
public class PooledDataSourceFactory extends UnPooledDataSourceFactory {
    @Override
    public DataSource getDataSource() {
        PooledDataSource dataSource = new PooledDataSource();
        dataSource.setDriver(properties.getProperty("driver"));
        dataSource.setUrl(properties.getProperty("url"));
        dataSource.setUsername(properties.getProperty("username"));
        dataSource.setPassword(properties.getProperty("password"));
        return dataSource;
    }
}
