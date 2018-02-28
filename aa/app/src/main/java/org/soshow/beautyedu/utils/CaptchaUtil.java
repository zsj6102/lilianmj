package org.soshow.beautyedu.utils;

import java.util.Timer;
import java.util.TimerTask;

import org.soshow.beautyedu.activity.GetCodeSmsActivity;
import org.soshow.beautyedu.activity.RegisterActivity;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class CaptchaUtil {
	private static String account;
	private static String mToken;
	private static SharedPreferences sp;
	private static Editor editor;
	private static Handler handler2;
	private static Handler handler = new Handler() {
		private Context cxt;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:
				cxt = (Context) msg.obj;
				if (TokenManager.handlered) {
					Log.d("345abc", "验证码获取新token");
					TokenManager.handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
						getCaptcha(account, cxt, handler2);
						;
					} else {
						Toast.makeText(cxt, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
							Log.d("345abc", "验证码循环等待");
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 2;
							msg_loop.obj = cxt;
							msg_loop.sendToTarget();
						}
					}, 1000);

				}

				break;

			default:
				break;
			}
		}

	};

	public static void getCaptcha(String account, final Context mContext, Handler handler) {
		CaptchaUtil.account = account;
		sp = mContext.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor=sp.edit();
		mToken = new TokenManager(mContext).getToken();
		handler2 = handler;
		if (mToken != null) {
			String app_nonce = StringUtil.getPhoneIMEI(mContext);
			Activity activity = (Activity) mContext;
			int send_type = 0;
			if (activity instanceof RegisterActivity) {
				send_type = 1;
			} else if (activity instanceof GetCodeSmsActivity) {
				send_type = 4;
			}
			String captchaurl = Constant.phpUrl + "/wap/api.php?action=GET_VERIFY_CODE&app_nonce=" + app_nonce
					+ "&mobile=" + account + "&tocken=" + mToken + "&send_type=" + send_type;
			Log.d("345abc", "验证码url=" + captchaurl);
			NetHelper.get(captchaurl, new SimpleSingleBeanNetHandler<Captcha>(mContext) {
				@Override
				protected void onSuccess(Captcha bean) {
					// TODO Auto-generated method stub
					Message msg_loop = handler2.obtainMessage();
					try {
						int result=Integer.parseInt(bean.result);
						switch (result) {
						case 0:// 发送成功
							Toast.makeText(mContext, "验证码获取成功,请注意查收短信", Toast.LENGTH_SHORT).show();
							Log.d("345abc", "验证码获取成功" + bean.result + "    " + bean.info);
							// Editor editor = sp.edit();
							// editor.putString("captcha_md5", bean.info);
							// editor.commit();
							msg_loop.what = 0;
							msg_loop.sendToTarget();

							break;
						case 3:// 手机号码已注册
							Toast.makeText(mContext, bean.info, Toast.LENGTH_SHORT).show();
							msg_loop.what = 99;
							msg_loop.sendToTarget();
							break;
						case 4:// 手机格式不正确
							//Log.d("1221", "发送消息");
							Toast.makeText(mContext, bean.info, Toast.LENGTH_SHORT).show();
							msg_loop.what = 99;
							msg_loop.sendToTarget();
							break;
						case 99:// token有误
							msg_loop.what = 99;
							msg_loop.sendToTarget();
							editor.putBoolean("token_logined", false);
							editor.remove("mToken");
							editor.commit();
							break;
						case 100:// 请求类型错误
							Toast.makeText(mContext, bean.info, Toast.LENGTH_SHORT).show();
							msg_loop.what = 99;
							msg_loop.sendToTarget();
							break;
						case 101:// 手机格式不正确
							Toast.makeText(mContext, bean.info, Toast.LENGTH_SHORT).show();
							msg_loop.what = 99;
							msg_loop.sendToTarget();
							break;
						case 102:// 发送太频繁
							Toast.makeText(mContext, bean.info, Toast.LENGTH_SHORT).show();
							msg_loop.what = 101;
							msg_loop.sendToTarget();
							break;
						case 103:// 发送太频繁
							Toast.makeText(mContext, bean.info, Toast.LENGTH_SHORT).show();
							msg_loop.what = 101;
							msg_loop.sendToTarget();
							break;
						case 104:// 发送太频繁
							Toast.makeText(mContext, bean.info, Toast.LENGTH_SHORT).show();
							msg_loop.what = 101;
							msg_loop.sendToTarget();
							break;
						case 105:// 发送失败
							Toast.makeText(mContext, bean.info, Toast.LENGTH_SHORT).show();
							msg_loop.what = 99;
							msg_loop.sendToTarget();
							break;
						case 106:// 发送失败
							Toast.makeText(mContext, bean.info, Toast.LENGTH_SHORT).show();
							msg_loop.what = 99;
							msg_loop.sendToTarget();
							break;
						default:
							Toast.makeText(mContext, bean.info, Toast.LENGTH_SHORT).show();
							msg_loop.what = 0;
							msg_loop.sendToTarget();
							break;
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					Toast.makeText(mContext, "验证码获取失败", Toast.LENGTH_SHORT).show();
					Message msg_loop = handler2.obtainMessage();
					msg_loop.what = 99;
					msg_loop.sendToTarget();
					// Activity activity = (Activity) mContext;
					// EditText
					// ed1=(EditText)(activity.findViewById(R.id.register_edit_ccaptcha));
				}

			});
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.obj = mContext;
			msg_loop.sendToTarget();
		}
	}

}
