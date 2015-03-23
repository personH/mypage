package com.txnetwork.mypage.datahandler;

import android.content.Context;
import com.txnetwork.mypage.http.HttpAction;
import com.txnetwork.mypage.utils.ConstantPool;
import com.txnetwork.mypage.utils.LogUtils;

public class SkinTabHandler extends DataHandler {

    private String server_url;
    private Context mContext;

    public SkinTabHandler(Context context) {
        super();
        this.mContext = context;
        server_url = ConstantPool.SERVER_URL + ConstantPool.GET_WEBTHEMETAB;
    }

    public void startNetWork() {
        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_GET);
        LogUtils.putLog(mContext, "获取皮肤tab页面");
        httpAction.setUri(server_url);
        addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        String result = new String(receiveBody);
        sendResult(ConstantPool.GET_THEME_TAB_OK, result);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.GET_THEME_TAB_ERROR, errorCode);
    }

}
