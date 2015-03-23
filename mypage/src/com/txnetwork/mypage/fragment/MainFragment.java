package com.txnetwork.mypage.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.activity.LoginActivity;
import com.txnetwork.mypage.activity.MainActivity;
import com.txnetwork.mypage.activity.UserInfoActivity;
import com.txnetwork.mypage.dsgv.DragGridAdapter;
import com.txnetwork.mypage.dslv.DragSortAdapter;
import com.txnetwork.mypage.datacenter.OperateDBUtils;
import com.txnetwork.mypage.datahandler.*;
import com.txnetwork.mypage.dslv.DragSortController;
import com.txnetwork.mypage.dslv.DragSortListView;
import com.txnetwork.mypage.entity.Type;
import com.txnetwork.mypage.entity.TypedUrlList;
import com.txnetwork.mypage.entity.UrlAddr;
import com.txnetwork.mypage.entity.UserBean;
import com.txnetwork.mypage.jsonparser.NavInfoParserModel;
import com.txnetwork.mypage.jsonparser.SecretKeyParserModel;
import com.txnetwork.mypage.jsonparser.SessionidParserModel;
import com.txnetwork.mypage.listener.OnFileLoadListener;
import com.txnetwork.mypage.listener.UserMsgCallback;
import com.txnetwork.mypage.utils.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcz on 2014/12/19.
 * 我的导航,主界面
 * <p/>
 * 拖动排序,不会出现内存无限上涨的过程
 * <p/>
 * 在每次结束拖动都有,内存回收的过程.
 */
public class MainFragment extends Fragment implements OnDataRetrieveListener, UserMsgCallback {

    private static final String TAG = RecommendFragment.class.getSimpleName();
    protected static final int CHECK_KEY_SUC = 0x00410;
    protected static final int CHECK_KEY_FAIL = 0x00411;
    protected static final int LOAD_KEY_PRO = 0x00412;

    protected static final int PARSE_NAV_SUC = 0x00414;
    protected static final int PARSE_NAV_FAIL = 0x00415;

    private Context mContext;
    private ArrayList<UrlAddr> urlAddrList;
    private ArrayList<Type> typeList;

    private TextView loginBtn;
    private ProgressDialog progressDialog;

    private UserBean userBean;
    private boolean isAutoLoginSkip = false;
    private SyncImageLoader syncImageLoader;

    private TextView logoutBtn;

    private static MainFragment mInstance = null;

    private DragSortAdapter adapter;
    public static ArrayList<TypedUrlList> typedUrlLists;
    private DragSortListView mDslv;
    private DragSortController mController;
    /**
     * 执行刷新操作
     * 1.编辑完成,刷新列表,直接使用MainActivity的数据
     * 2.获取服务器列表之后,刷新列表
     * 3.进来直接读取数据库的数据
     */
    private boolean loadDatafromDB = true;
    private TextView alert;
    private ImageView first_add;
    private RelativeLayout add_type;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOAD_KEY_PRO:
                    break;
                case CHECK_KEY_SUC:
                    logout();
                    break;
                case CHECK_KEY_FAIL:
                    break;
                case PARSE_NAV_SUC:
                    loadDatafromDB = true;
                    getLocalUrl();
                    refreshList();
                    break;
                case PARSE_NAV_FAIL:
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public static MainFragment getInstance() {
        return mInstance;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    TypedUrlList item = adapter.getItem(from);
                    adapter.remove(item);
                    adapter.insert(item, to);
                }
            };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        mContext = this.getActivity();
        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.getBoolean("reload")) {
            loadDatafromDB = false;//
        } else {
            getLocalUrl();//
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mine, container, false);

        loginBtn = (TextView) view.findViewById(R.id.login);
        loginBtn.setOnClickListener(myOnClick);
        logoutBtn = (TextView) view.findViewById(R.id.logout);
        logoutBtn.setOnClickListener(myOnClick);
        mDslv = (DragSortListView) view.findViewById(android.R.id.list);
        alert = (TextView) view.findViewById(R.id.alert);
        first_add = (ImageView) view.findViewById(R.id.first_add);
        first_add.setOnClickListener(myOnClick);
        add_type = (RelativeLayout) view.findViewById(R.id.add_type);
        add_type.setOnClickListener(myOnClick);

        intiView();
        refreshList();
        mController = buildController(mDslv);
        mDslv.setFloatViewManager(mController);
        mDslv.setOnTouchListener(mController);
        mDslv.setDragEnabled(true);
        mDslv.setDropListener(onDrop);
        return view;
    }

    private void intiView() {
        if (MainActivity.isLoginStatus) {
            loginBtn.setVisibility(View.INVISIBLE);
            logoutBtn.setVisibility(View.VISIBLE);
        }
    }

    private void refreshList() {

        if (loadDatafromDB) {

            typedUrlLists = new ArrayList<TypedUrlList>();
            for (int i = 0; i < typeList.size(); i++) {//所有分类
                int type = typeList.get(i).getType();
                String typeName = typeList.get(i).getName();
                int isExpanded = typeList.get(i).getIsExpanded();
                List<UrlAddr> urlAddrTypeList = new ArrayList<UrlAddr>();
                for (int j = 0; j < urlAddrList.size(); j++) {
                    if (urlAddrList.get(j).getType() == type) {
                        urlAddrTypeList.add(urlAddrList.get(j));
                    }
                }

                urlAddrTypeList.add(new UrlAddr("添加", "", "", type, urlAddrTypeList.size()));

                DragGridAdapter dragGridAdapter = new DragGridAdapter(mContext, urlAddrTypeList, type, typeName);
                TypedUrlList typedUrlItem;
                typedUrlItem = new TypedUrlList();
                typedUrlItem.setName(typeName);
                typedUrlItem.setType(type);
                typedUrlItem.setDragGridAdapter(dragGridAdapter);
                typedUrlItem.setUrlAddrTypeList(urlAddrTypeList);
                typedUrlItem.setExpanded(isExpanded == 0 ? false : true);
                typedUrlLists.add(typedUrlItem);
            }
        } else {
            typedUrlLists = MainActivity.typedUrlLists;
            MainActivity.typedUrlLists = null;
        }

        if (typedUrlLists != null && typedUrlLists.size() > 0) {
            mDslv.setVisibility(View.VISIBLE);
            alert.setVisibility(View.GONE);
            first_add.setVisibility(View.GONE);
            adapter = new DragSortAdapter(mContext, typedUrlLists);
            mDslv.setAdapter(adapter);
        } else {
            alert.setVisibility(View.VISIBLE);
            first_add.setVisibility(View.VISIBLE);
            mDslv.setVisibility(View.GONE);
        }
    }

    /**
     * 排序列表的controller,设置拖拽模式,以及是否支持拖拽,删除等
     *
     * @param dslv
     * @return
     */
    public DragSortController buildController(DragSortListView dslv) {
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setClickRemoveId(R.id.click_remove);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_LONG_PRESS);
        controller.setRemoveMode(1);
        return controller;
    }

    View.OnClickListener myOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login:
                    login();
                    requestSessionid();//请求sessionid
                    break;
                case R.id.logout:
                    showDialong(mContext.getString(R.string.exit_login_loading));//操作中,请稍等
                    logout();
                    break;
                case R.id.first_add:
                    addNewUrl();
                    break;
                case R.id.add_type:
                    addNewType();
                    break;
            }
        }
    };

    /**
     * 添加导航
     */
    private void addNewUrl() {

        final Dialog dialog = new Dialog(mContext, R.style.loading_dialog);
        dialog.setContentView(R.layout.dialog_main);

        TextView okBtn = (TextView) dialog.findViewById(R.id.ok);
        TextView cancelBtn = (TextView) dialog.findViewById(R.id.cancel);
        final EditText title = (EditText) dialog.findViewById(R.id.title);
        final EditText url = (EditText) dialog.findViewById(R.id.url);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(url.getText())) {
                    Toast.makeText(mContext, "请输入内容", Toast.LENGTH_SHORT).show();
                } else {
                    MainActivity.isUrlEdit = true;
                    String nameStr = title.getText().toString();
                    String urlStr = url.getText().toString();
                    ContentResolver resolver = mContext.getContentResolver();
                    OperateDBUtils.insertNewType(resolver, "未分类", 0, 0, 1);//添加默认分类,默认展开
                    OperateDBUtils.insertNewUrl(resolver, urlStr, nameStr, 0, 0);//添加新导航

                    getLocalUrl();//加载本地数据
                    loadDatafromDB = true;
                    refreshList();
                    dialog.dismiss();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 添加新的分类
     */
    private void addNewType() {
        ContentResolver resolver = mContext.getContentResolver();
        int newType = typedUrlLists.size();
        int newOrder = newType;
        String newTypeName = "未分类";
        OperateDBUtils.insertNewType(resolver, newTypeName, newType, newOrder, 1);
        TypedUrlList typedUrlList = new TypedUrlList();
        typedUrlList.setType(newType);
        typedUrlList.setName(newTypeName);
        List<UrlAddr> urlAddrs = new ArrayList<UrlAddr>();
        urlAddrs.add(new UrlAddr("添加", "", "", newType, 0));
        typedUrlList.setUrlAddrTypeList(urlAddrs);
        DragGridAdapter dragGridAdapter = new DragGridAdapter(mContext, urlAddrs, newType, newTypeName);
        typedUrlList.setDragGridAdapter(dragGridAdapter);
        typedUrlLists.add(0, typedUrlList);
        adapter.notifyDataSetChanged();
        MainActivity.isUrlEdit = true;
    }

    /**
     * 请求sessionid
     */
    private void requestSessionid() {
        SessionidHandler sessionidHandler = new SessionidHandler(mContext);
        sessionidHandler.setOnDataRetrieveListener(this);
        sessionidHandler.startNetWork();
    }

    /**
     * 获取手机密钥
     */
    private void reqAppkey() {
        SecretkeyHandler secretkeyHandler = new SecretkeyHandler(mContext);
        secretkeyHandler.setOnDataRetrieveListener(this);
        secretkeyHandler.startNetWork();
    }

    /**
     * 刷新界面
     *
     * @param userBean
     */
    private void refreshUI(UserBean userBean) {

        loginBtn.setVisibility(View.INVISIBLE);
        logoutBtn.setVisibility(View.VISIBLE);
        MainActivity.isLoginStatus = true;//登录成功
        String userId = userBean.getUserId();
        TxNetworkUtil.setUserId(userId);
        SharedUtils.setUsersId(mContext, userId);
    }

    /**
     * 退出登录
     */
    private void logout() {
        String userid = TxNetworkUtil.getUserId(mContext);
        String aesKey = TxNetworkUtil.getUserKey(mContext);
        if (StringUtil.isNotNull(aesKey)) {
            LoginExitHandler exitHandler = new LoginExitHandler(mContext);
            exitHandler.setOnDataRetrieveListener(this);
            exitHandler.startNetWork(userid, aesKey);
        } else {
            reqAppkey();
        }
    }

    /**
     * 退出登录
     */
    private void exitLogin() {

        MainActivity.isLoginStatus = false;
        MainActivity.sessionid = "";
        TxNetworkUtil.setUserId("");
        SharedUtils.clearUsersId(mContext);
        SharedUtils.clearAutoToken(mContext);
        logoutBtn.setVisibility(View.INVISIBLE);
        loginBtn.setVisibility(View.VISIBLE);
    }


    /**
     * 登录
     */
    private void login() {
        Intent loginIntent = new Intent();
        loginIntent.setClass(mContext, LoginActivity.class);
        startActivityForResult(loginIntent, 0);
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
                disDialog();
                break;
            case ConstantPool.LOGIN_AUTO_SUC:
                LogUtils.putLog(mContext, "自动登录返回==" + data);
                UserBean userBean = LoginHandler.loginParse(String.valueOf(data));
                int errorCode = userBean.getErrorCode();
                if (1 == errorCode) {
                    if (isAutoLoginSkip) {
                        // openActivity(url, itmeTitle, cls);
                    }
                    this.userBean = userBean;
                    refreshUI(userBean);
                } else {
                    Toast.makeText(mContext, R.string.login_auto_fail,
                            Toast.LENGTH_SHORT).show();
                }
                isAutoLoginSkip = false;
                disDialog();
                break;
            case ConstantPool.LOGIN_AUTO_FAIL:
                LogUtils.putLog(mContext, "自动登录返回==" + data);
                Toast.makeText(mContext, R.string.login_auto_fail, Toast.LENGTH_SHORT).show();
                isAutoLoginSkip = false;
                disDialog();
                break;
            case ConstantPool.LOGIN_EXIT_SUC:
                LogUtils.putLog(mContext, "退出登录返回==" + data);
                disDialog();
                exitLogin();
                break;
            case ConstantPool.LOGIN_EXIT_FAIL:
                LogUtils.putLog(mContext, "退出登录返回==" + data);
                disDialog();
                exitLogin();
                break;
            case ConstantPool.GET_SESSIONID_SUC:
                LogUtils.putLog(mContext, "请求sessionid返回成功==" + data);
                SessionidParserModel.parseMainJson(mContext, data);
                break;
            case ConstantPool.GET_SESSIONID_FAIL:
                LogUtils.putLog(mContext, "请求sessionid返回失败==" + data);
                break;
            case ConstantPool.GET_NAV_INFO_SUC:
                LogUtils.putLog(mContext, "请求导航信息返回成功==" + data);
                NavInfoParserModel.parseMainJson(mContext, mHandler, data, PARSE_NAV_SUC, PARSE_NAV_FAIL);
                break;
            case ConstantPool.GET_NAV_INFO_FAIL:
                LogUtils.putLog(mContext, "请求导航信息返回失败==" + data);
                break;
            default:
                break;
        }
    }

    /**
     * 登录成功获得用户信息
     */
    @Override
    public void getUserMsg(UserBean userBean) {
        LogUtils.LOGE(TAG, "==getUserMsg==");
        if (null != userBean) {
            refreshUI(userBean);
            String loginToken = userBean.getLoginToken();
            SharedUtils.setAutoToken(mContext, loginToken);
        }
    }

    @Override
    public void refreshUserMsg(String nickname, String avatarUrl, int coin, int userLevel, String exper) {

    }

    /**
     * 更改昵称
     */
    @Override
    public void refreshName(final String nickname) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                logoutBtn.setText(nickname);
                LogUtils.LOGE(TAG, "js==updateNick==nickname==>" + nickname);
            }
        });
    }

    /**
     * 更改头像
     */
    @Override
    public void refreshAvatar(final String avatarUrl) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                setImageView(avatarImageView, avatarUrl, FileUtil.WIFISW_FILE_AVATAR);
                LogUtils.LOGE(TAG, "js==updateAvatar==avatarUrl==>" + avatarUrl);
            }
        });
    }


    public void showDialong(String msg) {
        progressDialog = ProgressDialog.show(mContext, getText(R.string.wifi_connect_hint), msg, true, true);

    }

    public void disDialog() {
        if (null != progressDialog) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    OnFileLoadListener imageLoadListener = new OnFileLoadListener() {

        @Override
        public void onFileLoad(int t, Object o) {
            LogUtils.LOGV(TAG, "OnFileLoadListener.onFileLoad..., position = " + t);
            Bitmap bitmap = (Bitmap) o;
            if (null != bitmap) {
                //avatarImageView.setImageBitmap((Bitmap) o);
            } else {
                //avatarImageView.setImageResource(R.drawable.ic_launcher);
            }
        }

        @Override
        public void onError(int t) {
            LogUtils.LOGV(TAG, "OnFileLoadListener.onError..., position = " + t);
        }
    };

    /**
     * 获取本地的url数据
     */
    private void getLocalUrl() {
        ContentResolver resolver = mContext.getContentResolver();
        Cursor urlCursor = resolver.query(TxNetwork.CONTENT_URL_URI,
                new String[]{TxNetwork.URL, TxNetwork.NAME, TxNetwork.ICON, TxNetwork.ORDER, TxNetwork.TYPE},
                null, null, "order by type asc,order asc");

        urlAddrList = new ArrayList<UrlAddr>();
        while (urlCursor.moveToNext()) {
            urlAddrList.add(new UrlAddr(urlCursor.getString(urlCursor.getColumnIndex(TxNetwork.NAME)),
                    urlCursor.getString(urlCursor.getColumnIndex(TxNetwork.URL)),
                    urlCursor.getString(urlCursor.getColumnIndex(TxNetwork.ICON)),
                    urlCursor.getInt(urlCursor.getColumnIndex(TxNetwork.TYPE)),
                    urlCursor.getInt(urlCursor.getColumnIndex(TxNetwork.ORDER))));
        }
        urlCursor.close();

        Cursor typeCursor = resolver.query(TxNetwork.CONTENT_TYPE_URI,
                new String[]{TxNetwork.TYPENAME, TxNetwork.TYPE, TxNetwork.ORDER, TxNetwork.ISEXPANDED},
                null, null, "order by type asc");
        typeList = new ArrayList<Type>();
        while (typeCursor.moveToNext()) {
            typeList.add(new Type(
                    typeCursor.getString(typeCursor.getColumnIndex(TxNetwork.TYPENAME)),
                    typeCursor.getInt(typeCursor.getColumnIndex(TxNetwork.TYPE)),
                    typeCursor.getInt(typeCursor.getColumnIndex(TxNetwork.ISEXPANDED)),
                    typeCursor.getInt(typeCursor.getColumnIndex(TxNetwork.ORDER))));
        }
        typeCursor.close();
    }

    private void getRemoteUrl() {
        GetNavHandler getNavHandler = new GetNavHandler(mContext);
        getNavHandler.setOnDataRetrieveListener(this);
        getNavHandler.startNetWork();
    }

    //获取用户信息
    private void getUserInfo() {
        Intent intent = new Intent();
        intent.setClass(mContext, UserInfoActivity.class);
        intent.putExtra("nickname", logoutBtn.getText().toString());
        startActivity(intent);
    }

    public DragSortListView getmDslv() {
        return mDslv;
    }
}
