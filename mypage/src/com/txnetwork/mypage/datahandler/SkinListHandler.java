package com.txnetwork.mypage.datahandler;

import android.content.Context;
import com.txnetwork.mypage.http.HttpAction;
import com.txnetwork.mypage.utils.ConstantPool;
import com.txnetwork.mypage.utils.LogUtils;
import com.txnetwork.mypage.utils.TxNetworkUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/1/20.
 */
public class SkinListHandler extends DataHandler {

    private String server_url;

    private String userid;

    private int page = 0;

    private int pagesize = 10;

    private int navtype;

    private Context mContext;

    public SkinListHandler(Context context, int navtype, int page, int pagesizze) {
        mContext = context;
        server_url = ConstantPool.SERVER_URL + ConstantPool.GET_WEBTHEMELIST;
        this.userid = TxNetworkUtil.getUserId(mContext);//获取用户id
        this.pagesize = pagesizze;
        this.page = page;
        this.navtype = navtype;
    }

    public void startNetWork() {
        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_POST);
        LogUtils.putLog(mContext, "获取皮肤tab页面");
        httpAction.setUri(server_url);

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
//        if (navtype == ConstantPool.TAB_DIY) {
//            formparams.add(new BasicNameValuePair("userid", userid));
//        }
        formparams.add(new BasicNameValuePair("page", String.valueOf(page)));
        formparams.add(new BasicNameValuePair("pagesize", String.valueOf(pagesize)));
        formparams.add(new BasicNameValuePair("navtype", String.valueOf(navtype)));
        httpAction.addBodyParam(formparams);
        addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        String result = new String(receiveBody);
        sendResult(ConstantPool.GET_THEME_LIST_OK, result);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.GET_THEME_LIST_ERROR, errorCode);
    }
}