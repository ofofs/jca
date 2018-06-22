package com.github.ofofs.jca.handler;

/**
 * @author kangyonggan
 * @since 6/22/18
 */
public interface LogHandler {

    /**
     * 打印方法入参
     *
     * @param packageName 包名
     * @param className   类名
     * @param methodName  方法名
     * @param args        方法的入参
     */
    void logBefore(String packageName, String className, String methodName, Object... args);

}
