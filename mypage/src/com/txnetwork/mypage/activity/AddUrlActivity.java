package com.txnetwork.mypage.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.txnetwork.mypage.MyApplication;
import com.txnetwork.mypage.R;

/**
 * Created by hcz on 2014/12/26.
 * <p/>
 * 添加新的网址导航
 */
public class AddUrlActivity extends Activity implements View.OnClickListener {

    private Button saveBtn;
    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addurl);

        saveBtn = (Button) this.findViewById(R.id.save);
        saveBtn.setOnClickListener(this);
        cancelBtn = (Button) this.findViewById(R.id.cancel);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                break;
            case R.id.cancel:
                this.finish();
                break;
        }
    }
}
