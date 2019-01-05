package com.ljheee.generator.snowflake;

/**
 * 雪花算法
 */
public class SnowflakeWorker {


    /**
     * 算法的开始时间，基于 2017-09-01 开始
     */
    private static final long START_TIME = 1504195200000L;

    /**
     * 时间占的位数
     */
    private static final long TIME_BITS = 41;
    /**
     * 数据中心数量占的位数
     */
    private static final long DATA_CENTER_BITS = 5;
    /**
     * 计算机数量占的位数
     */
    private static final long WORKER_BITS = 5;
    /**
     * 序列占的位数
     */
    private static final long SEQUENCE_BITS = 12;

    /**
     * 时间的位偏移量
     */
    private static final long TIME_BIT_OFFSET = DATA_CENTER_BITS + WORKER_BITS + SEQUENCE_BITS;
    /**
     * 数据中心数量的位偏移量
     */
    private static final long DATA_CENTER_BIT_OFFSET = WORKER_BITS + SEQUENCE_BITS;
    /**
     * 计算机数量的位偏移量
     */
    private static final long WORKER_BIT_OFFSET = SEQUENCE_BITS;

    /**
     * 时间戳的最大数值: 31
     */
    private static final long MAX_TIME = ~(-1L << TIME_BITS);
    /**
     * 数据中心的最大数值: 31
     */
    private static final long MAX_DATA_CENTER = ~(-1L << DATA_CENTER_BITS);
    /**
     * 计算机的最大数值: 31
     */
    private static final long MAX_WORKER = ~(-1L << WORKER_BITS);
    /**
     * 序列号的最大数值: 4095
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * 最后获取ID时的时间戳
     */
    private static volatile long lastTimestamp = -1L;
    /**
     * 序列号
     */
    private static volatile long sequence = 0L;

    private final long dataCenterId;
    private final long workerId;

    /**
     * SnowFlake算法构造器，指定数据中心和计算机ID，初始化算法
     *
     * @param dataCenterId 数据中心ID
     * @param workerId     计算机ID
     */
    public SnowflakeWorker(long dataCenterId, long workerId) {
        if (dataCenterId < 0 || dataCenterId > MAX_DATA_CENTER) {
            throw new IllegalArgumentException(String.format(
                    "DataCenter Id can't be greater than %d or less than 0", MAX_DATA_CENTER));
        }
        if (workerId < 0 || workerId > MAX_WORKER) {
            throw new IllegalArgumentException(String.format(
                    "Worker Id can't be greater than %d or less than 0", MAX_WORKER));
        }
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
    }

    /**
     * 生成ID（线程安全）
     *
     * @return id
     */
    public synchronized long nextId() {
        long timestamp = timestamp();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟被修改过，回退在上一次ID生成时间之前应当抛出异常！！！
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException(String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        // 如果同一毫秒内并发获取ID，则递增计算序列号
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 如果序列号溢出（每毫秒并发数大于 4095），阻塞到下一毫秒获取时间戳
            if (sequence == 0) {
                timestamp = waitToNextMillis(lastTimestamp);
            }
        } else {  // 时间戳改变，重置序列号
            // 注意：如果分库分表，需要依赖ID，为避免分布不均衡，ID最后一位可以随机生成0-9
            sequence = 0L;
        }

        // 上一次生成ID的时间
        lastTimestamp = timestamp;

        return ((timestamp - START_TIME) << TIME_BIT_OFFSET)
                | (dataCenterId << DATA_CENTER_BIT_OFFSET)
                | (workerId << WORKER_BIT_OFFSET)
                | sequence;
    }

    /**
     * 解析ID
     *
     * @param id id
     * @return SnowFlakeId
     */
    public SnowFlakeId convert(long id) {
        SnowFlakeId snowFlakeId = new SnowFlakeId();
        snowFlakeId.setTimestamp(getTimestamp(id));
        snowFlakeId.setDataCenterId(getDataCenterId(id));
        snowFlakeId.setWorkerId(getWorkerId(id));
        snowFlakeId.setSequence(getSequence(id));
        return snowFlakeId;
    }

    /**
     * 从ID中解析出时间戳
     *
     * @param id id
     * @return 时间戳
     */
    public long getTimestamp(long id) {
        return (id >>> TIME_BIT_OFFSET & MAX_TIME) + START_TIME;
    }

    /**
     * 从ID中解析出数据中心ID
     *
     * @param id id
     * @return 数据中心ID
     */
    public long getDataCenterId(long id) {
        return (id >>> DATA_CENTER_BIT_OFFSET) & MAX_DATA_CENTER;
    }

    /**
     * 从ID中解析出计算机ID
     *
     * @param id id
     * @return 计算机ID
     */
    public long getWorkerId(long id) {
        return (id >>> WORKER_BIT_OFFSET) & MAX_WORKER;
    }

    /**
     * 从ID中解析出序列号
     *
     * @param id id
     * @return 序列号
     */
    public long getSequence(long id) {
        return id & MAX_SEQUENCE;
    }

    /**
     * 阻塞到 lastTimestamp 的下一毫秒
     *
     * @param lastTimestamp 阻塞的最后时间
     * @return timestamp
     */
    private long waitToNextMillis(long lastTimestamp) {
        long timestamp = timestamp();
        while (timestamp <= lastTimestamp)
            timestamp = timestamp();
        return timestamp;
    }

    /**
     * 获取时间戳
     */
    private long timestamp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowflakeWorker snowFlakeWorker = new SnowflakeWorker(0, 0);
        long length = 20 * 1;
        long start = System.currentTimeMillis();
        long end = start + length;
        long sum = 0;
        while (end > System.currentTimeMillis()) {
            long nextId = snowFlakeWorker.nextId();
            System.out.println(nextId);
            sum++;
        }
        System.out.println("一秒生成ID数量" + sum);
    }


}
