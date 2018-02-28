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
import org.soshow.beautyedu.bean.CommentInfo;
import org.soshow.beautyedu.bean.CourseInfoDetail;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.PurchasePayUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TimeUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.widget.LoadingDialogShow;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
 ;

public class OfflineCoursePayActivity extends BaseActivity implements
		OnClickListener {
	private String TAG = "OfflineCoursePay";
	private TextView title_name;
	private String app_nonce;
	private String mToken;
	private SharedPreferences sp;
	private Editor editor;
	private Dialog dialog;
	private String type_id;
	private Context context;
	private String content_url;

	/*
	 * 评论数据
	 */
	private TextView tvCount;
	private View tvView;// 显示更多
	private int startPage = 1;
	private int pageSize = 10;
	private boolean hasMore;
	private CourseInfoDetail courseInfoDetail;


	Map<String, String> resultunifiedorder;
	private String order_num;

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
						// signUp();
						getRecommomt(startPage);
					} else {
						Toast.makeText(OfflineCoursePayActivity.this,
								"网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
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
	private String from;
	private LinearLayout llRecomment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_offline_course_detail_pay);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);

		context = OfflineCoursePayActivity.this;

		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		type_id = getIntent().getStringExtra("offline_id");
		content_url = getIntent().getStringExtra("content_url");
		LogUtils.e("图文详情webview=" + content_url);
		from = getIntent().getStringExtra("from");

		dialog = ProgressDialogUtil.createLoadingDialog(
				OfflineCoursePayActivity.this, null, true, false);

		initView();
		getTokenLocal(startPage);
		weixinPayInit();
	}

	@Override
	protected void onRestart() {
		LogUtils.e("onRestart------------");
		if(AllCommentActivity.IS_ADD_RECOMMENT){
			getRecommomt(1);
		AllCommentActivity.IS_ADD_RECOMMENT = false;
		}
		super.onRestart();
	}

	private void initView() {
		title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText("报名详情");

		WebView linecourse_detail_web = (WebView) findViewById(R.id.line_course_detail_webView);
		WebSettings settings = linecourse_detail_web.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDefaultTextEncodingName("UTF-8");
		// 适应手机
		settings.setUseWideViewPort(false);
		settings.setLoadWithOverviewMode(true);
		// 自动加载图片
		settings.setLoadsImagesAutomatically(true);
		settings.setBlockNetworkImage(false);
		settings.setDomStorageEnabled(true);
		//禁止缩放
		settings.setDisplayZoomControls(false);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		settings.setSupportZoom(false);
		linecourse_detail_web.setInitialScale(0);
        settings.setBuiltInZoomControls(false);//显示缩放效果
        linecourse_detail_web.setHorizontalScrollBarEnabled(false);
		
		linecourse_detail_web.loadUrl(content_url);
		linecourse_detail_web.setWebChromeClient(new WebChromeClient());
		linecourse_detail_web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		loading = (LinearLayout) findViewById(R.id.linear_load);
		tvCount = (TextView) findViewById(R.id.off_line_free_tv_recommomt);
		llRecomment = (LinearLayout) findViewById(R.id.offline_free_myCustomListView);
		tvView = getLayoutInflater().inflate(R.layout.view_nodata_textview,
				null);
		tvNodata = (TextView) tvView.findViewById(R.id.tv_nodata);
		tvNodata.setTextColor(getResources().getColor(R.color.text_yellow));
		tvNodata.setBackgroundResource(R.drawable.shape_bg_tv_yellow);

	}

	private void weixinPayInit() {

		registerBoradcastReceiver(); // 注册广播
	}

	private void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();

		// 注册广播
		registerReceiver(broadcastReceiver, myIntentFilter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}

	@Override
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

	private void getTokenLocal(int page) {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(OfflineCoursePayActivity.this).getToken();

		if (mToken != null) {
			loading.setVisibility(View.VISIBLE);
			if (from.equals(LineCourseActivity.OFF_LINE)) {
				getDetailInfo();
			}
			getRecommomt(startPage);

		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}
		// Builder logout_confirm = new MyAlertDialog.Builder(
		// OfflineCoursePay.this);
		// logout_confirm
		// .setTitle("提示")
		// .setMessage("确定报名?")
		// .setPositiveButton("确定",
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface arg0,
		// int arg1) {
		// signUp();
		// arg0.dismiss();
		// }
		// })
		// .setNegativeButton("取消",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog,
		// int whichButton) {
		// dialog.dismiss();
		// }
		// }).show();

	}

	/*
	 * 获取评论数据
	 */
	private void getRecommomt(int page) {
		String url = "";
		if (from.equals(TuwenActivity.TU_WEN)) {
			url = Constant.LECTURE_COMMENT_LIST + "&tocken=" + mToken
					+ "&app_nonce=" + app_nonce + "&lecture_id=" + type_id
					+ "&page_no=" + page + "&page_size=" + pageSize;
			Log.e("", "图文课程评论列表URL=" + url);
		} else if (from.equals(LineCourseActivity.OFF_LINE)) {
			url = Constant.OFFLINE_COMMENT_LIST + "&tocken=" + mToken
					+ "&app_nonce=" + app_nonce + "&offline_id=" + type_id
					+ "&page_no=" + 1 + "&page_size=" + 2;
			Log.e("", "线下课程评论列表URL=" + url);
		}
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
						tvCount.setText(getResources().getString(
								R.string.course_recommomt)
								+ "(" + count + ")");
						tvNodata.setText("查看全部评论");
						llRecomment.addView(tvView);
						tvView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(
										OfflineCoursePayActivity.this,
										AllCommentActivity.class);
								intent.putExtra("from", from);
								intent.putExtra("type_id", type_id);
								startActivity(intent);
								overridePendingTransition(
										R.anim.anim_slider_right_in,
										R.anim.anim_slider_left_out);
							}
						});
						
					} else {
						ToastUtil.getInstance().showToast(
								OfflineCoursePayActivity.this, "数据获取异常");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				loading.setVisibility(View.GONE);
			}
		});
	}

	/*
	 * 线下报名操作
	 */
	private void signUp() {
		dialog.show();
		String url = Constant.LINE_LIST_DETAIL + "&tocken=" + mToken
				+ "&app_nonce=" + app_nonce + "&offline_id=" + type_id;
		Log.e(TAG, "报名URL=" + url);
		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);
		params.put("app_nonce", app_nonce);
		params.put("offline_id", type_id);
		NetHelper.post(Constant.LINE_LIST_DETAIL, params,
				new SimpleSingleBeanNetHandler<Captcha>(this) {

					@Override
					protected void onSuccess(Captcha bean) {
						dialog.hide();
						String messsage = bean.message;
						int result = Integer.parseInt(bean.result);
						Toast.makeText(OfflineCoursePayActivity.this,
								messsage + "", Toast.LENGTH_SHORT).show();
						String data = bean.info;
						if (!TextUtils.isEmpty(data)) {
							try {
								order_num = new JSONObject(data)
										.getString("order_num");
								Log.e("", "订单号========" + order_num);
								if (!TextUtils.isEmpty(order_num)) {
									GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
									getPrepayId.execute();
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}

				});
	}

	// 获取详情信息
	private void getDetailInfo() {
		String url_type = Constant.OFFLINE_INFO + "&tocken=" + mToken
				+ "&app_nonce=" + app_nonce + "&offline_id=" + type_id;
		Log.e(TAG, "线下课程详情url=" + url_type);
		NetHelper.get(url_type, new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				String result = bean.result;
				try {
					if (result.equals("0")) {
						String info = bean.info;
						JSONObject jsonObject = new JSONObject(info);
						courseInfoDetail = GsonUtils.parseJSON(
								jsonObject.toString(), CourseInfoDetail.class);
						// tvTitle.setText(courseInfoDetail.getTitle());
						// tvOldPrice.setText("￥" +
						// courseInfoDetail.getPrice());
						// tvNewPrice.setText("￥"
						// + courseInfoDetail.getVip_price());
						// UniversalImageLoadTool.disPlayTrue(
						// courseInfoDetail.getImg_url(), ivHead,
						// R.drawable.defaultpic);
					} else {
						ToastUtil.getInstance().showToast(
								OfflineCoursePayActivity.this, "数据请求异常");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	// TODO
	// --------------- 微信支付 ---------------------------//
	// app支付生成预支付订单
	private class GetPrepayIdTask extends
			AsyncTask<Void, Void, Map<String, String>> {

		private Dialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = LoadingDialogShow.loading(OfflineCoursePayActivity.this);
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
			String entity = genProductArgs();

			Log.e("orion", entity);

			byte[] buf = Util.httpPost(url, entity);
			String content = new String(buf);
			Log.e("orion", content);
			Map<String, String> xml = PurchasePayUtil.getInstance(context)
					.decodeXml(content);

			return xml;
		}
	}

	// 调用微信支付
	private void sendPayReq() {

	}

	private String genProductArgs() {
		StringBuffer xml = new StringBuffer();
		try {
			String nonceStr = PurchasePayUtil.genNonceStr();
			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", Constant.APP_ID));
			packageParams.add(new BasicNameValuePair("body", "weixin"));
			packageParams
					.add(new BasicNameValuePair("mch_id", Constant.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url",
					Constant.NOTIFY_URL_OFF_LINE));
			packageParams
					.add(new BasicNameValuePair("out_trade_no", order_num));
			packageParams.add(new BasicNameValuePair("spbill_create_ip",
					PurchasePayUtil.getInstance(context).gainIpAddress()));
			packageParams.add(new BasicNameValuePair("total_fee", 0.1 + ""));// 测试金额
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));
			String sign = PurchasePayUtil.getInstance(context).genPackageSign(
					packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));
			String xmlstring = PurchasePayUtil.getInstance(context).toXml(
					packageParams);
			// return xmlstring;
			return new String(xmlstring.toString().getBytes(), "ISO8859-1");

		} catch (Exception e) {
			Log.e("shoppay", "genProductArgs fail, ex = ====" + e.getMessage());
			return null;
		}
	}

	// 生成签名参数
	private void genPayReq() {


	}

	// --------------- 微信支付 END ---------------------------//

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, Intent intent) {
			String action = intent.getAction();

		}
	};
	private TextView tvNodata;
	private LinearLayout loading;

}
