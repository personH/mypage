package com.txnetwork.mypage.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class UserBean extends BaseBean implements Parcelable {
	private String userId;// 用户ID
	private String secretKey;// 交互密钥
	private int coin;// 当前金币数量
	private int userLevel;// 用户等级
	private String userexp;// 经验
	private String nickname;// 昵称
	private String status;// 状态(0正常状态，1.冻结状态)
	private String password;// 密码
	private String username;// 登录名
	private String invitecode;// 邀请码
	private String userType;// 用户类型（1-新浪微博，2-腾讯微博，3-微信，4-QQ，5-系统注册）
	private String nextLevelExp;// 到下一等级所需经验值
	private long dateCreated;// 注册时间
	private String loginToken;// 自动登陆Token
	private String avatarUrl;// 头像url

	public UserBean() {
		// TODO Auto-generated constructor stub
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getNextLevelExp() {
		return nextLevelExp;
	}

	public void setNextLevelExp(String nextLevelExp) {
		this.nextLevelExp = nextLevelExp;
	}

	public long getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(long dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getLoginToken() {
		return loginToken;
	}

	public void setLoginToken(String loginToken) {
		this.loginToken = loginToken;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public int getCoin() {
		return coin;
	}

	public void setCoin(int coin) {
		this.coin = coin;
	}

	public int getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(int userLevel) {
		this.userLevel = userLevel;
	}

	public String getUserexp() {
		return userexp;
	}

	public void setUserexp(String userexp) {
		this.userexp = userexp;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getInvitecode() {
		return invitecode;
	}

	public void setInvitecode(String invitecode) {
		this.invitecode = invitecode;
	}

	public void setUserMsg(String userId, String secretKey, int coin,
			String nickname, String status, int userLevel, String username,
			String userType, String nextLevelExp, long dateCreated,
			String loginToken, String avatarUrl, String userexp,
			String invitecode, String password) {
		this.userId = userId;
		this.secretKey = secretKey;
		this.coin = coin;
		this.userLevel = userLevel;
		this.userexp = userexp;
		this.nickname = nickname;
		this.status = status;
		this.userLevel = userLevel;
		this.username = username;
		this.password = password;
		this.invitecode = invitecode;
		this.userType = userType;
		this.nextLevelExp = nextLevelExp;
		this.dateCreated = dateCreated;
		this.loginToken = loginToken;
		this.avatarUrl = avatarUrl;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/** copy constructor {@hide} */
	public UserBean(Parcel source) {
		if (source != null) {
			userId = source.readString();
			coin = source.readInt();
			nickname = source.readString();
			userLevel = source.readInt();
			avatarUrl = source.readString();
			status = source.readString();
			username = source.readString();
			nextLevelExp = source.readString();
			userexp = source.readString();
			password = source.readString();
			invitecode = source.readString();
		}
	}

	/** Implement the Parcelable interface {@hide} */
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(userId);
		dest.writeInt(coin);
		dest.writeString(nickname);
		dest.writeInt(userLevel);
		dest.writeString(avatarUrl);
		dest.writeString(status);
		dest.writeString(username);
		dest.writeString(nextLevelExp);
		dest.writeString(userexp);
		dest.writeString(password);
		dest.writeString(invitecode);

	}

	/** Implement the Parcelable interface {@hide} */
	public static final Creator<UserBean> CREATOR = new Creator<UserBean>() {
		public UserBean createFromParcel(Parcel in) {
			return new UserBean(in);
		}

		public UserBean[] newArray(int size) {
			return new UserBean[size];
		}
	};

}
