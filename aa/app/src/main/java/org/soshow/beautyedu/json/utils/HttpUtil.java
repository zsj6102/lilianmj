package org.soshow.beautyedu.json.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;



import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.httpclient.PreferencesCookieStore;

import android.app.Application;
import android.graphics.drawable.Drawable;

import static android.R.attr.port;

/**
 *
 * Http请求工具类
 *
 * @author wuxiaohong
 * @date 2015-7-16 下午5:44:55
 *
 */
public class HttpUtil {

	private static HttpClient httpClient;
	private static final int DEFAULT_CONNMANAGER_TIMEOUT = 20000;
	private static final int DEFAULT_HOST_CONNECTIONS = 30;
	private static final int DEFAULT_MAX_CONNECTIONS = 30;
	private static final int DEFAULT_SOCKET_TIMEOUT = 20000;
	//private static final String SCHEME_NAME = "http";

	private HttpUtil() {

	}

	public static synchronized HttpClient getHttpClient() {
		if (null == httpClient) {
			final HttpParams httpParams = new BasicHttpParams();
			ConnManagerParams.setTimeout(httpParams, DEFAULT_CONNMANAGER_TIMEOUT);
			HttpConnectionParams.setConnectionTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, DEFAULT_SOCKET_TIMEOUT);

			ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(DEFAULT_HOST_CONNECTIONS));
			ConnManagerParams.setMaxTotalConnections(httpParams, DEFAULT_MAX_CONNECTIONS);
			//	SchemeRegistry schemeRegistry = new SchemeRegistry();
			//	schemeRegistry.register(new Scheme(SCHEME_NAME, PlainSocketFactory.getSocketFactory(), 80));
			//	ClientConnectionManager manager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
			//	httpClient = new DefaultHttpClient(manager, httpParams);
			httpClient = new DefaultHttpClient(httpParams);
			PreferencesCookieStore cookieStore = new PreferencesCookieStore(MyApplication.applicationContext);
			((DefaultHttpClient) httpClient).setCookieStore(cookieStore);

//			if (!StringUtil.isEmpty(Constant.PROXY_HOST)) {
//				HttpHost httpHost = new HttpHost(Constant.PROXY_HOST, Constant.PROXY_PORT);
//				//设置代理
//				httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, httpHost);
//			}
		}
		return httpClient;
	}

	public static String getRequest(String url) throws Exception {
		return getRequest(url, null);
	}
	public static String getRequest(String url, Map<String, String> params) throws Exception {
		StringBuilder sb = new StringBuilder(url);
		if (params != null && !params.isEmpty()) {
			String paramstr = "";
			List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
			for (String key : params.keySet()) {
				pairs.add(new BasicNameValuePair(key, params.get(key)));
			}
			paramstr= EntityUtils.toString(new UrlEncodedFormEntity(pairs, "utf-8"));
			if(url.indexOf("?")==-1){
				sb.append("?");
			}
			sb.append(paramstr);
		}
		HttpGet get = new HttpGet(sb.toString());
		HttpClient httpClient = getHttpClient();
		HttpResponse response = httpClient.execute(get);
		int code = response.getStatusLine().getStatusCode();
		LogUtils.e("responseCode="+code);
		if (200 == response.getStatusLine().getStatusCode()) {
			String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
			LogUtils.e("请求返回的数据==="+result);
			return result;
		}else{
			LogUtils.e("网络请求返数据  err："+response.getStatusLine().getStatusCode());
		}
		return null;
	}

	/**
	 * post
	 */
	public static String postRequest(String url, Map<String, Object> rawParams) throws Exception {
		return postRequest(url,rawParams,false);
	}

	/**
	 * post
	 */
	public static String postRequest(String url, Map<String, Object> rawParams,boolean isMultipart) throws Exception {
		HttpPost post = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		if(!isMultipart){
			for (String key : rawParams.keySet()) {
				params.add(new BasicNameValuePair(key, (String)rawParams.get(key)));
			}
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		}else{
			settMultipartParam(post, rawParams);
		}
		HttpClient httpClient = getHttpClient();
		HttpResponse response = httpClient.execute(post);
		LogUtils.e("网络请求返回码："+response.getStatusLine().getStatusCode());
		if (200 == response.getStatusLine().getStatusCode()) {
			String result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
			LogUtils.e("网络请求返数据："+result);
			return result;
		}
		else {
			LogUtils.e("网络请求返数据  err："+response.getStatusLine().getStatusCode());
			return null;
		}
	}

	protected static void settMultipartParam(HttpPost post, Map<String, Object> params) {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();

		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);// 设置游览器兼容模式
        builder.setCharset(Charset.forName(HTTP.UTF_8));
        for (String k : params.keySet()) {
			Object v = params.get(k);
			if (v == null) { continue; }
			if (v instanceof File) { //file byte stream
				builder.addBinaryBody(k, (File) v);
			} else {
				//中文乱码
				ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
				StringBody stringBody = new StringBody((String)v,contentType);
				builder.addPart(k, stringBody);
//				builder.addTextBody(k, (String) v);
			}
		}
		post.setEntity(builder.build());
	}

	/**
	 * 获取图片
	 *
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static Drawable getDrawable(final String url) {
		try {
			HttpGet get = new HttpGet(url);
			HttpClient httpClient = getHttpClient();
			HttpResponse response = httpClient.execute(get);
			if (200 == response.getStatusLine().getStatusCode()) {
				InputStream is = response.getEntity().getContent();
				return Drawable.createFromStream(is, url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
