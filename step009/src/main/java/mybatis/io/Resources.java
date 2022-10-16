package mybatis.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * @Description 通过类加载器获取input stream
 * @Author jiyang.li
 * @Date 2022/9/19 08:55
 **/
public class Resources {

	public static Reader getResourceAsReader(String resource) throws IOException {
		return new InputStreamReader(getResourceAsStream(resource));
	}

	public static InputStream getResourceAsStream(String resource) throws IOException {
		ClassLoader[] classLoaders = getClassLoaders();
		for (ClassLoader classLoader: classLoaders) {
            InputStream resourceAsStream = classLoader.getResourceAsStream(resource);
            if (null != resourceAsStream) {
                return resourceAsStream;
            }
        }
		throw new IOException("can not find resource:" + resource);
	}

	private static ClassLoader[] getClassLoaders() {
		return new ClassLoader[]{ClassLoader.getSystemClassLoader(), Thread.currentThread().getContextClassLoader()};
	}

	public static Class<?> classForName(String className) throws ClassNotFoundException {
	    return Class.forName(className);
    }
}
