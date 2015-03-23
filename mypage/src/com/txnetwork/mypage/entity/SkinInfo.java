package com.txnetwork.mypage.entity;

/**
 * Created by Administrator on 2015/1/20.
 */
public class SkinInfo {


    private String picid;
    private String picname;
    private String newname;
    private int pictype;
    private String suffix;
    private int picsize;
    private String picpath;
    private String picsrc;
    private String datacreated;

    public SkinInfo(String picid, String picname, String newname, int pictype, String suffix, int picsize, String picpath, String picsrc, String datacreated) {
        this.picid=picid;
        this.picname = picname;
        this.newname = newname;
        this.pictype = pictype;
        this.suffix = suffix;
        this.picsize = picsize;
        this.picpath = picpath;
        this.picsrc = picsrc;
        this.datacreated = datacreated;
    }

    public String getPicid() {
        return picid;
    }

    public void setPicid(String picid) {
        this.picid = picid;
    }

    public String getPicname() {
        return picname;
    }

    public void setPicname(String picname) {
        this.picname = picname;
    }

    public String getNewname() {
        return newname;
    }

    public void setNewname(String newname) {
        this.newname = newname;
    }

    public int getPictype() {
        return pictype;
    }

    public void setPictype(int pictype) {
        this.pictype = pictype;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getPicsize() {
        return picsize;
    }

    public void setPicsize(int picsize) {
        this.picsize = picsize;
    }

    public String getPicpath() {
        return picpath;
    }

    public void setPicpath(String picpath) {
        this.picpath = picpath;
    }

    public String getPicsrc() {
        return picsrc;
    }

    public void setPicsrc(String picsrc) {
        this.picsrc = picsrc;
    }

    public String getDatacreated() {
        return datacreated;
    }

    public void setDatacreated(String datacreated) {
        this.datacreated = datacreated;
    }
}
