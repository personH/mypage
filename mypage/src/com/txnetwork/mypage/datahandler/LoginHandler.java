package com.txnetwork.mypage.datahandler;

import android.content.Context;
import com.txnetwork.mypage.activity.MainActivity;
import com.txnetwork.mypage.fragment.MainFragment;
import com.txnetwork.mypage.entity.UserBean;
import com.txnetwork.mypage.http.HttpAction;
import com.txnetwork.mypage.utils.*;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginHandler extends DataHandler {

    private Context context;
    private String deviceid;// 设备id

    public LoginHandler(Context context) {
        super();
        this.context = context;
        deviceid = TxNetworkUtil.getIMEIId(context);
    }

    /**
     * @param username 账号,
     * @param password 密码
     */
    public void startNetWork(String aesKey, String username, String password) {

        final String server_url = ConstantPool.SERVER_URL + ConstantPool.LOGIN;

        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_POST);

        String sessionid = MainActivity.sessionid;

        password = MD5Util.md5Hex(password, "UTF-8").toLowerCase();
        password = MD5Util.md5Hex(password + sessionid, "UTF-8").toUpperCase();
        String sign = MD5Util.md5Hex(username + aesKey, "UTF-8").toUpperCase();

        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("deviceid", deviceid));
        formparams.add(new BasicNameValuePair("username", username));
        formparams.add(new BasicNameValuePair("password", password));
        formparams.add(new BasicNameValuePair("acctype", "5"));
        formparams.add(new BasicNameValuePair("sign", sign));
        httpAction.addBodyParam(formparams);

        LogUtils.putLog(context, "登录请求==server_url==" + server_url
                + "==deviceid==>" + deviceid + "==username==" + username
                + "==password==" + password + "==acctype==" + 5 + "==sign=="
                + sign);
        httpAction.setUri(server_url);
        addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        LogUtils.LOGE("LoginHandler", "===onNetReceiveOk===");
        String result = new String(receiveBody);
        sendResult(ConstantPool.LOGIN_HTTP_SUC, result);
    }

    @Override
    protected void onReceiveHeaders(Header[] receiveHeaders) {
        analyticHeaders(receiveHeaders);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.LOGIN_HTTP_FAIL, errorCode);
    }

    public static UserBean loginParse(String json) {
        UserBean userBean = new UserBean();
        try {
            JSONObject jsonObject = new JSONObject(json);
            int errorCode = jsonObject.optInt("errorCode");
            userBean.setErrorCode(errorCode);
            if (errorCode == 1) {
                String loginToken = jsonObject.optString("sys_token");// 自动登陆
                JSONObject userJson = jsonObject.optJSONObject("resultList");
                if (null != userJson) {
                    String userId = userJson.optString("userid");// 用户ID
                    String avatarUrl = userJson.optString("avatar");// 头像URL
                    String nickname = userJson.optString("nickname");// 昵称
                    String secretKey = userJson.optString("secretKey");// 交互密钥
                    int coin = userJson.optInt("coin");// 当前金币数量
                    int userLevel = userJson.optInt("userlevel");// 用户等级
                    String userexp = userJson.optString("userexp");// 经验
                    String userType = userJson.optString("usertype");// 用户类型（1-新浪微博，2-腾讯微博，3-微信，4-QQ，5-系统注册）
                    String status = userJson.optString("status");// 状态(0正常状态，1.冻结状态)
                    String username = userJson.optString("username");// 登录名
                    String invitecode = userJson.optString("invitecode");// 邀请码
                    String password = userJson.optString("password");// 密码
                    String mobileno = userJson.optString("mobileno");// 手机号
                    String email = userJson.optString("email");//
                    String gender = userJson.optString("gender");//
                    String groupid = userJson.optString("groupid");//
                    String hiscoin = userJson.optString("hiscoin");//
                    String onlinetime = userJson.optString("onlinetime");//
                    String parentcode = userJson.optString("parentcode");//
                    String privatetime = userJson.optString("privatetime");//
                    String publictime = userJson.optString("publictime");//
                    String regdeviceid = userJson.optString("regdeviceid");// 注册设备id
                    String remainexp = userJson.optString("remainexp");//
                    String todaylevelexp = userJson.optString("todaylevelexp");//
                    String todayonlinetime = userJson.optString("todayonlinetime");//
                    String nextLevelExp = userJson.optString("nextlevelexp");// 到下一等级所需经验值
                    String wifimode = userJson.optString("wifimode");//
                    String yourcoin = userJson.optString("yourcoin");//
                    long dateCreated = userJson.optLong("datecreated");// 注册时间
                    long freezetime = userJson.optLong("freezetime");//
                    long lastlogintime = userJson.optLong("lastlogintime");//
                    long lastupdated = userJson.optLong("lastupdated");//

                    userBean.setUserMsg(userId, secretKey, coin, nickname,
                            status, userLevel, username, userType,
                            nextLevelExp, dateCreated, loginToken, avatarUrl,
                            userexp, invitecode, password);
                }
            }
            String errorMsg = jsonObject.optString("errorMsg");
            userBean.setErrorMsg(errorMsg);
        } catch (JSONException e) {
            userBean.setErrorMsg(e.getMessage());
        }
        return userBean;
    }

}
