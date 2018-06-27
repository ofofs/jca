package com.github.ofofs.jca.util;

import com.github.ofofs.jca.annotation.Handler;
import com.github.ofofs.jca.constants.CoreConstants;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author kangyonggan
 * @since 6/26/18
 */
public final class PropertiesUtil {

    /**
     * 配置
     */
    private static Properties props;

    private PropertiesUtil() {

    }

    /**
     * 加载配置
     */
    static {
        props = new Properties();
        try {
            InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(CoreConstants.PROPERTIES_NAME);
            props.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断某类型的注解是否可用
     *
     * @param type 注解类型
     * @return 如果可用返回true，缺省可用
     */
    public static boolean isEnable(Handler.Type type) {
        return Boolean.parseBoolean(props.getProperty(type.name().toLowerCase() + ".enable", "true"));
    }

    /**
     * 根据键获取值
     *
     * @param key 键
     * @return 返回对应的值
     */
    public static String getProperty(String key) {
        return props.getProperty(key, CoreConstants.EMPTY);
    }

    /**
     * 根据键获取值
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 返回对应的值
     */
    public static String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
}
