package com.txnetwork.mypage.http;


import com.txnetwork.mypage.utils.LogUtils;
import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class MySSLSocketFactory extends SSLSocketFactory {
	SSLContext sslContext = null;

	public MySSLSocketFactory(KeyStore truststore)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
		super(truststore);
		sslContext = SSLContext.getInstance("TLS");
		TrustManager tm = new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {

			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {

			}

			public X509Certificate[] getAcceptedIssuers() {

				return null;

			}
		};
		sslContext.init(null, new TrustManager[] { tm }, null);
	}

	@Override
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {
		LogUtils.LOGW("MySSLSocketFactory", "socket--->" + socket);
		LogUtils.LOGW("MySSLSocketFactory", "host--->" + host);
		LogUtils.LOGW("MySSLSocketFactory", "port--->" + port);
		LogUtils.LOGW("MySSLSocketFactory", "autoClose--->" + autoClose);
		// modify by wangtao on 20120712 start
		if (port == -1) {
			port = 443;
		}
		// modify by wangtao on 20120712 end
		return sslContext.getSocketFactory().createSocket(socket, host, port,
				autoClose);

	}

	@Override
	public Socket createSocket() throws IOException {

		return sslContext.getSocketFactory().createSocket();

	}
}
