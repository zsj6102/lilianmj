package org.soshow.beautyedu.json.core;

import java.util.Map;

import org.soshow.beautyedu.json.utils.HttpUtil;
import org.soshow.beautyedu.json.utils.LogUtils;

import android.os.AsyncTask;

/**
 * 
 * 网络请求类
 * 
 * @author wuxiaohong
 * @date 2015-7-14 下午4:29:09
 * 
 */
public class NetExcutor {

	/**
	 * 请求url
	 */
	private String mUrl;
	private boolean isMultipart=false;


	private enum RequestType {
		REQUEST_TYPE_GET, REQUEST_TYPE_POST,
	}

	/**
	 * 请求类型
	 */
	private RequestType mRequestType = RequestType.REQUEST_TYPE_GET;

	/**
	 * post请求时的参数
	 */
	private Map<String, String> mParams;

	/**
	 * 请求后的回调
	 */
	private IExcutorListener mExcutorListener;

	public void setUrl(String url) {
		this.mUrl = url;
	}

	public void setParams(Map<String, String> params) {
		this.mParams = params;
	}

	public void setParamsMultipart(Map<String, String> params) {
		isMultipart = true;
		this.mParams = params;
	}
	public void setExcutorListener(IExcutorListener listener) {
		this.mExcutorListener = listener;
	}

	public void get() {
		mRequestType = RequestType.REQUEST_TYPE_GET;
		new NetTask().execute();
	}

	public void post() {
		mRequestType = RequestType.REQUEST_TYPE_POST;
		new NetTask().execute();
	}

	/**
	 * 网络请求异步任务，android 8 以上系统会自己对异步任务做线程池处理
	 */
	private class NetTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			if (null == mExcutorListener) {
				return false;
			}
			try {
				String result = request();
				mExcutorListener.sendSuccess(result);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				mExcutorListener.sendError(e);
				return false;
			}finally {
				mExcutorListener.complete();
			}
		}
	}

	private String request() throws Exception {
		String result = null;
		switch (mRequestType) {
		case REQUEST_TYPE_GET:
			result = HttpUtil.getRequest(mUrl ,mParams);
			break;

		case REQUEST_TYPE_POST:{

			LogUtils.e("NetExcutor============1");
			result = HttpUtil.postRequest(mUrl,(Map) mParams, isMultipart);
			break;
		}

		default:
			break;
		}
		return result;
	}

	/**
	 * 网络请求核心基类回调
	 */
	public interface IExcutorListener {
		public void sendSuccess(String result);
		public void sendError(Throwable error);
		public void complete();
	}

}
