package com.txnetwork.mypage.datahandler;

import android.content.Context;
import com.txnetwork.mypage.http.HttpAction;
import com.txnetwork.mypage.utils.*;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class GetNavHandler extends DataHandler {

    private Context mContext;
    private String deviceid;// 设备id

    public GetNavHandler(Context mContext) {
        super();
        this.mContext = mContext;
        deviceid = TxNetworkUtil.getIMEIId(mContext);
    }

    /**
     *
     */
    public void startNetWork() {
        final String server_url = ConstantPool.SERVER_URL + ConstantPool.GET_NAV_INFO;
        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_POST);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        String currentTime = SharedUtils.getDateUpdated(mContext);
        formparams.add(new BasicNameValuePair("dateupdated", currentTime));
        httpAction.addBodyParam(formparams);
        LogUtils.putLog(mContext, "获取我的导航信息==server_url==" + server_url + "==deviceid==>" + deviceid);
        httpAction.setUri(server_url);
        addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        String result = new String(receiveBody);
        sendResult(ConstantPool.GET_NAV_INFO_SUC, result);
    }

    @Override
    protected void onReceiveHeaders(Header[] receiveHeaders) {
        analyticHeaders(receiveHeaders);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.GET_NAV_INFO_FAIL, errorCode);
    }

}
