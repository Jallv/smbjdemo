package xgimi.com.smbjdemo.model;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


import java.util.ArrayList;

import xgimi.com.smbjdemo.model.AppManager;

public class MediaOpenUtils {

    public static final String IMAGE_POSITION = "STATE_POSITION";
    public static final String HTTP_IMAGE_PATH = "image_urls_path_lee_other_bbc";
    public static final String IMAGE_PATH = "image_urls_path_lee";
    private static final String DLNA_NAME = "dlan-name-list";

    public static void playPicture(Context context, String path) {
        try {
            Intent intent = new Intent();
            if (AppManager.isInstallAPK(context,"com.xgimi.image.browser")) {
                intent.setComponent(new ComponentName("com.xgimi.image.browser", "com.xgimi.image.browser.activity.ImagePlayActivity"));
            } else {
                intent.setComponent(new ComponentName("com.xgimi.gimiplayer", "com.xgimi.gimiplayer.activity.ImagePlayActivity"));
            }
            intent.putExtra(IMAGE_PATH, path);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void playPictureList(Context context, ArrayList<String> urlList, int position) {
        try {
            Intent intent = new Intent();
            if (AppManager.isInstallAPK(context,"com.xgimi.image.browser")) {
                intent.setComponent(new ComponentName("com.xgimi.image.browser", "com.xgimi.image.browser.activity.ImagePlayActivity"));
            } else {
                intent.setComponent(new ComponentName("com.xgimi.gimiplayer", "com.xgimi.gimiplayer.activity.ImagePlayActivity"));
            }
            intent.putExtra(HTTP_IMAGE_PATH, urlList);
            intent.putExtra(IMAGE_POSITION, position);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String URL_LIST = "badiu-daye-other-wocao-list";


    public static void playVideoList(Context context, ArrayList<String> urls, ArrayList<String> names, int position) {
        try {
            /* 新建一个Intent对象 */
            Intent intent = new Intent();
            intent.putStringArrayListExtra(URL_LIST, urls);
            intent.putStringArrayListExtra(DLNA_NAME, names);
            /* 指定intent要启动的类 */
            intent.setComponent(new ComponentName("com.xgimi.gimiplayer",
                    "com.xgimi.gimiplayer.activity.VideoPlayerActivity"));
            /* 启动一个新的Activity */
            intent.putExtra("position", position);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
