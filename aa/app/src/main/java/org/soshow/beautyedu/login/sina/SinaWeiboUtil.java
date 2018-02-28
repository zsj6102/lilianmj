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

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

public class SinaWeiboUtil {

	private static final String TAG = "SinaWeiboUtil";

	private static Context mContext;

	private static SinaWeiboUtil mInstantce;

	// private static Weibo mWeibo;
	private static AuthInfo mAuthInfo;
	/** 保存token等参数 **/
	private static Oauth2AccessToken mAccessToken;

	/** 调用SSO授权 **/
	private static SsoHandler mSsoHandler;

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

	public SinaWeiboUtil() {
		mAuthInfo = new AuthInfo(mContext, Sinas.CONSUMER_KEY, Sinas.REDIRECT_URL, Sinas.SINA_SCOPE);
		// mWeibo = Weibo.getInstance(Sinas.CONSUMER_KEY, Sinas.REDIRECT_URL);

		// Log.d("123321", "SinaWeiboUtil调用"+ mAuthInfo.toString());
	}

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
		mAccessToken = new Oauth2AccessToken();
		mAccessToken.setToken(token);
		mAccessToken.setExpiresTime(expiresTime);
		LOG.cstdr(TAG, "accessToken = " + mAccessToken);
		LOG.cstdr(TAG, "accessToken.getToken() = " + mAccessToken.getToken());
		LOG.cstdr(TAG, "accessToken.getExpiresTime() = " + mAccessToken.getExpiresTime());
		LOG.cstdr(TAG, "uid = " + uid);
		LOG.cstdr(TAG, "userName = " + userName);
		LOG.cstdr(TAG, "remindIn = " + remindIn);

		if (mAccessToken.isSessionValid()) { // TODO 判断是否已授权
			String date = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
					.format(new java.util.Date(mAccessToken.getExpiresTime()));
			LOG.cstdr(TAG, "access_token 仍在有效期内,无需再次登录: \naccess_token:" + mAccessToken.getToken() + "\n有效期：" + date
					+ "\nuid:" + uid + "\nuserName:" + userName + "\nremindIn:" + remindIn);
			l.init(true);
		} else {
			LOG.cstdr(TAG, "使用SSO登录前，请检查手机上是否已经安装新浪微博客户端，" + "目前仅3.0.0及以上微博客户端版本支持SSO；如果未安装，将自动转为Oauth2.0进行认证");
			l.init(false);
		}
	}

	/**
	 * SSO授权
	 * 
	 * @param l
	 */
	public void auth(WeiboListener l) {
		Log.d("123321", "微博授权调用");
		// SSO授权
		mSsoHandler = new SsoHandler((Activity) mContext, mAuthInfo);
		mSsoHandler.authorize(new AuthDialogListener());
		// Toast.makeText(mContext, "开始授权", 2000).show();
		listener = l;

		// test 网页授权
		// mWeibo.authorize(mContext, new AuthDialogListener());
	}

	/**
	 * 授权回调函数
	 *
	 * @author
	 */
	class AuthDialogListener implements WeiboAuthListener {

		@Override
		public void onCancel() {
			LOG.cstdr(TAG, "===================AuthDialogListener=Auth cancel==========");
			// Util.showToast(mContext, "取消授权操作。");
			// Toast.makeText(mContext, "取消授权操作", 2000).show();
		}

		@Override
		public void onComplete(Bundle values) {
			 Toast.makeText(mContext, "新浪微博授权成功", Toast.LENGTH_SHORT).show();
			LOG.cstdr(TAG, "===================AuthDialogListener=onComplete==========");
			for (String key : values.keySet()) {
				LOG.cstdr(TAG, "values:key = " + key + " value = " + values.getString(key));
			}
			String token = values.getString(Sinas.SINA_ACCESS_TOKEN);
			String uid = values.getString(Sinas.SINA_UID);
			String userName = values.getString(Sinas.SINA_USER_NAME);
			String expiresIn = values.getString(Sinas.SINA_EXPIRES_IN); // expiresIn
																		// 是授权时长，因为要初始化，所以为String类型
			String remindIn = values.getString(Sinas.SINA_REMIND_IN);

			mAccessToken = new Oauth2AccessToken(token, expiresIn);
			if (mAccessToken.isSessionValid()) {
				PreferenceUtil.getInstance(mContext).saveString(Sinas.PREF_SINA_ACCESS_TOKEN, token);
				PreferenceUtil.getInstance(mContext).saveString(Sinas.PREF_SINA_UID, uid);
				PreferenceUtil.getInstance(mContext).saveLong(Sinas.PREF_SINA_EXPIRES_TIME,
						mAccessToken.getExpiresTime()); // 存入的是到期时间
				PreferenceUtil.getInstance(mContext).saveString(Sinas.PREF_SINA_REMIND_IN, remindIn);
				// if (TextUtils.isEmpty(userName)) {
				// show(Long.parseLong(uid));
				// } else {
				PreferenceUtil.getInstance(mContext).saveString(Sinas.PREF_SINA_USER_NAME, userName);
				if (listener != null) {
					listener.onResult();
				}
				// }
				LOG.cstdr(TAG, "isSessionValid~~~~~~~token = " + token + " uid = " + uid + " userName = " + userName
						+ " expiresIn = " + expiresIn + " remindIn = " + remindIn);

			}
			CookieManager cookieManager = CookieManager.getInstance();
			cookieManager.removeAllCookie();
		}

		@Override
		public void onWeiboException(WeiboException e) {
			LOG.cstdr(TAG, "===================AuthDialogListener=onWeiboException=WeiboException = " + e.getMessage());
			// Toast.makeText(mContext, "分享失败，请检查网络连接。出错信息：" +
			// e.getMessage(),Toast.LENGTH_SHORT).show();;
			if (listener != null) {
				// listener.onResult();
			}
			// if(e.toString().contains("Failed to receive access token")){
			// handler.sendEmptyMessageDelayed(1, 100);
			//
			// }
			// Toast.makeText(mContext, "分享失败，请检查网络连接。出错信息1", 2000).show();
			Log.d("123321", "分享失败，请检查网络连接。出错信息1" + e.toString());
		}
	}

	/**
	 * SSO授权回调函数
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void authCallBack(int requestCode, int resultCode, Intent data) {
		if (mSsoHandler != null) {
			LOG.cstdr(TAG,
					"=====onActivityResult=mSsoHandler resultCode = " + resultCode + " requestCode = " + requestCode);
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
			// Toast.makeText(mContext, "授权回调", 2000).show();
		}
	}

	/**
	 * 网页授权时，需要单独获取UserName
	 * 
	 * @param uid
	 */
	/*
	 * public void show(long uid) { Log.d("123321", "show"); SinaWeiboAPI api =
	 * new SinaWeiboAPI(mAccessToken); api.show(uid, new RequestListener() {
	 * 
	 * @Override public void onIOException(IOException e) { LOG.cstdr(TAG,
	 * "onIOException---e = " + e.getMessage()); // Util.showToast(mContext,
	 * "分享失败，请检查网络连接。出错信息：" + // e.getMessage()); Toast.makeText(mContext,
	 * "分享失败，请检查网络连接。出错信息2", 2000).show(); }
	 * 
	 * @Override public void onError(WeiboException e) { LOG.cstdr(TAG,
	 * "WeiboException---e = " + e.getMessage()); LOG.showToast(mContext,
	 * "分享失败，请检查网络连接。出错信息3：" + e.getMessage()); // Toast.makeText(mContext,
	 * "分享失败，请检查网络连接。出错信息", 2000).show(); }
	 * 
	 * @Override public void onComplete(String json) { JSONObject object; try {
	 * object = new JSONObject(json); String userName =
	 * object.optString(Sinas.SINA_NAME); LOG.cstdr(TAG,
	 * "show---onComplete---userName = " + userName);
	 * PreferenceUtil.getInstance(mContext).saveString(Sinas.
	 * PREF_SINA_USER_NAME, userName); if (listener != null) {
	 * listener.onResult(); } } catch (JSONException e) { e.printStackTrace(); }
	 * } }); }
	 */
	/**
	 * 发布一条新微博(连续两次发布的微博不可以重复)
	 * 
	 * @param content
	 *            要发布的微博文本内容，内容不超过140个汉字。
	 * @param lat
	 *            纬度，有效范围：-90.0到+90.0，+表示北纬，默认为0.0。
	 * @param lon
	 *            经度，有效范围：-180.0到+180.0，+表示东经，默认为0.0。
	 * @param listener
	 */
	/*
	 * public void update(String content, String lat, String lon) {
	 * System.out.println("为什么发文字就有信息：" + mAccessToken); SinaWeiboAPI api = new
	 * SinaWeiboAPI(mAccessToken); api.update(content, lat, lon, new
	 * RequestListener() {
	 * 
	 * @Override public void onIOException(IOException e) { LOG.cstdr(TAG,
	 * "onIOException---e = " + e.getMessage()); // Util.showToast(mContext,
	 * "分享失败，请检查网络连接。出错信息：" + // e.getMessage()); Toast.makeText(mContext,
	 * "分享失败，请检查网络连接。出错信息4", 2000).show(); }
	 * 
	 * @Override public void onError(WeiboException e) { LOG.cstdr(TAG,
	 * "onError---e = " + e.getMessage() + " e.getStatusCode() = " +
	 * e.getStatusCode()); if (e.getStatusCode() == 400) { // 相同内容短时间内不能分享 //
	 * Util.showToast(mContext, "分享失败，相同内容短时间内不能分享，请稍候再试吧。出错信息：" // +
	 * e.getMessage()); Toast.makeText(mContext,
	 * "分享失败，相同内容短时间内不能分享，请稍候再试吧。出错信息：5" + e.getMessage(), 2000).show(); } else
	 * { // Util.showToast(mContext, // "分享失败，请检查网络连接。出错信息：" + e.getMessage());
	 * Toast.makeText(mContext, "分享失败，请检查网络连接。出错信息6" + e.getMessage(),
	 * 2000).show(); } }
	 * 
	 * @Override public void onComplete(String str) { LOG.cstdr(TAG,
	 * "onComplete---str = " + str); // Util.showToast(mContext,
	 * "分享成功，去你绑定的新浪微博看看吧！"); Toast.makeText(mContext, "分享成功，去你绑定的新浪微博看看吧！7",
	 * 2000).show(); } }); }
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
