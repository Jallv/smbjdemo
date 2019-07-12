package xgimi.com.smbjdemo;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import java.util.List;

/**
 * Author:anlong.jiang
 * Maintainer:anlong.jiang
 * Date:2017/1/14 0014
 */

public class AppManager {
//打开类型
    /**
     * 打开app
     */
    public static final int MSG_OPEN_TYPE_APP = 0;
    /**
     * 发送广播
     */
    public static final int MSG_OPEN_TYPE_BROADCAST = 1;
    /**
     * 打开activity
     */
    public static final int MSG_OPEN_TYPE_ACTIVITY = 2;
    /**
     * 发送toast
     */
    public static final int MSG_OPEN_TYPE_TOAST = 3;
    /**
     * 启动服务
     */
    public static final int MSG_OPEN_TYPE_SERVICE = 4;
    /**
     * 启动爱奇艺
     */
    public static final int MSG_OPEN_TYPE_IQIYI = 5;

    /**
     * 获取app的图标
     *
     * @param context
     * @param package_name
     * @return
     */
    public static Drawable getAppIcon(Context context, String package_name) {
        PackageManager packageManager = context.getPackageManager();
        Drawable drawable = null;
        try {
            ApplicationInfo info = packageManager.getApplicationInfo(package_name, PackageManager.GET_META_DATA);
            drawable = info.loadIcon(packageManager);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    /**
     * 是否安装指定应用
     *
     * @param context     上下文
     * @param packageName 包名
     * @return
     */
    public static boolean isInstallAPK(Context context, String packageName) {
        boolean result = false;
        List<PackageInfo> packageInfoList = context.getPackageManager().getInstalledPackages(0);
        for (PackageInfo info : packageInfoList) {
            if (info.packageName.equals(packageName)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 根据包名启动app
     *
     * @param context     The Context
     * @param packageName 包名
     */
    public static void launchAPP(Context context, String packageName) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动指定activity
     *
     * @param context
     * @param packageName
     * @param activityName
     */
    public static boolean launchActivity(Context context, String packageName, String activityName) {
        try {
            ComponentName cn = new ComponentName(packageName, activityName);
            Intent intent = new Intent();
            intent.setComponent(cn);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.i("AppsManager", "e=" + e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static Bitmap getApkIconInLauncher(Context context, String pkgname) {
        Log.i("JIcon", "-------------pkgname=" + pkgname);
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse("content://com.xgimi.home.provider.commonappprovider/query_app");
        Cursor cursor = contentResolver.query(uri, null, pkgname, null, null);
        byte[] icon = null;
        while (cursor != null && cursor.moveToNext()) {
            icon = cursor.getBlob(cursor.getColumnIndex("icon"));
        }
        cursor.close();
        return (icon != null && icon.length > 0) ? BitmapFactory.decodeByteArray(icon, 0, icon.length) : null;
    }

    public static boolean isTopApp(Context context, String pkgName) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
            if (list != null && list.size() > 0 && pkgName.equals(list.get(0).topActivity.getPackageName())) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * intent有效性检查
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean isIntentAvailable(Context context, Intent intent, int mode) {
        final PackageManager packageManager = context.getApplicationContext().getPackageManager();
        List<ResolveInfo> list;
        if (MSG_OPEN_TYPE_BROADCAST == mode) {
            list = packageManager.queryBroadcastReceivers(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
        } else if (MSG_OPEN_TYPE_ACTIVITY == mode) {
            list = packageManager.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
        } else if (MSG_OPEN_TYPE_SERVICE == mode) {
            list = packageManager.queryIntentServices(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
        } else {
            return true;
        }
        return list != null && list.size() > 0;
    }
}
