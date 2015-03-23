package com.txnetwork.mypage.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hcz on 2014/12/19.
 * <p/>
 * 我的导航
 * 导航基本信息
 */
public class UrlAddr implements Parcelable {

    public static final int ADD_BUTTON = -1;

    private String name;
    private String url;
    private String icon;//网站图标地址
    private int type;
    private int order;

    public UrlAddr(String name, String url, String icon, int type, int order) {
        this.name = name;
        this.url = url;
        this.icon = icon;
        this.type = type;
        this.order = order;
    }

    public UrlAddr(Parcel source) {
        name = source.readString();
        url = source.readString();
        icon = source.readString();
        type = source.readInt();
        order = source.readInt();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(icon);
        dest.writeInt(type);
        dest.writeInt(order);
    }

    /**
     * Implement the Parcelable interface {@hide}
     */
    public static final Creator<UrlAddr> CREATOR = new Creator<UrlAddr>() {
        public UrlAddr createFromParcel(Parcel in) {
            return new UrlAddr(in);
        }

        public UrlAddr[] newArray(int size) {
            return new UrlAddr[size];
        }
    };
}
