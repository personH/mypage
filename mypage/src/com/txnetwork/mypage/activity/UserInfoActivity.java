package com.txnetwork.mypage.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.datahandler.LoginExitHandler;
import com.txnetwork.mypage.datahandler.OnDataRetrieveListener;
import com.txnetwork.mypage.fragment.MainFragment;
import com.txnetwork.mypage.utils.TxNetworkUtil;

/**
 * Created by Administrator on 2015/1/21.
 */
public class UserInfoActivity extends Activity {

    private TextView nickname;
    private TextView logout;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;
        setContentView(R.layout.user_main);
        nickname= (TextView) findViewById(R.id.nickname);
        logout= (TextView) findViewById(R.id.logout);
        nickname.setText(getIntent().getStringExtra("nickname"));
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainFragment mainFragment = MainFragment.getInstance();
                mainFragment.showDialong(mContext.getString(R.string.exit_login_loading));
                String userid = TxNetworkUtil.getUserId(mContext);
                String aesKey = TxNetworkUtil.getUserKey(mContext);
                LoginExitHandler exitHandler = new LoginExitHandler(mContext);
                OnDataRetrieveListener onDataRetrieveListener = (OnDataRetrieveListener) mainFragment;
                exitHandler.setOnDataRetrieveListener(onDataRetrieveListener);
                exitHandler.startNetWork(userid, aesKey);
            }
        });
    }
}
