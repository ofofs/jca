package com.github.ofofs.jca.cache;

import com.github.ofofs.jca.annotation.Cache;

/**
 * @author kangyonggan
 * @since 2018/6/26 0026
 */
public class CacheTest {

    @Cache("test:name:${name}")
    private static String getName(String name) {
        System.out.println(name);
        return name;
    }

    @Cache("test:name2:${name}")
    private static void getName222(String name, String xxx) {
        System.out.println(name);
    }

    public static void main(String[] args) {
        System.out.println(getName("小新"));
        System.out.println(getName("小新"));
        System.out.println(getName("小新"));
    }

}
