package com.txnetwork.mypage.entity;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hcz on 2015/1/4.
 * <p/>
 * 我的导航
 * 导航类别
 */
public class Type implements Parcelable {

    private String name;//导航名称
    private int type;//导航
    private int isExpanded;//是否展开
    private int order;//排序

    public Type(String name, int type, int isExpanded, int order) {
        this.type = type;
        this.name = name;
        this.order = order;
        this.isExpanded = isExpanded;
    }

    public Type(Parcel source) {
        name = source.readString();
        type = source.readInt();
        isExpanded = source.readInt();
        order = source.readInt();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getIsExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(int isExpanded) {
        this.isExpanded = isExpanded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(type);
        dest.writeInt(isExpanded);
        dest.writeInt(order);
    }

    /**
     * Implement the Parcelable interface {@hide}
     */
    public static final Creator<Type> CREATOR = new Creator<Type>() {
        public Type createFromParcel(Parcel in) {
            return new Type(in);
        }

        public Type[] newArray(int size) {
            return new Type[size];
        }
    };
}
