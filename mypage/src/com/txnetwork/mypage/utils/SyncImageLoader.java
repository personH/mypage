package com.txnetwork.mypage.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import com.txnetwork.mypage.entity.ReqFileBean;
import com.txnetwork.mypage.listener.OnFileLoadListener;

import java.io.File;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;

public class SyncImageLoader {
    Context mContext = null;
    private Object lock = new Object();
    private boolean mAllowLoad = true;
    private boolean firstLoad = true;
    private int mStartLoadLimit = 0;
    private int mStopLoadLimit = 0;
    final Handler handler = new Handler();
    LoadFileHelper loadImg = null;

    private int imageWidth = 0;
    private int imageHeight = 0;

    private static LruCache<String, Bitmap> imageCache = null;

    public SyncImageLoader(Context context, int imageWidth, int imageHeight) {
        this.mContext = context;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        if (imageCache == null) {
            LogUtils.LOGV("imageCache", "init imageCache");
            // modify by Maxj 2013-09-26 LruCache分配缓存异常
            int maxMemory = (int) (Runtime.getRuntime().maxMemory());
            int cacheSize = maxMemory / 8;
            imageCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    int biSize = value.getRowBytes();
                    int hei = value.getHeight();
                    int bitSize = biSize * hei;
                    int rSize = bitSize / 1024;
                    return rSize;
                }

                @Override
                protected void entryRemoved(boolean evicted, String key,Bitmap oldValue, Bitmap newValue) {
                    imageCache.remove(key);
                    new PhantomReference<Bitmap>(oldValue,new ReferenceQueue<Bitmap>());
                    oldValue = null;
                    System.gc();
                    LogUtils.LOGV("imageCache", "entryRemoved old" + oldValue);
                }
            };
        }
    }

    public void setLoadLimit(int startLoadLimit, int stopLoadLimit) {
        if (startLoadLimit > stopLoadLimit) {
            return;
        }
        mStartLoadLimit = startLoadLimit;
        mStopLoadLimit = stopLoadLimit;
    }

    public void restore() {
        mAllowLoad = true;
        firstLoad = true;
    }

    public void lock() {
        mAllowLoad = false;
        firstLoad = false;
    }

    public void unlock() {
        mAllowLoad = true;
        /*
         * synchronized (lock) { lock.notifyAll(); }
		 */
    }

    /**
     * 根据imageUrl从网络获取图片
     *
     * @param t
     * @param imageUrl
     * @param listener
     */
    public void loadImage(Integer t, final String imageUrl,
                          final String paramPath, OnFileLoadListener listener) {
        final OnFileLoadListener mListener = listener;
        final String mImageUrl = imageUrl;
        final Integer mt = t;
        new Thread(new Runnable() {
            @Override
            public void run() {
				/*
				 * if (!mAllowLoad) { synchronized (lock) { try { lock.wait(); }
				 * catch (InterruptedException e) { e.printStackTrace(); } } }
				 */
                LogUtils.LOGV("imageCache", "mImageUrl----------->" + mImageUrl);
                loadImage(mImageUrl, mt, paramPath, mListener);
				/*
				 * if (mAllowLoad && firstLoad) { imageCache.clear();
				 * loadImage(mImageUrl, mt, paramPath, mListener); }
				 * 
				 * if (mAllowLoad && mt <= mStopLoadLimit && mt >=
				 * mStartLoadLimit) { imageCache.clear(); loadImage(mImageUrl,
				 * mt, paramPath, mListener); }
				 */
            }

        }).start();
    }

    public void loadImage(final String mImageUrl, final int mt,
                          String paramPath, final OnFileLoadListener mListener) {
        try {
            if (StringUtil.isNotNull(mImageUrl)) {
                if (imageCache.get(mImageUrl) != null) {
                    final Bitmap d = imageCache.get(mImageUrl);
                    if (d != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (mAllowLoad) {
                                    mListener.onFileLoad(mt, d);
                                }
                            }
                        });
                        return;
                    }
                }
                loadImageFromUrl(mImageUrl, mt, paramPath, mListener);
            }
        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onError(mt);
                }
            });
            e.printStackTrace();
        }
    }

    /**
     * 根据图片url判断本地是否有该图片， 返回true，则告诉上层ImageView不用设置默认图片
     * 返回false，则告诉上层ImageView设置默认图等待网络加载
     *
     * @param imgUrl
     * @param paramPath
     * @return
     */
    public boolean isImageExist(String imgUrl, String paramPath) {
        boolean isExist = false;
        File f = FileUtil.findImageFileByPath(imgUrl, mContext, paramPath);
        if (f != null && f.length() > 0) {
            isExist = true;
        }
        return isExist;

    }

    public void loadImageFromUrl(String url, final int mt, String paramPath,
                                 final OnFileLoadListener mListener) throws IOException {
        File f = null;
        try {
            if (!TextUtils.isEmpty(url)) {
                f = FileUtil.findImageFileByPath(url, mContext, paramPath);
                if (f != null) {
                    if (f.length() <= 0) {
                        f.delete();
                    } else {
                        final Bitmap bmp = ImageUtil.getBitmapFromFile(f);
                        if (bmp != null) {
                            addCacheUrl(url, bmp);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (bmp != null && mListener != null) {
                                        mListener.onFileLoad(mt, bmp);
                                    }
                                }
                            });
                        } else {
                            f.delete();
                            sendRequest(url, paramPath, mt, mListener);
                        }
                    }

                } else {
                    sendRequest(url, paramPath, mt, mListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能说明： 根据相关信息是否要下载图片
     *
     * @param t           位置
     * @param imageUrl    图片下载请求的url
     * @param paramPath   图片存储路径
     * @param dowloadPath 图片下载路径
     * @param listener    回调接口
     */
    public void loadImage(Integer t, final String imageUrl,
                          final String paramPath, final String dowloadPath,
                          OnFileLoadListener listener) {
        final OnFileLoadListener mListener = listener;
        final String mImageUrl = imageUrl;
        final Integer mt = t;

        new Thread(new Runnable() {
            @Override
            public void run() {
				/*
				 * if (!mAllowLoad) { synchronized (lock) { try { lock.wait(); }
				 * catch (InterruptedException e) { e.printStackTrace(); } } }
				 */
                loadImage(mImageUrl, mt, paramPath, dowloadPath, mListener);
				/*
				 * if (mAllowLoad && firstLoad) { imageCache.clear();
				 * loadImage(mImageUrl, mt, paramPath, mListener); }
				 * 
				 * if (mAllowLoad && mt <= mStopLoadLimit && mt >=
				 * mStartLoadLimit) { imageCache.clear(); loadImage(mImageUrl,
				 * mt, paramPath, mListener); }
				 */
            }

        }).start();
    }

    /**
     * 功能说明： 根据相关信息是否要下载图片
     *
     * @param mt        位置
     * @param mImageUrl 图片下载请求的url
     * @param paramPath 图片存储路径
     */
    public void loadImage(final String mImageUrl, final int mt, String paramPath, String downloadPath,
                          final OnFileLoadListener mListener) {
        try {
            if (imageCache.get(mImageUrl) != null) {
                final Bitmap d = imageCache.get(mImageUrl);
                if (d != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mAllowLoad) {
                                mListener.onFileLoad(mt, d);
                            }
                        }
                    });
                    return;
                }
            }
            loadImageFromUrl(mImageUrl, mt, paramPath, downloadPath, mListener);
        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onError(mt);
                }
            });
            e.printStackTrace();
        }
    }

    public void loadImageFromUrl(String url, final int mt, String paramPath,
                                 String downloadPath, final OnFileLoadListener mListener)
            throws IOException {
        LogUtils.LOGV("SyncImageLoader", " loadImageFromUrl paramPath: " + paramPath);
        try {
            if (TextUtils.isEmpty(paramPath)) {

                getImageFromPNCollect(url, downloadPath, mt, mListener);

            } else {

                getImageFromPNInfo(url, paramPath, downloadPath, mt, mListener);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param url
     * @param paramPath
     * @param downloadPath
     * @param mt
     * @param mListener
     */
    private void getImageFromPNInfo(String url, String paramPath,
                                    String downloadPath, final int mt,
                                    final OnFileLoadListener mListener) {

        LogUtils.LOGV("SyncImageLoader", " getImageFromPNInfo url: " + url);

        File f = FileUtil.findImageFileByPath(paramPath);
        if (null != f) {
            if (f.length() <= 0) {
                f.delete();
            } else {
                getImageCallBack(f, url, mt, downloadPath, mListener);
            }
        } else {
            getImageFromPNCollect(url, downloadPath, mt, mListener);
        }
    }

    private void getImageFromPNCollect(String url, String downloadPath,
                                       final int mt, final OnFileLoadListener mListener) {

        LogUtils.LOGV("SyncImageLoader", " getImageFromPNCollect url: " + url);

        File f = FileUtil.findImageFileByPath(url, mContext, downloadPath);
        if (f != null) {
            if (f.length() <= 0) {
                f.delete();
            } else {
                getImageCallBack(f, url, mt, downloadPath, mListener);

            }
        } else {
            LogUtils.LOGV("SyncImageLoader",
                    " getImageFromPNCollect sendRequest url: " + url);
            sendRequest(url, downloadPath, mt, mListener);
        }

    }

    private void getImageCallBack(File f, String url, final int mt,
                                  String downloadPath, final OnFileLoadListener mListener) {

        LogUtils.LOGV("SyncImageLoader", " getImageCallBack url: " + url);

        final Bitmap bmp = ImageUtil.getBitmapFromFile(f);
        if (bmp != null) {
            addCacheUrl(url, bmp);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (bmp != null) {
                        mListener.onFileLoad(mt, bmp);
                    }
                }
            });
        } else {
            f.delete();
            LogUtils.LOGV("SyncImageLoader",
                    " getImageCallBack sendRequest url: " + url);
            sendRequest(url, downloadPath, mt, mListener);
        }

    }

    private void sendRequest(String url, String paramPath, final int mt,
                             final OnFileLoadListener mListener) {
        if (loadImg == null) {
            loadImg = LoadFileHelper.getInstance();
        }
        ReqFileBean bean = new ReqFileBean();
        bean.setHanlder(handler);
        bean.setListener(mListener);
        bean.setImgUrl(url);
        bean.setParamPath(paramPath);
        bean.setIndex(mt);
        bean.setContext(mContext);
        bean.setImageWidth(imageWidth);
        bean.setImageHeight(imageHeight);
        loadImg.addRequest(bean);
    }

    // add by qianch for 引用失效，内存释放 start
    public static void addCacheUrl(String url, Bitmap bitmap) {
        if (StringUtil.isNotNull(url) && imageCache != null && bitmap != null) {
            synchronized (imageCache) {
                imageCache.put(url, bitmap);
            }
            LogUtils.LOGV("imageCache",
                    "imageCache size--->" + imageCache.size());
            LogUtils.LOGV("imageCache",
                    "imageCache maxsize--->" + imageCache.maxSize());
            LogUtils.LOGV("imageCache",
                    "imageCache putCount--->" + imageCache.putCount());
            LogUtils.LOGV("imageCache", "imageCache evictionCount--->"
                    + imageCache.evictionCount());
        }
    }

    // add by qianch for 引用失效，内存释放 end
    public static void clearCache() {
		/*
		 * synchronized (imageCache) { Map<String,Bitmap> caches =
		 * imageCache.snapshot(); Set<String> keys = caches.keySet(); for(String
		 * k : keys){ Bitmap bitmap = caches.get(k); caches.remove(bitmap);
		 * LogUtils.LOGV("imageCache", "clearCache"); } caches = null;
		 * 
		 * }
		 */
        System.gc();
    }

    public static Bitmap getCache(String url) {
        if (StringUtil.isNotNull(url) && imageCache != null) {
            return imageCache.get(url);
        }
        return null;
    }

    public static LruCache<String, Bitmap> getImageCache() {
        return imageCache;
    }

    public static void removeImageCache(String url) {
        if (imageCache != null) {
            synchronized (imageCache) {
                {
                    imageCache.remove(url);
                }

            }
        }

    }

    public static void clearLru() {
        if (imageCache != null) {
            imageCache.evictAll();
        }

    }

    public void freeContext() {
        this.mContext = null;
    }
}