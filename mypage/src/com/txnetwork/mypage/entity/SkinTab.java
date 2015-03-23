package com.txnetwork.mypage.entity;

/**
 * Created by hcz on 2015/1/20.
 *
 * 皮肤管理
 * 皮肤tab页面
 */
public class SkinTab {

    private int tabid;//
    private String tabname;//
    private int tabtype;//
    private int sort;//排序
    private String datecreated;//

    public SkinTab(int tabid, String tabname, int tabtype, int sort, String datecreated) {

        this.tabid = tabid;
        this.tabname = tabname;
        this.tabtype = tabtype;
        this.sort = sort;
        this.datecreated = datecreated;
    }

    public int getTabid() {
        return tabid;
    }

    public void setTabid(int tabid) {
        this.tabid = tabid;
    }

    public String getTabname() {
        return tabname;
    }

    public void setTabname(String tabname) {
        this.tabname = tabname;
    }

    public int getTabtype() {
        return tabtype;
    }

    public void setTabtype(int tabtype) {
        this.tabtype = tabtype;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }
}
