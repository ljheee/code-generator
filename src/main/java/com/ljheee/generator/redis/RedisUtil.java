package com.ljheee.generator.redis;

import redis.clients.jedis.Jedis;

/**
 *
 */
public class RedisUtil {


    public static long getIncr(String key, int timeout) {

        Jedis jedis = new Jedis("127.0.0.1", 6379);

        // incr(key)是同步方法调用，对key进行加1，如果key不存在就创建值为0的key
        Long num = jedis.incr(key);
        if (timeout > 0) {
            jedis.expire(key, timeout);
        }
        jedis.close();
        return num;
    }
}
