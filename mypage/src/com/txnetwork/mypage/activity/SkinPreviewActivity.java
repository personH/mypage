package com.txnetwork.mypage.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.txnetwork.mypage.MyApplication;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.cropimage.Crop;
import com.txnetwork.mypage.customview.ProgressBarButton;
import com.txnetwork.mypage.datahandler.DataHandler;
import com.txnetwork.mypage.datahandler.ModifySkinHandler;
import com.txnetwork.mypage.datahandler.OnDataRetrieveListener;
import com.txnetwork.mypage.datahandler.SecretkeyHandler;
import com.txnetwork.mypage.entity.Skin;
import com.txnetwork.mypage.jsonparser.ModifySkinParseModel;
import com.txnetwork.mypage.jsonparser.SecretKeyParserModel;
import com.txnetwork.mypage.utils.*;

import java.io.File;

/**
 * Created by hcz on 2015/1/12.
 */
public class SkinPreviewActivity extends Activity implements OnDataRetrieveListener {
    private Context mContext;

    private static final String TAG = SkinPreviewActivity.class.getSimpleName();

    private static final int CHECK_KEY_SUC = 0x00150;
    private static final int CHECK_KEY_FAIL = 0x00160;

    private ProgressBarButton bnp;
    private ImageView imageView;
    private RelativeLayout background;
    private String url;
    private String mPicName;
    private int mType;
    private int mSize;
    private String mPicid;
    private int navtype;
    private String fileName;
    private ImageView backBtn;
    private ProgressBar mProgressBar;

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
                case 0://下载失败
                    bnp.setText("重新下载");
                    bnp.setOnClickListener(DownloadOnclick);
                    Toast.makeText(mContext, "failure", Toast.LENGTH_SHORT).show();
                    break;
                case 1://下载成功
                    Toast.makeText(mContext, "succes", Toast.LENGTH_SHORT).show();
                    changeBtnState();
                    break;
                case 2://
                    bnp.setMax(msg.arg1);
                    break;
                case 3:
                    bnp.setProgress(msg.arg1);
                    break;
                case ModifySkinParseModel.SET_CURRENT_SKIN_SUC://修改皮肤返回结果 解析结果
                    Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                case ModifySkinParseModel.SET_CURRENT_SKIN_FAIL://皮肤修改失败
                    LogUtils.putLog(mContext, msg.obj.toString());
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        mContext = this;
        Bundle bundle = getIntent().getExtras();
        url = bundle.getString("url");
        mPicName = bundle.getString("picname");
        mType = bundle.getInt("type");
        mSize = bundle.getInt("size");
        mPicid = bundle.getString("picid");
        navtype = bundle.getInt("navtype");
        setContentView(R.layout.activity_skin_preview);
        initView();//

        File file;
        if (mType == Skin.DIY_SKIN) {//自定义
            fileName = url;
            file = new File(fileName);
            mProgressBar.setVisibility(View.GONE);
            Uri uri = Uri.fromFile(new File(fileName));
            if (uri != null) {
                ImageLoader.getInstance().displayImage(uri.toString(), imageView);
            }
        } else {
            fileName = url.substring(url.lastIndexOf("/") + 1);
            fileName = TxNetwork.APP_DOWNLOADFILE_PATH + fileName;
            file = new File(fileName);

            ImageLoader.getInstance().displayImage(url, imageView, UILUtils.options, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    bnp.setEnabled(false);
                    mProgressBar.setVisibility(View.GONE);
                    //Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mProgressBar.setVisibility(View.GONE);
                }
            });
        }
        //如果目标文件已经存在，则删除。产生覆盖旧文件的效果
        if (file.exists()) {
            changeBtnState();
        } else {
            bnp.setOnClickListener(DownloadOnclick);
        }
    }

    private void initView() {
        background = (RelativeLayout) findViewById(R.id.background);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        bnp = (ProgressBarButton) findViewById(R.id.download);
        imageView = (ImageView) findViewById(R.id.image);
        backBtn = (ImageView) this.findViewById(R.id.back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkinPreviewActivity.this.finish();
            }
        });
    }

    HttpDownloadUtils httpDownloadUtils = new HttpDownloadUtils();

    private Runnable saveFileRunnable = new Runnable() {
        @Override
        public void run() {
            httpDownloadUtils.download(url, mHandler);
        }
    };

    View.OnClickListener useTheme = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //1.要不要把图片拷贝到data/data/com.txnetwork.mypage/...
            //2.直接使用外部存储下面的图片文件,保存背景图片的路径到本地到本地.
            //下次读取配置文件,如果是外部走外部的解析过程,设置背景图片
            //成功读取外部图片,则设置,否则,设置系统默认背景
            //如果是系统内部背景,直接读取系统内部背景
        }
    };

    /**
     * 改变按钮状态为"使用"皮肤
     */
    private void changeBtnState() {
        bnp.setText("使用");

        if (navtype == ConstantPool.TAB_DIY) {
            bnp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    beginCrop(fileName, mPicid);//裁剪图片
                }
            });
        } else {
            bnp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedUtils.saveIsBackGroundOut(mContext, true);
                    SharedUtils.saveBackGroundOut(mContext, fileName);
                    SkinPreviewActivity.this.finish();
                    String aesKey = TxNetworkUtil.getUserKey(mContext);
                    if (StringUtil.isNotNull(aesKey)) {
                        setCurrentTheme(aesKey);
                    } else {
                        reqAppkey();
                    }
                    SkinUtils.addNewDownloadedTheme(mContext, fileName, mPicName, mSize, mPicid);
                }
            });
        }


    }

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

    View.OnClickListener DownloadOnclick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            File file = new File(fileName);
            //如果目标文件已经存在，则删除。产生覆盖旧文件的效果
            if (file.exists()) {
                Toast.makeText(mContext, "无需重复下载", Toast.LENGTH_SHORT).show();
            } else {
                new Thread(saveFileRunnable).start();
                bnp.setText("下载中...");
                bnp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }
    };

    private void beginCrop(String source, String picid) {
        Uri imageUri = Uri.fromFile(new File(source));
        Uri outputUri = Uri.fromFile(new File(TxNetwork.APP_DOWNLOADFILE_PATH, "cropped.jpg"));
        new Crop(imageUri, source, picid).output(outputUri).withAspect(TxNetworkUtil.getDisplayWidth(mContext), TxNetworkUtil.getDisplayHeight(mContext)).start(this);
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
                //disDialog();
                LogUtils.putLog(mContext, "请求密钥返回==" + data);
                //mHandler.sendEmptyMessage(CHECK_KEY_FAIL);
                break;
            case ConstantPool.SET_CURRENT_THEME_SUC:
                LogUtils.putLog(mContext, "修改皮肤返回成功==" + data);
                ModifySkinParseModel.parseMainJson(mContext, mHandler, data);
                break;
            case ConstantPool.SET_CURRENT_THEME_FAIL:
                LogUtils.putLog(mContext, "修改皮肤返回失败==" + data);
                break;
        }
    }
}

