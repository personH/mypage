package com.txnetwork.mypage.customview;

import android.content.Context;
import android.content.res.Resources;
import android.os.CountDownTimer;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.activity.ForgetPassActivity;
import com.txnetwork.mypage.datahandler.DataHandler;
import com.txnetwork.mypage.datahandler.PhoneCodeHandler;
import com.txnetwork.mypage.entity.PhoneCodeBean;
import com.txnetwork.mypage.utils.ConstantPool;
import com.txnetwork.mypage.utils.LogUtils;
import com.txnetwork.mypage.utils.StringUtil;
import com.txnetwork.mypage.utils.TxNetworkUtil;

public class ForgetPassPhone extends BaseLayout implements OnClickListener {

    private static final long millisInFuture = 60 * 1000;// 总时间
    private static final long countDownInterval = 1000;// 间隔时间
    private Resources resources;
    private EditText accountEdit, passEdit, againEdit, codeEdit;
    private Button codeBtn, forgetBtn;
    private String phoneStr;
    private String passStr;
    private String userid; // 用户id
    private String oldPwd; // 返回旧密码

    public ForgetPassPhone(Context context) {
        super(context);
        init(context);
    }

    public ForgetPassPhone(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ForgetPassPhone(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        View view = mInflater.inflate(R.layout.forget_pass_phone, null);
        initView(view);
        this.addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        resources = mContext.getResources();
    }

    private void initView(View view) {
        accountEdit = (EditText) view.findViewById(R.id.forget_phone_edit);
        passEdit = (EditText) view.findViewById(R.id.forget_phone_pass);
        againEdit = (EditText) view.findViewById(R.id.forget_phone_again_pass);
        codeEdit = (EditText) view.findViewById(R.id.forget_phone_code);
        codeBtn = (Button) view.findViewById(R.id.forget_code_btn);
        codeBtn.setOnClickListener(this);
        forgetBtn = (Button) view.findViewById(R.id.forget_phone_btn);
        forgetBtn.setOnClickListener(this);
    }

    private void downTimerText() {
        downTimer.start();
        codeBtn.setEnabled(false);
        codeBtn.setTextColor(resources.getColor(R.color.front_text_white));
        codeBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.text_size_60));
    }

    private void getCodeText() {
        codeBtn.setEnabled(true);
        codeBtn.setText(R.string.register_again_code);
        codeBtn.setTextColor(resources.getColor(R.color.white));
        codeBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                resources.getDimension(R.dimen.text_size_32));
    }

    private CountDownTimer downTimer = new CountDownTimer(millisInFuture,
            countDownInterval) {

        @Override
        public void onTick(long millisUntilFinished) {
            codeBtn.setText((millisUntilFinished / 1000) + "");
        }

        @Override
        public void onFinish() {
            getCodeText();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.forget_code_btn:
                forgetCode();
                break;
            case R.id.forget_phone_btn:
                forgetSubmit();
                break;

            default:
                break;
        }
    }

    /**
     * 下发验证码
     */
    private void forgetCode() {
        if (!isNetAvailable()) {
            return;
        }
        phoneStr = accountEdit.getText().toString().trim();
        if (!isRightPhone(phoneStr)) {
            return;
        }
        reqForgetCode();
        downTimerText();
        //MobclickAgent.onEvent(mContext, UmengConstant.FORGETCODE);// 友盟自定义事件统计
    }


    @Override
    protected void handlerCall(Message msg) {
        switch (msg.what) {
            case CHECK_KEY_SUC:
                if (codeMode == 2) {
                    reqForgetCode();
                }
                break;
            case CHECK_KEY_FAIL:
                Toast.makeText(mContext, R.string.register_code_fail,
                        Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        super.handlerCall(msg);
    }

    protected void reqForgetCode() {
        String keyfield = TxNetworkUtil.getUserKey(mContext);
        if (StringUtil.isNotNull(keyfield)) {
            PhoneCodeHandler codeHandler = new PhoneCodeHandler(mContext);
            codeHandler.setOnDataRetrieveListener(this);
            codeHandler.startNetWork(phoneStr, keyfield, 2);
        } else {
            codeMode = 2;
            reqAppkey();
        }
    }

    @Override
    public void onDataRetrieve(DataHandler dataHandler, int resultCode, Object data) {
        switch (resultCode) {
            case ConstantPool.PHONE_CODE_SUC:
                LogUtils.putLog(mContext, "获得验证码返回==data==" + data);
                PhoneCodeBean codeBean = PhoneCodeHandler.codeParse(
                        String.valueOf(data), 2);
                int errorCode = codeBean.getErrorCode();
                if (errorCode == 1) {
                    Toast.makeText(mContext, R.string.register_code_suc,Toast.LENGTH_SHORT).show();
                    userid = codeBean.getUserId();
                    oldPwd = codeBean.getPassword();
                } else if (1017 == errorCode) {
                    downTimer.cancel();
                    getCodeText();
                    Toast.makeText(mContext, R.string.forget_phone_no_exist,Toast.LENGTH_SHORT).show();
                } else if (1011 == errorCode) {
                    Toast.makeText(mContext, R.string.person_center_error,Toast.LENGTH_SHORT).show();
                    downTimer.cancel();
                    getCodeText();
                } else {
                    downTimer.cancel();
                    getCodeText();
                    String errorMsg = codeBean.getErrorMsg();
                    if (StringUtil.isNotNull(errorMsg)) {
                        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, R.string.register_code_fail,
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case ConstantPool.PHONE_CODE_FAIL:
                LogUtils.putLog(mContext, "获得验证码返回==data==" + data);
                downTimer.cancel();
                getCodeText();
                Toast.makeText(mContext, R.string.register_code_fail,
                        Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
        super.onDataRetrieve(dataHandler, resultCode, data);
    }

    /**
     * 手机忘记密码提交
     */
    private void forgetSubmit() {
        if (!isNetAvailable()) {
            return;
        }
        phoneStr = accountEdit.getText().toString().trim();
        passStr = passEdit.getText().toString().trim();
        String againStr = againEdit.getText().toString().trim();
        String codeStr = codeEdit.getText().toString().trim();
        if (!isRightPhone(phoneStr)) {
            return;
        }
        if (!isRightPass(passStr, againStr)) {
            return;
        }
        if (!isRightCode(codeStr)) {
            return;
        }
        ((ForgetPassActivity) mContext).reqPhonePass(userid, passStr, oldPwd, codeStr);
        //MobclickAgent.onEvent(mContext, UmengConstant.FORGETPHONE);// 友盟自定义事件统计
    }
}
