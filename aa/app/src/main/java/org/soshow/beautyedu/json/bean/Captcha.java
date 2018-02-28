package org.soshow.beautyedu.json.bean;

import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.utils.ToastUtil;

import android.util.Log;

/**
 */
public class Captcha extends NetBaseBean { //TODOJ rename  JsonResult

	public String result;
	public String info;
	public String message="";

	/**更友好的错误*/
	public String getMessage_(){
		if(Result_ERROR.equals(getResult())){
			return Result_ERROR_MSG;
		}
		return getMessage();
	}
	public static final String Result_Success = "0";
	public static final String Result_Fail = "99";
	/** 未知异常, 本地提示 "出错了"*/
	public static final String Result_ERROR = "ERROR";
	public static final String Result_NOTLOGIN = "NOTLOGIN";

	public static final String Result_ERROR_MSG = "出错了,请稍后重试";
	public boolean isSuccess(){
		return Result_Success.equals(getResult());
	}
	@Override
	public void initByJson(JSONObject jsonObject) throws JSONException {
		this.result = jsonObject.optString("result");
		this.info = jsonObject.optString("info");
		this.message = jsonObject.optString("message");

	}


	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
