package com.txnetwork.mypage.customview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.datahandler.DataHandler;
import com.txnetwork.mypage.datahandler.OnDataRetrieveListener;
import com.txnetwork.mypage.jsonparser.SecretKeyParserModel;
import com.txnetwork.mypage.datahandler.SecretkeyHandler;
import com.txnetwork.mypage.utils.ConstantPool;
import com.txnetwork.mypage.utils.LogUtils;
import com.txnetwork.mypage.utils.StringUtil;
import com.txnetwork.mypage.utils.TxNetworkUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseLayout extends FrameLayout implements OnDataRetrieveListener {

    protected static final int CHECK_KEY_SUC = 0x00350;
    protected static final int CHECK_KEY_FAIL = 0x00360;
    protected Context mContext;
    protected LayoutInflater mInflater;
    /**
     * 1注册，2找回
     */
    protected int codeMode;

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            handlerCall(msg);
            super.handleMessage(msg);
        }
    };

    public BaseLayout(Context context) {
        super(context);
        init(context);
    }

    public BaseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    protected void handlerCall(Message msg) {

    }

    protected void reqAppkey() {
        SecretkeyHandler secretkeyHandler = new SecretkeyHandler(mContext);
        secretkeyHandler.setOnDataRetrieveListener(this);
        secretkeyHandler.startNetWork();
    }

    @Override
    public void onDataRetrieve(DataHandler dataHandler, int resultCode,
                               Object data) {
        switch (resultCode) {
            case ConstantPool.GET_SECRET_KEY_OK:
                String dataString = String.valueOf(data);
                LogUtils.putLog(mContext, "请求密钥返回==" + dataString);
                SecretKeyParserModel.parseMainJson(mContext,
                        mHandler, dataString, CHECK_KEY_SUC,
                        CHECK_KEY_FAIL);
                break;
            case ConstantPool.GET_SECRET_KEY_ERROR:
                LogUtils.putLog(mContext, "请求密钥返回==" + data);
                mHandler.sendEmptyMessage(CHECK_KEY_FAIL);
                break;
        }
    }

    /**
     * 网络是否可用
     *
     * @return
     */
    protected boolean isNetAvailable() {
        if (!TxNetworkUtil.isNetConnected(mContext)) {
            Toast.makeText(mContext, R.string.setting_error_net_str,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 验证手机号码
     *
     * @param phoneStr
     * @return
     */
    protected boolean isRightPhone(String phoneStr) {
        if (!StringUtil.isNotNull(phoneStr)) {
            // 手机号码为空
            Toast.makeText(mContext, R.string.register_phone_empty,Toast.LENGTH_SHORT).show();
            return false;
        }
        // String regExp = "^[1]([3][0-9]{1}|59|58|88|89)[0-9]{8}$";//^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$
        Pattern p = Pattern.compile("^(13|15|14|17|18)\\d{9}$");
        Matcher m = p.matcher(phoneStr);
        if (!m.matches()) {
            // 请输入正确手机号码
            Toast.makeText(mContext, R.string.register_phone_right,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 验证邮箱
     *
     * @return
     */
    protected boolean isRightEmail(String emailStr) {
        if (!StringUtil.isNotNull(emailStr)) {
            // 邮箱为空
            Toast.makeText(mContext, R.string.register_email_empty,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        // String regex =
        // "^\\w+[-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$ ";

//		String str = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        String str = "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(emailStr);
        if (!m.matches()) {
            // 请输入正确邮箱
            Toast.makeText(mContext, R.string.register_email_right,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 验证昵称
     *
     * @param nicknameStr
     * @return
     */
    protected boolean isRightNickname(String nicknameStr) {
        if (!StringUtil.isNotNull(nicknameStr)) {
            // 昵称为空
            Toast.makeText(mContext, R.string.register_nickname_empty,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        //特殊字符
        String special = "^.*[\\'|\\\"|\\/|<|>|~|!|&|@|#|$|%|^|*|(|)|+|，|。|；|,|：|:|-|‘|“|?¤].*$";
        //2-15个汉字或4-30个字符
        String regName = "([\u4e00-\u9fa5]{1,15})|([A-Za-z0-9 ]{1,30})";
        Pattern p = Pattern.compile(special);
        Matcher m = p.matcher(nicknameStr);

        Pattern p1 = Pattern.compile(regName);
        Matcher m1 = p1.matcher(nicknameStr);

        if (!m.matches() && !m1.matches()) {
            // 请输入正确昵称
            Toast.makeText(mContext, R.string.register_nickname_right,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 验证密码
     *
     * @param passStr
     * @param againStr
     * @return
     */
    protected boolean isRightPass(String passStr, String againStr) {
        if (!StringUtil.isNotNull(passStr)) {
            // 密码为空
            Toast.makeText(mContext, R.string.register_pass_empty,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!StringUtil.isNotNull(againStr)) {
            // 密码为空
            Toast.makeText(mContext, R.string.register_again_pass_empty,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!passStr.equals(againStr)) {
            // 密码不一样
            Toast.makeText(mContext, R.string.register_pass_equals,
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        String str = "^[\\x21-\\x7E]{6,20}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(passStr);
        if (!m.matches()) {
            // 请输入密码 6—20个字符
            Toast.makeText(mContext, R.string.register_pass_right,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 验证验证码
     *
     * @param codeStr
     * @return
     */
    protected boolean isRightCode(String codeStr) {
        if (!StringUtil.isNotNull(codeStr)) {
            // 验证码为空
            Toast.makeText(mContext, R.string.register_code_empty,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
