package com.txnetwork.mypage.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.NotFoundException;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.datacenter.UserOperate;
import com.txnetwork.mypage.entity.UserInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class TxNetworkUtil {
    private static final String TAG = TxNetworkUtil.class.getSimpleName();
    private static String DEVICE_ID = null;
    public static float screenDensity = -1;
    private static int displayWidth = 0;
    private static int displayHeight = 0;
    private static int statusBarHeight = 0;

    private static String keyfield = "";// key
    private static String userId = "";// 用户ID

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            LogUtils.LOGE("VersionInfo", "Exception", e);
        }
        return versionName;
    }

    /**
     * 返回当前程序版本号
     */
    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (Exception e) {
            LogUtils.LOGE("VersionInfo", "Exception", e);
        }
        return versionCode;
    }

    /**
     * 获得手机卡的IMSI
     *
     * @param context
     * @return
     */
    public static String getSubscriberId(Context context) {
        String subscriberId = "";

        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if (tm != null) {
                Object obj = invokeDeclaredMethod(tm.getClass(), tm,
                        "getSubscriberId", null, null);
                if (obj != null && obj instanceof String) {
                    subscriberId = String.valueOf(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (TextUtils.isEmpty(subscriberId)) {
                if (tm != null) {
                    subscriberId = tm.getSubscriberId();
                }
            }
        }

        if (TextUtils.isEmpty(subscriberId)) {
            LogUtils.LOGV(TAG, "Failed to get IMSI!");
        }

        return subscriberId;
    }

    // Added by Shawn 2012-07-31 判断是否有sim卡
    public static boolean ifHasSim(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String sn = tm.getSubscriberId();
        if (sn != null && sn.length() > 0) {
            return true;
        } else {
            if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得自定义设备ID
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        if (DEVICE_ID == null || DEVICE_ID.length() > 0) {
            DeviceUuidFactory factory = new DeviceUuidFactory(context);
            DEVICE_ID = factory.getDeviceUuid();
        }
        return DEVICE_ID;
    }

    /**
     * 获得设备的IMEI
     *
     * @param context
     * @return
     */
    public static String getIMEIId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        return "866007023045618";
    }

    // 获取屏幕宽度
    public static int getDisplayWidth(Context context) {
        if (displayWidth <= 0) {
            WindowManager wm = (WindowManager) context.getApplicationContext()
                    .getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            displayWidth = dm.widthPixels;
        }
        return displayWidth;
    }

    // 获取屏幕高度
    public static int getDisplayHeight(Context context) {
        if (displayHeight <= 0) {
            WindowManager wm = (WindowManager) context.getApplicationContext()
                    .getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            displayHeight = dm.heightPixels;
        }
        return displayHeight;
    }

    /**
     * 获得状态栏的高
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight <= 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                statusBarHeight = context.getResources().getDimensionPixelSize(
                        x);
            } catch (Exception e) {
                LogUtils.LOGE(TAG, "get status bar height fail");
            }
        }
        return statusBarHeight;
    }

    /**
     * 获得屏幕密度
     *
     * @param context
     * @return
     */
    public static float getScreenDensity(Context context) {
        if (screenDensity <= 0) {
            WindowManager wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(dm);
            screenDensity = dm.density;
        }
        SharedUtils.storeScreenDensity(context, screenDensity);
        return screenDensity;
    }

    public static Object invokeDeclaredMethod(Class<?> declaredClass,
                                              Object classObject, String methodName, Object params[],
                                              Class<?> paramTypes[]) {
        Object ret = null;
        try {
            Method method = declaredClass.getDeclaredMethod(methodName,
                    paramTypes);
            method.setAccessible(true);
            if (params == null) {
                ret = method.invoke(classObject, new Object[0]);
            } else {
                ret = method.invoke(classObject, params);
            }
        } catch (Exception exception) {
            LogUtils.LOGV(TAG, "Exception in invokeDeclaredMethod", exception);
        }
        return ret;
    }


    /**
     * 获取手机厂商
     *
     * @return
     */
    public static String getManufacturer() {
        //return android.os.Build.MANUFACTURER;
        return "Meizu";
    }

    /**
     * 获取手机类型
     *
     * @return
     */
    public static String getType() {
        //return android.os.Build.MODEL;
        return "MX4";
    }


    /**
     * 获得用户key
     *
     * @return
     */
    public static String getUserKey(Context context) {
        if (StringUtil.isNotNull(keyfield)) {
            return keyfield;
        }
        UserInfo userInfo = UserOperate.getInstance(context).queryUser();
        if (null != userInfo && StringUtil.isNotNull(userInfo.getKeyfield())) {
            String localKey = userInfo.getKeyfield();
            keyfield = TxNetworkPool.CBCDecryptStr(localKey);
            return keyfield;
        } else {
            return null;
        }
    }

    /**
     * 设置用户ID
     *
     * @return
     */
    public static void setUserId(String userId) {
        TxNetworkUtil.userId = userId;
    }

    /**
     * 获得用户ID
     *
     * @param context
     * @return
     */
    public static String getUserId(Context context) {
        if (StringUtil.isNotNull(userId) && !userId.equals("0")) {
            return userId;
        }
        userId = SharedUtils.getUsersId(context);
        return userId;
    }

    /**
     * 检测网络是否可用
     *
     * @return true 可用，false不可用
     */
    public static boolean isNetConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 检测WIFI网络是否可用
     *
     * @return true 可用，false不可用
     */
    public static boolean isWIFIConnected(Context context) {
        boolean isWIFIConnected = false;

        try {
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (null != networkInfo && networkInfo.isConnected()
                    && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                isWIFIConnected = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isWIFIConnected;
    }

    public static String getVersion() {
//        return android.os.Build.VERSION.RELEASE;
        return "4.4.2";
    }

    public static String getLocalMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        //return info.getMacAddress();
        return "38:bc:1a:b7:8c:e4";
    }

    /**
     * 根据网址获取icon地址
     *
     * @param urlStr
     * @return
     */
    public static String getUrlIcon(String urlStr) {
        String icon;
        if (urlStr.charAt(urlStr.length() - 1) == '/') {
            icon = urlStr + "favicon.ico";
        } else {
            icon = urlStr + "/favicon.ico";
        }
        if (!urlStr.startsWith("http://")) {
            icon = "http://" + icon;
        }
        return icon;
    }
}
