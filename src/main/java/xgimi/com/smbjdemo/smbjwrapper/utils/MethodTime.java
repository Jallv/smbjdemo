package xgimi.com.smbjdemo.smbjwrapper.utils;

import android.os.SystemClock;
import android.util.Log;

public class MethodTime {
    public long start;
    public String tag;

    public MethodTime(String tag) {
        this.tag = tag;
    }

    public void start() {
        start = SystemClock.elapsedRealtime();
    }

    public long end(boolean log, String method) {
        long time = SystemClock.elapsedRealtime() - start;
        if (log) {
            Log.i(tag, method + "once time=" + time);
        }
        return time;
    }
}