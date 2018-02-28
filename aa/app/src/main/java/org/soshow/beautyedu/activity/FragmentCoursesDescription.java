package org.soshow.beautyedu.activity;

/**
 * 标准课程播放界面简介
 */
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.json.utils.LogUtils;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.TextView;

public class FragmentCoursesDescription extends BaseFragment {
	private View rootView;
	// private SharedPreferences sp;
	public static FragmentCoursesDescription fmt;
	// private static int count = 0;
	public static boolean mHasLoadedOnce;
	private boolean isPrepared;
//	private WebView webView;
	private String lecture_content;

//	public FragmentCoursesDescription(String lecture_content) {
//		this.lecture_content = lecture_content;
//	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		fmt = this;
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_description, null);
			WebView webView = (WebView) rootView.findViewById(R.id.video_play_webView);
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
			webView.loadUrl(lecture_content);
			int w = View.MeasureSpec.makeMeasureSpec(0,
	                View.MeasureSpec.UNSPECIFIED);
	        int h = View.MeasureSpec.makeMeasureSpec(0,
	                View.MeasureSpec.UNSPECIFIED);
	        //重新测量
	        webView.measure(w, h);
			isPrepared = true;
//			lazyLoad();

		}
		LogUtils.e("简介================"+lecture_content);
//		// 缓存的rootView需要判断是否已经被加过parent，
//		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
//		ViewGroup parent = (ViewGroup) rootView.getParent();
//		if (parent != null) {
//			parent.removeView(rootView);
//		}

		return rootView;
	}


	@Override
	public void lazyLoad() {
		// TODO Auto-generated method stub
		if (!isPrepared || !isVisible || mHasLoadedOnce) {
			return;
		}

		mHasLoadedOnce = true;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try {
			fmt = null;
		} catch (Exception e) {
			// TODO: handle exception
		}

		super.onDestroy();
	}

}