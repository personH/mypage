package com.txnetwork.mypage.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import com.txnetwork.mypage.MyApplication;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.customview.RegisterPhoneView;
import com.txnetwork.mypage.datahandler.*;
import com.txnetwork.mypage.entity.UserBean;
import com.txnetwork.mypage.jsonparser.SecretKeyParserModel;
import com.txnetwork.mypage.utils.ConstantPool;
import com.txnetwork.mypage.utils.LogUtils;
import com.txnetwork.mypage.utils.StringUtil;
import com.txnetwork.mypage.utils.TxNetworkUtil;

public class RegisterActivity extends UserActivity implements OnClickListener,
        OnDataRetrieveListener {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    protected static final int CHECK_KEY_FAIL = 0x00250;
    protected static final int CHECK_KEY_SUC = 0x00260;
    private TextView titleText;
    private ImageView backBtn;
    private ScrollView registerContent;
    private RegisterPhoneView phoneView;
    private ProgressDialog progressDialog;
    private String accountStr;
    private String nicknameStr;
    private String passStr;
    private String random;//验证码
    private int type;
    private TextView rightBtn;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_KEY_SUC:
                    registerAccount(accountStr, nicknameStr, passStr, random, type, false);
                    break;
                case CHECK_KEY_FAIL:
                    disDialog();
                    Toast.makeText(RegisterActivity.this, R.string.register_fail,
                            Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {

        rightBtn = (TextView) findViewById(R.id.rightBtn);
        rightBtn.setText(R.string.login_text);
        rightBtn.setOnClickListener(this);
        titleText = (TextView) findViewById(R.id.title);
        titleText.setText(R.string.mynavigation);
        backBtn = (ImageView) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);
        registerContent = (ScrollView) findViewById(R.id.register_content);
        if (null == phoneView) {
            phoneView = new RegisterPhoneView(this);
        }
        registerContent.addView(phoneView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                this.finish();
                break;
            case R.id.rightBtn:
                Intent loginIntent = new Intent();
                loginIntent.setClass(this, LoginActivity.class);
                startActivity(loginIntent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDataRetrieve(DataHandler dataHandler, int resultCode,
                               Object data) {
        switch (resultCode) {
            case ConstantPool.GET_SECRET_KEY_OK:
                LogUtils.putLog(RegisterActivity.this, "请求密钥返回==" + data);
                String dataString = String.valueOf(data);
                SecretKeyParserModel.parseMainJson(RegisterActivity.this, mHandler,
                        dataString, CHECK_KEY_SUC, CHECK_KEY_FAIL);
                break;
            case ConstantPool.GET_SECRET_KEY_ERROR:
                LogUtils.putLog(RegisterActivity.this, "请求密钥返回==" + data);
                disDialog();
                Toast.makeText(RegisterActivity.this, R.string.register_fail,
                        Toast.LENGTH_SHORT).show();
                break;
            case ConstantPool.REGISTER_HTTP_SUC:
                LogUtils.putLog(this, "注册返回==data==" + data);
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
                    Toast.makeText(this, R.string.register_success, Toast.LENGTH_SHORT).show();
                } else if (1025 == errorCode) {
                    Toast.makeText(this, R.string.register_phone_right,
                            Toast.LENGTH_SHORT).show();
                } else if (1024 == errorCode) {
                    Toast.makeText(this, R.string.register_code_right,
                            Toast.LENGTH_SHORT).show();
                } else if (1016 == errorCode) {
                    Toast.makeText(this, R.string.register_phone_exist,
                            Toast.LENGTH_SHORT).show();
                } else if (1020 == errorCode) {
                    Toast.makeText(this, R.string.register_email_exist,
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
                        Toast.makeText(this, R.string.register_fail,
                                Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case ConstantPool.REGISTER_HTTP_FAIL:
                LogUtils.putLog(this, "注册返回==data==" + data);
                Toast.makeText(this, R.string.register_fail, Toast.LENGTH_SHORT)
                        .show();
            default:
                break;
        }
        disDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //MobclickAgent.onPageEnd(TAG); // 统计页面,保证 onPageEnd 在onPause 之前调用,因为
        // onPause 中会保存信息
        //MobclickAgent.onPause(this);// 统计时长
    }

    @Override
    protected void onResume() {
        super.onResume();
        //MobclickAgent.onPageStart(TAG); // 统计页面,保证 onPageEnd 在onPause 之前调用,因为
        // onPause 中会保存信息
        //MobclickAgent.onResume(this);// 统计时长
    }

    public void showDialong() {
        progressDialog = ProgressDialog.show(this,
                getText(R.string.wifi_connect_hint),
                getText(R.string.register_httping), true, true);

    }

    private void disDialog() {
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

    /**
     * @param accountStr
     * @param nicknameStr
     * @param passStr
     * @param random      验证码
     * @param type
     * @param flag        是否显示加载框
     */
    public void registerAccount(String accountStr, String nicknameStr,
                                String passStr, String random, int type, boolean flag) {
        if (flag) {
            showDialong();
        }
        String aesKey = TxNetworkUtil.getUserKey(this);
        if (StringUtil.isNotNull(aesKey)) {
            RegisterHandler rehandler = new RegisterHandler(this);
            rehandler.setOnDataRetrieveListener(this);
            rehandler.startNetWork(aesKey, accountStr, nicknameStr, passStr, random,
                    type);
        } else {
            this.accountStr = accountStr;
            this.nicknameStr = nicknameStr;
            this.passStr = passStr;
            this.random = random;
            this.type = type;
            SecretkeyHandler secretkeyHandler = new SecretkeyHandler(this);
            secretkeyHandler.setOnDataRetrieveListener(this);
            secretkeyHandler.startNetWork();
        }

    }

}
