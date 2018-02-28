package org.soshow.beautyedu.json.utils;

import org.soshow.beautyedu.json.handler.BaseNetHandler;

import android.content.Context;
import android.util.Log;

/**
 * 
 * 网络请求后的 Toast 工具类
 * 
 * @author wuxiaohong
 * @date 2015-7-16 下午5:50:58
 * 
 */
public class NetToastUtil {

	/**
	 * 网络请求出错后的处理
	 * 
	 * @param context
	 * @param errorCode
	 * @param errorMsg
	 */
	public static void errorToast(Context context, int errorCode, String errorMsg) {
		Log.e("", "--------------------errorCode="+errorCode+"\nerrorMsg="+errorMsg);
		switch (errorCode) {
		case BaseNetHandler.CODE_ERROR_NET:
			netError(context);
			break;

		case BaseNetHandler.CODE_ERROR_UNKNOW:
			unknown(context, errorMsg);
			break;

		case BaseNetHandler.CODE_ERROR_JSON:
			jsonError(context);
			break;

		default:
			unknown(context);
			break;
		}
	}

	public static void netError(Context context) {
		if (NetUtil.isConnected(context)) {
			serverError(context);
		} else {
			noOpenNet(context);
		}
	}

	public static void serverError(Context context) {
		ToastUtil.longPrompt(context, "连接服务器失败！");
	}

	public static void noOpenNet(Context context) {
		ToastUtil.longPrompt(context, "网络未打开！");
	}

	public static void unknown(Context context, String retId) {
		ToastUtil.prompt(context, "未知错误：" + retId);
	}

	public static void unknown(Context context) {
		ToastUtil.prompt(context, "未知错误");
	}

	public static void jsonError(Context context) {
		ToastUtil.prompt(context, "数据解析出错");
	}

}
