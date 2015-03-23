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

/**
 * 退出登录
 *
 * @author dongxl
 */
public class LoginExitHandler extends DataHandler {

    private Context context;
    private String deviceid;// 设备id

    public LoginExitHandler(Context context) {
        super();
        this.context = context;
        deviceid = TxNetworkUtil.getIMEIId(context);
    }

    /**
     * @param userid 账号,
     * @param aesKey 密码
     */
    public void startNetWork(String userid, String aesKey) {

        final String server_url = ConstantPool.SERVER_URL + ConstantPool.LOGIN_EXIT;
        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_POST);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("deviceid", deviceid));
        formparams.add(new BasicNameValuePair("userid", userid));
        String sign = MD5Util.md5Hex(deviceid + userid + aesKey, "UTF-8").toUpperCase();
        formparams.add(new BasicNameValuePair("sign", sign));
        httpAction.addBodyParam(formparams);

        LogUtils.putLog(context, "退出登录请求==server_url==" + server_url
                + "==deviceid==>" + deviceid + "==userid==" + userid
                + "==sign==" + sign);
        httpAction.setUri(server_url);
        addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        String result = new String(receiveBody);
        sendResult(ConstantPool.LOGIN_EXIT_SUC, result);
    }

    @Override
    protected void onReceiveHeaders(Header[] receiveHeaders) {
        analyticHeaders(receiveHeaders);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.LOGIN_EXIT_FAIL, errorCode);
    }

}
