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
import org.soshow.beautyedu.bean.MoneyDetailInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TimeUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MoneyDetaiActivity extends BaseActivity implements OnHeaderRefreshListener, OnFooterRefreshListener {
	private SharedPreferences sp;
	private Editor editor;
	private LinearLayout loading;
	private TextView tvNodata;
	private ListView list_Card;
	private PullToRefreshView mPullToRefreshView;
	private String app_nonce;
	private String mToken;
	private TextView title;
	private ImageView ivRight;
	private boolean hasMore;
	private int startPage = 1;
	private int pageSize = 10;
	private CommonAdapter<MoneyDetailInfo> adapter;
	private List<MoneyDetailInfo> moneyDetailInfos;
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
						getDetailMoney(startPage);
					} else {
						Toast.makeText(MoneyDetaiActivity.this, "网络不佳，请稍后再试",
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_record_collect);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		iniView();
		getTokenLocal();
	}
	
	private void iniView() {
		moneyDetailInfos = new ArrayList<MoneyDetailInfo>();
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();

		title = (TextView) findViewById(R.id.title_name);
		title.setText(getResources().getString(R.string.money_detail_title));

		loading = (LinearLayout) findViewById(R.id.linear_load);
		tvNodata = (TextView) findViewById(R.id.list_no_course);
		list_Card = (ListView) findViewById(R.id.mycourse_list_record);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.mycourse_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		adapter = new CommonAdapter<MoneyDetailInfo>(MoneyDetaiActivity.this,
				moneyDetailInfos, R.layout.item_money_detail) {

			@Override
			public void convert(ViewHolder holder, MoneyDetailInfo data) {
				TextView tvTitle = holder.getView(R.id.money_detail_tv_title);
				TextView tvTime = holder.getView(R.id.money_detail_tv_time);
				TextView tvMoney = holder.getView(R.id.money_detail_tv_money);
				tvTitle.setText(data.getGoods_name());
				String deal_time = data.getAddtime();
				if(!TextUtils.isEmpty(deal_time)&&deal_time !=null){
					tvTime.setText(TimeUtil.getStringdatas(Long.valueOf(deal_time)));
				}
				
				String type = data.getType();
				if(type.equals("0")){//收入
					tvMoney.setText("+"+data.getCash());
					tvMoney.setTextColor(getResources().getColor(R.color.text_blue));
				}else if(type.equals("1")){//支出
					tvMoney.setText("-"+data.getCash());
					tvMoney.setTextColor(getResources().getColor(R.color.bg_green));
				}
				
			}

		};
		list_Card.setAdapter(adapter);
		
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		if (hasMore) {
			startPage++;
			getDetailMoney(startPage);
		} else {
			Toast.makeText(MoneyDetaiActivity.this, "没有更多数据",
					Toast.LENGTH_SHORT).show();
			mPullToRefreshView.onFooterRefreshComplete();
		}

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		startPage = 1;
		moneyDetailInfos.clear();
		getDetailMoney(startPage);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_search_logininput:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
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
			getDetailMoney(startPage);
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}
	
	/*
	 * 获取收支明细
	 */
	private void getDetailMoney(final int page){
		String url_bank = Constant.MY_INCOME_RECORD+"&tocken="+mToken+"&app_nonce="+app_nonce+"&type="+"&page_no="+page;
		LogUtils.e("获取收支明细url="+url_bank);
		NetHelper.get(url_bank , new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				try {
					if(bean.result.equals("0")){
						String info = bean.info;
						LogUtils.e("收支明细info="+info);
						JSONArray jsonArray = new JSONObject(info).getJSONArray("data");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject obj = (JSONObject) jsonArray.get(i);
							MoneyDetailInfo moneyDetailInfo = GsonUtils.parseJSON(obj.toString(), MoneyDetailInfo.class);
							moneyDetailInfos.add(moneyDetailInfo);
							LogUtils.e("收支明细==="+moneyDetailInfo.getGoods_name());
						}
						if(jsonArray.length()>=pageSize){
							hasMore = true;
						}else{
							hasMore = false;
						}
						
						if (page != 1) {
							mPullToRefreshView.onFooterRefreshComplete();
						} else {
							mPullToRefreshView.onHeaderRefreshComplete();
						}
						if(moneyDetailInfos.size()>0){
							tvNodata.setVisibility(View.GONE);
						}else{
							tvNodata.setVisibility(View.VISIBLE);
						}
						adapter.notifyDataSetChanged();
						
					}else{
						ToastUtil.getInstance().showToast(MoneyDetaiActivity.this, bean.message);
					}
					loading.setVisibility(View.GONE);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		});

	}
}
