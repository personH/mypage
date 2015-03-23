package com.txnetwork.mypage.datahandler;

import android.content.Context;
import com.txnetwork.mypage.http.HttpAction;
import com.txnetwork.mypage.utils.ConstantPool;
import com.txnetwork.mypage.utils.LogUtils;
import com.txnetwork.mypage.utils.TxNetworkUtil;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * 自动登录
 *
 * @author dongxl
 */
public class LoginAutoHandler extends DataHandler {

    private Context context;
    private String deviceid;// 设备id

    public LoginAutoHandler(Context context) {
        super();
        this.context = context;
        deviceid = TxNetworkUtil.getIMEIId(context);
    }

    /**
     * @param loginToken
     */
    public void startNetWork(String loginToken) {
        final String server_url = ConstantPool.SERVER_URL + ConstantPool.LOGIN;

        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_POST);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("deviceid", deviceid));
        formparams.add(new BasicNameValuePair("sys_token", loginToken));
        httpAction.addBodyParam(formparams);

        LogUtils.putLog(context, "自动登录请求==server_url==" + server_url + "==deviceid==>" + deviceid + "==sys_token==" + loginToken);
        httpAction.setUri(server_url);
        addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        String result = new String(receiveBody);
        sendResult(ConstantPool.LOGIN_AUTO_SUC, result);
    }

    @Override
    protected void onReceiveHeaders(Header[] receiveHeaders) {
        analyticHeaders(receiveHeaders);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.LOGIN_AUTO_FAIL, errorCode);
    }

}
