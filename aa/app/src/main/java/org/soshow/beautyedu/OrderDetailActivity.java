package org.soshow.beautyedu;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
import org.soshow.basketball.common.adapter.CommonAdapter;
import org.soshow.basketball.common.adapter.ViewHolder;
import org.soshow.beautydu.wxpay.Util;
import org.soshow.beautyedu.bean.PayDetail;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.PurchasePayUtil;
import org.soshow.beautyedu.utils.SPUtils;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.LoadingDialogShow;

import android.annotation.SuppressLint;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 积分卡支付页面
 * @author chenjiaming
 *
 */
public class OrderDetailActivity extends Activity implements OnClickListener{

	private CommonAdapter<PayDetail> adapter;
	private String app_nonce;
	private String mToken;
	private Context context;
	private SharedPreferences sp;
	private Editor editor;
	private ArrayList<PayDetail> payDetails;
	private String pay_type;
	private ImageView ivWeixin;
	private ImageView ivZhifubao;
	
	 @SuppressLint("HandlerLeak")
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
							// photoInfos.clear();
							getInfo(type);
						} else {
							Toast.makeText(OrderDetailActivity.this,
									"网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
							if (OrderDetailActivity.this != null) {
								Toast.makeText(OrderDetailActivity.this,
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
	private String order_num;//订单号

	Map<String, String> resultunifiedorder;
	private Dialog dialog;
	
	
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
//							String card_integral = (String) SPUtils.get(OrderDetailActivity.this, "card_integral", "");
//							if(!TextUtils.isEmpty(card_integral)){
//								currentInteger = currentInteger + Integer.valueOf(card_integral);
//							}
//							SPUtils.put(OrderDetailActivity.this, "card_integral", currentInteger+"");//当前积分
//							Intent data = new Intent();
//							((Activity) context).setResult(1, data);
//							finish();
//						}
//					}, 500);
//
//					break;
//				case -1:
//					ToastUtil.getInstance().showToast(context,
//							R.string.pay_fail);
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
	private String goods_id;
	private String goods_num;
	private String shopcard_id;
	private boolean isCar;
	private String type;
	private String notify_url = Constant.NOTIFY_URL_INTEGER_WEIXIN;
	private String shopName;//支付商品名
	private int currentInteger;//当前积分
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_order_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		context = OrderDetailActivity.this;
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		initView();
		goods_id = getIntent().getStringExtra("goods_id");
		goods_num = getIntent().getStringExtra("goods_num");
		isCar = getIntent().getBooleanExtra("isCar", false);
		//获取支付详情
		type = Constant.VIDEOCARDCART+"&act=makeoder";
		getDetailInfo();
		pay_type = "weixin";//默认微信支付
		weixinPayInit();
	}

	private void initView() {
		dialog = ProgressDialogUtil.createLoadingDialog(OrderDetailActivity.this, null,
				true, false);
		dialog.show();
		TextView title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText(R.string.pay_detail);
		
		payDetails = new ArrayList<PayDetail>();
		
		ListView listview = (ListView) findViewById(R.id.pay_detail_listView);
		adapter = new CommonAdapter<PayDetail>(OrderDetailActivity.this, payDetails,
				R.layout.item_pay_detail) {

			@Override
			public void convert(ViewHolder viewHolder, PayDetail listInfo) {
				TextView tvTitle = viewHolder.getView(R.id.shoppingCarItem_tv_name);
				TextView tvMoney = viewHolder.getView(R.id.shoppingCarItem_tv_new_money);
				TextView tvNum = viewHolder
						.getView(R.id.shoppingCarItem_tv_num);
				TextView tvPerPrice = viewHolder.getView(R.id.tv_item_shoping_card_money);
				TextView tvPayMoney = viewHolder.getView(R.id.tv_item_detail_paymoney);
				ImageView ivPic = viewHolder.getView(R.id.shoppingCarItem_iv_photo);
				tvTitle.setText(listInfo.getCard_title());
				tvMoney.setText("￥"+listInfo.getPrice());
				tvNum.setText("X"+listInfo.getGoods_num());
				String price = listInfo.getPrice();
				String number = listInfo.getGoods_num();
				int goodsNum = 0;
				double pri = 0;
				if(!TextUtils.isEmpty(price)){
					pri = Double.valueOf(price);
				}
				if(!TextUtils.isEmpty(number)){
					goodsNum = Integer.valueOf(number);
				}
				tvPayMoney.setText(String.format(OrderDetailActivity.this.getResources().getString(
                        R.string.money_describe), String
                        .valueOf(new DecimalFormat("0.00")
                                .format(pri * goodsNum))));
				tvPerPrice.setText(String.format(OrderDetailActivity.this.getResources().getString(
                        R.string.money_describe), String
                        .valueOf(new DecimalFormat("0.00")
                                .format(pri * goodsNum))));
				UniversalImageLoadTool.disPlayTrue(listInfo.getCard_imageurl(), ivPic, R.drawable.defaultpic);
			}
		};
		
		View footView = getLayoutInflater().inflate(R.layout.item_pay_detail_foot, null);
		footView.findViewById(R.id.weixin_rl_pay).setOnClickListener(this);
		footView.findViewById(R.id.zhifubao_rl_pay).setOnClickListener(this);
		footView.findViewById(R.id.pay_activity_tv_pay).setOnClickListener(this);
		ivWeixin = (ImageView) footView.findViewById(R.id.weixin_iv_state);
		ivZhifubao = (ImageView) footView.findViewById(R.id.zhifubao_iv_state);
		listview.addFooterView(footView);
		listview.setAdapter(adapter);
	}
	
	 @Override
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
				type = Constant.VIDEOCARDORDER;
				dialog.show();
				getDetailInfo();
//					getTokenLocal();
				break;
	        default:
	            break;
	        }

	    }
	 
	//数据初始化
	private void getDetailInfo() {
  		app_nonce = StringUtil.getPhoneIMEI(this);
  		mToken = new TokenManager(this).getToken();
  		if (mToken != null) {
  			getInfo(type);
  		} else {
  			TokenManager.handlered = false;
  			Message msg_loop = handler.obtainMessage();
  			msg_loop.what = 2;
  			msg_loop.sendToTarget();
  		}

  	}
	
	private void getInfo(final String type){
		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);
		params.put("app_nonce", app_nonce);
		params.put("goods_id", goods_id);
		params.put("goods_num", goods_num);
		if(isCar){
			shopcard_id = getIntent().getStringExtra("shop_id");
			params.put("shopcart_id", shopcard_id);
		}
		
		if(type.equals(Constant.VIDEOCARDORDER)){//生成订单
			params.put("adress", "");
			params.put("phone", "");
			params.put("real_name", "");
			params.put("phone_type", "1");
		}
		
		LogUtils.e("订单参数："+params.toString()+"\n接口="+type);
		NetHelper.post(type, params,
				new SimpleSingleBeanNetHandler<Captcha>(this) {

					@Override
					protected void onSuccess(Captcha bean) {
						LogUtils.e("订单数据：result=" + bean.result + "\ninfo="
								+ bean.message);
						if (bean.result.equals("0")) {
							String info = bean.info;
							if(type.equals(Constant.VIDEOCARDORDER)){//生成订单
								dialog.hide();
								try {
									JSONObject jsonObject = new JSONObject(info);
									order_num =  jsonObject.getString("order_sn");
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
													OrderDetailActivity.this, "该功能待开发");
										}
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
								
							}else{//订单详情
								try {
									JSONArray jsonArray = new JSONObject(info).getJSONArray("goods_info");
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject obj = (JSONObject) jsonArray.get(i);
										PayDetail payDetail = GsonUtils.parseJSON(obj.toString(), PayDetail.class);
										payDetails.add(payDetail);
										String card_integral = payDetail.getCard_integral();
										if(!TextUtils.isEmpty(card_integral)){
											currentInteger = currentInteger + Integer.valueOf(card_integral);
										}
										if(i == 0){
											shopName = payDetail.getCard_title();
										}
									}
									
									adapter.notifyDataSetChanged();
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							
						} else {
							Toast.makeText(context, bean.message + "",
									Toast.LENGTH_SHORT).show();
						}
						
						dialog.hide();
					}

				});
	}
	
	
	//支付
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

//			if (type.equals(RechargesActivity.VIP_CODE)) {
//				url = Constant.VIP_ORDER + "&tocken=" + mToken + "&app_nonce="
//						+ app_nonce + "&" + type + "=" + type_id;
//				Log.e("", "VIP生成订单URL=" + url);
//				urlOrder = Constant.VIP_ORDER;
//				notify_url = Constant.NOTIFY_URL_VIP;
//			} else if (type.equals(CoursesPlayActivity.COURSE)) {
//				url = Constant.LECTURE_ORDER + "&tocken=" + mToken + "&app_nonce="
//						+ app_nonce + "&" + type + "=" + type_id;
//				Log.e("", "培训课程生成订单URL=" + url);
//				urlOrder = Constant.LECTURE_ORDER;
//				notify_url = Constant.NOTIFY_URL_LECTURE;
//			} else if (type.equals(LineCourseActivity.OFF_LINE_CODE)) {
//				url = Constant.OFFLINE_ORDER + "&tocken=" + mToken + "&app_nonce="
//						+ app_nonce + "&" + type + "=" + type_id;
//				Log.e("", "线下课程生成订单URL=" + url);
//				urlOrder = Constant.OFFLINE_ORDER;
//				notify_url = Constant.NOTIFY_URL_OFF_LINE;
//			}

			Map<String, String> params = new HashMap<String, String>();
			params.put("tocken", mToken);
			params.put("app_nonce", app_nonce);
//			params.put(type, type_id);
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
														OrderDetailActivity.this, "该功能待开发");
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



		// --------------- 微信支付 ---------------------------//
		
		//判断手机是否有安装微信客户端或安装支持支付功能的客户端
		private boolean isWeiXinInstatleAndSuppose(){
//			IWXAPI wxapi = WXAPIFactory.createWXAPI(context, Constant.APP_ID, true);
//			wxapi.registerApp(Constant.APP_ID);


			return true;
		}
		
		// app支付生成预支付订单
		private class GetPrepayIdTask extends
				AsyncTask<Void, Void, Map<String, String>> {

			private Dialog dialog;

			@Override
			protected void onPreExecute() {
				dialog = LoadingDialogShow.loading(OrderDetailActivity.this);
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
			 ;
			Log.e("orion", "签名参数====" + signParams.toString());
		}

		// --------------- 微信支付 END ---------------------------//
		
		@Override
		protected void onDestroy() {
			if(dialog.isShowing()){
				dialog.dismiss();;
				dialog = null;
			}
			super.onDestroy();
		}

}
