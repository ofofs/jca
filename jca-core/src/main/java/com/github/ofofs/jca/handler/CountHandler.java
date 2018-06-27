package com.github.ofofs.jca.handler;

/**
 * @author kangyonggan
 * @since 6/27/18
 */
public interface CountHandler {

    /**
     * 判断是否在指定时间内超多最大调用次数
     *
     * @param key    键
     * @param during 时间段
     * @param count  最大次数
     * @return 超过最大调用次数返回true，否则返回false
     */
    boolean isViolate(String key, long during, int count);

}
