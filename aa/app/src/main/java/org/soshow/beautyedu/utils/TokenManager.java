package org.soshow.beautyedu.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.soshow.beautyedu.activity.user.ChangeCodeActivity;
import org.soshow.beautyedu.activity.LauncherActivity;
import org.soshow.beautyedu.activity.WebActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.Token;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

public class TokenManager {
	private Context mContext;
	private String mToken;
	private SharedPreferences sp;
	private Editor mEditor;
	//private String uid;
	private String app_nonce;
	private String app_id;
	private String app_key;
	public static Boolean handlered = false;
	//private Timer timer = new Timer();

	public TokenManager(Context cxt) {
		this.mContext = cxt;
		this.sp = mContext.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		this.mEditor = sp.edit();
	}

/*	public String getUID() {
		String uid_c = sp.getString("uid", null);
		if (uid_c != null) {
			uid = AESCipher.decryptd(Constant.aesKey, uid_c);
			return uid;
		}
		return null;
	}

	public void setUID(String str) {
		String uid_c;
		uid_c = AESCipher.encryptd(Constant.aesKey, str);
		mEditor.putString("uid", uid_c);
		mEditor.commit();

	}
*/
	public String getToken() {
		//TODO delte
		if(1==1)return "";
		synchronized (handlered) {
			mToken = sp.getString("mToken", null);
			LogUtils.e("1111111111111111111 获取到的Token="+mToken);
			if (mToken != null && mToken != "") {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
				try {
					Date now = df.parse(StringUtil.getDataTime());
					Date pDate = df.parse(sp.getString("token_Time", null));
					long l = now.getTime() - pDate.getTime();
					long min = (l / (60 * 1000));
					LogUtils.e("tocken的时长===="+min+"分");
					Log.d("234abc", "minute=              " + min);
					if (min >= 110) {
						mToken = null;
						mEditor.putBoolean("token_logined", false);
						mEditor.remove("mToken");
						mEditor.commit();
						tokenGet();
						LoginUtil.removeCookie(mContext);
						return null;

					}

				} catch (Exception e) {

					e.printStackTrace();
					return null;
				}
				return mToken;
			} else {
				tokenGet();
				LoginUtil.removeCookie(mContext);
				return null;
			}

		}
	}

	public void setToken(String str) {
		LogUtils.e("1111111111111111112 setToken="+str);
		mEditor.putString("mToken", str);
		mEditor.putString("token_Time", StringUtil.getDataTime());
		mEditor.commit();

	}

	public void tokenGet() {

		app_nonce = StringUtil.getPhoneIMEI(mContext);
		app_id = StringUtil.getMetaValue(mContext, "ANDRIOD_APP_ID");
		char an[] = app_nonce.toCharArray();
		char ai[] = app_id.toCharArray();
		int big = Math.min(an.length, ai.length);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= big - 1; i++) {
			try {
				char ann = an[i];
				sb.append(ann);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try {
				char ain = ai[i];
				sb.append(ain);

			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		app_key = Encryption.encryptd(Constant.code, Constant.code, sb.toString());
		final String tokenUrl = Constant.phpUrl + "/wap/api.php?action=GET_TOCKEN&app_id=" + app_id + "&app_nonce="
				+ app_nonce + "&app_key=" + app_key;
		Log.e("123321", "获取token地址:"+tokenUrl);
		NetHelper.get(tokenUrl, new SimpleSingleBeanNetHandler<Token>(mContext) {

			@Override
			protected void onSuccess(Token bean) {
				setToken(bean.tocken);
				mToken = bean.tocken;
				Log.e("345abc", "Token=============获取成功mToken="+mToken);
				String str1 = sp.getString("user_name", null);
				//Log.d("345abc", "获取用户" + str1);
				handlered = true;
				if (str1 != null && str1 != "") {// WEB端自动登录使用
					// sharedpreference中有信息时有token时自动登录
					try {
						Activity activity = (Activity) mContext;
						if (activity instanceof WebActivity) {
							new LoginUtil(mContext, 7);
							Log.d("345abc", "WebActivity登录");
						} else if (activity instanceof LauncherActivity) {
							new LoginUtil(mContext, 9);
							Log.d("123321", "LauncherActivity登录");
						}else if (activity instanceof ChangeCodeActivity) {
							new LoginUtil(mContext, 13);
						} else if (MyApplication.logined) {// 已登录状态下，停留在APP内，Token过期
							new LoginUtil(mContext, 10);

						}
						// else {
						// new LoginUtil(mContext,
						// Encryption.desEncryptd(Constant.code,
						// Constant.code,
						// str1), Encryption
						// .desEncryptd(Constant.code, Constant.code,
						// sp.getString("user_password", null)),
						// bean.tocken, 7);
						// }
					} catch (Exception e) {
						new LoginUtil(mContext, 8);

					}

				}

			}

			@Override
			protected void onError(int errorCode, String errorMsg) {
				handlered = true;
				if (app_nonce == null || app_nonce.equals("")) {
					Toast.makeText(mContext, "请打开APP获取手机识别码权限后重新打开APP", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(mContext, "连接服务器失败", Toast.LENGTH_SHORT).show();
				}
				Log.d("234abc", "在线获取token错误=" + errorCode + "   " + errorMsg);
			}

		});

	}

}
