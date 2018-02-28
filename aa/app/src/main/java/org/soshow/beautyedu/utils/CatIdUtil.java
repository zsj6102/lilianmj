package org.soshow.beautyedu.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.soshow.beautyedu.activity.CoursesListActivity;
import org.soshow.beautyedu.activity.MainTabActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.Cat;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleMultiBeanNetHandler;

import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class CatIdUtil {

	public static void getCatID(final Context context) {
		Log.e("123321", "cat获取地址为             " + Constant.phpUrl + "/wap/api.php?action=GET_CAT");
		final String app_nonce = StringUtil.getPhoneIMEI(context);
		final String mToken = new TokenManager(context).getToken();
		// getInfo()
		if (mToken != null) {
			NetHelper.get(Constant.phpUrl + "/wap/api.php?action=GET_CAT", new SimpleMultiBeanNetHandler<Cat>(context) {

				@Override
				protected void onSuccess(List<Cat> list1) {
					LinkedHashMap<String, String> first = new LinkedHashMap<String, String>();

					for (final Cat s1 : list1) {

						MyApplication.map_catID.put(s1.exam_cat_id, s1.exam_cat_name);
						first.put(s1.exam_cat_name, s1.exam_cat_id);
						// Log.d("345abc", "id=" + s1.exam_cat_id + " name=" +
						// s1.exam_cat_name);
						Log.e("", "url========="+Constant.phpUrl + "/wap/api.php?action=GET_SUB_CAT" + "&app_nonce=" + app_nonce
								+ "&tocken=" + mToken + "&id=" + s1.exam_cat_id);
						NetHelper.get(
								Constant.phpUrl + "/wap/api.php?action=GET_SUB_CAT" + "&app_nonce=" + app_nonce
										+ "&tocken=" + mToken + "&id=" + s1.exam_cat_id,
								new SimpleMultiBeanNetHandler<Cat>(context) {

							@Override
							protected void onSuccess(List<Cat> list2) {
								LinkedHashMap<String, String> second = new LinkedHashMap<String, String>();
								
								for (final Cat s2 : list2) {
									second.put(s2.exam_cat_id, s2.exam_cat_name);
									Log.e("", "url2======="+Constant.phpUrl + "/wap/api.php?action=GET_SUB_CAT" + 
									"&app_nonce="+ app_nonce + "&tocken=" + mToken + "&id=" + s2.exam_cat_id);
									NetHelper.get(
											Constant.phpUrl + "/wap/api.php?action=GET_SUB_CAT" + "&app_nonce="
													+ app_nonce + "&tocken=" + mToken + "&id=" + s2.exam_cat_id,
											new SimpleMultiBeanNetHandler<Cat>(context) {

										@Override
										protected void onSuccess(List<Cat> list3) {
											LinkedHashMap<String, String> third = new LinkedHashMap<String, String>();
											for (final Cat s3 : list3) {
												third.put(s3.exam_cat_id, s3.exam_cat_name);
											}
											MyApplication.catID.put(s2.exam_cat_id, third);
										}

										@Override
										protected void onError(int errorCode, String errorMsg) {
										}
									});
								}
								// Log.d("123456", "second=" +
								// second.toString());
								MyApplication.catID.put(s1.exam_cat_id, second);
							}

							@Override
							protected void onError(int errorCode, String errorMsg) {
							}

						});

					}
					// Log.d("123456", "first=" + first.toString());
					MyApplication.catID.put("first", first);
					// Toast.makeText(context, "成功", Toast.LENGTH_SHORT).show();
					try {
						Activity activity = (Activity) context;
						if (activity instanceof MainTabActivity && MainTabActivity.mta_instance != null) {
							new Timer().schedule(new TimerTask() {
								@Override
								public void run() {
									// 需要做的事:发送消息
									Log.d("345abc", "Catid发送消息");
									Message msg_loop = MainTabActivity.mta_instance.getCatHandler().obtainMessage();
									msg_loop.what = 101;
									msg_loop.sendToTarget();
								}
							}, 1000);
						} else if (activity instanceof CoursesListActivity && CoursesListActivity.context != null) {
							new Timer().schedule(new TimerTask() {
								@Override
								public void run() {
									// 需要做的事:发送消息
									Log.d("345abc", "Catid发送消息");
									Message msg_loop = CoursesListActivity.context.getHandler().obtainMessage();
									msg_loop.what = 1;
									msg_loop.sendToTarget();
								}
							}, 1000);

						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					// Log.d("123456", "1级caiid错误=" + errorMsg);
					try {
						Activity activity = (Activity) context;
						if (activity instanceof MainTabActivity && MainTabActivity.mta_instance != null) {
							super.onError(errorCode, errorMsg);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					// super.onError(errorCode, errorMsg);
				}

			});
		} else {
			return;
		}
	}
}
