package com.ljheee.generator;

/**
 *
 */
public class NumberUtil {


    /**
     * 左边填充0
     * @param num
     * @param len   返回结果的位数
     * @return
     */
    public static String padLeft(long num, int len) {
        return String.format("%0" + len + "d", num);
    }


    public static String toThousand(int num){
        return String.format("%,d", num);//输出千分位
    }


}
