package com.txnetwork.mypage.entity;

/**
 * 授权信息
 * @author dongxl
 * 
 */
public class AuthorizeInfo {

	private int ret;
	private String openid;
	private String expires_in;
	private String access_token;
	private int acctype;//登录方式,（1-新浪微博，2-腾讯微博，3-微信，4-QQ，5-系统注册）

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getExpiresIn() {
		return expires_in;
	}

	public void setExpiresIn(String expires_in) {
		this.expires_in = expires_in;
	}

	public String getAccessToken() {
		return access_token;
	}

	public void setAccessToken(String access_token) {
		this.access_token = access_token;
	}

	public int getAcctype() {
		return acctype;
	}

	public void setAcctype(int acctype) {
		this.acctype = acctype;
	}

	@Override
	public String toString() {
		return "AuthorizeInfo [ret=" + ret + ", openid=" + openid
				+ ", expires_in=" + expires_in + ", access_token="
				+ access_token + ", acctype=" + acctype + "]";
	}
	
}
