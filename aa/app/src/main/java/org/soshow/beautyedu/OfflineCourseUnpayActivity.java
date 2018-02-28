package org.soshow.beautyedu;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.activity.LoginInputActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.bean.CommentInfo;
import org.soshow.beautyedu.bean.CourseInfoDetail;
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
import org.soshow.beautyedu.utils.UniversalImageLoadTool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OfflineCourseUnpayActivity extends BaseActivity implements
		OnClickListener {

	public static boolean is_tuwenAndLine_pay;
	private String TAG = "OfflineCourseUnpayActivity";
	private String type_id;
	private String describe_url;

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
					Log.e(TAG, "handleMessage  token = " + mToken);
					if (mToken != null) {
						getDetailInfo();
						initRemark(1);
					} else {
						Toast.makeText(OfflineCourseUnpayActivity.this,
								"网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
						if (OfflineCourseUnpayActivity.this != null) {
							Toast.makeText(OfflineCourseUnpayActivity.this,
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
		}

	};
	private SharedPreferences sp;
	private Editor editor;
	private String app_nonce;
	private String mToken;
	private TextView tvTitle;
	private TextView tvOldPrice;
	private TextView tvNewPrice;

	private int startPage = 1;
	private int pageSize = 10;
	private boolean hasMore;
	private View tvView;
	private TextView tvNodata;
	private TextView tvCommomt;
	private ImageView ivHead;
	private String from;
	private String type;
	private CourseInfoDetail courseInfoDetail;
	private LinearLayout loading;
	private String price;
//	private CommonAdapter<CommentInfo> commomtAdapter;
//	private List<CommentInfo> commentInfos;
	private LinearLayout llRecomment;
	private TextView tvLook;
	private String img_url;
	private String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_offline_course_detail_unpay);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();

		describe_url = getIntent().getStringExtra("describe_url");
		type_id = getIntent().getStringExtra("offline_id");
		from = getIntent().getStringExtra("from");
		initView();

		getTokenLocal();
	}
	
	@Override
	protected void onRestart() {
		LogUtils.e("onRestart------------");
		if(AllCommentActivity.IS_ADD_RECOMMENT){
			initRemark(1);
			AllCommentActivity.IS_ADD_RECOMMENT = false;
		}
		if(PayActivity.isPay){
			PayActivity.isPay = false;
			finish();
			is_tuwenAndLine_pay = true;
		}
		super.onRestart();
	}

	private void getTokenLocal() {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(this).getToken();
		Log.e(TAG, "getTokenLocal  token = " + mToken);
		if (mToken != null) {
			loading.setVisibility(View.VISIBLE);
			if (from.equals(LineCourseActivity.OFF_LINE)) {
				getDetailInfo();
			}
			initRemark(1);

		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}

	// 获取详情信息
	private void getDetailInfo() {
		String url_type = Constant.OFFLINE_INFO + "&tocken=" + mToken
				+ "&app_nonce=" + app_nonce + "&offline_id="+type_id;
		Log.e(TAG, "线下课程详情url=" + url_type);
		NetHelper.get(url_type, new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				String result = bean.result;
				try {
					if (result.equals("0")) {
						String info = bean.info;
						JSONObject jsonObject = new JSONObject(info);
						courseInfoDetail = GsonUtils
								.parseJSON(jsonObject.toString(),
										CourseInfoDetail.class);
						tvTitle.setText(courseInfoDetail.getTitle());
						tvLook.setText(courseInfoDetail.getViews()+"人观看");
						img_url = courseInfoDetail.getImg_url();
						title = courseInfoDetail.getTitle();
						boolean is_vip = courseInfoDetail.isIs_vip();
						LogUtils.e("是否vip会员="+is_vip);
						if(is_vip){
							tvOldPrice.setText("￥" + courseInfoDetail.getOriginal_price());
							tvNewPrice.setText("￥"
									+ courseInfoDetail.getVip_price());
							price = courseInfoDetail.getVip_price();
						}else{
							tvOldPrice.setText("￥" + courseInfoDetail.getOriginal_price());
							tvNewPrice.setText("￥"
									+ courseInfoDetail.getPrice());
							price = courseInfoDetail.getPrice();
						}
					} else {
						ToastUtil.getInstance().showToast(
								OfflineCourseUnpayActivity.this, "数据请求异常");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		});
	}

	private void initView() {
		// 课程介绍
		WebView linecourse_detail_web = (WebView) findViewById(R.id.line_course_detail_unPaywebView);
		WebSettings settings = linecourse_detail_web.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDefaultTextEncodingName("UTF-8");
		// 设置可以缩放加载
		settings.setBuiltInZoomControls(true);
		// 适应手机
		settings.setUseWideViewPort(false);
		settings.setLoadWithOverviewMode(true);
		settings.setLoadsImagesAutomatically(true);
		linecourse_detail_web.setHorizontalScrollBarEnabled(false);
		linecourse_detail_web.loadUrl(describe_url);
		linecourse_detail_web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		TextView tvTitleName = (TextView) findViewById(R.id.title_name);
		tvTitleName.setText("课程详情");
		loading = (LinearLayout) findViewById(R.id.linear_load);
		ivHead = (ImageView) findViewById(R.id.offline_unpay_iv_head);
		tvTitle = (TextView) findViewById(R.id.offline_unpay_tv_title);
		tvLook = (TextView) findViewById(R.id.tuwen_detailtv_look);
		tvOldPrice = (TextView) findViewById(R.id.offline_unpay_tv_cost_old);
		tvNewPrice = (TextView) findViewById(R.id.offline_unpay_tv_cost_new);
		//设置图片尺寸
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = screenWidth*3/4;
		LayoutParams layoutParams = ivHead.getLayoutParams();
		layoutParams.width = screenWidth;
		layoutParams.height = screenHeight;
		ivHead.setLayoutParams(layoutParams);
		LogUtils.e("未支付详情图片网址"+getIntent().getStringExtra("img_url"));
		UniversalImageLoadTool.disPlayTrue(getIntent().getStringExtra("img_url"), ivHead, R.drawable.defaultpic);
		
		findViewById(R.id.rl_remark).setOnClickListener(this);
		// 评论
		tvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

		// 评论
		llRecomment = (LinearLayout) findViewById(R.id.offline_free_myCustomListView);
		tvCommomt = (TextView) findViewById(R.id.off_line_free_tv_recommomt);
		tvView = getLayoutInflater().inflate(R.layout.view_nodata_textview,
				null);
		tvNodata = (TextView) tvView.findViewById(R.id.tv_nodata);
		tvNodata.setTextColor(getResources().getColor(R.color.text_yellow));
		tvNodata.setBackgroundResource(R.drawable.shape_bg_tv_yellow);
		
		// 支付
		TextView tvSignUp = (TextView) findViewById(R.id.line_course_detail_tv_signup);
		tvSignUp.setOnClickListener(this);
		if (from.equals(TuwenActivity.TU_WEN)) {
			tvSignUp.setText("立即购买");
		}else if(from.equals(LineCourseActivity.OFF_LINE)){
			tvSignUp.setText("我要报名");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_search_logininput:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
			break;
		case R.id.line_course_detail_tv_signup:
			if (MyApplication.logined == false) {
				Intent intent = new Intent(this,
						LoginInputActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slider_right_in,
		                R.anim.anim_slider_left_out);
			} else {
			Intent intent = new Intent(this, PayActivity.class);
			LogUtils.e("线下课程图片img_url====="+img_url+"\n价格="+price+"\n标题="+title);
			intent.putExtra("type", type);
			intent.putExtra(type, type_id);
			intent.putExtra("title", title);
			intent.putExtra("img_url", img_url);
			intent.putExtra("price", price);
			startActivity(intent);
			overridePendingTransition(R.anim.anim_slider_right_in,
	                R.anim.anim_slider_left_out);
			}
			break;

		default:
			break;
		}
	}

	/*
	 * 初始化评论列表
	 */
	public void initRemark(int page) {
//		commentInfos.clear();
		String url = "";
		if (from.equals(TuwenActivity.TU_WEN)) {
			price = getIntent().getStringExtra("price");
			img_url = getIntent().getStringExtra("img_url");
			title = getIntent().getStringExtra("title");
			String look_count = getIntent().getStringExtra("look_count");
			url = Constant.LECTURE_COMMENT_LIST + "&tocken=" + mToken
					+ "&app_nonce=" + app_nonce + "&lecture_id=" + type_id
					+ "&page_no=" + 1 + "&page_size=" + 2;
			Log.e("", "图文课程评论列表URL=" + url);
			type = TuwenActivity.TU_WEN_CODE;//图文跳转到PayActivity的课程支付生成订单区别
			tvNewPrice.setText("￥"+getIntent().getStringExtra("price"));
			tvOldPrice.setText("");
			tvTitle.setText(title);
			tvLook.setText(look_count+"人观看");
		} else if (from.equals(LineCourseActivity.OFF_LINE)) {
			url = Constant.OFFLINE_COMMENT_LIST + "&tocken=" + mToken
					+ "&app_nonce=" + app_nonce + "&offline_id=" + type_id
					+ "&page_no=" + page + "&page_size=" + pageSize;
			Log.e("", "线下课程评论列表URL=" + url);
			type = LineCourseActivity.OFF_LINE_CODE;//线下跳转到PayActivity的课程支付生成订单区别
		}
		NetHelper.get(url, new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				String result = bean.result;
				LogUtils.e("result==="+result);
				try {
					if (result.equals("0")) {
						String info = bean.info;
						LogUtils.e("info==="+bean.info.toString());
						JSONObject jsonObject = new JSONObject(info);
						JSONArray dataObj = jsonObject.getJSONArray("data");
						LogUtils.e("dataObj===="+dataObj.length());
						
							llRecomment.removeAllViews();
						for (int i = 0; i < (dataObj.length() >= 2? 2 : dataObj.length()); i++) {
							JSONObject obj = (JSONObject) dataObj.get(i);
							CommentInfo commentInfo = GsonUtils.parseJSON(obj.toString(), CommentInfo.class);
							View view = getLayoutInflater().inflate(R.layout.view_offline_commomt, null);
							TextView tvName = (TextView) view.findViewById(R.id.off_line_recommemt_tv_name);
							TextView tvContent = (TextView) view.findViewById(R.id.off_line_recommemt_tv_content);
							TextView tvTime = (TextView) view.findViewById(R.id.off_line_recommemt_tv_time);
							tvName.setText(commentInfo.getUser_nickname());
							tvContent.setText(commentInfo.getContent());
							String strTime = commentInfo.getCreate_time();
							if(!TextUtils.isEmpty(strTime)&&strTime != null){
								String time = TimeUtil.getStringTime(Long.valueOf(strTime));
								tvTime.setText(time);
							}else{
								tvTime.setText(strTime);
							}
							llRecomment.addView(view);
						}
						String count = jsonObject.getString("count");
						tvCommomt.setText(getResources().getString(
								R.string.course_recommomt)
								+ "(" + count + ")");
							tvNodata.setText("查看全部评论");
							llRecomment.addView(tvView);
							tvView.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent(
											OfflineCourseUnpayActivity.this,
											AllCommentActivity.class);
									intent.putExtra("from", from);
									intent.putExtra("type_id", type_id);
									startActivity(intent);
									overridePendingTransition(R.anim.anim_slider_right_in,
							                R.anim.anim_slider_left_out);
								}
							});
					} else {
						ToastUtil.getInstance().showToast(
								OfflineCourseUnpayActivity.this, "数据获取异常");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				loading.setVisibility(View.GONE);
			}
		});
	}

//	class CommomtAdapter extends BaseAdapter {
//		private Context context;
//
//		public CommomtAdapter(Context context) {
//			this.context = context;
//		}
//
//		@Override
//		public int getCount() {
//			return names.size() >= 2 ? 2 : names.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return 0;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			ViewHolder holder = ViewHolder.get(context, convertView, parent,
//					R.layout.view_offline_commomt, position);
//			TextView tvName = holder.getView(R.id.off_line_recommemt_tv_name);
//			TextView tvContent = holder
//					.getView(R.id.off_line_recommemt_tv_content);
//			TextView tvTime = holder.getView(R.id.off_line_recommemt_tv_time);
//			tvTime.setText(times.get(position));
//			tvName.setText(names.get(position) + "：");
//			tvContent.setText(contents.get(position));
//			return holder.getConvertView();
//		}
//
//	}

}
