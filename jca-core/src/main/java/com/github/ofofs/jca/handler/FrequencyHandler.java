package com.github.ofofs.jca.handler;

/**
 * @author kangyonggan
 * @since 6/27/18
 */
public interface FrequencyHandler {

    /**
     * 判断是两次调用间隔是否小于指定的间隔时间
     *
     * @param key      键
     * @param interval 间隔时间
     * @return 超过间隔时间次数返回true，否则返回false
     */
    boolean isViolate(String key, long interval);

}
