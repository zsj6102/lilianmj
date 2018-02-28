package org.soshow.beautyedu.json.core;

import java.util.Map;

import org.soshow.beautyedu.json.core.NetExcutor.IExcutorListener;

/**
 * 
 * 网络请求帮助类
 * 
 * @author wuxiaohong
 * @date 2015-7-14 下午4:55:17
 * 
 */
public class NetHelper {

	/**
	 * 
	 * get方法请求数据
	 * 
	 * @param url
	 * @param listener
	 */
	public synchronized static void get(String url, IExcutorListener listener) {
		NetExcutor excutor = new NetExcutor();
		excutor.setUrl(url);
		excutor.setExcutorListener(listener);
		excutor.get();
	}
	//TODO
	public synchronized static void get(String url,Map<String, String> params, IExcutorListener listener) {
		NetExcutor excutor = new NetExcutor();
		excutor.setUrl(url);
		excutor.setParams(params);
		excutor.setExcutorListener(listener);
		excutor.get();
	}

	/**
	 * 
	 * post方法请求数据
	 * 
	 * @param url
	 * @param params
	 * @param listener
	 */
	public static void post(String url, Map<String, String> params, IExcutorListener listener) {
		NetExcutor excutor = new NetExcutor();
		excutor.setUrl(url);
		excutor.setParams(params);
		excutor.setExcutorListener(listener);
		excutor.post();
	}

	/**
	 * post 附件上传
	 */
	public static void postUpload(String url, Map<String, String> params, IExcutorListener listener) {
		NetExcutor excutor = new NetExcutor();
		excutor.setUrl(url);
		excutor.setParamsMultipart(params);
		excutor.setExcutorListener(listener);
		excutor.post();
	}

}
