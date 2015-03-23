package com.txnetwork.mypage.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.View;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.entity.DiySkin;
import com.txnetwork.mypage.entity.DownloadedSkin;

import java.io.File;

/**
 * Created by Administrator on 2015/1/14.
 */
public class SkinUtils {

    public static final int UPLOADED_SUC = 0x123;
    public static final int UPLOADED_FAIL = 0x124;
    public static final int NOT_EXISTS = 0x125;

    public static void setSkin(Context context, View background) {
        int drawableId;
        String filePath;
        boolean isBackgroundOut;
        isBackgroundOut = SharedUtils.getIsBackGroundsOut(context);
        if (isBackgroundOut) {
            filePath = SharedUtils.getBackGroundsOut(context);
            Uri uri = Uri.fromFile(new File(filePath));
            Bitmap bmpBj = ImageLoader.getInstance().loadImageSync(uri.toString());
            if (bmpBj == null) {
                background.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.default_background));
                SharedUtils.saveBackGround(context, R.drawable.default_background);
                SharedUtils.saveIsBackGroundOut(context, false);
            } else {
                BitmapDrawable bdBj = new BitmapDrawable(bmpBj);
                background.setBackgroundDrawable(bdBj);
            }
        } else {
            drawableId = SharedUtils.getBackGrounds(context);
            if (drawableId != SharedUtils.NO_BACKGROUND) {
                background.setBackgroundDrawable(context.getResources().getDrawable(drawableId));
            } else {
                background.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.default_background));
                SharedUtils.saveBackGround(context, R.drawable.default_background);
                SharedUtils.saveIsBackGroundOut(context, false);
            }
        }
    }

    /**
     * 添加本地已下载皮肤表
     *
     * @param filePath
     * @param themeName
     * @param size
     */
    public static void addNewDownloadedTheme(Context context, String filePath, String themeName, int size, String picid) {
        ContentResolver resolver = context.getContentResolver();

        if (!downloadedThemeExists(resolver, filePath)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(TxNetwork.PICDIR, filePath);
            contentValues.put(TxNetwork.PICID, picid);
            contentValues.put(TxNetwork.ISLOCALTHEME, DownloadedSkin.FALSE);
            contentValues.put(TxNetwork.NAME, themeName);
            contentValues.put(TxNetwork.SIZE, size);
            resolver.insert(TxNetwork.CONTENT_DOWNLOADED_THEME_URI, contentValues);
        } else {
            //本地已存在这套主题皮肤,无需重复添加
        }

    }

    /**
     * 检查本地是否已有皮肤
     *
     * @param resolver
     * @param filePath
     * @return
     */
    private static boolean downloadedThemeExists(ContentResolver resolver, String filePath) {
        Cursor cursor = resolver.query(TxNetwork.CONTENT_DOWNLOADED_THEME_URI, new String[]{TxNetwork.PICDIR}, null, null, null);
        while (cursor.moveToNext()) {
            if (filePath.equals(cursor.getString(cursor.getColumnIndex(TxNetwork.PICDIR)))) {
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public static void deleteDownloadedSkin(Context context, String picdir) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(TxNetwork.CONTENT_DOWNLOADED_THEME_URI, TxNetwork.PICDIR + "=?", new String[]{picdir});
    }

    /**
     * 添加到本地自定义皮肤表
     *
     * @param context
     * @param picPath
     */
    public static void insertDiyTheme(Context context, String picPath) {

        ContentResolver contentResolver = context.getContentResolver();

        if (NOT_EXISTS == diyThemeCheck(context, picPath)) {
            File picFile = new File(picPath);
            int fileSize = (int) (picFile.length() / 1024);//KB
            String fileName = picFile.getName().substring(0, picFile.getName().indexOf("."));

            ContentValues contentValues = new ContentValues();
            contentValues.put(TxNetwork.PICDIR, picPath);
            contentValues.put(TxNetwork.NAME, fileName);
            contentValues.put(TxNetwork.SIZE, fileSize);
            contentValues.put(TxNetwork.UPLOADED, DiySkin.FALSE);
            contentValues.put(TxNetwork.DATECREATED, System.currentTimeMillis());
            contentValues.put(TxNetwork.USERID, TxNetworkUtil.getUserId(context));
            contentResolver.insert(TxNetwork.CONTENT_DIY_THEME_URI, contentValues);
        } else {
            //本地已有自定义
        }
    }

    /**
     * 检查本地是否已有皮肤
     *
     * @param context
     * @param filePath
     * @return
     */
    public static int diyThemeCheck(Context context, String filePath) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(TxNetwork.CONTENT_DIY_THEME_URI, new String[]{TxNetwork.PICDIR, TxNetwork.UPLOADED}, null, null, null);
        while (cursor.moveToNext()) {
            if (filePath.equals(cursor.getString(cursor.getColumnIndex(TxNetwork.PICDIR)))) {
                if (DiySkin.TRUE.equals(cursor.getString(cursor.getColumnIndex(TxNetwork.UPLOADED)))) {
                    //有,并且上传成功
                    return UPLOADED_SUC;
                } else {
                    //有,但是为上传成功
                    return UPLOADED_FAIL;
                }
            }
        }
        cursor.close();
        return NOT_EXISTS;
    }

    /**
     * 更新
     *
     * @param context
     * @param picdir
     * @param userid
     */
    public static void updateDiyTheme(Context context, String picdir, String picid, String userid, String flag) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TxNetwork.UPLOADED, flag);
        contentValues.put(TxNetwork.PICID, picid);
        contentValues.put(TxNetwork.USERID, userid);
        contentResolver.update(TxNetwork.CONTENT_DIY_THEME_URI, contentValues, TxNetwork.PICDIR + "=?", new String[]{picdir});
    }

    /**
     * 删除本地自定义皮肤
     *
     * @param context
     * @param picdir
     */
    public static void deleteDiySkin(Context context, String picdir) {
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(TxNetwork.CONTENT_DIY_THEME_URI, TxNetwork.PICDIR + "=?", new String[]{picdir});
    }


}
