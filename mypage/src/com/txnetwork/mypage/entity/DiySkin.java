package com.txnetwork.mypage.entity;

/**
 * Created by Administrator on 2015/1/6.
 */
public class DiySkin {

    public static String TRUE = "true";//上传成功
    public static String FALSE = "false";//没有上传
    public static String FAIL = "fail";//上传失败


    private String picDirectory;//图片的路径
    private String uploaded;//是否是本地主题
    private String name;//主题的名称
    private int size;//图片资源的大小

    public DiySkin(String picDirectory, String name, int size, String flag) {
        this.picDirectory = picDirectory;
        this.name = name;
        this.size = size;
        uploaded = flag;
    }

    public DiySkin(String picDirectory, String uploaded, String name, int size) {
        this.picDirectory = picDirectory;
        this.uploaded = uploaded;
        this.name = name;
        this.size = size;
    }

    public String getPicDirectory() {
        return picDirectory;
    }

    public void setPicDirectory(String picDirectory) {
        this.picDirectory = picDirectory;
    }

    public String getUploaded() {
        return uploaded;
    }

    public void setUploaded(String uploaded) {
        this.uploaded = uploaded;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
