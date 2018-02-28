package org.soshow.beautyedu.activity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.bean.LectureList;
import org.soshow.beautyedu.json.bean.Quest;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleMultiBeanNetHandler;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.NetUtil;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.MyAlertDialog;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.widget.FragmentAdapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.qcload.playersdk.ui.UiChangeInterface;
import com.tencent.qcload.playersdk.ui.VideoRootFrame;
import com.tencent.qcload.playersdk.util.PlayerListener;
import com.tencent.qcload.playersdk.util.VideoInfo;
/**
 *视频播放
 */
public class PlayVideoActivity extends FragmentActivity
		implements OnClickListener, OnPageChangeListener, UiChangeInterface {
	private String TAG = "PlayVideoActivity";
	// ����textview
	private TextView tab1Tv, tab2Tv, tab3Tv;
	// ָʾ��
	private ImageView cursorImg;
	// viewpager
	private ViewPager viewPager;
	// fragment���󼯺�
	private ArrayList<Fragment> fragmentsList;
	// ��¼��ǰѡ�е�tab��index
	// private int currentIndex = 0;
	// ָʾ����ƫ����
	private int offset = 0;
	// ��margin
	// private int leftMargin = 0;
	// ��Ļ���

	private int screenWidth = 0, screenHeight;
	public static int video_width, video_height;
	private long port_height, land_height, land_width;
	// ��Ļ��ȵ�����֮һ
	private int screen1_3;
	//
	private LinearLayout.LayoutParams lp;
	// private final String TAG = "MainActivity";
	private TextView title_name;
	// public static int position_video;
	public static String lecture_id;
	private TextView title_tv;
	private VideoRootFrame player;
	private RelativeLayout title_play_video;
	private LinearLayout play_content;
	private android.view.ViewGroup.LayoutParams params;
	public static List<LectureList> list_lecture_play;
	public static List<Quest> quests;
	private String url_lecture_list;
	private String url_lecture_questions;
	private String url_lecture_add;
	private int page_no = 1;
	// private List<Map<String, Object>> listItems;
	private String mToken;
	private SharedPreferences sp;
	private Editor editor;
	// private SimpleAdapter sa;
	private String app_nonce;
	public static boolean load_finished = false;
	public static boolean load_quest = false;
	public static String[] lecture_questions_id;
	public static String lecture_questions_id_o;
	public static PlayVideoActivity pva_instance;
	// private boolean full_screen = false;
	private ImageButton fullscreen;
	// private boolean playing = false;
	// private SeekBar mediacontroller_progress;
	private OrientationEventListener mOrientationListener; // 屏幕方向改变监听器
	private boolean mIsLand = false; // 是否是横屏
	private boolean mClick = false; // 是否点击
	private boolean mClickLand = true; // 点击进入横屏
	private boolean mClickPort = true; // 点击进入竖屏
	// private PowerManager pManager;
	// private WakeLock wakeLock;
	private ImageView play_pause_view, lecture_sc;
	private ImageButton pause;
	// private boolean click_change = false;
	private boolean auto_change = false;
	private boolean remark_loaded = false;// 收藏状态获取完成
	private boolean remark_loading = true;// 正在获取收藏状态
	private boolean play_status = false;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(final Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				/*
				 * try { if (page_no == 1) { listItems = new
				 * ArrayList<Map<String, Object>>(); } for (LectureList dr :
				 * list_lecture_play) { // for (int i =
				 * (list_displayRecord.size() - 1); i >= 0; // i--) { //
				 * DisplayRecord dr = list_displayRecord.get(i);
				 * 
				 * Map<String, Object> listItem = new HashMap<String, Object>();
				 * listItem.put("lecture_title", dr.lecture_title);
				 * listItem.put("lecture_click_count",
				 * Integer.parseInt(dr.lecture_click_count) + 30 + "人观看"); //
				 * listItem.put("lecture_time", // new SimpleDateFormat(
				 * "yyyy-MM-dd // HH:mm:ss").format(new // Date(dr.add_time *
				 * 1000L))); // listItem.put("remark", dr.remark);
				 * listItem.put("image_url", dr.head_image);
				 * listItem.put("lecture_id", dr.lecture_id); // Log.d("345abc",
				 * "error_quest_count=" + // dr.lecture_add_time);
				 * listItems.add(listItem);
				 * 
				 * } if (page_no == 1) { // sa = new
				 * LectureListAdapter(PlayVideoActivity.this, // listItems); //
				 * list_record.setAdapter(dra);
				 * 
				 * } else { // dra.notifyDataSetChanged();
				 * 
				 * }
				 * 
				 * } catch (Exception e) { // TODO: handle exception }
				 */
				break;
			case 2:

				try {
					removeTimerTask(timer_token_task);
					final int index = Integer.parseInt(msg.obj.toString());
					if (LoginUtil.login_handlered) {
						Log.d("345abc", "处理完毕，获取新token");
						LoginUtil.login_handlered = false;
						mToken = sp.getString("mToken", null);
						if (mToken != null) {
							getInfo(index);
						} else {
							switch (index) {
							case 0:// 打开播放界面
								load_finished = true;
								load_quest = true;
								// change_remarking = false;
								remark_loading = false;
								remark_loaded = true;
								if (FragmentXiti.fmt != null) {
									FragmentXiti.mHasLoadedOnce = false;
									FragmentXiti.fmt.refresh();
								}

								if (FragmentDirectory.fmt != null) {
									FragmentDirectory.mHasLoadedOnce = false;
									FragmentDirectory.fmt.refresh();
								}
								break;
							case 1:// 更换视频
								load_quest = true;
								// change_remarking = false;
								remark_loading = false;
								remark_loaded = true;
								if (FragmentXiti.fmt != null) {
									FragmentXiti.mHasLoadedOnce = false;
									FragmentXiti.fmt.refresh();
								}

								if (FragmentDirectory.fmt != null) {
									FragmentDirectory.mHasLoadedOnce = false;
									FragmentDirectory.fmt.refresh();
								}
								break;
							case 2://
								change_remarking = false;
								break;
							case 3:
								hashing = false;
								break;
							default:
								break;
							}

							// Toast.makeText(PlayVideoActivity.this,
							// "网络不佳，请稍后再试",
							// Toast.LENGTH_SHORT).show();
						}
					} else {
						timer_token_task = new TimerTask() {
							@Override
							public void run() {
								// 需要做的事:发送消息
								// LoginUtil.login_handlered = false;
								Log.d("345abc", "视频播放Token循环等待");
								Message msg_loop = handler.obtainMessage();
								msg_loop.what = 2;
								msg_loop.obj = index;
								msg_loop.sendToTarget();
							}
						};
						timer_token.schedule(timer_token_task, 1000);

					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				break;
			case 3:
				// Toast.makeText(PlayVideoActivity.this, "到",
				// Toast.LENGTH_SHORT).show();
				removeTimerTask(time_list_task);
				load_quest = true;
				if (FragmentXiti.fmt != null) {
					FragmentXiti.mHasLoadedOnce = false;
					FragmentXiti.fmt.refresh();
				}
				break;
			case 4:
				try {
					removeTimerTask(timer_loading_task);
					if (load_finished && load_quest && remark_loaded) {
						// dialog.hide();
						// load_quest = false;
						remark_loaded = false;
					} else {
						timer_loading_task = new TimerTask() {
							@Override
							public void run() {
								// 需要做的事:发送消息
								// LoginUtil.login_handlered = false;
								Log.d("345abc", "视频播放加载数据循环等待");
								Message msg_loop = handler.obtainMessage();
								msg_loop.what = 4;
								msg_loop.sendToTarget();
							}
						};
						timer_loading.schedule(timer_loading_task, 1000);
					}
				} catch (Exception w)

				{

				}

				break;
			case 5:// 播放途中网络变化

				if (bd != null && !bd.isShowing() && player != null) {
					try {
						player.pause();
						bd.show();
					} catch (Exception e) {

					}

				}

				break;
			case 6:// 上传视频播放进度
				removeTimerTask(timer_progress_task);
				// if (timer_progress == null) {
				// timer_progress = new Timer();

				// }
				/*
				 * if (hashId == null) { if (timer_progress != null) { try {
				 * timer_progress.cancel(); timer_progress.purge();
				 * timer_progress = null;
				 * 
				 * } catch (Exception e) { // TODO: handle exception } } return;
				 * }
				 */
				if (hashId != null) {
					getTokenLocal(4);
					timer_progress_task = new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
							Log.d("345abc", "循环上传进度");
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 6;
							msg_loop.sendToTarget();
						}
					};
					try {
						timer_progress.schedule(timer_progress_task, 5000);
					} catch (Exception e) {

					}

				}

				break;
			case 7:
				Log.d("234abc", "开始计时");
				removeTimerTask(timer_loading_task);
				if (!sp.getBoolean("logined", false)) {
					if (player.getCurrentTime() >= 50) {
						removeTimer(timer_loading);
						player.pause();
						Toast.makeText(PlayVideoActivity.this, "请登录后继续观看视频", Toast.LENGTH_SHORT).show();
						Intent intent = new Intent(PlayVideoActivity.this, LoginInputActivity.class);
						intent.putExtra("key", 14);
						startActivity(intent);
					} else {
						timer_login_task = new TimerTask() {
							@Override
							public void run() {
								// 需要做的事:发送消息
								// LoginUtil.login_handlered = false;
								Log.d("345abc", "循环上传进度");
								Message msg_loop = handler.obtainMessage();
								msg_loop.what = 7;
								msg_loop.sendToTarget();
							}
						};
						timer_login.schedule(timer_login_task, 1000);
					}
				}
				break;
			case 8:
				Log.d("123321", "登陆回调");
				player.play();
				getTokenLocal(5);
				break;
			case 66:
				try {
					Log.d("abccba", "锁消息处理");
					// if (wakeLock.isHeld()) {
					Log.d("abccba", "播放完毕释放锁");
					// wakeLock.release();
					// }
				} catch (Exception q) {

				}

				break;
			case 77:
				try {
					mOrientationListener.enable();
				} catch (Exception o) {

				}

				break;
			case 88:
				try {
					removeTimerTask(timer_88_task);
					checkQuitFullscreen();
				} catch (Exception d) {

				}

				break;
			case 99:
				// Log.d("123321",
				// "延时后播放器全屏" + player.isFullScreen() + "进入全屏" + "播放器宽为" +
				// params.width + "播放器高为" + params.height);
				try {
					removeTimerTask(timer_99_task);
					checkFullscreen();
				} catch (Exception c) {

				}
				break;
			default:
				break;

			}
		}

	};

	public Handler getHandler() {
		return handler;
	}

	public static String remark = "2";
	public static String url_s, url_h, url_p,url_real, lecture_title, lecture_content;
	public static int position_video = 65535;
	
	private boolean firstloading = true;
	// private Dialog dialog;
	private Dialog bd;
	private Timer timer_88 = new Timer();
	private TimerTask timer_88_task;
	private Timer timer_99 = new Timer();
	private TimerTask timer_99_task;
	private Timer timer_token = new Timer();
	private TimerTask timer_token_task;
	private Timer timer_loading = new Timer();
	private TimerTask timer_loading_task;
	private Timer timer_list = new Timer();
	private TimerTask time_list_task;
	public static Timer timer_progress;
	private TimerTask timer_progress_task;
	public static Timer timer_login;
	private TimerTask timer_login_task;
	private boolean change_remarking = false;// 正在改变收藏状态
	// private SeekBar mediacontroller_progress;

	// 播放统计
	public static String hashId;
	private boolean hashing = false;
	// private String url_progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_play_video);
		pva_instance = this;
		timer_progress = new Timer();
		timer_login = new Timer();
		
		player = (VideoRootFrame) findViewById(R.id.player);
		play_pause_view = (ImageView) findViewById(R.id.play_pause_view);
		title_name = (TextView) findViewById(R.id.title_name);
		lecture_sc = (ImageView) findViewById(R.id.lecture_sc);

		title_name.setText(MyApplication.map_catID.get(getIntent().getStringExtra("lecture_cat1")));
		title_tv = (TextView) findViewById(R.id.title_tv);
		title_play_video = (RelativeLayout) findViewById(R.id.title_play_video);
		play_content = (LinearLayout) findViewById(R.id.play_content);
		// getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		initIntent();
		// setRemark();
		// Toast.makeText(PlayVideoActivity.this, "Activity创建",
		// Toast.LENGTH_SHORT).show();
		//
		// bd.setCancelable(false);

		sp = PlayVideoActivity.this.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		list_lecture_play = new ArrayList<LectureList>();
		quests = new ArrayList<Quest>();
		// pManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		// wakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
		// | PowerManager.FULL_WAKE_LOCK
		// | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,
		// "TAG");
		// Secure.putInt(getContentResolver(), Secure.LOCK_PATTERN_ENABLED, 0);
		// wakeLock.acquire();
		// wakeLock.isHeld()
		startListener();
		initView();
		getTokenLocal(0);
		bd = new MyAlertDialog.Builder(PlayVideoActivity.this).setTitle(getResources().getString(R.string.net_tip))
				.setMessage(getResources().getString(R.string.video_net_warn))
				.setPositiveButton(getResources().getString(R.string.net_tip_ok),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								// arg0.dismiss();
								// allow_net.setChecked(true);
								try {
									player.play();
									editor.putInt("allow_net_local", 1);
									editor.commit();
									Constant.allow_net_other = true;
								} catch (Exception e) {
									// TODO: handle exception
								}

							}
						})
				.setNegativeButton(getResources().getString(R.string.net_tip_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								// dialog.dismiss();
								finish();

							}
						})
				.setCancelable(false).create();
		// int divierId =
		// bd.getContext().getResources().getIdentifier("android:id/alertTitle",
		// null, null);
		// TextView divider = (TextView) bd.findViewById(divierId);
		// divider.setTextColor(getResources().getColor(R.color.black));
		if (!sp.getBoolean("logined", false)) {
			try {
				Message message2 = handler.obtainMessage();
				message2.what = 7;
				message2.sendToTarget();
			} catch (Exception e) {
				// TODO: handle exception
			}

		}
	}

	private void initIntent() {
		Intent intent = getIntent();
		// position_video = intent.getIntExtra("position", 10086);
		lecture_id = intent.getStringExtra("lecture_id");
		Log.d("123456", "获取id为" + lecture_id);
		// remark = intent.getStringExtra("remark");
		lecture_title = intent.getStringExtra("lecture_title");
		url_h = intent.getStringExtra("url_h");
		url_s = intent.getStringExtra("url_s");
		url_p = intent.getStringExtra("url_p");
		try {
			url_real = intent.getStringExtra("video_local_name");
		} catch (Exception e) {
		}
		Log.e(TAG, "视频播放地址=============================================="+url_real);
		try {
			lecture_questions_id_o = intent.getStringExtra("lecture_questions_id");
			Log.d("1221", "题目为" + lecture_questions_id_o);
			lecture_questions_id = intent.getStringExtra("lecture_questions_id").split(",");
		} catch (Exception e) {
			// TODO: handle exception
		}

		lecture_content = intent.getStringExtra("lecture_content");
	}

	private void initView() {

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if (dm.widthPixels < dm.heightPixels) {
			screenWidth = dm.widthPixels;
			screenHeight = dm.heightPixels;
		} else {
			screenWidth = dm.heightPixels;
			screenHeight = dm.widthPixels;
		}

		screen1_3 = screenWidth / 3;

		cursorImg = (ImageView) findViewById(R.id.cursor);
		lp = (LayoutParams) cursorImg.getLayoutParams();
		// Toast.makeText(PlayVideoActivity.this, "lp=="+lp.toString(),
		// Toast.LENGTH_SHORT).show();
		// leftMargin = lp.leftMargin;

		tab1Tv = (TextView) findViewById(R.id.tab1_tv);
		tab2Tv = (TextView) findViewById(R.id.tab2_tv);
		tab3Tv = (TextView) findViewById(R.id.tab3_tv);
		tab1Tv.setOnClickListener(this);
		tab2Tv.setOnClickListener(this);
		tab3Tv.setOnClickListener(this);
		initViewPager();

		setVideoTitle();

		video_height = Integer.parseInt(getIntent().getStringExtra("vheight_s"));
		video_width = Integer.parseInt(getIntent().getStringExtra("vwidth_s"));
		port_height = screenWidth * video_height / video_width;
		if (screenHeight * video_height / video_width <= screenWidth) {
			Log.d("123321", "视频合适");
			land_height = screenWidth;
			land_width = screenHeight;
		} else {
			Log.d("123321", "视频过高");
			land_height = screenWidth;
			// land_width = screenWidth * video_width / video_height;
			land_width = screenHeight;
		}

		// port_height = screenWidth * 9 / 16;
		// land_height = screenHeight * 9 / 16;

		params = (android.view.ViewGroup.LayoutParams) player.getLayoutParams();
		params.height = (int) port_height;
		params.width = screenWidth;
		Log.d("123321", "播放器宽为" + screenWidth + "高为" + port_height + "屏幕高为" + screenHeight);
		player.setLayoutParams(params);
		// player.is
		setVideoInfo();

	}

	public void setPlayerSize() {
		// video_height =
		// Integer.parseInt(getIntent().getStringExtra("vheight_s"));
		// video_width =
		// Integer.parseInt(getIntent().getStringExtra("vwidth_s"));
		port_height = screenWidth * video_height / video_width;
		if (screenHeight * video_height / video_width <= screenWidth) {
			Log.d("123321", "视频合适");
			land_height = screenWidth;
			land_width = screenHeight;
		} else {
			Log.d("123321", "视频过高");
			land_height = screenWidth;
			// land_width = screenWidth * video_width / video_height;
			land_width = screenHeight;

		}

		// port_height = screenWidth * 9 / 16;
		// land_height = screenHeight * 9 / 16;

		params = (android.view.ViewGroup.LayoutParams) player.getLayoutParams();
		params.height = (int) port_height;
		params.width = screenWidth;
		Log.d("123321", "播放器宽为" + screenWidth + "高为" + port_height + "屏幕高为" + screenHeight);
		player.setLayoutParams(params);
	}

	public void setRemark() {

		// if (remark.equals("1")) {
		// lecture_sc.setImageResource(R.drawable.souc1);
		// } else {
		// lecture_sc.setImageResource(R.drawable.souc);
		// }
		if (sp.getBoolean("logined", false)) {
			lecture_sc.setVisibility(View.VISIBLE);
		}
		remark_loading = true;
		String url_shoucang_state = Constant.phpUrl + "/wap/api.php?action=GET_CHANGE_REMARK&app_nonce=" + app_nonce
				+ "&tocken=" + mToken + "&lecture_id=" + lecture_id + "&remark=" + 3;
		Log.d("abccba", "获取收藏状态" + url_shoucang_state);
		NetHelper.get(url_shoucang_state, new SimpleSingleBeanNetHandler<Captcha>(PlayVideoActivity.this) {

			@Override
			protected void onSuccess(Captcha bean) {
				// TODO Auto-generated method stub
				remark_loaded = true;
				remark_loading = false;
				try {
					int result = Integer.parseInt(bean.result);
					switch (result) {
					case 0:
						switch (Integer.parseInt(bean.info)) {
						case 1:
							lecture_sc.setImageResource(R.drawable.souc1);
							remark = "1";
							break;
						case 99:
							editor.putBoolean("token_logined", false);
							editor.remove("mToken");
							editor.commit();
							break;
						case 100:
							if (sp.getBoolean("logined", false)) {
								Toast.makeText(PlayVideoActivity.this, "网络错误，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
								editor.putBoolean("token_logined", false);
								editor.putBoolean("logined", false);
								MyApplication.logined = false;
								new LoginUtil(PlayVideoActivity.this, 20);
							}
							break;
						default:
							lecture_sc.setImageResource(R.drawable.souc);
							remark = "2";
							break;
						}
						break;

					default:
						break;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

			@Override
			protected void onError(int errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				// super.onError(errorCode, errorMsg);
				remark_loaded = true;
				remark_loading = false;
			}

		});

	}

	public void setVideoTitle() {

		title_tv.setText(lecture_title);

	}

	public void getHash() {
		String url_hash = Constant.phpUrl + "/wap/api.php?action=PUT_PLAY_RECORD&app_nonce=" + app_nonce + "&tocken="
				+ mToken + "&lecture_id=" + lecture_id;
		Log.d("1221", "hash获取-------------" + url_hash);
		NetHelper.get(url_hash, new SimpleSingleBeanNetHandler<Captcha>(PlayVideoActivity.this) {
			@Override
			protected void onSuccess(Captcha bean) {
				// TODO Auto-generated method stub
				hashing = false;
				try {
					int result_hash = Integer.parseInt(bean.result);
					switch (result_hash) {
					case 0:
						hashId = bean.info;
						Message message = handler.obtainMessage();
						message.what = 6;
						message.sendToTarget();
						Log.d("234abc", "获取hash成功");
						Log.d("1221", "hash为" + hashId);
						break;
					case 99:
						editor.putBoolean("token_logined", false);
						editor.remove("mToken");
						editor.commit();
						break;
					default:
						break;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

			@Override
			protected void onError(int errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				// super.onError(errorCode, errorMsg);
				Log.d("1221", "hash失败");
				hashing = false;
				Log.d("234abc", "获取hash失败");
				// if (!sp.getBoolean("logined", false)) {
				// Message message2 = handler.obtainMessage();
				// message2.what = 7;
				// message2.sendToTarget();
				// }
				// getTokenLocal(3);
			}

		});
	}

	public void updateProgress() {

		try {

			String url_progress = Constant.phpUrl + "/wap/api.php?action=GET_VIDEO_TIME&app_nonce=" + app_nonce
					+ "&tocken=" + mToken + "&hash_id=" + URLEncoder.encode(hashId) + "&current_time="
					+ player.getCurrentTime();

			Log.d("1221", "进度上传获取" + url_progress);
			NetHelper.get(url_progress, new SimpleSingleBeanNetHandler<Captcha>(PlayVideoActivity.this) {
				@Override
				protected void onSuccess(Captcha bean) {
					// TODO Auto-generated method stub
					try {
						int result_hash = Integer.parseInt(bean.result);
						switch (result_hash) {
						case 0:
							// hashId = bean.info;
							Log.d("1221", "进度上传为" + bean.info);
							break;
						case 99:
							editor.putBoolean("token_logined", false);
							editor.remove("mToken");
							editor.commit();
							break;
						default:
							break;
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					// super.onError(errorCode, errorMsg);
					Log.d("1221", "进度上传失败");
					// getTokenLocal(3);
				}

			});
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void endingReport() {
		try {
			String url_ending = Constant.phpUrl + "/wap/api.php?action=PUT_VIDEO_ENDING&app_nonce=" + app_nonce
					+ "&tocken=" + mToken + "&hash_id=" + URLEncoder.encode(hashId);
			hashId = null;
			Log.d("1221", "播放结束上传" + url_ending);
			NetHelper.get(url_ending, new SimpleSingleBeanNetHandler<Captcha>(PlayVideoActivity.this) {
				@Override
				protected void onSuccess(Captcha bean) {
					// TODO Auto-generated method stub
					try {
						int result_hash = Integer.parseInt(bean.result);
						switch (result_hash) {
						case 0:
							// hashId = bean.info;
							Log.d("1221", "播放结束上传结果为" + bean.info);
							break;
						case 99:
							editor.putBoolean("token_logined", false);
							editor.remove("mToken");
							editor.commit();
							break;
						default:
							break;
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					// super.onError(errorCode, errorMsg);
					Log.d("1221", "结束传失败");
					// getTokenLocal(3);
				}

			});

		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void setVideoInfo() {
		// 播放器释放
		player.release();
		// 信息重新加载
		load_quest = false;
		quests.clear();
		FragmentXiti.mHasLoadedOnce = false;
		// FragmentDirectory.mHasLoadedOnce = false;
		FragmentDescription.mHasLoadedOnce = false;

		// 重新载入视频信息
		List<VideoInfo> videos = new ArrayList<VideoInfo>();
		VideoInfo v1 = new VideoInfo();
		v1.description = "标清";
		v1.type = VideoInfo.VideoType.MP4;

		videos.add(v1);

		VideoInfo v2 = new VideoInfo();
		v2.description = "高清";
		v2.type = VideoInfo.VideoType.MP4;

		videos.add(v2);
		
		VideoInfo v3 = new VideoInfo();
		v3.description = "手机";
		v3.type = VideoInfo.VideoType.MP4;
		
		videos.add(v3);

		// v1.url = MyApplication.list_lecture.get(position_video).url_s;
		// v2.url = MyApplication.list_lecture.get(position_video).url_h;
		// v3.url = MyApplication.list_lecture.get(position_video).url_p;
		
		
//		v1.url = url_s;
//		v2.url = url_h;
//		v3.url = url_p;
		
		v1.url = url_real;
		v2.url = url_real;
		v3.url = url_real;
		
		
		Log.e("", "播放当前视频地址url="+url_real);
		
		firstloading = true;
		try {
			player.play(videos);
			
		} catch (Exception e) {
		}
		// playing = true;

		Log.d("123321", "执行到此");
		player.seekTo(0);

		player.setToggleFullScreenHandler(this);
		fullscreen = (ImageButton) player.findViewById(R.id.fullscreen);
		// mediacontroller_progress = (SeekBar)
		// player.findViewById(R.id.mediacontroller_progress);
		pause = (ImageButton) player.findViewById(R.id.pause);
		player.setListener(new PlayerListener() {

			@Override
			public void onStateChanged(int state) {
				// TODO Auto-generated method stub
				Log.d("123321", "播放状态改变" + state + "进度条状态");
				switch (state) {
				case 1:
					// Log.d("123321", "播放器空闲，既不在准备也不在播放");
					play_pause_view.setVisibility(View.VISIBLE);
					if (!NetUtil.isConnected(PlayVideoActivity.this)) {
						Toast.makeText(PlayVideoActivity.this, "您当前网络状态不佳，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
					}
					break;
				case 2:
					play_pause_view.setVisibility(View.VISIBLE);
					// Log.d("123321", "播放器正在准备");
					break;
				case 3:
					if (firstloading) {
						player.pause();
						firstloading = false;
					} else {
						play_pause_view.setVisibility(View.GONE);
						// if
						// (mediacontroller_progress.isPressed()&&NetUtil.isConnected(PlayVideoActivity.this))
						// {
						// play_pause_view.setVisibility(View.GONE);
						// } else
						if (!NetUtil.isConnected(PlayVideoActivity.this)) {
							play_pause_view.setVisibility(View.VISIBLE);
							Toast.makeText(PlayVideoActivity.this, "您当前网络状态不佳，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
						} // else if (mediacontroller_progress.getProgress() >
							// 0) {
							// play_pause_view.setVisibility(View.VISIBLE);
							// Toast.makeText(PlayVideoActivity.this,
							// "您当前网络状态不佳，请稍等", Toast.LENGTH_SHORT).show();
							// }
							// SeekBar seekBar=new
							// SeekBar(PlayVideoActivity.this);
							// seekBar.getSecondaryProgress()
							// seekBar.is

					}

					break;
				case 4:
					play_pause_view.setVisibility(View.VISIBLE);
					// Log.d("123321", "播放器准备好并可以立即播放当前位置");
					break;
				case 5:
					play_pause_view.setVisibility(View.GONE);
					if (hashId == null && !hashing) {
						hashing = true;
						getTokenLocal(3);
					}
					/*
					 * if (!wakeLock.isHeld()) { Log.d("abccba", "播放中获得锁");
					 * wakeLock.acquire(); }
					 */
					// playing = true;
					// Log.d("123321", "播放器正在播放中 ");
					break;
				case 6:
					if (hashId != null) {
						endingReport();
					}
					play_pause_view.setVisibility(View.VISIBLE);
					// Log.d("123321", " 播放已完毕 ");
					if (fullscreen != null && player.isFullScreen()) {
						fullscreen.performClick();
					}
					// handler.sendEmptyMessageDelayed(66, 10000);
					break;

				default:
					break;
				}
			}

			@Override
			public void onError(Exception error) {
				// TODO Auto-generated method stub
				Log.d("123321", "播放错误" + error);

			}
		});

		// fullscreen.setOnClickListener(this);
		// mediacontroller_progress = (SeekBar)
		// player.findViewById(R.id.mediacontroller_progress);

		// 等待列表及题目加载完全
		Message msg_loop = handler.obtainMessage();
		msg_loop.what = 4;
		msg_loop.sendToTarget();

	}

	private void initViewPager() {
		// Secure.puti
		viewPager = (ViewPager) findViewById(R.id.play_vp);
		// viewPager.setOffscreenPageLimit(2);
		fragmentsList = new ArrayList<Fragment>();
		Fragment fragment = new FragmentDescription();
		fragmentsList.add(fragment);
		fragment = new FragmentDirectory();
		fragmentsList.add(fragment);
		fragment = new FragmentXiti();
		fragmentsList.add(fragment);

		viewPager.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragmentsList));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		// TODO Auto-generated method stub
		// Log.d("123456", "position=" + position);
		offset = (screen1_3 - cursorImg.getLayoutParams().width) / 2;
		Log.d("111", position + "--" + positionOffset + "--" + positionOffsetPixels);
		// final float scale = getResources().getDisplayMetrics().density;
		if (position == 0) {// 0<->1
			lp.leftMargin = (int) (positionOffsetPixels / 3) + offset;
		} else if (position == 1) {// 1<->2
			lp.leftMargin = (int) (positionOffsetPixels / 3) + screen1_3 + offset;
		}
		cursorImg.setLayoutParams(lp);
		// currentIndex = position;
	}

	@Override
	public void onPageSelected(int position_tab) {
		// TODO Auto-generated method stub
		switch (position_tab) {
		case 0:
			tab1Tv.setTextColor(getResources().getColor(R.color.blue));
			tab2Tv.setTextColor(getResources().getColor(R.color.word_grey_liu));
			tab3Tv.setTextColor(getResources().getColor(R.color.word_grey_liu));
			break;
		case 1:
			tab2Tv.setTextColor(getResources().getColor(R.color.blue));
			tab1Tv.setTextColor(getResources().getColor(R.color.word_grey_liu));
			tab3Tv.setTextColor(getResources().getColor(R.color.word_grey_liu));
			break;
		case 2:
			tab3Tv.setTextColor(getResources().getColor(R.color.blue));
			tab2Tv.setTextColor(getResources().getColor(R.color.word_grey_liu));
			tab1Tv.setTextColor(getResources().getColor(R.color.word_grey_liu));
			break;

		default:
			break;
		}

	}

	public synchronized void getTokenLocal(int index) {
		mToken = new TokenManager(PlayVideoActivity.this).getToken();
		if (mToken != null) {
			getInfo(index);
		} else {
			LoginUtil.login_handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.obj = index;
			msg_loop.sendToTarget();
		}
	}

	private synchronized void getInfo(int index) {
		try {

			app_nonce = StringUtil.getPhoneIMEI(PlayVideoActivity.this);
			switch (index) {
			case 0:// 打开播放界面
				getList();
				getVideoQuest();
				if (sp.getBoolean("logined", false)) {
					addRecord();
					setRemark();
				}
				// getHash();
				break;
			case 1:// 更换视频
				getVideoQuest();
				if (sp.getBoolean("logined", false)) {
					addRecord();
					setRemark();
				}
				// getHash();
				break;
			case 2:// 改变收藏状态
				changeRemark();
				break;
			case 3:
				Log.d("234abc", "获取hash");
				getHash();
				break;
			case 4:
				updateProgress();
				break;
			case 5:
				addRecord();
				setRemark();
				break;
			default:
				break;
			}

		} catch (

		Exception e)

		{
			// TODO: handle exception
		}

	}

	private void addRecord() {
		// if (list_lecture_play == null || list_lecture_play.isEmpty()) {
		// url_lecture_add = Constant.phpUrl +
		// "/wap/api.php?action=GET_ADD_LECTURE_MARK&tocken=" + mToken
		// + "&app_nonce=" + app_nonce + "&lecture_id=" +
		// getIntent().getStringExtra("lecture_id");
		// } else {
		// url_lecture_add = Constant.phpUrl +
		// "/wap/api.php?action=GET_ADD_LECTURE_MARK&tocken=" + mToken
		// + "&app_nonce=" + app_nonce + "&lecture_id=" +
		// list_lecture_play.get(position_video).lecture_id;
		// }
		// Log.d("234abc", "添加播放记录URL= " + url_lecture_add);
		url_lecture_add = Constant.phpUrl + "/wap/api.php?action=GET_ADD_LECTURE_MARK&tocken=" + mToken + "&app_nonce="
				+ app_nonce + "&lecture_id=" + lecture_id;
		Log.d("234abc", "添加播放记录URL=   " + url_lecture_add);
		NetHelper.get(url_lecture_add, new SimpleSingleBeanNetHandler<Captcha>(PlayVideoActivity.this) {

			@Override
			protected void onSuccess(Captcha bean) {
				// TODO Auto-generated method stub
				// Toast.makeText(PlayVideoActivity.this, bean.info,
				// Toast.LENGTH_SHORT).show();
				Log.d("234abc", "播放记录结果" + bean.result + bean.info);
				try {
					int result = Integer.parseInt(bean.result);
					switch (result) {
					case 1:
						// Toast.makeText(PlayVideoActivity.this, "后台添加",
						// Toast.LENGTH_SHORT).show();
						if (RecordActivity.dra != null && RecordActivity.listItems != null && list_lecture_play != null
								&& !list_lecture_play.isEmpty()) {// 播放记录界面进入，且播放界面有目录
							Map<String, Object> listItem = new HashMap<String, Object>();
							loop_record: for (int i = 0; i < list_lecture_play.size(); i++) {
								LectureList dr = list_lecture_play.get(i);
								if (dr.lecture_id == lecture_id) {

									listItem.put("lecture_title", dr.lecture_title);
									listItem.put("lecture_click_count",
											Integer.parseInt(dr.lecture_click_count) + 30 + "");

									listItem.put("image_url", dr.head_image);
									listItem.put("lecture_id", dr.lecture_id);
									Log.d("123456", "添加id1为" + dr.lecture_id);
									listItem.put("lecture_cat1", dr.lecture_cat1);
									listItem.put("lecture_cat3", dr.lecture_cat3);
									listItem.put("lecture_questions_id", dr.lecture_questions_id);
									
//									listItem.put("url_s", dr.url_s);
//									listItem.put("url_h", dr.url_h);
//									listItem.put("url_p", dr.url_p);
									
									listItem.put("url_s", dr.video_local_name);
									listItem.put("url_h", dr.video_local_name);
									listItem.put("url_p", dr.video_local_name);
									
									listItem.put("lecture_content_url", dr.lecture_content_url);
									listItem.put("vheight_s", dr.vheight_s);
									listItem.put("vwidth_s", dr.vwidth_s);
									listItem.put("price", dr.price);
									// listItem.put("lecture_id",
									// dr.lecture_id);
									// Log.d("1221", "价格为 "+dr.lecture_operate);
									listItem.put("lecture_operate", dr.lecture_operate);
									// listItem.put("remark", dr.remark);
									// Log.d("345abc", "error_quest_count=" +
									// dr.lecture_add_time);
									// listItems.add(listItem);
									RecordActivity.listItems.add(0, listItem);
									RecordActivity.dra.notifyDataSetChanged();
									// Toast.makeText(PlayVideoActivity.this,
									// "后台添加1" +
									// RecordActivity.listItems.get(0).get("lecture_title").toString()
									// + "lecture_title=" + lecture_title,
									// Toast.LENGTH_SHORT).show();
									try {
										FileOutputStream outStream = new FileOutputStream(
												getCacheDir().toString() + "/record_list.txt");
										ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
										objectOutputStream.writeObject(RecordActivity.listItems);
										outStream.close();
									} catch (Exception e) {
										// TODO: handle exception

									}
									break loop_record;
								}

							}
							if (listItem.isEmpty()) {// 若列表不完全，则重新获取

								listItem.put("lecture_title", lecture_title);
								listItem.put("lecture_click_count", getIntent().getStringExtra("lecture_click_count"));
								listItem.put("image_url", getIntent().getStringExtra("image_url"));
								listItem.put("lecture_id", getIntent().getStringExtra("lecture_id"));
								Log.d("123456", "添加id2为" + lecture_id);
								listItem.put("lecture_cat1", getIntent().getStringExtra("lecture_cat1"));
								listItem.put("lecture_cat3", getIntent().getStringExtra("lecture_cat3"));
								listItem.put("lecture_questions_id", lecture_questions_id_o);
								
//								listItem.put("url_s", url_s);
//								listItem.put("url_h", url_h);
//								listItem.put("url_p", url_p);
								
								listItem.put("url_s", url_real);
								listItem.put("url_h", url_real);
								listItem.put("url_p", url_real);
								
								listItem.put("lecture_content", lecture_content);
								listItem.put("vheight_s", getIntent().getStringExtra("vheight_s"));
								listItem.put("vwidth_s", getIntent().getStringExtra("vwidth_s"));
								listItem.put("price", getIntent().getStringExtra("price"));
								// Log.d("1221", "价格为 "+dr.lecture_operate);
								listItem.put("lecture_operate", getIntent().getStringExtra("lecture_operate"));
								// listItem.put("remark", dr.remark);
								// Log.d("345abc", "error_quest_count=" +
								// dr.lecture_add_time);
								// listItems.add(listItem);

								RecordActivity.listItems.add(0, listItem);
								RecordActivity.dra.notifyDataSetChanged();
								try {
									FileOutputStream outStream = new FileOutputStream(
											getCacheDir().toString() + "/record_list.txt");
									ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
									objectOutputStream.writeObject(RecordActivity.listItems);
									outStream.close();
								} catch (Exception e) {
									// TODO: handle exception

								}
								// Toast.makeText(PlayVideoActivity.this,
								// "后台添加1-2" +
								// RecordActivity.listItems.get(0).get("lecture_title").toString()
								// + "lecture_title=" + lecture_title,
								// Toast.LENGTH_SHORT).show();
							}

						} else if (RecordActivity.dra != null && RecordActivity.listItems != null) {// 播放记录界面进入，但无列表
							Map<String, Object> listItem = new HashMap<String, Object>();
							listItem.put("lecture_title", lecture_title);
							listItem.put("lecture_click_count", getIntent().getStringExtra("lecture_click_count"));
							listItem.put("image_url", getIntent().getStringExtra("image_url"));
							listItem.put("lecture_id", getIntent().getStringExtra("lecture_id"));
							Log.d("123456", "添加id3为" + lecture_id);
							listItem.put("lecture_cat1", getIntent().getStringExtra("lecture_cat1"));
							listItem.put("lecture_cat3", getIntent().getStringExtra("lecture_cat3"));
							listItem.put("lecture_questions_id", lecture_questions_id_o);
							
//							listItem.put("url_s", url_s);
//							listItem.put("url_h", url_h);
//							listItem.put("url_p", url_p);
							
							listItem.put("url_s", url_real);
							listItem.put("url_h", url_real);
							listItem.put("url_p", url_real);
							
							listItem.put("lecture_content", lecture_content);
							listItem.put("vheight_s", getIntent().getStringExtra("vheight_s"));
							listItem.put("vwidth_s", getIntent().getStringExtra("vwidth_s"));
							listItem.put("price", getIntent().getStringExtra("price"));
							// Log.d("1221", "价格为 "+dr.lecture_operate);
							listItem.put("lecture_operate", getIntent().getStringExtra("lecture_operate"));
							// listItem.put("remark", dr.remark);
							// Log.d("345abc", "error_quest_count=" +
							// dr.lecture_add_time);
							// listItems.add(listItem);

							RecordActivity.listItems.add(0, listItem);
							RecordActivity.dra.notifyDataSetChanged();
							try {
								FileOutputStream outStream = new FileOutputStream(
										getCacheDir().toString() + "/record_list.txt");
								ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
								objectOutputStream.writeObject(RecordActivity.listItems);
								outStream.close();
							} catch (Exception e) {
								// TODO: handle exception

							}
							// Toast.makeText(PlayVideoActivity.this,
							// "后台添加2" +
							// RecordActivity.listItems.get(0).get("lecture_title").toString()
							// + "lecture_title=" + lecture_title,
							// Toast.LENGTH_SHORT).show();
							// break loop_record;
						} else if (RecordActivity.dra == null && RecordActivity.listItems == null) {// 非播放记录界面进入，添加进缓存
							Map<String, Object> listItem = new HashMap<String, Object>();
							if (list_lecture_play != null && !list_lecture_play.isEmpty()) {

								loop_record: for (int i = 0; i < list_lecture_play.size(); i++) {
									LectureList dr = list_lecture_play.get(i);
									if (dr.lecture_id == lecture_id) {

										listItem.put("lecture_title", dr.lecture_title);
										listItem.put("lecture_click_count",
												Integer.parseInt(dr.lecture_click_count) + 30 + "");

										listItem.put("image_url", dr.head_image);
										listItem.put("lecture_id", dr.lecture_id);
										Log.d("123456", "添加id4为" + dr.lecture_id);
										listItem.put("lecture_cat1", dr.lecture_cat1);
										listItem.put("lecture_cat3", dr.lecture_cat3);
										listItem.put("lecture_questions_id", dr.lecture_questions_id);
										
//										listItem.put("url_s", dr.url_s);
//										listItem.put("url_h", dr.url_h);
//										listItem.put("url_p", dr.url_p);
										
										listItem.put("url_s", dr.video_local_name);
										listItem.put("url_h", dr.video_local_name);
										listItem.put("url_p", dr.video_local_name);
										
										listItem.put("lecture_content_url", dr.lecture_content_url);
										listItem.put("vheight_s", dr.vheight_s);
										listItem.put("vwidth_s", dr.vwidth_s);
										listItem.put("price", dr.price);
										// Log.d("1221", "价格为
										// "+dr.lecture_operate);
										listItem.put("lecture_operate", dr.lecture_operate);

										break loop_record;
									}

								}
								if (listItem.isEmpty()) {

									listItem.put("lecture_title", lecture_title);
									listItem.put("lecture_click_count",
											getIntent().getStringExtra("lecture_click_count"));
									listItem.put("image_url", getIntent().getStringExtra("image_url"));
									listItem.put("lecture_id", getIntent().getStringExtra("lecture_id"));
									Log.d("123456", "添加id5为" + lecture_id);
									listItem.put("lecture_cat1", getIntent().getStringExtra("lecture_cat1"));
									listItem.put("lecture_cat3", getIntent().getStringExtra("lecture_cat3"));
									listItem.put("lecture_questions_id", lecture_questions_id_o);
									
//									listItem.put("url_s", url_s);
//									listItem.put("url_h", url_h);
//									listItem.put("url_p", url_p);
									
									listItem.put("url_s", url_real);
									listItem.put("url_h", url_real);
									listItem.put("url_p", url_real);
									
									listItem.put("lecture_content", lecture_content);
									listItem.put("vheight_s", getIntent().getStringExtra("vheight_s"));
									listItem.put("vwidth_s", getIntent().getStringExtra("vwidth_s"));
									listItem.put("price", getIntent().getStringExtra("price"));
									// Log.d("1221", "价格为
									// "+dr.lecture_operate);
									listItem.put("lecture_operate", getIntent().getStringExtra("lecture_operate"));

								}
							} else {
								listItem.put("lecture_title", lecture_title);
								listItem.put("lecture_click_count", getIntent().getStringExtra("lecture_click_count"));
								listItem.put("image_url", getIntent().getStringExtra("image_url"));
								listItem.put("lecture_id", getIntent().getStringExtra("lecture_id"));
								Log.d("123456", "添加id6为" + lecture_id);
								listItem.put("lecture_cat1", getIntent().getStringExtra("lecture_cat1"));
								listItem.put("lecture_cat3", getIntent().getStringExtra("lecture_cat3"));
								listItem.put("lecture_questions_id", lecture_questions_id_o);
								
//								listItem.put("url_s", url_s);
//								listItem.put("url_h", url_h);
//								listItem.put("url_p", url_p);
								
								listItem.put("url_s", url_real);
								listItem.put("url_h", url_real);
								listItem.put("url_p", url_real);
								
								listItem.put("lecture_content", lecture_content);
								listItem.put("vheight_s", getIntent().getStringExtra("vheight_s"));
								listItem.put("vwidth_s", getIntent().getStringExtra("vwidth_s"));
								listItem.put("price", getIntent().getStringExtra("price"));
								// Log.d("1221", "价格为 "+dr.lecture_operate);
								listItem.put("lecture_operate", getIntent().getStringExtra("lecture_operate"));
							}
							try {
								FileInputStream freader = new FileInputStream(
										getCacheDir().toString() + "/record_list.txt");
								ObjectInputStream objectInputStream = new ObjectInputStream(freader);
								@SuppressWarnings("unchecked")
								List<Map<String, Object>> listItems_cache_record = (List<Map<String, Object>>) objectInputStream
										.readObject();
								objectInputStream.close();
								try {
									listItems_cache_record.add(0, listItem);
									FileOutputStream outStream = new FileOutputStream(
											getCacheDir().toString() + "/record_list.txt");
									ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
									objectOutputStream.writeObject(listItems_cache_record);
									outStream.close();
								} catch (Exception e) {
									// TODO: handle exception
								}
							} catch (Exception e) {
								// TODO: handle exception
								// try {
								// List<Map<String, Object>>
								// listItems_cache_record = new
								// ArrayList<Map<String, Object>>();
								// listItems_cache_record.add(0, listItem);
								// FileOutputStream outStream = new
								// FileOutputStream(
								// getCacheDir().toString() +
								// "/record_list.txt");
								// ObjectOutputStream objectOutputStream = new
								// ObjectOutputStream(outStream);
								// objectOutputStream.writeObject(listItems_cache_record);
								// outStream.close();
								/// } catch (Exception e2) {
								// TODO: handle exception
								// }
							}
						}
						// RecordActivity.listItems.add(0 object);
						// RecordActivity.dra.notifyDataSetChanged();
						break;
					case 99:
						editor.putBoolean("token_logined", false);
						editor.remove("mToken");
						editor.commit();
						break;
					case 100:
						if (sp.getBoolean("logined", false)) {
							// Toast.makeText(RecordActivity.this,
							// "网络错误，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
							editor.putBoolean("token_logined", false);
							editor.putBoolean("logined", false);
							MyApplication.logined = false;
							new LoginUtil(PlayVideoActivity.this, 20);
						}
						break;
					default:
						break;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

			@Override
			protected void onError(int errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				// super.onError(errorCode, errorMsg);
				// Toast.makeText(PlayVideoActivity.this, "添加失败",
				// Toast.LENGTH_SHORT).show();
			}

		});
	}

	private void changeRemark() {

		// String app_nonce = StringUtil.getPhoneIMEI(this);
		int remark = 0;
		if (PlayVideoActivity.remark.equals("1")) {
			remark = 2;
		} else {
			remark = 1;
		}
		/*
		 * String id_change_remark; // Log.d("abccba", "收藏按钮点击1" + //
		 * MyApplication.list_lecture.get(position_video).remark); if
		 * (list_lecture_play == null || list_lecture_play.isEmpty()) { switch
		 * (Integer.parseInt(getIntent().getStringExtra("remark"))) { case 1:
		 * remark = 2; break; default: remark = 1; break; } id_change_remark =
		 * getIntent().getStringExtra("lecture_id"); } else { switch
		 * (Integer.parseInt(list_lecture_play.get(position_video).remark)) {
		 * case 1: remark = 2; break; default: remark = 1; break; }
		 * id_change_remark = list_lecture_play.get(position_video).lecture_id;
		 * }
		 */
		String url_shoucang = Constant.phpUrl + "/wap/api.php?action=GET_CHANGE_REMARK&app_nonce=" + app_nonce
				+ "&tocken=" + mToken + "&lecture_id=" + lecture_id + "&remark=" + remark;
		Log.d("abccba", "改变收藏状态" + url_shoucang);
		NetHelper.get(url_shoucang, new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				// TODO Auto-generated method stub
				// dialog.dismiss();
				change_remarking = false;
				try {
					int result = Integer.parseInt(bean.result);
					switch (result) {
					case 0:
						Toast.makeText(PlayVideoActivity.this, bean.info, Toast.LENGTH_SHORT).show();
						if (bean.info.contains("取消")) {

							lecture_sc.setImageResource(R.drawable.souc);
							PlayVideoActivity.remark = "2";

							if (FragmentMyVideo.fmv_instance != null && FragmentMyVideo.listItems != null
									&& !FragmentMyVideo.listItems.isEmpty() && FragmentMyVideo.vca != null) {
								loop_collect: for (Iterator<Map<String, Object>> it = FragmentMyVideo.listItems
										.iterator(); it.hasNext();) {
									// Log.d("1221", "遍历寻找");
									Map<String, Object> map = it.next();
									if (map.get("lecture_id").toString().equals(lecture_id)) {
										it.remove();
										FragmentMyVideo.vca.notifyDataSetChanged();

										// Toast.makeText(PlayVideoActivity.this,
										// "取消收藏通知", Toast.LENGTH_SHORT).show();
										try {// 获取数据写入本地
											FileOutputStream outStream = new FileOutputStream(
													getCacheDir().toString() + "/myvideo_list.txt");
											ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
											objectOutputStream.writeObject(FragmentMyVideo.listItems);
											outStream.close();
										} catch (Exception e) {
											// TODO: handle exception

										}
										break loop_collect;
									}
								}

							} else {
								try {
									FileInputStream freader = new FileInputStream(
											getCacheDir().toString() + "/myvideo_list.txt");
									ObjectInputStream objectInputStream = new ObjectInputStream(freader);
									@SuppressWarnings("unchecked")
									List<Map<String, Object>> listItems_cache_co = (List<Map<String, Object>>) objectInputStream
											.readObject();
									if (listItems_cache_co != null && !listItems_cache_co.isEmpty()) {
										loop_collect: for (Iterator<Map<String, Object>> it = listItems_cache_co
												.iterator(); it.hasNext();) {
											// Log.d("1221", "遍历寻找");
											Map<String, Object> map = it.next();
											if (map.get("lecture_id").toString().equals(lecture_id)) {

												try {// 获取数据写入本地
													it.remove();
													FileOutputStream outStream = new FileOutputStream(
															getCacheDir().toString() + "/myvideo_list.txt");
													ObjectOutputStream objectOutputStream = new ObjectOutputStream(
															outStream);
													objectOutputStream.writeObject(listItems_cache_co);
													outStream.close();
												} catch (Exception e) {
													// TODO: handle exception

												}
												break loop_collect;
											}
										}
										objectInputStream.close();
									}
								} catch (Exception e) {
									// TODO: handle exception
								}
							}
						} else {
							lecture_sc.setImageResource(R.drawable.souc1);
							PlayVideoActivity.remark = "1";
							if (FragmentMyVideo.fmv_instance != null && FragmentMyVideo.listItems != null
									&& FragmentMyVideo.vca != null && list_lecture_play != null
									&& !list_lecture_play.isEmpty()) {
								Map<String, Object> listItem = new HashMap<String, Object>();
								loop_record: for (int i = 0; i < list_lecture_play.size(); i++) {
									LectureList dr = list_lecture_play.get(i);
									if (dr.lecture_id == lecture_id) {
										listItem.put("lecture_title", dr.lecture_title);
										listItem.put("lecture_click_count",
												Integer.parseInt(dr.lecture_click_count) + 30 + "");

										listItem.put("image_url", dr.head_image);
										listItem.put("lecture_id", dr.lecture_id);
										listItem.put("lecture_cat1", dr.lecture_cat1);
										listItem.put("lecture_cat3", dr.lecture_cat3);
										listItem.put("lecture_questions_id", dr.lecture_questions_id);
										
//										listItem.put("url_s", dr.url_s);
//										listItem.put("url_h", dr.url_h);
//										listItem.put("url_p", dr.url_p);
										
										listItem.put("url_s", dr.video_local_name);
										listItem.put("url_h", dr.video_local_name);
										listItem.put("url_p", dr.video_local_name);
										
										listItem.put("lecture_content_url", dr.lecture_content_url);
										listItem.put("vheight_s", dr.vheight_s);
										listItem.put("vwidth_s", dr.vwidth_s);
										listItem.put("price", dr.price);
										// Log.d("1221", "价格为
										// "+dr.lecture_operate);
										listItem.put("lecture_operate", dr.lecture_operate);
										listItem.put("remark", PlayVideoActivity.remark);
										// listItem.put("remark", dr.remark);
										// Log.d("345abc", "error_quest_count="
										// +
										// dr.lecture_add_time);
										// listItems.add(listItem);
										FragmentMyVideo.listItems.add(0, listItem);
										FragmentMyVideo.vca.notifyDataSetChanged();
										// Toast.makeText(PlayVideoActivity.this,
										// "收藏通知1", Toast.LENGTH_SHORT).show();
										try {// 获取数据写入本地
											FileOutputStream outStream = new FileOutputStream(
													getCacheDir().toString() + "/myvideo_list.txt");
											ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
											objectOutputStream.writeObject(FragmentMyVideo.listItems);
											outStream.close();
										} catch (Exception e) {
											// TODO: handle exception

										}
										break loop_record;
									}

								}
								if (listItem.isEmpty()) {

									listItem.put("lecture_title", lecture_title);
									listItem.put("lecture_click_count",
											getIntent().getStringExtra("lecture_click_count"));
									listItem.put("image_url", getIntent().getStringExtra("image_url"));
									listItem.put("lecture_id", lecture_id);
									listItem.put("lecture_cat1", getIntent().getStringExtra("lecture_cat1"));
									listItem.put("lecture_cat3", getIntent().getStringExtra("lecture_cat3"));
									listItem.put("lecture_questions_id", lecture_questions_id_o);
									
//									listItem.put("url_s", url_s);
//									listItem.put("url_h", url_h);
//									listItem.put("url_p", url_p);
									
									listItem.put("url_s", url_real);
									listItem.put("url_h", url_real);
									listItem.put("url_p", url_real);
									
									listItem.put("lecture_content", lecture_content);
									listItem.put("vheight_s", getIntent().getStringExtra("vheight_s"));
									listItem.put("vwidth_s", getIntent().getStringExtra("vwidth_s"));
									listItem.put("price", getIntent().getStringExtra("price"));
									// Log.d("1221", "价格为
									// "+dr.lecture_operate);
									listItem.put("lecture_operate", getIntent().getStringExtra("lecture_operate"));

									listItem.put("remark", PlayVideoActivity.remark);
									FragmentMyVideo.listItems.add(0, listItem);
									FragmentMyVideo.vca.notifyDataSetChanged();
									try {// 获取数据写入本地
										FileOutputStream outStream = new FileOutputStream(
												getCacheDir().toString() + "/myvideo_list.txt");
										ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
										objectOutputStream.writeObject(FragmentMyVideo.listItems);
										outStream.close();
									} catch (Exception e) {
										// TODO: handle exception

									}
									// Toast.makeText(PlayVideoActivity.this,
									// "收藏通知1-1", Toast.LENGTH_SHORT).show();

								}
							} else if (FragmentMyVideo.vca != null && FragmentMyVideo.listItems != null) {
								Map<String, Object> listItem = new HashMap<String, Object>();
								listItem.put("lecture_title", lecture_title);
								listItem.put("lecture_click_count", getIntent().getStringExtra("lecture_click_count"));
								listItem.put("image_url", getIntent().getStringExtra("image_url"));
								listItem.put("lecture_id", lecture_id);
								listItem.put("lecture_cat1", getIntent().getStringExtra("lecture_cat1"));
								listItem.put("lecture_cat3", getIntent().getStringExtra("lecture_cat3"));
								listItem.put("lecture_questions_id", lecture_questions_id_o);
								
//								listItem.put("url_s", url_s);
//								listItem.put("url_h", url_h);
//								listItem.put("url_p", url_p);
								
								listItem.put("url_s", url_real);
								listItem.put("url_h", url_real);
								listItem.put("url_p", url_real);
								
								listItem.put("lecture_content", lecture_content);
								listItem.put("vheight_s", getIntent().getStringExtra("vheight_s"));
								listItem.put("vwidth_s", getIntent().getStringExtra("vwidth_s"));
								listItem.put("price", getIntent().getStringExtra("price"));
								// Log.d("1221", "价格为 "+dr.lecture_operate);
								listItem.put("lecture_operate", getIntent().getStringExtra("lecture_operate"));

								listItem.put("remark", PlayVideoActivity.remark);
								FragmentMyVideo.listItems.add(0, listItem);
								FragmentMyVideo.vca.notifyDataSetChanged();
								try {// 获取数据写入本地
									FileOutputStream outStream = new FileOutputStream(
											getCacheDir().toString() + "/myvideo_list.txt");
									ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
									objectOutputStream.writeObject(FragmentMyVideo.listItems);
									outStream.close();
								} catch (Exception e) {
									// TODO: handle exception

								}
								// Toast.makeText(PlayVideoActivity.this,
								// "收藏通知2", Toast.LENGTH_SHORT).show();
							} else if (FragmentMyVideo.vca == null && FragmentMyVideo.listItems == null) {
								Map<String, Object> listItem = new HashMap<String, Object>();
								if (list_lecture_play != null && !list_lecture_play.isEmpty()) {
									loop_record: for (int i = 0; i < list_lecture_play.size(); i++) {
										LectureList dr = list_lecture_play.get(i);
										if (dr.lecture_id == lecture_id) {
											listItem.put("lecture_title", dr.lecture_title);
											listItem.put("lecture_click_count",
													Integer.parseInt(dr.lecture_click_count) + 30 + "");

											listItem.put("image_url", dr.head_image);
											listItem.put("lecture_id", dr.lecture_id);
											listItem.put("lecture_cat1", dr.lecture_cat1);
											listItem.put("lecture_cat3", dr.lecture_cat3);
											listItem.put("lecture_questions_id", dr.lecture_questions_id);
											
//											listItem.put("url_s", dr.url_s);
//											listItem.put("url_h", dr.url_h);
//											listItem.put("url_p", dr.url_p);
											
											listItem.put("url_s", dr.video_local_name);
											listItem.put("url_h", dr.video_local_name);
											listItem.put("url_p", dr.video_local_name);
											
											listItem.put("lecture_content_url", dr.lecture_content_url);
											listItem.put("vheight_s", dr.vheight_s);
											listItem.put("vwidth_s", dr.vwidth_s);
											listItem.put("price", dr.price);
											// Log.d("1221", "价格为
											// "+dr.lecture_operate);
											listItem.put("lecture_operate", dr.lecture_operate);
											listItem.put("remark", PlayVideoActivity.remark);
											break loop_record;
										}

									}
									if (listItem.isEmpty()) {

										listItem.put("lecture_title", lecture_title);
										listItem.put("lecture_click_count",
												getIntent().getStringExtra("lecture_click_count"));
										listItem.put("image_url", getIntent().getStringExtra("image_url"));
										listItem.put("lecture_id", lecture_id);
										listItem.put("lecture_cat1", getIntent().getStringExtra("lecture_cat1"));
										listItem.put("lecture_cat3", getIntent().getStringExtra("lecture_cat3"));
										listItem.put("lecture_questions_id", lecture_questions_id_o);
										
//										listItem.put("url_s", url_s);
//										listItem.put("url_h", url_h);
//										listItem.put("url_p", url_p);
										
										listItem.put("url_s", url_real);
										listItem.put("url_h", url_real);
										listItem.put("url_p", url_real);
										
										listItem.put("lecture_content", lecture_content);
										listItem.put("vheight_s", getIntent().getStringExtra("vheight_s"));
										listItem.put("vwidth_s", getIntent().getStringExtra("vwidth_s"));
										listItem.put("price", getIntent().getStringExtra("price"));
										// Log.d("1221", "价格为
										// "+dr.lecture_operate);
										listItem.put("lecture_operate", getIntent().getStringExtra("lecture_operate"));

										listItem.put("remark", PlayVideoActivity.remark);

										// Toast.makeText(PlayVideoActivity.this,
										// "收藏通知1-1",
										// Toast.LENGTH_SHORT).show();

									}
								} else {
									listItem.put("lecture_title", lecture_title);
									listItem.put("lecture_click_count",
											getIntent().getStringExtra("lecture_click_count"));
									listItem.put("image_url", getIntent().getStringExtra("image_url"));
									listItem.put("lecture_id", lecture_id);
									listItem.put("lecture_cat1", getIntent().getStringExtra("lecture_cat1"));
									listItem.put("lecture_cat3", getIntent().getStringExtra("lecture_cat3"));
									listItem.put("lecture_questions_id", lecture_questions_id_o);
									
//									listItem.put("url_s", url_s);
//									listItem.put("url_h", url_h);
//									listItem.put("url_p", url_p);
									
									listItem.put("url_s", url_real);
									listItem.put("url_h", url_real);
									listItem.put("url_p", url_real);
									
									listItem.put("lecture_content", lecture_content);
									listItem.put("vheight_s", getIntent().getStringExtra("vheight_s"));
									listItem.put("vwidth_s", getIntent().getStringExtra("vwidth_s"));
									listItem.put("price", getIntent().getStringExtra("price"));
									// Log.d("1221", "价格为 "+dr.lecture_operate);
									listItem.put("lecture_operate", getIntent().getStringExtra("lecture_operate"));
									listItem.put("remark", PlayVideoActivity.remark);
								}
								try {
									FileInputStream freader = new FileInputStream(
											getCacheDir().toString() + "/myvideo_list.txt");
									ObjectInputStream objectInputStream = new ObjectInputStream(freader);
									@SuppressWarnings("unchecked")
									List<Map<String, Object>> listItems_cache_record = (List<Map<String, Object>>) objectInputStream
											.readObject();
									objectInputStream.close();
									try {
										listItems_cache_record.add(0, listItem);
										FileOutputStream outStream = new FileOutputStream(
												getCacheDir().toString() + "/myvideo_list.txt");
										ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
										objectOutputStream.writeObject(listItems_cache_record);
										outStream.close();
									} catch (Exception e) {
										// TODO: handle exception
									}
								} catch (Exception e) {
									// TODO: handle exception

								}
							}
							// Toast.makeText(PlayVideoActivity, text, duration)
						}

						break;
					case 99:
						editor.putBoolean("token_logined", false);
						editor.remove("mToken");
						editor.commit();
						break;
					case 100:
						if (sp.getBoolean("logined", false)) {
							Toast.makeText(PlayVideoActivity.this, "网络错误，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
							editor.putBoolean("token_logined", false);
							editor.putBoolean("logined", false);
							MyApplication.logined = false;
							new LoginUtil(PlayVideoActivity.this, 20);
						}
						break;
					default:
						Toast.makeText(PlayVideoActivity.this, bean.info, Toast.LENGTH_SHORT).show();
						break;
					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.d("1221", "收藏异常---" + e.toString());
				}

			}

			@Override
			protected void onError(int errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				// super.onError(errorCode, errorMsg);
				// dialog.dismiss();
				change_remarking = false;
				Toast.makeText(PlayVideoActivity.this, R.string.net_error_warning, Toast.LENGTH_SHORT).show();
			}

		});
	}

	private void getList() {
		// Toast.makeText(PlayVideoActivity.this, "更新列表",
		// Toast.LENGTH_SHORT).show();
		Log.d("123321", "加载列表----------");
		if (!load_finished) {
			String cat3_id = getIntent().getStringExtra("lecture_cat3");
			url_lecture_list = Constant.phpUrl + "/wap/api.php?action=GET_VIDEO_LIST&tocken=" + mToken + "&app_nonce="
					+ app_nonce + "&page_no=" + page_no + "&cat3_id=" + cat3_id;
			Log.e("123321", "播放视频目录url = " + url_lecture_list);
			NetHelper.get(url_lecture_list, new SimpleMultiBeanNetHandler<LectureList>(this) {
				@Override
				protected void onSuccess(List<LectureList> list) {
					for (int i = 0; i < list.size(); i++) {
						LectureList ll = list.get(i);
						if (ll.video_status.equals("2")) {
							if (list_lecture_play != null) {
								list_lecture_play.add(ll);
							}
						}
					}
					page_no++;
					getList();
				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					Log.d("123321", "详情页列表加载完毕" + errorMsg);
					// Toast.makeText(PlayVideoActivity.this, "结束",
					// Toast.LENGTH_SHORT).show();
					FragmentDirectory.mHasLoadedOnce = false;
					load_finished = true;
					if (FragmentDirectory.fmt != null) {
						FragmentDirectory.fmt.refresh();
					}
					Log.d("123456", "列表加载完毕");
				}

			});

		}
	}

	private void getVideoQuest() {

		for (int i = 0; i < lecture_questions_id.length; i++) {
			url_lecture_questions = Constant.phpUrl + "/wap/api.php?action=GET_QUEST&tocken=" + mToken + "&app_nonce="
					+ app_nonce + "&quest_id=" + lecture_questions_id[i];

			Log.d("123321", "视频播放界面题目详情url为" + url_lecture_questions);

			NetHelper.get(url_lecture_questions, new SimpleSingleBeanNetHandler<Quest>(PlayVideoActivity.this) {

				@Override
				protected void onSuccess(Quest bean) {
					// TODO Auto-generated method stub
					if (quests != null) {
						quests.add(bean);
					}

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					// super.onError(errorCode, errorMsg);
				}

			});
			if (i == lecture_questions_id.length - 1) {
				time_list_task = new TimerTask() {
					@Override
					public void run() {
						// 需要做的事:发送消息
						// LoginUtil.login_handlered = false;

						Log.d("345abc", "视频播放获取题目详情延时发送");
						Message msg_loop = handler.obtainMessage();
						msg_loop.what = 3;
						// msg_loop.obj = index;
						msg_loop.sendToTarget();
					}
				};
				timer_list.schedule(time_list_task, 1000);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.tab1_tv:
			// Toast.makeText(CollectActivity.this, "tab1_tv点击",
			// Toast.LENGTH_SHORT).show();
			tab1Tv.setTextColor(getResources().getColor(R.color.blue));
			tab2Tv.setTextColor(getResources().getColor(R.color.word_grey_liu));
			tab3Tv.setTextColor(getResources().getColor(R.color.word_grey_liu));
			viewPager.setCurrentItem(0);
			// Toast.makeText(PlayVideoActivity.this, "" +
			// viewPager.getCurrentItem(), Toast.LENGTH_SHORT).show();

			break;
		case R.id.tab2_tv:
			// Toast.makeText(CollectActivity.this, "tab1_tv点击",
			// Toast.LENGTH_SHORT).show();
			tab2Tv.setTextColor(getResources().getColor(R.color.blue));
			tab1Tv.setTextColor(getResources().getColor(R.color.word_grey_liu));
			tab3Tv.setTextColor(getResources().getColor(R.color.word_grey_liu));
			viewPager.setCurrentItem(1);
			// Toast.makeText(PlayVideoActivity.this, "" +
			// viewPager.getCurrentItem(), Toast.LENGTH_SHORT).show();
			break;
		case R.id.tab3_tv:
			tab3Tv.setTextColor(getResources().getColor(R.color.blue));
			tab2Tv.setTextColor(getResources().getColor(R.color.word_grey_liu));
			tab1Tv.setTextColor(getResources().getColor(R.color.word_grey_liu));
			viewPager.setCurrentItem(2);
			// Toast.makeText(PlayVideoActivity.this, "" +
			// viewPager.getCurrentItem(), Toast.LENGTH_SHORT).show();
			break;
		case R.id.back_search_logininput:

			finish();
			// player.play();
			break;
		// case R.id.player:
		// Toast.makeText(PlayVideoActivity.this, "播放器点击",
		// Toast.LENGTH_SHORT).show();
		// break;
		case R.id.play_pause_view:
			// Toast.makeText(PlayVideoActivity.this,
			// "播放器点击",Toast.LENGTH_SHORT).show();
			pause.performClick();
			break;
		case R.id.lecture_sc:
			if (!change_remarking && !remark_loading) {
				change_remarking = true;
				getTokenLocal(2);
			} else if (!change_remarking && remark_loading) {
				Toast.makeText(PlayVideoActivity.this, "正在获取微课收藏状态，请稍后", Toast.LENGTH_SHORT).show();
			}
			break;
		default:

			break;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		try {
			removeTimer(timer_88);
			removeTimerTask(timer_88_task);
			removeTimer(timer_99);
			removeTimerTask(timer_99_task);
			removeTimer(timer_list);
			removeTimerTask(time_list_task);
			removeTimer(timer_loading);
			removeTimerTask(timer_loading_task);
			removeTimer(timer_progress);
			removeTimerTask(timer_progress_task);
			removeTimer(timer_token);
			removeTimerTask(timer_token_task);
			removeTimer(timer_login);
			removeTimerTask(timer_login_task);
			try {
				handler.removeCallbacksAndMessages(null);
				// handler = null;
			} catch (Exception e) {
				// TODO: handle exception
			}

			try {
				player.release();
			} catch (Exception e) {
				// TODO: handle exception
			}

			try {
				mOrientationListener.disable();
				mOrientationListener = null;
			} catch (Exception e) {
				// TODO: handle exception
			}

			try {
				list_lecture_play.clear();
				list_lecture_play = null;
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				quests.clear();
				quests = null;
			} catch (Exception e) {
				// TODO: handle exception
			}

			try {
				lecture_id = null;
				load_finished = false;
				load_quest = false;
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				FragmentDirectory.fmt = null;
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				FragmentXiti.fmt.fmt = null;
				FragmentXiti.xiti_index = 0;
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (bd != null) {
				try {
					bd.dismiss();
					bd = null;
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
			hashId = null;
			try {
				fragmentsList.clear();
				fragmentsList = null;
			} catch (Exception e) {
				// TODO: handle exception
			}

			pva_instance = null;
		} catch (Exception e) {
			// TODO: handle exception
			Log.d("123321", "错误" + e.toString());
		}

		// lecture_questions_id=[];
		/*
		 * if(wakeLock.isHeld()){ Log.d("abccba", "Activity结束释放锁");
		 * wakeLock.release(); }
		 */
		// try {
		// if (dialog != null) {
		// dialog.dismiss();
		// }
		// } catch (Exception e) {
		// TODO: handle exception
		// }
		super.finish();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (player != null) {

			switch (player.getCurrentStatus()) {
			case 5:
				play_status = true;
				break;

			default:
				play_status = false;
				break;
			}
			player.pause();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (player != null) {
			if (play_status) {
				player.play();
			}

		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				params = (android.view.ViewGroup.LayoutParams) player.getLayoutParams();
				params.width = screenWidth;
				params.height = (int) port_height;
				player.setLayoutParams(params);
				play_content.setVisibility(View.VISIBLE);
				title_play_video.setVisibility(View.VISIBLE);
			} else {
				// try {
				// if (dialog != null) {
				// dialog.dismiss();
				// }
				// } catch (Exception e) {
				// TODO: handle exception
				// }
				finish();
			}
			return true;
		// break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * @Override public void onConfigurationChanged(Configuration newConfig) {
	 * // TODO Auto-generated method stub
	 * super.onConfigurationChanged(newConfig); String screen =
	 * newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? "横向屏幕" :
	 * "竖向屏幕"; Toast.makeText(this, "系统屏幕方向发送改变 " + screen,
	 * Toast.LENGTH_SHORT).show(); //fullscreen = (ImageButton)
	 * player.findViewById(R.id.fullscreen); switch (newConfig.orientation) {
	 * case Configuration.ORIENTATION_LANDSCAPE: if (player != null &&
	 * !player.isFullScreen()) { fullscreen.performClick(); } break; case
	 * Configuration.ORIENTATION_PORTRAIT: if (player != null &&
	 * player.isFullScreen()) { fullscreen.performClick(); } break; default:
	 * break; } }
	 */
	@Override
	public void OnChange() {
		// TODO Auto-generated method stub
		// Log.d("111", "自动旋转为"+auto_change);
		// Log.d("111", "执行点击");
		if (!auto_change) {
			mClick = true;
			mOrientationListener.disable();
			Message message = handler.obtainMessage();
			message.what = 77;
			handler.sendMessageDelayed(message, 1000);
			// handler.sendEmptyMessageDelayed(77, 1000);
			// message.sendToTarget();
		}
		// if (player.isFullScreen()) {
		if (mIsLand) {
			play_content.setVisibility(View.VISIBLE);
			title_play_video.setVisibility(View.VISIBLE);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			params = (android.view.ViewGroup.LayoutParams) player.getLayoutParams();
			params.width = screenWidth;
			params.height = (int) port_height;
			player.setLayoutParams(params);
			timer_88_task = new TimerTask() {
				@Override
				public void run() {
					// 需要做的事:发送消息
					// LoginUtil.login_handlered = false;
					// Log.d("345abc", "播放记录循环等待4");
					Message msg_loop = handler.obtainMessage();
					msg_loop.what = 88;
					msg_loop.sendToTarget();
				}
			};
			timer_88.schedule(timer_88_task, 10);
			// Log.d("1221", "播放器全屏" + player.isFullScreen() + "退出全屏" + "播放器宽为"
			// + params.width + "播放器高为" + params.height);

		} else {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			params = (android.view.ViewGroup.LayoutParams) player.getLayoutParams();
			params.width = (int) land_width;
			params.height = (int) land_height;
			player.setLayoutParams(params);
			play_content.setVisibility(View.GONE);
			title_play_video.setVisibility(View.GONE);
			timer_99_task = new TimerTask() {

				@Override
				public void run() {
					// 需要做的事:发送消息
					// LoginUtil.login_handlered = false;
					// Log.d("345abc", "播放记录循环等待5");
					Message msg_loop = handler.obtainMessage();
					msg_loop.what = 99;
					msg_loop.sendToTarget();
				}

			};
			timer_99.schedule(timer_99_task, 10);
			// Log.d("1221", "播放器全屏" + player.isFullScreen() + "进入全屏" + "播放器宽为"
			// + params.width + "播放器高为" + params.height);
			// checkFullscreen();
		}
	}

	/**
	 * 开启监听器
	 */
	private final void startListener() {
		mOrientationListener = new OrientationEventListener(this) {
			@Override
			public void onOrientationChanged(int rotation) {
				// 设置竖屏
				// Log.d("111", "点击为"+mClick+"点击横屏为"+mClickLand);
				// Log.d("1221", "屏幕角度" + rotation);
				if (((rotation >= 0) && (rotation <= 30)) || (rotation >= 330)) {
					// Log.d("111", "进入设置竖屏");
					if (mClick) {
						if (mIsLand && !mClickLand) {
							// Log.d("111", "进入设置竖屏----------------------返回");
							return;
						} else {
							// Log.d("111",
							// "进入设置竖屏--------------------------修改");
							mClickPort = true;
							mClick = false;
							mIsLand = false;
						}
					} else {
						// Log.d("111",
						// "进入设置竖屏--------------------------等待不切换");
						if (mIsLand) {
							// Log.d("111",
							// "进入设置竖屏--------------------------自动切换竖屏");
							Log.d("1221", "自动切换竖屏");
							// Toast.makeText(PlayVideoActivity.this,
							// "切换竖屏"+rotation, Toast.LENGTH_SHORT).show();
							auto_change = true;
							mOrientationListener.disable();
							Message message = handler.obtainMessage();
							message.what = 77;
							handler.sendMessageDelayed(message, 1000);
							// handler.sendEmptyMessageDelayed(77, 1000);
							fullscreen.performClick();
							// mIsLand = false;
							// mClick = false;
						}
					}
				}
				// 设置横屏
				else if (((rotation >= 230) && (rotation <= 310))) {
					// Log.d("111", "进入设置横屏");
					if (mClick) {
						if (!mIsLand && !mClickPort) {
							return;
						} else {
							mClickLand = true;
							mClick = false;
							mIsLand = true;
						}
					} else {
						if (!mIsLand) {
							// Log.d("1221", "自动切换横屏");
							// Toast.makeText(PlayVideoActivity.this,
							// "切换横屏"+rotation, Toast.LENGTH_SHORT).show();
							auto_change = true;
							mOrientationListener.disable();
							Message message = handler.obtainMessage();
							message.what = 77;
							handler.sendMessageDelayed(message, 1000);
							// handler.sendEmptyMessageDelayed(77, 1000);
							fullscreen.performClick();
							// mIsLand = true;
							// mClick = false;
						}
					}
				}

			}
		};
		mOrientationListener.enable();
	}

	private void checkFullscreen() {
		try {
			if (!player.isFullScreen()) {
				// OnChange();
				// Log.d("1221", "进入全屏检查重点");
				// fullscreen = (ImageButton)
				// player.findViewById(R.id.fullscreen);
				fullscreen.performClick();
			} else {
				// mOrientationListener.disable();
				// handler.sendEmptyMessageDelayed(77, 1000);
				// Log.d("1221", "已为横屏");
				if (!auto_change) {
					mIsLand = true;
					mClickLand = false;
				} else {
					auto_change = false;
					mIsLand = true;
					mClick = false;
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	private void checkQuitFullscreen() {
		try {
			if (player.isFullScreen()) {
				// OnChange();
				// Log.d("1221", "退出全屏检查重点");
				// fullscreen = (ImageButton)
				// player.findViewById(R.id.fullscreen);
				fullscreen.performClick();
			} else {
				// Log.d("1221", "已为竖屏");
				// mOrientationListener.disable();
				// handler.sendEmptyMessageDelayed(77, 1000);
				if (!auto_change) {
					mIsLand = false;
					mClickPort = false;
				} else {
					auto_change = false;
					mIsLand = false;
					mClick = false;
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public VideoRootFrame getPlayer() {
		return player;
	}

	private void removeTimerTask(TimerTask task) {
		if (task != null) {
			try {
				task.cancel();
				task = null;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	private void removeTimer(Timer t) {
		if (t != null) {
			try {
				t.cancel();
				t.purge();
				t = null;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	@Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slider_left_in,
                R.anim.anim_slider_right_out);
    }

}
