package xgimi.com.smbjdemo.smbjwrapper.hpptd;

import android.text.TextUtils;
import android.util.Log;


import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @Author anlong.jiang
 * @Date 2018/9/4 0004
 * @Desc
 **/

public class SmbTools {
    private final static String TAG = "SmbTools";

    public static String convertToHttpUrl(String url, String ip, int port) {
        Log.i(TAG, " path:" + url);
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        try {
            url = URLEncoder.encode(url, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder("http://")
                .append(ip)
                .append(File.pathSeparator)
                .append(port)
                .append(SmbHelper.CONTENT_EXPORT_URI);
        builder.append(url);
        Log.i(TAG, " ---------> final file path: " + builder.toString());

        return builder.toString();
    }

    /**
     * Turn from <b>"/smb=XXX"</b> to <b>"smb://XXX"</b>
     */
    public final static String cropStreamSmbURL(String url) {
        Log.d(SmbHelper.TAG, " cropStreamSmbURL ----------> url = " + url);
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        if (!url.startsWith(SmbHelper.CONTENT_EXPORT_URI)) {
            return url;
        }
        if (url.length() <= SmbHelper.CONTENT_EXPORT_URI.length()) {
            return url;
        }
        String filePaths = url.substring(SmbHelper.CONTENT_EXPORT_URI.length());
        int indexOf = filePaths.indexOf("&");
        if (indexOf != -1) {
            filePaths = filePaths.substring(0, indexOf);
        }
        return filePaths;
    }

    public final static boolean isSmbUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return true;
    }
}
