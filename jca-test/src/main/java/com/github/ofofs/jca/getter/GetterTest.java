package com.github.ofofs.jca.getter;

import com.github.ofofs.jca.annotation.Getter;

/**
 * @author kangyonggan
 * @since 2018/6/30 0030
 */
public class GetterTest {

    @Getter
    private String name;

    @Getter
    private static int age;

    @Getter
    private static final String PASSWORD = "123456";

    public static int getAge() {
        return age;
    }
}
