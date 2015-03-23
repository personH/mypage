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

import java.util.ArrayList;
import java.util.List;

public class ForgetPassHandler extends DataHandler {

    private Context context;
    private String deviceid;// 设备id

    public ForgetPassHandler(Context context) {
        super();
        this.context = context;
        deviceid = TxNetworkUtil.getIMEIId(context);
    }

    /**
     * @param aesKey
     * @param passStr
     * @param random  验证码
     * @param userid  账号,
     * @param oldPass 密码
     */
    public void startNetWork(String userid, String passStr, String oldPass, String random, String aesKey) {
        passStr = MD5Util.md5Hex(passStr, "UTF-8").toLowerCase();
        final String server_url = ConstantPool.SERVER_URL + ConstantPool.FORGET_PHONE;
        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_POST);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("deviceid", deviceid));
        formparams.add(new BasicNameValuePair("userid", userid));
        formparams.add(new BasicNameValuePair("password", passStr));
        formparams.add(new BasicNameValuePair("oldpwd", oldPass));
        formparams.add(new BasicNameValuePair("random", random));
        String sign = MD5Util.md5Hex(deviceid + userid + aesKey, "UTF-8")
                .toUpperCase();
        formparams.add(new BasicNameValuePair("sign", sign));
        httpAction.addBodyParam(formparams);

        LogUtils.putLog(context, "手机找回密码请求==server_url==" + server_url
                + "==deviceid==>" + deviceid + "==userid==" + userid
                + "==oldpwd==" + oldPass + "==password==" + passStr
                + "==random==" + random + "==sign==" + sign);

        httpAction.setUri(server_url);
        addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        String result = new String(receiveBody);
        sendResult(ConstantPool.FORGET_PHONE_SUC, result);
    }

    @Override
    protected void onReceiveHeaders(Header[] receiveHeaders) {
        analyticHeaders(receiveHeaders);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.FORGET_PHONE_FAIL, errorCode);
    }

}
