package com.txnetwork.mypage.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.txnetwork.mypage.MyApplication;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.cropimage.ChildAdapter;
import com.txnetwork.mypage.cropimage.Crop;
import com.txnetwork.mypage.utils.*;

import java.io.*;
import java.util.*;

public class ShowImageActivity extends Activity {
    private GridView mGridView;
    private List<String> list;
    private ChildAdapter adapter;
    private TextView finishBtn;
    private Context mContext;
    public static String TRUE = "true";
    public static String FALSE = "false";
    private String mPicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyApplication.getInstance().addActivity(this);
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.show_image_activity);

        mGridView = (GridView) findViewById(R.id.child_grid);
        list = getIntent().getStringArrayListExtra("data");

        adapter = new ChildAdapter(this, list, mGridView);
        mGridView.setAdapter(adapter);

        finishBtn = (TextView) findViewById(R.id.finish);
        //判断当前选中的图片是否已经选择过?
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String picPath = list.get(adapter.getSelectItem());
                File picFile = new File(picPath);
                if (picFile.exists()) {
                    if (SkinUtils.UPLOADED_SUC != SkinUtils.diyThemeCheck(mContext, picPath)) {
                        beginCrop(picPath);//裁剪图片
                    } else {
                        Toast.makeText(mContext, R.string.already_upload_pic, Toast.LENGTH_LONG).show();
                        return;
                    }
                } else {
                    Toast.makeText(mContext, R.string.no_pic, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void beginCrop(String source) {
        //
        Uri imageUri = Uri.fromFile(new File(source));
        Uri outputUri = Uri.fromFile(new File(TxNetwork.APP_DOWNLOADFILE_PATH, "cropped.jpg"));
        new Crop(imageUri, source, "").output(outputUri).withAspect(TxNetworkUtil.getDisplayWidth(mContext), TxNetworkUtil.getDisplayHeight(mContext)).start(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == MainActivity.ADD_LOCAL_THEME) {
            this.setResult(MainActivity.ADD_LOCAL_THEME, data);
            this.finish();
        }
    }

}
