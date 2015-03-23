package com.txnetwork.mypage.entity;

/**
 * Created by Administrator on 2015/1/6.
 */
public class DownloadedSkin {

    public static String TRUE = "true";
    public static String FALSE = "false";

    private int drawbleId;//皮肤图片资源Id
    private String picid;//远程皮肤Id
    private String picDirectory;//远程皮肤本地文件的路径
    private String isLocalTheme;//是否是本地主题
    private String name;//主题的名称
    private int size;//图片资源的大小


    public DownloadedSkin(String picDirectory, String name, int size) {
        this.picDirectory = picDirectory;
        this.name = name;
        this.size = size;
        isLocalTheme = FALSE;
    }

    public DownloadedSkin(int drawbleId, String picid, String picDirectory, String isLocalTheme, String name, int size) {
        this.drawbleId = drawbleId;
        this.picid = picid;
        this.picDirectory = picDirectory;
        this.isLocalTheme = isLocalTheme;
        this.name = name;
        this.size = size;
    }

    public int getDrawbleId() {
        return drawbleId;
    }

    public void setDrawbleId(int drawbleId) {
        this.drawbleId = drawbleId;
    }

    public String getPicid() {
        return picid;
    }

    public void setPicid(String picid) {
        this.picid = picid;
    }

    public String getPicDirectory() {
        return picDirectory;
    }

    public void setPicDirectory(String picDirectory) {
        this.picDirectory = picDirectory;
    }

    public String getIsLocalTheme() {
        return isLocalTheme;
    }

    public void setIsLocalTheme(String isLocalTheme) {
        this.isLocalTheme = isLocalTheme;
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
