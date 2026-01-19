package com.weatheria.weatheria.util;

public class ElapsedTimeUtil {

    public long calculateElapsedTime(long startNanos) {
        return Math.max(0L, (System.nanoTime() - startNanos) / 1_000_000);
    }
}
