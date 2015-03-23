package com.txnetwork.mypage.fragment;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.activity.DownloadedActivity;
import com.txnetwork.mypage.activity.LocalPicActivity;
import com.txnetwork.mypage.activity.MainActivity;
import com.txnetwork.mypage.activity.SkinPreviewActivity;
import com.txnetwork.mypage.adapter.SkinAdapter;
import com.txnetwork.mypage.datahandler.*;
import com.txnetwork.mypage.entity.DiySkin;
import com.txnetwork.mypage.entity.SkinInfo;
import com.txnetwork.mypage.entity.SkinTab;
import com.txnetwork.mypage.entity.Skin;
import com.txnetwork.mypage.jsonparser.SecretKeyParserModel;
import com.txnetwork.mypage.jsonparser.ThemeListParserModel;
import com.txnetwork.mypage.jsonparser.ThemeTabParserModel;
import com.txnetwork.mypage.pulltorefresh.PullToRefreshBase;
import com.txnetwork.mypage.pulltorefresh.PullToRefreshGridView;
import com.txnetwork.mypage.utils.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/12/19.
 */
public class SkinFragment extends Fragment implements OnDataRetrieveListener {

    private static final int THEME_TAB_PARSE_SUC = 0x00150;
    private static final int THEME_TAB_PARSE_FAIL = 0x00160;
    private static final int THEME_LIST_PARSE_SUC = 0x00170;
    private static final int THEME_LIST_PARSE_FAIL = 0x00180;
    public static final int CHECK_KEY_SUC = 0x00151;
    public static final int CHECK_KEY_FAIL = 0x00161;

    private Context mContext;
    private Resources resources;
    private GridView mGridView;
    private PullToRefreshGridView pullToRefreshGridview;
    private SkinAdapter skinAdapter;
    private LinearLayout navigationTab;

    private int mNavType;
    private TextView downloaded;
    public String mPicid;

    private int mWebPageNo = 0;//
    private int mDiyPageNo = 0;//
    private int mPageSize = ConstantPool.WEB_SKIN_PAGE_SIZE;//

    private PullToRefreshBase.Mode webRefreshMode = PullToRefreshBase.Mode.PULL_FROM_END;
    private PullToRefreshBase.Mode diyRefreshMode = PullToRefreshBase.Mode.PULL_FROM_END;
    //获取缩略图链接
    List<Skin> webSkinArrayList = null;
    List<Skin> diySkinArrayList = null;

    public static boolean isEditable = false;//自定义皮肤编辑状态标识

    private String mUserId;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_KEY_SUC:
                    String keyfield = TxNetworkUtil.getUserKey(mContext);
                    deleteDiyTheme(mPicid, keyfield);
                    break;
                case CHECK_KEY_FAIL:
                    Toast.makeText(mContext, R.string.set_theme_fail, Toast.LENGTH_SHORT).show();
                    break;
                case THEME_TAB_PARSE_SUC://加载tab页成功
                    List<SkinTab> skinTabList = (List<SkinTab>) msg.obj;
                    initTab(skinTabList);
                    break;
                case THEME_TAB_PARSE_FAIL://加载tab页失败
                    mNavType = ConstantPool.TAB_DIY;
                    noNetworkLoadDiySkin();
                    break;
                case THEME_LIST_PARSE_SUC:
                    List<SkinInfo> themeList = (List<SkinInfo>) msg.obj;
                    if (mNavType == ConstantPool.TAB_DIY) {
                        initDiyThemeList(mDiyPageNo, themeList, true);
                    } else {
                        initWebThemeList(mWebPageNo, themeList);
                    }
                    break;
                case THEME_LIST_PARSE_FAIL://解析皮肤列表失败
                    if (msg.obj != null) {
                        Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    }
                    initDiyThemeList(mDiyPageNo, null, true);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        resources = mContext.getResources();

        if (savedInstanceState != null && "diy".equals(savedInstanceState.getString("diy"))) {
            mNavType = ConstantPool.TAB_DIY;
            navigationTab.removeAllViews();
            mDiyPageNo = 0;
            diyRefreshMode = PullToRefreshBase.Mode.PULL_FROM_END;
            diySkinArrayList = null;
            pullToRefreshGridview.setMode(diyRefreshMode);
        }
        /**
         * 1.有网络有登录
         * 2.有网络没有登录
         * 3.没有网络
         */
        if (TxNetworkUtil.isNetConnected(mContext)) {
            getSkinTab();
        } else {
            noNetworkLoadDiySkin();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_skin, container, false);
        pullToRefreshGridview = (PullToRefreshGridView) view.findViewById(R.id.gridview);
        initView(view);
        return view;
    }

    /**
     * @param view
     */
    private void initView(View view) {
        downloaded = (TextView) view.findViewById(R.id.downloaded);
        downloaded.setOnClickListener(DownloadOnClickListener);
        navigationTab = (LinearLayout) view.findViewById(R.id.navigation);
    }


    /**
     * 初始化tab页面
     *
     * @param skinTabList
     */
    private void initTab(List<SkinTab> skinTabList) {
        if (skinTabList != null && skinTabList.size() > 0) {

            if (mNavType != ConstantPool.TAB_DIY) {
                mNavType = skinTabList.get(0).getTabtype();
                getSkinList(mNavType, mWebPageNo, false, mPageSize);//初始化当前的tab界面
            } else {
                if (isEditable == true) {
                    isEditable = false;
                    getSkinList(mNavType, mDiyPageNo, true, mPageSize);//初始化当前的tab界面
                } else {
                    getSkinList(mNavType, mDiyPageNo, true, mPageSize + 1);//初始化当前的tab界面
                }
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.weight = 1;
            for (int i = 0; i < skinTabList.size(); i++) {
                TextView tabView = new TextView(mContext);
                tabView.setTag(skinTabList.get(i));
                tabView.setText(skinTabList.get(i).getTabname());
                tabView.setGravity(Gravity.CENTER);
                tabView.setTextColor(resources.getColor(R.color.text_color_nine));
                tabView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_26));
                tabView.setLayoutParams(layoutParams);
                tabView.setOnClickListener(TabOnClickListener);
                if (mNavType == skinTabList.get(i).getTabtype()) {
                    tabView.setTextColor(resources.getColor(R.color.blue));
                }
                navigationTab.addView(tabView);
            }
        }
    }

    /**
     * 初始化皮肤列表
     *
     * @param themeList
     */
    private void initWebThemeList(int pageno, List<SkinInfo> themeList) {

        if (pageno == 0) {//初始化
            webSkinArrayList = new ArrayList<Skin>();
        }

        if (themeList != null && themeList.size() > 0) {
            for (int i = 0; i < themeList.size(); i++) {
                //根据当前分辨率,匹配最优图片
                SkinInfo temp = themeList.get(i);
                String picid = temp.getPicid();
                String pic_url_s = ConstantPool.PIC_SERVER_URL + temp.getPicpath() + "/240_320/" + "s_" + temp.getNewname();
                String pic_url = ConstantPool.PIC_SERVER_URL + temp.getPicpath() + "/240_320/" + temp.getNewname();
                String name = temp.getPicname();
                int size = temp.getPicsize();
                webSkinArrayList.add(new Skin(picid, pic_url_s, pic_url, name, size, Skin.WEB_SKIN));
            }
        }
        setRefreshMode(themeList.size());
        if (pageno == 0) {
            skinAdapter = new SkinAdapter(mContext, webSkinArrayList, false, mHandler);
            pullToRefreshGridview.setAdapter(skinAdapter);
            pullToRefreshGridview.setOnRefreshListener(WebPullRefresh);
            pullToRefreshGridview.setOnItemClickListener(WebThemeItemOnclick);
        } else {
            skinAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 初始化自定义皮肤列表
     *
     * @param pageno
     * @param themeList
     * @param login     是否登录
     */
    private void initDiyThemeList(int pageno, List<SkinInfo> themeList, boolean login) {

        int listSize = 0;

        if ((pageno == 0 && diySkinArrayList == null) || !login) {//初始化
            diySkinArrayList = new ArrayList<Skin>();
        } else {
            removeAddBtn(diySkinArrayList);
        }

        if (themeList != null && themeList.size() > 0) {
            listSize = themeList.size();
            for (int i = 0; i < listSize; i++) {
                SkinInfo temp = themeList.get(i);
                String picid = temp.getPicid();
                String pic_url_s = ConstantPool.PIC_SERVER_URL + temp.getPicpath() + "/s_" + temp.getNewname();
                String pic_url = ConstantPool.PIC_SERVER_URL + temp.getPicpath() + "/" + temp.getNewname();
                String name = temp.getPicname();
                int size = temp.getPicsize();
                diySkinArrayList.add(new Skin(picid, pic_url_s, pic_url, name, size, Skin.WEB_SKIN));
            }
        }

        if (listSize < ConstantPool.MAX_WEB_DIY_SKIN) {//当网络皮肤不超过最大自定义皮肤个数时
            getLocalDiyThemeList(listSize);//添加本地皮肤
        }
        skinAdapter = new SkinAdapter(mContext, diySkinArrayList, false, mHandler);
        pullToRefreshGridview.setAdapter(skinAdapter);
        pullToRefreshGridview.setMode(PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY);//不支持刷新,自定义上传图片最多不超过5张
        pullToRefreshGridview.setOnRefreshListener(DiyPullRefresh);
        pullToRefreshGridview.setOnItemClickListener(DiyThemeItemOnclick);
        mGridView = pullToRefreshGridview.getRefreshableView();
        mGridView.setOnItemLongClickListener(onItemLongClickListener);
    }

    /**
     * 没有网络情况下,加载本地自定义皮肤列表
     */
    private void noNetworkLoadDiySkin() {
        navigationTab.removeAllViews();
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.weight = 1;
        TextView tabView = new TextView(mContext);
        tabView.setText("自定义");
        tabView.setTextColor(resources.getColor(R.color.blue));
        tabView.setGravity(Gravity.CENTER);
        tabView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.text_size_26));
        tabView.setLayoutParams(layoutParams);
        tabView.setOnClickListener(TabOnClickListener);
        navigationTab.addView(tabView);
        mNavType = ConstantPool.TAB_DIY;
        initDiyThemeList(mDiyPageNo, null, false);
    }

    /**
     * tab页面点击事件
     * 1.初始化 当前页面的其他页面,如自定义页面
     * 2.跳转到其他界面
     */
    View.OnClickListener TabOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getTag() != null) {
                TextView tabView = (TextView) v;
                setSelectedTabColor();
                tabView.setTextColor(resources.getColor(R.color.blue));
                SkinTab skinTab = (SkinTab) v.getTag();
                if (mNavType != skinTab.getTabtype()) {
                    mNavType = skinTab.getTabtype();
                    if (mNavType != ConstantPool.TAB_NEWEST) {
                        //自定义tab页
                        mUserId = TxNetworkUtil.getUserId(mContext);
                        mNavType = ConstantPool.TAB_DIY;

                        if (StringUtil.isNotNull(mUserId)) {
                            //已登录
                            getSkinList(mNavType, mDiyPageNo, false, mPageSize);
                        } else {
                            //未登录
                            initDiyThemeList(mDiyPageNo, null, false);
                        }
                    } else {
                        getSkinList(mNavType, mWebPageNo, false, mPageSize);
                    }
                } else {
                    //点击当前tab页面,无需响应
                }
            }
        }
    };

    /**
     * 设置tab标签的字体颜色
     */
    private void setSelectedTabColor() {
        for (int i = 0; i < navigationTab.getChildCount(); i++) {
            TextView tabView = (TextView) navigationTab.getChildAt(i);
            tabView.setTextColor(resources.getColor(R.color.text_color_nine));
        }
    }

    View.OnClickListener DownloadOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.downloaded:
                    startActivity(new Intent(mContext, DownloadedActivity.class));
                    break;
            }
        }
    };

    private void getSkinTab() {
        SkinTabHandler handler = new SkinTabHandler(mContext);
        handler.setOnDataRetrieveListener(this);
        handler.startNetWork();
    }

    private void getSkinList(int navtype, int pageno, boolean loadMore, int pagesize) {

        if (mNavType == ConstantPool.TAB_DIY && diySkinArrayList != null && loadMore == false) {
            skinAdapter = new SkinAdapter(mContext, diySkinArrayList, false, mHandler);
            pullToRefreshGridview.setAdapter(skinAdapter);
            pullToRefreshGridview.setOnRefreshListener(DiyPullRefresh);
            pullToRefreshGridview.setMode(diyRefreshMode);
            pullToRefreshGridview.setOnItemClickListener(DiyThemeItemOnclick);
            mGridView = pullToRefreshGridview.getRefreshableView();
            mGridView.setOnItemLongClickListener(onItemLongClickListener);
        } else if (mNavType == ConstantPool.TAB_NEWEST && webSkinArrayList != null && loadMore == false) {
            skinAdapter = new SkinAdapter(mContext, webSkinArrayList, false, mHandler);
            pullToRefreshGridview.setAdapter(skinAdapter);
            pullToRefreshGridview.setOnRefreshListener(WebPullRefresh);
            pullToRefreshGridview.setMode(webRefreshMode);
            pullToRefreshGridview.setOnItemClickListener(WebThemeItemOnclick);
            mGridView = pullToRefreshGridview.getRefreshableView();
            mGridView.setOnItemLongClickListener(null);
            if (isEditable) {
                isEditable = false;
                returnAddBtn(diySkinArrayList);
            }
        } else {
            SkinListHandler handler = new SkinListHandler(mContext, navtype, pageno, pagesize);
            handler.setOnDataRetrieveListener(this);
            handler.startNetWork();
        }
    }

    /**
     * 根据上次加载的item数量,控制ptr是否继续支持刷新
     *
     * @param listSize
     */
    private void setRefreshMode(int listSize) {
        if (listSize >= mPageSize) {//可能有更多数据待加载
            if (mNavType == ConstantPool.TAB_DIY) {
                diyRefreshMode = PullToRefreshBase.Mode.PULL_FROM_END;
            } else {
                webRefreshMode = PullToRefreshBase.Mode.PULL_FROM_END;
            }
            pullToRefreshGridview.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        } else {
            if (mNavType == ConstantPool.TAB_DIY) {
                diyRefreshMode = PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY;
            } else {
                webRefreshMode = PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY;
            }
            pullToRefreshGridview.setMode(PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY);
        }

    }


    /**
     * 删除自定义皮肤
     *
     * @param picid
     * @param aesKey
     */
    private void deleteDiyTheme(String picid, String aesKey) {
        DeleteSkinHandler deleteSkinHandler = new DeleteSkinHandler(mContext);
        deleteSkinHandler.setOnDataRetrieveListener(this);
        deleteSkinHandler.startNetWork(picid, aesKey);
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
            case ConstantPool.GET_THEME_TAB_OK:
                String dataString = String.valueOf(data);
                LogUtils.putLog(mContext, "获取皮肤tab页面返回成功==" + dataString);
                ThemeTabParserModel.parseMainJson(mContext, mHandler, dataString, THEME_TAB_PARSE_SUC, THEME_TAB_PARSE_FAIL);
                break;
            case ConstantPool.GET_THEME_TAB_ERROR:
                LogUtils.putLog(mContext, "获取皮肤tab页面返回失败==" + data);
                mHandler.sendEmptyMessage(THEME_TAB_PARSE_FAIL);
                break;
            case ConstantPool.GET_THEME_LIST_OK:
                String dataList = String.valueOf(data);
                LogUtils.putLog(mContext, "获取皮肤列表页面返回成功==" + dataList);
                ThemeListParserModel.parseMainJson(mContext, mHandler, dataList, THEME_LIST_PARSE_SUC, THEME_LIST_PARSE_FAIL);
                break;
            case ConstantPool.GET_THEME_LIST_ERROR:
                LogUtils.putLog(mContext, "获取皮肤列表页面返回失败==" + data);
                mHandler.sendEmptyMessage(THEME_LIST_PARSE_FAIL);
                break;
            case ConstantPool.GET_SECRET_KEY_OK:
                String resutStr = String.valueOf(data);
                LogUtils.putLog(mContext, "请求密钥返回==" + resutStr);
                SecretKeyParserModel.parseMainJson(mContext, mHandler, resutStr, CHECK_KEY_SUC, CHECK_KEY_FAIL);
                break;
            case ConstantPool.GET_SECRET_KEY_ERROR:
                //disDialog();
                LogUtils.putLog(mContext, "请求密钥返回==" + data);
                //mHandler.sendEmptyMessage(CHECK_KEY_FAIL);
                break;
            case ConstantPool.SET_CURRENT_THEME_SUC:
                LogUtils.putLog(mContext, "修改皮肤返回成功==" + data);
                break;
            case ConstantPool.SET_CURRENT_THEME_FAIL:
                LogUtils.putLog(mContext, "修改皮肤返回失败==" + data);
                break;
            case ConstantPool.DELETE_DIY_THEME_SUC:
                LogUtils.putLog(mContext, "删除成功");
                break;
            case ConstantPool.DELETE_DIY_THEME_FAIL:
                LogUtils.putLog(mContext, "删除失败");
                break;
        }

    }

    /**
     * 自定义皮肤tab页面的点击事件
     */
    AdapterView.OnItemClickListener DiyThemeItemOnclick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (diySkinArrayList.get(position).getType() == Skin.OTHERS) {
                Intent intent = new Intent(mContext, LocalPicActivity.class);
                startActivityForResult(intent, MainActivity.ADD_LOCAL_THEME);
            } else {
                Intent intent = new Intent(mContext, SkinPreviewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                String url = diySkinArrayList.get(position).getUrl();
                String picid = diySkinArrayList.get(position).getPicid();
                String name = diySkinArrayList.get(position).getName();
                int size = diySkinArrayList.get(position).getSize();
                int type = diySkinArrayList.get(position).getType();
                intent.putExtra("url", url);
                intent.putExtra("picid", picid);
                intent.putExtra("picname", name);
                intent.putExtra("type", type);
                intent.putExtra("size", size);
                intent.putExtra("navtype", mNavType);
                startActivity(intent);
            }
        }
    };

    AdapterView.OnItemClickListener WebThemeItemOnclick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (webSkinArrayList.get(position).getType() == Skin.OTHERS) {
                Intent intent = new Intent(mContext, LocalPicActivity.class);
                startActivityForResult(intent, MainActivity.ADD_LOCAL_THEME);
            } else {
                Intent intent = new Intent(mContext, SkinPreviewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                String url = webSkinArrayList.get(position).getUrl();
                String picid = webSkinArrayList.get(position).getPicid();
                String name = webSkinArrayList.get(position).getName();
                int size = webSkinArrayList.get(position).getSize();
                int type = webSkinArrayList.get(position).getType();
                intent.putExtra("url", url);
                intent.putExtra("picid", picid);
                intent.putExtra("picname", name);
                intent.putExtra("type", type);
                intent.putExtra("size", size);
                intent.putExtra("navtype", mNavType);
                startActivity(intent);
            }
        }
    };

    PullToRefreshBase.OnRefreshListener2<GridView> WebPullRefresh = new PullToRefreshBase.OnRefreshListener2<GridView>() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
            //
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
            //上划刷新
            getSkinList(mNavType, ++mWebPageNo, true, mPageSize);
            pullToRefreshGridview.onRefreshComplete();
        }
    };
    PullToRefreshBase.OnRefreshListener2<GridView> DiyPullRefresh = new PullToRefreshBase.OnRefreshListener2<GridView>() {
        @Override
        public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
            //
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
            //上划刷新
            getSkinList(mNavType, ++mDiyPageNo, true, mPageSize);
            pullToRefreshGridview.onRefreshComplete();
        }
    };

    /**
     * 自定义皮肤长按删除
     */
    AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            isEditable = true;
            removeAddBtn(diySkinArrayList);

            skinAdapter = new SkinAdapter(mContext, diySkinArrayList, isEditable, mHandler);
            pullToRefreshGridview.setAdapter(skinAdapter);
            pullToRefreshGridview.setOnItemClickListener(null);
            pullToRefreshGridview.setMode(PullToRefreshBase.Mode.MANUAL_REFRESH_ONLY);
            return true;
        }
    };

    /**
     * 移出添加btn
     *
     * @param list
     */
    private void removeAddBtn(List<Skin> list) {
        Skin lastSkin = list.get(list.size() - 1);
        if (Skin.OTHERS == lastSkin.getType()) {
            list.remove(list.size() - 1);
        }
    }

    /**
     * 添加btn
     *
     * @param list
     */
    private void returnAddBtn(List<Skin> list) {
        Skin lastSkin = list.get(list.size() - 1);
        if (Skin.OTHERS != lastSkin.getType()) {
            list.add(new Skin());
        }
    }

    /**
     * 查询本地自定义皮肤
     */
    private void getLocalDiyThemeList(int webSize) {
        boolean doShowAddBtn = true;
        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor cursor;
        if (TxNetworkUtil.isNetConnected(mContext) && StringUtil.isNotNull(TxNetworkUtil.getUserId(mContext))) {
            //网络连通,并且登录
            cursor = contentResolver.query(TxNetwork.CONTENT_DIY_THEME_URI,
                    new String[]{TxNetwork.PICID, TxNetwork.PICDIR, TxNetwork.NAME, TxNetwork.SIZE},
                    TxNetwork.UPLOADED + "=?", new String[]{DiySkin.FALSE}, "datecreated asc");

            int totalCount = cursor.getCount() + webSize;
            if (totalCount >= ConstantPool.MAX_WEB_DIY_SKIN) {
                doShowAddBtn = false;
            }
        } else {
            //网络断开,或网络连通但未登录
            cursor = contentResolver.query(TxNetwork.CONTENT_DIY_THEME_URI,
                    new String[]{TxNetwork.PICID, TxNetwork.PICDIR, TxNetwork.NAME, TxNetwork.SIZE},
                    null, null, "datecreated asc");
            if (cursor.getCount() >= ConstantPool.MAX_LOCAL_DIY_SKIN) {
                doShowAddBtn = false;
            }
        }

        while (cursor.moveToNext()) {
            diySkinArrayList.add(new Skin(cursor.getString(cursor.getColumnIndex(TxNetwork.PICID)),
                    cursor.getString(cursor.getColumnIndex(TxNetwork.PICDIR)),
                    cursor.getString(cursor.getColumnIndex(TxNetwork.PICDIR)),
                    cursor.getString(cursor.getColumnIndex(TxNetwork.NAME)),
                    cursor.getInt(cursor.getColumnIndex(TxNetwork.SIZE)), Skin.DIY_SKIN));
        }
        if (doShowAddBtn) {
            diySkinArrayList.add(new Skin());//添加按钮
        }
        cursor.close();
    }
}
