package com.txnetwork.mypage.datahandler;

import android.content.Context;
import com.txnetwork.mypage.http.HttpAction;
import com.txnetwork.mypage.utils.ConstantPool;
import com.txnetwork.mypage.utils.LogUtils;
import com.txnetwork.mypage.utils.MD5Util;
import com.txnetwork.mypage.utils.TxNetworkUtil;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeleteSkinHandler extends DataHandler {

    private Context mContext;
    private String deviceid;// 设备id

    public DeleteSkinHandler(Context mContext) {
        super();
        this.mContext = mContext;
        deviceid = TxNetworkUtil.getIMEIId(mContext);
    }

    /**
     * @param aesKey
     * @param picid
     */
    public void startNetWork(String picid, String aesKey) {
        final String server_url = ConstantPool.SERVER_URL + ConstantPool.DELETE_DIY_THEME;
        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_POST);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("deviceid", deviceid));
        formparams.add(new BasicNameValuePair("picid", picid));
        String userid = TxNetworkUtil.getUserId(mContext);
        String sign = MD5Util.md5Hex(deviceid + userid + aesKey, "UTF-8").toUpperCase();
        formparams.add(new BasicNameValuePair("sign", sign));
        httpAction.addBodyParam(formparams);

        LogUtils.putLog(mContext, "删除自定义皮肤请求==server_url==" + server_url
                + "==deviceid==>" + deviceid + "==userid==" + userid
                + "==sign==" + sign);

        httpAction.setUri(server_url);
        addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        resultParse(receiveBody);
    }

    @Override
    protected void onReceiveHeaders(Header[] receiveHeaders) {
        analyticHeaders(receiveHeaders);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.DELETE_DIY_THEME_FAIL, errorCode);
    }

    private void resultParse(byte[] receiveBody) {
        try {
            String resultStr = new String(receiveBody);
            JSONObject jsonObject = new JSONObject(resultStr);
            int errorCode = jsonObject.getInt("errorCode");
            String result = "";
            if (errorCode == 1) {
                sendResult(ConstantPool.DELETE_DIY_THEME_SUC, result);
            } else {
                String errorMsg = jsonObject.getString("errorMsg");
                sendResult(ConstantPool.DELETE_DIY_THEME_FAIL, errorMsg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
