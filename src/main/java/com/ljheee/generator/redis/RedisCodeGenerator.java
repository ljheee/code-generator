package com.ljheee.generator.redis;

import com.ljheee.generator.CodeGenerator;
import com.ljheee.generator.NumberUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 第三方 缓存实现 订单号生成
 */
public class RedisCodeGenerator implements CodeGenerator {


    @Override
    public String generate(String prefix) {
        return generateByDate(prefix, new Date(), "yyyyMMdd");
    }

    private String generateByDate(String prefix, Date date, String pattern) {

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String key = prefix + sdf.format(date);
        long num = RedisUtil.getIncr(key, -1);
        return key + NumberUtil.padLeft(num, prefix.length());
    }


    public static void main(String[] args) {
        CodeGenerator codeGenerator = new RedisCodeGenerator();
        for (int i = 0; i < 100; i++) {
            System.out.println(codeGenerator.generate("BJBG"));//返回的第一个BJBG201901040001
        }
    }
}
