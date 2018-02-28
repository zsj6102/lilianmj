package org.soshow.beautyedu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautydu.wxpay.Util;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.activity.LoginInputActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.bean.CommentInfo;
import org.soshow.beautyedu.bean.VideoCourse;
import org.soshow.beautyedu.bean.VideoCourseInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.json.utils.NetUtil;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.MyAlertDialog;
import org.soshow.beautyedu.utils.PurchasePayUtil;
import org.soshow.beautyedu.utils.SPUtils;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TimeUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.LoadingDialogShow;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class VipPayActivity extends BaseActivity implements OnClickListener {

	public static boolean PER_INTEGERAL_PAY_SUCCESS;
	public static boolean is_course_pay;
	private String app_nonce;
	private String mToken;
	private TextView tvCommomtNum;
	private View tvView;
	private TextView tvNodata;
	private SharedPreferences sp;
	private Editor editor;
	private Context context;
	private LinearLayout loading;
	private LinearLayout llRecomment;
	private Dialog dialog;
	private VideoCourseInfo videoCourseInfo;
	private int currentInteger;
	private int course_integral;

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
						initRemark();
					} else {
						Toast.makeText(
								context,
								getResources()
										.getString(R.string.generic_error),
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

	Map<String, String> resultunifiedorder;
	private String notify_url = Constant.NOTIFY_URL_PER_INTEGER_WEIXIN;
	private String shopName;//支付商品名
	
	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, Intent intent) {
			String action = intent.getAction();
//			if (action.equals(WXPayEntryActivity.ACTION_WXPayEntryCallback)) {
//				int errCode = intent.getExtras().getInt("resp.errCode");
//				LogUtils.e("支付errCode=" + errCode);
//				switch (errCode) {
//				case 0:
//
//					ToastUtil.getInstance().showToast(context,
//							R.string.pay_success);
//					new Handler().postDelayed(new Runnable() {
//
//						@Override
//						public void run() {
//							PER_INTEGERAL_PAY_SUCCESS = true;
//							finish();
//						}
//					}, 500);
//
//					break;
//				case -1:
//					ToastUtil.getInstance().showToast(context,
//							R.string.pay_fail);
//
//					break;
//				case -2:
//					ToastUtil.getInstance().showToast(context,
//							R.string.pay_cancel);
//					break;
//				default:
//					break;
//				}
//			}
		}
	};
	protected String order_num;
	private String lecture_type;//1：本地视频；2：网页视频；0：图文课程
	private VideoCourse tuwenCourse;

	@Override
	protected void onRestart() {
		if(AllCommentActivity.IS_ADD_RECOMMENT){
			initRemark();
			AllCommentActivity.IS_ADD_RECOMMENT = false;
		}
		if(PayActivity.isPay){
//			getTokenLocal();
			PayActivity.isPay = false;
			finish();
			is_course_pay = true;
		}
		super.onRestart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_vip_pay);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		dialog = new Dialog(this, R.style.HeadScale);
		lecture_type = getIntent().getStringExtra("lecture_type");
		initView();
		getTokenLocal();
		weixinPayInit();
	}
	

	private void initView() {
		context = VipPayActivity.this;
		if(lecture_type.equals("0")){
			tuwenCourse = (VideoCourse) getIntent().getSerializableExtra("tuwenCourse");
		}else{
			videoCourseInfo = (VideoCourseInfo) getIntent().getSerializableExtra("VideoCourseInfo");
		}
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		TextView title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText(getIntent().getStringExtra("title"));

		loading = (LinearLayout) findViewById(R.id.linear_load);
		loading.setVisibility(View.VISIBLE);
		ImageView iv = (ImageView) findViewById(R.id.vip_unpay_iv_head);
		TextView tvTitle = (TextView) findViewById(R.id.vip_unpay_tv_title);
		TextView tvLook = (TextView) findViewById(R.id.vip_detailtv_look);
		TextView tvPrice = (TextView) findViewById(R.id.vip_unpay_tv_cost_new);
		findViewById(R.id.vip_detail_tv_signup).setOnClickListener(this);
		tvCommomtNum = (TextView) findViewById(R.id.vip_tv_recommomt);
		WebView webView = (WebView) findViewById(R.id.vip_detail_description);
		// 设置图片尺寸
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int screenWidth = displayMetrics.widthPixels;
		int screenHeight = screenWidth * 3 / 4;
		LayoutParams layoutParams = iv.getLayoutParams();
		layoutParams.width = screenWidth;
		layoutParams.height = screenHeight;
		iv.setLayoutParams(layoutParams);

		tvView = getLayoutInflater().inflate(R.layout.view_nodata_textview,
				null);
		tvNodata = (TextView) tvView.findViewById(R.id.tv_nodata);
		tvNodata.setTextColor(getResources().getColor(R.color.text_yellow));
		tvNodata.setBackgroundResource(R.drawable.shape_bg_tv_yellow);

		llRecomment = (LinearLayout) findViewById(R.id.offline_free_myCustomListView);
		
//		MyCustomListView listView = (MyCustomListView) findViewById(R.id.vip_free_myCustomListView);
//		commomtAdapter = new CommomtAdapter(this);
//		listView.addFooterView(tvView);
//		listView.setAdapter(commomtAdapter);

		UniversalImageLoadTool.disPlayTrue(getIntent().getStringExtra("image_url"),
				iv, R.drawable.defaultpic);
		shopName = getIntent().getStringExtra("lectureTitle");
		tvTitle.setText(getIntent().getStringExtra("lectureTitle"));
		tvLook.setText(getIntent().getStringExtra("look") + "人观看");
		String integral = getIntent().getStringExtra("lecture_integral");
		if(!TextUtils.isEmpty(integral)){
			course_integral = Integer.valueOf(integral);
		}else{
			course_integral = 0;
		}
		tvPrice.setText(course_integral+"");
		
		webView.setVisibility(View.VISIBLE);
		WebSettings settings = webView.getSettings();
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
		webView.loadUrl(getIntent().getStringExtra("lecture_content_url"));
		int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        //重新测量
        webView.measure(w, h);

	}

	private void getTokenLocal() {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(this).getToken();
		if (mToken != null) {
			initRemark();
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}

	/*
	 * 初始化评论列表
	 */
	public void initRemark() {
//		names.clear();
//		contents.clear();
//		times.clear();
		String url = "";
		url = Constant.LECTURE_COMMENT_LIST + "&tocken=" + mToken
				+ "&app_nonce=" + app_nonce + "&lecture_id="
				+ getIntent().getStringExtra("lecture_id") + "&page_no=" + 1
				+ "&page_size=" + 2;
		Log.e("", "VIP课程评论数据URL=" + url);
		NetHelper.get(url, new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				

				String result = bean.result;
				try {
					if (result.equals("0")) {
						String info = bean.info;
						JSONObject jsonObject = new JSONObject(info);
						JSONArray dataObj = jsonObject.getJSONArray("data");
						
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
						tvCommomtNum.setText(getResources().getString(
								R.string.course_recommomt)
								+ "(" + count + ")");
						tvNodata.setText("查看全部评论");
						llRecomment.addView(tvView);
						tvView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(VipPayActivity.this,
										AllCommentActivity.class);
								intent.putExtra("lecture_id", getIntent()
										.getStringExtra("lecture_id"));
								startActivity(intent);
								overridePendingTransition(R.anim.anim_slider_right_in,
						                R.anim.anim_slider_left_out);
							}
						});
						
					} else {
						ToastUtil.getInstance().showToast(VipPayActivity.this, "数据获取异常");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				loading.setVisibility(View.GONE);
			
				
				
				
//				String result = bean.result;
//				try {
//					if (result.equals("0")) {
//						String info = bean.info;
//						JSONObject jsonObject = new JSONObject(info);
//						JSONArray dataObj = jsonObject.getJSONArray("data");
//						String count = jsonObject.getString("count");
//						tvCommomt.setText(getResources().getString(
//								R.string.course_recommomt)
//								+ "(" + count + ")");
//						tvNodata.setText("查看全部评论");
//						tvView.setOnClickListener(new OnClickListener() {
//
//							@Override
//							public void onClick(View v) {
//								
//								Intent intent = new Intent(VipPayActivity.this,
//										AllCommentActivity.class);
//								intent.putExtra("lecture_id", getIntent()
//										.getStringExtra("lecture_id"));
//								startActivity(intent);
//								overridePendingTransition(R.anim.anim_slider_right_in,
//						                R.anim.anim_slider_left_out);
//							}
//						});
//						for (int i = 0; i < dataObj.length(); i++) {
//							JSONObject obj = (JSONObject) dataObj.get(i);
//							String name = obj.getString("username");
//							String content = obj.getString("content");
//							String strTime = obj.getString("addtime");
//							if (!TextUtils.isEmpty(strTime) && strTime != null) {
//								String time = TimeUtil.getStringTime(Long
//										.valueOf(strTime));
//								times.add(time);
//							} else {
//								times.add(strTime);
//							}
//							names.add(name);
//							contents.add(content);
//						}
//						commomtAdapter.notifyDataSetChanged();
//					} else {
//						ToastUtil.getInstance().showToast(VipPayActivity.this,
//								bean.message);
//					}
//
//					loading.setVisibility(View.GONE);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
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
//			TextView tvReconmentContent = holder
//					.getView(R.id.off_line_recommemt_tv_content);
//			TextView tvTime = holder.getView(R.id.off_line_recommemt_tv_time);
//			tvTime.setText(times.get(position));
//			tvName.setText(names.get(position) + "：");
//			tvReconmentContent.setText(contents.get(position));
//			return holder.getConvertView();
//		}
//
//	}

	public void onClick(View v) {
		String card_integral = (String) SPUtils.get(this, "card_integral", "");
		switch (v.getId()) {
		case R.id.back_search_logininput:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
			break;
		case R.id.vip_detail_tv_signup:// 购买
			if (MyApplication.logined == false) {
				Intent intent = new Intent(context,
						LoginInputActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slider_right_in,
		                R.anim.anim_slider_left_out);
			} else {
				View dialogLayout = null;
				if(!TextUtils.isEmpty(card_integral)){
					currentInteger = Integer.valueOf(card_integral);
					
				}else{
					currentInteger = 0;
				}
				if(currentInteger >= course_integral){
					dialogLayout = getLayoutInflater().inflate(R.layout.dialog_enough_integer, null);
				}else{
					dialogLayout = getLayoutInflater().inflate(R.layout.dialog_no_enough_integer, null);
					dialogLayout.findViewById(R.id.positiveButton_per).setOnClickListener(this);
				}
				TextView tvRemainInteger = (TextView) dialogLayout.findViewById(R.id.tv_remain_integer);
				TextView tvPayInteger = (TextView) dialogLayout.findViewById(R.id.tv_use_integer);
				TextView tvWhatch = (TextView) dialogLayout.findViewById(R.id.positiveButton);
				ImageView ivClose = (ImageView) dialogLayout.findViewById(R.id.negativeButton);
				tvRemainInteger.setText(card_integral);
				tvPayInteger.setText("本次观看视频使用"+course_integral+"积分");
				tvWhatch.setOnClickListener(this);
				ivClose.setOnClickListener(this);
				dialog.setContentView(dialogLayout);
				Window windowDialog = dialog.getWindow();
//				windowDialog.setGravity(Gravity.AXIS_PULL_BEFORE|Gravity.AXIS_PULL_AFTER|Gravity.BOTTOM);
				android.view.WindowManager.LayoutParams lp = windowDialog.getAttributes();
				lp.y = 0;
				lp.x = 0;
				windowDialog.setAttributes(lp);
				dialog.show();
				
			
				
			}
			break;
			
		case R.id.positiveButton:
			if(currentInteger >= course_integral){
				deductionIntegral();
			}else{
				Intent intent = new Intent(this, IntegerShopingActivity.class);
				startActivity(intent);
			}
			dialog.dismiss();
			overridePendingTransition(R.anim.anim_slider_right_in,
	                R.anim.anim_slider_left_out);
			break;
			
		case R.id.negativeButton:
			dialog.dismiss();
			break;
			
		case R.id.positiveButton_per:
			buyPerVideo(course_integral+"");
			break;

		default:
			break;
		}
	}
	
	/*
	 * 购买单个视频操作
	 */
	private void buyPerVideo(String lecture_integral){

		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);
		params.put("app_nonce", app_nonce);
		params.put("lecture_integral", lecture_integral);
		NetHelper.post(Constant.GEINTEGRALINFO, params,
				new SimpleSingleBeanNetHandler<Captcha>(this) {

					@Override
					protected void onSuccess(Captcha bean) {
						LogUtils.e("单次购买操作：result=" + bean.result + "\ninfo="
								+ bean.info);
						if (bean.result.equals("0")) {
						try {
							dialog.dismiss();
							String total_price = new JSONObject(bean.info).getString("total_price");
							tipDialog(total_price);
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
	
	// 单次购买积分提示
    private void tipDialog(String total_price) {
    	
    	View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_no_integer, null);
		dialogLayout.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				
			}
		});
		dialogLayout.findViewById(R.id.sure).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getOrderNum();
				dialog.dismiss();
			}
		});
	TextView tvPrice = (TextView) dialogLayout.findViewById(R.id.tv_two);
	ImageView ivClose = (ImageView) dialogLayout.findViewById(R.id.negativeButton);
	
	//设置字体颜色
//	String str = "购买该视频需要"+total_price+"元，是否确定购买？";
//	SpannableStringBuilder builder = new SpannableStringBuilder(str);
//	ForegroundColorSpan fcsGray = new ForegroundColorSpan(getResources().getColor(R.color.text_gray));
//	ForegroundColorSpan fcsRed = new ForegroundColorSpan(getResources().getColor(R.color.text_red));
//	builder.setSpan(fcsGray, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
//	builder.setSpan(fcsRed, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);  
	
	tvPrice.setText(Html.fromHtml("<font color=\'#666666\'>购买该视频需要</font><font color=\'#F50103\'>"+total_price+"</font><font color=\'#666666\'>元，是否确定购买？</font>"));
	ivClose.setOnClickListener(this);
	dialog.setContentView(dialogLayout);
	Window windowDialog = dialog.getWindow();
	android.view.WindowManager.LayoutParams lp = windowDialog.getAttributes();
	lp.y = 0;
	lp.x = 0;
	windowDialog.setAttributes(lp);
	dialog.show();
    	
//    	final MyDialog.MyBuilder dailog = new MyDialog.MyBuilder(this);
//		dailog.setPositiveButton("确定",
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog,
//							int which) {
//						getOrderNum();
//						dialog.dismiss();
//					}
//				});
//
//		dailog.setNagetiveButton("取消",
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog,
//							int which) {
//						dialog.dismiss();
//
//					}
//				});
//
//		MyDialog creatDialog = dailog.creatDialog();
//		View layout = dailog.getView();
//		TextView tvTitle = (TextView) layout
//				.findViewById(R.id.pop_title);
//		tvTitle.setText("购买该视频需要"+total_price+"元，是否确定购买？");
//		EditText et = (EditText) layout
//				.findViewById(R.id.ed_input);
//		et.setVisibility(View.GONE);
//		creatDialog.show();
    	
    }
	
	/*
	 * 网页播放
	 */
	private void addClick() {
		String url_click = Constant.phpUrl
				+ "/wap/api.php?action=ADD_LECTURE_CLICK&tocken=" + mToken
				+ "&app_nonce=" + app_nonce + "&lecture_id=" + videoCourseInfo.getLecture_id();
		Log.d("234abc", "点击地址" + url_click);

		NetHelper.get(url_click, new SimpleSingleBeanNetHandler<Captcha>(VipPayActivity.this) {

			@Override
			protected void onSuccess(Captcha bean) {
				try {
					int resule = Integer.parseInt(bean.result);
					switch (resule) {
					case 0:
						String lecture_click_count = videoCourseInfo.getLecture_click_count();
						if(!TextUtils.isEmpty(lecture_click_count)&&lecture_click_count !=null){
							int clickCount = Integer.valueOf(lecture_click_count);
							videoCourseInfo.setLecture_click_count(clickCount+1+"");
						} 
						 
						//非全屏播放
						Intent intent = new Intent(VipPayActivity.this,
								WebPlayVideoActivity.class);
						intent.putExtra("url", videoCourseInfo
								.getLecture_link());
						intent.putExtra("title",videoCourseInfo.getLecture_title());
						
						//全屏播放
//						Intent intent = new Intent(SalonListActivity.this,
//								WebActivity.class);
//						intent.putExtra("catID", 12);
//						intent.putExtra("url", videoCourseInfo
//								.getLecture_link());
						
						startActivity(intent);
						overridePendingTransition(R.anim.anim_slider_right_in,
				                R.anim.anim_slider_left_out);
						break;
					case 99:
						editor.putBoolean("token_logined", false);
						editor.remove("mToken");
						editor.commit();
						break;
					default:

						break;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

			@Override
			protected void onError(int errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				// super.onError(errorCode, errorMsg);
			}

		});
	}
	
	
	/**
	 * 积分卡扣除积分操作
	 */
	private void deductionIntegral(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);
		params.put("app_nonce", app_nonce);
		params.put("lecture_id", getIntent().getStringExtra("lecture_id"));
		params.put("lecture_integral", course_integral+"");
		LogUtils.e("扣除积分参数："+params.toString());
		NetHelper.post(Constant.VIDEOCARD, params,
				new SimpleSingleBeanNetHandler<Captcha>(VipPayActivity.this) {

					@Override
					protected void onSuccess(Captcha bean) {
						LogUtils.e("扣除积分操作数据：result=" + bean.result + "\ninfo="
								+ bean.message);
						if (bean.result.equals("0")) {
							try {
							String integral_l = new JSONObject(bean.info).getString("integral_l");//获取当前积分
							SPUtils.put(VipPayActivity.this, "card_integral", integral_l);
							if(lecture_type.equals("0")){//图文
								Intent intent = new Intent(VipPayActivity.this,
										OfflineCoursePayActivity.class);
								intent.putExtra("content_url",tuwenCourse.getLecture_graphic_content_url()+"&tocken="+ mToken + "&app_nonce=" + app_nonce);
								intent.putExtra("offline_id",tuwenCourse.getLecture_id());
								intent.putExtra("from",TuwenActivity.TU_WEN);
								startActivity(intent);
							}else{//视频
								
									if(videoCourseInfo.getLecture_type().equals("1")){//本地观看
										playVideoInfo();
									}else if(videoCourseInfo.getLecture_type().equals("2")){//网页视频
										addClick();
									}
								
							}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							Toast.makeText(context, bean.message + "",
									Toast.LENGTH_SHORT).show();
						}
					}

				});
	}
	

	/*
	 * 本地播放
	 */
	private void playVideoInfo() {
		Log.e("培训视频列表播放地址：", videoCourseInfo
				.getVideo_local_name().toString());

		if ((NetUtil.isConnected(context) && NetUtil
				.isWifi(context))
				|| NetUtil.isConnected(context)
				&& !NetUtil.isWifi(context)
				&& sp.getInt("allow_net_local", 0) == 1) {
			if (Build.VERSION.SDK_INT >= 16) {

				Intent intent = new Intent(context,
						CoursesPlayActivity.class);
				intent.putExtra("title", videoCourseInfo.getLecture_title());
				
				String count = videoCourseInfo.getLecture_click_count();
				intent.putExtra("look", count);
				if(!TextUtils.isEmpty(count)&&!count.equals("")){
					int click_count = Integer.valueOf(count)+1;
					LogUtils.e("点击人数=============="+click_count);
					videoCourseInfo.setLecture_click_count(click_count+"");
				}
				
				intent.putExtra("payState", "1");
				
				intent.putExtra("lecture_cat1", "");
				intent.putExtra("lecture_questions_id", "");
				intent.putExtra("lecture_cat3", "");
//				intent.putExtra("url_s", videoCourseInfo
//						.getVideo_local_name());
//				intent.putExtra("url_h", videoCourseInfo
//						.getVideo_local_name());
//				intent.putExtra("url_p", videoCourseInfo
//						.getVideo_local_name());
//
//				intent.putExtra("video_local_name", videoCourses
//						.get(position).getVideo_local_name());
				if(videoCourseInfo.getPlay_urls().size() > 0){
					intent.putExtra("url_s", videoCourseInfo.getPlay_urls().get(0).getUrl());
					intent.putExtra("url_h", videoCourseInfo.getPlay_urls().get(0).getUrl());
					intent.putExtra("url_p", videoCourseInfo.getPlay_urls().get(0).getUrl());
					intent.putExtra("video_local_name", videoCourseInfo.getPlay_urls().get(0).getUrl());
				}else{
					intent.putExtra("url_s", "");
					intent.putExtra("url_h", "");
					intent.putExtra("url_p", "");
					intent.putExtra("video_local_name", "");
				}

				intent.putExtra("lecture_title",
						videoCourseInfo
								.getLecture_title());
				intent.putExtra("lecture_content_url", videoCourseInfo.getLecture_content_url());
				intent.putExtra("lecture_id",
						videoCourseInfo.getLecture_id());
				intent.putExtra("course_id", "");
				intent.putExtra("vheight_s", "9");
				intent.putExtra("vwidth_s", "16");
				intent.putExtra("lecture_click_count", videoCourseInfo.getLecture_click_count());
				intent.putExtra("image_url",
						videoCourseInfo.getImage_url());
				intent.putExtra("price", videoCourseInfo
						.getPrice());
				intent.putExtra("lecture_operate", "");
				intent.putExtra("course_pack_id", "");
				try {
					intent.putExtra("play_duration", "");
				} catch (Exception e) {
				}

				if (sp.getBoolean("logined", false)) {
					intent.putExtra("learn_status", 0);
					intent.putExtra("my_duration", 0);
				}
				context.startActivity(intent);
				overridePendingTransition(R.anim.anim_slider_right_in,
		                R.anim.anim_slider_left_out);
			} else {
				Toast.makeText(context, "您当前系统版本过低,请升级到安卓4.1及以上",
						Toast.LENGTH_SHORT).show();
			}
		} else if (NetUtil.isConnected(context)
				&& sp.getInt("allow_net_local", 0) == 0
				&& !NetUtil.isWifi(context)) {
			Builder bd = new MyAlertDialog.Builder(context);
			bd.setTitle("网络提示")
					.setMessage(R.string.video_net_warn)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface arg0,
										int arg1) {
									arg0.dismiss();
									if (Build.VERSION.SDK_INT >= 16) {
										Intent intent = new Intent(
												context,
												CoursesPlayActivity.class);
										intent.putExtra(
												"title",videoCourseInfo.getLecture_title());
										
										String count = videoCourseInfo.getLecture_click_count();
										intent.putExtra("look", count);
										if(!TextUtils.isEmpty(count)&&!count.equals("")){
											int click_count = Integer.valueOf(count)+1;
											LogUtils.e("点击人数=============="+click_count);
											videoCourseInfo.setLecture_click_count(click_count+"");
										}
										
										intent.putExtra("payState", "1");
										intent.putExtra(
												"lecture_cat1", "");
										intent.putExtra(
												"lecture_questions_id",
												"");
										intent.putExtra(
												"lecture_cat3", "");
										if(videoCourseInfo.getPlay_urls().size() > 0){
											intent.putExtra("url_s", videoCourseInfo.getPlay_urls().get(0).getUrl());
											intent.putExtra("url_h", videoCourseInfo.getPlay_urls().get(0).getUrl());
											intent.putExtra("url_p", videoCourseInfo.getPlay_urls().get(0).getUrl());
											intent.putExtra("video_local_name", videoCourseInfo.getPlay_urls().get(0).getUrl());
										}else{
											intent.putExtra("url_s", "");
											intent.putExtra("url_h", "");
											intent.putExtra("url_p", "");
											intent.putExtra("video_local_name", "");
										}

										intent.putExtra(
												"lecture_title",
												videoCourseInfo
														.getLecture_title());
										intent.putExtra(
												"lecture_content_url",
												videoCourseInfo
														.getLecture_content_url());
										intent.putExtra(
												"lecture_id",
												videoCourseInfo
														.getLecture_id());
										intent.putExtra(
												"course_id", "");
										intent.putExtra(
												"vheight_s", "9");
										intent.putExtra("vwidth_s",
												"16");
										intent.putExtra(
												"lecture_click_count",
												videoCourseInfo
														.getLecture_click_count());
										intent.putExtra(
												"image_url",
												videoCourseInfo
														.getImage_url());
										intent.putExtra(
												"price",
												videoCourseInfo
														.getPrice());
										intent.putExtra(
												"lecture_operate",
												"");
										intent.putExtra(
												"course_pack_id",
												"");
										try {
											intent.putExtra(
													"play_duration",
													"");
										} catch (Exception e) {
											// TODO: handle
											// exception
										}

										if (sp.getBoolean(
												"logined", false)) {
											intent.putExtra(
													"learn_status",
													0);
											intent.putExtra(
													"my_duration",
													0);
										}
										editor.putInt(
												"allow_net_local",
												1);
										editor.commit();
										Constant.allow_net_other = true;
										context.startActivity(intent);
										overridePendingTransition(R.anim.anim_slider_right_in,
								                R.anim.anim_slider_left_out);
									} else {
										Toast.makeText(
												context,
												"您当前系统版本过低,请升级到安卓4.1及以上",
												Toast.LENGTH_SHORT)
												.show();
									}
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int whichButton) {
									dialog.dismiss();

								}
							}).show();
		} else if (!NetUtil.isConnected(context)) {
			Toast.makeText(context, "您当前无网络", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	/*
	 * 单次购买生成订单号
	 */
	private void getOrderNum(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);
		params.put("app_nonce", app_nonce);
		params.put("lecture_integral", course_integral+"");
		params.put("adress", "");
		params.put("phone", "");
		params.put("real_name", "");
		params.put("phone_type", "1");		
		LogUtils.e("单词购买订单参数："+params.toString());
		NetHelper.post(Constant.INTEGRAL, params,
				new SimpleSingleBeanNetHandler<Captcha>(this) {

					@Override
					protected void onSuccess(Captcha bean) {
						LogUtils.e("单次购买订单数据：result=" + bean.result + "\ninfo="
								+ bean.message);
						if (bean.result.equals("0")) {
							try {
								JSONObject jsonObject = new JSONObject(bean.info);
								order_num =  jsonObject.getString("order_sn");
								Log.e("", "订单号========" + order_num);
								if (!TextUtils.isEmpty(order_num)) {
										boolean isWXInstatleAndSuppose = isWeiXinInstatleAndSuppose();
										if(isWXInstatleAndSuppose){
											GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
											getPrepayId.execute();
										}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							Toast.makeText(context, bean.message + "",
									Toast.LENGTH_SHORT).show();
						}
						
						dialog.hide();
					}

				});
	}
	
	
	
	/*
	 * -----------------------------微信支付------------------------------------
	 */
	private void weixinPayInit() {

	}

	private void registerBoradcastReceiver() {
//		IntentFilter myIntentFilter = new IntentFilter();
//		myIntentFilter.addAction(WXPayEntryActivity.ACTION_WXPayEntryCallback);
//		// 注册广播
//		registerReceiver(broadcastReceiver, myIntentFilter);
	}

	
	//判断手机是否有安装微信客户端或安装支持支付功能的客户端
	private boolean isWeiXinInstatleAndSuppose(){
//		IWXAPI wxapi = WXAPIFactory.createWXAPI(context, Constant.APP_ID, true);
//		wxapi.registerApp(Constant.APP_ID);

		return true;
	}
	
	// app支付生成预支付订单
	private class GetPrepayIdTask extends
			AsyncTask<Void, Void, Map<String, String>> {

		private Dialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = LoadingDialogShow.loading(VipPayActivity.this);
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			if (dialog != null) {
				dialog.dismiss();
			}
			for (Entry<String, String> map : result.entrySet()) {
				Log.e("map", map.getKey() + " = " + map.getValue());
			}
			resultunifiedorder = result;
			// 生成prepay_id
			Log.e("", "异步请求结果result.prepay_id = " + result.get("prepay_id"));

			// 生成签名参数
			genPayReq();

			sendPayReq();
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String, String> doInBackground(Void... params) {
			String url = String
					.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			LogUtils.e("异步加载====== doInBackground   1111");
			String entity = genProductArgs();

			LogUtils.e("异步加载====== doInBackground   2222");
			Log.e("orion------", entity);

			byte[] buf = Util.httpPost(url, entity);
			String content = new String(buf);
			Log.e("orion/////////", content);
			Map<String, String> xml = PurchasePayUtil.getInstance(context)
					.decodeXml(content);

			return xml;
		}
	}

	// 调用微信支付
	private void sendPayReq() {

	}

	private String genProductArgs() {
		LogUtils.e("--------------------------------------111");
		StringBuffer xml = new StringBuffer();
		try {
			String nonceStr = PurchasePayUtil.genNonceStr();
			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", Constant.APP_ID));
			packageParams.add(new BasicNameValuePair("body",shopName));
			packageParams
					.add(new BasicNameValuePair("mch_id", Constant.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url", notify_url));//回调地址
			packageParams
					.add(new BasicNameValuePair("out_trade_no", order_num));
			packageParams.add(new BasicNameValuePair("spbill_create_ip",
					PurchasePayUtil.getInstance(context).gainIpAddress()));
			packageParams.add(new BasicNameValuePair("total_fee", 1+""));// 测试金额
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));
			String sign = PurchasePayUtil.getInstance(context).genPackageSign(
					packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));
			String xmlstring = PurchasePayUtil.getInstance(context).toXml(
					packageParams);
			// return xmlstring;
			LogUtils.e("--------------------------------------222");
			return new String(xmlstring.toString().getBytes(), "ISO8859-1");

		} catch (Exception e) {
			Log.e("shoppay", "genProductArgs fail, ex = ====" + e.getMessage());
			return null;
		}
	}

	// 生成签名参数
	private void genPayReq() {


		List<NameValuePair> signParams = new LinkedList<NameValuePair>();

		Log.e("orion", "签名参数====" + signParams.toString());
	}

	// --------------- 微信支付 END ---------------------------//

}
