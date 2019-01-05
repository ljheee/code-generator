package com.ljheee.generator.snowflake;

import java.io.Serializable;

public class SnowFlakeId implements Serializable {

    private long timestamp;
    private long dataCenterId;
    private long workerId;
    private long sequence;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SnowFlakeId{");
        sb.append("timestamp=").append(timestamp);
        sb.append(", dataCenterId=").append(dataCenterId);
        sb.append(", workerId=").append(workerId);
        sb.append(", sequence=").append(sequence);
        sb.append('}');
        return sb.toString();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDataCenterId() {
        return dataCenterId;
    }

    public void setDataCenterId(long dataCenterId) {
        this.dataCenterId = dataCenterId;
    }

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}
