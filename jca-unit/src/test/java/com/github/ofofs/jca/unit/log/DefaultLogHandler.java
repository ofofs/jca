package com.github.ofofs.jca.unit.log;

import com.github.ofofs.jca.handler.LogHandler;

/**
 * <p> 默认 LogHandler </p>
 *
 * <pre> Created: 2018-06-29 14:25  </pre>
 * <pre> Project: jca  </pre>
 *
 * @author houbinbin
 * @version 1.0
 * @since 1.0
 */
public class DefaultLogHandler implements LogHandler {
    @Override
    public void logBefore(String packageName, String className, String methodName, Object... args) {

    }

    @Override
    public Object logAfter(String packageName, String className, String methodName, long startTime, Object returnValue) {
        return null;
    }
}
