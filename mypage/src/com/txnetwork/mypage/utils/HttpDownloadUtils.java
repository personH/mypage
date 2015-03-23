package com.txnetwork.mypage.utils;

import android.os.Handler;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2015/1/13.
 */
public class HttpDownloadUtils {

    public void download(String urlString, Handler handler) {
        try {
            // 构造URL
            URL url = new URL(urlString);
            // 打开连接
            URLConnection con = url.openConnection();
            //获得文件的长度
            int contentLength = con.getContentLength();
            handler.sendMessage(handler.obtainMessage(2, contentLength, 0));
            System.out.println("长度 :" + contentLength);
            // 输入流
            InputStream is = con.getInputStream();

            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            // 输出的文件流
            File dirPath = new File(TxNetwork.APP_DOWNLOADFILE_PATH);
            if (!dirPath.exists()) {
                dirPath.mkdir();
            }
            String newFileName = urlString.substring(urlString.lastIndexOf("/") + 1);
            newFileName = dirPath + "/" + newFileName;
            File file = new File(newFileName);
            //如果目标文件已经存在，则删除。产生覆盖旧文件的效果
            if (file.exists()) {
                file.delete();
            }

            OutputStream os = new FileOutputStream(newFileName);
            // 开始读取
            int count = 0;

            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
                count = count + len;
                handler.sendMessage(handler.obtainMessage(3, count, 0));

            }
            // 完毕，关闭所有链接
            os.close();
            is.close();
        } catch (IOException e) {
            handler.sendEmptyMessage(0);
            e.printStackTrace();
            return;
        }
        handler.sendEmptyMessage(1);
    }
}


