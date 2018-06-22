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

}
