package org.soshow.beautyedu.activity;


import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.json.bean.Register;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.RegisterUtil;
import org.soshow.beautyedu.utils.TextStringUtils;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
 * 注册
 */
public class RegisterActivity extends BaseActivity implements OnFocusChangeListener, TextWatcher {
	private ImageView register_account_edit_clear, register_code_edit_clear, register_code1_edit_clear;
	private EditText register_edit_account, register_edit_code, register_edit_code1;
	private InputMethodManager imm;
	private RegisterUtil ru;
	private EditText code_local;
	private ImageView code_pic;
	private TextView code_change;
	private String code;
	// private boolean captcha_geted=false;
	private boolean captcha_getting = false;
	// private AlertDialog dialog_code;
	private EditText register_edit_ccaptcha;
	private TextView tv_getCode;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_registers);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_register);
//		captcha = (Button) findViewById(R.id.captcha);
		// xieyi = (TextView) findViewById(R.id.xieyi);
		// xieyi.setMovementMethod(LinkMovementMethod.getInstance());

		// 初始化各控件
		register_edit_account = (EditText) findViewById(R.id.register_edit_account);
		register_edit_account.addTextChangedListener(this);
		register_edit_account.setOnFocusChangeListener(this);
		register_edit_code = (EditText) findViewById(R.id.register_edit_code);
		register_edit_code.addTextChangedListener(this);
		register_edit_code.setOnFocusChangeListener(this);
		tv_getCode = (TextView)findViewById(R.id.captcha);
		register_edit_code1 = (EditText) findViewById(R.id.regist_et_phone_other);
		register_edit_code1.addTextChangedListener(this);
		register_edit_ccaptcha = (EditText) findViewById(R.id.register_edit_ccaptcha);
		register_edit_code1.setOnFocusChangeListener(this);
		register_account_edit_clear = (ImageView) findViewById(R.id.register_account_edit_clear);

		register_code_edit_clear = (ImageView) findViewById(R.id.register_code_edit_clear);
		register_code1_edit_clear = (ImageView) findViewById(R.id.register_code1_edit_clear);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}
    private void getCode(){
		String url_register = Constant.phpUrl + "/wap/api.php?action=USER_REGISTSTEP1";
		Map<String, String> params = new HashMap<String, String>();
		params.put("phone", register_edit_account.getText().toString());
		NetHelper.post(url_register,params,new SimpleSingleBeanNetHandler<Register>(this) {

			@Override
			protected void onSuccess(Register bean) {
				Log.e("", "注册返回的结果：result="+bean.result+bean.info);
				Toast.makeText(RegisterActivity.this, bean.message, Toast.LENGTH_SHORT).show();
				if(bean.result.equals("0")){
					TimeCount time = new TimeCount(60000, 1000, tv_getCode);
					time.start();//开启倒计时
				}
			}

			@Override
			protected void onError(int errorCode, String errorMsg) {

				Toast.makeText(RegisterActivity.this, "通信失败", Toast.LENGTH_SHORT).show();
			}
		});
	}
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_search_register:
			if (imm.isActive() && RegisterActivity.this != null) {
				imm.hideSoftInputFromWindow(RegisterActivity.this.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
			finish();
			break;
		case R.id.captcha://获取验证码
			if (tv_getCode.getText().toString().trim().equals("重新验证")
					|| tv_getCode.getText().toString().trim().equals("获取验证码")) {
				if (TextUtils.isEmpty(register_edit_account.getText().toString())) {
					Toast.makeText(RegisterActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
					return;
				} else if (!TextStringUtils.isMobileNO(register_edit_account.getText().toString())) {
					Toast.makeText(RegisterActivity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
					return;
				}
                getCode();
			}
			break;
		case R.id.register_account_edit_clear:
			register_edit_account.setText("");
			break;
		case R.id.register_code_edit_clear:
			register_edit_code.setText("");
			break;
		case R.id.register_code1_edit_clear:
			register_edit_code1.setText("");
			break;
		case R.id.btn_register1:
			ru = new RegisterUtil(RegisterActivity.this, register_edit_account.getText().toString(),
					register_edit_code.getText().toString(), register_edit_ccaptcha.getText().toString(),register_edit_code1.getText().toString());
			break;
		default:
			break;
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

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		// Log.d("123456", "其他焦点");
		switch (v.getId()) {
		case R.id.register_edit_account:
			if (hasFocus && register_edit_account.getText().length() != 0) {
				register_account_edit_clear.setVisibility(View.VISIBLE);
			} else {
				register_account_edit_clear.setVisibility(View.GONE);
			}
			break;
		case R.id.register_edit_code:
			if (hasFocus && register_edit_code.getText().length() != 0) {
				register_code_edit_clear.setVisibility(View.VISIBLE);
			} else {
				register_code_edit_clear.setVisibility(View.GONE);
			}
			break;
		case R.id.regist_et_phone_other:
			if (hasFocus && register_edit_code1.getText().length() != 0) {
				register_code1_edit_clear.setVisibility(View.VISIBLE);
			} else {
				register_code1_edit_clear.setVisibility(View.GONE);
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

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		if (s != null) {
			try {
				int id_c = getCurrentFocus().getId();
				switch (id_c) {
				case R.id.register_edit_account:
//					captcha.setBackgroundResource(R.drawable.captcha_btn_selector);
//					if (!captcha_getting) {
//						captcha.setClickable(true);
//					}
					if (s.length() != 0) {
						register_account_edit_clear.setVisibility(View.VISIBLE);
					} else {
						register_account_edit_clear.setVisibility(View.GONE);
					}
					break;
				case R.id.register_edit_code:
					if (s.length() != 0) {
						register_code_edit_clear.setVisibility(View.VISIBLE);
					} else {
						register_code_edit_clear.setVisibility(View.GONE);
					}
					break;
				case R.id.regist_et_phone_other:
					if (s.length() != 0) {
						register_code1_edit_clear.setVisibility(View.VISIBLE);
					} else {
						register_code1_edit_clear.setVisibility(View.GONE);
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
		super.finish();


		captcha_getting = false;
		try {
//			mHandler.removeCallbacksAndMessages(null);
			//mHandler = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}
