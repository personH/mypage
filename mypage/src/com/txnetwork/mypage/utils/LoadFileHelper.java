package com.txnetwork.mypage.utils;

import android.graphics.Bitmap;
import android.os.Handler;
import com.txnetwork.mypage.entity.ReqFileBean;
import com.txnetwork.mypage.listener.OnFileLoadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;

public class LoadFileHelper {
	private static final String TAG = LoadFileHelper.class.getSimpleName();

	private boolean isRun = true;
	private boolean exceBool = true;

	private static LoadFileHelper imgHelper = null;
	private static Vector<ReqFileBean> queue = new Vector<ReqFileBean>();

	private Object syncToken = new Object();

	private LoadFileHelper() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (isRun) {
					try {
						if ((queue == null) || queue.isEmpty()) {
							synchronized (syncToken) {
								LogUtils.LOGW(TAG, "syncToken.wait()");
								syncToken.wait();
							}
						} else {
							if (exceBool) {
								if (queue != null && !queue.isEmpty()) {
									LogUtils.LOGW(TAG, "queue.size()---->"
											+ queue.size());
									exceBool = false;
									ReqFileBean bean = queue.get(0);
									LogUtils.LOGE(TAG,
											"start " + bean.getImgUrl());
									if (bean.getImgUrl() != null
											&& bean.getImgUrl().length() > 0) {
										downloadImg(bean);
									}
								}
							}
							Thread.sleep(10);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	public static synchronized LoadFileHelper getInstance() {
		if (imgHelper == null) {
			imgHelper = new LoadFileHelper();
		}
		return imgHelper;
	}

	public void addRequest(ReqFileBean bean) {
		if (null == bean) {
			return;
		}

		try {
			// 避免下载重复的图片或者文件
			if (bean.getImgUrl() != null && bean.getImgUrl().length() > 0) {
				if (queue.contains(bean)) {
					return;
				}
			}
			queue.add(bean);
			synchronized (syncToken) {
				LogUtils.LOGW(TAG, "syncToken.wait()");
				syncToken.notify();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downloadImg(final ReqFileBean bean) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				FileOutputStream fOutStream = null;
				try {
					File file = FileUtil.createImageCacheFile(bean,
							bean.getImgUrl(), bean.getContext(),
							bean.getParamPath());
					if (file != null) {
						if (file.length() <= 0) {
							file.delete();
						} else {
							Bitmap fbitmap = null;
							Bitmap bitmap = ImageUtil.getBitmapFromFile(file);
							
							// end
							if (bitmap != null) {
								if (bean.getImageWidth() > 0
										&& bean.getImageHeight() > 0) {
									boolean needScale = !(bitmap.getHeight() == bean
											.getImageHeight() && bitmap
											.getWidth() == bean.getImageWidth());
									LogUtils.LOGI("downfile", "needScale--->"
											+ needScale);
									if (needScale) {
										Bitmap tbitmap = Bitmap
												.createScaledBitmap(bitmap,
														bean.getImageWidth(),
														bean.getImageHeight(),
														true);
										if (tbitmap != null) {
											file.delete();
											fOutStream = new FileOutputStream(
													file);
											boolean bitFlag = tbitmap.compress(
													Bitmap.CompressFormat.JPEG,
													75, fOutStream);
											if (bitFlag) {
												fOutStream.flush();
												fOutStream.close();
												bitmap.recycle();
												tbitmap.recycle();
											} else {
												fOutStream.close();
												bitmap.recycle();
												tbitmap.recycle();
											}
										}

									}
								}
								fbitmap = ImageUtil.getBitmapFromFile(file);
								if (null != fbitmap) {
									SyncImageLoader.addCacheUrl(
											bean.getImgUrl(), fbitmap);
									notifySuccessComm(bean, fbitmap);
								} else {
									notifyErrorComm(bean);
									file.delete();
								}

							} else {
								file.delete();
							}
						}
					} else {
						notifyErrorComm(bean);
					}
				} catch (Exception e) {
					LogUtils.LOGE(TAG,
							"downloadImg  Exception " + e.getMessage());
					e.printStackTrace();
					notifyErrorComm(bean);
				} finally {
					if (fOutStream != null) {
						try {
							fOutStream.flush();
							fOutStream.close();
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
					allowNextReq();
				}
			}

		}).start();
	}

	/*
	 * public void downloadContent(final ReqFileBean bean) { new Thread(new
	 * Runnable() {
	 * 
	 * @Override public void run() { try { File file =
	 * FileUtil.createContentCacheFile(bean.getContext(), bean.getNewsId(),
	 * bean.getJson(), FileUtil.NEWS_FILE_CONTENT); if (file != null) { String
	 * content = FileUtil.getNewsContent(bean.getContext(), file); if (content
	 * != null && content.length() > 0) { notifySuccessComm(bean, content); } }
	 * else { notifyErrorComm(bean); } } catch (Exception e) { Log.e(TAG,
	 * "downloadContent  Exception " + e.getMessage()); notifyErrorComm(bean); }
	 * finally { allowNextReq(); } } }).start(); }
	 */

	// 允许开启新的线程下载队列后的成员
	private void allowNextReq() {
		if (!queue.isEmpty()) {
			queue.remove(0);
		}
		exceBool = true;
	}

	private void notifySuccessComm(final ReqFileBean bean, final Object o) {
		Handler handler = bean.getHanlder();
		final OnFileLoadListener listener = bean.getListener();
		if (o != null) {
			if (handler != null) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						if (listener != null) {
							listener.onFileLoad(bean.getIndex(), o);
							ImageUtil.freeReqFileBean(bean);
						}
					}
				});
			} else {
				if (listener != null) {
					listener.onFileLoad(bean.getIndex(), o);
					ImageUtil.freeReqFileBean(bean);
				}
			}
		} else {
			notifyErrorComm(bean);
		}
	}

	public void notifyErrorComm(final ReqFileBean bean) {
		Handler handler = bean.getHanlder();
		final OnFileLoadListener listener = bean.getListener();
		if (handler != null) {
			handler.post(new Runnable() {
				@Override
				public void run() {
					if (listener != null) {
						listener.onError(bean.getIndex());
						ImageUtil.freeReqFileBean(bean);
					}
				}
			});
		} else {
			if (listener != null) {
				listener.onError(bean.getIndex());
				ImageUtil.freeReqFileBean(bean);
			}
		}
	}
}