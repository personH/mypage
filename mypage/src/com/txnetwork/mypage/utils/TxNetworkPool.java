package com.txnetwork.mypage.utils;

public class TxNetworkPool {

    static {
        System.loadLibrary("txnetwork");
    }

    /**
     * 解密用户密钥
     *
     * @param keyfield 用户私钥
     * @return
     */
    public native static boolean CBCDecryptTx(String keyfield);

    /**
     * 解密用户密钥
     *
     * @param keyfield 用户私钥
     * @return key
     */
    public native static String CBCDecryptStr(String keyfield);

    /**
     * 获取本地密钥
     *
     * @param
     * @return 本地密钥
     */
    public native static String GetLocalKey();

    /**
     * 设置本地密钥
     *
     * @param localkey 本地密钥
     * @return
     */
    public native static boolean SetLocalKey(String localkey);

    /**
     * 加密数据
     *
     * @param keyfield 用户私钥
     * @return
     */
    public native static String ECBEncrypt(String keyfield);

    /**
     * 解密数据
     *
     * @param keyfield 用户私钥
     * @return
     */
    public native static String ECBDecrypt(String keyfield);

    /**
     * 解密数据pc端
     *
     * @param keyfield 用户私钥
     * @return
     */
    public native static String ECBDecryptPc(String keyfield);

}
