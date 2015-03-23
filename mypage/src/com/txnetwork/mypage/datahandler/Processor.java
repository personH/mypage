/**
 * Processor.java 2012-6-26 Copyright 2010-2011 CloudReal Network Technology
 * Co., Ltd. All Rights Reserved.
 */
package com.txnetwork.mypage.datahandler;

import android.os.Process;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 数据处理器，用来管理各个DataHandler
 * Processor.java
 * 
 * @author zhangrui 2013-4-26
 */
public class Processor extends Thread {
    private static final String TAG = "Processor";

    private static Processor instance;
    private BlockingQueue<DataHandler> mDataHandlers;

    protected Processor() {
        mDataHandlers = new LinkedBlockingQueue<DataHandler>();
    }

    public synchronized static Processor getInstance() {
        if (instance == null) {
            instance = new Processor();
            instance.start();
        }
        return instance;
    }

    /**
     * 新增一个数据处理请求
     * 
     * @param dataHandler
     */
    public void put(DataHandler dataHandler) {
        if (dataHandler != null && !mDataHandlers.contains(dataHandler)) {
            mDataHandlers.add(dataHandler);
        }
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        while (true) {
            try {
                DataHandler dataHandler = mDataHandlers.take();
                dataHandler.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}