package org.soshow.beautyedu.json.handler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.json.bean.NetBaseBean;
import org.soshow.beautyedu.utils.Constant;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * 返回是多个的（List）网络请求Handler
 * 
 * @author wuxiaohong
 * @date 2015-7-15 上午11:22:12
 * 
 */
public abstract class MultiBeanNetHandler<T extends NetBaseBean> extends BaseBeanNetHandler<T> {

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			try {
				switch (msg.what) {
				case CODE_SUCCESS:
					onSuccess((List<T>) msg.obj);
					break;

				default:
					break;
				}
			} catch (Exception e) {
				Log.e("MultiBeanNetHandler", "空指针异常");
			}
			
		};
	};

	@Override
	protected void onReceivedRet(String ret) {
		Log.e("MultiBeanNetHandler", "返回的消息为:" + ret);
		try {

			JSONArray ja = new JSONArray(ret);
			List<T> list = new ArrayList<T>();
			for (int i = 0; i < ja.length(); i++) {
				JSONObject object = ja.getJSONObject(i);
				T t = parseItem(object);
				list.add(t);
			}
			handleSuccess(CODE_SUCCESS, list);
		}

		catch (JSONException e) {
			try {

				JSONObject jb = new JSONObject(ret);
				Constant.total=jb.optString("total");
				JSONArray array = jb.optJSONArray("info");
//				Log.e("MultiBeanNetHandler", "info解析到的数组数据："+array.toString());
				List<T> list = new ArrayList<T>();
				for (int i = 0; i < array.length(); i++) {
					//Log.d("345abc", "开始分解");

					JSONObject object = array.getJSONObject(i);
				//	Log.d("1221", "解析的jsonObject为"+object);
					T t = parseItem(object);
					Log.d("MultiBeanNetHandler", "解析的对象为："+toString().toString());
					list.add(t);

				}
				handleSuccess(CODE_SUCCESS, list);
			//	Log.d("345abc", "ja解析无错误");
			} catch (Exception e2) {
				// TODO: handle exception
				e.printStackTrace();
				//Log.d("345abc", "ja解析错误" + e2.toString());
				// handleError(CODE_ERROR_JSON, e.getMessage());
				//Log.d("345abc", "MutilBean错误消息=               " + ret);
				handleError(CODE_ERROR_JSON, ret);

			}
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
	 * 子类实现这个方法来得到网络请求返回的List
	 * 
	 * @param list
	 */
	abstract protected void onSuccess(List<T> list);

}
