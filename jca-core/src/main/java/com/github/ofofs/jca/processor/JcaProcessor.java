package com.github.ofofs.jca.processor;

import com.github.ofofs.jca.util.JcaUtil;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * JCA编译时注解处理器
 *
 * @author kangyonggan
 * @since 6/22/18
 */
@SupportedAnnotationTypes("com.github.ofofs.jca.annotation.Log")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JcaProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        JcaUtil.init(env);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        // 日志
        new LogProcessor(env).process();
        return true;
    }

}
