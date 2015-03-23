package com.txnetwork.mypage.utils;

import android.graphics.Bitmap;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.txnetwork.mypage.R;

/**
 * Created by Administrator on 2015/1/12.
 */
public class UILUtils {

    public static DisplayImageOptions options = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.image_load_fail)
            .showImageOnLoading(R.drawable.default_background)
            .showImageOnFail(R.drawable.image_load_fail)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public static DisplayImageOptions DhOptions = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.unknown)
            .showImageOnLoading(R.drawable.unknown)
            .showImageOnFail(R.drawable.unknown)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();
}
