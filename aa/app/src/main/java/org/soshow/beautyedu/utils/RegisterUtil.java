package org.soshow.beautyedu.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.RegisterActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.Register;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoHelper;
import com.easemob.exceptions.EaseMobException;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class RegisterUtil {
	private Context mContext;
	private String account;
	private String password;
	private String otherPhone;
	private String captcha;
	private String invatation;
	// private TokenManager tManager;
	// private Intent intent;
	private String mToken;
	private SharedPreferences sp;
	private Editor editor;
	 private String check_code;
	private Dialog dialog;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:
				if (TokenManager.handlered) {
					Log.d("345abc", "注册获取新token");
					TokenManager.handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
						getInfo();

					} else {
						Toast.makeText(mContext, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
							Log.d("345abc", "注册循环等待");
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 2;
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

	public RegisterUtil(Context cxt, String account, String password, String otherPhone,String invitation) {
		this.mContext = cxt;
		this.account = account;
		this.password = password.trim();
		this.captcha = otherPhone.trim();
		this.invatation = invitation.trim();
		dialog = ProgressDialogUtil.createLoadingDialog(mContext, "注册中", true, false);
		register();
	}

	public void register() {
		if (isEmpty()) {
			return;
		}
		if (!StringUtil.isPhone(account)) {
			Toast.makeText(mContext, "请输入正确的手机", Toast.LENGTH_SHORT).show();
			return;
		}
		sp = mContext.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		// check_code = sp.getString("captcha_md5", null);
		// if (check_code == null ||
		// !StringUtil.md5(captcha).equals(check_code)) {
		// Log.d("345abc", "输入验证码" + StringUtil.md5(captcha) + "保存验证码" +
		// check_code);
		// Toast.makeText(mContext, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
		// return;
		// }
//		mToken = new TokenManager(mContext).getToken();
//		if (mToken != null) {
			getInfo();
//		} else {
//			TokenManager.handlered = false;
//			Message msg_loop = handler.obtainMessage();
//			msg_loop.what = 2;
//			msg_loop.sendToTarget();
//		}
	}


	private Boolean isEmpty() {
		if (StringUtil.isEmpty(account)) {
			Toast.makeText(mContext, "账号不能为空", Toast.LENGTH_SHORT).show();
			return true;
		}
		if (StringUtil.isEmpty(password)) {
			Toast.makeText(mContext, "密码不能为空", Toast.LENGTH_SHORT).show();
			return true;
		}
		if (password.trim().length() < 6) {
			Toast.makeText(mContext, "密码最低为6位,不包含首尾空格", Toast.LENGTH_SHORT).show();
			// Log.d("123456", "trim后为" + password + "abc");
			return true;
		}
		if (StringUtil.isEmpty(captcha)) {
			Toast.makeText(mContext, "请输入短信验证码", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}
	
	/*
	 * 环信注册
	 */
//	public void registHuanXin(final String username,final String pwd){
//		final RegisterActivity activivty = (RegisterActivity) mContext;
//
//        final String st6 = mContext.getResources().getString(R.string.Registered_successfully);
//
//        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
//            final ProgressDialog pd = new ProgressDialog(mContext);
//            pd.show();
//            final String st7 = mContext.getResources().getString(R.string.network_anomalies);
//            final String st8 = mContext.getResources().getString(R.string.User_already_exists);
//            final String st9 = mContext.getResources().getString(R.string.registration_failed_without_permission);
//            final String st10 = mContext.getResources().getString(R.string.Registration_failed);
//            new Thread(new Runnable() {
//                public void run() {
//                    try {
//                        // 调用sdk注册方法
//                        EMChatManager.getInstance().createAccountOnServer(username, pwd);
//                        activivty.runOnUiThread(new Runnable() {
//                            public void run() {
//                            	Log.e("", "环信注册成功yyyyyyyy");
//                                if (!activivty.isFinishing())
//                                    pd.dismiss();
//                                // 保存用户名
//								DemoHelper.getInstance().setCurrentUserName(username);
//                                Toast.makeText(mContext.getApplicationContext(), st6, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    } catch (final EaseMobException e) {
//                    	activivty.runOnUiThread(new Runnable() {
//                            public void run() {
//                                if (!activivty.isFinishing())
//                                    pd.dismiss();
//                                int errorCode=e.getErrorCode();
//                                if(errorCode==EMError.NONETWORK_ERROR){
//                                    Toast.makeText(mContext.getApplicationContext(), st7, Toast.LENGTH_SHORT).show();
//                                }else if(errorCode==EMError.USER_ALREADY_EXISTS){
//                                    Toast.makeText(mContext.getApplicationContext(), st8, Toast.LENGTH_SHORT).show();
//                                }else if(errorCode==EMError.UNAUTHORIZED){
//                                    Toast.makeText(mContext.getApplicationContext(), st9, Toast.LENGTH_SHORT).show();
//                                }else{
//                                    Toast.makeText(mContext.getApplicationContext(), st10 + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    }
//                }
//            }).start();
//
//        }
//	}

	private void getInfo() {
		try {
			
			//注册环信
//			registHuanXin(account, password);
			
			dialog.show();
//			String app_nonce = StringUtil.getPhoneIMEI(mContext);
//			String data = Encryption.encryptd(Constant.code, Constant.code, password);
			String url_register = Constant.phpUrl + "/wap/api.php?action=USER_REGIST";
			Log.e("345abc", "注册url=" + url_register);
			Map<String, String> params = new HashMap<String, String>();
			params.put("phone", account);
			params.put("vcode", captcha);
			params.put("new_pwd", password);
			params.put("recommend", otherPhone);
			params.put("invitationCode",invatation);
			NetHelper.post(url_register, params , new SimpleSingleBeanNetHandler<Register>(mContext) {

				@Override
				protected void onSuccess(Register bean) {
					Log.e("", "注册返回的结果：result="+bean.result+bean.info);
					try {
						dialog.dismiss();
					} catch (Exception e) {
						// TODO: handle exception
					}
					int index_result = 888;
					try {
						index_result = Integer.parseInt(bean.result);
						switch (index_result) {
						case 0:
							Toast.makeText(mContext, "注册成功", Toast.LENGTH_SHORT).show();
							editor.putString("user_name",
									Encryption.encryptd(Constant.code_local, Constant.code_local, account));
							editor.putString("user_password",
									Encryption.encryptd(Constant.code_local, Constant.code_local, password));
							editor.commit();
							 ((RegisterActivity) mContext).finish();
							break;
						case 99:// tocken有误，该情况考虑是否会出现
							editor.putBoolean("token_logined", false);
							editor.remove("mToken");
							editor.commit();
							break;
						default:
							Toast.makeText(mContext, bean.message, Toast.LENGTH_SHORT).show();
							break;
						}
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(mContext, "注册失败", Toast.LENGTH_SHORT).show();
					}

				}
				
				@Override
				protected void onError(int errorCode, String errorMsg) {
					try {
						dialog.dismiss();
					} catch (Exception e) {
						// TODO: handle exception
					}
					Toast.makeText(mContext, "通信失败", Toast.LENGTH_SHORT).show();
				}
			});
			
			
			
			
			
			//旧版本的注册
//			String url_register = Constant.phpUrl + "/wap/api.php?action=REGISTER&app_nonce=" + app_nonce + "&mobile="
//					+ account + "&tocken=" + mToken + "&data=" + data + "&check_code=" + StringUtil.md5(captcha);
//			NetHelper.get(url_register, new SimpleSingleBeanNetHandler<Register>(mContext) {
//
//				@Override
//				protected void onSuccess(Register bean) {
//					try {
//						dialog.dismiss();
//					} catch (Exception e) {
//						// TODO: handle exception
//					}
//					int index_result = 888;
//					try {
//						index_result = Integer.parseInt(bean.result);
//						switch (index_result) {
//						case 0:
//							Toast.makeText(mContext, "注册成功", Toast.LENGTH_SHORT).show();
//							// editor.remove("captcha_md5");
//							editor.putString("user_name",
//									Encryption.encryptd(Constant.code_local, Constant.code_local, account));
//							editor.putString("user_password",
//									Encryption.encryptd(Constant.code_local, Constant.code_local, password));
//							editor.commit();
//							
//							// ((RegisterActivity) mContext).finish();
//							break;
//						case 99:// tocken有误，该情况考虑是否会出现
//							editor.putBoolean("token_logined", false);
//							editor.remove("mToken");
//							editor.commit();
//							break;
//						default:
//							Toast.makeText(mContext, bean.info, Toast.LENGTH_SHORT).show();
//							break;
//						}
//					} catch (Exception e) {
//						// TODO: handle exception
//						Toast.makeText(mContext, "注册失败", Toast.LENGTH_SHORT).show();
//					}
//
//				}
//
//				@Override
//				protected void onError(int errorCode, String errorMsg) {
//					// TODO Auto-generated method stub
//					/*
//					 * if (LoginActivity._instace != null) {
//					 * LoginActivity._instace.finish(); }
//					 * Toast.makeText(mContext, "通信失败",
//					 * Toast.LENGTH_SHORT).show(); if
//					 * (LoginInputActivity.lia_instance == null) {
//					 * //mContext.startActivity(intent); } RegisterActivity ra =
//					 * (RegisterActivity) mContext; ra.finish();
//					 */
//					try {
//						dialog.dismiss();
//					} catch (Exception e) {
//						// TODO: handle exception
//					}
//					Toast.makeText(mContext, "通信失败", Toast.LENGTH_SHORT).show();
//				}
//
//			});

		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
}
