package com.github.ofofs.jca.processor;

import com.github.ofofs.jca.model.JcaClass;
import com.github.ofofs.jca.model.JcaField;
import com.github.ofofs.jca.model.JcaMethod;
import com.sun.tools.javac.code.Symbol;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kangyonggan
 * @since 6/22/18
 */
public abstract class BaseProcessor {

    /**
     * 环境
     */
    private RoundEnvironment env;

    protected BaseProcessor(RoundEnvironment env) {
        this.env = env;
    }

    /**
     * 处理方法
     */
    protected abstract void process();

    /**
     * 获取带有指定注解的方法
     *
     * @param annotationClass 注解类
     * @return 返回所有带有指定注解的方法
     */
    protected Set<JcaMethod> getJcaMethods(Class<? extends Annotation> annotationClass) {
        Set<? extends Element> elements = env.getElementsAnnotatedWith(annotationClass);
        Set<JcaMethod> jcaMethods = new HashSet<>(elements.size());
        for (Element e : elements) {
            jcaMethods.add(new JcaMethod((Symbol.MethodSymbol) e));
        }
        return jcaMethods;
    }

    /**
     * 获取带有指定注解的类
     *
     * @param annotationClass 注解类
     * @return 返回所有带有指定注解的类
     */
    protected Set<JcaClass> getJcaClasses(Class<? extends Annotation> annotationClass) {
        Set<? extends Element> elements = env.getElementsAnnotatedWith(annotationClass);
        Set<JcaClass> jcaClasses = new HashSet<>(elements.size());
        for (Element e : elements) {
            jcaClasses.add(new JcaClass((Symbol.ClassSymbol) e));
        }
        return jcaClasses;
    }

    /**
     * 获取带有指定注解的字段
     *
     * @param annotationClass 注解类
     * @return 返回所有带有指定注解的字段
     */
    protected Set<JcaField> getJcaFields(Class<? extends Annotation> annotationClass) {
        Set<? extends Element> elements = env.getElementsAnnotatedWith(annotationClass);
        Set<JcaField> jcaFields = new HashSet<>(elements.size());
        for (Element e : elements) {
            jcaFields.add(new JcaField((Symbol.VarSymbol) e));
        }
        return jcaFields;
    }
}
