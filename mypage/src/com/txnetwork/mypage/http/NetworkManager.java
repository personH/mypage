package com.txnetwork.mypage.http;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebSettings;
import com.txnetwork.mypage.utils.LogUtils;

import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class NetworkManager {
    private final static String LOG_TAG = NetworkManager.class.getSimpleName();
    private static int mIsOPhoneChecked = 0;
    public final static String PROXY_HOST = "proxy_host";
    public final static String PROXY_PORT = "proxy_port";

    public static boolean isWIFIConnected(Context context) {
        boolean isWIFIConnected = false;

        try {
            ConnectivityManager connManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo.isConnected()
                    && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                isWIFIConnected = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isWIFIConnected;
    }

    /**
     * 检测网络是否可用
     *
     * @return
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

    /*
     * public static String getNetworkType(ConnectivityManager conManager) {
     * NetworkInfo networkInfo = con.getActiveNetworkInfo(); return
     * getNetworkType(networkInfo);
     *
     * }
     *
     * private static String getNetworkType(NetworkInfo networkInfo) { String
     * networkType = "UNKNOWN"; try { if (networkInfo != null) { switch
     * (networkInfo.getType()) { case ConnectivityManager.TYPE_WIFI: {
     * networkType = "WIFI"; break; } default: { if
     * (!TextUtils.isEmpty(networkInfo.getExtraInfo())) { networkType =
     * networkInfo.getExtraInfo().toUpperCase(); } else { LogUtils.LOGW(LOG_TAG,
     * "networkInfo.getExtraInfo() is empty!"); networkType =
     * networkInfo.getTypeName(); } break; } } }
     *
     * networkType = TextUtils.isEmpty(networkType) ? "UNKNOWN" : networkType;
     *
     * LogUtils.LOGW(LOG_TAG, "getNetworkType = " + networkType);
     *
     * } catch (Exception e) { networkType = ""; e.printStackTrace(); } return
     * networkType;
     *
     * }
     *
     * private static void showNetworkInfo(ConnectivityManager con) { try {
     * NetworkInfo aNetworkInfo[] = con.getAllNetworkInfo();
     *
     * for (NetworkInfo networkInfo : aNetworkInfo) { if (networkInfo != null) {
     * LogUtils.LOGV(LOG_TAG, "NetworkInfo: " + networkInfo.toString()); } }
     *
     * NetworkInfo networkInfo = con.getActiveNetworkInfo(); if (networkInfo !=
     * null) { LogUtils.LOGI(LOG_TAG, "ActiveNetworkInfo: " +
     * networkInfo.toString()); } else { LogUtils.LOGI(LOG_TAG,
     * "Failed to get active network info"); } } catch (Exception e) {
     * e.printStackTrace(); }
     *
     * }
     */
    @SuppressWarnings("finally")
    public static Map<String, Object> getProxy() {
        Map<String, Object> res = new HashMap<String, Object>();
        try {
            String proxyHost = android.net.Proxy.getDefaultHost();
            int proxyPort = android.net.Proxy.getDefaultPort();

            if (!TextUtils.isEmpty(proxyHost) && (proxyPort > 0)) {
                res.put(PROXY_HOST, proxyHost);
                res.put(PROXY_PORT, proxyPort);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res = null;
        } finally {
            return res;
        }
    }

    // Added by Li Yong on 2012-05-18
    // Samsung GT-I9008L has a strange bug, apps which use WebView cannot
    // connect to network in default mode
    public static void initNetworkForI9008L() {
        String model = Build.MODEL;
        if (!TextUtils.isEmpty(model) && model.equalsIgnoreCase("GT-I9008L")) {
            try {
                // Samsung GT-I9008L use a special method to set current apn for
                // webkit core
                String ret = System.setProperty("android.com.browser.apn", "internet");
                LogUtils.LOGW(LOG_TAG, "GT-I9008L found, result of 'System.setProperty' = " + ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //

    public static boolean isOPhone() {
        switch (mIsOPhoneChecked) {
            case 0:
                mIsOPhoneChecked = 2;
                try {
                    Method method1 = NetworkInfo.class.getMethod("getApType");
                    Method method2 = NetworkInfo.class.getMethod("getInterfaceName");
                    // Added by Li Yong on 2011-10-10
                    Method method3 = Socket.class.getMethod("setInterface", String.class);
                    Method method4 = WebSettings.class.getMethod("setProxy", Context.class, String.class, int.class);
                    // if (method1 != null && method2 != null)
                    if (method1 != null && method2 != null && method3 != null && method4 != null) {
                        mIsOPhoneChecked = 1;
                        return true;
                    }
                } catch (Exception e) {
                }
                return false;
            case 1:
                return true;
            case 2:
                return false;
        }
        return false;
    }
}
