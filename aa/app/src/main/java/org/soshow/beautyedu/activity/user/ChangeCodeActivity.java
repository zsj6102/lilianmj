package org.soshow.beautyedu.activity.user;

import java.util.Timer;
import java.util.TimerTask;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.Register;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.Encryption;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.MyAlertDialog;
import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TokenManager;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
//import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 修改密码页面
 */
public class ChangeCodeActivity extends BaseActivity implements OnFocusChangeListener, TextWatcher {
	private EditText getcode_edit_code, getcode_edit_code1, previous_edit_code;
	private ImageView getcode_code_edit_clear, getcode_code1_edit_clear, previous_code_edit_clear;
	private TextView title_name;
	// private Button btn_getcodesms;
	// private int count = 100;
	// private Timer timer;
	private SharedPreferences sp;
	private Editor editor;
	private String password;
	private String account;
	// private String mcaptcha;
	private String mToken;
	private InputMethodManager imm;
	private Dialog dialog;
	// private MyCount mc;
	// private int index;
//	private Handler handler = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//			switch (msg.what) {
//			case 2:
//				if (LoginUtil.login_handlered) {
//					Log.d("345abc", "获取新token-changeCode");
//					LoginUtil.login_handlered = false;
//					mToken = sp.getString("mToken", null);
//					if (mToken != null) {
//						getInfo();
//					} else {
//						Toast.makeText(ChangeCodeActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
//					}
//				} else {
//					new Timer().schedule(new TimerTask() {
//						@Override
//						public void run() {
//							// 需要做的事:发送消息
//							// LoginUtil.login_handlered = false;
//							Log.d("345abc", "循环等待-changeCode");
//							Message msg_loop = handler.obtainMessage();
//							msg_loop.what = 2;
//							msg_loop.sendToTarget();
//						}
//					}, 1000);
//
//				}
//				break;
//			case 3:
//
//				Builder bd6 = new MyAlertDialog.Builder(ChangeCodeActivity.this);
//				bd6.setTitle("升级提示").setMessage("验证码已过期，请重新获取后修改密码").setPositiveButton("确定",
//						new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface arg0, int arg1) {
//						// TODO Auto-generated method stub
//						try {
//							if (dialog != null) {
//								dialog.dismiss();
//							}
//						} catch (Exception e) {
//							// TODO: handle exception
//						}
//						finish();
//						overridePendingTransition(R.anim.anim_slider_left_in,
//			                    R.anim.anim_slider_right_out);
//					}
//				});
//				final Dialog dialog6 = bd6.show();
//				dialog6.setCancelable(false);
//
//				break;
//			default:
//				break;
//			}
//		}
//
//	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_change_code);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_getcode);
		title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText("修改密码");
		previous_edit_code = (EditText) findViewById(R.id.previous_edit_code);
		previous_edit_code.setOnFocusChangeListener(this);
		previous_edit_code.addTextChangedListener(this);
		getcode_edit_code = (EditText) findViewById(R.id.getcode_edit_code);
		getcode_edit_code.setOnFocusChangeListener(this);
		getcode_edit_code.addTextChangedListener(this);
		getcode_edit_code1 = (EditText) findViewById(R.id.getcode_edit_code1);
		getcode_edit_code1.setOnFocusChangeListener(this);
		getcode_edit_code1.addTextChangedListener(this);
		// getcode_account_edit_clear = (ImageView)
		// findViewById(R.id.getcode_account_edit_clear);
		previous_code_edit_clear = (ImageView) findViewById(R.id.previous_code_edit_clear);
		getcode_code_edit_clear = (ImageView) findViewById(R.id.getcode_code_edit_clear);
		getcode_code1_edit_clear = (ImageView) findViewById(R.id.getcode_code1_edit_clear);
		// getcode_captcha = (Button) findViewById(R.id.getcode_captcha);
		// btn_getcodesms = (Button) findViewById(R.id.btn_getcodesms);
		sp = ChangeCodeActivity.this.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		Intent intent = getIntent();
		account = intent.getStringExtra("account");

		// mc = new MyCount(1800000, 1800000);
		dialog = ProgressDialogUtil.createLoadingDialog(ChangeCodeActivity.this, "修改中", true, false);
		// mcaptcha = StringUtil.md5(intent.getStringExtra("captcha"));
		// Log.d("123456", "账号为" + account);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
				try {
					ChangeCodeActivity.this.getCurrentFocus();
					ChangeCodeActivity.this.getCurrentFocus().getWindowToken();
					imm.hideSoftInputFromWindow(ChangeCodeActivity.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		}
		return super.onTouchEvent(event);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_getcode:
			if (imm.isActive() && ChangeCodeActivity.this != null) {
				try {
					ChangeCodeActivity.this.getCurrentFocus();
					ChangeCodeActivity.this.getCurrentFocus().getWindowToken();
					imm.hideSoftInputFromWindow(ChangeCodeActivity.this.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
			break;
		// case R.id.getcode_account_edit_clear:
		// getcode_edit_account.setText("");
		// break;
		case R.id.getcode_code_edit_clear:
			getcode_edit_code.setText("");
			break;
		case R.id.getcode_code1_edit_clear:
			getcode_edit_code1.setText("");
			break;
		case R.id.previous_code_edit_clear:
			previous_edit_code.setText("");
			break;
		// case R.id.getcode_captcha:
		// if (StringUtil.isEmpty(getcode_edit_account.getText().toString())
		// || !StringUtil.isPhone(getcode_edit_account.getText().toString())) {
		// Toast.makeText(GetCodeActivity.this, "请输入正确的手机号",
		// Toast.LENGTH_SHORT).show();
		// } else {
		// getcode_captcha.setClickable(false);
		// timer = new Timer();
		// timer.schedule(new TimerTask() {

		// @Override
		// public void run() {
		// // TODO Auto-generated method stub
		// mHandler.sendEmptyMessage(0);
		// count--;
		// }
		// }, 0, 1000);
		// CaptchaUtil.getCaptcha(getcode_edit_account.getText().toString(),
		// GetCodeActivity.this);
		// }
		// break;
		case R.id.btn_getcodesms:
			getCode();
			break;
		default:
			break;
		}
	}

	// Handler mHandler = new Handler() {
	// @Override
	// public void handleMessage(android.os.Message msg) {
	// if (count < 1) {
	// timer.cancel();
	// getcode_captcha.setText("获取验证码");
	// getcode_captcha.setClickable(true);
	// count = 100;
	// getcode_captcha.setClickable(true);
	// } else
	// getcode_captcha.setText(count + "秒后重新获取");
	// };
	// };

	private void getCode() {
		// if (StringUtil.isEmpty(getcode_edit_account.getText().toString())
		// || !StringUtil.isPhone(getcode_edit_account.getText().toString())) {
		// Toast.makeText(GetCodeActivity.this, "请输入正确的手机号",
		// Toast.LENGTH_SHORT).show();
		// Log.d("345abc", "输入号码为" + getcode_edit_account.getText().toString());
		// return;
		// }
		password = getcode_edit_code.getText().toString().trim();
		if (StringUtil.isEmpty(previous_edit_code.getText().toString())
				|| previous_edit_code.getText().toString().trim().length() < 6) {
			Toast.makeText(ChangeCodeActivity.this, "请输入您当前的不小于6位数密码", Toast.LENGTH_SHORT).show();
			return;
		}
		if (StringUtil.isEmpty(password)) {
			Toast.makeText(ChangeCodeActivity.this, "请输入新密码", Toast.LENGTH_SHORT).show();
			return;
		}
		if (password.trim().length() < 6) {
			Toast.makeText(ChangeCodeActivity.this, "新密码最低为6位", Toast.LENGTH_SHORT).show();
			return;
		}
		if (StringUtil.isEmpty(getcode_edit_code1.getText().toString())) {
			Toast.makeText(ChangeCodeActivity.this, "请确认新密码", Toast.LENGTH_SHORT).show();
			return;
		}
		if (!password.equals(getcode_edit_code1.getText().toString())) {
			Toast.makeText(ChangeCodeActivity.this, "新密码两次输入不同，请重新输入", Toast.LENGTH_SHORT).show();
			return;
		}

		// mcaptcha = sp.getString("captcha_md5", null);
		// if (StringUtil.isEmpty(getcodesms_edit_ccaptcha.getText().toString())
		// ||
		// !StringUtil.md5(getcodesms_edit_ccaptcha.getText().toString()).equals(mcaptcha))
		// {
		// Toast.makeText(GetCodeActivity.this, "请输入正确的验证码",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }

//		getTokenLocal();

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
		// case R.id.getcode_edit_account:
		// if (hasFocus && getcode_edit_account.getText().length() != 0) {
		// getcode_account_edit_clear.setVisibility(View.VISIBLE);
		// } else {
		// getcode_account_edit_clear.setVisibility(View.GONE);
		// }
		// break;
		case R.id.getcode_edit_code:
			// Toast.makeText(GetCodeActivity.this, "密码框获得焦点",
			// Toast.LENGTH_SHORT).show();
			if (hasFocus && getcode_edit_code.getText().length() != 0) {
				getcode_code_edit_clear.setVisibility(View.VISIBLE);
			} else {
				getcode_code_edit_clear.setVisibility(View.GONE);
			}
			break;
		case R.id.getcode_edit_code1:
			if (hasFocus && getcode_edit_code1.getText().length() != 0) {
				getcode_code1_edit_clear.setVisibility(View.VISIBLE);
			} else {
				getcode_code1_edit_clear.setVisibility(View.GONE);
			}
			break;
		case R.id.previous_edit_code:
			if (hasFocus && previous_edit_code.getText().length() != 0) {
				previous_code_edit_clear.setVisibility(View.VISIBLE);
			} else {
				previous_code_edit_clear.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}

	}

//	private void getTokenLocal() {
//		mToken = new TokenManager(ChangeCodeActivity.this).getToken();
//		if (mToken != null) {
//			getInfo();
//		} else {
//			LoginUtil.login_handlered = false;
//			Message msg_loop = handler.obtainMessage();
//			msg_loop.what = 2;
//			msg_loop.sendToTarget();
//		}
//	}

	private void getInfo() {
		try {
			String app_nonce = StringUtil.getPhoneIMEI(ChangeCodeActivity.this);
			String data = Encryption.encryptd(Constant.code, Constant.code,
					previous_edit_code.getText().toString().trim());
			String data_new = Encryption.encryptd(Constant.code, Constant.code, password);

			String url_getcode = Constant.phpUrl + "/wap/api.php?action=MODIFY_PWD&app_nonce=" + app_nonce + "&mobile="
					+ account + "&tocken=" + mToken + "&data=" + data + "&data_new=" + data_new;
			Log.d("123456", "修改密码地址为" + url_getcode);
			try {
				if (dialog != null) {
					dialog.show();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			NetHelper.get(url_getcode, new SimpleSingleBeanNetHandler<Register>(ChangeCodeActivity.this) {

				@Override
				protected void onSuccess(Register bean) {
					// TODO Auto-generated method stub
					// String id = bean.id;
					// tManager = new TokenManager(mContext);
					// tManager.setUID(id);
					Log.d("345abc", bean.info);
					dialog.hide();
					try {
						int result = Integer.parseInt(bean.result);
						switch (result) {
						case 0:
							Toast.makeText(ChangeCodeActivity.this, "修改密码成功", Toast.LENGTH_SHORT).show();
							// editor.remove("captcha_md5");
							// editor.commit();
							editor.putBoolean("logined", false);
							MyApplication.logined = false;
							editor.remove("mToken");
							editor.remove("user_password");
							editor.putString("user_password",
									Encryption.encryptd(Constant.code_local, Constant.code_local, password));
							editor.putBoolean("token_logined", false);
							editor.commit();
							new TokenManager(ChangeCodeActivity.this).tokenGet();

							// new LoginUtil(ChangeCodeActivity.this, 13);
							try {
								if (dialog != null) {
									dialog.dismiss();
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
							finish();
							overridePendingTransition(R.anim.anim_slider_left_in,
				                    R.anim.anim_slider_right_out);
							break;

						case 99:
							// Toast.makeText(GetCodeActivity.this, bean.info,
							// Toast.LENGTH_SHORT).show();
							// editor.remove("captcha_md5");
							// editor.commit();
							editor.putBoolean("token_logined", false);
							editor.remove("mToken");
							editor.commit();
							break;
						default:
							Toast.makeText(ChangeCodeActivity.this, bean.info, Toast.LENGTH_SHORT).show();
							editor.remove("captcha_md5");
							editor.commit();
							break;
						}
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(ChangeCodeActivity.this, "修改密码失败", Toast.LENGTH_SHORT).show();
					}

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					/*
					 * if (LoginActivity._instace != null) {
					 * LoginActivity._instace.finish(); }
					 * Toast.makeText(mContext, "通信失败",
					 * Toast.LENGTH_SHORT).show(); if
					 * (LoginInputActivity.lia_instance == null) {
					 * //mContext.startActivity(intent); } RegisterActivity ra =
					 * (RegisterActivity) mContext; ra.finish();
					 */
					dialog.hide();
					Toast.makeText(ChangeCodeActivity.this, "通信失败", Toast.LENGTH_SHORT).show();
				}

			});

		} catch (Exception e) {
			// TODO: handle exception
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
				// case R.id.getcode_edit_account:
				// if (c.length() != 0) {
				// getcode_account_edit_clear.setVisibility(View.VISIBLE);
				// } else {
				// getcode_account_edit_clear.setVisibility(View.GONE);
				// }
				// break;
				case R.id.getcode_edit_code:
					if (c.length() != 0) {
						getcode_code_edit_clear.setVisibility(View.VISIBLE);
					} else {
						getcode_code_edit_clear.setVisibility(View.GONE);
					}
					break;
				case R.id.getcode_edit_code1:
					if (c.length() != 0) {
						getcode_code1_edit_clear.setVisibility(View.VISIBLE);
					} else {
						getcode_code1_edit_clear.setVisibility(View.GONE);
					}
					break;
				case R.id.previous_edit_code:
					if (c.length() != 0) {
						previous_code_edit_clear.setVisibility(View.VISIBLE);
					} else {
						previous_code_edit_clear.setVisibility(View.GONE);
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

	/*
	 * class MyCount extends CountDownTimer { public MyCount(long
	 * millisInFuture, long countDownInterval) { super(millisInFuture,
	 * countDownInterval); }
	 * 
	 * @Override public void onFinish() { Message msg_loop =
	 * handler.obtainMessage(); msg_loop.what = 3; msg_loop.sendToTarget(); //
	 * tv.setText("finish"); }
	 * 
	 * @Override public void onTick(long millisUntilFinished) { //
	 * tv.setText("请等待30秒(" + millisUntilFinished / 1000 + ")..."); //
	 * Toast.makeText(NewActivity.this, millisUntilFinished / 1000 + "", //
	 * Toast.LENGTH_LONG).show();// toast有显示时间延迟 } }
	 */
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		// super.finish();
		try {
			if (dialog != null) {
				dialog.dismiss();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
//		try {
//			handler.removeCallbacksAndMessages(null);
//			//handler = null;
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		super.finish();
		// if (mc != null) {
		// mc.cancel();
		// }

	}

}
