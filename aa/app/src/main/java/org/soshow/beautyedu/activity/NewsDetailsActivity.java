package org.soshow.beautyedu.activity;


import org.soshow.beautyedu.R;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

/**
 * 新闻详情
 */
public class NewsDetailsActivity extends BaseActivity {
	private TextView title_name ;

	private WebView news_web;
	private SharedPreferences sp;
	private String url;
	private ShareAction action;
	private PopupWindow window;
	private UMImage image;
	private View mView;
	private String images;
	private String title;
	private ImageView right_title;
	private LinearLayout loading;
	private String content;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_news_details);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_logininput);
		mView = LayoutInflater.from(this).inflate(R.layout.activity_news_details,null);
		url = getIntent().getStringExtra("url");
		title = getIntent().getStringExtra("title");
		content = getIntent().getStringExtra("content");
		images = getIntent().getStringExtra("images");
		right_title = (ImageView)findViewById(R.id.right_title);
		right_title.setVisibility(View.VISIBLE);
	    right_title.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopupWindow(url);
			}
		});
		loading = (LinearLayout) findViewById(R.id.linear_load);
		loading.setVisibility(View.VISIBLE);
		action = new ShareAction(this);
		title_name = (TextView) findViewById(R.id.title_name);
		news_web = (WebView) findViewById(R.id.news_web);
		news_web.loadUrl(url);
		WebSettings webSettings = news_web.getSettings();
		webSettings.setDefaultTextEncodingName("UTF-8");
		title_name.setText(R.string.news_title);

		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
//		getTokenLocal();
//		getInfo();
		news_web.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				return super.shouldOverrideUrlLoading(view, url);
			}
			@Override
			public void onPageFinished(WebView view, String url) {
				loading.setVisibility(View.GONE);
				super.onPageFinished(view, url);
			}
		});
		
	}
	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onStart(SHARE_MEDIA share_media) {

		}

		@Override
		public void onResult(SHARE_MEDIA share_media) {

		}

		@Override
		public void onError(SHARE_MEDIA share_media, Throwable throwable) {

		}

		@Override
		public void onCancel(SHARE_MEDIA share_media) {

		}
	};

	private void showPopupWindow(String shareUrl) {
		if (window == null) {
			window = new PopupWindow(initPop(shareUrl),
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		}
		window.setFocusable(true);
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		window.setBackgroundDrawable(new BitmapDrawable());
		window.showAtLocation(mView, Gravity.BOTTOM, 100, 0);
		window.showAsDropDown(mView);
	}

	private View initPop(final String shareUrl) {
		View view = LayoutInflater.from(this).inflate(R.layout.popupwindow_share, null);
		view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissSharePop();
			}
		});
		view.findViewById(R.id.ll_share_wechat).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share(SHARE_MEDIA.WEIXIN, shareUrl);
			}
		});
		view.findViewById(R.id.ll_share_circle).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share(SHARE_MEDIA.WEIXIN_CIRCLE, shareUrl);
			}
		});
		view.findViewById(R.id.ll_share_fav).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share(SHARE_MEDIA.WEIXIN_FAVORITE, shareUrl);
			}
		});
		return view;
	}

	private void dismissSharePop() {
		if (window != null) {
			window.dismiss();
		}
	}

	private void share(SHARE_MEDIA platform, String shareUrl) {
		if (TextUtils.isEmpty(images)) {
			image = new UMImage(this,
					BitmapFactory.decodeResource(getResources(), R.drawable.logo_new));
		} else {
			image = new UMImage(this, images);
		}
		UMWeb web = new UMWeb(shareUrl);
		web.setTitle(title);//标题
		web.setThumb(image);  //缩略图
		web.setDescription(content);//描述
		action.setPlatform(platform)
				.setCallback(umShareListener)
//				.withText(title)
//				.withTargetUrl(shareUrl)
				.withMedia(web)
				.share();
		dismissSharePop();
	}

	public void onClick(View v) {
		// Intent intent;

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

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}

}
