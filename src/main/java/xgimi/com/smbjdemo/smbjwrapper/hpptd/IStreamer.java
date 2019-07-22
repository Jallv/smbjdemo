package xgimi.com.smbjdemo.smbjwrapper.hpptd;

/**
 * Created by ram on 15/1/28.
 */
public interface IStreamer {

    void start();

    void stopStream();

    /**
     * get post
     *
     * @return
     */
    int getPort();

    /**
     * For using in current device, you can just use "127.0.0.1"<p/>
     * For others, use your true ip in ipv4
     *
     * @return
     */
    String getIp();
}
