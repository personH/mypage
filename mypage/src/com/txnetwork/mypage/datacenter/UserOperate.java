package com.txnetwork.mypage.datacenter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.txnetwork.mypage.entity.UserInfo;
import com.txnetwork.mypage.utils.LogUtils;
import com.txnetwork.mypage.utils.TxNetwork;

public class UserOperate {
    private static final String TAG = UserOperate.class.getSimpleName();

    private static UserOperate instance = null;
    private Context mContext;
    private ContentResolver contentResolver;

    public static UserOperate getInstance(Context context) {
        if (instance == null) {
            instance = new UserOperate(context);
        }
        return instance;
    }

    private UserOperate(Context context) {
        mContext = context;
        contentResolver = mContext.getContentResolver();
    }

    public int insertUser(UserInfo info) {
        if (null == info) {
            return -1;
        }
        LogUtils.LOGI(TAG, "===insertUser===" + info.getKeyfield());
        deleteUser();
        ContentValues values = new ContentValues();
        values.put(TxNetwork.USERID, info.getUserId());
        values.put(TxNetwork.KEYFIELD, info.getKeyfield());
        values.put(TxNetwork.CURRENT_TIME, System.currentTimeMillis());
        values.put(TxNetwork.EXI1, info.getExi1());
        values.put(TxNetwork.EXI2, info.getExi2());
        values.put(TxNetwork.EXI3, info.getExi3());

        Uri newUri = contentResolver.insert(TxNetwork.CONTENT_USER_URI, values);
        if (newUri == null) {
            LogUtils.LOGI("insertSingleParkInfo", "Uri is null");
            return -1;
        } else {
            String uriString = newUri.toString();
            return Integer.parseInt(uriString.substring(uriString.lastIndexOf("/") + 1));
        }
    }

    public UserInfo queryUser() {
        LogUtils.LOGI(TAG, "===queryUser===");
        Cursor cursor = contentResolver.query(TxNetwork.CONTENT_USER_URI, null, null, null, TxNetwork.CURRENT_TIME + " asc limit 1 offset 0");
        if (null != cursor && cursor.getCount() > 0) {
            UserInfo info = new UserInfo();
            while (cursor.moveToNext()) {
                info.setUserId(cursor.getString(cursor
                        .getColumnIndex(TxNetwork.USERID)));
                info.setKeyfield(cursor.getString(cursor
                        .getColumnIndex(TxNetwork.KEYFIELD)));
                info.setCurrentTime(cursor.getLong(cursor
                        .getColumnIndex(TxNetwork.CURRENT_TIME)));
                info.setExi1(cursor.getString(cursor
                        .getColumnIndex(TxNetwork.EXI1)));
                info.setExi2(cursor.getString(cursor
                        .getColumnIndex(TxNetwork.EXI2)));
                info.setExi3(cursor.getString(cursor
                        .getColumnIndex(TxNetwork.EXI3)));
            }
            cursor.close();
            return info;
        }
        if (null != cursor) {
            cursor.close();
        }
        return null;
    }

    /**
     * 删除用户信息
     *
     * @return
     */
    public boolean deleteUser() {
        int row = contentResolver.delete(TxNetwork.CONTENT_USER_URI, null, null);
        if (row == -1) {
            return false;
        }
        return true;
    }

    public boolean isExistUser() {
        Cursor cursor = contentResolver.query(TxNetwork.CONTENT_USER_URI, null,
                null, null, null);
        if (null != cursor && cursor.getCount() > 0) {
            LogUtils.LOGI(TAG, "===isExistUser===" + cursor.getCount());
            cursor.close();
            return true;
        }
        if (null != cursor) {
            cursor.close();
        }
        return false;
    }

}
