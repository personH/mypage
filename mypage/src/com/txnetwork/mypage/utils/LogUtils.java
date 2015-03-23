package com.txnetwork.mypage.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtils {
	public static boolean DEBUG = true;

	/*
	 * private static final String LOG_PREFIX = "SurfNews_"; private static
	 * final int LOG_PREFIX_LENGTH = LOG_PREFIX.length(); private static final
	 * int MAX_LOG_TAG_LENGTH = 23;
	 * 
	 * public static String makeLogTag(String str) { if (str.length() >
	 * MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) { return LOG_PREFIX +
	 * str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1); }
	 * 
	 * return LOG_PREFIX + str; }
	 */

	/**
	 * WARNING: Don't use this when obfuscating class names with Proguard!
	 */
	/*
	 * public static String makeLogTag(Class cls) { return
	 * makeLogTag(cls.getSimpleName()); }
	 */

	public static void LOGD(final String tag, String message) {
		// if (Log.isLoggable(tag, Log.DEBUG)) {
		if (DEBUG) {
			Log.d(tag, message);
		}
	}

	public static void LOGD(final String tag, String message, Throwable cause) {
		// if (Log.isLoggable(tag, Log.DEBUG)) {
		if (DEBUG) {
			Log.d(tag, message, cause);
		}
	}

	public static void LOGV(final String tag, String message) {
		// noinspection PointlessBooleanExpression,ConstantConditions
		// if (DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
		if (DEBUG) {
			Log.v(tag, message);
		}
	}

	public static void LOGV(final String tag, String message, Throwable cause) {
		// noinspection PointlessBooleanExpression,ConstantConditions
		// if (DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
		if (DEBUG) {
			Log.v(tag, message, cause);
		}
	}

	public static void LOGI(final String tag, String message) {
		if (DEBUG) {
			Log.i(tag, message);
		}
	}

	public static void LOGI(final String tag, String message, Throwable cause) {
		if (DEBUG) {
			Log.i(tag, message, cause);
		}
	}

	public static void LOGW(final String tag, String message) {
		if (DEBUG) {
			Log.w(tag, message);
		}
	}

	public static void LOGW(final String tag, String message, Throwable cause) {
		if (DEBUG) {
			Log.w(tag, message, cause);
		}
	}

	public static void LOGE(final String tag, String message) {
		if (DEBUG) {
			Log.e(tag, message);
		}
	}

	public static void LOGE(final String tag, String message, Throwable cause) {
		if (DEBUG) {
			Log.e(tag, message, cause);
		}
	}

	private LogUtils() {
	}

	private static String MYLOGFILEName = "txwlkj_wifiTx_Log.txt";// 本类输出的日志文件名称
	private static SimpleDateFormat myLogSdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");// 日志的输出格式
	private static SimpleDateFormat logfile = new SimpleDateFormat("yyyy-MM-dd");// 日志文件格式
	public static final String LOG_FILE_PATH = "/TxNetWork/log/";
	public static void putLog(Context context, String message) {
		if (DEBUG) {
			Log.e("LogUtils====putLog", message);
			writeLogtoFile(context, "e", "txwlkj", message);
		}
	}
	
	/**
	 * 打开日志文件并写入日志
	 * 
	 * @return
	 * **/
	private static void writeLogtoFile(Context context, String mylogtype,
			String tag, String text) {// 新建或打开日志文件
		Date nowtime = new Date();
		String needWriteFiel = logfile.format(nowtime);
		String needWriteMessage = myLogSdf.format(nowtime) + "    ==" + System.currentTimeMillis()
				+ "==    " + tag + "    " + text;
		String filepath = null;

		// if (android.os.Build.VERSION.SDK_INT < 8) {
		// if (FileUtil.isSDCardAvaliable()) {
		// filepath = Environment.getExternalStorageDirectory() + "/";
		// }
		// } else {
		// if (FileUtil.isSDCardAvaliable()) {
		// filepath = context.getExternalCacheDir().getAbsolutePath()
		// + "/";
		// }
		// }

		if (FileUtil.isSDCardAvaliable()) {
			filepath = Environment.getExternalStorageDirectory()
					+ LOG_FILE_PATH;
			if (!new File(filepath).exists()) {
				new File(filepath).mkdirs();
			}
		}

		if (null == filepath) {
			return;
		}

		File file = new File(filepath, (needWriteFiel + MYLOGFILEName));
		try {
			FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
			BufferedWriter bufWriter = new BufferedWriter(filerWriter);
			bufWriter.write(needWriteMessage);
			bufWriter.newLine();
			bufWriter.close();
			filerWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
