package mybatis.builder;

import mybatis.session.Configuration;

/**
 * @Description 建造者
 * @Author jiyang.li
 * @Date 2022/9/19 09:13
 **/
public class BaseBuilder {
    protected final Configuration configuration;

    public BaseBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public Configuration getConfiguration() {
        return configuration;
    }
}
