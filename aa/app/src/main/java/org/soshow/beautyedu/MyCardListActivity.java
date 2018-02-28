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
import org.soshow.basketball.common.adapter.CommonAdapter;
import org.soshow.basketball.common.adapter.ViewHolder;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.bean.BankInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.widget.MyDialog;
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyCardListActivity extends BaseActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener, OnClickListener {

	private SharedPreferences sp;	
	private Editor editor;
	private LinearLayout loading;
	private ListView list_Card;
	private PullToRefreshView mPullToRefreshView;
	private String app_nonce;
	private String mToken;
	private TextView title;
	private ImageView ivRight;
	private boolean hasMore;
	private int startPage = 1;
	private int pageSize = 10;
	private List<BankInfo> bankInfos;
	private CommonAdapter<BankInfo> adapter;
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
						getCarInfo(startPage);
					} else {
						Toast.makeText(MyCardListActivity.this, "网络不佳，请稍后再试",
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
		bankInfos = new ArrayList<BankInfo>();
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();

		title = (TextView) findViewById(R.id.title_name);
		title.setText(getResources().getString(R.string.my_card));
//		ivRight = (ImageView) findViewById(R.id.right_title);
//		ivRight.setVisibility(View.VISIBLE);
//		ivRight.setOnClickListener(this);

		loading = (LinearLayout) findViewById(R.id.linear_load);
		View view = getLayoutInflater().inflate(R.layout.item_add_bank_card, null);
		view.findViewById(R.id.add_card).setOnClickListener(this);;
		list_Card = (ListView) findViewById(R.id.mycourse_list_record);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.mycourse_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		list_Card.addFooterView(view);
		adapter = new CommonAdapter<BankInfo>(MyCardListActivity.this,
				bankInfos, R.layout.my_card_list_info) {

			@Override
			public void convert(ViewHolder holder, BankInfo data) {
				ImageView iv = holder.getView(R.id.bank_list_iv_logo);
				TextView tvTitle = holder.getView(R.id.bank_list_tv_title);
				TextView tvType = holder.getView(R.id.bank_list_tv_type);
				TextView tvNumber = holder.getView(R.id.bank_list_tv_number);
				String number_hide = getResources().getString(R.string.bank_number);
				String bank_number = data.getBank_number();
				tvNumber.setText(number_hide+bank_number.substring(bank_number.length()-4, bank_number.length()));
				tvTitle.setText(data.getBank_name());
			}

		};
		list_Card.setAdapter(adapter);
		
		list_Card.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				final MyDialog.MyBuilder dailog = new MyDialog.MyBuilder(MyCardListActivity.this);
				dailog.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								deleteCard(position);
								dialog.dismiss();
							}
						});

				dailog.setNagetiveButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});

				MyDialog creatDialog = dailog.creatDialog();
				View layout = dailog.getView();
				TextView tvTitle = (TextView) layout
						.findViewById(R.id.pop_title);
				tvTitle.setText("是否删除该银行卡？");
				EditText et = (EditText) layout
						.findViewById(R.id.ed_input);
				et.setVisibility(View.GONE);
				creatDialog.show();
				return true;
			}
		});
		
		
		String typr_from = getIntent().getStringExtra("type_from");
		if(typr_from.equals(CashActivity.TYPE)){
			list_Card.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					BankInfo bankInfo = bankInfos.get(position);
					Intent data = new Intent(MyCardListActivity.this, CashActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("bankInfo", bankInfo);
					data.putExtra("bundle", bundle);
					setResult(1, data);
					finish();
					overridePendingTransition(R.anim.anim_slider_left_in,
		                    R.anim.anim_slider_right_out);
				}
			});
		}

	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		if (hasMore) {
			startPage++;
			getCarInfo(startPage);
		} else {
			Toast.makeText(MyCardListActivity.this, "没有更多数据",
					Toast.LENGTH_SHORT).show();
			mPullToRefreshView.onFooterRefreshComplete();
		}

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		startPage = 1;
		bankInfos.clear();
		getCarInfo(startPage);
	}

	private void getTokenLocal() {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(this).getToken();
		if (mToken != null) {
			loading.setVisibility(View.VISIBLE);
			getCarInfo(startPage);
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
	private void getCarInfo(final int page){
		String url_bank = Constant.BANK_CARD+"&tocken="+mToken+"&app_nonce="+app_nonce+"&page_no="+page;
		LogUtils.e("获取银行卡信息："+url_bank);
		NetHelper.get(url_bank , new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				try {
					if(bean.result.equals("0")){
						String info = bean.info;
						LogUtils.e("银行卡信息info="+bean.info);
							JSONArray jsonData = new JSONArray(info);
							if(jsonData.length()>=pageSize){
								hasMore = true;
							}else{
								hasMore = false;
							}
							for (int i = 0; i < jsonData.length(); i++) {
								JSONObject obj = (JSONObject) jsonData.get(i);
								BankInfo bankInfo = GsonUtils.parseJSON(obj.toString(), BankInfo.class);
								bankInfos.add(bankInfo);
							}
							
							if (page != 1) {
								mPullToRefreshView.onFooterRefreshComplete();
							} else {
								mPullToRefreshView.onHeaderRefreshComplete();
							}
							adapter.notifyDataSetChanged();
					}else{
						ToastUtil.getInstance().showToast(MyCardListActivity.this, bean.info);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				loading.setVisibility(View.GONE);
			}
		});
	}
	
	/**
	 * 删除银行卡操作
	 */
	private void deleteCard(final int position){
		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);
		params.put("app_nonce", app_nonce);
		params.put("bank_card_id", bankInfos.get(position).getBank_card_id());
		NetHelper.post(Constant.DELETE_BANK_CARD ,params , new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				ToastUtil.getInstance().showToast(MyCardListActivity.this, bean.message);
				bankInfos.remove(position);
				adapter.notifyDataSetChanged();
			}
			
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_search_logininput:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
			break;
		case R.id.add_card://添加银行卡
			Intent intent = new Intent(this, MyCarInfoActivity.class);
			intent.putExtra("type", "list");
			startActivityForResult(intent, 1);
			overridePendingTransition(R.anim.anim_slider_right_in,
	                R.anim.anim_slider_left_out);
			break;

		default:
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK){
			loading.setVisibility(View.VISIBLE);
			bankInfos.clear();
			getCarInfo(startPage);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
