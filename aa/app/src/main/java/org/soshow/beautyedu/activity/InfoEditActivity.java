package org.soshow.beautyedu.activity;

import java.net.URLEncoder;
import java.util.Timer;
import java.util.TimerTask;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.user.ChangeCodeActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.Encryption;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TokenManager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 个人信息修改
 */
public class InfoEditActivity extends BaseActivity implements OnFocusChangeListener, TextWatcher {
	private EditText getcode_edit_account, email_account;
	private ImageView getcode_account_edit_clear, email_account_clear;
	private Button change_code;
	// private int count = 100;
	// private Timer timer;
	private SharedPreferences sp;
	private Editor editor;
	// private String password;
	// private String mcaptcha;
	private String mToken;
	private InputMethodManager imm;
	// private String account;
	// private EditText code_local;
	// private ImageView code_pic;
	private TextView mobile_number;
	private String code;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:
				if (LoginUtil.login_handlered) {
					Log.d("345abc", "个人信息获取新token");
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
						name = getcode_edit_account.getText().toString().trim();
						;
						getInfo();
					} else {
						Toast.makeText(InfoEditActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
							Log.d("345abc", "个人信息循环等待");
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 2;
							msg_loop.sendToTarget();
						}
					}, 1000);

				}
				break;
			case 55:

				finish();
				break;
			case 66:
				Toast.makeText(InfoEditActivity.this, getCurrentFocus().toString(), Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}

	};
	public static InfoEditActivity gcs_instance;
	private Dialog dialog;
	private TextView title_name;
	private String name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_info_edit);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_getcode);
		title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText("个人信息");

		mobile_number = (TextView) findViewById(R.id.mobile_number);
		getcode_account_edit_clear = (ImageView) findViewById(R.id.getcode_account_edit_clear);
		email_account_clear = (ImageView) findViewById(R.id.email_account_clear);
		getcode_edit_account = (EditText) findViewById(R.id.getcode_edit_account);
		email_account = (EditText) findViewById(R.id.email_account);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// btn_getcodesms = (Button) findViewById(R.id.btn_getcodesms);
		sp = InfoEditActivity.this.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();

		gcs_instance = this;
		dialog = ProgressDialogUtil.createLoadingDialog(InfoEditActivity.this, null, true, false);

		if (!StringUtil.isEmpty(sp.getString("username", null))) {
			getcode_edit_account.setText(sp.getString("username", null));
		} else {
			getcode_edit_account.setHint("请输入昵称");
		}
		if (StringUtil.isPhone(
				Encryption.desEncryptd(Constant.code_local, Constant.code_local, sp.getString("user_name", null)))) {
			mobile_number.setText(
					Encryption.desEncryptd(Constant.code_local, Constant.code_local, sp.getString("user_name", null)));
		} else if (StringUtil.isPhone(sp.getString("cellphone", null))) {
			mobile_number.setText(sp.getString("cellphone", null));
		}
		if (!StringUtil.isEmpty(sp.getString("email", null))) {
			email_account.setText(sp.getString("email", null));
		}
		getcode_edit_account.setOnFocusChangeListener(this);
		getcode_edit_account.addTextChangedListener(this);
		email_account.setOnFocusChangeListener(this);
		email_account.addTextChangedListener(this);

		change_code = (Button) findViewById(R.id.change_code);
		if (StringUtil.isPhone(
				Encryption.desEncryptd(Constant.code_local, Constant.code_local, sp.getString("user_name", null)))) {
			change_code.setVisibility(View.VISIBLE);
		} else {
			change_code.setVisibility(View.GONE);
		}
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
			if (imm.isActive() && InfoEditActivity.this != null) {
				imm.hideSoftInputFromWindow(InfoEditActivity.this.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				// Toast.makeText(InfoEditActivity.this, "隐藏键盘",
				// Toast.LENGTH_SHORT).show();
				// Log.d("123456", "隐藏键盘");
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						// 需要做的事:发送消息
						// LoginUtil.login_handlered = false;
						Log.d("345abc", "延时返回");
						Message msg_loop = handler.obtainMessage();
						msg_loop.what = 55;
						msg_loop.sendToTarget();
					}
				}, 50);
			}
			// finish();
			break;
		case R.id.getcode_account_edit_clear:
			getcode_edit_account.setText("");
			break;
		case R.id.email_account_clear:
			email_account.setText("");
			break;
		case R.id.btn_getcodesms://保存
			getCode();
			break;
		case R.id.change_code:
			Intent intent = new Intent(InfoEditActivity.this, ChangeCodeActivity.class);
			intent.putExtra("account",
					Encryption.desEncryptd(Constant.code_local, Constant.code_local, sp.getString("user_name", null)));
			// intent.putExtra("index", 1);
			// intent.putExtra("captcha",
			// getcodesms_edit_ccaptcha.getText().toString());
			// getcode_edit_account.setText("");
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void getCode() {
		if (StringUtil.isEmpty(getcode_edit_account.getText().toString())) {
			Toast.makeText(InfoEditActivity.this, "您未输入昵称", Toast.LENGTH_SHORT).show();
			// Log.d("345abc", "输入号码为" +
			// getcode_edit_account.getText().toString());
			return;
		}
		// Toast.makeText(InfoEditActivity.this,
		// "邮箱格式不"+StringUtil.isEmail(email_account.getText().toString()),
		// Toast.LENGTH_SHORT).show();
		if ((!StringUtil.isEmpty(sp.getString("username", null))
				&& sp.getString("username", null).equals(getcode_edit_account.getText().toString()))
				&& ((StringUtil.isEmpty(email_account.getText().toString()))
						|| (!StringUtil.isEmpty(sp.getString("email", null))
								&& sp.getString("email", null).equals(email_account.getText().toString())))) {
			// Toast.makeText(InfoEditActivity.this, "您当前资料未改变",
			// Toast.LENGTH_SHORT).show();
			// Log.d("345abc", "输入号码为" +
			// getcode_edit_account.getText().toString());
			if (imm.isActive() && InfoEditActivity.this != null) {
				imm.hideSoftInputFromWindow(InfoEditActivity.this.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
				// Toast.makeText(InfoEditActivity.this, "隐藏键盘",
				// Toast.LENGTH_SHORT).show();
				// Log.d("123456", "隐藏键盘");
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						// 需要做的事:发送消息
						// LoginUtil.login_handlered = false;
						Log.d("345abc", "延时返回");
						Message msg_loop = handler.obtainMessage();
						msg_loop.what = 55;
						msg_loop.sendToTarget();
					}
				}, 50);
			}
			return;
		}
		if (!StringUtil.isEmpty(email_account.getText().toString())
				&& !StringUtil.isEmail(email_account.getText().toString())) {
			Toast.makeText(InfoEditActivity.this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
			// Log.d("345abc", "输入号码为" +
			// getcode_edit_account.getText().toString());
			return;
		}

		getTokenLocal();

	}

	private void getTokenLocal() {
		mToken = new TokenManager(InfoEditActivity.this).getToken();
		if (mToken != null) {
			name = getcode_edit_account.getText().toString().trim();

			getInfo();
		} else {
			LoginUtil.login_handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}
	}

	private void getInfo() {
		try {
			String app_nonce = StringUtil.getPhoneIMEI(InfoEditActivity.this);
			// String name_handlerd = name.replaceAll(" ",
			// "%20").replaceAll("<",
			// "%3C").replaceAll(">", "%3E")
			// .replaceAll("\n", "%0A").replaceAll("\r", "%0d").replaceAll("#",
			// "%23");
			String name_handlerd = URLEncoder.encode(name);
			String url_myrecord = null;
			if (StringUtil.isEmpty(email_account.getText().toString())
					|| !StringUtil.isEmail(email_account.getText().toString())) {
				url_myrecord = Constant.phpUrl + "/wap/api.php?action=GET_MODIFY_NICKNAME&tocken=" + mToken
						+ "&app_nonce=" + app_nonce + "&nickname=" + name_handlerd;
			} else {
				url_myrecord = Constant.phpUrl + "/wap/api.php?action=GET_MODIFY_NICKNAME&tocken=" + mToken
						+ "&app_nonce=" + app_nonce + "&nickname=" + name_handlerd + "&email="
						+ URLEncoder.encode(email_account.getText().toString());
			}
			Log.d("345abc", "修改昵称url=        " + url_myrecord);
			dialog.show();
			NetHelper.get(url_myrecord, new SimpleSingleBeanNetHandler<Captcha>(this) {
				@Override
				protected void onSuccess(Captcha bean) {
					dialog.hide();
					// int result = 999;
					try {
						int result = Integer.parseInt(bean.result);
						switch (result) {
						case 1:
							Toast.makeText(InfoEditActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
							editor.putString("username", name);
							editor.putString("email", email_account.getText().toString());
							editor.commit();
							try {
								if (dialog != null) {
									dialog.dismiss();
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
							finish();
							break;
						case 99:
							Toast.makeText(InfoEditActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
							editor.putBoolean("token_logined", false);
							editor.remove("mToken");
							editor.commit();
							break;
						case 100:
							if (sp.getBoolean("logined", false)) {
								Toast.makeText(InfoEditActivity.this, "网络错误，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
								editor.putBoolean("token_logined", false);
								editor.putBoolean("logined", false);
								MyApplication.logined = false;
								new LoginUtil(InfoEditActivity.this, 20);
							}
							break;
						default:
							Toast.makeText(InfoEditActivity.this, bean.info, Toast.LENGTH_SHORT).show();
							break;

						}
					} catch (Exception e) {
						// TODO: handle exception
						Toast.makeText(InfoEditActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
					}

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					// super.onError(errorCode, errorMsg);
					dialog.hide();
					Log.d("345abc", "信息编辑错误=" + errorMsg);
					//// if (errorMsg.contains("Illegal character")) {
					// Toast.makeText(InfoEditActivity.this, "请输入正常文字",
					//// Toast.LENGTH_SHORT).show();
					// } else {
					Toast.makeText(InfoEditActivity.this, "修改失败，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
					// }

				}

			});
		} catch (Exception e) {
			// TODO: handle exception
		}

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
				// Toast.makeText(InfoEditActivity.this,
				// getcode_edit_account.getText(), Toast.LENGTH_SHORT).show();
				getcode_account_edit_clear.setVisibility(View.VISIBLE);
			} else {
				getcode_account_edit_clear.setVisibility(View.GONE);
			}
			break;
		case R.id.email_account:
			if (hasFocus && email_account.getText().length() != 0) {
				// Toast.makeText(InfoEditActivity.this,
				// email_account.getText(), Toast.LENGTH_SHORT).show();
				email_account_clear.setVisibility(View.VISIBLE);
			} else {
				email_account_clear.setVisibility(View.GONE);
			}
			break;
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
					if (c.length() != 0) {

						getcode_account_edit_clear.setVisibility(View.VISIBLE);
					} else {
						getcode_account_edit_clear.setVisibility(View.GONE);
					}
					break;
				case R.id.email_account:
					if (c.length() != 0) {

						email_account_clear.setVisibility(View.VISIBLE);
					} else {
						email_account_clear.setVisibility(View.GONE);
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

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		try {
			if (dialog != null) {
				dialog.dismiss();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			handler.removeCallbacksAndMessages(null);
			// handler = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.finish();

	}

}
