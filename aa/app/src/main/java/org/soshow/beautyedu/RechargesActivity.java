package org.soshow.beautyedu;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.basketball.common.adapter.CommonAdapter;
import org.soshow.basketball.common.adapter.ViewHolder;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.bean.VIPListInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.DensityUtil;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.widget.MyCustomListView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class RechargesActivity extends BaseActivity implements OnClickListener {

	public static boolean is_recharge_pay;
	public static String VIP_CODE = "vip_id";
	private String app_nonce;
	private String mToken;
	private RechargesActivity context;
	private SharedPreferences sp;
	private Editor editor;
	private List<VIPListInfo> vips;
	private int clickPos;
	private MyCustomListView listView;
	private CommonAdapter<VIPListInfo> adapter;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:// token获取
				if (LoginUtil.login_handlered) {
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
						getType();
					} else {
						Toast.makeText(context, "网络不佳，请稍后再试",
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
	private TextView tvNum;
	private LinearLayout loading;
	private WebView web;
	private TextView tvSure;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_recharge);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		context = RechargesActivity.this;
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		initView();
		getTokenLocal();

	}
	
	@Override
	protected void onRestart() {
		if(PayActivity.isPay){
			vips.clear();
			getTokenLocal();
			PayActivity.isPay = false;
			is_recharge_pay = true;
		}
		super.onRestart();
	}

	private void initView() {
		vips = new ArrayList<VIPListInfo>();
		
		TextView title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText("VIP充值");
		loading = (LinearLayout) findViewById(R.id.linear_load);
		tvNum = (TextView) findViewById(R.id.recharge_tv_num);
		listView = (MyCustomListView) findViewById(R.id.rechcarge_listView);
		tvSure = (TextView) findViewById(R.id.recharge_tv_become);
		tvSure.setOnClickListener(this);
		web = (WebView) findViewById(R.id.vip_rights_web);
		WebSettings settings = web.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDefaultTextEncodingName("UTF-8");
		// 设置可以缩放加载
		settings.setBuiltInZoomControls(false);
		// 适应手机
		settings.setUseWideViewPort(false);
		settings.setLoadWithOverviewMode(true);
		//自动加载图片
		settings.setLoadsImagesAutomatically(true);
		settings.setBlockNetworkImage(false);
		//支持内容重新布局
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		final int marginLeft = DensityUtil.dip2px(context, 23);
		final int marginLeft2 = DensityUtil.dip2px(context, 16);
		adapter = new CommonAdapter<VIPListInfo>(context, vips,
				R.layout.view_recharge_choose_item) {
			@Override
			public void convert(ViewHolder holder, final VIPListInfo vipListInfo) {
				Log.e("", "vip充值====================isChecks=");
				TextView tvType = holder.getView(R.id.recharge_tv_type);
				TextView tvPrice = holder.getView(R.id.recharge_tv_price);
				CheckBox checkBox = holder
						.getView(R.id.recharge_iv_choose);
				tvType.setText(vipListInfo.getMonths() + "个月");
				tvPrice.setText("￥" + vipListInfo.getPrice());
				checkBox.setChecked(vipListInfo.isChoose());
				// 对齐布局
				String months = vipListInfo.getMonths();
				if (!TextUtils.isEmpty(months) && months != null) {
					Integer month = Integer.valueOf(months);
					if (month >= 10) {
						LayoutParams params = (LayoutParams) tvPrice
								.getLayoutParams();
						params.setMargins(marginLeft2, 0, 0, 0);
						tvPrice.setGravity(Gravity.CENTER_VERTICAL);
						tvPrice.setLayoutParams(params);
					} else {
						LayoutParams params = (LayoutParams) tvPrice
								.getLayoutParams();
						params.setMargins(marginLeft, 0, 0, 0);
						tvPrice.setGravity(Gravity.CENTER_VERTICAL);
						tvPrice.setLayoutParams(params);
					}
				}

				final int position = holder.getPosition();
				checkBox.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						clickPos = position;
						Log.e("", "position==="+position+"\nclickPos="+clickPos);
						for (int i = 0; i < vips.size(); i++) {
							if(position == i){
								vips.get(i).setChoose(true);
							}else{
								vips.get(i).setChoose(false);
							}
						}
						
						adapter.notifyDataSetChanged();
					}
				});
			}
		};

		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listView.setAdapter(adapter);

	}

	private void getTokenLocal() {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(context).getToken();

		if (mToken != null) {
			loading.setVisibility(View.VISIBLE);
			getType();
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}

	/*
	 * VIP充值价格列表
	 */
	private void getType() {
		String url_type = Constant.VIP_PRICE_LIST + "&tocken=" + mToken
				+ "&app_nonce=" + app_nonce;
		Log.e("", "VIP充值价格列表url=" + url_type);
		NetHelper.get(url_type, new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				String result = bean.result;
				try {
					if (result.equals("0")) {
						String info = bean.info;

						JSONArray jsonArray = new JSONObject(info).getJSONArray("vip_list");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject obj = (JSONObject) jsonArray.get(i);
							VIPListInfo vipListInfo = GsonUtils.parseJSON(
									obj.toString(), VIPListInfo.class);
							if(i == 0){
								vipListInfo.setChoose(true);
							}else{
								vipListInfo.setChoose(false);
							}
							vips.add(vipListInfo);
						}
						if(vips.size()<=0){
							tvSure.setVisibility(View.GONE);
						}else{
							tvSure.setVisibility(View.VISIBLE);
						}
						JSONObject jsonObject = new JSONObject(info).getJSONObject("describe");
						String vip_describe = jsonObject.getString("vip_describe");
						String vip_rights_url = jsonObject.getString("vip_rights_url");
						tvNum.setText(vip_describe);
						LogUtils.e("会员权益vip_rights_url="+vip_rights_url);
						web.loadUrl(vip_rights_url);
						int w = View.MeasureSpec.makeMeasureSpec(0,
				                View.MeasureSpec.UNSPECIFIED);
				        int h = View.MeasureSpec.makeMeasureSpec(0,
				                View.MeasureSpec.UNSPECIFIED);
				        //重新测量
				        web.measure(w, h);
						
						web.setWebViewClient(new WebViewClient() {
							@Override
							public boolean shouldOverrideUrlLoading(WebView view, String url) {
								view.loadUrl(url);
								return true;
							}
						});
						
					} else {
						ToastUtil.getInstance()
								.showToast(context, bean.message);
					}
					for (int i = 0; i < vips.size(); i++) {
						LogUtils.e(" 获取到的数据ischoose="+vips.get(i).isChoose());
					}
					adapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				loading.setVisibility(View.GONE);
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
		case R.id.recharge_tv_become:
			LogUtils.e("clickPos=" + clickPos + "        vips="
					+ vips.size());
			if (vips.size()>0&&clickPos >= 0) {
				Intent intent = new Intent(this, PayActivity.class);
				intent.putExtra("type", VIP_CODE);
				intent.putExtra(VIP_CODE, vips.get(clickPos).getVip_id());
				intent.putExtra("price", vips.get(clickPos).getPrice());
				intent.putExtra("title", "VIP会员");
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slider_right_in,
		                R.anim.anim_slider_left_out);
			} else {
				ToastUtil.getInstance().showToast(context, "请选择VIP会员套餐");
			}
			break;

		default:
			break;
		}
	}

}
