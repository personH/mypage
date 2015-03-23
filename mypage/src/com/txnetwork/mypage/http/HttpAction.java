package com.txnetwork.mypage.http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.MIME;
import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

/**
 * Http请求及响应操作，主要是基于Http数据包协议来进行处理的，即http的包头、包体、安全选项、协议、IP\端口进行设置。
 * HttpAction.java
 * 
 * @author zhangrui 2013-4-2
 */
public class HttpAction
{
    private static String TAG = "HttpAction";

    private int requestType;

    /**
     * Http请求GET类型
     */
    public static final int REQUEST_TYPE_GET = 0;
    /**
     * Http请求POST类型
     */
    public static final int REQUEST_TYPE_POST = 1;

    private HttpRequestBase requestBase;
    private HttpObserver httpObserver;

    private Header[] receiveHeaders;
    private Object receiveBody;

    private UrlEncodedFormEntity requestContent;

    private int errorCode;

    private HttpAction()
    {
//        requestContent = new MultipartEntity();
    }

    /**
     * Http构造方法
     * 
     * @param requestType
     *            请求类型，可以是REQUEST_TYPE_GET或REQUEST_TYPE_POST
     */
    public HttpAction(int requestType)
    {
        this();
        this.requestType = requestType;

        if (requestType == REQUEST_TYPE_POST)
        {
            requestBase = new HttpPost();
        }
        else
        {
            requestBase = new HttpGet();
        }
    }

    public int getErrorCode()
    {
        return errorCode;
    }

    /**
     * 获取url
     * 
     * @return 请求事件的URL地址
     */
    public String getUrl()
    {
        return requestBase.getURI().toString();
    }

    /**
     * 获取响应头
     * 
     * @return 响应头
     */
    public Header[] getReceiveHeaders()
    {
        return receiveHeaders;
    }

    /**
     * 获取响应体
     * 
     * @return 响应体
     */
    public Object getReceiveBody()
    {
        return receiveBody;
    }

    /**
     * 设置请求URL地址
     * 
     * @param url
     *            URL地址
     */
    public void setUri(String url)
    {
        requestBase.setURI(URI.create(url));
    }

    /**
     * 设置请求头
     * 
     * @param httpObserver
     */
    public void setHttpObserver(HttpObserver httpObserver)
    {
        this.httpObserver = httpObserver;
    }

    /**
     * 添加请求头参数
     * 
     * @param name
     *            参数名
     * @param value
     *            参数值
     */
    public void addRequestHeader(String name, String value)
    {
        requestBase.addHeader(name,value);
    }

    /**
     * 添加请求体参数
     *
     */
    public void addBodyParam(List<NameValuePair> formparams)
    {
        try
        {
//            Log.e("addBodyParam",
//                paramName + "=" + paramValue);

//            requestContent.addPart(paramName,
//                new StringBody(paramValue,
//                    Charset.forName(HTTP.UTF_8)));
        	requestContent = new UrlEncodedFormEntity(formparams,HTTP.UTF_8);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 添加请求体文件
     * 
     * @param paramName
     *            参数名
     * @param data
     *            文件数据
     * @param mimeType
     *            文件格式类型
     * @param fileName
     *            文件名
     */
    public void addBodyFile(String paramName, byte[] data, String mimeType, String fileName)
    {
        try
        {
//            requestContent.addPart(paramName,
//                new ByteArrayBody(data,
//                    mimeType,
//                    fileName));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void connect(DefaultHttpClient defaultHttpClient) throws Exception
    {

        if (requestType == REQUEST_TYPE_POST && requestContent.getContentLength() > 0)
        {
            ((HttpPost) requestBase).setEntity(requestContent);
        }

        HttpResponse response = defaultHttpClient.execute(requestBase);
        int resultCode = response.getStatusLine().getStatusCode();
        receiveBody = getBody(response);
        receiveHeaders = response.getAllHeaders();
        if (resultCode != 200)
        {
            // requestBase.abort();
            // LogUtil.print(TAG,
            // "the error code is :"+String.valueOf(resultCode));
            errorCode = resultCode;
            if (receiveBody != null)
            {
                String errorLog = new String((byte[]) receiveBody);
                // LogUtil.print(TAG, errorLog);
                try
                {
                    JSONObject errorLogJson = new JSONObject(errorLog);
                    // LogUtil.print(TAG, errorLogJson.toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            throw new IOException();
        }

        if (httpObserver != null)
        {
            httpObserver.httpResultOK(this);
        }
    }

    /**
     * 请求的执行方法
     * 
     * @param defaultHttpClient
     *            Http处理对象
     */
    public void execute(DefaultHttpClient defaultHttpClient)
    {
        try
        {
            connect(defaultHttpClient);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            if (httpObserver != null)
            {
                httpObserver.httpResultError(this);
            }
        }
        finally
        {
            defaultHttpClient.clearResponseInterceptors();
        }
    }

    /**
     * 获取响应体方法
     * 
     * @param response
     *            Http响应
     * @return 响应体数据
     * @throws java.io.IOException
     */
    protected Object getBody(HttpResponse response) throws IOException
    {
        byte[] data = null;
        InputStream inputStream = response.getEntity().getContent();
        if (inputStream != null)
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try
            {
                byte[] tmpData = new byte[1024];
                int length = 0;
                while ((length = inputStream.read(tmpData)) > -1)
                {
                    bos.write(tmpData,
                        0,
                        length);
                }
                data = bos.toByteArray();
            }
            catch (IOException ioException)
            {
                throw ioException;
            }
            finally
            {
                if (bos != null)
                {
                    bos.close();
                    bos = null;
                }
                if (inputStream != null)
                {
                    inputStream.close();
                    inputStream = null;
                }
            }
        }
        return data;
    }
    
    private class ByteArrayBody extends AbstractContentBody {
		private final byte[] bytes;
		private final String fileName;

		public ByteArrayBody(byte[] bytes, String mimeType, String fileName) {
			super(mimeType);
			this.bytes = bytes;
			this.fileName = fileName;
		}

		public String getFilename() {
			return fileName;
		}

		public void writeTo(OutputStream out) throws IOException {
			out.write(bytes);
		}

		public String getCharset() {
			return null;
		}

		public long getContentLength() {
			return bytes.length;
		}

		public String getTransferEncoding() {
			return MIME.ENC_BINARY;
		}

	}
  
}
