package com.txnetwork.mypage.datahandler;


import com.txnetwork.mypage.datahandler.DataHandler;

/**
 * 
 * OnDataRetrieveListener.java
 * @author zhangrui
 * 2013-4-26
 */
    public interface OnDataRetrieveListener
    {
        /**
         * 数据接收回调方法
         * @param dataHandler 数据处理器
         * @param resultCode 接收结果
         * @param data 接收数据
         */
        public void onDataRetrieve(DataHandler dataHandler, int resultCode, Object data);
    }
