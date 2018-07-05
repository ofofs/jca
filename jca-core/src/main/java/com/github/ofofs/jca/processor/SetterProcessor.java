package com.github.ofofs.jca.processor;

import com.github.ofofs.jca.annotation.Handler;
import com.github.ofofs.jca.annotation.Setter;
import com.github.ofofs.jca.model.JcaClass;
import com.github.ofofs.jca.model.JcaField;

import javax.annotation.processing.RoundEnvironment;

/**
 * 生成setter的注解处理器
 *
 * @author kangyonggan
 * @since 6/23/18
 */
public class SetterProcessor extends AbstractJcaProcessor {

    protected SetterProcessor(RoundEnvironment env) {
        super(env);
    }

    @Override
    protected void process() {
        if (isEnable(Handler.Type.SETTER)) {
            for (JcaClass jcaClass : getJcaClasses(Setter.class)) {
                for (JcaField jcaField : jcaClass.getJcaFields()) {
                    jcaClass.addSetterMethod(jcaField);
                }
            }
            for (JcaField jcaField : getJcaFields(Setter.class)) {
                jcaField.getJcaClass().addSetterMethod(jcaField);
            }
        }
    }

}
