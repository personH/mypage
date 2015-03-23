package com.txnetwork.mypage.datacenter;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.entity.DownloadedSkin;
import com.txnetwork.mypage.utils.TxNetwork;

/**
 * Created by hcz on 2014/12/31.
 * <p/>
 * 本地数据库,包含表有:
 * 1.用户表
 * 2.网址导航表
 * 3.网址类型表
 * 4.本地已下载皮肤表
 * 5.自定义皮肤表
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        //执行父类构造器,创建数据库,以及版本号
        super(context, TxNetwork.DBNAME, null, TxNetwork.VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 本地网址导航表
        db.execSQL("create table  IF NOT EXISTS " + TxNetwork.URL_TABLE + "(" +
                TxNetwork._ID + " integer primary key autoincrement not null," +
                TxNetwork.URL + " text not null," +
                TxNetwork.NAME + " text not null," +
                TxNetwork.ICON + " text," +
                TxNetwork.ORDER + " interger not null," +
                TxNetwork.TYPE + " interger not null);");

        //网址导航分类表
        db.execSQL("create table  IF NOT EXISTS " + TxNetwork.TYPE_TABLE + "(" +
                TxNetwork._ID + " integer primary key autoincrement not null," +
                TxNetwork.TYPENAME + " text not null," +
                TxNetwork.ORDER + " interger not null," +
                TxNetwork.ISEXPANDED + " interger not null," +
                TxNetwork.TYPE + " interger not null);");

        //主题皮肤表,已下载列表
        db.execSQL("create table  IF NOT EXISTS " + TxNetwork.DOWNLOADED_THEME_TABLE + "(" +
                TxNetwork._ID + " integer primary key autoincrement not null," +
                TxNetwork.DRAWABLEID + " integer," +
                TxNetwork.PICID + " text," +
                TxNetwork.PICDIR + " text," +
                TxNetwork.ISLOCALTHEME + " text not null," +
                TxNetwork.SIZE + " integer not null," +
                TxNetwork.NAME + " text not null);");

        //用户表
        db.execSQL("create table  IF NOT EXISTS " + TxNetwork.USER_TABLE + "(" +
                TxNetwork._ID + " integer primary key autoincrement not null," +
                TxNetwork.USERID + " text," +
                TxNetwork.KEYFIELD + " text not null," +
                TxNetwork.CURRENT_TIME + " long not null," +
                TxNetwork.EXI1 + " text," +
                TxNetwork.EXI2 + " text," +
                TxNetwork.EXI3 + " text);");

        //自定义皮肤本地图片表,在无网络时显示本地的
        db.execSQL("create table  IF NOT EXISTS " + TxNetwork.DIY_THEME_TABLE + "(" +
                TxNetwork._ID + " integer primary key autoincrement not null," +
                TxNetwork.USERID + " text," +
                TxNetwork.PICID + " text," +
                TxNetwork.PICDIR + " text not null," +
                TxNetwork.SIZE + " integer not null," +
                TxNetwork.DATECREATED + " long not null," +
                TxNetwork.UPLOADED + " text not null," +
                TxNetwork.NAME + " text not null);");

        //initUrlTable(db);

        //initTypeTable(db);

        initDownloadedThemeTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists " + TxNetwork.URL_TABLE);
            db.execSQL("drop table if exists " + TxNetwork.TYPE_TABLE);
            db.execSQL("drop table if exists " + TxNetwork.DOWNLOADED_THEME_TABLE);
            db.execSQL("drop table if exists " + TxNetwork.USER_TABLE);
            db.execSQL("drop table if exists " + TxNetwork.DIY_THEME_TABLE);

            onCreate(db);
        }
    }

    private void initDownloadedThemeTable(SQLiteDatabase db) {
        ContentValues contentValues8 = new ContentValues();
        contentValues8.put(TxNetwork.PICDIR, "");
        contentValues8.put(TxNetwork.PICID, "");
        contentValues8.put(TxNetwork.DRAWABLEID, R.drawable.bg_default);
        contentValues8.put(TxNetwork.ISLOCALTHEME, DownloadedSkin.TRUE);
        contentValues8.put(TxNetwork.NAME, "默认皮肤");
        contentValues8.put(TxNetwork.SIZE, 2);
        db.insert(TxNetwork.DOWNLOADED_THEME_TABLE, TxNetwork._ID, contentValues8);
    }

    private void initTypeTable(SQLiteDatabase db) {
        ContentValues contentValues5 = new ContentValues();
        contentValues5.put(TxNetwork.TYPENAME, "分类一");
        contentValues5.put(TxNetwork.TYPE, 0);
        contentValues5.put(TxNetwork.ISEXPANDED, 1);
        contentValues5.put(TxNetwork.ORDER, 0);

        ContentValues contentValues6 = new ContentValues();
        contentValues6.put(TxNetwork.TYPENAME, "分类二");
        contentValues6.put(TxNetwork.TYPE, 1);
        contentValues6.put(TxNetwork.ISEXPANDED, 1);
        contentValues6.put(TxNetwork.ORDER, 1);

        ContentValues contentValues7 = new ContentValues();
        contentValues7.put(TxNetwork.TYPENAME, "分类三");
        contentValues7.put(TxNetwork.TYPE, 2);
        contentValues7.put(TxNetwork.ISEXPANDED, 1);
        contentValues7.put(TxNetwork.ORDER, 2);

        db.insert(TxNetwork.TYPE_TABLE, TxNetwork._ID, contentValues5);
        db.insert(TxNetwork.TYPE_TABLE, TxNetwork._ID, contentValues7);
        db.insert(TxNetwork.TYPE_TABLE, TxNetwork._ID, contentValues6);
    }

    private void initUrlTable(SQLiteDatabase db) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TxNetwork.URL, "www.baidu.com");
        contentValues.put(TxNetwork.NAME, "百度");
        contentValues.put(TxNetwork.ORDER, R.drawable.unknown);
        contentValues.put(TxNetwork.TYPE, 0);

        ContentValues contentValues1 = new ContentValues();
        contentValues1.put(TxNetwork.URL, "www.weibo.com");
        contentValues1.put(TxNetwork.NAME, "微博");
        contentValues1.put(TxNetwork.ORDER, R.drawable.unknown);
        contentValues1.put(TxNetwork.TYPE, 1);

        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(TxNetwork.URL, "www.taobao.com");
        contentValues2.put(TxNetwork.NAME, "淘宝");
        contentValues2.put(TxNetwork.ORDER, R.drawable.unknown);
        contentValues2.put(TxNetwork.TYPE, 2);

        ContentValues contentValues3 = new ContentValues();
        contentValues3.put(TxNetwork.URL, "www.ifeng.com");
        contentValues3.put(TxNetwork.NAME, "凤凰");
        contentValues3.put(TxNetwork.ORDER, R.drawable.unknown);
        contentValues3.put(TxNetwork.TYPE, 0);

        ContentValues contentValues4 = new ContentValues();
        contentValues4.put(TxNetwork.URL, "www.qq.com");
        contentValues4.put(TxNetwork.NAME, "腾讯");
        contentValues4.put(TxNetwork.ORDER, R.drawable.unknown);
        contentValues4.put(TxNetwork.TYPE, 1);
        ContentValues contentValues5 = new ContentValues();
        contentValues5.put(TxNetwork.URL, "www.qq.com");
        contentValues5.put(TxNetwork.NAME, "腾讯");
        contentValues5.put(TxNetwork.ORDER, R.drawable.unknown);
        contentValues5.put(TxNetwork.TYPE, 1);
        ContentValues contentValues6 = new ContentValues();
        contentValues6.put(TxNetwork.URL, "www.qq.com");
        contentValues6.put(TxNetwork.NAME, "腾讯");
        contentValues6.put(TxNetwork.ORDER, R.drawable.unknown);
        contentValues6.put(TxNetwork.TYPE, 1);
        ContentValues contentValues7 = new ContentValues();
        contentValues7.put(TxNetwork.URL, "www.qq.com");
        contentValues7.put(TxNetwork.NAME, "腾讯");
        contentValues7.put(TxNetwork.ORDER, R.drawable.unknown);
        contentValues7.put(TxNetwork.TYPE, 1);
        ContentValues contentValues8 = new ContentValues();
        contentValues8.put(TxNetwork.URL, "www.qq.com");
        contentValues8.put(TxNetwork.NAME, "腾讯");
        contentValues8.put(TxNetwork.ORDER, R.drawable.unknown);
        contentValues8.put(TxNetwork.TYPE, 1);
        ContentValues contentValues9 = new ContentValues();
        contentValues9.put(TxNetwork.URL, "www.qq.com");
        contentValues9.put(TxNetwork.NAME, "腾讯");
        contentValues9.put(TxNetwork.ORDER, R.drawable.unknown);
        contentValues9.put(TxNetwork.TYPE, 1);
        ContentValues contentValues10 = new ContentValues();
        contentValues10.put(TxNetwork.URL, "www.qq.com");
        contentValues10.put(TxNetwork.NAME, "腾讯");
        contentValues10.put(TxNetwork.ORDER, R.drawable.unknown);
        contentValues10.put(TxNetwork.TYPE, 1);

        db.insert(TxNetwork.URL_TABLE, TxNetwork._ID, contentValues);
        db.insert(TxNetwork.URL_TABLE, TxNetwork._ID, contentValues1);
        db.insert(TxNetwork.URL_TABLE, TxNetwork._ID, contentValues2);
        db.insert(TxNetwork.URL_TABLE, TxNetwork._ID, contentValues3);
        db.insert(TxNetwork.URL_TABLE, TxNetwork._ID, contentValues4);

        db.insert(TxNetwork.URL_TABLE, TxNetwork._ID, contentValues5);
        db.insert(TxNetwork.URL_TABLE, TxNetwork._ID, contentValues6);
        db.insert(TxNetwork.URL_TABLE, TxNetwork._ID, contentValues7);
        db.insert(TxNetwork.URL_TABLE, TxNetwork._ID, contentValues8);
        db.insert(TxNetwork.URL_TABLE, TxNetwork._ID, contentValues9);
        db.insert(TxNetwork.URL_TABLE, TxNetwork._ID, contentValues10);
    }
}
