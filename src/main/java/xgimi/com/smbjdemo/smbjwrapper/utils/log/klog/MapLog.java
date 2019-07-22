package xgimi.com.smbjdemo.smbjwrapper.utils.log.klog;

import android.util.Log;


import java.util.Map;

import xgimi.com.smbjdemo.smbjwrapper.utils.log.KLogUtil;

/**
 * @author anlong.jiang
 * @date on 2019/7/16
 * @describe TODO
 */
public class MapLog {
    public static void printMap(String tag, Map<?, ?> map) {

        KLogUtil.printLine(tag, true);
        if (map == null || map.size() == 0) {
            Log.d(tag, "null");
        } else {
            for (Map.Entry<?, ?> item : map.entrySet()) {
                Log.d(tag, item.getKey() + ":" + item.getValue());
            }
        }
        KLogUtil.printLine(tag, false);
    }
}
