package xgimi.com.smbjdemo;

import xgimi.com.smbjdemo.model.SambaThreadPoolExecutor;
import xgimi.com.smbjdemo.smbjwrapper.core.ShareClient;

/**
 * @author anlong.jiang
 * @date on 2019/7/19
 * @describe TODO
 */
public class SmbClientController {
    private SambaThreadPoolExecutor mSambaThreadPoolExecutor;

    public SmbClientController() {
        mSambaThreadPoolExecutor = new SambaThreadPoolExecutor();
    }

    public void connect(String host, String name, String passworld, String domain) {
        mSambaThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    new ShareClient(host, name, passworld, domain);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
