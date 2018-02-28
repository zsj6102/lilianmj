package org.soshow.beautyedu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.bean.BankInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.SPUtils;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CashActivity extends BaseActivity implements OnClickListener {

	public static String TYPE = "cash";
	private SharedPreferences sp;
	private Editor editor;
	private String app_nonce;
	private String mToken;
	private List<BankInfo> bankInfos;
	private String icon;
	
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
						getCarInfo();
					} else {
						Toast.makeText(CashActivity.this, "网络不佳，请稍后再试",
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
	private TextView tvName;
	private TextView tvNumber;
	private TextView tvPhone;
	private TextView tvRemainMoney;
	private EditText etMoney;
	protected String bank_card_id;
	private LinearLayout loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_cash);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		iniView();
		getTokenLocal();
	}

	private void iniView() {
		bankInfos = new ArrayList<BankInfo>();
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();

		loading = (LinearLayout) findViewById(R.id.linear_load);
		TextView title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText("提现");
		tvName = (TextView) findViewById(R.id.cash_tv_name);
		tvNumber = (TextView) findViewById(R.id.cash_tv_cardNumber);
		tvPhone = (TextView) findViewById(R.id.cash_tv_phone);
		etMoney = (EditText) findViewById(R.id.cash_et_money);
		tvRemainMoney = (TextView) findViewById(R.id.tv_remain_money);
		findViewById(R.id.cash_tv_all_money).setOnClickListener(this);
		findViewById(R.id.cash_tv_commit).setOnClickListener(this);
		findViewById(R.id.choose_other_card).setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_search_logininput:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
			break;
		case R.id.cash_tv_all_money://全部提现提现
			etMoney.setText(icon);
			break;
		case R.id.cash_tv_commit://提现
			getCash();
			break;
		case R.id.choose_other_card://选择其他银行卡
			Intent intent = new Intent(this, MyCardListActivity.class);
			intent.putExtra("type_from", TYPE);
			startActivityForResult(intent, 1);
			overridePendingTransition(R.anim.anim_slider_right_in,
	                R.anim.anim_slider_left_out);
			break;

		default:
			break;
		}

	}
	
	private void getTokenLocal() {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(this).getToken();
		if (mToken != null) {
			loading.setVisibility(View.VISIBLE);
			getCarInfo();
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}

	/*
	 * 获取我的银行卡
	 */
	private void getCarInfo(){
		String url_bank = Constant.BANK_CARD+"&tocken="+mToken+"&app_nonce="+app_nonce;
		LogUtils.e("获取银行卡信息："+url_bank);
		NetHelper.get(url_bank , new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				try {
					if(bean.result.equals("0")){
						String info = bean.info;
						LogUtils.e("银行卡信息info="+bean.info);
							JSONArray jsonData = new JSONArray(info);
							for (int i = 0; i < jsonData.length(); i++) {
								JSONObject obj = (JSONObject) jsonData.get(i);
								BankInfo bankInfo = GsonUtils.parseJSON(obj.toString(), BankInfo.class);
								bankInfos.add(bankInfo);
							}
							
							if(bankInfos.size()>0){
								BankInfo bankInfo = bankInfos.get(0);
								tvName.setText(bankInfo.getRealname());
								tvNumber.setText(bankInfo.getBank_number());
								tvPhone.setText(bankInfo.getMobile());
								bank_card_id = bankInfo.getBank_card_id();
								icon = (String) SPUtils.get(CashActivity.this, "icon", "");
								tvRemainMoney.setText(getResources().getString(R.string.current_money)+icon+getResources().getString(R.string.unit));
							}else{
								Intent intent = new Intent(CashActivity.this, MyCarInfoActivity.class);
								intent.putExtra("type", TYPE);
								startActivityForResult(intent, 0);
								overridePendingTransition(R.anim.anim_slider_right_in,
						                R.anim.anim_slider_left_out);
							}
							
					}else{
						ToastUtil.getInstance().showToast(CashActivity.this, bean.info);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				loading.setVisibility(View.GONE);
				
			}
		});
	}
	
	/*
	 * 提现操作
	 */
	private void getCash(){
		String cash = etMoney.getText().toString().trim();
		String url_bank = Constant.TAKE_CASH+"&tocken="+mToken+"&app_nonce="+app_nonce+"&cash="+cash+"&bank_card_id="+bank_card_id;
		LogUtils.e("提现操作："+url_bank);
		if(cash.equals("")){
			ToastUtil.getInstance().showToast(CashActivity.this, getResources().getString(R.string.input_money));
			return;
		}
		double money = Double.valueOf(cash);
		if(money == 0){
			ToastUtil.getInstance().showToast(CashActivity.this, getResources().getString(R.string.tip_no_money));
			return;
		}
		double remain_money = Double.valueOf(icon);
		if(money > remain_money){
			ToastUtil.getInstance().showToast(CashActivity.this, getResources().getString(R.string.no_enough_money));
			return;
		}     
		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);
		params.put("app_nonce", app_nonce);
		params.put("cash", cash);
		params.put("bank_card_id", bank_card_id);
		NetHelper.post(url_bank , params , new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				LogUtils.e("提现操作：result="+bean.result+"\nmessage="+bean.message);
				if(bean.result.equals("0")){
					finish();
				}
				ToastUtil.getInstance().showToast(CashActivity.this, bean.message);
//				if(bean.result.equals("0")){
//					startActivity(new Intent(CashActivity.this, CashTipActivity.class));
//				}
			}
			
		});
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			getCarInfo();
		}
		
		if(requestCode == 1 && resultCode == 1 && data!=null){
			Bundle bundle = data.getBundleExtra("bundle");
			BankInfo bankInfo = (BankInfo) bundle.getSerializable("bankInfo");
			tvName.setText(bankInfo.getRealname());
			tvNumber.setText(bankInfo.getBank_number());
			tvPhone.setText(bankInfo.getMobile());
			bank_card_id = bankInfo.getBank_card_id();
			icon = (String) SPUtils.get(CashActivity.this, "icon", "");
			tvRemainMoney.setText(getResources().getString(R.string.current_money)+icon+getResources().getString(R.string.unit));
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
}
