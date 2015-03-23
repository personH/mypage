package com.txnetwork.mypage.datahandler;

import android.content.Context;
import com.txnetwork.mypage.entity.AuthorizeInfo;
import com.txnetwork.mypage.http.HttpAction;
import com.txnetwork.mypage.utils.ConstantPool;
import com.txnetwork.mypage.utils.LogUtils;
import com.txnetwork.mypage.utils.MD5Util;
import com.txnetwork.mypage.utils.TxNetworkUtil;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 授权登录
 */
public class LoginAuthHandler extends DataHandler {

    private Context context;
    private String deviceid;// 设备id

    public LoginAuthHandler(Context context) {
        super();
        deviceid = TxNetworkUtil.getIMEIId(context);
        this.context = context;
    }

    /**
     * @param aesKey
     * @param authorize
     */
    public void startNetWork(AuthorizeInfo authorize, String aesKey) {
        if (null == authorize || 0 != authorize.getRet()) {
            sendResult(ConstantPool.AUTHORIZE_LOGIN_FAIL, 2);
            return;
        }
        final String server_url = ConstantPool.SERVER_URL + ConstantPool.AUTHORIZE_LOGIN;

        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_POST);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("deviceid", deviceid));
        String sign = "";
        int acctype = authorize.getAcctype();// 1-新浪微博，2-腾讯微博，3-微信，4-QQ，5-系统注册
        switch (acctype) {
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            case 4:
                String openid = authorize.getOpenid();
                String access_token = authorize.getAccessToken();
                formparams.add(new BasicNameValuePair("openid", openid));
                formparams.add(new BasicNameValuePair("access_token", access_token));
                sign = MD5Util.md5Hex(openid + access_token + aesKey, "UTF-8").toUpperCase();
                break;
            case 5:

                break;

            default:
                break;
        }
        formparams.add(new BasicNameValuePair("acctype", String.valueOf(acctype)));
        formparams.add(new BasicNameValuePair("sign", sign));
        httpAction.addBodyParam(formparams);

        LogUtils.putLog(context, "授权请求==>server_url==" + server_url
                + "==deviceid==>" + deviceid + "==" + authorize.toString() + "==sign==" + sign);

        httpAction.setUri(server_url);
        addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        String result = new String(receiveBody);
        sendResult(ConstantPool.AUTHORIZE_LOGIN_SUC, result);
    }

    @Override
    protected void onReceiveHeaders(Header[] receiveHeaders) {
        analyticHeaders(receiveHeaders);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.AUTHORIZE_LOGIN_FAIL, errorCode);
    }

    public static AuthorizeInfo QQAuthorizeParser(Object response) {
        try {
            JSONObject jsonObject = (JSONObject) response;
            int ret = jsonObject.optInt("ret");
            if (ret == 0) {
                AuthorizeInfo authorize = new AuthorizeInfo();
                authorize.setRet(ret);
                String openid = jsonObject.optString("openid");
                String expires_in = jsonObject.optString("expires_in");
                String access_token = jsonObject.optString("access_token");
                authorize.setAccessToken(access_token);
                authorize.setExpiresIn(expires_in);
                authorize.setOpenid(openid);
                authorize.setAcctype(4);
                return authorize;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
