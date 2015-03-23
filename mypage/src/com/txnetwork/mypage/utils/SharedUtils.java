package com.txnetwork.mypage.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2014/12/30.
 */
public class SharedUtils {

    private static final String TAG = SharedUtils.class.getSimpleName();

    public static final String MAIN_MORE_SOFTUPDATE_DOWNLOADED = "MAIN_MORE_SOFTUPDATE_DOWNLOADED";

    public final static String SP_SCREENDENSITY_KEY = "SP_SCREENDENSITY_KEY";

    // 版本更新
    private static final String MAIN_MORE_SOFTUPDATE_TITLE = "software_title";
    private static final String MAIN_MORE_SOFTUPDATE_PACK = "software_pack";
    private static final String MAIN_MORE_SOFTUPDATE_INFO = "software_info";
    private static final String MAIN_MORE_SOFTUPDATE_VERSION = "software_versionName";
    private static final String MAIN_MORE_SOFTUPDATE_CODE = "software_versionCode";
    private static final String MAIN_MORE_SOFTUPDATE_APK_URL = "software_apkUrl";
    private static final String MAIN_MORE_SOFTUPDATE_APK_SIZE = "software_apkSize";
    private static final String MAIN_MORE_SOFTUPDATE_FORCE_UPDATE = "software_force_update";

    // 应用设置
    private static final String APPLY_SETTING_SHARE = "apply_setting_share";
    private static final String APPLY_SETTING_BACKUP = "apply_setting_backup";
    private static final String APPLY_SETTING_NOTIFI = "apply_setting_notifi";
    private static final String APPLY_SETTING_UPDATE = "apply_setting_update";
    private static final String APPLY_SETTING_EXIT = "apply_setting_exit";
    private static final String APPLY_SETTING_MSG = "apply_setting_msg";
    private static final String APPLY_SETTING_WIFI = "apply_setting_wifi";

    private static final String CREATE_INSTALL_SHORTCUT = "create_install_shortcut";
    private static final String USE_UMENG = "use_Umeng";

    private static final String PC_RECOMMEND = "pc_recommend";// pc推手机

    private static final String SHOWGUIDE = "show_guide_6";// 是否显示引导

    // 分享内容
    private static final String SHARE_MSG_ID = "share_msg_id";
    private static final String SHARE_MSG_CODE = "share_msg_code";
    private static final String SHARE_MSG_USERID = "share_msg_userid";
    private static final String SHARE_MSG_CONTENT = "share_msg_content";
    private static final String SHARE_MSG_APP_URL = "share_msg_app_url";
    private static final String SHARE_MSG_IMAGE_URL = "share_msg_image_url";
    private static final String SHARE_USER_NAME = "share_user_name";

    private static final String SHUTDOWN_PC_TIME = "shutdown_pc_time";

    public static String SP_NAME_MAIN = "MYHOMEPAGE";
    public static String APP_BACKGROUND = "BACKGROUND";
    public static String APP_BACKGROUND_OUT = "BACKGROUND_OUT";
    public static String IS_BACKGROUND_OUT = "IS_BACKGROUND_OUT";
    public static String FIRST_RUN = "FIRSTRUN";
    public static String IS_REMEMBER_PASSWORD = "IS_REMEMBER_PASSWORD";
    public static String REMEMBER_PASSWORD = "REMEMBER_PASSWORD";
    public static String REMEMBER_USERNAME = "REMEMBER_USERNAME";

    public static String DATEUPDATED = "DATEUPDATED";

    public static int NO_BACKGROUND = 0;
    public static String NO_BACKGROUND_OUT = "";

    private static final String SHARE_USERID = "share_userid";
    private static final String SHARE_USER_TOKEN = "share_user_token";


    public static void saveIsBackGroundOut(Context context, boolean isBackgroundOut) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(IS_BACKGROUND_OUT, isBackgroundOut);
        editor.commit();
    }

    public static boolean getIsBackGroundsOut(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        return sp.getBoolean(IS_BACKGROUND_OUT, false);
    }

    public static void saveBackGround(Context context, int resId) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(APP_BACKGROUND, resId);
        editor.commit();
    }

    public static int getBackGrounds(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        return sp.getInt(APP_BACKGROUND, NO_BACKGROUND);
    }

    public static void saveBackGroundOut(Context context, String filePath) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(APP_BACKGROUND_OUT, filePath);
        editor.commit();
    }

    public static String getBackGroundsOut(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        return sp.getString(APP_BACKGROUND_OUT, NO_BACKGROUND_OUT);
    }

    public static boolean getFirstRun(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        return sp.getBoolean(FIRST_RUN, true);
    }

    public static void setNoFirstRun(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(FIRST_RUN, false);
        editor.commit();
    }

    /**
     * 保存屏幕密度到share，以便widget里面使用
     */
    public static void storeScreenDensity(Context context, float density) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat(SP_SCREENDENSITY_KEY, density);
        editor.commit();

    }

    /**
     * 设置用户id
     *
     * @param context
     */
    public static void setUsersId(Context context, String userId) {
        if (!StringUtil.isNotNull(userId)) {
            return;
        }
        try {
            SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(SHARE_USERID, userId);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置用户id
     *
     * @param context
     */
    public static void clearUsersId(Context context) {
        try {
            SharedPreferences sp = context
                    .getSharedPreferences(SP_NAME_MAIN, 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(SHARE_USERID, "");
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到用户id
     *
     * @param context
     */
    public static String getUsersId(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, 0);
        return sp.getString(SHARE_USERID, "");
    }


    public static void setAutoToken(Context context, String autoToken) {
        if (!StringUtil.isNotNull(autoToken)) {
            return;
        }
        try {
            SharedPreferences sp = context
                    .getSharedPreferences(SP_NAME_MAIN, 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(SHARE_USER_TOKEN, autoToken);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearAutoToken(Context context) {
        try {
            SharedPreferences sp = context
                    .getSharedPreferences(SP_NAME_MAIN, 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(SHARE_USER_TOKEN, "");
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAutoToken(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, 0);
        return sp.getString(SHARE_USER_TOKEN, "");
    }

    /**
     * 是否记住密码
     *
     * @param context
     * @return
     */
    public static boolean getRememberPass(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        return sp.getBoolean(IS_REMEMBER_PASSWORD, false);
    }


    /**
     * 是否记住密码
     *
     * @param context
     * @param flag
     */
    public static void setRememberPass(Context context, boolean flag) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(IS_REMEMBER_PASSWORD, flag);
        editor.commit();
    }

    /**
     * 是否记住密码
     *
     * @param context
     * @return
     */
    public static String getUsername(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        return sp.getString(REMEMBER_USERNAME, "");
    }

    /**
     * 记住用户名
     *
     * @param context
     * @param username
     */
    public static void setUername(Context context, String username) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(REMEMBER_USERNAME, username);
        editor.commit();
    }

    /**
     * 密码
     *
     * @param context
     * @return
     */
    public static String getPassword(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        return sp.getString(REMEMBER_PASSWORD, "");
    }

    /**
     * 密码
     *
     * @param context
     * @param password
     */
    public static void setPassword(Context context, String password) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(REMEMBER_PASSWORD, password);
        editor.commit();
    }

    /**
     * 导航同步时间戳
     *
     * @param context
     * @return
     */
    public static String getDateUpdated(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        return sp.getString(DATEUPDATED, "");
    }

    /**
     * 导航同步时间戳
     *
     * @param context
     * @param time
     */
    public static void setDateUpdated(Context context, String time) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(DATEUPDATED, time);
        editor.commit();
    }


}
