package com.txnetwork.mypage.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;
import com.txnetwork.mypage.fragment.MainFragment;
import com.txnetwork.mypage.listener.UserMsgCallback;
import com.txnetwork.mypage.utils.LogUtils;

public class UserActivity extends Activity {
    protected UserMsgCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainFragment mainFragment = MainFragment.getInstance();
        if (null != mainFragment) {
            callback = mainFragment;
        }
    }

    /**
     * 判断键盘是否显示 true显示
     *
     * @return
     */
    protected boolean isShowKlavier() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        boolean isShow = (params.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
        LogUtils.LOGE("UserActivity", "isShowKlavier===>" + isShow);
        return isShow;
    }

    /**
     * 隐藏键盘
     */
    protected void hideKlavier() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().getAttributes().softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN;
    }

}
