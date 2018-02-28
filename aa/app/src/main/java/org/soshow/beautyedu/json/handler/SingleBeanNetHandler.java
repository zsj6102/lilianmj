package org.soshow.beautyedu.json.handler;

import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.json.bean.NetBaseBean;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * 返回是单个的（单个Bean）网络请求Handler
 * 
 * @author wuxiaohong
 * @date 2015-7-15 上午11:22:12
 * 
 */
public abstract class SingleBeanNetHandler<T extends NetBaseBean> extends BaseBeanNetHandler<T> {

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_SUCCESS:
				onSuccess((T) msg.obj);
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onReceivedRet(String ret) {
//		Log.e("SingleBeanNetHandler", "返回的消息为:" + ret);
		try {
			JSONObject object = new JSONObject(ret);
			T t = parseItem(object);
			handleSuccess(CODE_SUCCESS, t);
		} catch (JSONException e) {
			e.printStackTrace();
			handleError(CODE_ERROR_JSON, e.getMessage());
		}
	}

	@Override
	protected void handleSuccess(int code, Object object) {
		Message msg = new Message();
		msg.what = code;
		msg.obj = object;
		mHandler.sendMessage(msg);
	}

	/**
	 * 子类实现这个方法来得到网络请求返回的单个Bean
	 */
	abstract protected void onSuccess(T bean);

}
