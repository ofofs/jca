package com.github.ofofs.jca.handler.impl;

import com.github.ofofs.jca.handler.FrequencyHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kangyonggan
 * @since 6/29/18
 */
public class MemoryFrequencyHandler implements FrequencyHandler {

    private static Map<String, Long> map = new HashMap();

    @Override
    public synchronized boolean isViolate(String key, long interval) {
        Long lastTime = map.getOrDefault(key, 0L);
        Long currentTime = System.currentTimeMillis();

        if (interval > currentTime - lastTime) {
            return true;
        }

        map.put(key, currentTime);
        return false;
    }
}
