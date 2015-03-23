package com.txnetwork.mypage.datacenter;

import android.content.ContentResolver;
import android.content.ContentValues;
import com.txnetwork.mypage.utils.TxNetwork;
import com.txnetwork.mypage.utils.TxNetworkUtil;

/**
 * Created by hcz on 2015/2/6.
 */
public class OperateDBUtils {

    /**
     * 添加新导航到本地数据库
     *
     * @param resolver
     * @param url
     * @param name
     * @param type
     * @param order
     */
    public static void insertNewUrl(ContentResolver resolver, String url, String name, int type, int order) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TxNetwork.URL, url);
        contentValues.put(TxNetwork.NAME, name);
        contentValues.put(TxNetwork.ICON, TxNetworkUtil.getUrlIcon(url));
        contentValues.put(TxNetwork.TYPE, type);
        contentValues.put(TxNetwork.ORDER, order);
        resolver.insert(TxNetwork.CONTENT_URL_URI, contentValues);
    }

    /**
     * 添加新分类到本地数据库
     *
     * @param resolver
     * @param typeName
     * @param type
     * @param order
     */
    public static void insertNewType(ContentResolver resolver, String typeName, int type, int order, int isExpanded) {
        ContentValues typeValues = new ContentValues();
        typeValues.put(TxNetwork.TYPENAME, typeName);
        typeValues.put(TxNetwork.TYPE, type);
        typeValues.put(TxNetwork.ISEXPANDED, isExpanded);
        typeValues.put(TxNetwork.ORDER, order);
        resolver.insert(TxNetwork.CONTENT_TYPE_URI, typeValues);
    }
}
