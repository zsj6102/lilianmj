package org.soshow.beautyedu.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.CoursesPlayActivity;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.bean.WxCallbackInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.HttpUtil;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.json.utils.NetUtil;
import org.soshow.beautyedu.login.sina.SinaWeiboUtil;
import org.soshow.beautyedu.login.sina.Sinas;
import org.soshow.beautyedu.login.tencent.Util;
import org.soshow.beautyedu.login.tools.LOG;
import org.soshow.beautyedu.login.tools.PreferenceUtil;
import org.soshow.beautyedu.login.tools.WeiboListener;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.SPUtils;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMError;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.DemoHelper;
import com.easemob.exceptions.EaseMobException;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;

import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 登陆
 */
public class LoginInputActivity extends BaseActivity implements OnFocusChangeListener, OnClickListener, TextWatcher {

	public static final int RESULT_CODE = 1;
	private static ImageView login_account_edit_clear, login_key_edit_clear;
	private static EditText login_edit_account, login_edit_key;
	private static Button login_btn;
	private static TextView login_regist, login_forget;
	// private static ScrollView scrollView;
	private static LinearLayout layout;
    private ImageView iv_weixin;
	// 登录工具
	private LoginUtil loginUtil;
	// 获取Instance
	public static LoginInputActivity lia_instance;
	// 腾讯登录部分变量

	// private Button mNewLoginButton;
	// private TextView mUserInfo;
	// private ImageView mUserLogo;
	public static QQAuth mQQAuth;
	private UserInfo mInfo;
	private Tencent mTencent;
	private final String APP_ID = "1104778287";// 测试时使用，真正发布的时候要换成自己的APP_ID
	private InputMethodManager imm;
	public static String openid;

	// 保存登录信息
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor editor;

	private Context mContext;
	// 新浪微博登录变量
	private SinaWeiboUtil weibo_sina;

	private int tag = 0;

	// private static Handler mHandler;
	private Intent intent;
	private static Handler lHandler;
	// private static int REQUEST_CODE = 1;
    private String photo_url;
	private int id;
	private String username;
	private String nickname;
	// 定义第三方登陆类型
	private int login_type;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// if (progressDialog != null) {
			// progressDialog.dismiss();
			// }
			editor.putBoolean("logined", true);
			editor.commit();
			MyApplication.logined = true;
			if (msg.what == 0) {
				Intent intent1 = new Intent();
				intent1.putExtra("tag", tag);
				switch (key) {
				case 1:
					intent1.setClass(LoginInputActivity.this, RecordActivity.class);
					startActivity(intent1);
					break;
				case 2:
					intent1.setClass(LoginInputActivity.this, CollectActivity.class);
					startActivity(intent1);
					break;

				case 3:
					intent1.setClass(LoginInputActivity.this, PracticeActivity.class);
					startActivity(intent1);
					break;

				case 4:
					intent1.setClass(LoginInputActivity.this, ErrorActivity.class);
					startActivity(intent1);
					break;
				// 个人页返回
				case 5:
					LoginInputActivity.this.setResult(RESULT_CODE);
					break;
				default:
					break;
				}

				finish();
				
				overridePendingTransition(R.anim.anim_slider_left_in,
	                    R.anim.anim_slider_right_out);
			}
		}
	};
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 55:
				finish();
				overridePendingTransition(R.anim.anim_slider_left_in,
	                    R.anim.anim_slider_right_out);
				break;
			default:
				break;
			}
		}
	};
	public int key = 0;
	private boolean third_click = false;
    private UMShareAPI umShareAPI;
	UMAuthListener authListener = new UMAuthListener() {
		@Override
		public void onStart(SHARE_MEDIA share_media) {

		}

		/**
		 * @desc 授权成功的回调
		 *
		 */
		@Override
		public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
			Map<String, String> params = new HashMap<String, String>();
			if(map.get("access_token")!=null && map.get("refresh_token")!=null && map.get("openid")!=null){
				params.put("accessToken", map.get("access_token"));
				params.put("refreshToken", map.get("refresh_token"));
				params.put("openId", map.get("openid"));
				NetHelper.post(Constant.LoginWxUrl, params,
						new SimpleSingleBeanNetHandler<Captcha>(LoginInputActivity.this) {
							@Override
							protected void onSuccess(Captcha bean) {
								if(bean.result.equals("0")){
									try {
										JSONObject jsonObject = new JSONObject(bean.info);
										WxCallbackInfo callbackInfo = GsonUtils
												.parseJSON(jsonObject.toString(),
														WxCallbackInfo.class);
										if( callbackInfo.getUserInfo().getPhoto_url()!=null){
											photo_url = callbackInfo.getUserInfo().getPhoto_url();
										}
										id = callbackInfo.getUserInfo().getId();
										if(callbackInfo.getUserInfo().getPhoto_url()!=null){
											username = callbackInfo.getUserInfo().getWxopenid_app();
										}
									    if( callbackInfo.getUserInfo().getNickname()!=null){
											nickname = callbackInfo.getUserInfo().getNickname();
										}
										if(callbackInfo.getIsFirstLogin()!=null && callbackInfo.getUserInfo().getWxopenid_app()!=null){
											registerHuanxin(callbackInfo.getUserInfo().getWxopenid_app(),"111111");
										}else if(callbackInfo.getIsFirstLogin()==null && callbackInfo.getUserInfo().getWxopenid_app()!=null){
											loginHuanxin(callbackInfo.getUserInfo().getWxopenid_app(),"111111");
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}else{
									ToastUtil.getInstance().showToast(LoginInputActivity.this, bean.message);
								}
							}
						});
		    }else{
				Toast.makeText(mContext, "授权失败",Toast.LENGTH_LONG).show();
			}
		}

		/**
		 * @desc 授权失败的回调
		 * @param platform 平台名称
		 * @param action 行为序号，开发者用不上
		 * @param t 错误原因
		 */
		@Override
		public void onError(SHARE_MEDIA platform, int action, Throwable t) {

			Toast.makeText(mContext, "失败：" + t.getMessage(),Toast.LENGTH_LONG).show();
		}

		/**
		 * @desc 授权取消的回调
		 * @param platform 平台名称
		 * @param action 行为序号，开发者用不上
		 */
		@Override
		public void onCancel(SHARE_MEDIA platform, int action) {
			Toast.makeText(mContext, "取消了", Toast.LENGTH_LONG).show();
		}
	};

	private void registerHuanxin(final String username,final String pwd){
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
						 runOnUiThread(new Runnable() {
							public void run() {
								Log.e("", "环信注册成功yyyyyyyy");
								if (!isFinishing())
									pd.dismiss();
								// 保存用户名
								DemoHelper.getInstance().setCurrentUserName(username);
								loginHuanxin(username,pwd);
								Toast.makeText(mContext.getApplicationContext(), st6, Toast.LENGTH_SHORT).show();
							}
						});
					} catch (final EaseMobException e) {
						 runOnUiThread(new Runnable() {
							public void run() {
								if (isFinishing())
									pd.dismiss();
								int errorCode=e.getErrorCode();
								if(errorCode== EMError.NONETWORK_ERROR){
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

	private void loginHuanxin(final String huanxin,final String huanxing_pwd){
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
						DemoHelper.getInstance().registerGroupAndContactListener();
						EMGroupManager.getInstance().loadAllGroups();
						EMChatManager.getInstance().loadAllConversations();
						EMChat.getInstance().setAutoLogin(true);
						MyApplication.logined = true;
						editor.putBoolean("token_logined", true);
						editor.putBoolean("logined", true);
						if (username != null && username != "" ) {
							editor.putString("user_name",username
							);
//								Encryption.encryptd(Constant.code_local, Constant.code_local, name)
							editor.putString("user_password",  "111111");
						}
//							Encryption.encryptd(Constant.code_local, Constant.code_local, password)
						editor.putString("username", username);
						editor.putString("user_id",String.valueOf(id));
						editor.commit();
						SPUtils.put(mContext, "headUrl", (String)photo_url);
						SPUtils.put(MyApplication.applicationContext,"headUrl",photo_url);
//							SPUtils.put(mContext, "signature", signature);
						SPUtils.put(mContext, "user_id", String.valueOf(id));
						SPUtils.put(mContext, "username", username);
						SPUtils.put(mContext,"nickname",nickname);
					    finish();
					}

					@Override
					public void onError(int arg0,
										String arg1) {
						LogUtils.e("环信登入失败："+arg1);

					}

					@Override
					public void onProgress(int arg0,
										   String arg1) {
						LogUtils.e("环信登入进度："+arg1);

					}
				});
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_login_input);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_logininput);

		umShareAPI =UMShareAPI.get(this);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		Intent intent1 = getIntent();
		third_click = false;
		try {
			key = intent1.getExtras().getInt("key");
		} catch (Exception e) {
			// TODO: handle exception
		}
		lia_instance = this;
		// 保存登录信息，开启第三方登录
		mContext = LoginInputActivity.this;
		sharedPreferences = this.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();
		weibo_sina = SinaWeiboUtil.getInstance(mContext);
		init();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
				imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
		return super.onTouchEvent(event);
	}

	private void init() {
		layout = (LinearLayout) findViewById(R.id.login_layout);
		layout.setOnClickListener(this);
		login_account_edit_clear = (ImageView) findViewById(R.id.login_account_edit_clear);
		login_account_edit_clear.setOnClickListener(this);
		// login_key = (ImageView) findViewById(R.id.login_key);
		login_key_edit_clear = (ImageView) findViewById(R.id.login_key_edit_clear);
		login_key_edit_clear.setOnClickListener(this);
		login_edit_account = (EditText) findViewById(R.id.login_edit_account);
		login_edit_account.addTextChangedListener(this);
		login_edit_account.setOnFocusChangeListener(this);
		login_edit_key = (EditText) findViewById(R.id.login_edit_key);
		login_edit_key.addTextChangedListener(this);
		login_edit_key.setOnFocusChangeListener(this);
		login_btn = (Button) findViewById(R.id.login_btn);
		login_btn.setOnClickListener(this);
		login_regist = (TextView) findViewById(R.id.login_regist);
		iv_weixin = (ImageView) findViewById(R.id.weinxin_login);
		iv_weixin.setOnClickListener(this);
		login_regist.setOnClickListener(this);
		login_forget = (TextView) findViewById(R.id.login_forget);
		login_forget.setOnClickListener(this);
		// scrollView = (ScrollView) findViewById(R.id.login_scroller);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.login_btn:
			// 登录

			switch (key) {
			case 1:// 播放记录跳转
				intent = new Intent(LoginInputActivity.this, RecordActivity.class);
				loginUtil = new LoginUtil(LoginInputActivity.this, login_edit_account.getText().toString(),
						login_edit_key.getText().toString(), intent, key);
				break;
			case 2:// 收藏跳转
				intent = new Intent(LoginInputActivity.this, CollectActivity.class);
				loginUtil = new LoginUtil(LoginInputActivity.this, login_edit_account.getText().toString(),
						login_edit_key.getText().toString(), intent, key);
				break;

			case 3:// 我的练习界面跳转
				intent = new Intent(LoginInputActivity.this, PracticeActivity.class);
				loginUtil = new LoginUtil(LoginInputActivity.this, login_edit_account.getText().toString(),
						login_edit_key.getText().toString(), intent, key);
				break;

			case 4:// 我的错题跳转
				intent = new Intent(LoginInputActivity.this, ErrorActivity.class);
				loginUtil = new LoginUtil(LoginInputActivity.this, login_edit_account.getText().toString(),
						login_edit_key.getText().toString(), intent, key);
				break;
			// 个人页返回
			case 5:
				loginUtil = new LoginUtil(LoginInputActivity.this, login_edit_account.getText().toString(),
						login_edit_key.getText().toString(), key);
				break;
			case 6:
				loginUtil = new LoginUtil(LoginInputActivity.this, login_edit_account.getText().toString(),
						login_edit_key.getText().toString(), key);
				break;
			case 13:
				loginUtil = new LoginUtil(LoginInputActivity.this, login_edit_account.getText().toString(),
						login_edit_key.getText().toString(), key);
				break;
			default:
				loginUtil = new LoginUtil(LoginInputActivity.this, login_edit_account.getText().toString(),
						login_edit_key.getText().toString(), key);
				break;
			}
			break;
		case R.id.login_account_edit_clear:
			login_edit_account.setText("");
			break;
		case R.id.login_key_edit_clear:
			login_edit_key.setText("");
			break;
		case R.id.login_regist:
			// third_party_layout.startAnimation(mShowAction);
			// third_party_layout.setVisibility(View.VISIBLE);

			if (NetUtil.isConnected(LoginInputActivity.this)) {
				intent = new Intent(LoginInputActivity.this, RegisterActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slider_right_in,
		                R.anim.anim_slider_left_out);
			} else {
				Toast.makeText(LoginInputActivity.this, "请打开您的网络连接", Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.login_forget:
			// third_party_layout.startAnimation(mHiddenAction);
			// third_party_layout.setVisibility(View.GONE);

			if (NetUtil.isConnected(LoginInputActivity.this)) {
				intent = new Intent(LoginInputActivity.this, GetCodeSmsActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slider_left_in,
	                    R.anim.anim_slider_right_out);
			} else {
				Toast.makeText(LoginInputActivity.this, "请打开您的网络连接", Toast.LENGTH_SHORT).show();
			}

			// Toast.makeText(this, "忘记密码", Toast.LENGTH_SHORT).show();
			break;
		case R.id.login_layout:
			// 隐藏输入键盘
			if (imm.isActive() && LoginInputActivity.this != null) {
				try {
					LoginInputActivity.this.getCurrentFocus();
					LoginInputActivity.this.getCurrentFocus().getWindowToken();
					imm.hideSoftInputFromWindow(LoginInputActivity.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				} catch (Exception e) {
					// TODO: handle exception
				}

				Log.d("123456", "隐藏键盘");
			}
			break;
		case R.id.back_search_logininput:
			if (imm.isActive() && LoginInputActivity.this != null) {
				try {
					LoginInputActivity.this.getCurrentFocus();
					LoginInputActivity.this.getCurrentFocus().getWindowToken();
					imm.hideSoftInputFromWindow(LoginInputActivity.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				} catch (Exception e) {
					// TODO: handle exception
				}

				Log.d("123456", "隐藏键盘");
			}
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					// 需要做的事:发送消息
					// LoginUtil.login_handlered = false;
					Log.d("345abc", "延时退出");
					Message msg_loop = mHandler.obtainMessage();
					msg_loop.what = 55;
					msg_loop.sendToTarget();
				}
			}, 50);
			break;
//		case R.id.qq_btn:
//			if (NetUtil.isConnected(LoginInputActivity.this) && !third_click) {
//				third_click = true;
//				onClickLogin();
//
//			} else if (!NetUtil.isConnected(LoginInputActivity.this)) {
//				Toast.makeText(LoginInputActivity.this, "请打开您的网络连接", Toast.LENGTH_SHORT).show();
//			} else if (third_click) {
//				Toast.makeText(LoginInputActivity.this, "正在打开QQ登陆授权页面，请稍后", Toast.LENGTH_SHORT).show();
//			}
//			break;
//		case R.id.sina_btn:
//			if (NetUtil.isConnected(LoginInputActivity.this) && !third_click) {
//				third_click = true;
//				logonToSina();
//
//			} else if (!NetUtil.isConnected(LoginInputActivity.this)) {
//				Toast.makeText(LoginInputActivity.this, "请打开您的网络连接", Toast.LENGTH_SHORT).show();
//			} else if (third_click) {
//				Toast.makeText(LoginInputActivity.this, "正在打开微博登陆授权页面，请稍后", Toast.LENGTH_SHORT).show();
//			}
//			break;
		case R.id.weinxin_login:
			if (NetUtil.isConnected(LoginInputActivity.this) && !third_click) {
				third_click = true;
//				logonToWeixin();
				umShareAPI.doOauthVerify(this, SHARE_MEDIA.WEIXIN, authListener);
			} else if (!NetUtil.isConnected(LoginInputActivity.this)) {
				Toast.makeText(LoginInputActivity.this, "请打开您的网络连接", Toast.LENGTH_SHORT).show();
			} else if (third_click) {
				Toast.makeText(LoginInputActivity.this, "正在打开微信登陆授权页面，请稍后", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}

	}



	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.login_edit_account:
			if (hasFocus && login_edit_account.getText().length() != 0) {
				login_account_edit_clear.setVisibility(View.VISIBLE);
			} else {
				login_account_edit_clear.setVisibility(View.GONE);
			}
			break;
		case R.id.login_edit_key:
			if (hasFocus && login_edit_key.getText().length() != 0) {
				login_key_edit_clear.setVisibility(View.VISIBLE);
			} else {
				login_key_edit_clear.setVisibility(View.GONE);
			}
		default:
			break;
		}

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		// Log.d("123321", "after触发");
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub
		// Log.d("123321", "before触发");
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		// Log.d("123321", "changed触发");
		if (s != null) {
			/*
			 * if (s.equals(login_edit_account.getText())) {
			 * 
			 * login_user.setBackgroundResource(R.drawable.
			 * login_user_hightlighted); } if
			 * (s.equals(login_edit_key.getText())) {
			 * login_key.setBackgroundResource(R.drawable.login_key_hightlighted
			 * ); }
			 */
			try {
				int id_c = getCurrentFocus().getId();
				switch (id_c) {
				case R.id.login_edit_account:
					if (s.length() != 0) {
						login_account_edit_clear.setVisibility(View.VISIBLE);
					} else {
						login_account_edit_clear.setVisibility(View.GONE);
					}
					break;
				case R.id.login_edit_key:
					if (s.length() != 0) {
						login_key_edit_clear.setVisibility(View.VISIBLE);
					} else {
						login_key_edit_clear.setVisibility(View.GONE);
					}
					break;
				default:
					break;

				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

	}
	public static Handler getHandler() {
		if (lHandler == null) {
			lHandler = new Handler();
		}
		return lHandler;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// Toast.makeText(mContext, requestCode+" "+resultCode+" ",
		// Toast.LENGTH_SHORT).show();
		// rennClient.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
		Log.d("1221", "onActivityResult==============" + requestCode);


	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// third_click = false;
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		third_click = false;
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void finish() {

		super.finish();
		try {
			third_click = false;
			handler.removeCallbacksAndMessages(null);
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (CoursesPlayActivity.pva_instance != null && !sharedPreferences.getBoolean("logined", false)) {
			CoursesPlayActivity.pva_instance.finish();
			// Toast.makeText(PlayVideoActivity.this, "", duration)
		}
		overridePendingTransition(R.anim.anim_slider_left_in,
                R.anim.anim_slider_right_out);
	}

}
