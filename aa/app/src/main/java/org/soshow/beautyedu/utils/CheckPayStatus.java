package org.soshow.beautyedu.utils;


import org.soshow.basketball.inf.CallBackResponse;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;

import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;


public class CheckPayStatus {

	private static CheckPayStatus manageApi = null;
	private Context context;

	private CheckPayStatus(Context context) {
		this.context = context;
	}

	public static CheckPayStatus getInstence(Context context) {
		if (manageApi == null) {
			synchronized (CheckPayStatus.class) {
				if (manageApi == null) {
					manageApi = new CheckPayStatus(context);
				}
			}
		}
		return manageApi;
	}

	/*
	 * 获取支付状态
	 */
	public void getStatus(String tocken, String app_nonce, String lecture_id,final CallBackResponse callBackResponse) {
		String url = Constant.GET_PAY_STATUS + "&tocken=" + tocken
				+ "&app_nonce=" + app_nonce + "&lecture_id=" + lecture_id;
		final ProgressDialog progressDialog = new ProgressDialog(
				context);
		progressDialog.show();
		NetHelper.get(url, new SimpleSingleBeanNetHandler<Captcha>(
				context) {

			@Override
			protected void onSuccess(Captcha bean) {
				progressDialog.hide();
				String result = bean.result;
				callBackResponse.result(result);
			}

		});
	}
	
	
}
