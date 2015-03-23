package com.txnetwork.mypage.utils;

import android.net.Uri;

/**
 * Created by Administrator on 2014/12/31.
 */
public class TxNetwork {

    public static final String DBNAME = "txnetworkdb";

    //表名
    public static final String URL_TABLE = "t_url";
    public static final String TYPE_TABLE = "t_type";
    public static final String DOWNLOADED_THEME_TABLE = "t_downloaded_theme";
    public static final String USER_TABLE = "t_user";
    public static final String DIY_THEME_TABLE = "t_diy_theme";
    public static final int VERSION = 1;

    //各表各字段
    public static String _ID = "_id";
    public static final String URL = "url";
    public static final String NAME = "name";
    public static final String ICON = "icon";
    public static final String ORDER = "px";
    public static final String TYPE = "type";
    public static final String TYPENAME = "typename";
    public static final String DRAWABLEID = "drawableid";
    public static final String STYLEID = "styleid";
    public static final String PICDIR = "picdir";
    public static final String ISLOCALTHEME = "islocaltheme";
    public static final String SIZE = "size";
    public static final String ISEXPANDED = "isexpanded";

    //用户表
    public static final String USERID = "USERID";
    public static final String KEYFIELD = "KEYFIELD";
    public static final String CURRENT_TIME = "CURRENT_TIME";
    public static final String EXI1 = "EXI1";
    public static final String EXI2 = "EXI2";
    public static final String EXI3 = "EXI3";

    //本地自定义皮肤表
    public static final String DATECREATED = "DATECREATED";//创建时间
    public static final String UPLOADED = "UPLOADED";//是否已上传
    public static final String PICID = "PICID";//上传成功,返回的picid

    public static final String AUTOHORITY = "com.txnetwork.homepage";

    public static final int URL_ITEM = 1;
    public static final int URL_ITEM_ID = 2;
    public static final int TYPE_ITEM = 3;
    public static final int TYPE_ITEM_ID = 4;
    public static final int DOWNLOADED_THEME_ITEM = 5;
    public static final int DOWNLOADED_THEME_ITEM_ID = 6;
    public static final int USER_ITEM = 7;
    public static final int USER_ITEM_ID = 8;
    public static final int DIY_THEME_ITEM = 9;
    public static final int DIY_THEME_ITEM_ID = 10;

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/table";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/table";

    public static final Uri CONTENT_URL_URI = Uri.parse("content://" + AUTOHORITY + "/t_url");
    public static final Uri CONTENT_TYPE_URI = Uri.parse("content://" + AUTOHORITY + "/t_type");
    public static final Uri CONTENT_DOWNLOADED_THEME_URI = Uri.parse("content://" + AUTOHORITY + "/t_downloaded_theme");
    public static final Uri CONTENT_USER_URI = Uri.parse("content://" + AUTOHORITY + "/t_user");
    public static final Uri CONTENT_DIY_THEME_URI = Uri.parse("content://" + AUTOHORITY + "/t_diy_theme");

    public static final String DOWNLOAD_SKIN_FILE_PATH = "/sdcard/Download/skin.zip";
    public static final String PACKAGE_SKIN_FILE_PATH = "/data/data/com.txnetwork.mypage/Skin_kris/";
    public static final String PACKAGE_BASE_SKIN_FILE_PATH = TxNetwork.PACKAGE_SKIN_FILE_PATH + "/skin/";

    public static final String APP_DOWNLOADFILE_PATH = "/sdcard/mypage/";

    public static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }

}
