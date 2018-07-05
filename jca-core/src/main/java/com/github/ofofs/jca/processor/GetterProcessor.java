package com.github.ofofs.jca.processor;

import com.github.ofofs.jca.annotation.Getter;
import com.github.ofofs.jca.annotation.Handler;
import com.github.ofofs.jca.model.JcaClass;
import com.github.ofofs.jca.model.JcaField;

import javax.annotation.processing.RoundEnvironment;

/**
 * 生成getter的注解处理器
 *
 * @author kangyonggan
 * @since 6/23/18
 */
public class GetterProcessor extends AbstractJcaProcessor {

    protected GetterProcessor(RoundEnvironment env) {
        super(env);
    }

    @Override
    protected void process() {
        if (isEnable(Handler.Type.GETTER)) {
            for (JcaClass jcaClass : getJcaClasses(Getter.class)) {
                for (JcaField jcaField : jcaClass.getJcaFields()) {
                    jcaClass.addGetterMethod(jcaField);
                }
            }
            for (JcaField jcaField : getJcaFields(Getter.class)) {
                jcaField.getJcaClass().addGetterMethod(jcaField);
            }
        }
    }

}
