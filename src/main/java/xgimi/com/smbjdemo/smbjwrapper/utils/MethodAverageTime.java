package xgimi.com.smbjdemo.smbjwrapper.utils;

import android.os.SystemClock;
import android.util.Log;

public class MethodAverageTime {
    public long start;
    public long count;
    public long average;
    public long allTime;
    public String tag;

    public MethodAverageTime(String tag) {
        this.tag = tag;
    }

    public void start() {
        start = SystemClock.elapsedRealtime();
    }

    public long end() {
        long time = SystemClock.elapsedRealtime() - start;
        allTime += time;
        count++;
        average = allTime / count;
        Log.i(tag, "allTime=" + allTime + ",count=" + count + ",average=" + average);
        return time;
    }

    public void reset() {
        allTime = 0;
        count = 0;
        average = 0;
    }
}