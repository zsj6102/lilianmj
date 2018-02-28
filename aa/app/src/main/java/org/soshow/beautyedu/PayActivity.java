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
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautydu.wxpay.Util;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.PurchasePayUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TimeUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.LoadingDialogShow;
import org.soshow.beautyedu.widget.MyDialog;
import org.soshow.beautyedu.widget.PayFailDialog;
import org.soshow.beautyedu.widget.PaySuccessDialog;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class PayActivity extends BaseActivity implements OnClickListener {
	public static boolean isPay;

	Map<String, String> resultunifiedorder;
	private String order_num;

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, Intent intent) {
			String action = intent.getAction();
//			if (action.equals(WXPayEntryActivity.ACTION_WXPayEntryCallback)) {
//				int errCode = intent.getExtras().getInt("resp.errCode");
//				LogUtils.e("支付errCode=" + errCode);
//				switch (errCode) {
//				case 0:
//					final PaySuccessDialog.MyBuilder dailog = new PaySuccessDialog.MyBuilder(PayActivity.this);
//					dailog.setPositiveButton("立即学习",
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									ToastUtil.getInstance().showToast(context,"立即学习");
//									dialog.dismiss();
//									isPay = true;
//									finish();
//								}
//							});
//
//					dailog.setNagetiveButton("",
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									dialog.dismiss();
//									isPay = true;
//								}
//							});
//
//					MyDialog creatDialog = dailog.creatDialog();
//					creatDialog.show();
//
////					ToastUtil.getInstance().showToast(context,
////							R.string.pay_success);
////					new Handler().postDelayed(new Runnable() {
////
////						@Override
////						public void run() {
////
////							Intent data = new Intent();
////							((Activity) context).setResult(1, data);
////							finish();
////						}
////					}, 500);
//
//					break;
//				case -1:
//					final PayFailDialog.FailBuilder dailogFail = new PayFailDialog.FailBuilder(PayActivity.this);
//					dailogFail.setPositiveButton("选择其他支付方式",
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									dailogFail.dismiss();
//									isPay = false;
//								}
//							});
//
//					dailogFail.setNagetiveButton("",
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									dailogFail.dismiss();
//									isPay = false;
//								}
//							});
//
//					MyDialog creatFailDialog = dailogFail.creatDialog();
//					creatFailDialog.show();
//
////					ToastUtil.getInstance().showToast(context,
////							R.string.pay_fail);
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
						signUp();
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
	private Context context;
	private String app_nonce;
	private String mToken;
	private SharedPreferences sp;
	private Editor editor;
	private Dialog dialog;
	private String notify_url;// 回调地址
	private ImageView ivWeixin;
	private ImageView ivZhifubao;
	private String pay_type = "weixin";// 支付方式,默认支付方式为微信支付
	private String type;
	private String type_id;
	private TextView tvTitle;
	private ImageView iv;
	private String price;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_pay);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		context = PayActivity.this;
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		type = getIntent().getStringExtra("type");
		type_id = getIntent().getStringExtra(type);
		String strPrice = getIntent().getStringExtra("price");
		if(!TextUtils.isEmpty(strPrice)){
			int money = (int)(Double.valueOf(strPrice)*100);
			price = money+"";
			LogUtils.e("测试金额="+price);
		}
		initView();
		weixinPayInit();
		if (type.equals(RechargesActivity.VIP_CODE)) {
			iv.setImageResource(R.drawable.img_vip);
			getToken();
		}
	}

	private void getToken() {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(context).getToken();
		if (mToken != null) {
			vipInitTime();
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}

	/*
	 * vip充值界面获取时间节点
	 */
	private void vipInitTime() {
		String url_vip = Constant.USER_VIP_TIME + "&" + type + "=" + type_id
				+ "&tocken=" + mToken + "&app_nonce=" + app_nonce;
		Log.e("", "VIP有效期限url=" + url_vip);
		NetHelper.get(url_vip, new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				try {
					String result = bean.result;
					if (result.equals("0")) {
						String start = "";
						String end = "";
						String old = "";
						String info = bean.info;
						JSONObject jsonObject = new JSONObject(info);
						boolean is_vip = jsonObject.getBoolean("is_vip");
						String startTime = jsonObject.getString("start_time");
						String endTime = jsonObject.getString("end_time");
						String oldEndTime = jsonObject
								.getString("old_end_time");
						if (!TextUtils.isEmpty(startTime)) {
							start = TimeUtil.getStringTypeYear(Long
									.valueOf(startTime));
						}
						if (!TextUtils.isEmpty(endTime)) {
							end = TimeUtil.getStringYear(Long.valueOf(endTime));
						}
						if (!TextUtils.isEmpty(oldEndTime)) {
							old = TimeUtil.getStringTypeYear(Long
									.valueOf(oldEndTime));
						}
						LogUtils.e("start=" + start + "\nend="+end+"\nold="+old);
						tvTitle.setText("到期时间：" + end);
					} else {
						ToastUtil.getInstance()
								.showToast(context, bean.message);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void initView() {
		dialog = ProgressDialogUtil.createLoadingDialog(PayActivity.this, null,
				true, false);

		TextView title_name = (TextView) findViewById(R.id.title_name);

		title_name.setText(R.string.pay_title);
		iv = (ImageView) findViewById(R.id.pay_activity_iv);
		tvTitle = (TextView) findViewById(R.id.pay_activity_title);
		TextView tvPrice = (TextView) findViewById(R.id.pay_activity_tv_price);
		TextView pay = (TextView) findViewById(R.id.pay_activity_tv_pay);
		String img_url = getIntent().getStringExtra("img_url");
		if (img_url == null) {
			img_url = "";
		}
		UniversalImageLoadTool.disPlayTrue(img_url, iv, R.drawable.defaultpic);
		tvPrice.setText("￥" + getIntent().getStringExtra("price"));
		tvTitle.setText(getIntent().getStringExtra("title"));
		pay.setText(getResources().getString(R.string.sure_to_pay));
		pay.setOnClickListener(this);
		findViewById(R.id.weixin_rl_pay).setOnClickListener(this);
		findViewById(R.id.zhifubao_rl_pay).setOnClickListener(this);
		ivWeixin = (ImageView) findViewById(R.id.weixin_iv_state);
		ivZhifubao = (ImageView) findViewById(R.id.zhifubao_iv_state);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_search_logininput:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
			break;
		case R.id.weixin_rl_pay:
			pay_type = "weixin";
			ivWeixin.setImageResource(R.drawable.icon_choose_selecteds);
			ivZhifubao.setImageResource(R.drawable.icon_choose_normals);
			break;
		case R.id.zhifubao_rl_pay:
			pay_type = "zhifubao";
			ivZhifubao.setImageResource(R.drawable.icon_choose_selecteds);
			ivWeixin.setImageResource(R.drawable.icon_choose_normals);
			break;
		case R.id.pay_activity_tv_pay:
				getTokenLocal();
			
			break;

		default:
			break;
		}
	}

	private void getTokenLocal() {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(context).getToken();
		LogUtils.e("获取到的tocken=" + mToken+"\napp_nonce="+app_nonce);
		if (mToken != null) {
			signUp();
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}

	/*
	 * 购买课程生成订单操作
	 */
	private void signUp() {
		dialog.show();
		String url = "";
		String urlOrder = "";

		if (type.equals(RechargesActivity.VIP_CODE)) {
			url = Constant.VIP_ORDER + "&tocken=" + mToken + "&app_nonce="
					+ app_nonce + "&" + type + "=" + type_id;
			Log.e("", "VIP生成订单URL=" + url);
			urlOrder = Constant.VIP_ORDER;
			notify_url = Constant.NOTIFY_URL_VIP;
		} else if (type.equals(CoursesPlayActivity.COURSE)) {
			url = Constant.LECTURE_ORDER + "&tocken=" + mToken + "&app_nonce="
					+ app_nonce + "&" + type + "=" + type_id;
			Log.e("", "培训课程生成订单URL=" + url);
			urlOrder = Constant.LECTURE_ORDER;
			notify_url = Constant.NOTIFY_URL_LECTURE;
		} else if (type.equals(LineCourseActivity.OFF_LINE_CODE)) {
			url = Constant.OFFLINE_ORDER + "&tocken=" + mToken + "&app_nonce="
					+ app_nonce + "&" + type + "=" + type_id;
			Log.e("", "线下课程生成订单URL=" + url);
			urlOrder = Constant.OFFLINE_ORDER;
			notify_url = Constant.NOTIFY_URL_OFF_LINE;
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);
		params.put("app_nonce", app_nonce);
		params.put(type, type_id);
		NetHelper.post(urlOrder, params,
				new SimpleSingleBeanNetHandler<Captcha>(this) {

					@Override
					protected void onSuccess(Captcha bean) {
						dialog.hide();
						LogUtils.e("生成订单：result=" + bean.result + "\ninfo="
								+ bean.info);
						if (bean.result.equals("0")) {
							String data = bean.info;
							if (!TextUtils.isEmpty(data)) {
								try {
									order_num = new JSONObject(data)
											.getString("order_num");
									Log.e("", "订单号========" + order_num);
									if (!TextUtils.isEmpty(order_num)) {
										if (pay_type.equals("weixin")) {// 微信支付
											boolean isWXInstatleAndSuppose = isWeiXinInstatleAndSuppose();
											if(isWXInstatleAndSuppose){
												GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
												getPrepayId.execute();
											}
										} else if (pay_type.equals("zhifubao")) {// 支付宝支付
											ToastUtil.getInstance().showToast(
													PayActivity.this, "该功能待开发");
										}
									}

								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						} else {
							Toast.makeText(context, bean.message + "",
									Toast.LENGTH_SHORT).show();
						}

					}

				});
	}

	private void weixinPayInit() {

	}

	private void registerBoradcastReceiver() {
//		IntentFilter myIntentFilter = new IntentFilter();
//		myIntentFilter.addAction(WXPayEntryActivity.ACTION_WXPayEntryCallback);
//		// 注册广播
//		registerReceiver(broadcastReceiver, myIntentFilter);
	}

	// --------------- 微信支付 ---------------------------//
	
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
			dialog = LoadingDialogShow.loading(PayActivity.this);
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
			packageParams.add(new BasicNameValuePair("body", getIntent()
					.getStringExtra("title")));
			packageParams
					.add(new BasicNameValuePair("mch_id", Constant.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url", notify_url));
			packageParams
					.add(new BasicNameValuePair("out_trade_no", order_num));
			packageParams.add(new BasicNameValuePair("spbill_create_ip",
					PurchasePayUtil.getInstance(context).gainIpAddress()));
			packageParams.add(new BasicNameValuePair("total_fee", price));// 测试金额
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

	}

	// --------------- 微信支付 END ---------------------------//

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(broadcastReceiver);
	}

}
