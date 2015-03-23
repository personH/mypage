package com.txnetwork.mypage.listener;


import com.txnetwork.mypage.entity.UserBean;

public interface UserMsgCallback {
	public void getUserMsg(UserBean userBean);// 登录成功获得用户信息

	public void refreshUserMsg(String nickname, String avatarUrl, int coin, int userLevel, String exper);// 更改金币等级经验

	public void refreshName(String nickname);// 更改昵称

	public void refreshAvatar(String avatarUrl);// 更改头像

}
