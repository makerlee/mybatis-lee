package mybatis;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @Description 类型别名注册机
 * @Author jiyang.li
 * @Date 2022/9/19 20:58
 **/
public class TypeAliasRegistry {
    private final Map<String, Class<?>> TYPE_ALIAS = new HashMap<>();

    public TypeAliasRegistry() {
        TYPE_ALIAS.put("byte", Byte.class);
        TYPE_ALIAS.put("short", Short.class);
        TYPE_ALIAS.put("int", Integer.class);
        TYPE_ALIAS.put("integer", Integer.class);
        TYPE_ALIAS.put("boolean", Boolean.class);
        TYPE_ALIAS.put("long", Long.class);
        TYPE_ALIAS.put("float", Float.class);
        TYPE_ALIAS.put("double", Double.class);
    }

    public void registerAlia(String alia, Class<?> type) {
        String key = alia.toLowerCase(Locale.ENGLISH);
        TYPE_ALIAS.put(key, type);
    }

    public <T> Class<T> resolveAlia(String alia) {
        String key = alia.toLowerCase(Locale.ENGLISH);
        return (Class<T>) TYPE_ALIAS.get(key);
    }
}
