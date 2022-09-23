package mybatis.session;

/**
 * sqlSession创建工厂
 */
public interface SqlSessionFactory {

    /**
     * 打开一个session
     * @return session
     */
    SqlSession openSession();
}
