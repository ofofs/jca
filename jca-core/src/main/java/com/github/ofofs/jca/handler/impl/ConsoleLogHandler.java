package com.github.ofofs.jca.handler.impl;

import com.alibaba.fastjson.JSON;
import com.github.ofofs.jca.handler.LogHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author kangyonggan
 * @since 6/22/18
 */
public class ConsoleLogHandler implements LogHandler {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public void logBefore(String packageName, String methodName, Object... args) {
        log(packageName, methodName, String.format("method args：%s", JSON.toJSONString(args)));
    }

    private void log(String packageName, String methodName, String msg) {
        System.out.println(String.format("[INFO ] %s [%s]<%s> - %s", format.format(new Date()), packageName, methodName, msg));
    }
}
