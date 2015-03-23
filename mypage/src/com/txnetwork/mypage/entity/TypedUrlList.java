package com.txnetwork.mypage.entity;

import com.txnetwork.mypage.dsgv.DragGridAdapter;

import java.util.List;

/**
 * Created by hcz on 2015/2/2.
 *
 * 我的导航
 * 每一个大类,及包含的导航信息
 *
 */
public class TypedUrlList {

    private String name;
    private int type;
    private boolean isExpanded = true;
    private List<UrlAddr> urlAddrTypeList;
    private DragGridAdapter dragGridAdapter;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean isVisiable) {
        this.isExpanded = isVisiable;
    }

    public List<UrlAddr> getUrlAddrTypeList() {
        return urlAddrTypeList;
    }

    public void setUrlAddrTypeList(List<UrlAddr> urlAddrTypeList) {
        this.urlAddrTypeList = urlAddrTypeList;
    }

    public DragGridAdapter getDragGridAdapter() {
        return dragGridAdapter;
    }

    public void setDragGridAdapter(DragGridAdapter dragGridAdapter) {
        this.dragGridAdapter = dragGridAdapter;
    }

    @Override
    public String toString() {
        return name;
    }
}
