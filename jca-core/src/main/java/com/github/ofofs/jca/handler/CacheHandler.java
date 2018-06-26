package com.github.ofofs.jca.handler;

/**
 * @author kangyonggan
 * @since 2018/6/26 0026
 */
public interface CacheHandler {

    /**
     * 保存缓存
     *
     * @param key         缓存的键
     * @param returnValue 方法的返回值
     * @param expire      过期时间
     * @return 返回方法的返回值
     */
    Object set(String key, Object returnValue, Long expire);

    /**
     * 获取缓存
     *
     * @param key 缓存的键
     * @return 返回缓存的值
     */
    Object get(String key);

    /**
     * 删除缓存
     *
     * @param keys 缓存的键
     */
    void delete(String... keys);

}
