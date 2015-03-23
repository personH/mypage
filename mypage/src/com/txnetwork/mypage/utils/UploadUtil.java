package com.txnetwork.mypage.utils;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import com.txnetwork.mypage.activity.MainActivity;
import com.txnetwork.mypage.entity.DiySkin;
import com.txnetwork.mypage.entity.SkinInfo;
import com.txnetwork.mypage.fragment.MainFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 上传工具类
 */
public class UploadUtil {

    private Context mContext;
    private String mPicPath;
    private static UploadUtil uploadUtil;
    private static final String BOUNDARY = "*****"; // 边界标识 随机生成
    private static final String PREFIX = "--";
    private static final String LINE_END = "\r\n";
    private static final String CONTENT_TYPE = "multipart/form-data"; // 内容类型
    private String mUserId;//用户id


    private UploadUtil() {

    }

    /**
     * 单例模式获取上传工具类
     *
     * @return
     */
    public static UploadUtil getInstance() {
        if (null == uploadUtil) {
            uploadUtil = new UploadUtil();
        }
        return uploadUtil;
    }

    private static final String TAG = "UploadUtil";
    private int readTimeOut = 10 * 1000; // 读取超时
    private int connectTimeout = 10 * 1000; // 超时时间
    /**
     * 请求使用多长时间
     */
    private static int requestTime = 0;

    private static final String CHARSET = "UTF-8"; // 设置编码
    private static final String FILE_KEY = "img"; // 设置编码

    /**
     * 上传成功
     */
    public static final int UPLOAD_SUCCESS_CODE = 1;
    /**
     * 文件不存在
     */
    public static final int UPLOAD_FILE_NOT_EXISTS_CODE = 2;
    /**
     * 服务器出错
     */
    public static final int UPLOAD_SERVER_ERROR_CODE = 3;
    //未登录
    public static final int NO_LOGIN = 4;

    /**
     * android上传文件到服务器
     *
     * @param filePath   需要上传的文件的路径
     * @param RequestURL 请求的URL
     */
    public void uploadFile(Context context, String filePath, String RequestURL) {

        mContext = context;
        mPicPath = filePath;
        mUserId = TxNetworkUtil.getUserId(mContext);//获取用户id

        if (mUserId == null || "".equals(mUserId)) {
            sendMessage(NO_LOGIN, "未登录");
            return;
        }

        if (filePath == null) {
            sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE, "文件不存在");
            return;
        }

        try {
            File file = new File(filePath);
            uploadFile(file, FILE_KEY, RequestURL);
        } catch (Exception e) {
            sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE, "文件不存在");
            e.printStackTrace();
            return;
        }
    }

    /**
     * android上传文件到服务器
     *
     * @param file       需要上传的文件
     * @param fileKey    在网页上<input type=file name=xxx/> xxx就是这里的fileKey
     * @param RequestURL 请求的URL
     */
    public void uploadFile(final File file, final String fileKey, final String RequestURL) {

        if (file == null || (!file.exists())) {
            sendMessage(UPLOAD_FILE_NOT_EXISTS_CODE, "文件不存在");
            return;
        }

        Log.i(TAG, "请求的URL=" + RequestURL);
        Log.i(TAG, "请求的fileName=" + file.getName());
        Log.i(TAG, "请求的fileKey=" + fileKey);

        new Thread(new Runnable() {  //开启线程上传文件
            @Override
            public void run() {
                Looper.prepare();
                try {
                    toUploadFile(file, fileKey, RequestURL);
                } catch (Exception e) {
                    SkinUtils.updateDiyTheme(mContext, mPicPath, "", mUserId, DiySkin.FAIL);
                    sendMessage(UPLOAD_SERVER_ERROR_CODE, "图片上传失败");
                    e.printStackTrace();
                    return;
                }
            }
        }).start();

    }

    private void toUploadFile(File file, String fileKey, String RequestURL) throws Exception {

        String result = null;
        requestTime = 0;
        long requestTime = System.currentTimeMillis();
        long responseTime = 0;


        URL url = new URL(RequestURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setReadTimeout(readTimeOut);
        conn.setConnectTimeout(connectTimeout);
        conn.setDoInput(true); // 允许输入流
        conn.setDoOutput(true); // 允许输出流
        conn.setUseCaches(false); // 不允许使用缓存
        conn.setRequestMethod("POST"); // 请求方式
        conn.setRequestProperty("Charset", CHARSET); // 设置编码
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
        conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

        String sessionid = MainActivity.sessionid;
        if (StringUtil.isNotNull(sessionid)) {
            conn.setRequestProperty(ConstantPool.HTTP_COOKIE, "JSESSIONID=" + sessionid + ConstantPool.COOKIE_PATH);
        }
        /**
         * 当文件不为空，把文件包装并且上传
         */
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        dos.writeBytes(PREFIX + BOUNDARY + LINE_END);
        dos.writeBytes("Content-Disposition: form-data; " + "name=\"file1\";filename=\"" + file.getName() + "\"" + LINE_END);
        dos.writeBytes(LINE_END);
        /**上传文件*/
        InputStream is = new FileInputStream(file);
        onUploadProcessListener.initUpload((int) file.length());
        byte[] bytes = new byte[1024];
        int len = 0;
        int curLen = 0;
        while ((len = is.read(bytes)) != -1) {
            curLen += len;
            dos.write(bytes, 0, len);
            onUploadProcessListener.onUploadProcess(curLen);
        }
        is.close();
        dos.write(LINE_END.getBytes());
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
        dos.write(end_data);
        dos.flush();
        /**
         * 获取响应码 200=成功 当响应成功，获取响应的流
         */
        int res = conn.getResponseCode();
        responseTime = System.currentTimeMillis();
        this.requestTime = (int) ((responseTime - requestTime) / 1000);
        Log.e(TAG, "response code:" + res);

        if (res == 200) {
            Log.e(TAG, "request success");
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
            } catch (Exception e) {
                String userid = TxNetworkUtil.getUserId(mContext);
                SkinUtils.updateDiyTheme(mContext, mPicPath, "", userid, DiySkin.FAIL);
                sendMessage(UPLOAD_SERVER_ERROR_CODE, "图片上传失败");
                e.printStackTrace();
            } finally {
                if (null != reader) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            result = buffer.toString();
            Log.e(TAG, "result : " + result);
            parseJson(result);
            return;
        } else {
            Log.e(TAG, "request error");
            SkinUtils.updateDiyTheme(mContext, mPicPath, "", mUserId, DiySkin.FAIL);
            sendMessage(UPLOAD_SERVER_ERROR_CODE, "图片上传失败");
            return;
        }

    }

    /**
     * 发送上传结果
     *
     * @param responseCode
     * @param responseMessage
     */
    private void sendMessage(int responseCode, String responseMessage) {
        onUploadProcessListener.onUploadDone(responseCode, responseMessage);
    }

    /**
     * 下面是一个自定义的回调函数，用到回调上传文件是否完成
     *
     * @author shimingzheng
     */
    public static interface OnUploadProcessListener {
        /**
         * 上传响应
         *
         * @param responseCode
         * @param message
         */
        void onUploadDone(int responseCode, String message);

        /**
         * 上传中
         *
         * @param uploadSize
         */
        void onUploadProcess(int uploadSize);

        /**
         * 准备上传
         *
         * @param fileSize
         */
        void initUpload(int fileSize);
    }

    private OnUploadProcessListener onUploadProcessListener;


    public void setOnUploadProcessListener(
            OnUploadProcessListener onUploadProcessListener) {
        this.onUploadProcessListener = onUploadProcessListener;
    }

    public int getReadTimeOut() {
        return readTimeOut;
    }

    public void setReadTimeOut(int readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * 获取上传使用的时间
     *
     * @return
     */
    public static int getRequestTime() {
        return requestTime;
    }

    public static interface uploadProcessListener {

    }

    private void parseJson(String strResult) {
        try {
            JSONObject jsonObject = new JSONObject(strResult);
            int errorCode = jsonObject.getInt("errorCode");
            if (1 == errorCode) {
                JSONArray resultList = jsonObject.getJSONArray("resultList");
                if (resultList != null && resultList.length() > 0) {
                    SkinInfo skinInfo = new SkinInfo(resultList.getJSONObject(0).getString("picid"),
                            resultList.getJSONObject(0).getString("picname"),
                            resultList.getJSONObject(0).getString("newname"),
                            resultList.getJSONObject(0).getInt("pictype"),
                            resultList.getJSONObject(0).getString("suffix"),
                            resultList.getJSONObject(0).getInt("picsize"),
                            resultList.getJSONObject(0).getString("picpath"),
                            resultList.getJSONObject(0).getString("picsrc"),
                            String.valueOf(System.currentTimeMillis()));

                    //修改本地数据库
                    String picid = skinInfo.getPicid();
                    SkinUtils.updateDiyTheme(mContext, mPicPath, picid, mUserId, DiySkin.TRUE);
                    sendMessage(UPLOAD_SUCCESS_CODE, "图片上传成功");
                } else {
                    SkinUtils.updateDiyTheme(mContext, mPicPath, "", mUserId, DiySkin.FAIL);
                    sendMessage(UPLOAD_SERVER_ERROR_CODE, "图片上传失败");
                }
            } else {
                SkinUtils.updateDiyTheme(mContext, mPicPath, "", mUserId, DiySkin.FAIL);
                sendMessage(UPLOAD_SERVER_ERROR_CODE, "图片上传失败");
            }
        } catch (JSONException e) {
            SkinUtils.updateDiyTheme(mContext, mPicPath, "", mUserId, DiySkin.FAIL);
            LogUtils.LOGE(TAG, "parseMainJson==JSONException=" + e.getMessage());
            sendMessage(UPLOAD_SERVER_ERROR_CODE, "图片上传失败");
        }
    }


}
