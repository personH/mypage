package com.txnetwork.mypage.utils;


import android.os.Environment;

/**
 * @author TXWL
 */
public class ConstantPool {


    public static final int TAB_NEWEST = 1;//皮肤tab标签,最新
    public static final int TAB_HOTEST = 2;//排行
    public static final int TAB_GROUP = 3;//合集
    public static final int TAB_DIY = 4;//自定义

    static final int SECURITY_WEP = 1;
    static final int SECURITY_PSK = 2;
    static final int SECURITY_EAP = 3;


    public static final int MAX_WEB_DIY_SKIN = 5;
    public static final int MAX_LOCAL_DIY_SKIN = 10;
    public static final int WEB_SKIN_PAGE_SIZE = 6;

    /**
     * WIFI信号强度区间
     */
    public static final int SINGNAL_INTERVAL = 4;

    public static final int UPDATE_TIME = 35;

    // public static final String USER_KEY="302B7A6160D4ED888483645703FC1975";

    public static final String PRODUCTID = "wifi%20share";// wifi%20share

    public static final String SINA_ID = "85007504";
    public static final String SINA_REDIRECT_URL = "http://app.banma.com/callback/wifi";
    public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
    public static final long TENCENT_ID = 801454537;
    public static final String TENCET_SECRET = "542bac53396b685f27f16ed597fed2c5";
    public static final String WXAPP_ID = "wx4c4a9198a10119ec";
    public static final String QZONE_ID = "1104101753";

    public static final String TAG_USER = "UserBean";
    public static final String HTTP_COOKIE = "Cookie";
    public static final String COOKIE_PATH = "; Path=/; HttpOnly";//; Path=/; HttpOnly
    /**
     * 图片文件夹路径
     */
    public static final String PHOTO_PATH = Environment.getExternalStorageDirectory() + "/" + "TxNetWork/photo/";

    public static final String AGREEURL = "agreeUrl";
    /**
     * 测试服务器
     */
//    public static final String SERVER_URL = "http://192.168.3.57:8080/";
    public static final String SERVER_URL = "http://192.168.1.85:8088/";//192.168.3.159:8080    192.168.1.85:8087
    public static final String WIFI_SERVER_URL = "http://192.168.1.85:8083/";
    public static final String UPDATEC_URL = "http://192.168.1.85:8083/";
    public static final String FEEDBACK_URL = "http://192.168.1.85:8083/";
    public static final String PCTOPHONE_URL = "http://192.168.1.85:8083/";
    public static final String SHAREMSG_URL = "http://192.168.1.85:8083/";
    public static final String PERSONAL_URL = "http://192.168.1.85:8087/";

    public static final String PIC_SERVER_URL = "http://192.168.1.85:8088";

    /**
     * 服务器地址
     */
//	public static final String SERVER_URL = "http://txuc.tx-network.com/api/";
//	public static final String WIFI_SERVER_URL = "http://wifikey.api.tx-network.com/";
//	public static final String UPDATEC_URL = "http://update.api.tx-network.com/";
//	public static final String FEEDBACK_URL = "http://feedback.api.tx-network.com/";
//	public static final String SHAREMSG_URL = "http://wifisw.tx-network.com/";// http://wifisw.tx-network.com/http://112.124.116.151:8080/
//	public static final String PCTOPHONE_URL = "http://wifigx.tx-network.com/";
//	public static final String SERVER_URL = "http://wifisw.tx-network.com/";
//	public static final String PERSONAL_URL = "http://wifisw.tx-network.com/";

    public static final String GET_SECRDTKEY = "api/user/downloadsecretkey";// 获取密钥
    public static final String GET_WEBTHEMELIST = "api/skin/getskininfo";// 获取皮肤列表
    public static final String GET_WEBTHEMETAB = "api/skin/getskintab";// 获取皮肤tab页面
    public static final String LOGIN = "api/user/login";// 登录
    public static final String VERIFY_CODE = "api/user/sendMessage";// 验证码
    public static final String REGISTER = "api/user/register";// 注册
    public static final String FORGET_PHONE = "api/user/updatepwd";//手机找回密码
    public static final String LOGIN_EXIT = "api/user/logout";// 退出登录
    public static final String AUTHORIZE_LOGIN = "api/user/thirdlogin";// 授权登录
    public static final String SET_CURRENT_THEME = "api/skin/setcurrentskin";// 修改当前皮肤
    public static final String UPLOAD_PIC = "api/photo/upload";// 上传自定义图片
    public static final String GET_SESSIONID = "api/user/getsessionid";// 获取sessionid
    public static final String DELETE_DIY_THEME = "api/skin/delskin";// 删除自定义皮肤
    public static final String SUB_NAV_INFO = "api/nav/subnavinfo";// 提交我的导航信息
    public static final String GET_NAV_INFO = "api/nav/getnavinfo";// 获取我的导航信息

    public static final String GET_WIFI_LIST = "qryaplistinfo";// 获取热点列表
    public static final String SHARE_WIFI_PWD = "confirmappwd";// 分享热点密码
    public static final String COMMEND_WIFI_PWD = "trycommonpwd";// 获取常用热点密码
    public static final String FEEDBACK = "feedback";// 意见反馈
    public static final String UPDATECLIENT = "updateclient";// 检测更新
    public static final String FREEZE = "freezeAp";// 冻结账号
    public static final String PCTOPHONE = "pcpromotemobile";// pc推手机
    public static final String SHARECONTENT = "getsharecontent";// 获取分享内容
    public static final String UPDATEPWD = "api/updatepwd";// 修改密码
    public static final String FORGET_EMAIL = "api/sendEmailForBack";//邮箱找回密码

    public static final String PERSON_GAME = "game/index";// 游戏中心
    public static final String PERSON_EXCHANGE = "changecenter/index";// 兑换中心
    public static final String PERSON_MESSAGE = "persioncenter/person";// 个人信息
    public static final String PERSON_COIN = "persioncenter/gold";// 赚取金币
    public static final String PERSON_RECEIVE = "persioncenter/myRecord";// 我的领取
    public static final String PERSON_SIGNAL = "persioncenter/spread";// 邀请暗号

    // 广播action
    public static final String UPDATE_WIFI_LIST = "com.tx.internetwizard.UPDATE_WIFI_LIST";

    public static final String UPDATE_WIFI_INFO = "com.tx.internetwizard.UPDATE_WIFI_INFO";

    public static final String WIFI_HOTSPOT = "com.tx.internetwizard.connecter.extra.HOTSPOT";
    /**
     * wifi状态，1：已连接
     */
    public static final String WIFI_DETAIL_TYPE = "com.tx.internetwizard.connecter.extra.detail_type";

    public static final String WIFI_CONNECT_BROADCAST = "com.tx.internetwizard.WIFI.CONNECT.BROADCAST";

    /**
     * 上次wifi密码的action
     */
    public static final String SERVICE_UPLOAD_PASS = "com.tx.internetwizard.uplaod_pass";
    /**
     * apk下载的action
     */
    public static final String SERVICE_DOWNLOAD_APK = "com.tx.internetwizard.download_apk";
    /**
     * apk下载中的action
     */
    public static final String SERVICE_DOWNLOAD_APK_ING = "com.tx.internetwizard.download_apking";
    /**
     * 手动检查更新的action
     */
    public static final String ACTION_HTTP_UPGRADE_HAND = "com.tx.internetwizard.hand_upgrade_apk";
    /**
     * wifi下自动下载检测更新的广播action
     */
    public static final String ACTION_HTTP_UPGRADE_DOWN = "com.tx.internetwizard.down_upgrade_apk";
    /**
     * 启动wifi下自动下载检测更新的的服务
     */
    public static final String ACTION_SERVICE_UPGRADE_DOWN = "com.tx.internetwizard.service_upgrade";
    /**
     * 启动wifi通知服务
     */
    public static final String ACTION_SERVICE_WIFI_NOTIFI = "com.tx.internetwizard.service.WIFI_NOTIFI";
    public static final String ACTION_SERVICE_WIFI_NOTIFI_CLEAR = "com.tx.internetwizard.service.WIFI_NOTIFI_CLEAR";
    /**
     * 下载进度
     */
    public static final String PROGRESS_PERCENT = "DOWNLOAD_APK_PROGESS";
    public static final String DOWNLOAD_SOFTWARE_KEY = "download_software_apk";
    public static final String DOWNLOAD_HIDE_KEY = "download_hide_apk";

    /**
     * 开启时自动检测更新
     */
    public static final int DOWNLOAD_TYPE_AUTO = 1;
    /**
     * 手动检测更新
     */
    public static final int DOWNLOAD_TYPE_HAND = 2;
    /**
     * wifi下后台自动更新下载
     */
    public static final int DOWNLOAD_TYPE_WIFI = 3;

    public static final String WIFI_CURRENT = "ScanResult";
    public static final String WIFI_RESULT_CODE = "resultcode";
    public static final String WIFI_PASSWORD = "wifi_password";
    public static final String PASSWORD_SOURCE = "wifi_password_source";
    public static final String WIFI_PASS_LIST = "wifi_pass_list";
    public static final String PASSWORD_INPUT = "wifi_password_input";

    /**
     * 设置默认wifi工具的值
     */
    public static final String DETAIL_WIFI_SETTING = "com.tx.internetwizard.wifi_setting";
    public static final String WIFI_SETTING_UI = "com.tx.internetwizard.wifi_setting_ui";

    /**
     * wifi开放密码连接
     */
    public static final int WIFI_CONNECT_OPEN = 0x0009;
    /**
     * wifi输入密码连接
     */
    public static final int WIFI_CONNECT_PASS = 0x0010;
    /**
     * wifi试试手气连接
     */
    public static final int WIFI_CONNECT_TRY = 0x0011;
    /**
     * wifi本地以保存的密码
     */
    public static final int WIFI_CONNECT_ROOT = 0x0012;
    /**
     * wifi网络密码
     */
    public static final int WIFI_CONNECT_NET = 0x0013;

    /**
     * 是否有影视列表
     */
    public static final int IS_EXIST_MOVIE_HAS = 0x0014;// 是否有影视列表，有
    public static final int IS_EXIST_MOVIE_NO = 0x0015;// 是否有影视列表，没有

    /**
     * 获取密钥成功
     */
    public static final int GET_SECRET_KEY_OK = 0;

    /**
     * 获取密钥失败
     */
    public static final int GET_SECRET_KEY_ERROR = 1;


    /**
     * 获取皮肤列表成功
     */
    public static final int GET_THEME_LIST_OK = 3;

    /**
     * 获取皮肤列表失败
     */
    public static final int GET_THEME_LIST_ERROR = 4;

    /**
     * 获取皮肤tab成功
     */
    public static final int GET_THEME_TAB_OK = 5;

    /**
     * 获取皮肤tab失败
     */
    public static final int GET_THEME_TAB_ERROR = 6;


    /**
     * 登录成功
     */
    public static final int LOGIN_HTTP_SUC = 20;
    /**
     * 登录失败
     */
    public static final int LOGIN_HTTP_FAIL = 21;

    /**
     * 授权登录成功
     */
    public static final int AUTHORIZE_LOGIN_SUC = 22;
    /**
     * 授权登录失败
     */
    public static final int AUTHORIZE_LOGIN_FAIL = 23;

    /**
     * 获得验证码成功
     */
    public static final int PHONE_CODE_SUC = 24;
    /**
     * 获得验证码失败
     */
    public static final int PHONE_CODE_FAIL = 25;

    /**
     * 注册成功
     */
    public static final int REGISTER_HTTP_SUC = 26;
    /**
     * 注册失败
     */
    public static final int REGISTER_HTTP_FAIL = 27;

    /**
     * 手机找回成功
     */
    public static final int FORGET_PHONE_SUC = 28;
    /**
     * 手机找回失败
     */
    public static final int FORGET_PHONE_FAIL = 29;

    /**
     * 退出登录成功
     */
    public static final int LOGIN_EXIT_SUC = 30;
    /**
     * 退出登录失败
     */
    public static final int LOGIN_EXIT_FAIL = 31;

    /**
     * 自动登录成功
     */
    public static final int LOGIN_AUTO_SUC = 32;
    /**
     * 自动登录失败
     */
    public static final int LOGIN_AUTO_FAIL = 33;

    /**
     * 修改密码成功
     */
    public static final int UPDATE_PWD_SUC = 34;
    /**
     * 修改密码失败
     */
    public static final int UPDATE_PWD_FAIL = 35;

    /**
     * 设置当前皮肤成功
     */
    public static final int SET_CURRENT_THEME_SUC = 36;
    /**
     * 设置当前皮肤失败
     */
    public static final int SET_CURRENT_THEME_FAIL = 37;

    /**
     * 获取sessionid成功
     */
    public static final int GET_SESSIONID_SUC = 38;
    /**
     * 获取sessionid失败
     */
    public static final int GET_SESSIONID_FAIL = 39;

    /**
     * 删除自定义皮肤成功
     */
    public static final int DELETE_DIY_THEME_SUC = 40;
    /**
     * 删除自定义皮肤失败
     */
    public static final int DELETE_DIY_THEME_FAIL = 41;

    /**
     * 获取皮肤详情成功
     */
    public static final int GET_SKIN_DETAIL_SUC = 42;
    /**
     * 获取皮肤详情失败
     */
    public static final int GET_SKIN_DETAIL_FAIL = 43;

    /**
     * 提交我的导航成功
     */
    public static final int SUB_NAV_INFO_SUC = 44;
    /**
     * 提交我的导航失败
     */
    public static final int SUB_NAV_INFO_FAIL = 45;

    /**
     * 获取我的导航成功
     */
    public static final int GET_NAV_INFO_SUC = 46;
    /**
     * 获取我的导航失败
     */
    public static final int GET_NAV_INFO_FAIL = 47;

}
