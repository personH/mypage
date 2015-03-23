package com.txnetwork.mypage.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;
import com.txnetwork.mypage.MyApplication;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.datahandler.*;
import com.txnetwork.mypage.entity.AuthorizeInfo;
import com.txnetwork.mypage.entity.UserBean;
import com.txnetwork.mypage.fragment.MainFragment;
import com.txnetwork.mypage.jsonparser.SecretKeyParserModel;
import com.txnetwork.mypage.utils.*;

/**
 * 登录
 *
 * @author dongxl
 */
public class LoginActivity extends UserActivity implements OnClickListener, OnDataRetrieveListener {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int CHECK_KEY_SUC = 0x00150;
    private static final int CHECK_KEY_FAIL = 0x00160;
    private QQAuth mQQAuth;
    private ImageView backBtn;
    private TextView titleText;
    private EditText accountEdit, passEdit;
    private Button loginBtn, QQLoginBtn;
    private TextView forgetBtn;
    private ProgressDialog progressDialog;
    private AuthorizeInfo authorize;
    private String username;
    private String password;
    private ImageView rememPassImg;
    private TextView rememPassTextView;
    private Context mContext;

    private TextView rightBtn;

    private boolean isRememberPass;
    /**
     * 1手动登录，2授权登录
     */
    private int loginMode;
    /**
     * 是否可以请求两次，true可以
     */
    private boolean isHttpTwice = false;

    private static LoginActivity mInstance = null;

    public static LoginActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getIntentData(intent);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK_KEY_SUC:
                    if (1 == loginMode) {
                        String keyfield = TxNetworkUtil.getUserKey(LoginActivity.this);
                        reqLogin(keyfield);
                    } else if (2 == loginMode) {
                        authorizeLogin();
                    }
                    break;
                case CHECK_KEY_FAIL:
                    disDialog();
                    Toast.makeText(LoginActivity.this, R.string.login_fail, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    private void getIntentData(Intent intent) {
        mInstance = this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_login);
        getIntentData(getIntent());
        initView();
    }

    private void initView() {

        accountEdit = (EditText) findViewById(R.id.login_account_edit);
        passEdit = (EditText) findViewById(R.id.login_pass_edit);

        isRememberPass = SharedUtils.getRememberPass(mContext);
        rememPassImg = (ImageView) findViewById(R.id.remember_pass_img);
        if (isRememberPass) {
            rememPassImg.setImageResource(R.drawable.checkbox_yes);
            accountEdit.setText(SharedUtils.getUsername(mContext));
            passEdit.setText(SharedUtils.getPassword(mContext));
        } else {
            rememPassImg.setImageResource(R.drawable.checkbox_no);
        }
        rememPassImg.setOnClickListener(this);

        rememPassTextView = (TextView) findViewById(R.id.remember_pass_btn);
        rememPassTextView.setOnClickListener(this);

        rightBtn = (TextView) findViewById(R.id.rightBtn);
        rightBtn.setText(R.string.register_text);
        rightBtn.setOnClickListener(this);

        titleText = (TextView) findViewById(R.id.title);
        titleText.setText(R.string.mynavigation);

        backBtn = (ImageView) findViewById(R.id.back_btn);
        backBtn.setOnClickListener(this);

        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);

        forgetBtn = (TextView) findViewById(R.id.forget_pass_btn);
        forgetBtn.setOnClickListener(this);
        QQLoginBtn = (Button) findViewById(R.id.qq_login_btn);
        QQLoginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                hideKlavier();
                this.finish();
                break;
            case R.id.login_btn:
                loginSubmit();
                break;
            case R.id.rightBtn:
                Intent registerIntent = new Intent();
                registerIntent.setClass(this, RegisterActivity.class);
                registerIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(registerIntent);
                break;
            case R.id.forget_pass_btn:
                Intent forgetIntent = new Intent();
                forgetIntent.setClass(this, ForgetPassActivity.class);
                forgetIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(forgetIntent);
                break;
            case R.id.qq_login_btn:
                QQAuthorize();
                break;
            case R.id.remember_pass_btn:
            case R.id.remember_pass_img:
                if (isRememberPass) {
                    rememPassImg.setImageResource(R.drawable.checkbox_no);
                    isRememberPass = false;
                } else {
                    isRememberPass = true;
                    rememPassImg.setImageResource(R.drawable.checkbox_yes);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 登录
     */
    private void loginSubmit() {
        if (!TxNetworkUtil.isNetConnected(this)) {
            Toast.makeText(this, R.string.setting_error_net_str, Toast.LENGTH_SHORT).show();
        }
        username = accountEdit.getText().toString().trim();

        if (!StringUtil.isNotNull(username)) {
            Toast.makeText(this, R.string.register_phone_empty, Toast.LENGTH_SHORT).show();
            return;
        } else if (!StringUtil.isRightPhone(username)) {
            Toast.makeText(this, R.string.register_phone_right, Toast.LENGTH_SHORT).show();
            return;
        }
        password = passEdit.getText().toString().trim();
        if (!StringUtil.isNotNull(password)) {
            Toast.makeText(this, R.string.login_password_empty, Toast.LENGTH_SHORT).show();
            return;
        }


        if (isRememberPass) {
            SharedUtils.setRememberPass(mContext, true);
            SharedUtils.setUername(mContext, username);
            SharedUtils.setPassword(mContext, password);
        } else {
            SharedUtils.setRememberPass(mContext, false);
        }

        showDialong();
        if (!StringUtil.isNotNull(MainActivity.sessionid)) {
            isHttpTwice = true;//没有sessionid 可以请求两次
        } else {
            isHttpTwice = false;//
        }
        String keyfield = TxNetworkUtil.getUserKey(this);
        if (StringUtil.isNotNull(keyfield)) {
            reqLogin(keyfield);
        } else {
            loginMode = 1;
            reqAppkey();
        }
        //MobclickAgent.onEvent(this, UmengConstant.PERSONLOGIN);// 友盟自定义事件统计
    }

    private void reqLogin(String keyfield) {
        LoginHandler loginHandler = new LoginHandler(this);
        loginHandler.setOnDataRetrieveListener(this);
        loginHandler.startNetWork(keyfield, username, password);
    }

    /**
     * QQ授权登录
     */
    private void QQAuthorize() {
        if (!TxNetworkUtil.isNetConnected(this)) {
            Toast.makeText(this, R.string.setting_error_net_str, Toast.LENGTH_SHORT).show();
        }
        final Context context = LoginActivity.this;
        final Context ctxContext = context.getApplicationContext();
        final String mAppid = ConstantPool.QZONE_ID;
        try {
            mQQAuth = QQAuth.createInstance(mAppid, ctxContext);
            if (!mQQAuth.isSessionValid()) {
                mQQAuth.login(LoginActivity.this, "all", new BaseUiListener());
            } else {
                mQQAuth.logout(LoginActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            LogUtils.putLog(LoginActivity.this, response.toString() + "QQ授权成功");
            showDialong();
            isHttpTwice = false;
            authorize = LoginAuthHandler.QQAuthorizeParser(response);
            authorizeLogin();
        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(LoginActivity.this,
                    getString(R.string.authorize_error) + "：" + e.errorDetail,
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(LoginActivity.this, R.string.authorize_cancel, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 授权登录
     */
    public void authorizeLogin() {
        String keyfield = TxNetworkUtil.getUserKey(LoginActivity.this);
        if (StringUtil.isNotNull(keyfield)) {
            LoginAuthHandler loginHandler = new LoginAuthHandler(LoginActivity.this);
            loginHandler.setOnDataRetrieveListener(LoginActivity.this);
            loginHandler.startNetWork(authorize, keyfield);
        } else {
            loginMode = 2;
            reqAppkey();
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

    @Override
    public void onDataRetrieve(DataHandler dataHandler, int resultCode, Object data) {
        switch (resultCode) {
            case ConstantPool.GET_SECRET_KEY_OK:
                String dataString = String.valueOf(data);
                LogUtils.putLog(LoginActivity.this, "请求密钥返回==" + dataString);
                SecretKeyParserModel.parseMainJson(LoginActivity.this, mHandler, dataString, CHECK_KEY_SUC, CHECK_KEY_FAIL);
                break;
            case ConstantPool.GET_SECRET_KEY_ERROR:
                disDialog();
                LogUtils.putLog(LoginActivity.this, "请求密钥返回==" + data);
                mHandler.sendEmptyMessage(CHECK_KEY_FAIL);
                break;
            case ConstantPool.LOGIN_HTTP_SUC:
            case ConstantPool.AUTHORIZE_LOGIN_SUC:
                LogUtils.putLog(this, "登录返回内容==" + data);
                UserBean userBean = LoginHandler.loginParse(String.valueOf(data));
                int errorCode = userBean.getErrorCode();
                if (1002 == errorCode || 1018 == errorCode) {
                    if (isHttpTwice
                            && StringUtil.isNotNull(MainActivity.sessionid)
                            && resultCode == ConstantPool.LOGIN_HTTP_SUC) {
                        String keyfield = TxNetworkUtil.getUserKey(this);
                        if (StringUtil.isNotNull(keyfield)) {
                            reqLogin(keyfield);
                        } else {
                            reqAppkey();
                        }
                    } else if (1018 == errorCode) {
                        Toast.makeText(this, R.string.login_password_error, Toast.LENGTH_SHORT).show();
                        SharedUtils.setPassword(mContext, "");//清空密码
                        disDialog();
                    } else {
                        Toast.makeText(this, R.string.login_fail, Toast.LENGTH_SHORT).show();
                        disDialog();
                    }
                } else {
                    if (1 == errorCode) {//登录成功
                        if (null != callback) {
                            callback.getUserMsg(userBean);
                        }
                        hideKlavier();
                        this.setResult(MainActivity.LOGIN_SUC, null);
                        this.finish();
                    } else if (1017 == errorCode) {
                        Toast.makeText(this, R.string.login_account_no_exist, Toast.LENGTH_SHORT).show();
                        SharedUtils.setUername(mContext, "");
                        SharedUtils.setPassword(mContext, "");//清空密码
                    } else if (1011 == errorCode) {
                        Toast.makeText(this, R.string.person_center_error, Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMsg = userBean.getErrorMsg();
                        if (StringUtil.isNotNull(errorMsg)) {
                            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, R.string.login_fail, Toast.LENGTH_SHORT).show();
                        }
                    }
                    disDialog();
                }
                isHttpTwice = false;
                break;
            case ConstantPool.LOGIN_HTTP_FAIL:
            case ConstantPool.AUTHORIZE_LOGIN_FAIL:
                LogUtils.putLog(this, "登录返回内容==" + data);
                Toast.makeText(this, R.string.login_fail, Toast.LENGTH_SHORT).show();
                SharedUtils.setUername(mContext, "");
                SharedUtils.setPassword(mContext, "");//清空密码
                disDialog();
                break;
            default:
                break;
        }
    }

    /**
     * 获取手机密钥
     */
    private void reqAppkey() {
        SecretkeyHandler secretkeyHandler = new SecretkeyHandler(this);
        secretkeyHandler.setOnDataRetrieveListener(this);
        secretkeyHandler.startNetWork();
    }

    private void showDialong() {
        progressDialog = ProgressDialog.show(this,
                getText(R.string.wifi_connect_hint), getText(R.string.login_httping), true, true);
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

}
