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

public class RegisterHandler extends DataHandler {

	private Context context;
	private String deviceid;// 设备id

	public RegisterHandler(Context context) {
		super();
		this.context = context;
		deviceid = TxNetworkUtil.getIMEIId(context);
	}

	/**
	 * @param aesKey
	 * @param passStr
	 * @param nicknameStr
	 * @param random 验证码
	 */
	public void startNetWork(String aesKey, String accountStr,
			String nicknameStr, String passStr, String random, int type) {
		passStr = MD5Util.md5Hex(passStr, "UTF-8").toLowerCase();
		final String server_url = ConstantPool.SERVER_URL + ConstantPool.REGISTER;

		HttpAction httpAction = new HttpAction(HttpAction.REQUEST_TYPE_POST);
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("deviceid", deviceid));
		formparams.add(new BasicNameValuePair("username", accountStr));
		formparams.add(new BasicNameValuePair("nickname", nicknameStr));
		formparams.add(new BasicNameValuePair("password", passStr));
		if(type == 1){
			formparams.add(new BasicNameValuePair("random", random));
		}
		formparams.add(new BasicNameValuePair("acctype", "5"));
		String sign = MD5Util.md5Hex(accountStr + passStr + aesKey, "UTF-8")
				.toUpperCase();
		formparams.add(new BasicNameValuePair("sign", sign));
		httpAction.addBodyParam(formparams);

		LogUtils.putLog(context, "注册请求==server_url==" + server_url
				+ "==deviceid==>" + deviceid + "==accountStr==" + accountStr
				+ "==nicknameStr==>" + nicknameStr + "==passStr==" + passStr + "==random==" + random
				+ "==sign==>" + sign + "==type==" + type);
		httpAction.setUri(server_url);
		addRequestCookie(httpAction);
		startNetwork(httpAction);
	}

	@Override
	protected void onNetReceiveOk(byte[] receiveBody) {
		String result = new String(receiveBody);
		sendResult(ConstantPool.REGISTER_HTTP_SUC, result);
	}

	@Override
	protected void onReceiveHeaders(Header[] receiveHeaders) {
		analyticHeaders(receiveHeaders);
    }

	@Override
	protected void onNetReceiveError(int errorCode) {
		sendResult(ConstantPool.REGISTER_HTTP_FAIL, errorCode);
	}

}
