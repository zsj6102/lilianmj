package org.soshow.beautyedu.json.handler;

import org.soshow.beautyedu.json.core.NetExcutor.IExcutorListener;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 公用网络请求逻辑
 */
public abstract class BaseNetHandler implements IExcutorListener {

	/**
	 * 网络请求数据成功并解析成功
	 */
	public final static int CODE_SUCCESS = 0;

	/**
	 * 网络请求失败
	 */
	public final static int CODE_ERROR_NET = -1;

	/**
	 * 服务器返回非成功数据，返回的id非00001
	 */
	public final static int CODE_ERROR_UNKNOW = -2;

	/**
	 * 解析json出错
	 */
	public final static int CODE_ERROR_JSON = -3;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CODE_ERROR_NET:
			case CODE_ERROR_UNKNOW:
			case CODE_ERROR_JSON:
				//Log.d("345abc", "onError调用" +msg.obj);
				onError(msg.what, (String) msg.obj);
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void sendSuccess(String result) {
		try {
		if (result != null) {
			
			//Log.d("234abc", "result=" + result);
			onReceivedRet(result);
				/*Log.d("123321", "返回成功");
				JSONObject jsonObject = new JSONObject(result);
				String id = jsonObject.optString("result");
				
				if (id.equals("1")) {
					if (jsonObject.optString("ret").equals("")) {
						onReceivedRet(result);
					} else {
						String ret = jsonObject.optString("ret");
						onReceivedRet(ret);
					}

				} else {
					handleError(CODE_ERROR_UNKNOW, id);
					Log.d("abcde", "dafefa");
				}*/

			} else {
				// HttpUtil请求后返回是空的，是因为返回码不为200
				handleError(CODE_ERROR_NET, "result is null");
				//Log.d("abcde", "dafefa");
			}
		} catch (Exception e) {
			e.printStackTrace();
			//Log.d("abcde", "dafefa");
			handleError(CODE_ERROR_JSON, e.getMessage());
		}
	}

	@Override
	public void sendError(Throwable error) {
		// HttpUtil请求中途异常了
		handleError(CODE_ERROR_NET, error.getMessage());
	}

	protected void handleError(int code, String errorMsg) {
		//Log.d("345abc", "BaseNet错误消息=               " + errorMsg);
		Message msg = new Message();
		msg.what = code;
		msg.obj = errorMsg;
		mHandler.sendMessage(msg);
	}

	abstract protected void onError(int errorCode, String errorMsg);

	/**
	 * 根据业务区分ret解析成list或者单个实体，或者解析成其它结果
	 */
	abstract protected void onReceivedRet(String ret);

	/**
	 * 在onReceivedRet中用来通知ui线程已经返回数据了
	 */
	abstract protected void handleSuccess(int code, Object object);
	public void complete(){
	};

}
