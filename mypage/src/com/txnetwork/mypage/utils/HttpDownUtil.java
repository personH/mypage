package com.txnetwork.mypage.utils;

import android.content.Context;
import com.txnetwork.mypage.http.MySSLSocketFactory;
import com.txnetwork.mypage.http.NetworkManager;
import com.txnetwork.mypage.listener.OnDownloadListener;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.*;
import java.security.KeyStore;
import java.util.Map;

public class HttpDownUtil {
	private static final String TAG = HttpDownUtil.class.getSimpleName();
	private final static int TIMEOUT = 35 * 1000;
	private static HttpClient httpClient = null;

	public static File downloadImageFile(Context context, String url, File file) {
		LogUtils.LOGV(TAG, "downloadImageFile..., url = " + url);

		HttpEntity entity = null;
		InputStream conIn = null;
		DataInputStream in = null;
		OutputStream out = null;
		httpClient = getNewHttpClient(context);

		HttpGet httpGet = null;
		long totalSize = 0;
		try {

			String path = file.getAbsolutePath();

			file.createNewFile();
			boolean toTemp = file.renameTo(new File(path + ".temp"));
			LogUtils.LOGD(TAG, "renameTo .temp file = " + toTemp);

			file = new File(path + ".temp");

			long startTime = System.nanoTime();
			httpGet = new HttpGet(url);
			Object obj = httpClient.getParams().getParameter(
					ConnRoutePNames.DEFAULT_PROXY);
			boolean proxyFlag = (obj != null);
			if (proxyFlag) {
				httpGet.setHeader("Connection", "Close");
			} else {
				httpGet.setHeader("Connection", "Keep-Alive");
			}

			HttpResponse httpResponse = httpClient.execute(httpGet);

			if (httpResponse != null) {
				long endTime = System.nanoTime();
				LogUtils.LOGV(TAG, (endTime - startTime) / 1000000
						+ "ms used to get image: " + url);

				StatusLine line = httpResponse.getStatusLine();
				if (line != null) {
					int responseCode = line.getStatusCode();

					LogUtils.LOGV(TAG, "ResponseCode = " + responseCode);

					if (responseCode == HttpStatus.SC_OK) {
						entity = httpResponse.getEntity();
						if (entity != null) {
							conIn = entity.getContent();
							totalSize = entity.getContentLength();

							in = new DataInputStream(conIn);
							out = new FileOutputStream(file);
							byte[] buffer = new byte[1024];
							int byteread = 0;
							while ((byteread = in.read(buffer)) != -1) {
								out.write(buffer, 0, byteread);
							}

							File f = new File(path + ".temp");
							boolean toFinal = f.renameTo(new File(path));
							LogUtils.LOGD(TAG, "renameTo final file = "
									+ toFinal);

							file = new File(path);

						} else {
							if (file != null) {
								file.delete();
								file = null;
							}
						}

					} else {
						LogUtils.LOGV(
								"downImage",
								url
										+ " downLoadImage Server return error, response code = "
										+ responseCode);
						if (file != null) {
							file.delete();
							file = null;
						}
					}
				} else {
					if (file != null) {
						file.delete();
						file = null;
					}
					LogUtils.LOGV("downImage", url
							+ " Server return error, StatusLine  " + line);
				}

			} else {
				if (file != null) {
					file.delete();
					file = null;
				}
				LogUtils.LOGV("downImage", url
						+ " Server return error, httpResponse  " + httpResponse);
			}

		} catch (Exception e) {
			LogUtils.LOGE("downImage",
					url + " downImage Exception -----" + e.getMessage());
			if (file != null) {
				file.delete();
				file = null;
			}
			if (httpGet != null) {
				httpGet.abort();
			}
		} finally {
			if (file != null) {
				if (file.length() != totalSize) {
					file.delete();
				}
			}
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (conIn != null) {
					conIn.close();
				}
				if (entity != null) {
					entity.consumeContent();
				}
				if (httpGet != null) {
					httpGet.abort();
					httpGet = null;
				}
				if (httpClient != null) {
					httpClient.getConnectionManager().closeExpiredConnections();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return file;
	}
	
	@SuppressWarnings("finally")
	public static boolean downloadApkFile(String url, int mode,
			Context context, OnDownloadListener listener, long size)
			throws Exception {

		boolean ret = true;
		try {
			ret = breakPointDownload(context,
					FileUtil.getApkRandomAccessFile(context), url, size,
					listener);

		} catch (Exception e) {
			ret = false;
		} finally {
			return ret;
		}
	}

	public static boolean breakPointDownload(Context context, File file,
			String url, long size, OnDownloadListener listener) {

		boolean ret = true;
		InputStream in = null;
		RandomAccessFile raf = null;
		HttpGet httpGet = null;
		long len = 0;
		httpClient = getNewHttpClient(context);
		try {
			if (file != null && url != null && url.length() > 0) {

				raf = new RandomAccessFile(file, "rw");
				len = raf.length();
				String start = "bytes=" + len + "-";
				httpGet = new HttpGet(url);
				httpGet.setHeader("Range", start);
				HttpResponse httpResponse = httpClient.execute(httpGet);
				int responseCode = httpResponse.getStatusLine().getStatusCode();
				if (responseCode == HttpStatus.SC_OK || responseCode == HttpStatus.SC_PARTIAL_CONTENT) {
					if (len == 0 || (len > 0 && responseCode == HttpStatus.SC_PARTIAL_CONTENT)) {
						long totalSize = httpResponse.getEntity().getContentLength();
						if (size <= 0) {
							size = totalSize;
							//SharedUtil.setSoftUpdateApkSize(context, size);
						}
						long downloadSize = len;
						in = httpResponse.getEntity().getContent();
						raf.seek(len);
						byte[] bytes = new byte[4096];
						int c;
						while ((c = in.read(bytes)) != -1) {
							raf.write(bytes, 0, c);
							downloadSize += c;
							if (listener != null && !listener.onDownload(size, downloadSize)) {
								ret = false;
								break;
							}
						}
					} else {
						ret = false;
					}
				}

				if (ret == false) {
					if (httpGet != null) {
						httpGet.abort();
					}
					httpResponse.getEntity().consumeContent();
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			ret = false;
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (raf != null) {
					raf.close();
				}
				if (httpClient != null) {
					httpClient.getConnectionManager().closeExpiredConnections();
				}
				if (httpGet != null) {
					httpGet.abort();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
	}

	public static HttpClient getNewHttpClient(Context context) {
		try {
			if (httpClient == null) {
				KeyStore trustStore = KeyStore.getInstance(KeyStore
						.getDefaultType());
				trustStore.load(null, null);
				SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
				sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

				HttpParams params = new BasicHttpParams();
				HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
				HttpProtocolParams.setContentCharset(params, "gbk");
				// About 100-Continue
				// 100-Continue while cause some performance problem because the
				// request being send twice.
				// Expect-Continue is only needed when your request is large
				// (like file uploading)
				// and the server may have authorization requirement.
				// You don't want send a huge file and get a Access Denied
				// error.
				// So you just send the headers first and if the server says
				// continue,
				// you will then send the whole request.
				HttpProtocolParams.setUseExpectContinue(params, false);

				HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
				HttpConnectionParams.setSoTimeout(params, TIMEOUT);

				SchemeRegistry registry = new SchemeRegistry();
				registry.register(new Scheme("http", PlainSocketFactory
						.getSocketFactory(), 80));
				registry.register(new Scheme("https", sf, 443));
				ClientConnectionManager ccm = new ThreadSafeClientConnManager(
						params, registry);

				httpClient = new DefaultHttpClient(ccm, params);
			}
			setProxy(context, httpClient);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return httpClient;
	}

	private static void setProxy(Context context, HttpClient client) {
		HttpHost proxy = null;
		try {
			if ((android.os.Build.VERSION.SDK_INT <= 7)
					&& !NetworkManager.isOPhone()
					&& !NetworkManager.isWIFIConnected(context)) {
				Map<String, Object> map = NetworkManager.getProxy();
				if (map != null && !map.isEmpty()) {
					String proxyHost = (String) map
							.get(NetworkManager.PROXY_HOST);
					int proxyPort = (Integer) map
							.get(NetworkManager.PROXY_PORT);
					proxy = new HttpHost(proxyHost, proxyPort);

					LogUtils.LOGV(TAG, "Set default proxy to: " + proxyHost
							+ ":" + proxyPort);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			proxy = null;
		} finally {
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
		}
	}
}
