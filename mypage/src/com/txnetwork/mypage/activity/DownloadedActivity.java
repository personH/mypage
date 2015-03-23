package com.txnetwork.mypage.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import com.txnetwork.mypage.MyApplication;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.adapter.DownloadedSkinAdapter;
import com.txnetwork.mypage.customview.MyGridView;
import com.txnetwork.mypage.datahandler.DataHandler;
import com.txnetwork.mypage.datahandler.ModifySkinHandler;
import com.txnetwork.mypage.datahandler.OnDataRetrieveListener;
import com.txnetwork.mypage.datahandler.SecretkeyHandler;
import com.txnetwork.mypage.entity.DownloadedSkin;
import com.txnetwork.mypage.jsonparser.ModifySkinParseModel;
import com.txnetwork.mypage.jsonparser.SecretKeyParserModel;
import com.txnetwork.mypage.utils.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hcz on 2015/1/5.
 */
public class DownloadedActivity extends Activity implements OnDataRetrieveListener {

    private static final int CHECK_KEY_SUC = 0x00150;
    private static final int CHECK_KEY_FAIL = 0x00160;

    private MyGridView myGridView = null;
    private DownloadedSkinAdapter downloadedSkinAdapter = null;
    private Context mContext;
    private ImageView backBtn;
    private TextView editBtn;
    private TextView doneBtn;
    public static LinearLayout background;
    private List<DownloadedSkin> downloadedSkins = null;
    private String mPicid;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_KEY_SUC:
                    String keyfield = TxNetworkUtil.getUserKey(mContext);
                    setCurrentTheme(keyfield);
                    break;
                case CHECK_KEY_FAIL:
                    Toast.makeText(mContext, R.string.set_theme_fail, Toast.LENGTH_SHORT).show();
                    break;
                case ModifySkinParseModel.SET_CURRENT_SKIN_SUC://修改皮肤返回结果 解析结果
                    Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                case ModifySkinParseModel.SET_CURRENT_SKIN_FAIL://皮肤修改失败
                    LogUtils.putLog(mContext, msg.obj.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.getInstance().addActivity(this);//对于释放内存好像没有什么作用
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_downloaded_main);

        initViews();

        SkinUtils.setSkin(mContext, background);

        getLocalSkins();//加载本地下载皮肤
        downloadedSkinAdapter = new DownloadedSkinAdapter(mContext, downloadedSkins);

        myGridView.setAdapter(downloadedSkinAdapter);
        myGridView.setOnItemClickListener(MyOnItemOclick);
    }

    private void initViews() {
        background = (LinearLayout) findViewById(R.id.background);
        myGridView = (MyGridView) findViewById(R.id.gridview);
        backBtn = (ImageView) this.findViewById(R.id.back);
        backBtn.setOnClickListener(MyOnClickListener);
        editBtn = (TextView) this.findViewById(R.id.edit);
        editBtn.setOnClickListener(MyOnClickListener);
        doneBtn = (TextView) this.findViewById(R.id.done);
        doneBtn.setOnClickListener(MyOnClickListener);
    }

    private void getLocalSkins() {
        downloadedSkins = new ArrayList<DownloadedSkin>();
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(TxNetwork.CONTENT_DOWNLOADED_THEME_URI, new String[]{
                TxNetwork.DRAWABLEID, TxNetwork.PICID, TxNetwork.PICDIR,
                TxNetwork.ISLOCALTHEME, TxNetwork.NAME, TxNetwork.SIZE}, null, null, null);

        while (cursor.moveToNext()) {
            downloadedSkins.add(new DownloadedSkin(cursor.getInt(cursor.getColumnIndex(TxNetwork.DRAWABLEID)),
                    cursor.getString(cursor.getColumnIndex(TxNetwork.PICID)),
                    cursor.getString(cursor.getColumnIndex(TxNetwork.PICDIR)),
                    cursor.getString(cursor.getColumnIndex(TxNetwork.ISLOCALTHEME)),
                    cursor.getString(cursor.getColumnIndex(TxNetwork.NAME)),
                    cursor.getInt(cursor.getColumnIndex(TxNetwork.SIZE))));
        }
        cursor.close();
    }

    View.OnClickListener MyOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    DownloadedActivity.this.finish();
                    break;
                case R.id.edit:
                    //startActivity(new Intent(mContext, UnzipThemeActivity.class));
                    changeToDoneState();
                    break;
                case R.id.done:
                    //startActivity(new Intent(mContext, UnzipThemeActivity.class));
                    changeToEditState();
                    break;
            }
        }
    };

    /**
     * 编辑状态
     */
    private void changeToDoneState() {
        downloadedSkinAdapter = new DownloadedSkinAdapter(mContext, downloadedSkins, true);
        myGridView.setAdapter(downloadedSkinAdapter);
        myGridView.setOnItemClickListener(null);
        editBtn.setVisibility(View.GONE);
        doneBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 正常状态
     */
    private void changeToEditState() {
        downloadedSkinAdapter = new DownloadedSkinAdapter(mContext, downloadedSkins);
        myGridView.setAdapter(downloadedSkinAdapter);
        myGridView.setOnItemClickListener(MyOnItemOclick);
        editBtn.setVisibility(View.VISIBLE);
        doneBtn.setVisibility(View.GONE);
    }

    AdapterView.OnItemClickListener MyOnItemOclick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String isLocalTheme = downloadedSkins.get(position).getIsLocalTheme();
            if (DownloadedSkin.TRUE.equals(isLocalTheme)) {
                //本地默认皮肤
                int drawableId = R.drawable.default_background;
                view.findViewById(R.id.checked).setVisibility(View.VISIBLE);
                SharedUtils.saveBackGround(mContext, drawableId);
                SharedUtils.saveIsBackGroundOut(mContext, false);
                downloadedSkinAdapter.setBackgroundOut(false);
                downloadedSkinAdapter.setDrawableId(drawableId);
                mPicid = "0";//默认皮肤id为 0
            } else {
                String filePath = downloadedSkins.get(position).getPicDirectory();
                String picId = downloadedSkins.get(position).getPicid();
                view.findViewById(R.id.checked).setVisibility(View.VISIBLE);
                SharedUtils.saveIsBackGroundOut(mContext, true);
                SharedUtils.saveBackGroundOut(mContext, filePath);
                downloadedSkinAdapter.setBackgroundOut(true);
                downloadedSkinAdapter.setmCurrentSkinPicPath(filePath);
                mPicid = picId;
            }
            SkinUtils.setSkin(mContext, background);
            downloadedSkinAdapter.setmSelectPosition(position);
            downloadedSkinAdapter.notifyDataSetChanged();
            //保存到服务器
            String aesKey = TxNetworkUtil.getUserKey(mContext);
            if (StringUtil.isNotNull(aesKey)) {
                setCurrentTheme(aesKey);
            } else {
                reqAppkey();
            }
        }
    };

    /**
     * 设置当前皮肤
     *
     * @param aesKey
     */
    private void setCurrentTheme(String aesKey) {
        String userid = TxNetworkUtil.getUserId(mContext);//获取用户id
        ModifySkinHandler modifySkinHandler = new ModifySkinHandler(mContext);
        modifySkinHandler.setOnDataRetrieveListener(this);
        modifySkinHandler.startNetWork(userid, mPicid, aesKey);
    }

    /**
     * 获取手机密钥
     */
    private void reqAppkey() {
        SecretkeyHandler secretkeyHandler = new SecretkeyHandler(this);
        secretkeyHandler.setOnDataRetrieveListener(this);
        secretkeyHandler.startNetWork();
    }

    @Override
    public void onDataRetrieve(DataHandler dataHandler, int resultCode, Object data) {
        switch (resultCode) {
            case ConstantPool.GET_SECRET_KEY_OK:
                String dataString = String.valueOf(data);
                LogUtils.putLog(mContext, "请求密钥返回==" + dataString);
                SecretKeyParserModel.parseMainJson(mContext, mHandler, dataString, CHECK_KEY_SUC, CHECK_KEY_FAIL);
                break;
            case ConstantPool.GET_SECRET_KEY_ERROR:
                LogUtils.putLog(mContext, "请求密钥返回==" + data);
                break;
            case ConstantPool.SET_CURRENT_THEME_SUC:
                LogUtils.putLog(mContext, "修改皮肤返回成功==" + data);
                ModifySkinParseModel.parseMainJson(mContext, mHandler, data);
                break;
            case ConstantPool.SET_CURRENT_THEME_FAIL:
                LogUtils.putLog(mContext, "修改皮肤返回失败==" + data);
                Toast.makeText(mContext, R.string.set_theme_fail, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {

        /**
         * 不停的在已下载和皮肤管理界面切换
         *
         * app的内存占用,越来越高
         *
         * 应该在此处回收所有内存
         */
        //1.无效
        //downloadedSkins = null;
        //downloadedSkinAdapter = null;
        //myGridView = null;
        //2.稍微有点反应
        //System.gc();
        //无效
        //ImageLoader.getInstance().clearMemoryCache();
        background = null;
        super.onDestroy();
    }
}
