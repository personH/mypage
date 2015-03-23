package com.txnetwork.mypage.entity;

public class BaseBean {

	protected int errorCode;
	protected String errorMsg = "";
	protected Object data;
	
	public BaseBean() {
		// TODO Auto-generated constructor stub
	}
	
	public BaseBean(int errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
