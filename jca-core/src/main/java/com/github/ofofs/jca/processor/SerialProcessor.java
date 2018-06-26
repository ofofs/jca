package com.github.ofofs.jca.processor;

import com.github.ofofs.jca.annotation.Handler;
import com.github.ofofs.jca.annotation.Serial;
import com.github.ofofs.jca.model.JcaClass;
import com.github.ofofs.jca.model.JcaCommon;
import com.github.ofofs.jca.model.JcaField;
import com.github.ofofs.jca.model.JcaObject;
import com.github.ofofs.jca.util.Sequence;
import com.sun.tools.javac.code.Flags;

import javax.annotation.processing.RoundEnvironment;
import java.io.Serializable;

/**
 * 序列化注解处理器
 *
 * @author kangyonggan
 * @since 6/23/18
 */
public class SerialProcessor extends CoreProcessor {

    protected SerialProcessor(RoundEnvironment env) {
        super(env);
    }

    @Override
    protected void process() {
        if (isEnable(Handler.Type.SERIAL)) {
            for (JcaClass jcaClass : getJcaClasses(Serial.class)) {
                process(jcaClass);
            }
        }
    }

    /**
     * 处理每一个类
     *
     * @param jcaClass 类
     */
    private void process(JcaClass jcaClass) {
        // 给此类添加一个接口
        jcaClass.addInterface(Serializable.class);

        // 创建一个字段
        createSerialVersionUID(jcaClass);
    }

    /**
     * private static final Long serialVersionUID = -7216267182065602559L;
     *
     * @param jcaClass 类
     */
    private void createSerialVersionUID(JcaClass jcaClass) {
        JcaObject value = JcaCommon.getValue(Sequence.nextLong());

        // private static final Long serialVersionUID = value;
        JcaField jcaField = new JcaField(Flags.PRIVATE | Flags.STATIC | Flags.FINAL, Long.class, "serialVersionUID", value);
        jcaClass.insert(jcaField);
    }
}
