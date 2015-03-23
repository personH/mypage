package com.txnetwork.mypage.http;

/**
 * 
 * HttpObserver.java
 * @author zhangrui
 * 2013-4-2
 */
public interface HttpObserver
{
    /**
     * 当请求被成功响应时该接口被回调
     * 
     * @param httpAction
     *            正在处理的Http事件
     */
    public abstract void httpResultOK(HttpAction httpAction);

    /**
     * 当请求响应失败时该接口被回调
     * 
     * @param httpAction
     *            正在处理的Http事件
     */
    public abstract void httpResultError(HttpAction httpAction);
}

