package com.txnetwork.mypage.entity;

public class PhoneCodeBean extends BaseBean {

	private String userId;//用户id
	private String data;
	private String password;//密码

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
