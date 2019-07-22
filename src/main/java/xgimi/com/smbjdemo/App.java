package xgimi.com.smbjdemo;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * @author anlong.jiang
 * @date on 2019/7/19
 * @describe TODO
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
    }
}
