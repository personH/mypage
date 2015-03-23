package com.txnetwork.mypage.datahandler;

import android.content.Context;
import com.txnetwork.mypage.http.HttpAction;
import com.txnetwork.mypage.utils.*;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SubNavHandler extends DataHandler {

    private Context mContext;
    private String deviceid;// 设备id

    public SubNavHandler(Context mContext) {
        super();
        this.mContext = mContext;
        deviceid = TxNetworkUtil.getIMEIId(mContext);
    }

    /**
     * @param aesKey
     * @param userid     账号,
     * @param navinfostr
     */
    public void startNetWork(String userid, String navinfostr, String aesKey) {
        final String server_url = ConstantPool.SERVER_URL + ConstantPool.SUB_NAV_INFO;

        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_POST);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("deviceid", deviceid));
        //formparams.add(new BasicNameValuePair("userid", userid)); 不在传userid
        formparams.add(new BasicNameValuePair("navinfostr", navinfostr));
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//设置日期格式
        String currentTime = df.format(new Date());// new Date()为获取当前系统时间
        formparams.add(new BasicNameValuePair("dateupdated", currentTime));
        SharedUtils.setDateUpdated(mContext, currentTime);
        String sign = MD5Util.md5Hex(deviceid + userid + aesKey, "UTF-8").toUpperCase();
        formparams.add(new BasicNameValuePair("sign", sign));
        httpAction.addBodyParam(formparams);

        LogUtils.putLog(mContext, "提交我的导航信息==server_url==" + server_url + "==navinfo==>" + navinfostr
                + "==deviceid==>" + deviceid + "==userid==" + userid
                + "==sign==" + sign);

        httpAction.setUri(server_url);
        addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        String result = new String(receiveBody);
        sendResult(ConstantPool.SUB_NAV_INFO_SUC, result);
    }

    @Override
    protected void onReceiveHeaders(Header[] receiveHeaders) {
        analyticHeaders(receiveHeaders);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.SUB_NAV_INFO_FAIL, errorCode);
    }

}
