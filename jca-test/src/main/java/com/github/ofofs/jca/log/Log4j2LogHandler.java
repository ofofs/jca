package com.github.ofofs.jca.log;

import com.github.ofofs.jca.annotation.Handler;
import com.github.ofofs.jca.handler.LogHandler;

/**
 * @author kangyonggan
 * @since 6/23/18
 */
@Handler(Handler.Type.LOG)
public class Log4j2LogHandler implements LogHandler {

    @Override
    public void logBefore(String packageName, String className, String methodName, Object... args) {
        System.out.println("log4j2 before>>>>>>" + methodName);
    }

    @Override
    public Object logAfter(String packageName, String className, String methodName, long startTime, Object returnValue) {
        System.out.println("log4j2 after>>>>>>" + methodName);
        return returnValue;
    }
}
