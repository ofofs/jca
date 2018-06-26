package com.github.ofofs.jca.handler.impl;

import com.github.ofofs.jca.handler.CacheHandler;
import com.github.ofofs.jca.model.CacheInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于内存的缓存
 *
 * @author kangyonggan
 * @since 2018/6/26 0026
 */
public class MemoryCacheHandler implements CacheHandler {

    /**
     * 存储所有缓存
     */
    private volatile static Map<String, CacheInfo> caches = new HashMap();

    @Override
    public Object set(String key, Object returnValue, Long expire) {
        caches.put(key, new CacheInfo(returnValue, expire));
        return returnValue;
    }

    @Override
    public Object get(String key) {
        CacheInfo cacheInfo = caches.get(key);
        if (cacheInfo == null) {
            return null;
        }

        if (cacheInfo.isExpire()) {
            // 删除过期的缓存
            caches.remove(key);
            return null;
        }

        return cacheInfo.getValue();
    }

    @Override
    public void delete(String... keys) {
        for (String key : keys) {
            caches.remove(key);
        }
    }
}
