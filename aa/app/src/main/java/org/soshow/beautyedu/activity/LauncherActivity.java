package org.soshow.beautyedu.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.bean.LunBoInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.bean.UpDateInfo;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.pushnonotification.ServiceManager;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
/**
 * 启动页
 */
public class LauncherActivity extends FragmentActivity {
	// 软件升级部分变量
	private final String PACKAGE_NAME = "org.soshow.beautyedu";
	@Deprecated
	private final String VERSION_KEY = "version_code";
	private static final String DATE_PREVIOUS = "date_previous";
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;
	private PackageInfo info;

	// private String upUrl = "http://192.168.10.12/out.txt";
	private MyApplication app;
	private int lastversion;

	// 轮播图部分变量
	// private List<LunBo> lunbos;
	private String lunboUrl;
	private String mToken;
	// private WebView myWebView;
	public static LauncherActivity la_ins;
	private boolean isfirst = false;
	private boolean isfirst_finished = false;
	// private ImageView launcher_view;
	private RelativeLayout launcher_first;

	private ViewPager viewPage;
//	private Fragment1 mFragment1;
//	private Fragment2 mFragment2;
//	private Fragment3 mFragment3;
	private PagerAdapter mPgAdapter;
	private RadioGroup dotLayout;
//	private List<Fragment> mListFragment = new ArrayList<Fragment>();
	private String date;
	
	private List<LunBoInfo> lunBos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("abccba", "建立");
		if (!this.isTaskRoot()) { // 判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
			// 如果你就放在launcher Activity中话，这里可以直接return了
			Intent mainIntent = getIntent();
			String action = mainIntent.getAction();
			if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
				finish();
				return;
				// finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception } }
			}
		}
		setContentView(R.layout.activity_launcher);
		la_ins = this;
		// launcher_view = (ImageView) findViewById(R.id.launcher_view);
		preferences = getSharedPreferences("huizhongjia", MODE_PRIVATE);
		editor = preferences.edit();
		app = (MyApplication) getApplication();
		MyApplication.logined = preferences.getBoolean("logined", false);
		LogUtils.e("欢迎界面      登入标志MyApplication.logined======"+MyApplication.logined);
		ServiceManager serviceManager = new ServiceManager(this);
		serviceManager.setNotificationIcon(R.drawable.notification);
		serviceManager.startService();
		
		mToken = new TokenManager(LauncherActivity.this).getToken();
		if (mToken != null) {
			init();
//			getLunBo();
		} else {
			init();
//			getLunBo();
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 99;
			msg_loop.sendToTarget();
		}

	}

	/**启动完成*/
	public static final int Msg_firstlogin_8 = 8;
	public static final int Msg_startmain_1 = 1;
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Msg_startmain_1:
				Intent intent = new Intent(LauncherActivity.this, MainTabActivity.class);
				startActivity(intent);
				finish();
				break;
			case 2:
				getUpDateInfo();
				break;
			case 99://token为null时
				if (TokenManager.handlered) {
					Log.d("345abc", "启动界面获取新token");
					TokenManager.handlered = false;
					mToken = preferences.getString("mToken", null);
					if (mToken != null) {
						initSecond();
					} else {
						initSecond();

					}

				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							Log.d("345abc", "启动界面循环等待");
							if (handler != null) {
								try {
									Message msg_loop = handler.obtainMessage();
									msg_loop.what = 99;
									msg_loop.sendToTarget();
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						}
					}, 1000);

				}
				break;
			case 3:
				// myWebView.setVisibility(View.GONE);
				initSecond();
				break;
			case 5:
				// myWebView.setVisibility(View.VISIBLE);
				Log.d("abccba", "显示网页");
				break;
			case Msg_firstlogin_8://首次登入
				editor.putString(DATE_PREVIOUS, date);
				editor.putInt(VERSION_KEY, MyApplication.currentVersion);
				editor.commit();
				Intent intent2 = new Intent(LauncherActivity.this, MainTabActivity.class);
				startActivity(intent2);
				finish();
				// launcher_first.setVisibility(View.GONE);
				isfirst_finished = true;

				break;
			default:
				break;
			}

		};
	};

	public Handler getHandler() {
		return handler;
	}

	private void init() {
		lunBos = new ArrayList<LunBoInfo>();
		try {
			info = getPackageManager().getPackageInfo(PACKAGE_NAME, 0);
			MyApplication.currentVersion = info.versionCode;
			lastversion = preferences.getInt(VERSION_KEY, 0);
			LogUtils.e("欢迎页面===========lastversion===="+lastversion+"\nMyApplication.logined="+MyApplication.logined);
//			if (lastversion <= 8) {
//				// Constant.code_changed = true;
//				editor.putBoolean("logined", false);
//				MyApplication.logined = false;
//				editor.remove("user_name");
//				editor.remove("username");
//				editor.remove("user_password");
//				editor.remove("allow_net_local");
//				editor.remove("net_flag");
//				editor.putBoolean("token_logined", false);
//				editor.remove("login_type");
//				editor.remove("email");
//				editor.remove("cellphone");
//				editor.remove("headUrl");
//				editor.remove("signature");
//				editor.remove("user_id");
//				editor.commit();
//				LoginUtil.removeCookie(LauncherActivity.this);
//				LoginUtil.removeData(LauncherActivity.this);
//			} else {
//				// Constant.code_changed = false;
//			}

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// Toast.makeText(LauncherActivity.this, "错误:"+e.toString(),
			// Toast.LENGTH_SHORT).show();
			//

		} finally {
			// 首次启动

			if (MyApplication.currentVersion > lastversion) {
				// 如果当前版本大于上次版本，该版本属于第一次启动
				Log.d("abccba", "执行到此");

				// launcher_view.setVisibility(View.VISIBLE);
				// Toast.makeText(LauncherActivity.this, "第一次启动",
				// Toast.LENGTH_SHORT).show();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				date = sdf.format(new java.util.Date());
				// 将当前版本,登录日期写入preference中，则下次启动的时候，据此判断，不再为首次启动

				// 第一次启动，且无快捷方式，则安装快捷方式
				if (!hasShortcut(LauncherActivity.this, getResources().getString(R.string.app_name))) {
					Intent addShortCut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
					addShortCut.putExtra("duplicate", false);
					// 加载app名
					String title = getResources().getString(R.string.app_name);
					// 加载app的logo
					Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(),
							R.drawable.logo_new);
					// 点击快捷方式后操作Intent,快捷方式建立后，再次启动该程序
					ComponentName comp = new ComponentName(this.getPackageName(),
							this.getPackageName() + "." + this.getLocalClassName());
					Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(comp);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					// 设置快捷方式的标题
					addShortCut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
					// 设置快捷方式的图标
					addShortCut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
					// 设置快捷方式对应的Intent
					addShortCut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
					// 发送广播添加快捷方式
					sendBroadcast(addShortCut);

				}
				launcher_first = (RelativeLayout) findViewById(R.id.launcher_first);
				launcher_first.setVisibility(View.VISIBLE);
				dotLayout = (RadioGroup) findViewById(R.id.advertise_point_group);
				viewPage = (ViewPager) findViewById(R.id.viewpager);
//				mFragment1 = new Fragment1();
//				mFragment2 = new Fragment2();
//				mFragment3 = new Fragment3();
//
//				mListFragment.add(mFragment1);
//				mListFragment.add(mFragment2);
//				mListFragment.add(mFragment3);
//
//				
//				
//				mPgAdapter = new org.soshow.beautyedu.adapters.ViewPagerAdapter(getSupportFragmentManager(),
//						mListFragment);
//				viewPage.setAdapter(mPgAdapter);
//				viewPage.setOnPageChangeListener(new MyPagerChangeListener());
				
				if(LauncherActivity.la_ins!=null){
					Message message=LauncherActivity.la_ins.getHandler().obtainMessage();
					message.what= Msg_firstlogin_8;
					message.sendToTarget();
				}
				
				if (mToken != null) {
					initSecond();
				}
			}
			// 非首次启动
			else {
				if (isConnect(LauncherActivity.this)) {
					// Toast.makeText(LauncherActivity.this, "有网络",
					// Toast.LENGTH_SHORT).show();
					if (isFirstToday()) {
						// Toast.makeText(LauncherActivity.this, "今日首次启动",
						// Toast.LENGTH_SHORT).show();
						/// if (mToken != null) {
						/// upGradeCheck();
						/// }

					}
				} else {
					Toast.makeText(LauncherActivity.this, "网络不给力，请检查下网络", Toast.LENGTH_SHORT).show();
				}
				// 非当日首次启动
				// Intent intent = new Intent(LauncherActivity.this,
				// MainActivity.class);
				// startActivity(intent);
				// finish();
				if (mToken != null) {
					initSecond();
				}
			}

		}

		// Intent intent = new Intent(LauncherActivity.this,
		// MainTabActivity.class);
		// startActivity(intent);
		// finish();
		// Log.d("time", StringUtil.getDataTime());

	}

	public class MyPagerChangeListener implements OnPageChangeListener {

		public void onPageSelected(int position) {

		}

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			((RadioButton) dotLayout.getChildAt(position)).setChecked(true);
		}

	}

	private void initSecond() {
//		if (mToken != null) {
//			upGradeCheck();
//		}
//		CatIdUtil.getCatID(LauncherActivity.this);
		if (!isfirst) {
			new Timer().schedule(new TimerTask() {

				@Override
				public void run() {
					if (handler != null) {
						try {
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = Msg_startmain_1;
							msg_loop.sendToTarget();
						} catch (Exception e) {
						}
					}

				}

			}, 1000);
		} else {

		}
	}

	private void getLunBo() {
		if (mToken != null) {
			String app_nonce = StringUtil.getPhoneIMEI(LauncherActivity.this);
			lunboUrl = Constant.GET_HOME_PAGE;
			Log.e("123321", "轮播图地址url================================" + lunboUrl);
			NetHelper.get(lunboUrl, new SimpleSingleBeanNetHandler<Captcha>(this) {
				@Override
				protected void onSuccess(Captcha bean) {
					try {
						if(bean.result.equals("0")){
							String info = bean.info;
							JSONObject jsonObject = new JSONObject(bean.info);
							JSONArray lunboArray = jsonObject.getJSONArray("carouselData");
							JSONArray newsArray = jsonObject.getJSONArray("news");
							JSONArray recommendArray = jsonObject.getJSONArray("recommend");
							
							//轮播
							for (int i = 0; i < lunboArray.length(); i++) {
								JSONObject objLunbo = (JSONObject) lunboArray.get(i);
								LunBoInfo lunBo = GsonUtils.parseJSON(objLunbo.toString(), LunBoInfo.class);
								lunBos.add(lunBo);
							}
							app.setLunBoInfo(lunBos);
							try {
								FileOutputStream outStream = new FileOutputStream(getCacheDir().toString() + "/lunBos.txt");
								ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
								objectOutputStream.writeObject(lunBos);
								outStream.close();
							} catch (Exception e) {

							}
							if (FragmentMain.fm_instance != null) {
								FragmentMain.fm_instance.initViews();
							}
							
						}else{
							ToastUtil.getInstance().showToast(LauncherActivity.this, bean.message);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {

					Log.d("123321", "获取轮播图失败" + errorMsg);
					try {
						JSONObject jo = new JSONObject(errorMsg);
						if (jo.optString("result").equals("100")) {
							app.setLunBoEmpty();
							File file = new File(getCacheDir().toString() + "/lunBos.txt");
							if (file.exists()) {
								file.delete();
							}
							if (FragmentMain.fm_instance != null) {
								FragmentMain.fm_instance.initViews();
							}
						} else if (jo.optString("result").equals("99")) {
							editor.putBoolean("token_logined", false);
							editor.remove("mToken");
							editor.commit();
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

				}

			});
		}
	}

	// 检查更新，如果有更新则使用Handler处理
	public void upGradeCheck() {

		// TODO Auto-generated method stub
		getUpDateInfo();
		// getUpDateInfo(map);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.launcher, menu);

		return true;
	}

	// 检查是否当日首次启动
	@SuppressLint("SimpleDateFormat")
	public boolean isFirstToday() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new java.util.Date());
		// Toast.makeText(LauncherActivity.this, "今日首次启动",
		// Toast.LENGTH_SHORT).show();
		String date_previous = preferences.getString(DATE_PREVIOUS, null);

		if (!date.equals(date_previous)) {
			editor.putString(DATE_PREVIOUS, date);
			editor.commit();
			return true;
		}

		else
			return true;

	}

	/*
	 * 获取通信JSON
	 */
	private void getUpDateInfo() {
		String app_nonce = StringUtil.getPhoneIMEI(LauncherActivity.this);

		if (mToken != null) {
			String upUrl = Constant.phpUrl + "/wap/api.php?action=GET_ANDROID_VERSION&app_nonce=" + app_nonce
					+ "&tocken=" + mToken
					+ "&version_code=" + MyApplication.currentVersion;
			Log.e("345abc", "欢迎页面   升级信息地址为                    " + upUrl);
			NetHelper.get(upUrl, new SimpleSingleBeanNetHandler<UpDateInfo>(this) {
				@Override
				protected void onSuccess(UpDateInfo bean) {
					// 这里是ui线程，可进行ui操作
					Log.d("345abc", "升级信息为" + bean.result);
					try {
						if(bean.isSuccess()) {
							app.setUpdateinfo(bean);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					Log.d("345abc", "获取更新信息失败");
				}

			});
		}
	}

	/*
	 * private void getUpDateInfo(Map map) { NetHelper.post(url, map, new
	 * SimpleSingleBeanNetHandler<UpDateInfo>(this) {
	 * 
	 * @Override protected void onSuccess(UpDateInfo bean) { // 这里是ui线程，可进行ui操作
	 * app = (MyApplication) getApplication(); app.setUpdateinfo(bean);
	 * updateinfo = bean; }
	 * 
	 * }); }
	 */
	public boolean isConnect(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}

			}

		}
		return false;
	}

	public int getVerCode(Context _context, String _package) {
		int verCode = -1;
		try {
			verCode = _context.getPackageManager().getPackageInfo(_package, 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return verCode;
	}

	public boolean hasShortcut(Activity activity, String shortcutName) {
		try {
			String url = "";
			url = "content://" + getAuthorityFromPermission(activity, "com.android.launcher.permission.READ_SETTINGS")
					+ "/favorites?notify=true";
			ContentResolver resolver = activity.getContentResolver();
			Cursor cursor = resolver.query(Uri.parse(url), new String[] { "title", "iconResource" }, "title=?",
					new String[] { getString(R.string.app_name).trim() }, null);
			if (cursor != null && cursor.moveToFirst()) {
				cursor.close();
				return true;
			}
			return false;
		} catch (Exception e) {
			// TODO: handle exception
			return true;
		}

	}

	private String getAuthorityFromPermission(Context context, String permission) {
		if (permission == null)
			return null;
		List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
		if (packs != null) {
			for (PackageInfo pack : packs) {
				ProviderInfo[] providers = pack.providers;
				if (providers != null) {
					for (ProviderInfo provider : providers) {
						if (permission.equals(provider.readPermission))
							return provider.authority;
						if (permission.equals(provider.writePermission))
							return provider.authority;
					}
				}
			}
		}
		return null;
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		la_ins = null;
		super.finish();
		try {
			handler.removeCallbacksAndMessages(null);
			handler = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
