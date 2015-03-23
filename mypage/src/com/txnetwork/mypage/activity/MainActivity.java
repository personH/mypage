package com.txnetwork.mypage.activity;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.txnetwork.mypage.MyApplication;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.datacenter.OperateDBUtils;
import com.txnetwork.mypage.datahandler.*;
import com.txnetwork.mypage.entity.TypedUrlList;
import com.txnetwork.mypage.entity.UrlAddr;
import com.txnetwork.mypage.entity.UserBean;
import com.txnetwork.mypage.fragment.MainFragment;
import com.txnetwork.mypage.fragment.RecommendFragment;
import com.txnetwork.mypage.fragment.SkinFragment;
import com.txnetwork.mypage.jsonparser.NavInfoParserModel;
import com.txnetwork.mypage.jsonparser.SecretKeyParserModel;
import com.txnetwork.mypage.jsonparser.SessionidParserModel;
import com.txnetwork.mypage.utils.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnDataRetrieveListener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    protected static final int CHECK_KEY_SUC = 0x00410;
    protected static final int CHECK_KEY_FAIL = 0x00411;

    protected static final int PARSE_NAV_SUC = 0x00414;
    protected static final int PARSE_NAV_FAIL = 0x00415;

    public static final int ADD_LOCAL_THEME = 0x2333;
    public static final int LOGIN_SUC = 0x2334;
    /**
     * Called when the activity is first created.
     */
    private FragmentManager mFManager;
    private Fragment currentFragment;
    private RecommendFragment recommendFragment;
    private MainFragment mainFragment;
    private SkinFragment skinFragment;
    private RelativeLayout first;
    private RelativeLayout second;
    private RelativeLayout third;

    private ImageView recommendImg;
    private ImageView mineImg;
    private ImageView skinImg;

    private TextView recommendTxt;
    private TextView mineTxt;
    private TextView skinTxt;

    private int mCurrentTab = 1;
    public static RelativeLayout background;
    private Context mContext;
    public static ArrayList<TypedUrlList> typedUrlLists = null;
    private String mNavInfo;

    public static String sessionid;
    public static boolean isLoginStatus = false;
    public static boolean autoLogin = false;
    public static boolean isUrlEdit = false;
    private String loginToken;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_KEY_SUC:
                    subNavInfo(mNavInfo);
                    break;
                case CHECK_KEY_FAIL:
                    break;
                case PARSE_NAV_SUC:
                    reloadMainFragment(false);//解析导航新
                    break;
                case PARSE_NAV_FAIL:
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MyApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        mContext = this;
//        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
//        getWindow().setFlags(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);  //设置全屏
        loginToken = SharedUtils.getAutoToken(mContext);
        setContentView(R.layout.activity_main);
        background = (RelativeLayout) findViewById(R.id.background);
        //配置1.设置背景图片
        SkinUtils.setSkin(mContext, background);
        initView();
        initFragment(mCurrentTab);

        //配置2.判断程序是都首次运行
        if (SharedUtils.getFirstRun(mContext)) {
            SharedUtils.setNoFirstRun(mContext);
        }
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(TxNetworkUtil.getDisplayWidth(mContext) / 3, 4);
        params.addRule(RelativeLayout.BELOW, R.id.navigation);

        if (StringUtil.isNotNull(loginToken)) {
            requestSessionid();//请求sessionid
            autoLogin();
        }
    }

    /**
     * 请求sessionid
     */
    private void requestSessionid() {
        SessionidHandler sessionidHandler = new SessionidHandler(mContext);
        sessionidHandler.setOnDataRetrieveListener(this);
        sessionidHandler.startNetWork();
    }

    private void initView() {
        first = (RelativeLayout) findViewById(R.id.tab1);
        second = (RelativeLayout) findViewById(R.id.tab2);
        third = (RelativeLayout) findViewById(R.id.tab3);

        recommendImg = (ImageView) findViewById(R.id.tab1image);
        mineImg = (ImageView) findViewById(R.id.tab2image);
        skinImg = (ImageView) findViewById(R.id.tab3image);

        recommendTxt = (TextView) findViewById(R.id.tab1text);
        mineTxt = (TextView) findViewById(R.id.tab2text);
        skinTxt = (TextView) findViewById(R.id.tab3text);

        first.setOnClickListener(this);
        second.setOnClickListener(this);
        third.setOnClickListener(this);
    }

    private void initFragment(int navIndex) {

        if (navIndex == 0) {
            recommendFragment = new RecommendFragment();
            currentFragment = recommendFragment;
            mCurrentTab = 0;
            recommendImg.setImageResource(R.drawable.tab_recommend_selected);
            recommendTxt.setTextColor(getResources().getColor(R.color.tab_text_color_selected));
        } else if (navIndex == 1) {
            mainFragment = new MainFragment();
            currentFragment = mainFragment;
            mCurrentTab = 1;
            mineImg.setImageResource(R.drawable.tab_mine_selected);
            mineTxt.setTextColor(getResources().getColor(R.color.tab_text_color_selected));
        } else if (navIndex == 2) {
            skinFragment = new SkinFragment();
            currentFragment = skinFragment;
            mCurrentTab = 2;
            skinImg.setImageResource(R.drawable.tab_skin_selected);
            skinTxt.setTextColor(getResources().getColor(R.color.tab_text_color_selected));
        }
        try {
            mFManager = getSupportFragmentManager();
            FragmentTransaction mFTransaction = mFManager.beginTransaction();
            mFTransaction.add(R.id.content, currentFragment);
            mFTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            mFTransaction.commit();
            //MobclickAgent.onEvent(this, currentTabName);// 友盟自定义事件统计
        } catch (Exception e) {
            LogUtils.LOGE(TAG, "iniFragment====>" + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 自动登录
     */
    private void autoLogin() {
        if (isLoginStatus) {
            return;
        }
        if (!TxNetworkUtil.isNetConnected(mContext)) {
            return;
        }

        if (StringUtil.isNotNull(loginToken)) {
            LoginAutoHandler autoHandler = new LoginAutoHandler(mContext);
            autoHandler.setOnDataRetrieveListener(this);
            autoHandler.startNetWork(loginToken);
        }
    }

    @Override
    protected void onResume() {

        //为什么在这?
        SkinUtils.setSkin(mContext, background);
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ADD_LOCAL_THEME) {//添加本地自定义图片后,刷新fragment
            Bundle bundle = new Bundle();
            bundle.putString("diy", "diy");
            this.skinFragment.onCreate(bundle);
        } else if (resultCode == LOGIN_SUC) {//登录成功后刷新本地数据
            reloadSkinFragment();//刷新皮肤界面
            getRemoteUrl();//
        }
    }

    /**
     * 加载服务器导航数据
     */
    private void getRemoteUrl() {
        GetNavHandler getNavHandler = new GetNavHandler(mContext);
        getNavHandler.setOnDataRetrieveListener(this);
        getNavHandler.startNetWork();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab1:
                if (mCurrentTab != 0) {
                    mCurrentTab = 0;
                    if (recommendFragment == null) {
                        recommendFragment = new RecommendFragment();
                    }
                    recommendImg.setImageResource(R.drawable.tab_recommend_selected);
                    mineImg.setImageResource(R.drawable.tab_mine_normal);
                    skinImg.setImageResource(R.drawable.tab_skin_normal);
                    recommendTxt.setTextColor(getResources().getColor(R.color.tab_text_color_selected));
                    mineTxt.setTextColor(getResources().getColor(R.color.tab_text_color_normal));
                    skinTxt.setTextColor(getResources().getColor(R.color.tab_text_color_normal));
                    changeFragment(mCurrentTab, recommendFragment);
                }
                break;
            case R.id.tab2:
                if (mCurrentTab != 1) {
                    mCurrentTab = 1;
                    if (mainFragment == null) {
                        mainFragment = new MainFragment();
                    }
                    recommendImg.setImageResource(R.drawable.tab_recommend_normal);
                    mineImg.setImageResource(R.drawable.tab_mine_selected);
                    skinImg.setImageResource(R.drawable.tab_skin_normal);
                    recommendTxt.setTextColor(getResources().getColor(R.color.tab_text_color_normal));
                    mineTxt.setTextColor(getResources().getColor(R.color.tab_text_color_selected));
                    skinTxt.setTextColor(getResources().getColor(R.color.tab_text_color_normal));
                    changeFragment(mCurrentTab, mainFragment);
                }
                break;
            case R.id.tab3:
                if (mCurrentTab != 2) {
                    mCurrentTab = 2;
                    if (skinFragment == null) {
                        skinFragment = new SkinFragment();
                    }
                    recommendImg.setImageResource(R.drawable.tab_recommend_normal);
                    mineImg.setImageResource(R.drawable.tab_mine_normal);
                    skinImg.setImageResource(R.drawable.tab_skin_selected);

                    recommendTxt.setTextColor(getResources().getColor(R.color.tab_text_color_normal));
                    mineTxt.setTextColor(getResources().getColor(R.color.tab_text_color_normal));
                    skinTxt.setTextColor(getResources().getColor(R.color.tab_text_color_selected));
                    changeFragment(mCurrentTab, skinFragment);
                }
                break;
        }
    }

    /**
     * 切换tab界面
     *
     * @param navIndex
     * @param fragment
     */
    private void changeFragment(int navIndex, Fragment fragment) {

        if (mFManager == null) {
            mFManager = getSupportFragmentManager();
        }

        FragmentTransaction mFTransaction = mFManager.beginTransaction();

        if (mainFragment.getmDslv().isEditing()) {

        }

        if (fragment.isAdded()) {
            mFTransaction.show(fragment);
        } else {
            mFTransaction.add(R.id.content, fragment);
            mFTransaction.show(fragment);
        }
        if (null != currentFragment) {
            mFTransaction.hide(currentFragment);
        }

        mFTransaction.commit();
        currentFragment = fragment;
    }

    @Override
    public void onBackPressed() {

        /**
         * 监测skinfragment的编辑状态
         */
        if (skinFragment != null && skinFragment.isAdded()) {
            if (SkinFragment.isEditable) {
                Bundle bundle = new Bundle();
                bundle.putString("diy", "diy");
                skinFragment.onCreate(bundle);
                return;
            }
        }

        /**
         * 监测mainfragment的编辑状态
         */
        if (mainFragment != null && mainFragment.isAdded()) {
            typedUrlLists = MainFragment.typedUrlLists;
            if (mainFragment.getmDslv().isEditing()) {
                reloadMainFragment(true);
                updateLocalAndRemote(typedUrlLists);//更新数据库
                return;
            } else if (isUrlEdit) {
                updateLocalAndRemote(typedUrlLists);//更新数据库
            }
            MainFragment.typedUrlLists = null;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        //退出登录
        MyApplication.getInstance().clearActivity();
        super.onDestroy();
    }

    private void reloadSkinFragment() {

        if (skinFragment != null) {
            if (mFManager == null) {
                mFManager = getSupportFragmentManager();
            }
            FragmentTransaction mFTransaction = mFManager.beginTransaction();
            if (skinFragment.isAdded()) {
                mFTransaction.remove(skinFragment);
            }
            skinFragment = null;
        }

    }

    /**
     * 重新加载mainfragment
     *
     * @param flag
     */
    private void reloadMainFragment(boolean flag) {

        if (mFManager == null) {
            mFManager = getSupportFragmentManager();
        }
        FragmentTransaction mFTransaction = mFManager.beginTransaction();

        mFTransaction.remove(mainFragment);
        mainFragment = null;
        mainFragment = new MainFragment();

        Bundle savedInstance = new Bundle();
        savedInstance.putBoolean("reload", flag);
        mainFragment.setArguments(savedInstance);
        mFTransaction.add(R.id.content, mainFragment);
        mFTransaction.show(mainFragment);
        if (null != currentFragment) {
            mFTransaction.hide(currentFragment);
        }
        mFTransaction.commit();

        mCurrentTab = 1;
        currentFragment = mainFragment;

        recommendImg.setImageResource(R.drawable.tab_recommend_normal);
        mineImg.setImageResource(R.drawable.tab_mine_selected);
        skinImg.setImageResource(R.drawable.tab_skin_normal);
        recommendTxt.setTextColor(getResources().getColor(R.color.tab_text_color_normal));
        mineTxt.setTextColor(getResources().getColor(R.color.tab_text_color_selected));
        skinTxt.setTextColor(getResources().getColor(R.color.tab_text_color_normal));
    }

    private void updateLocalAndRemote(final List<TypedUrlList> typedUrlLists) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (typedUrlLists != null) {

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("[");

                    ContentResolver resolver = mContext.getContentResolver();
                    resolver.delete(TxNetwork.CONTENT_TYPE_URI, null, null);
                    resolver.delete(TxNetwork.CONTENT_URL_URI, null, null);

                    for (int i = 0; i < typedUrlLists.size(); i++) {
                        TypedUrlList typedUrlList = typedUrlLists.get(i);
                        int type = typedUrlList.getType();
                        int isExpanded = typedUrlList.isExpanded() == true ? 1 : 0;//1表示展开,0表示收起
                        String typeName = typedUrlList.getName();//这个typeName还不是修改过后的name

                        OperateDBUtils.insertNewType(resolver, typeName, type, i, isExpanded);//保存分类

                        stringBuilder.append("{\"sections\":\"").append(typeName).
                                append("\",\"isexpanded\":\"").append(isExpanded).
                                append("\",\"items\":[");

                        List<UrlAddr> urlAddrList = typedUrlList.getUrlAddrTypeList();

                        for (int j = 0; j < urlAddrList.size() - 1; j++) {
                            UrlAddr urlAddr = urlAddrList.get(j);
                            if ("".equals(urlAddr.getUrl()) && "添加".equals(urlAddr.getName())) {

                            } else {
                                OperateDBUtils.insertNewUrl(resolver, urlAddr.getUrl(), urlAddr.getName(), type, j);//添加新导航

                                stringBuilder.append("{\"title\":\"").append(urlAddr.getName()).append("\",");
                                stringBuilder.append("\"site\":\"").append(urlAddr.getUrl()).append("\",");
                                String icon = TxNetworkUtil.getUrlIcon(urlAddr.getUrl());
                                stringBuilder.append("\"icon\":\"").append(icon).append("\"},");
                            }
                        }
                        if (urlAddrList.size() > 1) {//只有添加按钮
                            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        }

                        stringBuilder.append("]},");
                    }
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);

                    stringBuilder.append("]");
                    mNavInfo = stringBuilder.toString();
                    subNavInfo(mNavInfo);
                }
            }
        }).start();
    }


    /**
     * 提交导航信息
     *
     * @param navInfo
     */
    public void subNavInfo(String navInfo) {
        String userid = TxNetworkUtil.getUserId(mContext);
        String aesKey = TxNetworkUtil.getUserKey(mContext);

        if (StringUtil.isNotNull(userid)) {
            if (StringUtil.isNotNull(aesKey)) {
                SubNavHandler subNavHandler = new SubNavHandler(mContext);
                subNavHandler.setOnDataRetrieveListener(this);
                subNavHandler.startNetWork(userid, navInfo, aesKey);
            } else {
                reqAppkey();
            }
        } else {
            //没有登录
        }

    }

    /**
     * 获取手机密钥
     */
    private void reqAppkey() {
        SecretkeyHandler secretkeyHandler = new SecretkeyHandler(mContext);
        secretkeyHandler.setOnDataRetrieveListener(this);
        secretkeyHandler.startNetWork();
    }


    @Override
    public void onDataRetrieve(DataHandler dataHandler, int resultCode, Object data) {
        switch (resultCode) {
            case ConstantPool.GET_SECRET_KEY_OK:
                String dataString = String.valueOf(data);
                LogUtils.putLog(mContext, "获取密钥返回==" + dataString);
                SecretKeyParserModel.parseMainJson(mContext, mHandler, dataString, CHECK_KEY_SUC, CHECK_KEY_FAIL);
                break;
            case ConstantPool.GET_SECRET_KEY_ERROR:
                LogUtils.putLog(mContext, "获取密钥返回==" + data);
                break;
            case ConstantPool.SUB_NAV_INFO_SUC:
                String dataString2 = String.valueOf(data);
                LogUtils.putLog(mContext, "获取密钥返回==" + dataString2);
                SecretKeyParserModel.parseMainJson(mContext, mHandler, dataString2, CHECK_KEY_SUC, CHECK_KEY_FAIL);
                break;
            case ConstantPool.SUB_NAV_INFO_FAIL:
                LogUtils.putLog(mContext, "获取密钥返回==" + data);
                break;
            case ConstantPool.GET_NAV_INFO_SUC:
                LogUtils.putLog(mContext, "请求导航信息返回成功==" + data);
                NavInfoParserModel.parseMainJson(mContext, mHandler, data, PARSE_NAV_SUC, PARSE_NAV_FAIL);
                break;
            case ConstantPool.GET_NAV_INFO_FAIL:
                LogUtils.putLog(mContext, "请求导航信息返回失败==" + data);
                break;
            case ConstantPool.LOGIN_AUTO_SUC:
                LogUtils.putLog(mContext, "自动登录返回==" + data);
                UserBean userBean = LoginHandler.loginParse(String.valueOf(data));
                int errorCode = userBean.getErrorCode();
                if (1 == errorCode) {
                    isLoginStatus = true;
                    autoLogin = true;
                    getRemoteUrl();
                    //this.userBean = userBean;
                }
                break;
            case ConstantPool.LOGIN_AUTO_FAIL:
                LogUtils.putLog(mContext, "自动登录返回==" + data);
                break;
            case ConstantPool.GET_SESSIONID_SUC:
                LogUtils.putLog(mContext, "请求sessionid返回成功==" + data);
                SessionidParserModel.parseMainJson(mContext, data);
                break;
            case ConstantPool.GET_SESSIONID_FAIL:
                LogUtils.putLog(mContext, "请求sessionid返回失败==" + data);
                break;
        }
    }
}
