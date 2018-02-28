package org.soshow.beautyedu.activity;

import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.Cat;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleMultiBeanNetHandler;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.TokenManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;
/**
 * 做题
 */
public class WebActivity extends Activity {

	private FrameLayout videoview;// 全屏时视频加载view
	// private Button videolandport;
	public static WebView videowebview;
	private Boolean islandport = true;// true表示此时是竖屏，false表示此时横屏。
	private View xCustomView;
	private xWebChromeClient xwebchromeclient;
	private WebChromeClient.CustomViewCallback xCustomViewCallback;

	// 动态网页部分
	private String url_base_video = Constant.phpUrl + "/wap/panel/video/videolist.php";
	private static String url_base_tiku = Constant.phpUrl + "/wap/panel/quest/quests.php";
	private TokenManager tManager;
	private String url_update;
	private String catID;
	private String mToken;
	// 版本信息变量
	// private SharedPreferences msp;
	// 请求码
	private static int REQUEST_CODE = 1;

	// 保存网页地址及登录信息
	private static String current_url;
	private SharedPreferences sp;
	private Editor editor;
	// 获取打开参数
	private Intent intent;
	private static int index;
	private static boolean token_changed = false;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:
				if (index == 0 || index == 1 || index == 2 || index == 3) {
					if (TokenManager.handlered) {
						Log.d("345abc", "webn获取新token");
						TokenManager.handlered = false;
						mToken = new TokenManager(WebActivity.this).getToken();
						if (mToken != null) {
							updateUrl(index);
						} else {
							Toast.makeText(WebActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
						}
					} else {
						new Timer().schedule(new TimerTask() {
							@Override
							public void run() {
								// 需要做的事:发送消息
								// LoginUtil.login_handlered = false;
								Log.d("345abc", "网页n循环等待");
								Message msg_loop = handler.obtainMessage();
								msg_loop.what = 2;
								msg_loop.sendToTarget();
							}
						}, 1000);

					}
				} else {
					if (LoginUtil.login_handlered) {
						Log.d("345abc", "webl获取新token");
						LoginUtil.login_handlered = false;
						mToken = sp.getString("mToken", null);
						if (mToken != null) {
							updateUrl(index);
						} else {
							Toast.makeText(WebActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
						}
					} else {
						new Timer().schedule(new TimerTask() {
							@Override
							public void run() {
								// 需要做的事:发送消息
								// LoginUtil.login_handlered = false;
								Log.d("345abc", "网页L循环等待");
								Message msg_loop = handler.obtainMessage();
								msg_loop.what = 2;
								msg_loop.sendToTarget();
							}
						}, 1000);

					}
				}

				break;
			case 88:

				if (timeing && WebActivity.this.videowebview.getProgress() < 100 && WebActivity.web_instance != null) {
					finish();
					Toast.makeText(WebActivity.this, R.string.net_error_warning, Toast.LENGTH_SHORT).show();
				}
				break;
			case 565:
				videowebview.loadUrl("javascript:recvAndroidMsg(1)");
				Toast.makeText(WebActivity.this, "延时", Toast.LENGTH_SHORT).show();
				// videowebview.loadUrl("javascript:funFromjs()");
				break;
			default:
				break;
			}
		}

	};
	public static WebActivity web_instance;
	private Dialog dialog;
	private Timer timer;
	private long timeout = 8000;
	private Boolean timeing = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉应用标题
		Log.d("webviewtest", "webview创建");
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_web);
		dialog = ProgressDialogUtil.createLoadingDialog(WebActivity.this, null, true, false);
		dialog.show();

		web_instance = this;
		intent = getIntent();
		index = intent.getIntExtra("catID", 99);
		sp = WebActivity.this.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		initwidget();
		tManager = new TokenManager(WebActivity.this);
		// int index = intent.getIntExtra("catID", 0);

		// catID = intent.getStringExtra("catID");
		// url_update = updateUrl();
		// if (url_update == null) {
		// Intent intent2 = new Intent(WebActivity.this, LoginActivity.class);
		// intent2.putExtra("catID", catID);
		// startActivity(intent2);
		// }

		// initListener();
		// videowebview.loadUrl(url_update);

		updateUrl(index);
		// videowebview.loadUrl("file:///android_asset/123.html");

	}

	// private void initListener() {
	// TODO Auto-generated method stub
	// videolandport.setOnClickListener(new Listener());
	// }

	@SuppressLint({ "JavascriptInterface", "NewApi" })
	@SuppressWarnings("deprecation")

	private void initwidget() {
		// TODO Auto-generated method stub
		videoview = (FrameLayout) findViewById(R.id.video_view);
		// videolandport = (Button) findViewById(R.id.video_landport);
		videowebview = (WebView) findViewById(R.id.video_webview);

		WebSettings ws = videowebview.getSettings();
		/**
		 * setAllowFileAccess 启用或禁止WebView访问文件数据 setBlockNetworkImage 是否显示网络图像
		 * setBuiltInZoomControls 设置是否支持缩放 setCacheMode 设置缓冲的模式
		 * setDefaultFontSize 设置默认的字体大小 setDefaultTextEncodingName 设置在解码时使用的默认编码
		 * setFixedFontFamily 设置固定使用的字体 setJavaSciptEnabled 设置是否支持Javascript
		 * setLayoutAlgorithm 设置布局方式 setLightTouchEnabled 设置用鼠标激活被选项
		 * setSupportZoom 设置是否支持变焦
		 */
		// ws.setBuiltInZoomControls(true);// 隐藏缩放按钮
		String cacheDirPath = getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
		// Log.d("123456", "网页cacheDirPath===" + cacheDirPath);

		ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕
		ws.setUseWideViewPort(false);// 可任意比例缩放
		ws.setLoadWithOverviewMode(true);// setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
		// ws.setSavePassword(true);

		ws.setJavaScriptEnabled(true);
		// ws.setGeolocationEnabled(true);// 启用地理定位
		// ws.setGeolocationDatabasePath("/data/data/com.huatianlong.tongkao/databases/");//
		// 设置定位的数据库路径
		ws.setDomStorageEnabled(true);
		// 未知效果
		ws.setSaveFormData(true);// 保存表单数据
		ws.setAllowFileAccess(true);
		ws.setDatabaseEnabled(true);
		ws.setDatabasePath(cacheDirPath);
		// 新增
		ws.setRenderPriority(RenderPriority.HIGH);

		// ws.setPluginState(PluginState.ON);
		// ws.setJavaScriptEnabled(true);
		if (index != 0 && index != 1 && index != 2 && index != 7) {
			Log.d("1221", "开启js");
			videowebview.addJavascriptInterface(new FinishWeb(WebActivity.this, handler), "finishWeb");
		}

		// ws.setUserAgentString(MyApplication.getUserAgent());
		ws.setAppCacheEnabled(true);
		ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		ws.setAppCachePath(cacheDirPath);
		ws.setDatabaseEnabled(true);
		ws.setAppCacheMaxSize(1024 * 1024 * 8);

		xwebchromeclient = new xWebChromeClient();
		videowebview.setWebChromeClient(xwebchromeclient);
		videowebview.setWebViewClient(new xWebViewClientent());
		// videowebview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

	}

	class Listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			default:
				break;
			}
		}

	}

	// 手机返回键监听
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			// Log.d("123321", "点击返回键");
			// 如果是全屏状态 按返回键则变成非全屏状态，否则执行返回操作
			if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				quitFullScreen();
			} else {
				// if (videowebview.canGoBack() && current_url != null &&
				// !current_url.contains("videolist.php")) {
				if (videowebview.canGoBack()) {
					videowebview.goBack();

				} else {

					finish();
				}
			}

			return true;
		default:
			break;
		}
		return false;
	}

	/**
	 * 退出全屏
	 */
	private void quitFullScreen() {
		// 声明当前屏幕状态的参数并获取

		final WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setAttributes(attrs);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		hideCustomView();
		// Log.d("123456", "点击退出全屏");
	}

	/**
	 * 判断是否是全屏
	 * 
	 * @return
	 */
	public boolean inCustomView() {
		return (xCustomView != null);
	}

	/**
	 * 全屏时按返加键执行退出全屏方法
	 */
	public void hideCustomView() {
		// Log.d("123456", " hideCustomView调用");
		xwebchromeclient.onHideCustomView();
	}

	/**
	 * 处理Javascript的对话框、网站图标、网站标题以及网页加载进度等
	 * 
	 * @author
	 */
	public class xWebChromeClient extends WebChromeClient {
		private Bitmap xdefaltvideo;
		private View xprogressvideo;

		@Override
		public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
				JsPromptResult result) {
			// TODO Auto-generated method stub
			// Toast.makeText(WebActivity.this, "收到onJsPrompt",
			// Toast.LENGTH_SHORT).show();
			return super.onJsPrompt(view, url, message, defaultValue, result);
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			// TODO Auto-generated method stub

			Toast.makeText(WebActivity.this, message, Toast.LENGTH_SHORT).show();
			// Log.d("1221", "alert信息为" + message);
			/*
			 * if (index == 9) { if (message.equals("收藏成功")) { if
			 * (FragmentMyTitle.listItems != null && FragmentMyTitle.sa != null
			 * && FragmentMyTitle.listItem_cache != null) { //
			 * FragmentMyTitle.listItems.add(); FragmentMyTitle.listItems.add(0,
			 * FragmentMyTitle.listItem_cache);
			 * FragmentMyTitle.sa.notifyDataSetChanged(); } } else if
			 * (message.equals("取消收藏成功")) { if (FragmentMyTitle.listItems !=
			 * null && FragmentMyTitle.sa != null &&
			 * FragmentMyTitle.listItem_cache != null) {
			 * FragmentMyTitle.listItems.remove(FragmentMyTitle.listItem_cache);
			 * FragmentMyTitle.sa.notifyDataSetChanged(); } } }
			 */
			result.confirm();

			return true;
			// return super.onJsAlert(view, url, message, result);
		}

		@Override
		public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
			// TODO Auto-generated method stub
			// Toast.makeText(WebActivity.this, "收到onJsConfirm",
			// Toast.LENGTH_SHORT).show();
			return super.onJsConfirm(view, url, message, result);
		}

		@Override
		// 播放网络视频时全屏会被调用的方法
		public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
			// Log.d("123456", "进入全屏");
			if (islandport) {

			} else {

				// ii = "1";
				// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			videowebview.setVisibility(View.GONE);
			// 如果一个视图已经存在，那么立刻终止并新建一个
			if (xCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}

			videoview.addView(view);
			xCustomView = view;
			xCustomViewCallback = callback;
			videoview.setVisibility(View.VISIBLE);
		}

		@Override
		// 视频播放退出全屏会被调用的
		public void onHideCustomView() {
			// Log.d("123456", "实际退出全屏");
			if (xCustomView == null) {// 不是全屏播放状态
				Log.d("123456", "xCustomView == null");
				return;
			}

			// Hide the custom view.
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			xCustomView.setVisibility(View.GONE);

			// Remove the custom view from its container.
			videoview.removeView(xCustomView);
			xCustomView = null;
			videoview.setVisibility(View.GONE);
			xCustomViewCallback.onCustomViewHidden();

			videowebview.setVisibility(View.VISIBLE);

			// Log.i(LOGTAG, "set it to webVew");
		}

		// 视频加载添加默认图标
		@Override
		public Bitmap getDefaultVideoPoster() {
			// Log.i(LOGTAG, "here in on getDefaultVideoPoster");
			if (xdefaltvideo == null) {
				// xdefaltvideo = BitmapFactory.decodeResource(getResources(),
				// R.drawable.videoicon);
			}
			return xdefaltvideo;

		}

		// 视频加载时进程loading
		@Override
		public View getVideoLoadingProgressView() {
			// Log.i(LOGTAG, "here in on getVideoLoadingPregressView");

			if (xprogressvideo == null) {
				LayoutInflater inflater = LayoutInflater.from(WebActivity.this);
				xprogressvideo = inflater.inflate(R.layout.video_loading_progress, null);
			}
			return xprogressvideo;
		}

		// 网页标题
		@Override
		public void onReceivedTitle(WebView view, String title) {
			(WebActivity.this).setTitle(title);
		}

		@Override
		public void onReachedMaxAppCacheSize(long requiredStorage, long quota, QuotaUpdater quotaUpdater) {
			// TODO Auto-generated method stub
			super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
			quotaUpdater.updateQuota(requiredStorage * 2);

		}

		// @Override
		// //当WebView进度改变时更新窗口进度
		// public void onProgressChanged(WebView view, int newProgress) {
		// (MainActivity.this).getWindow().setFeatureInt(Window.FEATURE_PROGRESS,
		// newProgress*100);
		// }

	}

	/**
	 * 处理各种通知、请求等事件
	 * 
	 * @author
	 */
	public class xWebViewClientent extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i("webviewtest", "shouldOverrideUrlLoading: " + url);
			if (url.contains("114so")) {
				Toast.makeText(WebActivity.this, R.string.net_error_warning, Toast.LENGTH_SHORT).show();

				WebActivity.this.finish();
			} else if (!url.equals(Constant.phpUrl + "//wap/") && !url.equals(Constant.phpUrl + "/wap/")
					&& !url.contains("login.php") && !url.contains("my.php") && !url.contains("mykc.html")
					&& url.contains(Constant.phpUrl) && url.contains("114so")) {

				current_url = url;

				// videowebview.loadUrl(url);
				// Log.i("webviewtest", "保存地址为: " + current_url);
				// } else if (url.equals(Constant.phpUrl + "//wap/") ||
				// url.equals(Constant.phpUrl + "/wap/")) {
				// WebActivity.this.finish();
			} else if (url.contains("login.php")) {
				Log.i("webviewtest", " 含有login");
				if (MyApplication.logined == false) {

					Intent intent1 = new Intent(WebActivity.this, LoginInputActivity.class);
					intent1.putExtra("key", 6);
					startActivityForResult(intent1, REQUEST_CODE);
					return true;
				} else {
					Log.i("webviewtest", " loginBackground");
					loginBackground();
				}
			} else if (url.contains("my.php") || url.contains("mykc.html")) {
				if (PracticeActivity.pratice_instance != null) {
					PracticeActivity.pratice_instance.finish();

				} else if (ErrorActivity.error_instance != null) {
					// Log.i("webviewtest", "shouldOverrideUrlLoading:
					// errorActivity不存在");
					ErrorActivity.error_instance.finish();
				}
				MyApplication.setTab1 = true;

				finish();
			}

			/*
			 * if (url.contains("login.php")) { Log.i("webviewtest", " 含有login"
			 * ); if (MyApplication.logined == false) {
			 * 
			 * Intent intent1 = new Intent(WebActivity.this,
			 * LoginInputActivity.class); intent1.putExtra("key", 6);
			 * startActivityForResult(intent1, REQUEST_CODE); return true; }
			 * else { Log.i("webviewtest", " loginBackground");
			 * loginBackground(); } } else if
			 * (!url.equals("http://192.168.10.20//wap/") &&
			 * !url.contains("my.php") && !url.contains("mykc.html")) {
			 * current_url = url; // videowebview.loadUrl(url);
			 * Log.i("webviewtest", "保存地址为: " + current_url); }
			 * 
			 * if (url.contains("my.php") || url.contains("mykc.html")) { if
			 * (PracticeActivity.pratice_instance != null) {
			 * PracticeActivity.pratice_instance.finish();
			 * 
			 * } else if (ErrorActivity.error_instance != null) { //
			 * Log.i("webviewtest", "shouldOverrideUrlLoading: //
			 * errorActivity不存在"); ErrorActivity.error_instance.finish(); }
			 * MyApplication.setTab1 = true; finish();
			 * 
			 * }
			 */
			return false;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
			Toast.makeText(WebActivity.this, R.string.net_error_warning, Toast.LENGTH_SHORT).show();

			finish();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			Log.i("webviewtest", "onPageStarted: " + url);
			super.onPageStarted(view, url, favicon);
			try {
				if ((!url.equals(Constant.phpUrl + "//wap/") && !url.equals(Constant.phpUrl + "/wap/")
						&& !url.contains("login.php") && !url.contains("my.php") && !url.contains("mykc.html")
						&& url.contains(Constant.phpUrl)) || !url.contains(Constant.phpUrl)) {
					try {
						if (dialog != null) {
//							dialog.show();
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					timer = new Timer();
					timeing = true;
					TimerTask tt = new TimerTask() {
						@Override
						public void run() {
							/*
							 * 超时后,首先判断页面加载进度,超时并且进度小于100,就执行超时后的动作
							 */
							try {

								Log.d("testTimeout", "timeout...........");
								Message msg = new Message();
								msg.what = 88;
								handler.sendMessage(msg);
								timer.cancel();
								timer.purge();

							} catch (Exception e) {
								// TODO: handle exception
								Log.d("123456", e.toString());
							}

						}
					};
					timer.schedule(tt, timeout);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			if (token_changed) {
				videowebview.clearHistory();
				token_changed = false;
			}
			switch (index) {
			case 3:// 题库
				videowebview.loadUrl("javascript:recvAndroidMsg(1)");
				// Toast.makeText(WebActivity.this,
				// "javascript:recvAndroidMsg(1)", Toast.LENGTH_SHORT).show();
				break;
			case 5:
				switch (Integer.parseInt(intent.getStringExtra("finished"))) {
				case 1:// 查看报告
					videowebview.loadUrl("javascript:recvAndroidMsg(2)");
					// Toast.makeText(WebActivity.this,
					// "javascript:recvAndroidMsg(2)",
					// Toast.LENGTH_SHORT).show();
					break;
				case 0:// 继续练习
					videowebview.loadUrl("javascript:recvAndroidMsg(5)");
					// Toast.makeText(WebActivity.this,
					// "javascript:recvAndroidMsg(5)",
					// Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
				break;
			case 6:// 我的错题
				videowebview.loadUrl("javascript:recvAndroidMsg(3)");
				// Toast.makeText(WebActivity.this,
				// "javascript:recvAndroidMsg(3)", Toast.LENGTH_SHORT).show();
				break;
			case 9:// 题目收藏
				videowebview.loadUrl("javascript:recvAndroidMsg(4)");
				// Toast.makeText(WebActivity.this,
				// "javascript:recvAndroidMsg(4)", Toast.LENGTH_SHORT).show();
				break;

			default:
				break;
			}
			Log.d("webviewtest", "onPageFinished:  " + url);
			if (timer != null) {
				timer.cancel();
				timer.purge();
				timeing = false;
			}
			if (dialog != null) {
				dialog.hide();
			}
			if (!url.equals(Constant.phpUrl + "//wap/") && !url.equals(Constant.phpUrl + "/wap/")
					&& !url.contains("login.php") && !url.contains("my.php") && !url.contains("mykc.html")
					&& url.contains(Constant.phpUrl) && url.contains("114so")) {

				current_url = url;

				// videowebview.loadUrl(url);
				// Log.i("webviewtest", "保存地址为: " + current_url);
				// } else if (url.equals(Constant.phpUrl + "//wap/") ||
				// url.equals(Constant.phpUrl + "/wap/")) {
				// WebActivity.this.finish();
			}
			super.onPageFinished(view, url);
		}

	}

	/**
	 * 当横竖屏切换时会调用该方法
	 * 
	 * @author
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i("testwebview", "=====<<<  onConfigurationChanged  >>>=====");
		super.onConfigurationChanged(newConfig);

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Log.i("webview", "   现在是横屏1");
			islandport = false;
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.i("webview", "   现在是竖屏1");
			islandport = true;
		}

	}

	@Override
	protected void onDestroy() {
		videowebview.destroy();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if (videowebview != null) {
			// videowebview.onPause();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		videowebview.onResume();
		super.onResume();
	}

	private void updateUrl(final int index) {
		try {
			mToken = tManager.getToken();
			if (mToken != null && !mToken.equals("")) {
				String name = null;
				// if (true) {
				switch (index) {
				case 0:
					name = "育婴员";
					break;
				case 1:
					name = "家政服务员";
					break;
				case 2:
					name = "养老护理";
					break;
				case 3:// 首页我的课程
					url_update = url_base_tiku + "?tocken=" + mToken;
					Log.d("123456", url_update);
					break;
				case 5:// 我的练习
					String paper_id = intent.getStringExtra("paper_id");
					// String mToen = sp.getString("mToken", null);
					switch (Integer.parseInt(intent.getStringExtra("finished"))) {
					case 1:// 查看报告
						url_update = Constant.phpUrl + "/wap/panel/quest/report.php?paper_id=" + paper_id + "&tocken="
								+ mToken;
						break;
					case 0:// 继续练习
						String cat1_id = intent.getStringExtra("cat1_id");
						String cat2_id = intent.getStringExtra("cat2_id");
						url_update = Constant.phpUrl + "/wap/panel/quest/exercises.php?cat1_id=" + cat1_id + "&cat2_id="
								+ cat2_id + "&paper_id=" + paper_id + "&tocken=" + mToken;
						break;
					default:
						break;
					}

					Log.d("345abc", "我的练习链接为" + url_update);

					break;
				case 6:// 我的错题
					String paper_id1 = intent.getStringExtra("paper_id");
					url_update = Constant.phpUrl + "/wap/panel/quest/analyse.php?error=1&error_paper_id=" + paper_id1
							+ "&tocken=" + mToken;
					Log.d("345abc", "我的错题链接为" + url_update);

					break;
				case 7:// 搜索
					url_update = Constant.phpUrl + "/wap/search.php";

					break;
				case 8:// 视频收藏
					String lecture_id = intent.getStringExtra("lecture_id");
					url_update = Constant.phpUrl + "/wap/panel/video/videos.php?id=" + lecture_id + "&tocken=" + mToken;
					break;
				case 9:// 题目收藏
					String quest_id = intent.getStringExtra("quest_id");
					url_update = Constant.phpUrl + "/wap/panel/my/quest_view.php?id=" + quest_id + "&tocken=" + mToken;
					break;
				case 10:// 播放记录
					String lecture_id1 = intent.getStringExtra("lecture_id");
					url_update = Constant.phpUrl + "/wap/panel/video/videos.php?id=" + lecture_id1 + "&tocken="
							+ mToken;
					break;
				case 11:
					url_update = intent.getStringExtra("image_url");

					break;
				default:
					break;
				}
				if (index == 0 || index == 1 || index == 2) {
					if (MyApplication.map_catID != null && !MyApplication.map_catID.toString().equals("{}")) {
						Log.d("345abc", "map_catID存在" + MyApplication.map_catID);
						Set<String> kset = MyApplication.map_catID.keySet();
						l: for (String key : kset) {
							if (name.equals(MyApplication.map_catID.get(key))) {
								// Log.d("123456", url_update);
								catID = key;
								url_update = url_base_video + "?tocken=" + mToken + "&id=" + catID;
								break l;
							}
						}
					} else {
						NetHelper.get(Constant.phpUrl + "/wap/api.php?action=GET_CAT",
								new SimpleMultiBeanNetHandler<Cat>(WebActivity.this) {

									@Override
									protected void onSuccess(List<Cat> list) {
										for (Cat s : list) {
											MyApplication.map_catID.put(s.exam_cat_id, s.exam_cat_name);
											Log.d("345abc", "id=" + s.exam_cat_id + "    name=" + s.exam_cat_name);

										}
										updateUrl(index);
									}

									@Override
									protected void onError(int errorCode, String errorMsg) {
										// TODO Auto-generated method stub
										Log.d("345abc", "caiid错误=" + errorMsg);
										Toast.makeText(WebActivity.this, R.string.net_error_warning, Toast.LENGTH_SHORT)
												.show();

										finish();
										// super.onError(errorCode, errorMsg);
									}

								});
					}
				}
				// catID = list.get(index).exam_cat_id;

				// Toast.makeText(WebActivity.this, mToken,
				// Toast.LENGTH_SHORT).show();

				// Toast.makeText(WebActivity.this,
				// url_update,
				// Toast.LENGTH_SHORT).show();
				Log.d("345abc", "网页地址为" + url_update);
				if (url_update != null) {
					videowebview.loadUrl(url_update);
					// videowebview.loadUrl("file:///android_asset/test.html");

					/*
					 * new Timer().schedule(new TimerTask() {
					 * 
					 * @Override public void run() { // 需要做的事:发送消息 //
					 * LoginUtil.login_handlered = false; Log.d("345abc",
					 * "播放记录循环等待"); Message msg_loop = handler.obtainMessage();
					 * msg_loop.what = 565; msg_loop.sendToTarget(); } },
					 * 10000);
					 */
				} else {
					Toast.makeText(WebActivity.this, R.string.net_error_warning, Toast.LENGTH_SHORT).show();

					finish();
				}
				// String url_abc="file:///android_asset/123.html";
				// videowebview.loadUrl(url_abc);

			} else {
				TokenManager.handlered = false;
				LoginUtil.login_handlered = false;
				Message msg_loop = handler.obtainMessage();
				msg_loop.what = 2;
				msg_loop.sendToTarget();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.d("123456", "返回");
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE && resultCode == LoginInputActivity.RESULT_CODE
				&& MyApplication.logined == true) {

			String token_new = sp.getString("mToken", null);
			if (token_new != null && token_new != "") {
				changeToken(token_new);
			}
		} else if (MyApplication.logined == false && index == 3) {
			Toast.makeText(WebActivity.this, "请登录后使用", Toast.LENGTH_SHORT).show();

			finish();
		}
	}

	private void loginBackground() {
		// editor.putBoolean("logined", false);
		editor.putBoolean("token_logined", false);
		editor.remove("mToken");
		editor.commit();
		tManager.tokenGet();
	}

	public static void changeToken(String token_new) {
		Log.d("345abc", "token_new=" + token_new);
		Log.d("345abc", "current_url=" + current_url);
		token_changed = true;
		if (index == 3) {
			// Log.d("345abc", "网页地址为" + url_base_tiku + "?token=" + token_new);
			// Toast.makeText(web_instance, "后台自动登录" + url_base_tiku + "?token="
			// + token_new, Toast.LENGTH_SHORT).show();
			if (current_url == null) {
				videowebview.loadUrl(url_base_tiku + "?tocken=" + token_new);
				// videowebview.clearHistory();
			} else {
				Log.d("345abc", "current_url=   " + current_url);
				StringBuilder sb = new StringBuilder(current_url);
				int index_start = current_url.lastIndexOf("tocken=");
				Log.i("webviewtest", "索引为: " + index_start);
				sb.replace(index_start + 7, index_start + 47, token_new);
				String url_new = sb.toString();
				Log.i("webviewtest", "新地址为: " + url_new);
				videowebview.loadUrl(url_new);
				// videowebview.clearHistory();
			}

		} else if (current_url != null) {
			Log.d("345abc", "current_url=   " + current_url);
			StringBuilder sb = new StringBuilder(current_url);
			int index_start = current_url.lastIndexOf("tocken=");
			Log.i("webviewtest", "索引为: " + index_start);
			sb.replace(index_start + 7, index_start + 47, token_new);
			String url_new = sb.toString();
			Log.i("webviewtest", "新地址为: " + url_new);
			videowebview.loadUrl(url_new);
			// videowebview.clearHistory();
		}
	}

	public class FinishWeb {
		Context mContext;
		Handler handler;

		FinishWeb(Context cxt, Handler handler) {

			// TODO Auto-generated constructor stub
			mContext = cxt;
			this.handler = handler;
		}

		@JavascriptInterface
		public void finish() {
			Log.d("345abc", "js调用                     123");
			WebActivity.this.finish();
		}

		@JavascriptInterface
		public void cancelCollection() {
			Toast.makeText(WebActivity.this, "取消收藏JS", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		Log.d("123456", "WebActivity   finish");
		if (timer != null) {
			try {
				timer.cancel();
				timer.purge();
				timeing = false;
			} catch (Exception e) {
				// TODO: handle exception
			}

		}

		// Toast.makeText(WebActivity.this, text, duration)

		try {

			if (dialog != null) {
				dialog.dismiss();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		// dialog = null;
		try {
			handler.removeCallbacksAndMessages(null);
			// handler = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.finish();
	}

}
