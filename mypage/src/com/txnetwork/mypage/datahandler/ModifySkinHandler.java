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

public class ModifySkinHandler extends DataHandler {

    private Context context;
    private String deviceid;// 设备id

    public ModifySkinHandler(Context context) {
        super();
        this.context = context;
        deviceid = TxNetworkUtil.getIMEIId(context);
    }

    /**
     * @param aesKey
     * @param userid 账号,
     * @param picid
     */
    public void startNetWork(String userid, String picid, String aesKey) {
        final String server_url = ConstantPool.SERVER_URL + ConstantPool.SET_CURRENT_THEME;

        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_POST);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("deviceid", deviceid));
        //formparams.add(new BasicNameValuePair("userid", userid)); 不在传userid
        formparams.add(new BasicNameValuePair("picid", picid == null ? "0" : picid));
        String sign = MD5Util.md5Hex(deviceid + userid + aesKey, "UTF-8").toUpperCase();
        formparams.add(new BasicNameValuePair("sign", sign));
        httpAction.addBodyParam(formparams);

        LogUtils.putLog(context, "设置当前皮肤请求==server_url==" + server_url
                + "==deviceid==>" + deviceid + "==userid==" + userid
                + "==sign==" + sign);

        httpAction.setUri(server_url);
        addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        String result = new String(receiveBody);
        sendResult(ConstantPool.SET_CURRENT_THEME_SUC, result);
    }

    @Override
    protected void onReceiveHeaders(Header[] receiveHeaders) {
        analyticHeaders(receiveHeaders);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.SET_CURRENT_THEME_FAIL, errorCode);
    }

}
