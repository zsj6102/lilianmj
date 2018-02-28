package org.soshow.beautyedu;

import org.soshow.beautyedu.activity.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SchoolWebActivity extends BaseActivity {

	private TextView title_name;
	private LinearLayout loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_school_web);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		
		String url = getIntent().getStringExtra("url");
		String name = getIntent().getStringExtra("name");
		title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText(name);
		loading = (LinearLayout) findViewById(R.id.linear_load);
		loading.setVisibility(View.VISIBLE);
		
		WebView school_webview = (WebView) findViewById(R.id.school_webview);
		WebSettings settings = school_webview.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDefaultTextEncodingName("UTF-8");
		// 设置可以缩放加载
		settings.setBuiltInZoomControls(true);
		// 适应手机
		settings.setUseWideViewPort(false);
		settings.setLoadWithOverviewMode(true);
		school_webview.loadUrl(url);
		school_webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				loading.setVisibility(View.GONE);
				super.onPageFinished(view, url);
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

}
