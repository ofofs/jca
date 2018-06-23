package com.github.ofofs.jca.handler.impl;

import com.alibaba.fastjson.JSON;
import com.github.ofofs.jca.handler.LogHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author kangyonggan
 * @since 6/22/18
 */
public class ConsoleLogHandler implements LogHandler {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void logBefore(String packageName, String className, String methodName, Object... args) {
        String date = LocalDateTime.now().format(FORMAT);
        String msg = String.format("[INFO ] %s [%s.%s]<%s> - method args：%s", date, packageName, className, methodName, JSON.toJSONString(args));
        System.out.println(msg);
    }

    @Override
    public Object logAfter(String packageName, String className, String methodName, long startTime, Object returnValue) {
        String date = LocalDateTime.now().format(FORMAT);
        String msg = String.format("[INFO ] %s [%s.%s]<%s> - method return：%s", date, packageName, className, methodName, JSON.toJSONString(returnValue));
        System.out.println(msg);
        msg = String.format("[INFO ] %s [%s.%s]<%s> - method used time：%dms", date, packageName, className, methodName, System.currentTimeMillis() - startTime);
        System.out.println(msg);

        return returnValue;
    }

}
