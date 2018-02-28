package org.soshow.beautyedu;

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
import org.soshow.beautyedu.utils.SPUtils;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WealthActivity extends BaseActivity implements OnClickListener {

	private SharedPreferences sp;
	private Editor editor;
	private String app_nonce;
	private String mToken;
	
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
						getMyIcon();
					} else {
						Toast.makeText(WealthActivity.this, "网络不佳，请稍后再试",
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
	private TextView tvMoney;
	private ImageView iv;
	private TextView tvUserName;
	private LinearLayout loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_wealth);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		iniView();
		getTokenLocal();
	}

	private void iniView() {
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();

		loading = (LinearLayout) findViewById(R.id.linear_load);
		TextView title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText("我的财富");
		iv = (ImageView) findViewById(R.id.wealth_iv);
		tvUserName = (TextView) findViewById(R.id.wealth_tv_name);
		tvMoney = (TextView) findViewById(R.id.wealth_tv_money);
		findViewById(R.id.rl_my_car).setOnClickListener(this);
		findViewById(R.id.rl_cash).setOnClickListener(this);
		findViewById(R.id.rl_detail).setOnClickListener(this);
		
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_search_logininput:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
			break;
		case R.id.rl_my_car://我的银行卡
			Intent intent = new Intent(this, MyCardListActivity.class);
			intent.putExtra("type_from", "");
			startActivity(intent);
			overridePendingTransition(R.anim.anim_slider_right_in,
	                R.anim.anim_slider_left_out);
			break;
		case R.id.rl_cash://我要提现
			startActivity(new Intent(this, CashActivity.class));
			overridePendingTransition(R.anim.anim_slider_right_in,
	                R.anim.anim_slider_left_out);
			break;
		case R.id.rl_detail://收支明细
			startActivity(new Intent(this, MoneyDetaiActivity.class));
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
			getMyIcon();
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}
	
	/*
	 * 获取我余额
	 */
	private void getMyIcon(){
		String url_bank = Constant.MY_INCOME+"&tocken="+mToken+"&app_nonce="+app_nonce;
		LogUtils.e("我的余额url："+url_bank);
		NetHelper.get(url_bank , new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				String result = bean.result;
				try {
					if(result.equals("0")){
						String info = bean.info;
						JSONObject data = new JSONObject(info);
						String icon = (String) data.get("income");
						String userName = (String) data.get("username");
						String imgUrl = (String) data.get("photo");
						UniversalImageLoadTool.disPlayTrue(imgUrl, iv, R.drawable.defaultpic);
						tvUserName.setText(userName);
						tvMoney.setText("￥"+icon);
						SPUtils.put(WealthActivity.this, "icon", icon);
					}else{
						ToastUtil.getInstance().showToast(WealthActivity.this, bean.message);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				loading.setVisibility(View.GONE);
			}
			
		});

	}

}
