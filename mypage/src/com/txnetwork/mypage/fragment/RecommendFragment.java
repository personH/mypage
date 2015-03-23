package com.txnetwork.mypage.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.txnetwork.mypage.R;

/**
 * Created by Administrator on 2014/12/19.
 */

public class RecommendFragment extends Fragment {

    private Context mContext;
    private static final String TAG = RecommendFragment.class.getSimpleName();
    protected static final int CHECK_KEY_SUC = 0x00410;
    protected static final int CHECK_KEY_FAIL = 0x00411;

    private WebView webview;

    private static RecommendFragment mInstance = null;


    public static RecommendFragment getInstance() {
        return mInstance;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        mInstance = this;
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        webview = (WebView) view.findViewById(R.id.webview);
        //设置WebView属性，能够执行Javascript脚本
        webview.getSettings().setJavaScriptEnabled(true);
        //加载需要显示的网页
        webview.loadUrl("http://wap.hao123.com/");
        //设置Web视图
        webview.setWebViewClient(new HelloWebViewClient());
        return view;
    }

    //Web视图
    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
