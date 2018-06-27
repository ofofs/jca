package com.github.ofofs.jca.count;

import com.github.ofofs.jca.annotation.Count;

/**
 * @author kangyonggan
 * @since 6/27/18
 */
public class CountTest {

    /**
     * 10秒内同一个name最多调用5次，否则触发回调方法
     *
     * @param name name
     * @return username
     */
    @Count(value = "user:${name}", during = 10000, count = 5, violate = "getUsernameViolate")
    public String getUsername(String name) {
        return name;
    }

    /**
     * 10秒内同一个ip最多调用5次，否则触发回调方法
     *
     * @param name name
     * @return username
     */
    @Count(key = "getKey", during = 10000, count = 5, violate = "getUsernameViolate")
    public String getUsername2(String name) {
        return name;
    }

    /**
     * 10秒内同一个ip最多调用5次，否则触发回调方法
     *
     * @param name name
     */
    @Count(value = "user:${name}", during = 10000, count = 5, violate = "getUsernameViolate")
    public void getUsername3(String name) {
        if ("".equals(name)) {
            System.out.println(name);
            return;
        }
        System.out.println("xx");
    }

    /**
     * 10秒内同一个ip最多调用5次，否则触发回调方法
     *
     * @param name name
     */
    @Count(key = "getKey2", during = 10000, count = 5, violate = "getUsernameViolate")
    public static void getUsername4(String name) {
        if ("".equals(name)) {
            System.out.println(name);
            return;
        }
        System.out.println("xx");
    }

    public static String getUsernameViolate(String name) {
        return "error";
    }

    public String getKey(String name) {
        return "ip";
    }

    public static String getKey2(String name) {
        return "ip";
    }
}
