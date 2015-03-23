package com.txnetwork.mypage.datacenter;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.txnetwork.mypage.utils.TxNetwork;

/**
 * Created by hcz on 2014/12/31.
 * <p/>
 * 此类主要用于方便数据库操作,以及实现数据的共享
 */
public class MyProvider extends ContentProvider {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private Context mContext = null;

    private static final UriMatcher sMatcher;

    static {
        sMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        sMatcher.addURI(TxNetwork.AUTOHORITY, TxNetwork.URL_TABLE, TxNetwork.URL_ITEM);
        sMatcher.addURI(TxNetwork.AUTOHORITY, TxNetwork.URL_TABLE + "/#", TxNetwork.URL_ITEM_ID);

        sMatcher.addURI(TxNetwork.AUTOHORITY, TxNetwork.TYPE_TABLE, TxNetwork.TYPE_ITEM);
        sMatcher.addURI(TxNetwork.AUTOHORITY, TxNetwork.TYPE_TABLE + "/#", TxNetwork.TYPE_ITEM_ID);

        sMatcher.addURI(TxNetwork.AUTOHORITY, TxNetwork.DOWNLOADED_THEME_TABLE, TxNetwork.DOWNLOADED_THEME_ITEM);
        sMatcher.addURI(TxNetwork.AUTOHORITY, TxNetwork.DOWNLOADED_THEME_TABLE + "/#", TxNetwork.DOWNLOADED_THEME_ITEM_ID);

        sMatcher.addURI(TxNetwork.AUTOHORITY, TxNetwork.USER_TABLE, TxNetwork.USER_ITEM);
        sMatcher.addURI(TxNetwork.AUTOHORITY, TxNetwork.USER_TABLE + "/#", TxNetwork.URL_ITEM_ID);

        sMatcher.addURI(TxNetwork.AUTOHORITY, TxNetwork.DIY_THEME_TABLE, TxNetwork.DIY_THEME_ITEM);
        sMatcher.addURI(TxNetwork.AUTOHORITY, TxNetwork.DIY_THEME_TABLE + "/#", TxNetwork.DIY_THEME_ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        mContext = this.getContext();
        //创建数据库
        this.databaseHelper = new DatabaseHelper(mContext);
        return true;
    }


    /**
     * 该方法用于返回当前Url所代表数据的MIME类型。
     * 如果操作的数据属于集合类型，那么MIME类型字符串应该以vnd.android.cursor.dir/开头，
     * 例如：要得到所有person记录的Uri为content://com.txnetwork.homepage/person，
     * 那么返回的MIME类型字符串应该为："vnd.android.cursor.dir/person"。
     * 如果要操作的数据属于非集合类型数据，那么MIME类型字符串应该以vnd.android.cursor.item/开头，
     * 例如：得到id为10的person记录，Uri为content://com.txnetwork.homepage/person/10，
     * 那么返回的MIME类型字符串为："vnd.android.cursor.item/person"。
     *
     * @param uri
     * @return
     */
    @Override
    public String getType(Uri uri) {
        switch (sMatcher.match(uri)) {
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = databaseHelper.getWritableDatabase();
        int count = 0;
        switch (sMatcher.match(uri)) {
            case TxNetwork.URL_ITEM:
                count = db.delete(TxNetwork.URL_TABLE, selection, selectionArgs);
                break;
            case TxNetwork.URL_ITEM_ID:
                String id = uri.getPathSegments().get(1);
                count = db.delete(TxNetwork._ID, TxNetwork._ID + "=" + id + (!TextUtils.isEmpty(TxNetwork._ID = "?") ? "AND(" + selection + ')' : ""), selectionArgs);
                break;
            case TxNetwork.DOWNLOADED_THEME_ITEM:
                count = db.delete(TxNetwork.DOWNLOADED_THEME_TABLE, selection, selectionArgs);
                break;
            case TxNetwork.USER_ITEM:
                count = db.delete(TxNetwork.USER_TABLE, selection, selectionArgs);
                break;
            case TxNetwork.DIY_THEME_ITEM://删除本地自定义皮肤
                count = db.delete(TxNetwork.DIY_THEME_TABLE, selection, selectionArgs);
                break;
            case TxNetwork.TYPE_ITEM://删除分类
                count = db.delete(TxNetwork.TYPE_TABLE, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        mContext.getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db = databaseHelper.getWritableDatabase();
        long rowId;
        switch (sMatcher.match(uri)) {
            case TxNetwork.URL_ITEM://url
                rowId = db.insert(TxNetwork.URL_TABLE, TxNetwork._ID, values);
                break;
            case TxNetwork.TYPE_ITEM://type
                rowId = db.insert(TxNetwork.TYPE_TABLE, TxNetwork._ID, values);
                break;
            case TxNetwork.DOWNLOADED_THEME_ITEM://theme
                rowId = db.insert(TxNetwork.DOWNLOADED_THEME_TABLE, TxNetwork._ID, values);
                break;
            case TxNetwork.USER_ITEM://user
                rowId = db.insert(TxNetwork.USER_TABLE, TxNetwork._ID, values);
                break;
            case TxNetwork.DIY_THEME_ITEM://diy_theme
                rowId = db.insert(TxNetwork.DIY_THEME_TABLE, TxNetwork._ID, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(TxNetwork.CONTENT_URL_URI, rowId);
            mContext.getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }
        throw new IllegalArgumentException("Unknown URI" + uri);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        db = databaseHelper.getWritableDatabase();
        Cursor c;
        Log.d("-------", String.valueOf(sMatcher.match(uri)));
        switch (sMatcher.match(uri)) {
            case TxNetwork.URL_ITEM:
                c = db.query(TxNetwork.URL_TABLE, projection, selection, selectionArgs, null, null, null);
                break;
            case TxNetwork.URL_ITEM_ID:
                String urlId = uri.getPathSegments().get(1);
                c = db.query(TxNetwork.URL_TABLE, projection, TxNetwork._ID + "=" + urlId + (!TextUtils.isEmpty(selection) ? "AND(" + selection + ')' : ""), selectionArgs, null, null, sortOrder);
                break;
            case TxNetwork.TYPE_ITEM:
                c = db.query(TxNetwork.TYPE_TABLE, projection, selection, selectionArgs, null, null, null);
                break;
            case TxNetwork.TYPE_ITEM_ID:
                String typeId = uri.getPathSegments().get(1);
                c = db.query(TxNetwork.TYPE_TABLE, projection, TxNetwork._ID + "=" + typeId + (!TextUtils.isEmpty(selection) ? "AND(" + selection + ')' : ""), selectionArgs, null, null, sortOrder);
                break;
            case TxNetwork.DOWNLOADED_THEME_ITEM:
                c = db.query(TxNetwork.DOWNLOADED_THEME_TABLE, projection, selection, selectionArgs, null, null, null);
                break;
            case TxNetwork.USER_ITEM:
                c = db.query(TxNetwork.USER_TABLE, projection, selection, selectionArgs, null, null, null);
                break;
            case TxNetwork.DIY_THEME_ITEM:
                c = db.query(TxNetwork.DIY_THEME_TABLE, projection, selection, selectionArgs, null, null, null);
                break;
            default:
                Log.d("!!!!!!", "Unknown URI" + uri);
                throw new IllegalArgumentException("Unknown URI" + uri);
        }
        c.setNotificationUri(mContext.getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String whereClause, String[] whereArgs) {
        db = databaseHelper.getWritableDatabase();
        int count = 0;
        Log.d("-------", String.valueOf(sMatcher.match(uri)));
        switch (sMatcher.match(uri)) {
            case TxNetwork.DIY_THEME_ITEM:
                count = db.update(TxNetwork.DIY_THEME_TABLE, values, whereClause, whereArgs);
                break;
        }
        return count;
    }
}