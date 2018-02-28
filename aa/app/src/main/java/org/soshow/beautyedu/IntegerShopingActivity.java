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
import org.soshow.beautyedu.bean.IntegerInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.DensityUtil;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

/**
 * 积分商城
 * 
 * @author chenjiaming
 *
 */
public class IntegerShopingActivity extends BaseActivity implements OnItemClickListener {

	private LinearLayout loading;
	private TextView tvNoData;
	private PullToRefreshGridView pullToRefreshGridView;
	private Context context;
	private SharedPreferences sp;
	private Editor editor;
	private String app_nonce;
	private String mToken;
	private int startPage = 1;
	private int pageSize = 10;
	private boolean hasMore;
	private List<IntegerInfo> integerShops;
	private CommonAdapter<IntegerInfo> shopAdapter;
	
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:// token获取
				if (LoginUtil.login_handlered) {
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
//						 integerShops.clear();
						getShopList(startPage);
					} else {
						Toast.makeText(IntegerShopingActivity.this,
								"网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
						if (IntegerShopingActivity.this != null) {
							Toast.makeText(IntegerShopingActivity.this,
									"网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
						}
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
		};
	};
	private int page_size = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_integer_shoping);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		context = IntegerShopingActivity.this;
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		initView();
		getTokenLocal();
	}

	private void initView() {
		integerShops = new ArrayList<IntegerInfo>();
		TextView title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText(R.string.my_video_card);

		loading = (LinearLayout) findViewById(R.id.linear_load);
		tvNoData = (TextView) findViewById(R.id.shop_noData);
		tvNoData.setVisibility(View.INVISIBLE);

		pullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.homepage_gridView);
		GridView gridView = pullToRefreshGridView.getRefreshableView();
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		shopAdapter = new CommonAdapter<IntegerInfo>(context, integerShops,
				R.layout.item_integer_shop) {

			@Override
			public void convert(ViewHolder viewHolder, IntegerInfo listInfo) {
				//整理布局
				RelativeLayout rlView = viewHolder.getView(R.id.shopActivity_rl);
		        int right = DensityUtil.dip2px(context, 8);
		        int top = DensityUtil.dip2px(context, 13);
		        // 设置布局
		        if(viewHolder.getPosition() == 0){
		        	rlView.setPadding(right, top, 0, 0);
		        }else{
		        	if (viewHolder.getPosition() % 2 == 0) {
		        		rlView.setPadding(right, top, right, 0);
		        	} else {
		        		rlView.setPadding(right, top, 0, 0);
		        	}
		        }
		        
		        ImageView iv = viewHolder.getView(R.id.shop_grid_item_img);
		        TextView tvTitle = viewHolder.getView(R.id.shop_grid_item_tv_title);
		        TextView tvPrice = viewHolder.getView(R.id.shop_grid_item_tv_price);
		        TextView tvPayNum = viewHolder.getView(R.id.shop_grid_item_tv_payNum);
		        
		        UniversalImageLoadTool.disPlayTrue(listInfo.getCard_imgurl(), iv, R.drawable.defaultpic);
		        tvTitle.setText(listInfo.getCard_name());
		        tvPrice.setText("￥"+listInfo.getPrice());
		        tvPayNum.setText(listInfo.getPay_num()+"人付款");
			}
		};
		 gridView.setAdapter(shopAdapter);
		 gridView.setOnItemClickListener(this);
		
		 pullToRefreshGridView
			.setOnRefreshListener(new OnRefreshListener2<GridView>() {

				@Override
				public void onPullDownToRefresh(
						PullToRefreshBase<GridView> refreshView) {
					startPage = 1;
					integerShops.clear();
					getShopList(startPage);
				}

				@Override
				public void onPullUpToRefresh(
						PullToRefreshBase<GridView> refreshView) {
					if (hasMore) {
						startPage++;
						getShopList(startPage);
					} else {
						Toast.makeText(IntegerShopingActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
						pullToRefreshGridView.onRefreshComplete();
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
			
		default:
			break;
		}

	}

	// 获取tocken
	private void getTokenLocal() {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(this).getToken();
		if (mToken != null) {
			loading.setVisibility(View.VISIBLE);
			getShopList(startPage);
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}

	/**
	 * 获取商品列表
	 * 
	 * @param startPage
	 */
	private void getShopList(int page_num) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);
		params.put("app_nonce", app_nonce);
		params.put("page_num", page_num+"");
		params.put("page_size", page_size+"");
		LogUtils.e("积分卡商城：" + params.toString());
		NetHelper.post(Constant.VIDEOCARDURL, params,
				new SimpleSingleBeanNetHandler<Captcha>(this) {

					@Override
					protected void onSuccess(Captcha bean) {
						LogUtils.e("积分卡商城列表：result=" + bean.result + "\ninfo="
								+ bean.info);
						if (bean.result.equals("0")) {
							String info = bean.info;
							try {
								JSONArray jsonArray = new JSONArray(info);
								if (jsonArray.length() >= pageSize) {
									hasMore = true;
								} else {
									hasMore = false;
								}
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject obj = (JSONObject) jsonArray
											.get(i);
									IntegerInfo integerInfo = GsonUtils
											.parseJSON(obj.toString(),
													IntegerInfo.class);
									if(integerInfo.getStatus().equals("1")){//上架
										integerShops.add(integerInfo);
									}
								}
								pullToRefreshGridView.onRefreshComplete();
								shopAdapter.notifyDataSetChanged();
							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else {
							Toast.makeText(context, bean.message + "",
									Toast.LENGTH_SHORT).show();
						}
						loading.setVisibility(View.GONE);
					}

				});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		IntegerInfo integerInfo = integerShops.get(position);
		Intent intent = new Intent(this, ShopDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("integerInfo", integerInfo);
		intent.putExtras(bundle);
		startActivity(intent);
		
	}

}
