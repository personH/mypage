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

public class SkinDetailHandler extends DataHandler {

    private Context context;

    public SkinDetailHandler(Context context) {
        super();
        this.context = context;
    }

    /**
     * @param picid
     */
    public void startNetWork(String picid) {
        final String server_url = ConstantPool.SERVER_URL + ConstantPool.SET_CURRENT_THEME;

        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_POST);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("picid", picid == null ? "0" : picid));
        httpAction.addBodyParam(formparams);

        LogUtils.putLog(context, "设置当前皮肤请求==server_url==" + server_url + "==picid==>" + picid);

        httpAction.setUri(server_url);
        addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        String result = new String(receiveBody);
        sendResult(ConstantPool.GET_SKIN_DETAIL_SUC, result);
    }

    @Override
    protected void onReceiveHeaders(Header[] receiveHeaders) {
        analyticHeaders(receiveHeaders);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.GET_SKIN_DETAIL_FAIL, errorCode);
    }

}
