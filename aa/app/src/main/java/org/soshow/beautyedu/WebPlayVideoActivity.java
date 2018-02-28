package org.soshow.beautyedu;

import java.lang.reflect.InvocationTargetException;

import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.json.utils.LogUtils;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WebPlayVideoActivity extends BaseActivity {

	private WebView web;
	private LinearLayout loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_web_play_video);
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
//				R.layout.titlebar_logininput);
		TextView title_name = (TextView) findViewById(R.id.title_name);
		RelativeLayout rlLayout = (RelativeLayout) findViewById(R.id.comment_title);
//		RelativeLayout rlLayout = (RelativeLayout) findViewById(R.id.title_commomt);
		title_name.setText(getIntent().getStringExtra("title"));
		if(getIntent().getStringExtra("title").equals("lunbo")){
			rlLayout.setVisibility(View.GONE);
		}else{
			rlLayout.setVisibility(View.VISIBLE);
		}
		loading = (LinearLayout) findViewById(R.id.linear_load);
		loading.setVisibility(View.VISIBLE);
		web = (WebView) findViewById(R.id.web_play_video_webView);
		WebSettings setting = web.getSettings();
//		settings.setJavaScriptEnabled(true);
//		settings.setPluginState(PluginState.ON);
//		settings.setPluginsEnabled(true);//4.1
//		// 设置可以缩放加载
//		settings.setBuiltInZoomControls(true);
//		// 适应手机
//		settings.setUseWideViewPort(true);
//		settings.setJavaScriptCanOpenWindowsAutomatically(true);
//		settings.setAllowFileAccess(true);
//		settings.setDefaultTextEncodingName("UTF-8");
//		settings.setLoadWithOverviewMode(true);
//		LogUtils.e("url=" + getIntent().getStringExtra("url"));
//		web.loadUrl(getIntent().getStringExtra("url"));
//		web.setWebViewClient(new WebViewClient() {
//			@Override
//			public boolean shouldOverrideUrlLoading(WebView view, String url) {
//				view.loadUrl(url);
//				return true;
//			}
//		});
		
		setting.setJavaScriptEnabled(true);
        setting.setDomStorageEnabled(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
 
        setting.setDatabaseEnabled(true);
        setting.setGeolocationEnabled(true);
        String dir = getDir("database", Context.MODE_PRIVATE).getPath();
        setting.setDatabasePath(dir);
        setting.setGeolocationDatabasePath(dir);
 
        setting.setAppCacheEnabled(true);
        String cacheDir = getDir("cache", Context.MODE_PRIVATE).getPath();
        setting.setAppCachePath(cacheDir);
        setting.setCacheMode(WebSettings.LOAD_DEFAULT);
        setting.setAppCacheMaxSize(1024 * 1024 * 10);
        setting.setAllowFileAccess(true);
 
        setting.setRenderPriority(RenderPriority.HIGH);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
         
        setting.setBuiltInZoomControls(true);
        setting.setDisplayZoomControls(false);
         LogUtils.e("url="+getIntent().getStringExtra("url"));
        web.loadUrl(getIntent().getStringExtra("url"));
        web.setWebViewClient(new WebViewClient(){
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

	@Override
	public void onPanelClosed(int featureId, Menu menu) {
		try {
			web.getClass().getMethod("onPause").invoke(web, (Object[]) null);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onPanelClosed(featureId, menu);
	}

	@Override
	protected void onResume() {
		try {
			web.getClass().getMethod("onResume").invoke(web, (Object[]) null);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if(loading != null){
			loading = null;
		}
		web.destroy();
		super.onDestroy();
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
	
	
  
  
    public static int getPhoneAndroidSDK() {  
        // TODO Auto-generated method stub  
        int version = 0;  
        try {  
            version = Integer.valueOf(android.os.Build.VERSION.SDK);  
        } catch (NumberFormatException e) {  
            e.printStackTrace();  
        }  
        return version;  
  
    }  

}
