package com.github.ofofs.jca.processor;

import com.github.ofofs.jca.model.JcaCommon;
import com.github.ofofs.jca.util.JcaExpressionUtil;

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
@SupportedAnnotationTypes("com.github.ofofs.jca.annotation.Serial")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JcaProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        JcaCommon.init(env);
        JcaExpressionUtil.init(env);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        // 序列化
        new SerialProcessor(env).process();
        // Getter
        new GetterProcessor(env).process();
        // 缓存
        new CacheProcessor(env).process();
        // 日志(日志注解需要在缓存注解后面处理)
        new LogProcessor(env).process();
        // 调用次数限制(调用次数限制注解需要在日志注解后面处理)
        new CountProcessor(env).process();
        // 调用间隔限制(调用间隔限制注解需要在日志注解后面处理)
        new FrequencyProcessor(env).process();
        // 工具类处理
        new UtilProcessor(env).process();
        return true;
    }

}
