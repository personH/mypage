package com.txnetwork.mypage.jsonparser;

import android.content.Context;
import android.os.Handler;
import com.txnetwork.mypage.entity.SkinInfo;
import com.txnetwork.mypage.utils.LogUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangrui Create at 2014-7-10
 * @ClassName EntranceParserModel
 * @description
 */
public class ThemeListParserModel {
    protected static final String TAG = ThemeListParserModel.class.getSimpleName();

    /**
     * @param @param  strResult
     * @param @return 参数
     * @return Map<Entrance,Integer> 返回类型
     * @throws
     * @Title: parseJson
     * @Description: 将获取到的jason对象解析为热点列表集合
     */
    public static void parseMainJson(final Context context,
                                     final Handler handler, final String strResult, final int source,
                                     final int fail) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    JSONObject jsonObject = new JSONObject(strResult);

                    List<SkinInfo> themeTabList = new ArrayList<SkinInfo>();

                    int errorCode = jsonObject.getInt("errorCode");
                    if (1 == errorCode) {
                        JSONArray resultList = jsonObject.getJSONArray("resultList");
                        if (resultList != null && resultList.length() > 0) {
                            for (int i = 0; i < resultList.length(); i++) {
                                SkinInfo skinInfo = new SkinInfo(resultList.getJSONObject(i).getString("picid"),
                                        resultList.getJSONObject(i).getString("picname"),
                                        resultList.getJSONObject(i).getString("newname"),
                                        resultList.getJSONObject(i).getInt("pictype"),
                                        resultList.getJSONObject(i).getString("suffix"),
                                        resultList.getJSONObject(i).getInt("picsize"),
                                        resultList.getJSONObject(i).getString("picpath"),
                                        resultList.getJSONObject(i).getString("picsrc"),
                                        resultList.getJSONObject(i).getString("datecreated"));
                                themeTabList.add(skinInfo);
                            }
                            handler.sendMessage(handler.obtainMessage(source, themeTabList));
                        } else {
                            handler.sendMessage(handler.obtainMessage(source, themeTabList));
                        }
                    } else if (1007 == errorCode) {
                        handler.sendMessage(handler.obtainMessage(fail, jsonObject.getString("errorMsg")));
                    } else {
                        handler.sendEmptyMessage(fail);
                    }
                } catch (JSONException e) {
                    LogUtils.LOGE(TAG,
                            "parseMainJson==JSONException=" + e.getMessage());
                    handler.sendEmptyMessage(fail);
                }
            }
        }).start();
    }
}
