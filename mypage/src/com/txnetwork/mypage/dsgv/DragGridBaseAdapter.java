package com.txnetwork.mypage.dsgv;

import com.txnetwork.mypage.entity.UrlAddr;

/**
 * Created by Administrator on 2015/1/15.
 */
public interface DragGridBaseAdapter {
    /**
     * 重新排列数据
     *
     * @param oldPosition
     * @param newPosition
     */
    public void reorderItems(int oldPosition, int newPosition);


    /**
     * 设置某个item隐藏
     *
     * @param hidePosition
     */
    public void setHideItem(int hidePosition);

    /**
     * 获取adapter大小
     *
     * @return
     */
    public int getSize();

    public void deleteItem(int position);

    public void addItem(UrlAddr urlAddr);


    public UrlAddr getItemAtPostion(int postion);


}
