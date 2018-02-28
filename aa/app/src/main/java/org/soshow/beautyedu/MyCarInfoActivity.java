package org.soshow.beautyedu;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MyCarInfoActivity extends BaseActivity implements OnClickListener {

	private SharedPreferences sp;
	private Editor editor;
	private String app_nonce;
	private String mToken;
	private TextView title;
	private EditText etCardNum;
	private EditText etName;
	private EditText etPhone;
	private TextView tvBankName;
	private TextView tvBind;
	private Dialog dialog;
	private String inputText;//银行卡帐号输入内容
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:// token获取
				if (LoginUtil.login_handlered) {
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
						addCard();
					} else {
						Toast.makeText(MyCarInfoActivity.this, "网络不佳，请稍后再试",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
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
	private boolean bankError;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_my_car_info);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		iniView();
	}

	private void iniView() {
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();

		title = (TextView) findViewById(R.id.title_name);
		title.setText(getResources().getString(R.string.add_bank));
		
		etCardNum = (EditText) findViewById(R.id.bind_bank_et_cardNumber);
		etName = (EditText) findViewById(R.id.bind_bank_et_name);
		etPhone = (EditText) findViewById(R.id.bind_bank_et_phone);
		tvBankName = (TextView) findViewById(R.id.bind_bank_tv_bank_name);
		tvBankName.setOnClickListener(this);
		tvBind = (TextView) findViewById(R.id.add_car_tv_commit);
		tvBind.setOnClickListener(this);
		etCardNum.addTextChangedListener(new EdittextChange());
		etCardNum.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				LogUtils.e("获取到焦点="+hasFocus);
				inputText = etCardNum.getText().toString().trim();
				if(!hasFocus&&inputText.length()>=13){
					getActribute();
				}else{
					bankError = true;
					tvBankName.setText(getResources().getString(R.string.banks_name));
				}
			}
		});
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_search_logininput:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
			break;
		case R.id.bind_bank_tv_bank_name:
			break;
		case R.id.add_car_tv_commit:
			getTokenLocal();
			break;
		default:
			break;
		}

	}
	
	
	
	private void getTokenLocal() {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(MyCarInfoActivity.this).getToken();
		if (mToken != null) {
			addCard();
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}
	
	/*
	 * 添加银行卡
	 */
	private void addCard(){
		dialog = ProgressDialogUtil.createLoadingDialog(this, null, true, false);
		String bank_number = etCardNum.getText().toString().trim();
		String realname = etName.getText().toString().trim();
		String mobile = etPhone.getText().toString().trim();
		String bank_name = tvBankName.getText().toString().trim();
		if(bank_number.equals("")){
			ToastUtil.getInstance().showToast(this, R.string.input_card_number);
			return;
		}
		if(bankError){
			ToastUtil.getInstance().showToast(this, R.string.error_bank);
			return;
		}
		if(realname.equals("")){
			ToastUtil.getInstance().showToast(this, R.string.input_name);
			return;
		}
		if(mobile.equals("")){
			ToastUtil.getInstance().showToast(this, R.string.input_phone);
			return;
		}
		if(bank_name.equals("")){
			ToastUtil.getInstance().showToast(this, R.string.input_bank_name);
			tvBankName.setText(getResources().getString(R.string.banks_name));
			return;
		}
		dialog.show();
		String url_add_bank = Constant.BANK_CARD+"&tocken="+mToken+"&app_nonce="+app_nonce+"&bank_number="+bank_number+"&bank_name="+bank_name+"&realname="+realname+"&mobile="+mobile;
		LogUtils.e("添加绑定银行卡："+url_add_bank);
		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);
		params.put("app_nonce", app_nonce);
		params.put("bank_number", bank_number);
		params.put("realname", realname);
		params.put("mobile", mobile);
		params.put("bank_name", bank_name);
		NetHelper.post(Constant.BANK_CARD ,params , new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				dialog.hide();
				ToastUtil.getInstance().showToast(MyCarInfoActivity.this, bean.message);
				setResult(RESULT_OK);
				finish();
				overridePendingTransition(R.anim.anim_slider_left_in,
	                    R.anim.anim_slider_right_out);
			}
		});
	}
	
	/*
	 * 获取银行卡的归属地
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                	 */
	private void getActribute(){
		String cardnum = etCardNum.getText().toString().trim();
		String url_actribute = Constant.CHECK_CARD_INFO+"&cardnum="+cardnum ;
		LogUtils.e("银行卡的归属地url="+url_actribute);
		NetHelper.get(url_actribute ,new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				LogUtils.e("bean.result="+bean.result);
				try {
					if(bean.result.equals("0")){
						String bankname = new JSONObject(bean.info).getString("bankname");
						tvBankName.setText(bankname);
						tvBankName.setFocusable(false);
						bankError = false;
					}else{
						ToastUtil.getInstance().showToast(MyCarInfoActivity.this, bean.message);
						tvBankName.setText(getResources().getString(R.string.banks_name));
						bankError = true;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		});
	}
	
	/*
	 * 输入银行卡号监听
	 */
	private class EdittextChange implements TextWatcher{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			inputText = s.toString().trim();
		}
		
	}
}
