package com.txnetwork.mypage.entity;

/**
 * Created by Administrator on 2015/1/13.
 */
public class Skin {

    public static final int DIY_SKIN = 0;//自定义
    public static final int OTHERS = -1;//添加
    public static final int WEB_SKIN = 1;//服务器皮肤

    private String picid;//图片的id
    private String url;
    private String s_url;//缩略图
    private String name;
    private int size;
    private int type;//0表示本地,1表示服务器端,-1表示添加

    public Skin() {
        type = OTHERS;
    }

    public Skin(String s_url, String url) {
        this.s_url = s_url;
        this.url = url;
        this.type = WEB_SKIN;
    }

    public Skin(String picid, String s_url, String url, String name, int size, int type) {
        this.picid=picid;
        this.s_url = s_url;
        this.url = url;
        this.name = name;
        this.size = size;
        this.type = type;
    }

    public String getPicid() {
        return picid;
    }

    public void setPicid(String picid) {
        this.picid = picid;
    }

    public String getS_url() {
        return s_url;
    }

    public void setS_url(String s_url) {
        this.s_url = s_url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
