package com.txnetwork.mypage.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import com.txnetwork.mypage.MyApplication;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.customview.ForgetPassPhone;
import com.txnetwork.mypage.datahandler.*;
import com.txnetwork.mypage.entity.UserBean;
import com.txnetwork.mypage.jsonparser.SecretKeyParserModel;
import com.txnetwork.mypage.utils.ConstantPool;
import com.txnetwork.mypage.utils.LogUtils;
import com.txnetwork.mypage.utils.StringUtil;
import com.txnetwork.mypage.utils.TxNetworkUtil;

public class ForgetPassActivity extends UserActivity implements OnClickListener, OnDataRetrieveListener {
    private static final String TAG = ForgetPassActivity.class.getSimpleName();
    protected static final int CHECK_KEY_FAIL = 0x00450;
    protected static final int CHECK_KEY_SUC = 0x00460;
    private TextView titleText;
    private ImageView backBtn;
    private ScrollView forgetContent;
    private ForgetPassPhone phoneView;
    private ProgressDialog progressDialog;
    private String userid;
    private String passStr;
    private String oldPwd;
    private String random;//验证码
    private TextView rightBtn;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_KEY_SUC:
                    reqPhonePass(userid, passStr, oldPwd, random);
                    break;
                case CHECK_KEY_FAIL:
                    disDialog();
                    Toast.makeText(ForgetPassActivity.this, R.string.forget_pass_fail, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        initView();
    }

    private void initView() {
        rightBtn = (TextView) findViewById(R.id.rightBtn);
        rightBtn.setVisibility(View.INVISIBLE);
        titleText = (TextView) findViewById(R.id.title);
        titleText.setText(R.string.getbackpass);
        backBtn = (ImageView) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);
        forgetContent = (ScrollView) findViewById(R.id.forget_content);
        if (null == phoneView) {
            phoneView = new ForgetPassPhone(this);
        }
        forgetContent.addView(phoneView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                this.finish();
                break;
            default:
                break;
        }
    }


    public void reqPhonePass(String userid, String passStr, String oldPwd, String random) {
        showDialong();
        String keyfield = TxNetworkUtil.getUserKey(this);
        if (StringUtil.isNotNull(keyfield)) {
            ForgetPassHandler forgethandler = new ForgetPassHandler(this);
            forgethandler.setOnDataRetrieveListener(this);
            forgethandler.startNetWork(userid, passStr, oldPwd, random, keyfield);
        } else {
            this.userid = userid;
            this.passStr = passStr;
            this.oldPwd = oldPwd;
            this.random = random;
            reqAppkey();
        }
    }


    private void reqAppkey() {
        SecretkeyHandler secretkeyHandler = new SecretkeyHandler(this);
        secretkeyHandler.setOnDataRetrieveListener(this);
        secretkeyHandler.startNetWork();
    }

    @Override
    public void onDataRetrieve(DataHandler dataHandler, int resultCode,
                               Object data) {
        switch (resultCode) {
            case ConstantPool.GET_SECRET_KEY_OK:
                LogUtils.putLog(this, "请求密钥返回==" + data);
                String dataString = String.valueOf(data);
                SecretKeyParserModel.parseMainJson(this, mHandler, dataString,
                        CHECK_KEY_SUC, CHECK_KEY_FAIL);
                break;
            case ConstantPool.GET_SECRET_KEY_ERROR:
                LogUtils.putLog(this, "请求密钥返回==" + data);
                mHandler.sendEmptyMessage(CHECK_KEY_FAIL);
                break;
            case ConstantPool.FORGET_PHONE_SUC:
                LogUtils.putLog(this, "找回手机密码返回==data==" + data);
                UserBean userBean = LoginHandler.loginParse(String.valueOf(data));
                int errorCode = userBean.getErrorCode();
                if (1 == errorCode) {
                    if (null != callback) {
                        callback.getUserMsg(userBean);
                    }
                    hideKlavier();
                    LoginActivity.getInstance().setResult(MainActivity.LOGIN_SUC, null);
                    LoginActivity.getInstance().finish();
                    this.finish();
                    Toast.makeText(this, R.string.forget_pass_success,
                            Toast.LENGTH_SHORT).show();
                } else if (1025 == errorCode) {
                    Toast.makeText(this, R.string.register_phone_right,
                            Toast.LENGTH_SHORT).show();
                } else if (1024 == errorCode) {
                    Toast.makeText(this, R.string.register_code_right,
                            Toast.LENGTH_SHORT).show();
                } else if (1021 == errorCode) {
                    Toast.makeText(this, R.string.phone_code_overdue,
                            Toast.LENGTH_SHORT).show();
                } else if (1011 == errorCode) {
                    Toast.makeText(this, R.string.person_center_error,
                            Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = userBean.getErrorMsg();
                    if (StringUtil.isNotNull(errorMsg)) {
                        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, R.string.forget_pass_fail,
                                Toast.LENGTH_SHORT).show();
                    }
                }
                disDialog();
                break;
            case ConstantPool.FORGET_PHONE_FAIL:
                LogUtils.putLog(this, "找回手机密码返回==data==" + data);
                Toast.makeText(this, R.string.forget_pass_fail, Toast.LENGTH_SHORT)
                        .show();
                disDialog();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
//		MobclickAgent.onPageEnd(TAG); // 统计页面,保证 onPageEnd 在onPause 之前调用,因为
//										// onPause 中会保存信息
//		MobclickAgent.onPause(this);// 统计时长
    }

    @Override
    protected void onResume() {
        super.onResume();
//		MobclickAgent.onPageStart(TAG); // 统计页面,保证 onPageEnd 在onPause 之前调用,因为
//										// onPause 中会保存信息
//		MobclickAgent.onResume(this);// 统计时长
    }

    public void showDialong() {
        progressDialog = ProgressDialog.show(this,
                getText(R.string.wifi_connect_hint),
                getText(R.string.forget_pass_httping), true, true);

    }

    public void disDialog() {
        if (null != progressDialog) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        disDialog();
        super.onDestroy();
    }

}
