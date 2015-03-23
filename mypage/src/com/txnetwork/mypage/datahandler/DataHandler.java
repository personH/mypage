package com.txnetwork.mypage.datahandler;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.txnetwork.mypage.activity.MainActivity;
import com.txnetwork.mypage.fragment.MainFragment;
import com.txnetwork.mypage.http.HttpAction;
import com.txnetwork.mypage.http.HttpManager;
import com.txnetwork.mypage.http.HttpObserver;
import com.txnetwork.mypage.utils.ConstantPool;
import com.txnetwork.mypage.utils.LogUtils;
import com.txnetwork.mypage.utils.StringUtil;
import org.apache.http.Header;

/**
 * DataHandler.java
 *
 * @author zhangrui 2013-4-26
 */
public abstract class DataHandler implements Runnable {
    private static final String TAG = "DataHandler";
    public static final int OK = 0;
    public static final int ERROR = 1;
    private OnDataRetrieveListener onDataRetrieveListener;
    private DataUIHandler dataUIHandler;
    private DataHttpObserver dataHttpObserver;

    public DataHandler() {
        dataUIHandler = new DataUIHandler();
        dataHttpObserver = new DataHttpObserver();
    }

    /**
     * 开始数据处理
     */
    public final void start() {
        Processor.getInstance().put(this);
    }

    /**
     * 添加请求的cookie
     *
     * @param httpAction
     */
    protected final void addRequestCookie(HttpAction httpAction) {
        String sessionid = MainActivity.sessionid;
        if (StringUtil.isNotNull(sessionid)) {
            httpAction.addRequestHeader(ConstantPool.HTTP_COOKIE, "JSESSIONID=" + sessionid + ConstantPool.COOKIE_PATH);
            LogUtils.LOGE("DataHandler", "添加请求的cookie==>" + sessionid);
        }
    }

    /**
     * 开始联网
     *
     * @param httpAction Http事件
     */
    protected final void startNetwork(HttpAction httpAction) {
        httpAction.setHttpObserver(dataHttpObserver);
        HttpManager.getInstance().add(httpAction);
    }

    /**
     * 联网成功回调
     *
     * @param receiveBody 联网获取的数据
     */
    protected void onNetReceiveOk(byte[] receiveBody) {

    }

    /**
     * 联网成功回调
     *
     * @param receiveHeaders 联网获取的数据
     */
    protected void onReceiveHeaders(Header[] receiveHeaders) {

    }

    /**
     * 联网失败回调
     */
    protected void onNetReceiveError(int errorCode) {

    }

    private class DataHttpObserver implements HttpObserver {
        @Override
        public void httpResultOK(HttpAction httpAction) {
            if (!StringUtil.isNotNull(MainActivity.sessionid)) {
                LogUtils.LOGE(TAG, "httpResultOK == FirstFragment.sessionid==" + MainActivity.sessionid);
                Header[] receiveHeaders = httpAction.getReceiveHeaders();
                onReceiveHeaders(receiveHeaders);
            }
            byte[] data = (byte[]) httpAction.getReceiveBody();
            onNetReceiveOk(data);
        }

        @Override
        public void httpResultError(HttpAction httpAction) {
            int errorcode = httpAction.getErrorCode();
            onNetReceiveError(errorcode);
        }
    }

    /**
     * 分解head取出cookie
     *
     * @param receiveHeaders
     */
    protected final void analyticHeaders(Header[] receiveHeaders) {
        LogUtils.LOGE(TAG, "analyticHeaders==Header[]==" + receiveHeaders);
        for (int i = 0; i < receiveHeaders.length; i++) {
            Header header = receiveHeaders[i];
            if (header.getName().equals("Set-Cookie")) {
                String cookieval = header.getValue();
                if (cookieval != null) {
                    MainActivity.sessionid = cookieval.substring(0, cookieval.indexOf(";"));
                    // FirstFragment.sessionid = cookieval;
                }
                LogUtils.LOGE("DataHandler", "=Set-Cookie==>" + header.getValue() + "<==head取出cookie==>" + MainActivity.sessionid);
            }
        }
    }

    /**
     * 发送数据处理结果
     *
     * @param resultCode 处理结果
     * @param object     结果数据
     */
    protected void sendResult(int resultCode, Object object) {
        Message msg = new Message();
        msg.what = resultCode;
        msg.obj = object;
        dataUIHandler.sendMessage(msg);
    }

    private class DataUIHandler extends Handler {

        private DataUIHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            if (onDataRetrieveListener != null) {
                onDataRetrieveListener.onDataRetrieve(DataHandler.this, msg.what, msg.obj);
            }
        }
    }

    /**
     * 获取数据接收监听器
     *
     * @return
     */
    public OnDataRetrieveListener getOnDataRetrieveListener() {
        return onDataRetrieveListener;
    }

    /**
     * 设置数据接收监听器
     *
     * @param onDataRetrieveListener 数据接收监听器
     */
    public void setOnDataRetrieveListener(
            OnDataRetrieveListener onDataRetrieveListener) {
        if (onDataRetrieveListener != null) {
            this.onDataRetrieveListener = onDataRetrieveListener;
        }
    }

    /**
     * 该方法用于被子类重写，以达到实现处理数据过程的目的
     */
    @Override
    public void run() {

    }
}
