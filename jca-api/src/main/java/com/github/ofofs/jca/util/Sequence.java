package com.github.ofofs.jca.util;


import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author kangyonggan
 * @since 6/15/18
 */
public final class Sequence {

    private long workerId;
    private long datacenterId;
    private long sequence = 0L;
    private long max = 31L;
    private static long maxOffset = 5L;
    private long lastTimestamp = -1L;

    private static Sequence instance = new Sequence();

    private Sequence() {
        this.datacenterId = getDatacenterId(31L);
        this.workerId = getMaxWorkerId(this.datacenterId, 31L);
    }

    public Sequence(long workerId, long datacenterId) {
        if (workerId <= max && workerId >= 0L) {
            if (datacenterId <= max && datacenterId >= 0L) {
                this.workerId = workerId;
                this.datacenterId = datacenterId;
            }
        }
    }

    private static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuilder mpid = new StringBuilder();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (name != null && name.length() > 0) {
            mpid.append(name.split("@")[0]);
        }

        return (long) (mpid.toString().hashCode() & '\uffff') % (maxWorkerId + 1L);
    }

    private static long getDatacenterId(long maxDatacenterId) {
        long id = 0L;

        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                if (null != mac) {
                    id = (255L & (long) mac[mac.length - 1] | 65280L & (long) mac[mac.length - 2] << 8) >> 6;
                    id %= maxDatacenterId + 1L;
                }
            }
        } catch (Exception var7) {
        }

        return id;
    }

    public static synchronized long nextLong() {
        long timestamp = instance.timeGen();
        if (timestamp < instance.lastTimestamp) {
            long offset = instance.lastTimestamp - timestamp;
            if (offset > maxOffset) {
                throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
            }

            try {
                instance.wait(offset << 1);
                timestamp = instance.timeGen();
                if (timestamp < instance.lastTimestamp) {
                    throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", offset));
                }
            } catch (Exception var6) {
                throw new RuntimeException(var6);
            }
        }

        if (instance.lastTimestamp == timestamp) {
            instance.sequence = instance.sequence + 1L & 4095L;
            if (instance.sequence == 0L) {
                timestamp = instance.tilNextMillis(instance.lastTimestamp);
            }
        } else {
            instance.sequence = ThreadLocalRandom.current().nextLong(1L, 3L);
        }

        instance.lastTimestamp = timestamp;
        return timestamp - 1288834974657L << 22 | instance.datacenterId << 17 | instance.workerId << 12 | instance.sequence;
    }

    public static String nextString() {
        Long nextLong = nextLong();
        return String.valueOf(Math.abs(nextLong));
    }

    public static String nextString(String str) {
        Long nextLong = nextLong();
        return str + Math.abs(nextLong);
    }

    protected long tilNextMillis(long lastTimestamp) {
        long timestamp;
        for (timestamp = this.timeGen(); timestamp <= lastTimestamp; timestamp = this.timeGen()) {
        }

        return timestamp;
    }

    protected long timeGen() {
        return System.nanoTime();
    }
}

