package com.github.ofofs.jca.log;

import com.github.ofofs.jca.annotation.Log;

/**
 * @author kangyonggan
 * @since 6/22/18
 */
public class LogTest {

    /**
     * 测试无代码块
     */
    @Log
    public void test01() {
    }

    /**
     * 测试有代码块
     */
    @Log
    public void test02() {
        System.out.println("Hello");
    }

    /**
     * 测试代码块中有return
     *
     * @param a a
     * @param b b
     */
    @Log
    public void test03(int a, int b) {
        System.out.println(a);
        if (a > b) {
            System.out.println("return;");
            return;
        }
        if (a > 1) {
            System.out.println("return;");
            return;
        }
        System.out.println(b);
    }

    /**
     * 测试代码块中有return且有返回值
     *
     * @param a a
     * @param b b
     * @return max
     */
    @Log
    public int test04(int a, int b) {
        if (a > b) {
            return a;
        }
        return b;
    }

    /**
     * 测试while
     *
     * @param a a
     * @param b b
     * @return max
     */
    @Log
    public int test05(int a, int b) {
        while (a < 10) {
            a++;
            System.out.println(a);
            if (a == b) {
                System.out.println("xxxxxxxxxx");
                return b;
            }
        }
        return b;
    }

    public static void main(String[] args) {
        System.out.println(new LogTest().test04(1, 2));
    }

}
