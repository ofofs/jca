package com.github.ofofs.jca.constants;

import com.github.ofofs.jca.annotation.API;

/**
 * 常量
 *
 * @author kangyonggan
 * @since 6/23/18
 */
public interface JcaConstants {

    /**
     * 返回值类型void
     */
    String RETURN_VOID = "void";

    /**
     * 构造器名称
     */
    @API(status = API.Status.EXPERIMENTAL)
    String CONSTRUCTOR_NAME = "<init>";
}
