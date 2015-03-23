package com.txnetwork.mypage.jsonparser;

import android.content.ContentResolver;
import android.content.Context;
import android.os.Handler;
import com.txnetwork.mypage.activity.MainActivity;
import com.txnetwork.mypage.datacenter.OperateDBUtils;
import com.txnetwork.mypage.utils.LogUtils;
import com.txnetwork.mypage.utils.TxNetwork;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NavInfoParserModel {
    protected static final String TAG = NavInfoParserModel.class.getSimpleName();

    public static void parseMainJson(final Context context,
                                     final Handler handler, final Object object, final int source,
                                     final int fail) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(String.valueOf(object));
                    int errorCode = jsonObject.getInt("errorCode");
                    if (1 == errorCode) {
                        if (jsonObject.has("resultlist")) {
                            ContentResolver resolver = context.getContentResolver();
                            resolver.delete(TxNetwork.CONTENT_TYPE_URI, null, null);
                            resolver.delete(TxNetwork.CONTENT_URL_URI, null, null);
                            JSONObject resultList = jsonObject.getJSONObject("resultlist");
                            JSONArray navInfoArray = new JSONArray(resultList.getString("navinfostr"));

                            if (navInfoArray != null && navInfoArray.length() > 0) {
//                                long dateupfated = Long.parseLong(resultList.getString("dateupdated"));
//                                Date date = new Date(dateupfated);
//                                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//设置日期格式
//                                String timestamp = df.format(date);
                                //SharedUtils.setDateUpdated(context, timestamp);//更新当前的时间戳
                                for (int i = 0; i < navInfoArray.length(); i++) {
                                    JSONObject urlJson = navInfoArray.getJSONObject(i);
                                    String typeName = urlJson.getString("sections");
                                    int isExpanded = urlJson.getInt("isexpanded");

                                    int type = i;//根据序号设定类型字段
                                    OperateDBUtils.insertNewType(resolver, typeName, type, i, isExpanded);

                                    JSONArray thisTypeUrlList = new JSONArray(urlJson.getString("items"));
                                    if (thisTypeUrlList != null && thisTypeUrlList.length() > 0) {
                                        for (int j = 0; j < thisTypeUrlList.length(); j++) {
                                            JSONObject url = thisTypeUrlList.getJSONObject(j);
                                            String title = url.getString("title");
                                            String site = url.getString("site");
                                            String icon = url.getString("icon");

                                            OperateDBUtils.insertNewUrl(resolver, site, title, type, j);//添加新导航
                                        }
                                    }
                                }
                                handler.sendEmptyMessage(source);
                            } else {
                                handler.sendEmptyMessage(fail);
                            }
                        } else {
                            //本地时间戳是最新的
                            if (MainActivity.autoLogin) {
                                handler.sendEmptyMessage(source);
                            }
                        }
                    } else if (1007 == errorCode) {
                        handler.sendMessage(handler.obtainMessage(fail, jsonObject.getString("errorMsg")));
                    } else {
                        handler.sendEmptyMessage(fail);
                    }
                } catch (JSONException e) {
                    LogUtils.LOGE(TAG, "parseMainJson==JSONException=" + e.getMessage());
                    handler.sendEmptyMessage(fail);
                }
            }
        }).start();
    }
}
