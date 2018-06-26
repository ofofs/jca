package com.github.ofofs.jca.processor;

import com.github.ofofs.jca.annotation.Handler;
import com.github.ofofs.jca.annotation.Log;
import com.github.ofofs.jca.handler.impl.MemoryCacheHandler;
import com.github.ofofs.jca.model.*;
import com.github.ofofs.jca.util.Sequence;
import com.sun.tools.javac.code.Flags;

import javax.annotation.processing.RoundEnvironment;

/**
 * 日志注解处理器
 *
 * @author kangyonggan
 * @since 6/22/18
 */
public class CacheProcessor extends AbstractJcaProcessor {

    CacheProcessor(RoundEnvironment env) {
        super(env);
    }

    /**
     * 处理日志注解
     */
    @Override
    protected void process() {
        if (isEnable(Handler.Type.LOG)) {
            String fieldName = Sequence.nextString("field");
            for (JcaMethod jcaMethod : getJcaMethods(Log.class)) {
                process(jcaMethod, fieldName);
            }
        }
    }

    /**
     * 处理每一个方法
     *
     * @param jcaMethod 方法
     * @param fieldName 字段名
     */
    private void process(JcaMethod jcaMethod, String fieldName) {
        // 给方法所在的类创建一个字段
        createField(jcaMethod.getJcaClass(), fieldName);

    }

    /**
     * private static final MemoryCacheHandler fieldName = new MemoryCacheHandler();
     *
     * @param jcaClass  类
     * @param fieldName 字段名
     */
    private void createField(JcaClass jcaClass, String fieldName) {
        String handlerClass = MemoryCacheHandler.class.getName();
        JcaClass handler = getHandler(Handler.Type.CACHE);
        if (handler != null) {
            handlerClass = handler.getFullName();
        }

        // new MemoryCacheHandler()
        JcaObject value = JcaCommon.instance(jcaClass, handlerClass);

        // private static final MemoryCacheHandler fieldName = value;
        JcaField jcaField = new JcaField(Flags.PRIVATE | Flags.STATIC | Flags.FINAL, handlerClass, fieldName, value);
        jcaClass.insert(jcaField);
    }
}
