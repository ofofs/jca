package com.github.ofofs.jca.processor;

import com.github.ofofs.jca.annotation.Log;
import com.github.ofofs.jca.util.Sequence;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

/**
 * @author kangyonggan
 * @since 6/22/18
 */
public class LogProcessor {

    /**
     * 处理日志注解
     *
     * @param env 环境
     */
    public void process(RoundEnvironment env) {
        // 遍历每一个拥有@Log注解的方法
        for (Element element : env.getElementsAnnotatedWith(Log.class)) {
            // TODO "private static final ConsoleLogHandler varXxx = new ConsoleLogHandler();"
            String varName = Sequence.nextString("var");
            System.out.println(varName);

            // TODO "varXxx.logBefore(packageName, methodName, args);"

            // TODO "Long varXxx = System.currentTimeMillis();"

            // TODO "fieldXxx.logAfter(packageName, methodName, varXxx, returnValue);"
        }
    }

}
