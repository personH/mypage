package com.txnetwork.mypage.datahandler;

import android.content.Context;
import com.txnetwork.mypage.entity.PhoneCodeBean;
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

/**
 * 获得验证码
 *
 * @author dongxl
 */
public class PhoneCodeHandler extends DataHandler {

    private Context context;
    private String deviceid;// 设备id

    public PhoneCodeHandler(Context context) {
        super();
        this.context = context;
        deviceid = TxNetworkUtil.getIMEIId(context);
    }

    /**
     * @param phoneNum 手机号码
     * @param type     1注册验证码，2找回密码验证码
     */
    public void startNetWork(String phoneNum, String aesKey, int type) {
        String server_url = "";

        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_POST);
        server_url = ConstantPool.SERVER_URL + ConstantPool.VERIFY_CODE;

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("deviceid", deviceid));
        formparams.add(new BasicNameValuePair("phone", phoneNum));
        String sign = MD5Util.md5Hex(deviceid + phoneNum + aesKey, "UTF-8").toUpperCase();
        formparams.add(new BasicNameValuePair("sign", sign));
        formparams.add(new BasicNameValuePair("type", String.valueOf(type)));
        httpAction.addBodyParam(formparams);

        LogUtils.putLog(context, "获得验证码请求==server_url==" + server_url
                + "==deviceid==>" + deviceid + "==phoneNum==" + phoneNum
                + "==type==" + type + "==sign==" + sign);
        httpAction.setUri(server_url);
        addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        String result = new String(receiveBody);
        sendResult(ConstantPool.PHONE_CODE_SUC, result);
    }

    @Override
    protected void onReceiveHeaders(Header[] receiveHeaders) {
        analyticHeaders(receiveHeaders);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.PHONE_CODE_FAIL, errorCode);
    }

    /**
     * @param json
     * @param type 1注册验证码，2找回密码验证码
     * @return
     */
    public static PhoneCodeBean codeParse(String json, int type) {
        PhoneCodeBean codeBean = new PhoneCodeBean();
        try {
            JSONObject jsonObject = new JSONObject(json);
            int errorCode = jsonObject.optInt("errorCode");
            codeBean.setErrorCode(errorCode);
            if (errorCode == 1) {
                if (type == 1) {
                    String data = jsonObject.optString("data");//
                    codeBean.setData(data);
                } else if (type == 2) {
                    String userId = jsonObject.optString("userid");// 用户ID
                    codeBean.setUserId(userId);
                    String password = jsonObject.optString("password");// 密码
                    codeBean.setPassword(password);
                }
            }
            String errorMsg = jsonObject.optString("errorMsg");
            codeBean.setErrorMsg(errorMsg);
        } catch (JSONException e) {
            codeBean.setErrorMsg(e.getMessage());
        }
        return codeBean;
    }

}
