package com.txnetwork.mypage.datahandler;

import android.content.Context;
import com.txnetwork.mypage.http.HttpAction;
import com.txnetwork.mypage.utils.ConstantPool;
import com.txnetwork.mypage.utils.LogUtils;
import com.txnetwork.mypage.utils.MD5Util;
import com.txnetwork.mypage.utils.TxNetworkUtil;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

public class SecretkeyHandler extends DataHandler {
    private String server_url;
    /**
     * 手机厂商
     */
    private String manufacturer;
    /**
     * 用户编号，保证唯一
     */
    private String userid;
    /**
     * 手机类型
     */
    private String type;
    /**
     * 手机网卡
     */
    private String mac;
    /**
     * 入网号
     */
    private String imer;
    /**
     * 手机系统
     */
    private String system;
    /**
     * 手机系统版本
     */
    private String systemVersion;

    private Context context;

    public SecretkeyHandler(Context context) {
        super();
        this.context = context;
        manufacturer = TxNetworkUtil.getManufacturer().replaceAll(" ", "-");
        userid = TxNetworkUtil.getIMEIId(context);
        type = TxNetworkUtil.getType().replace(" ", "-");
        mac = TxNetworkUtil.getLocalMacAddress(context);
        imer = TxNetworkUtil.getIMEIId(context);
        system = "android";
        systemVersion = TxNetworkUtil.getVersion();
    }

    public void startNetWork() {
        HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_POST);
        TreeMap<String, String> treeMap = new TreeMap<String, String>();
        treeMap.put("manufacturer", manufacturer);
        treeMap.put("userid", userid);
        treeMap.put("type", type);
        treeMap.put("mac", mac);
        treeMap.put("imer", imer);
        treeMap.put("system", system);
        treeMap.put("systemVersion", systemVersion);

        StringBuffer signString = new StringBuffer();
        Iterator<String> ite = treeMap.keySet().iterator();
        while (ite.hasNext()) {
            String key = ite.next();
            String value = treeMap.get(key);
            signString.append(key).append(value);
        }

        String sign = MD5Util.md5Hex(signString.toString(), "UTF-8").toUpperCase();

        LogUtils.putLog(context, "获取密钥请求---manufacturer:" + manufacturer + "---"
                + "userid" + userid + "---" + "type" + type + "---" + "mac" + mac + "---" + "imer" + imer
                + "---" + "system" + system + "---" + "systemVersion" + systemVersion + "---" + "sign" + sign);

        server_url = ConstantPool.SERVER_URL + ConstantPool.GET_SECRDTKEY;
        httpAction.setUri(server_url);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("manufacturer", manufacturer));
        formparams.add(new BasicNameValuePair("userid", userid));
        formparams.add(new BasicNameValuePair("type", type));
        formparams.add(new BasicNameValuePair("mac", mac));
        formparams.add(new BasicNameValuePair("imer", imer));
        formparams.add(new BasicNameValuePair("system", system));
        formparams.add(new BasicNameValuePair("systemVersion", systemVersion));
        formparams.add(new BasicNameValuePair("sign", sign));
        httpAction.addBodyParam(formparams);
        addRequestCookie(httpAction);
        startNetwork(httpAction);
    }

    @Override
    protected void onNetReceiveOk(byte[] receiveBody) {
        String result = new String(receiveBody);
        sendResult(ConstantPool.GET_SECRET_KEY_OK, result);
    }

    @Override
    protected void onNetReceiveError(int errorCode) {
        sendResult(ConstantPool.GET_SECRET_KEY_ERROR, errorCode);
    }

}
