package org.soshow.beautyedu.login.sina;

import java.text.SimpleDateFormat;

import org.soshow.beautyedu.login.tools.LOG;
import org.soshow.beautyedu.login.tools.PreferenceUtil;
import org.soshow.beautyedu.login.tools.WeiboListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;


public class SinaWeiboUtil {

	private static final String TAG = "SinaWeiboUtil";

	private static Context mContext;

	private static SinaWeiboUtil mInstantce;

	// private static Weibo mWeibo;


	private WeiboListener listener;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if (listener != null) {
					listener.onResult();
				}
				break;

			default:
				break;
			}
		}

	};



	public static SinaWeiboUtil getInstance(Context context) {
		mContext = context;
		if (mInstantce == null) {
			mInstantce = new SinaWeiboUtil();
		}
		return mInstantce;
	}

	/**
	 * 初始化新浪微博
	 * 
	 * @param l
	 *            授权是否过期回调函数
	 */
	public void initSinaWeibo(WeiboListener l) {
		String token = PreferenceUtil.getInstance(mContext).getString(Sinas.PREF_SINA_ACCESS_TOKEN, "");
		// Log.d("123321", "initSinaWeibo调用新浪微博token=========================="+
		// token);
		long expiresTime = PreferenceUtil.getInstance(mContext).getLong(Sinas.PREF_SINA_EXPIRES_TIME, 0);
		String uid = PreferenceUtil.getInstance(mContext).getString(Sinas.PREF_SINA_UID, "");
		String userName = PreferenceUtil.getInstance(mContext).getString(Sinas.PREF_SINA_USER_NAME, "");
		String remindIn = PreferenceUtil.getInstance(mContext).getString(Sinas.PREF_SINA_REMIND_IN, "");


	}

	/**
	 * SSO授权
	 * 
	 * @param l
	 */

	/**
	 * 授权回调函数
	 *
	 * @author
	 */


	/**
	 * 上传图片并发布一条新微博，此方法会处理urlencode
	 * 
	 * @param content
	 *            要发布的微博文本内容，内容不超过140个汉字
	 * @param file
	 *            要上传的图片，仅支持JPEG、GIF、PNG格式，图片大小小于5M。
	 * @param lat
	 *            纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
	 * @param lon
	 *            经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。
	 */
	/*
	 * public void upload(String content, String file, String lat, String lon) {
	 * // sharedPreferences = mContext.getSharedPreferences("sharpandroid", //
	 * Context.MODE_WORLD_WRITEABLE); //
	 * System.out.println(sharedPreferences.getString("token_Sina", "")); //
	 * mAccessToken = new Oauth2AccessToken(); //
	 * mAccessToken.setToken(sharedPreferences.getString("tokem_Sina", ""));
	 * System.out.println("怎么得到的：" + mAccessToken); SinaWeiboAPI api = new
	 * SinaWeiboAPI(mAccessToken); api.upload(content, file, lat, lon, new
	 * RequestListener() {
	 * 
	 * @Override public void onIOException(final IOException e) { LOG.cstdr(TAG,
	 * "onIOException---e = " + e.getMessage());
	 * LoginInputActivity.getHandler().post(new Runnable() {
	 * 
	 * @Override public void run() { LOG.showToast(mContext,
	 * "分享失败，请检查网络连接。出错信息：8" + e.getMessage());
	 * 
	 * } }); }
	 * 
	 * @Override public void onError(final WeiboException e) { LOG.cstdr(TAG,
	 * "onError---e = " + e.getMessage() + " e.getStatusCode() = " +
	 * e.getStatusCode()); if (e.getStatusCode() == 400) { //
	 * 相同内容短时间内不能分享，判断还以促出错信息为准 LoginInputActivity.getHandler().post(new
	 * Runnable() {
	 * 
	 * @Override public void run() { LOG.showToast(mContext,
	 * "分享失败，相同内容短时间内不能分享，请稍候再试吧。出错信息：9" + e.getMessage()); } }); } else {
	 * LoginInputActivity.getHandler().post(new Runnable() {
	 * 
	 * @Override public void run() { LOG.showToast(mContext,
	 * "分享失败，请检查网络连接。出错信息：10" + e.getMessage()); } }); } }
	 * 
	 * @Override public void onComplete(String str) { LOG.cstdr(TAG,
	 * "onComplete---str = " + str); LoginInputActivity.getHandler().post(new
	 * Runnable() {
	 * 
	 * @Override public void run() { LOG.showToast(mContext,
	 * "分享成功，去你绑定的新浪微博看看吧！11"); } }); } }); }
	 */
	/**
	 * 注销授权
	 * 
	 * @param l
	 */

	/*
	 * public void logout(WeiboListener l) { SinaWeiboAPI api = new
	 * SinaWeiboAPI(mAccessToken); api.endSession(new RequestListener() {
	 * 
	 * @Override public void onIOException(IOException arg0) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onError(WeiboException arg0) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void onComplete(String arg0) { // TODO Auto-generated
	 * method stub
	 * 
	 * } });
	 * PreferenceUtil.getInstance(mContext).remove(Sinas.PREF_SINA_ACCESS_TOKEN)
	 * ; PreferenceUtil.getInstance(mContext).remove("token_Sina");
	 * PreferenceUtil.getInstance(mContext).remove("uid_Sina"); l.onResult(); }
	 */
	/**
	 * 检查是否已授权
	 * 
	 * @return true 已授权，false 未授权
	 */
	public boolean isAuth() {
		String token = PreferenceUtil.getInstance(mContext).getString(Sinas.PREF_SINA_ACCESS_TOKEN, "");
		if (TextUtils.isEmpty(token)) {
			return false;
		}
		return true;
	}
}
