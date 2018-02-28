package org.soshow.beautyedu.utils;

import java.io.File;
import java.net.URLEncoder;
import org.soshow.beautyedu.CoursesPlayActivity;
import org.soshow.beautyedu.activity.user.PersonInfoActivity;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.FragmentPer;
import org.soshow.beautyedu.activity.LoginInputActivity;
import org.soshow.beautyedu.activity.MainTabActivity;
import org.soshow.beautyedu.activity.PlayVideoActivity;
import org.soshow.beautyedu.activity.RegisterActivity;
import org.soshow.beautyedu.activity.WebActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.bean.PersonInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.bean.Logout;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.DemoHelper;


import com.easemob.chatuidemo.utils.PreferenceManager;
import com.easemob.exceptions.EaseMobException;
public class LoginUtil {
	private String mToken;
	private static Context mContext;
	private String name;
	private String password;
	private String password_encrypted;
	// private Intent intent;
	private int key;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;
	private String loginUrl;
	private TokenManager tManager;
	public static AlarmManager alarmManager;
	public static PendingIntent pi;
	public static Boolean login_handlered = false;
	private Dialog dialog;
	private String nickname;
	private String huanxin;
	private String huanxinpwd;
	private int third;
	private String login_type;

	static final int Msg_huanxinLogin = 3;
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			String log = (String) msg.obj;
			LogUtils.e("环信====="+log);
//			if(log.equals("huanxinLogin")){
//				huanxinLogin();
//			}
		};
	};

	public LoginUtil(Context cxt, String name, String password, Intent intent, int key) {
		this.mContext = cxt;
		this.name = name;
		this.password = password.trim();
		// this.intent = intent;
		this.key = key;
		sharedPreferences = mContext.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		Login();
	}

	public LoginUtil(Context cxt) {
		this.mContext = cxt;

		sharedPreferences = mContext.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		Login();
	}

	public LoginUtil(Context cxt, int key) {
		this.mContext = cxt;
		this.key = key;
		sharedPreferences = mContext.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		Login();
	}

	public LoginUtil(Context cxt, String name, String nickname, int third, int key) {
		this.mContext = cxt;
		this.name = name;
		this.nickname = nickname;
		this.key = key;
		this.third = third;
		sharedPreferences = mContext.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		Login();
	}

	public LoginUtil(Context cxt, String name, String nickname, int third, Intent intent, int key) {
		this.mContext = cxt;
		this.name = name;
		this.nickname = nickname;
//		 this.intent = intent;
		this.key = key;
		this.third = third;
		sharedPreferences = mContext.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		Login();
	}

	public LoginUtil(Context cxt, String name, String password, String mToken, int key) {
		this.mContext = cxt;
		this.name = name;
		this.password = password.trim();
		this.mToken = mToken;
		this.key = key;
		sharedPreferences = mContext.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		Login();
	}

	public LoginUtil(Context cxt, String name, String password, int key) {
		this.mContext = cxt;
		this.name = name;
		this.password = password.trim();
		this.key = key;
		sharedPreferences = mContext.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		Login();
	}

	public void Login() {
		dialog = ProgressDialogUtil.createLoadingDialog(mContext, "", true, false);
		try {
			Activity activity = (Activity) mContext;
			if (activity instanceof LoginInputActivity) {
				dialog.show();
			}
		} catch (Exception e) {
		}
//		if(name!=null && !isEmpty()){
//			userLogin();
//		}
		if (name == null) {
			Log.d("345abc", "name为空");
			updateUrlBackground();
			Boolean b = sharedPreferences.getBoolean("token_logined", false);
			if (b == true) {
				Toast.makeText(mContext, "已登录", Toast.LENGTH_SHORT).show();
				return;
			}

		} else {
			Log.d("345abc", "name不为空");
			if (nickname == null && isEmpty()) {
				if (dialog != null) {
					try {
						dialog.dismiss();
					} catch (Exception e) {
					}

				}
				return;
			}
			if (nickname == null && !StringUtil.isPhone(name)) {
				Toast.makeText(mContext, "请输入正确的手机号", Toast.LENGTH_SHORT).show();
				if (dialog != null) {
					try {
						dialog.dismiss();
					} catch (Exception e) {
					}
				}
				return;
			}
			updateUrl();
		}

        localLogin();
		
	}


//	private void huanxinLogin() {
//		if(1==1) return;
//	}

	public void huanxinLogin(final String huanxin,final String huanxing_pwd) {
 		if (DemoHelper.getInstance().isLoggedIn()) {
			DemoHelper.getInstance().logout(true, null);
        }
//		"18859663978", "123456",
        EMChatManager.getInstance().login(
				huanxin, huanxing_pwd,
                new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        // 登陆成功，保存用户名密码
//						DemoHelper.getInstance().setCurrentUserName(huanxin);
						// 注册群组和联系人监听

						DemoHelper.getInstance().getUserProfileManager().reset();
						DemoHelper.getInstance().registerGroupAndContactListener();
						DemoHelper.getInstance().setCurrentUserName(huanxin);
//						DemoHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(user.nickname);
//						DemoHelper.getInstance().getUserProfileManager().setCurrentUserAvatar(user.photo_url);

						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						loginOver();
//						DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();
                        try {
                            // **
							// 第一次登录或者之前logout后再登录，加载所有本地群和回话

							loginOver();
							// 处理好友和群组
							//                            initializeContacts();
						} catch (Exception e) {
                            e.printStackTrace();
                            // 取好友或者群聊失败，不让进入主页面
                            ((LoginInputActivity) mContext).runOnUiThread(new Runnable() {
                                public void run() {
                                    DemoHelper
                                            .getInstance()
                                            .logout(true,
                                                    null);
                                    Toast.makeText(mContext,
                                    		R.string.login_failure_failed,
                                            Toast.LENGTH_SHORT)
                                            .show();
                                }
                            });
                            return;
                        }
//                        LoginInputActivity activity = (LoginInputActivity) mContext;
//                     // 更新当前用户的nickname 此方法的作用是在ios离线推送时能够显示用户nick
//        				boolean updatenick = EMChatManager.getInstance().updateCurrentUserNick(
//        						MyApplication.currentUserNick.trim());
//        				if (!updatenick) {
//        					Log.e("LoginActivity", "update current user nick fail");
//        				}
                        if(dialog.isShowing()){
                        	dialog.dismiss();
                        }
                    }

                    @Override
                    public void onError(int arg0,
                            String arg1) {
                    	LogUtils.e("环信登入失败："+arg1);
                    	if(dialog.isShowing()){
                        	dialog.dismiss();
                        }
                    }

                    @Override
                    public void onProgress(int arg0,
                            String arg1) {
                    	LogUtils.e("环信登入进度："+arg1);
                    	if(dialog.isShowing()){
                        	dialog.dismiss();
                        }
                    }
                });
	}

	/**
	 * 本地登入
	 */
	PersonInfo user = null;
	private void localLogin() {
		if (loginUrl != null) {
			Log.d("345abc", "登录地址为 ================" + loginUrl);
			NetHelper.get(loginUrl, new SimpleSingleBeanNetHandler<Captcha>(mContext) {
				@Override
				protected void onError(int errorCode, String errorMsg) {
					super.onError(errorCode, errorMsg);
				}

				@Override
				protected void onSuccess(Captcha bean) {
					EMChat.getInstance().setAutoLogin(true);
					login_handlered = true;
						if(bean.isSuccess()){

//							Log.e("-------------------------------", "name===="+name+"  \npassword==="+password);
							String username = "";
							String email = "";
							String signature = "";
							String nickname = "";
							int id = 0;
							String photo_url = "";
							String cellphone = "";
//							int card_integral = 0;

							try {
								String info = bean.info;
								user = GsonUtils.parseJSON(info, PersonInfo.class);
								username = user.username;
								id=user.id;
								nickname = user.nickname;
								if(user.photo_url!=null){
									photo_url=user.photo_url;
								}
								cellphone=user.mobile;
								PersonInfoActivity.isInfoModify = true;
//								Message messageHuanXin = new Message();
//								messageHuanXin.obj = "huanxinLogin";
//								handler.sendMessage(messageHuanXin);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
							
							editor.putBoolean("token_logined", true);
							editor.putBoolean("logined", true);
							if (name != null && name != "" && password != null && password != "") {
								editor.putString("user_name",name
										);
//								Encryption.encryptd(Constant.code_local, Constant.code_local, name)
//								editor.putString("user_password",  password);
							}
//							Encryption.encryptd(Constant.code_local, Constant.code_local, password)
							editor.putString("username", username);
							editor.putString("user_id",String.valueOf(id));
//							editor.putString("email", email);
//							LogUtils.e("headUrl="+headUrl+"\nuser_id="+user_id);
							SPUtils.put(mContext, "headUrl", (String)photo_url);

//							SPUtils.put(mContext, "signature", signature);
							SPUtils.put(mContext, "user_id", String.valueOf(id));
							SPUtils.put(mContext, "username", username);
							SPUtils.put(mContext,"nickname",nickname);
//							SPUtils.put(mContext, "card_integral", card_integral+"");
							LogUtils.e("登入   当前积分card_integral2=" + SPUtils.get(mContext, "card_integral", ""));
							editor.commit();
							MyApplication.logined = true;

							//登录环信
								huanxinLogin(username,password);

						}else{
							Log.d("345abc", "登录失败 result="+bean.result );
							if(bean.result.equals("4")){
								ToastUtil.getInstance().showToast(mContext, "你还未注册，请先注册");
							}else if(bean.result.equals("2")){
								ToastUtil.getInstance().showToast(mContext, "账号密码有误");
							}else{
								ToastUtil.getInstance().showToast(mContext, bean.message);
							}
							dialog.dismiss();
						}
				}

			});
		}
	}

	private void loginOver(){
		if (key == 1 || key == 2 || key == 3 || key == 4 || key == 36 || key == 37) {// 打开我的课程
			LoginInputActivity lg = (LoginInputActivity) mContext;
			lg.finish();
			// mContext.startActivity(intent);

		} else if (key == 6) {// 网页手动登录
			LoginInputActivity lg = (LoginInputActivity) mContext;
			lg.setResult(LoginInputActivity.RESULT_CODE);
			lg.finish();
		} else if (key == 5) {// “我的”界面登录成功,条件为5
			LoginInputActivity lg = (LoginInputActivity) mContext;
			lg.setResult(LoginInputActivity.RESULT_CODE);
			lg.finish();
		}else if (key == MainTabActivity.CHAT_CORD) {// “我的”界面登录成功,条件为5
			LoginInputActivity lg = (LoginInputActivity) mContext;
			lg.setResult(LoginInputActivity.RESULT_CODE);
			lg.finish();
		}else if (key == 31) {// 个人资料
			LoginInputActivity lg = (LoginInputActivity) mContext;
			lg.setResult(LoginInputActivity.RESULT_CODE);
			lg.finish();
		} else if (key == 7) {// 网页自动登录(做题)
			WebActivity.changeToken(mToken);
		} else if (key == 0) {// 默认key,使用为退出后重新登录
			LoginInputActivity lg = (LoginInputActivity) mContext;
			lg.setResult(LoginInputActivity.RESULT_CODE);
			lg.finish();
		} else if (key == 8) { // Service自动登录
			// Log.d("345abc", "Service登录后用户密码" +
			// sharedPreferences.getString("user_password",
			// null));
			Log.d("345abc", "Service登录成功");
		} else if (key == 9) {// LauncherActivity重新登录
			Log.d("345abc", "Service登录成功");
		} else if (key == 10) {// 其他情况自动登录
			Log.d("345abc", "后台登录成功登录成功");
		} else if (key == 11) {// 注册界面返回自动登录

			if (LoginInputActivity.lia_instance != null) {
				LoginInputActivity.lia_instance.finish();
			}
			RegisterActivity ra = (RegisterActivity) mContext;
			ra.finish();
			if (PlayVideoActivity.pva_instance != null) {
				try {
					Message message = PlayVideoActivity.pva_instance.getHandler()
							.obtainMessage();
					message.what = 8;
					message.sendToTarget();
				} catch (Exception e) {
					// TODO: handle exception
				}

			} else if (CoursesPlayActivity.pva_instance != null) {
				try {
					Message message = CoursesPlayActivity.pva_instance.getHandler()
							.obtainMessage();
					message.what = 8;
					message.sendToTarget();
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		} else if (key == 13) {// 后台自动登录
			// Toast.makeText(mContext, "de",
			// Toast.LENGTH_SHORT).show();
		} else if (key == 14) {// 视频播放界面登陆
			try {
				if (PlayVideoActivity.pva_instance != null) {
					Message message = PlayVideoActivity.pva_instance.getHandler()
							.obtainMessage();
					message.what = 8;
					message.sendToTarget();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else if (key == 15) {// 标准课程界面登陆
			try {
				if (CoursesPlayActivity.pva_instance != null) {
					Message message = CoursesPlayActivity.pva_instance.getHandler()
							.obtainMessage();
					message.what = 8;
					message.sendToTarget();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else if (key == 20) {// 重新登录刷新标准课程
			try {
				if (CoursesPlayActivity.pva_instance != null) {
					Message message = CoursesPlayActivity.pva_instance.getHandler()
							.obtainMessage();
					message.what = 8;
					message.sendToTarget();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		} else {

		}
		if (Constant.next_page != null) {
			mContext.startActivity(Constant.next_page);
			Constant.next_page = null;
		}
		if (LoginInputActivity.lia_instance != null) {
			LoginInputActivity.lia_instance.finish();

		}
	}
	
	public Boolean isEmpty() {
		if (StringUtil.isEmpty(name)) {
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

		return false;
	}

	private void updateUrl() {
		tManager = new TokenManager(mContext);
		mToken = tManager.getToken();
//		if (mToken != null) {
			String app_nonce = StringUtil.getPhoneIMEI(mContext);
			if (nickname == null) {
				Log.d("234abc", "getToken=            " + mToken);
				password_encrypted = URLEncoder.encode(Encryption.encryptd(Constant.code, Constant.code, password));

//				loginUrl = Constant.phpUrl + "/wap/api.php?action=LOGIN&type=1&mobile=" + URLEncoder.encode(name)
//						+ "&data=" + password_encrypted + "&app_nonce=" + app_nonce + "&tocken=" + mToken;
//				Log.d("234abc", loginUrl);
//				RSAEncrypt rsaEncrypt = RSAEncrypt.get();
//				String a = "";
//				try{
//					byte[] cipher = rsaEncrypt.encrypt(rsaEncrypt.getPublicKey(), password.getBytes());
//					 a = Base58.encode(cipher);
//				}catch (Exception e){
//					e.printStackTrace();
//				}
//				loginUrl = Constant.LoginUrl+"&mobile="+name+"&passwordEncry="+a;
				loginUrl =  Constant.LoginUrl+"&mobile="+name+"&data="+password;
			}
//			else {
//				login_type = null;
//				switch (third) {
//					case 0:// 微博
//						login_type = "weibo";
//						break;
//					case 1:// QQ
//						login_type = "qq";
//						break;
//					case 2:// 微信
//						login_type = "weixin";
//						break;
//					default:
//						break;
//				}
//				loginUrl = Constant.phpUrl + "/wap/api.php?action=GET_THIRD_PARTY_LOGIN&app_nonce=" + app_nonce
//						+ "&tocken=" + mToken + "&login_type=" + login_type + "&login_id=" + URLEncoder.encode(name)
//						+ "&nickname=" + URLEncoder.encode(nickname);
//				Log.d("234abc", loginUrl);
//			}
//		}
//		} else {
//			try {
//				dialog.dismiss();
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//		}
	}

	private void updateUrlBackground() {

		tManager = new TokenManager(mContext);
		mToken = tManager.getToken();
		if (mToken != null) {
			Log.d("345abc", "获取Token" + mToken);
			// if (Constant.code_changed) {
			// name = Encryption.desEncryptd(Constant.code, Constant.code,
			// sharedPreferences.getString("user_name", null));
			// } else {
			name = Encryption.desEncryptd(Constant.code_local, Constant.code_local,
					sharedPreferences.getString("user_name", null));
			// }
			Log.d("345abc", "账户名称为" + name);
			if (name != null) {
				String app_nonce = StringUtil.getPhoneIMEI(mContext);
				// Log.d("345abc", "密码="+Encryption.desEncryptd(Constant.code,
				// Constant.code, str1));
				if (StringUtil.isPhone(name)) {// 账号登陆
					// if (Constant.code_changed) {
					// password_encrypted = Encryption.encryptd(Constant.code,
					// Constant.code, Encryption.desEncryptd(
					// Constant.code, Constant.code,
					// sharedPreferences.getString("user_password", null)));
					// } else {
					password_encrypted = Encryption.encryptd(Constant.code, Constant.code,
							Encryption.desEncryptd(Constant.code_local, Constant.code_local,
									sharedPreferences.getString("user_password", null)));
					// }

					loginUrl = Constant.phpUrl + "/wap/api.php?action=LOGIN&type=1&mobile=" + URLEncoder.encode(name)
							+ "&data=" + URLEncoder.encode(password_encrypted) + "&app_nonce=" + app_nonce + "&tocken="
							+ mToken;
					Log.e("345abc", "登录对象为=" + mContext.toString() + "登录地址" + loginUrl);
				} else {// 第三方登陆
					nickname = sharedPreferences.getString("username", null);
					loginUrl = Constant.phpUrl + "/wap/api.php?action=GET_THIRD_PARTY_LOGIN&app_nonce=" + app_nonce
							+ "&tocken=" + mToken + "&login_type=" + sharedPreferences.getString("login_type", null)
							+ "&login_id=" + URLEncoder.encode(name) + "&nickname=" + URLEncoder.encode(nickname);
					Log.d("234abc", loginUrl);
				}

			}
		}
	}

	public static void logout(final Context cxt) {
		final SharedPreferences sp = cxt.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		final Editor editor = sp.edit();
		String mobile = sp.getString("user_name", null);
		String app_nonce = StringUtil.getPhoneIMEI(cxt);

		TokenManager tManager1 = new TokenManager(cxt);

		String token1 = tManager1.getToken();
		if (token1 != null) {
			String logouturl = Constant.phpUrl + "/wap/api.php?action=LOGOUT&type=1&mobile=" + mobile + "&app_nonce="
					+ app_nonce + "&tocken=" + token1;
			NetHelper.get(logouturl, new SimpleSingleBeanNetHandler<Logout>(cxt) {

				@Override
				protected void onSuccess(Logout bean) {
					// TODO Auto-generated method stub
					SPUtils.put(cxt, "is_loginout", true);
					boolean is_loginout = (Boolean) SPUtils.get(cxt, "is_loginout", false);
					Log.e("234abc", "is_loginout=" + is_loginout);
					try {
						int result = Integer.parseInt(bean.result);
						switch (result) {
						case 0:
							editor.putBoolean("logined", false);
							MyApplication.logined = false;
							editor.remove("user_name");
							editor.remove("username");
							editor.remove("user_password");
							// editor.remove("allow_net_local");
							editor.remove("net_flag");
							editor.putBoolean("token_logined", false);
							editor.remove("login_type");
//							editor.remove("email");
							editor.remove("cellphone");
							editor.remove("headUrl");
							editor.remove("signature");
							editor.remove("id");
							editor.commit();
							
							SPUtils.remove(cxt, "headUrl");
//							SPUtils.remove(cxt, "signature");
							SPUtils.remove(cxt, "id");
							SPUtils.remove(cxt, "username");
							PersonInfoActivity.isInfoModify = false;
							
							// FragmentSet.allow_net.setEnabled(false);
							// FragmentSet.allow_net.setChecked(false);
							// new TokenManager(cxt).tokenGet();
							removeCookie(cxt);
							removeData(cxt);
							Toast.makeText(cxt, "退出成功", Toast.LENGTH_SHORT).show();
							
							
							//退出环信
							DemoHelper.getInstance().logout(true, null);
							
							// Intent intent = new Intent((MainTabActivity) cxt,
							// LoginInputActivity.class);
							// cxt.startActivity(intent);
							try {
								Activity at = (Activity) cxt;
								RelativeLayout logout_set = (RelativeLayout) at.findViewById(R.id.logout_set);
								if (logout_set != null) {
									logout_set.setVisibility(View.GONE);
								}
							} catch (Exception e) {
								// TODO: handle exception
								Log.d("123456", "异常" + e.toString());
							}
							if (MainTabActivity.mta_instance != null && MainTabActivity.mTabHost != null) {
								MainTabActivity.mTabHost.setCurrentTab(0);
								if(FragmentPer.set_name != null){
									FragmentPer.set_name.setVisibility(View.GONE);
								}
								if(FragmentPer.rl != null){
									FragmentPer.rl.setVisibility(View.GONE);
								}
								if(FragmentPer.tvLogin != null){
									FragmentPer.tvLogin.setVisibility(View.VISIBLE);
								}
								
								
							}
							// RelativeLayout logout_set=(RelativeLayout)cxt.
							break;
						case 1:
							Toast.makeText(cxt, "退出失败", Toast.LENGTH_SHORT).show();
							break;
						case 99:
							editor.putBoolean("token_logined", false);
							editor.remove("mToken");
							editor.commit();
							break;
						default:
							break;
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					// super.onError(errorCode, errorMsg);
					Toast.makeText(cxt, "退出失败，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
				}

			});
		}
	}

	public static void removeCookie(Context cxt) {
		CookieSyncManager.createInstance(cxt.getApplicationContext());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		CookieSyncManager.getInstance().sync();
	}

	public static void removeData(Context cxt) {
		try {// 退出删除播放记录
			File file = new File(cxt.getCacheDir().toString() + "/record_list.txt");
			file.delete();

		} catch (Exception e) {
			// TODO: handle exception
		}
		try {// 退出删除练习记录
			File file = new File(cxt.getCacheDir().toString() + "/mypractice_list.txt");
			file.delete();
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {// 退出删除错题记录

			File file = new File(cxt.getCacheDir().toString() + "/myerror_list.txt");
			file.delete();
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {// 退出删除题目收藏
			File file = new File(cxt.getCacheDir().toString() + "/mytitle_list.txt");
			file.delete();

		} catch (Exception e) {
			// TODO: handle exception
		}
		try {// 退出删除视频收藏
			File file = new File(cxt.getCacheDir().toString() + "/myvideo_list.txt");
			file.delete();

		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	

	
	/*
	 * 环信注册
	 */
	private void registHuanXin(final String username,final String pwd){
		//delete
		final RegisterActivity activivty = (RegisterActivity) mContext;
		
        final String st6 = mContext.getResources().getString(R.string.Registered_successfully);  
  
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {  
            final ProgressDialog pd = new ProgressDialog(mContext);  
            pd.show();  
            final String st7 = mContext.getResources().getString(R.string.network_anomalies);  
            final String st8 = mContext.getResources().getString(R.string.User_already_exists);  
            final String st9 = mContext.getResources().getString(R.string.registration_failed_without_permission);  
            final String st10 = mContext.getResources().getString(R.string.Registration_failed);  
            new Thread(new Runnable() {  
                public void run() {  
                    try {  
                        // 调用sdk注册方法  
                        EMChatManager.getInstance().createAccountOnServer(username, pwd);  
                        activivty.runOnUiThread(new Runnable() {  
                            public void run() {  
                            	Log.e("", "环信注册成功yyyyyyyy");
                                if (!activivty.isFinishing())  
                                    pd.dismiss();  
                                // 保存用户名  

                                Toast.makeText(mContext.getApplicationContext(), st6, Toast.LENGTH_SHORT).show();
//                                huanxinLogin();
                            }  
                        });  
                    } catch (final EaseMobException e) {  
                    	activivty.runOnUiThread(new Runnable() {  
                            public void run() {  
                                if (!activivty.isFinishing())  
                                    pd.dismiss();  
                                int errorCode=e.getErrorCode();  
                                if(errorCode==EMError.NONETWORK_ERROR){  
                                    Toast.makeText(mContext.getApplicationContext(), st7, Toast.LENGTH_SHORT).show();  
                                }else if(errorCode==EMError.USER_ALREADY_EXISTS){  
                                    Toast.makeText(mContext.getApplicationContext(), st8, Toast.LENGTH_SHORT).show();  
                                }else if(errorCode==EMError.UNAUTHORIZED){  
                                    Toast.makeText(mContext.getApplicationContext(), st9, Toast.LENGTH_SHORT).show();  
                                }else{  
                                    Toast.makeText(mContext.getApplicationContext(), st10 + e.getMessage(), Toast.LENGTH_SHORT).show();  
                                }  
                            }  
                        });  
                    }  
                }  
            }).start();  
  
        }  
	}
	
	
	
}
