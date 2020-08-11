package org.example.main;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        /*int a = 2;
        System.out.println(~a);
        System.out.println(Integer.toBinaryString(2));*/

        // int a = 15, b = 2;
        // System.out.println(a ^ b);

        // 11111111111111111110011101100111
        /*int a = 10;
        System.out.println(Integer.toBinaryString(a));
        System.out.println(Integer.toBinaryString(-a));
        System.out.println(Integer.toBinaryString(a>>5));
        System.out.println(Integer.toBinaryString(-a>>5));
        System.out.println(Integer.toBinaryString(a>>>5));
        System.out.println(Integer.toBinaryString(-a>>>5));
        System.out.println(Integer.toBinaryString(a<<5));
        System.out.println(Integer.toBinaryString(-a<<5));
        */

        System.out.println(hash(12));

    }

    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

}
