package org.soshow.beautyedu;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.activity.LoginInputActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.bean.IntegerInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @Author: caixiaozhen
 * @Time: 2015年10月8日 下午3:46:46
 * @Description: 商品详情
 */
public class ShopDetailActivity extends BaseActivity implements
        OnClickListener {


    private IntegerInfo integerInfo;
	private String app_nonce;
	private String mToken;
	private LinearLayout loading;
	private Context context;
	private SharedPreferences sp;
	private Editor editor;
	
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 2:// token获取
				if (LoginUtil.login_handlered) {
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
					} else {
						Toast.makeText(ShopDetailActivity.this,
								"网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
						if (ShopDetailActivity.this != null) {
							Toast.makeText(ShopDetailActivity.this,
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
	private EditText etNum;
	private Integer num;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_shop_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		
		context = ShopDetailActivity.this;
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		integerInfo = (IntegerInfo) getIntent().getSerializableExtra("integerInfo");
        initView();

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
    	TextView title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText(R.string.video_look);
		
		loading = (LinearLayout) findViewById(R.id.linear_load);
		ImageView iv = (ImageView) findViewById(R.id.shop_detail_iv);
		TextView tvName = (TextView) findViewById(R.id.shop_detail_tv_name);
		TextView tvNewPrice = (TextView) findViewById(R.id.shop_detail_tv_new_price);
		TextView tvOldPrice = (TextView) findViewById(R.id.shop_detail_tv_old_price);
		TextView tvPayNum = (TextView) findViewById(R.id.shop_detail_tv_pay_num);
		TextView tvCard = (TextView) findViewById(R.id.shop_detail_add_iv);
		TextView tvAddtoCard = (TextView) findViewById(R.id.shop_detail_tv_add);
		TextView tvBuy = (TextView) findViewById(R.id.shop_detail_tv_buy);
		WebView web = (WebView) findViewById(R.id.shop_detail_webView);
		
		findViewById(R.id.shoppingCard_ll_down).setOnClickListener(this);;
		findViewById(R.id.shoppingCard_ll_up).setOnClickListener(this);
		etNum = (EditText) findViewById(R.id.shoppingCard_edi_num);
		Editable text = etNum.getText();
		Selection.setSelection(text, text.length());
		
		UniversalImageLoadTool.disPlayTrue(integerInfo.getCard_imgurl(), iv, R.drawable.defaultpic);
		tvName.setText(integerInfo.getCard_title());
		tvCard.setOnClickListener(this);
		tvAddtoCard.setOnClickListener(this);
		tvBuy.setOnClickListener(this);
		tvNewPrice.setText("￥"+integerInfo.getPrice());
		tvOldPrice.setText(integerInfo.getCost_price());
		tvOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
		tvPayNum.setText(integerInfo.getPay_num()+"人付款");
		
		
		
		WebSettings settings = web.getSettings();
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
		web.setInitialScale(0);
        settings.setBuiltInZoomControls(false);//显示缩放效果
        web.setHorizontalScrollBarEnabled(false);
		
		web.loadUrl(integerInfo.getInfo_url());
		web.setWebChromeClient(new WebChromeClient());
		web.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(integerInfo.getInfo_url());
				return true;
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
			
		case R.id.shop_detail_add_iv://购物车
			Intent intent = null;
			 if(MyApplication.logined == false){
				 intent = new Intent(this, LoginInputActivity.class); 
			 }else{
				 intent = new Intent(this, ShoppingCarActivity.class); 
				 
			 }
			 startActivity(intent);
			break;
		case R.id.shop_detail_tv_add://加入购物车
			if(MyApplication.logined == false){
				 intent = new Intent(this, LoginInputActivity.class); 
				 startActivity(intent);
			 }else{
				 addToCard();
			 }
			break;
		case R.id.shop_detail_tv_buy://立即购买
			if(MyApplication.logined == false){
				 intent = new Intent(this, LoginInputActivity.class); 
				 intent.putExtras(intent);
			 }else{
				 String strDownNum = etNum.getText().toString();
				 intent = new Intent(context,
			                OrderDetailActivity.class);
			        intent.putExtra("goods_id", integerInfo.getVideo_carid());
			        intent.putExtra("goods_num", strDownNum);
			        intent.putExtra("isCar", false);
			        overridePendingTransition(R.anim.anim_slider_right_in,
			                R.anim.anim_slider_left_out);
			        
			 }
			startActivity(intent);
			break;
			
		case R.id.shoppingCard_ll_down://数量下调
			String strDownNum = etNum.getText().toString();
            num = Integer.valueOf(strDownNum);
            num--;
            if (num < 1) {
                etNum.setText("1");
                num = 1;
            } else {
                etNum.setText(num + "");
            }
			break;
		case R.id.shoppingCard_ll_up://数量上调
			String strUpNum = etNum.getText().toString();
            num = Integer.valueOf(strUpNum);
            num++;
            etNum.setText(num + "");
			break;
			
		default:
			break;
		}

	}
    
 // 加入购物车
  	private void addToCard() {
  		app_nonce = StringUtil.getPhoneIMEI(this);
  		mToken = new TokenManager(this).getToken();
  		if (mToken != null) {
  			actionAdd();
  		} else {
  			TokenManager.handlered = false;
  			Message msg_loop = handler.obtainMessage();
  			msg_loop.what = 2;
  			msg_loop.sendToTarget();
  		}

  	}

  	/*
  	 * 加入购物车操作
  	 */
	private void actionAdd() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);
		params.put("app_nonce", app_nonce);
		params.put("goods_id", integerInfo.getVideo_carid());
		params.put("goods_num", etNum.getText().toString());
		LogUtils.e("加入购物车参数："+params.toString());
		NetHelper.post(Constant.VIDEOCARDCART+"&act=change", params,
				new SimpleSingleBeanNetHandler<Captcha>(this) {

					@Override
					protected void onSuccess(Captcha bean) {
						LogUtils.e("加入购物车操作：result=" + bean.result + "\ninfo="
								+ bean.message);
						if (bean.result.equals("0")) {
							Toast.makeText(context, "加入购物车",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(context, bean.message + "",
									Toast.LENGTH_SHORT).show();
						}
					}

				});
	}
	


}
