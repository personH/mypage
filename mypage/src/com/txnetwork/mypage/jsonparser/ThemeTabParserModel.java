package com.txnetwork.mypage.jsonparser;

import android.content.Context;
import android.os.Handler;
import com.txnetwork.mypage.datacenter.UserOperate;
import com.txnetwork.mypage.entity.SkinTab;
import com.txnetwork.mypage.entity.UserInfo;
import com.txnetwork.mypage.utils.LogUtils;
import com.txnetwork.mypage.utils.StringUtil;
import com.txnetwork.mypage.utils.TxNetworkPool;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangrui Create at 2014-7-10
 * @ClassName EntranceParserModel
 * @description TODO(描述这个类的作用)
 */
public class ThemeTabParserModel {
    protected static final String TAG = ThemeTabParserModel.class.getSimpleName();

    /**
     * @param @param  strResult
     * @param @return 参数
     * @return Map<Entrance,Integer> 返回类型
     * @throws
     * @Title: parseJson
     * @Description: 将获取到的jason对象解析为热点列表集合
     */
    public static void parseJson(final Context context, final Handler handler,
                                 final String strResult) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(strResult);

                    int errorCode = jsonObject.getInt("errorCode");
                    if (1 == errorCode) {
                        String secretkey = jsonObject.getString("secretkey");
                        if (StringUtil.isNotNull(secretkey)) {
                            boolean flag = TxNetworkPool.CBCDecryptTx(secretkey);
                            LogUtils.LOGI(TAG, "解密前==》" + flag);
                            if (flag) {
                                UserInfo userInfo = new UserInfo();
                                String localkey = TxNetworkPool.GetLocalKey();
                                userInfo.setKeyfield(localkey);
                                if (null != userInfo) {
                                    UserOperate.getInstance(context)
                                            .insertUser(userInfo);
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

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

                    List<SkinTab> skinTabList = new ArrayList<SkinTab>();

                    int errorCode = jsonObject.getInt("errorCode");
                    if (1 == errorCode) {
                        JSONArray resultList = jsonObject.getJSONArray("resultList");

                        if (resultList != null && resultList.length() > 0) {
                            for (int i = 0; i < resultList.length(); i++) {
                                SkinTab skinTab = new SkinTab(resultList.getJSONObject(i).getInt("tabid"),
                                        resultList.getJSONObject(i).getString("tabname"),
                                        resultList.getJSONObject(i).getInt("tabtype"),
                                        resultList.getJSONObject(i).getInt("sort"),
                                        resultList.getJSONObject(i).getString("datecreated"));
                                skinTabList.add(skinTab);
                            }
                            handler.sendMessage(handler.obtainMessage(source, skinTabList));
                        } else {
                            handler.sendEmptyMessage(fail);
                        }
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
