package com.ljheee.generator.uuid;

import com.ljheee.generator.CodeGenerator;

import java.util.UUID;

/**
 * java通过UUID生成11位唯一单号
 */
public class UuidCodeGenerator implements CodeGenerator {

    @Override
    public String generate(String prefix) {
        return prefix + getCode();
    }

    public static String getCode() {
        int machineId = 1;// 最大支持1-9个集群机器部署
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {// 有可能是负数
            hashCodeV = -hashCodeV;
        }
        // 0 代表前面补充0
        // d 代表参数为正数型
        return machineId + String.format("%010d", hashCodeV);//整数长度为10，如果不到10位就用0填充
    }


    public static void main(String[] args) {
        System.out.println(getCode());//10150008978   1+10=11位
    }


}
