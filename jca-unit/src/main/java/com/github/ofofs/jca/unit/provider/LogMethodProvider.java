package com.github.ofofs.jca.unit.provider;

import com.github.ofofs.jca.unit.annotation.CLog;
import com.github.ofofs.jca.unit.context.LogMethodContext;

import org.apiguardian.api.API;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.stream.Stream;

/**
 * <p> CLog 方法测试实现 </p>
 *
 * <pre> Created: 2018-06-29 09:52  </pre>
 * <pre> Project: jca  </pre>
 *
 * @author houbinbin
 * @version 1.0
 * @since 1.0
 */
@API(status = API.Status.EXPERIMENTAL)
public class LogMethodProvider implements TestTemplateInvocationContextProvider {

    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return context.getTestMethod()
                .filter(m -> AnnotationSupport.isAnnotated(m, CLog.class))
                .isPresent();
    }

    @Override
    public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
        return Stream.of(new LogMethodContext(context));
    }

}
