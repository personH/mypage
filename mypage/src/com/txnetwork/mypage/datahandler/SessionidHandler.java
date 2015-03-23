package com.txnetwork.mypage.datahandler;

import android.content.Context;
import com.txnetwork.mypage.http.HttpAction;
import com.txnetwork.mypage.utils.*;
import org.apache.http.Header;

public class SessionidHandler extends DataHandler {

    private Context context;

    public SessionidHandler(Context context) {
        super();
        this.context = context;
    }

    /**
     *
     */
    public void startNetWork() {
        final String server_url = ConstantPool.SERVER_URL + ConstantPool.GET_SESSIONID;
        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_GET);
        httpAction.setUri(server_url);
        //addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        LogUtils.LOGE("SessionidHandler", "===onNetReceiveOk===");
        String result = new String(receiveBody);
        sendResult(ConstantPool.GET_SESSIONID_SUC, result);
    }

    @Override
    protected void onReceiveHeaders(Header[] receiveHeaders) {
        analyticHeaders(receiveHeaders);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.GET_SESSIONID_FAIL, errorCode);
    }
}
