package com.github.ofofs.jca.cache;

import com.github.ofofs.jca.annotation.Handler;
import com.github.ofofs.jca.handler.CacheHandler;

/**
 * @author kangyonggan
 * @since 8/30/18
 */
@Handler(Handler.Type.CACHE)
public class RedisCacheHandler implements CacheHandler {
    @Override
    public Object set(String key, Object returnValue, Long expire) {
        return null;
    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public void delete(String... keys) {

    }
}
