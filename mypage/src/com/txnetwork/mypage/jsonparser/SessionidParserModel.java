package com.txnetwork.mypage.jsonparser;

import android.content.Context;
import android.os.Handler;
import com.txnetwork.mypage.activity.MainActivity;
import com.txnetwork.mypage.datacenter.UserOperate;
import com.txnetwork.mypage.entity.UserInfo;
import com.txnetwork.mypage.fragment.MainFragment;
import com.txnetwork.mypage.utils.LogUtils;
import com.txnetwork.mypage.utils.StringUtil;
import com.txnetwork.mypage.utils.TxNetworkPool;
import com.txnetwork.mypage.utils.TxNetworkUtil;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zhangrui Create at 2014-7-10
 * @ClassName EntranceParserModel
 */
public class SessionidParserModel {
    protected static final String TAG = SessionidParserModel.class.getSimpleName();

    /**
     * @param @param  strResult
     * @param @return 参数
     * @return Map<Entrance,Integer> 返回类型
     * @throws
     * @Title: parseJson
     * @Description: 将获取到的jason对象解析为热点列表集合
     */
    public static void parseMainJson(final Context context, final Object data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String sessionStr = String.valueOf(data);
                    JSONObject resultJson = new JSONObject(sessionStr);
                    MainActivity.sessionid = resultJson.getString("sessionId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
