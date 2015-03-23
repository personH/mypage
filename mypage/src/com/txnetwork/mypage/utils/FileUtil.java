package com.txnetwork.mypage.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import com.txnetwork.mypage.entity.ReqFileBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

public class FileUtil {
	
	private static final String TAG = FileUtil.class.getSimpleName();
	public static final String WIFISW_FILE_ICON = "/TxNetWork/icon/";
	public static final String WIFISW_WEBVIEW = "/TxNetWork/webview/";
	public static final String WIFISW_FILE_AVATAR = "/TxNetWork/avatar/";
	public static final String APK_FILE_PATH = "/TxNetWork/apk/";
	public static final String APK_FILE_NAME = "upgrade.apk";

	public static File getApkRandomAccessFile(Context context)
			throws IOException {
		File file = null;
		if (isSDCardAvaliable()) {
			String filepath = Environment.getExternalStorageDirectory()
					+ APK_FILE_PATH;
			if (!new File(filepath).exists()) {
				LogUtils.LOGW("getApkFile", "mkdirs");
				new File(filepath).mkdirs();
			}
			file = new File(filepath + APK_FILE_NAME);
		} else {
			file = context.getFileStreamPath(APK_FILE_NAME);
			if (file != null && !file.exists()) {
				FileOutputStream out = null;
				out = context.openFileOutput(APK_FILE_NAME,
						Context.MODE_WORLD_READABLE);
				// TODO Auto-generated catch block
				if (out != null) {
					out.close();
				}
			}
		}
		return file;
	}

	public static boolean isSDCardAvaliable() {
		LogUtils.LOGE(TAG, "===isSDCardAvaliable====");
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static File getApkFile(Context context) {
		File file = null;
		if (isSDCardAvaliable()) {
			String filepath = Environment.getExternalStorageDirectory()
					+ APK_FILE_PATH;
			if (!new File(filepath).exists()) {
				LogUtils.LOGW("getApkFile", "mkdirs");
				new File(filepath).mkdirs();
			}
			file = new File(filepath + APK_FILE_NAME);
		} else {
			file = context.getFileStreamPath(APK_FILE_NAME);
		}
		return file;
	}

	public static void deleteApkFile(Context context) {
		if (isSDCardAvaliable()) {
			String filepath = Environment.getExternalStorageDirectory()
					+ APK_FILE_PATH;
			File file = new File(filepath + APK_FILE_NAME);
			if (file.exists()) {
				file.delete();
			}
		} else {
			context.deleteFile(APK_FILE_NAME);
		}
	}


	/**
	 * 判断该图片url命名的图片是否已经存在，存在则返回改图片文件 否则：不存在则调用httpclientUtil的downloadImg方法下载图片
	 */
	public static File createImageCacheFile(ReqFileBean bean, String url,
			Context context, String paramPath) {
		String fileName = digest2Str(url);
		File cacheFile = null;
		try {
			String filepath = getFilePath(context, paramPath);
			cacheFile = new File(filepath + fileName);
			LogUtils.LOGI(TAG, " createImageCacheFile filepath: " + filepath
					+ " cacheFile: " + cacheFile);

			if (cacheFile.exists()) {
				return cacheFile;
			} else {
				if (!new File(filepath).exists()) {
					LogUtils.LOGW("loadImageFromUrl", "mkdirs");
					new File(filepath).mkdirs();
				}
				// cacheFile.createNewFile();
				cacheFile = HttpDownUtil.downloadImageFile(context, url,
						cacheFile);

			}
		} catch (Exception e) {
			cacheFile = null;
			LogUtils.LOGE(
					TAG,
					" FileUtil createImageCacheFile Exception "
							+ e.getMessage());
			e.printStackTrace();
		}
		return cacheFile;
	}

	public static File findImageFileByPath(String url, Context context,String paramPath) {
		File file = null;
		try {
			String fileName = digest2Str(url);
			String filepath = getFilePath(context, paramPath);
			file = new File(filepath + fileName);
			if (file.exists()) {
				return file;
			}
		} catch (Exception e) {
			LogUtils.LOGE(TAG,"findImageFileByPath paramPath Exception " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public static File findImageFileByPath(String imgFilePath) {
		File file = null;
		try {

			file = new File(imgFilePath);
			if (file.exists()) {
				return file;
			}
		} catch (Exception e) {
			LogUtils.LOGE(
					TAG,
					"findImageFileByPath imgFilePath Exception "
							+ e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	public static String getFilePath(Context context, String paramPath) {
		try {
			// modify by wangtao on 2013-1-10 modify filepath start
			String packageName = "/." + context.getPackageName();
			paramPath = packageName + paramPath;
			String filepath = context.getCacheDir().getPath() + paramPath;
			if (Build.VERSION.SDK_INT < 8) {
				if (isSDCardAvaliable()) {
					filepath = Environment.getExternalStorageDirectory()+ paramPath;
				}
			} else {
				if (isSDCardAvaliable()) {
					File file = context.getExternalCacheDir();
					if (null != file) {
						filepath = file.getAbsolutePath() + paramPath;
					}
				}
			}
			// modify by wangtao on 2013-1-10 modify filepath end
			return filepath;
		} catch (Exception e) {
			LogUtils.LOGE(TAG, "getFilePath error!");
			return "/";
		}
	}
	
	public static byte[] digest2Bytes(byte[] bytes) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (Exception localNoSuchAlgorithmException) {
		}
		return md.digest(bytes);
	}

	public static String digest2Str(byte[] bytes) {
		return bytes2Hex(digest2Bytes(bytes));
	}

	public static String digest2Str(String str) {
		if (TextUtils.isEmpty(str)) {
			return "";
		}
		return digest2Str(str.getBytes());
	}

	public static String bytes2Hex(byte[] b) {
		char[] chars = new char[b.length * 2];
		int charsIndex = 0;

		for (int bytesIndex = 0; bytesIndex < b.length; ++bytesIndex) {
			int intValue = b[bytesIndex];
			if (intValue < 0) {
				intValue += 256;
			}

			int intValueHi = (intValue & 0xF0) >> 4;
			if (intValueHi > 9)
				chars[charsIndex] = (char) (intValueHi - 10 + 97);
			else {
				chars[charsIndex] = (char) (intValueHi + 48);
			}

			int intValueLo = intValue & 0xF;
			if (intValueLo > 9)
				chars[(charsIndex + 1)] = (char) (intValueLo - 10 + 97);
			else {
				chars[(charsIndex + 1)] = (char) (intValueLo + 48);
			}
			charsIndex += 2;
		}
		return new String(chars, 0, charsIndex);
	}
}
