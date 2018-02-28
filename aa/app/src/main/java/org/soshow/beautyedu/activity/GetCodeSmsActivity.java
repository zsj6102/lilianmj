package org.soshow.beautyedu.activity;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.NetUtil;
import org.soshow.beautyedu.utils.CaptchaUtil;
import org.soshow.beautyedu.utils.Code;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.MyAlertDialog;
import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TokenManager;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 找回密码短信验证
 */
public class GetCodeSmsActivity extends BaseActivity implements OnFocusChangeListener, TextWatcher {
	private EditText getcode_edit_account, getcodesms_edit_ccaptcha;
	private ImageView getcode_account_edit_clear;
	private Button getcode_captcha;
	private int count = 100;
	private Timer timer;
	private SharedPreferences sp;
	 private Editor editor;
	// private String password;
	private String mcaptcha;
	private String mToken;
	private InputMethodManager imm;
	private String account;
	private EditText code_local;
	private ImageView code_pic;
	private TextView code_change;
	private String code;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:
				if (TokenManager.handlered) {
					Log.d("345abc", "gcs获取新token");
					TokenManager.handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
						// getInfo();
					} else {
						Toast.makeText(GetCodeSmsActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
							Log.d("345abc", "gcs循环等待");
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 2;
							msg_loop.sendToTarget();
						}
					}, 1000);

				}
				break;
			case 0:
				Log.d("1221", "Handler处理0");
				if (count < 1) {
					timer.cancel();
					getcode_captcha.setText("获取验证码");

					count = 100;
					getcode_captcha.setClickable(true);
					captcha_getting = false;
				} else {
					getcode_captcha.setText(count + "秒后重新获取");
					timer = new Timer();
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Log.d("1221", "Timer到時run");
							count--;
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 0;
							msg_loop.sendToTarget();

						}
					}, 1000);
				}

				break;
			case 99:
				Log.d("1221", "Handler处理99");
				captcha_getting = false;
				if (getcode_captcha != null) {
					getcode_captcha.setClickable(true);
				}
				break;
			case 101:
				Log.d("1221", "Handler处理101");
				captcha_getting = false;
				if (getcode_captcha != null) {
					getcode_captcha.setClickable(false);
					getcode_captcha.setBackgroundResource(R.drawable.shapes_captcha);
				}
				break;
			case 200:
				break;
			default:
				break;
			}
		}

	};
	public static GetCodeSmsActivity gcs_instance;
	private Dialog dialog;
	private boolean captcha_getting = false;
	// private String mToken;

	@Override
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_get_code_sms);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_getcode);
		getcode_edit_account = (EditText) findViewById(R.id.getcode_edit_account);
		getcode_edit_account.setOnFocusChangeListener(this);
		getcode_edit_account.addTextChangedListener(this);
		getcodesms_edit_ccaptcha = (EditText) findViewById(R.id.getcodesms_edit_ccaptcha);
		// getcode_edit_code = (EditText) findViewById(R.id.getcode_edit_code);
		// getcode_edit_code.setOnFocusChangeListener(this);
		// getcode_edit_code.addTextChangedListener(this);
		// getcode_edit_code1 = (EditText)
		// findViewById(R.id.getcode_edit_code1);
		// getcode_edit_code1.setOnFocusChangeListener(this);
		// getcode_edit_code1.addTextChangedListener(this);
	
		getcode_account_edit_clear = (ImageView) findViewById(R.id.getcode_account_edit_clear);
		// getcode_code_edit_clear = (ImageView)
		// findViewById(R.id.getcode_code_edit_clear);
		// getcode_code1_edit_clear = (ImageView)
		// findViewById(R.id.getcode_code1_edit_clear);
		getcode_captcha = (Button) findViewById(R.id.getcode_captcha);
		// btn_getcodesms = (Button) findViewById(R.id.btn_getcodesms);
		sp = GetCodeSmsActivity.this.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		gcs_instance = this;
		dialog = ProgressDialogUtil.createLoadingDialog(GetCodeSmsActivity.this, null, true, false);
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

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_getcode:
			if (imm.isActive() && GetCodeSmsActivity.this != null) {
				imm.hideSoftInputFromWindow(GetCodeSmsActivity.this.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
			break;
		case R.id.getcode_account_edit_clear:
			getcode_edit_account.setText("");
			break;

		case R.id.getcode_captcha:
			if (StringUtil.isEmpty(getcode_edit_account.getText().toString())) {
				Toast.makeText(GetCodeSmsActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
			} else if (!StringUtil.isPhone(getcode_edit_account.getText().toString())) {
				Toast.makeText(GetCodeSmsActivity.this, "该账号不存在", Toast.LENGTH_SHORT).show();
			} else if (!NetUtil.isConnected(GetCodeSmsActivity.this)) {
				Toast.makeText(GetCodeSmsActivity.this, "请打开您的网络连接", Toast.LENGTH_SHORT).show();
			} else {
				LinearLayout ll = (LinearLayout) LayoutInflater.from(GetCodeSmsActivity.this)
						.inflate(R.layout.dialog_code, null);
				code_local = (EditText) ll.findViewById(R.id.code_local);
				code_pic = (ImageView) ll.findViewById(R.id.code_pic);
				code_pic.setImageBitmap(org.soshow.beautyedu.utils.Code.getInstance().getBitmap());
				code = Code.getInstance().getCode();
				code_change = (TextView) ll.findViewById(R.id.code_change);
				code_change.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						code_pic.setImageBitmap(org.soshow.beautyedu.utils.Code.getInstance().getBitmap());
						code = Code.getInstance().getCode();
					}
				});

				Builder logout_confirm = new MyAlertDialog.Builder(GetCodeSmsActivity.this);
				logout_confirm.setTitle("请输入验证码").setView(ll).setCancelable(false)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int arg1) {

								// TODO Auto-generated method stub
								String ecode = code_local.getText().toString().trim();
								if (ecode == null || ecode.equals("")) {
									Toast.makeText(GetCodeSmsActivity.this, "没有填写验证码", Toast.LENGTH_SHORT).show();
									try {
										Field field = dialog.getClass().getSuperclass().getSuperclass()
												.getDeclaredField("mShowing");
										field.setAccessible(true);
										field.set(dialog, false);

									} catch (Exception e) {
										e.printStackTrace();
									}
									// code_pic.setImageBitmap(Code.getInstance().getBitmap());
									// code = Code.getInstance().getCode();
								} else if (!ecode.toLowerCase().equals(code.toLowerCase())) {
									Toast.makeText(GetCodeSmsActivity.this, "验证码填写不正确", Toast.LENGTH_SHORT).show();
									try {
										Field field = dialog.getClass().getSuperclass().getSuperclass()
												.getDeclaredField("mShowing");
										field.setAccessible(true);
										field.set(dialog, false);

									} catch (Exception e) {
										e.printStackTrace();
									}
									code_pic.setImageBitmap(Code.getInstance().getBitmap());
									code = Code.getInstance().getCode();
								} else {
									try {
										Field field = dialog.getClass().getSuperclass().getSuperclass()
												.getDeclaredField("mShowing");
										field.setAccessible(true);
										field.set(dialog, true);

									} catch (Exception e) {
										e.printStackTrace();
									}
									getcode_captcha.setClickable(false);
									captcha_getting = true;
									account = getcode_edit_account.getText().toString();
									CaptchaUtil.getCaptcha(getcode_edit_account.getText().toString(),
											GetCodeSmsActivity.this, handler);

								}

							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								try {
									Field field = dialog.getClass().getSuperclass().getSuperclass()
											.getDeclaredField("mShowing");
									field.setAccessible(true);
									field.set(dialog, true);

								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).show();

			}
			break;
		case R.id.btn_getcodesms:
			getCode();
			break;
		default:
			break;
		}
	}

	/*
	 * Handler mHandler = new Handler() {
	 * 
	 * @Override public void handleMessage(android.os.Message msg) { if (count <
	 * 1) { timer.cancel(); getcode_captcha.setText("获取验证码");
	 * getcode_captcha.setClickable(true); count = 100;
	 * getcode_captcha.setClickable(true); } else {
	 * getcode_captcha.setText(count + "秒后重新获取"); } }; };
	 */
	private void getCode() {
		if (StringUtil.isEmpty(getcode_edit_account.getText().toString())) {
			Toast.makeText(GetCodeSmsActivity.this, "请输入手机号", Toast.LENGTH_SHORT).show();
			// Log.d("345abc", "输入号码为" +
			// getcode_edit_account.getText().toString());
			return;
		}
		if (!StringUtil.isPhone(getcode_edit_account.getText().toString())) {
			Toast.makeText(GetCodeSmsActivity.this, "该账号不存在", Toast.LENGTH_SHORT).show();
			return;
		}
		mcaptcha = getcodesms_edit_ccaptcha.getText().toString();
		if (StringUtil.isEmpty(mcaptcha)) {
			Toast.makeText(GetCodeSmsActivity.this, "请输入正确的验证码", Toast.LENGTH_SHORT).show();
			return;
		}
		getTokenLocal();
		// Intent intent = new Intent(GetCodeSmsActivity.this,
		// GetCodeActivity.class);
		// intent.putExtra("account", account);
		// intent.putExtra("captcha",
		// getcodesms_edit_ccaptcha.getText().toString());
		// startActivity(intent);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.get_code, menu);
		return true;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.getcode_edit_account:
			if (hasFocus && getcode_edit_account.getText().length() != 0) {
				getcode_account_edit_clear.setVisibility(View.VISIBLE);
			} else {
				getcode_account_edit_clear.setVisibility(View.GONE);
			}
			break;
		// case R.id.getcode_edit_code:
		// Toast.makeText(GetCodeSmsActivity.this, "密码框获得焦点",
		// Toast.LENGTH_SHORT).show();
		// if (hasFocus && getcode_edit_code.getText().length() != 0) {
		// getcode_code_edit_clear.setVisibility(View.VISIBLE);
		// } else {
		// getcode_code_edit_clear.setVisibility(View.GONE);
		// }
		// break;
		// case R.id.getcode_edit_code1:
		// if (hasFocus && getcode_edit_code1.getText().length() != 0) {
		// getcode_code1_edit_clear.setVisibility(View.VISIBLE);
		// } else {
		// getcode_code1_edit_clear.setVisibility(View.GONE);
		// }
		// break;
		default:
			break;
		}

	}

	@Override
	public void afterTextChanged(Editable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence c, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if (c != null) {
			try {
				int id_c = getCurrentFocus().getId();
				switch (id_c) {
				case R.id.getcode_edit_account:
					getcode_captcha.setBackgroundResource(R.drawable.captcha_btn_selector);
					if (!captcha_getting) {
						getcode_captcha.setClickable(true);
					}
					if (c.length() != 0) {
						getcode_account_edit_clear.setVisibility(View.VISIBLE);
					} else {
						getcode_account_edit_clear.setVisibility(View.GONE);
					}
					break;
				// case R.id.getcode_edit_code:
				// if (c.length() != 0) {
				// getcode_code_edit_clear.setVisibility(View.VISIBLE);
				// } else {
				// getcode_code_edit_clear.setVisibility(View.GONE);
				// }
				// break;
				// case R.id.getcode_edit_code1:
				// if (c.length() != 0) {
				// getcode_code1_edit_clear.setVisibility(View.VISIBLE);
				// } else {
				// getcode_code1_edit_clear.setVisibility(View.GONE);
				// }
				// break;

				default:
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	private void getTokenLocal() {
		mToken = new TokenManager(GetCodeSmsActivity.this).getToken();
		if (mToken != null) {
			getInfo();
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}
	}

	private void getInfo() {

		String app_nonce = StringUtil.getPhoneIMEI(GetCodeSmsActivity.this);
		String url_check_verify = Constant.phpUrl + "/wap/api.php?action=VERIFY_CHECK_CODE&app_nonce=" + app_nonce
				+ "&tocken=" + mToken + "&mobile=" + account + "&check_code=" + StringUtil.md5(mcaptcha);
		dialog.show();
		Log.d("1221", "验证码" + url_check_verify);
		NetHelper.get(url_check_verify, new SimpleSingleBeanNetHandler<Captcha>(GetCodeSmsActivity.this) {

			@Override
			protected void onSuccess(Captcha bean) {
				// TODO Auto-generated method stub

				dialog.hide();
				Log.d("345abc", "验证码验证结果为" + bean.result + "   " + bean.info);
				if (!StringUtil.isEmpty(bean.result)) {
					try {
						int result = Integer.parseInt(bean.result);
						switch (result) {
						case 0:
							Toast.makeText(GetCodeSmsActivity.this, bean.info, Toast.LENGTH_SHORT).show();
							Intent intent = new Intent(GetCodeSmsActivity.this, GetCodeActivity.class);
							intent.putExtra("account", account);
							// intent.putExtra("index", 0);
							// intent.putExtra("captcha",
							// getcodesms_edit_ccaptcha.getText().toString());
							// getcode_edit_account.setText("");
							startActivity(intent);
							break;
						case 99:
							editor.putBoolean("token_logined", false);
							editor.remove("mToken");
							editor.commit();
							break;
						default:
							Toast.makeText(GetCodeSmsActivity.this, bean.info, Toast.LENGTH_SHORT).show();
							break;
						}
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(GetCodeSmsActivity.this, "验证码验证失败", Toast.LENGTH_SHORT).show();
					}

				}

			}

			@Override
			protected void onError(int errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				// super.onError(errorCode, errorMsg);
				dialog.hide();
				Toast.makeText(GetCodeSmsActivity.this, "通信失败，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
				Log.d("1221", "验证码验证错误结果为" + errorMsg);
			}

		});
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		try {
			if (timer != null) {
				timer.cancel();
				timer.purge();
			}
			if (dialog != null) {
				dialog.dismiss();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			handler.removeCallbacksAndMessages(null);
			//handler = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.finish();
		// if(dialog!=null){
		// dialog.dismiss();
		// }
	}

}
