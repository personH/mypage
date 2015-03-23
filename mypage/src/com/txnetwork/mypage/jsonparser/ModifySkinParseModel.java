package com.txnetwork.mypage.jsonparser;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;
import com.txnetwork.mypage.fragment.MainFragment;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author zhangrui Create at 2014-7-10
 * @ClassName EntranceParserModel
 */
public class ModifySkinParseModel {
    protected static final String TAG = ModifySkinParseModel.class.getSimpleName();
    public static final int SET_CURRENT_SKIN_SUC = 0x123;
    public static final int SET_CURRENT_SKIN_FAIL = 0x124;

    /**
     * @param @param  strResult
     * @param @return 参数
     * @return Map<Entrance,Integer> 返回类型
     * @throws
     * @Title: parseJson
     * @Description: 将获取到的jason对象解析为热点列表集合
     */
    public static void parseMainJson(final Context context, final Handler handler, final Object data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String resultStr = String.valueOf(data);
                    JSONObject resultJson = new JSONObject(resultStr);
                    int errorCode = resultJson.getInt("errorCode");
                    String result = "";
                    if (errorCode == 1) {
                        //登录成功
                        result = "皮肤修改成功";
                        handler.sendMessage(handler.obtainMessage(SET_CURRENT_SKIN_SUC, result));
                        return;
                    } else if (errorCode == 1007) {
                        //未登录
                        //String errorMsg = resultJson.getString("errorMsg");
                        result = "皮肤修改失败,请先登录";
                    } else {
                        //未登录
                        String errorMsg = resultJson.getString("errorMsg");
                        result = errorMsg;
                    }
                    handler.sendMessage(handler.obtainMessage(SET_CURRENT_SKIN_FAIL, result));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
