package org.soshow.beautyedu.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;
import org.soshow.beautyedu.FindItemDetailActivity;

import org.soshow.beautyedu.KuaixunActivity;
import org.soshow.beautyedu.activity.user.PersonInfoActivity;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.ShoppingCarActivity;
import org.soshow.beautyedu.TuwenActivity;

import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.bean.UpDateInfo;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;

import org.soshow.beautyedu.updatedownload.entity.LoadInfo;
import org.soshow.beautyedu.updatedownload.service.Downloader;
import org.soshow.beautyedu.utils.CommonDialog;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.DialogOnClickListener;

import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.MD5;
import org.soshow.beautyedu.utils.MyAlertDialog;


import android.R.color;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chatuidemo.DemoHelper;

/**
 * 首页
 */
public class MainTabActivity extends FragmentActivity {
    public static final int FIND_CORD = 42;
    public static final int CHAT_CORD = 43;
    public static int index = 0;
    // 定义FragmentTabHost对象
    public static FragmentTabHost mTabHost;

    // 定义一个布局
    private LayoutInflater layoutInflater;

    // 定义数组来存放Fragment界面
    private Class fragmentArray[] = {
            FragmentMain.class,
            FragmentFind.class,
//            FragmentChat.class,
//			FragmentCart.class,
            FragmentPer.class
    };

    // 定义数组来存放按钮图片
    private int mImageViewArray[] = {R.drawable.tab_home_btn,
            R.drawable.tab_set_btn,
            R.drawable.tab_search_btn,
//			R.drawable.tab_cart_btn,
            R.drawable.tab_my_btn
    };

    // Tab选项卡的文字
    private String mTextviewArray[] = {"首页", "念力",   "我的"};

    /*
     * 检查更新部分变量
     */
    private String newVContent;
    private ProgressBar pb; // 下载进度
    private TextView tv; // 文本框"下载进度"
    public static int loading_process = 0;
    private MyApplication app;
    private UpDateInfo udi;
    private StringBuilder sb;
    private String file_path_update;
    // 两次返回键退出标志位
    private boolean isExit = false;

    /*
     * 下载部分 (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    private String SD_PATH = "/mnt/sdcard/";
    private Map<String, Downloader> downloaders = new HashMap<String, Downloader>();
    // private int tempSize;
    private static String urlstr;
    private Dialog dialogdown;

    // 保存登录信息
    private SharedPreferences sharedPreferences;
    private Editor editor;

    private NotificationCompat.Builder builder2;

    // 标题
    public static TextView title_main;
    // 请求码
    public static int REQUEST_CODE = 1;
    // 静态实例
    private NotificationManager manager;
    public static MainTabActivity mta_instance;
    private boolean downloading = false;
    private boolean version_checking = false;
    public static RelativeLayout titlebar_main;
    BroadcastReceiver receiver;
    private LocalBroadcastManager broadcastManager;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main_tab);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.titlebar_main);
        getUpdateInfoNet();
        mta_instance = this;
        sharedPreferences = this.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        initView();
        broadcastManager = LocalBroadcastManager.getInstance(MainTabActivity.this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(com.easemob.chatuidemo.Constant.ACCOUNT_CONFLICT);
         receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if(intent.getAction().equals(com.easemob.chatuidemo.Constant.ACCOUNT_CONFLICT)){
                    showConflictDialog();
                }
            }
        };
        broadcastManager.registerReceiver(receiver,filter);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder2 = new NotificationCompat.Builder(this);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_new);
        // builder2.setContentTitle("升级提示").setContentText("下载进度").setLargeIcon(icon);
        builder2.setContentTitle("升级提示").setContentText("下载进度")
                .setSmallIcon(R.drawable.logo_new);
        app = (MyApplication) getApplication();
        mHandler.sendEmptyMessageDelayed(8, 1500);

    }

    /**
     * 初始化组件
     */
    private void initView() {
        title_main = (TextView) findViewById(R.id.title_main);
        titlebar_main = (RelativeLayout) title_main.getParent();
        layoutInflater = LayoutInflater.from(this);
        // 实例化TabHost对象，得到TabHost
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        // 得到fragment的个数
        int count = fragmentArray.length;

        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            // 设置Tab按钮的背景
            mTabHost.getTabWidget().getChildAt(i)
                    .setBackgroundResource(color.white);
        }

        mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals(mTextviewArray[0])) {
                    index = 0;
                }
                if (FragmentChat.ivAdd != null) {
                    FragmentChat.ivAdd.setVisibility(View.INVISIBLE);
                }
                if (tabId.equals(mTextviewArray[1])) {
                    if (FragmentFind.tvPublish != null) {
                        FragmentFind.tvPublish.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (FragmentFind.tvPublish != null) {
                        FragmentFind.tvPublish.setVisibility(View.GONE);
                    }
                }
                // 聊天
                if (tabId.equals(mTextviewArray[2])) {
                    Intent intent;
                    Log.e("MyApplication.logined=" + MyApplication.logined,
                            "DemoHXSDKHelper.getInstance().isLogined()==="
                                    + DemoHelper.getInstance().isLoggedIn());
                    if (MyApplication.getInstance().logined == false
                            || !DemoHelper.getInstance().isLoggedIn()) {
                        intent = new Intent(MainTabActivity.this,
                                LoginInputActivity.class);
                        intent.putExtra("key", CHAT_CORD);
                        startActivityForResult(intent, CHAT_CORD);
                    }
                }

            }
        });

    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.tab_item_view, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setImageResource(mImageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.textview);
        textView.setText(mTextviewArray[index]);

//		if (index == 2) {
//			tvUnReadNum = (TextView) view.findViewById(R.id.fragment_number);
//			FragmentChat.updateUnreadLabel();
//			int count = FragmentChat.getUnreadMsgCountTotal();
//			Log.e("VISIBLE", "index===============" + index);
//			if (count > 0) {
//				tvUnReadNum.setVisibility(View.VISIBLE);
//				tvUnReadNum.setText("" + count);
//			}
//		}
        return view;
    }

    private Integer getVersionCode() {
        PackageManager pm = MainTabActivity.this.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(MainTabActivity.this.getPackageName(), 0);
            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void clickIntent(String tag) {
        Intent intent;
//        if (MyApplication.logined == false) {
//            intent = new Intent(MainTabActivity.this,
//                    LoginInputActivity.class);
//            intent.putExtra("key", CHAT_CORD);
//            startActivityForResult(intent, CHAT_CORD);
//        } else {
            if (tag.equals("changshi")) {
                intent = new Intent(MainTabActivity.this, TuwenActivity.class);
                intent.putExtra("tag", "changshi");
                startActivity(intent);
            } else if (tag.equals("fenxi")) {
                intent = new Intent(MainTabActivity.this, TuwenActivity.class);
                intent.putExtra("tag", "fenxi");
                startActivity(intent);
            } else if (tag.equals("kuaixun")) {
                intent = new Intent(MainTabActivity.this, KuaixunActivity.class);
                startActivity(intent);
            } else if (tag.equals("shipin")) {
                intent = new Intent(MainTabActivity.this, TuwenActivity.class);
                intent.putExtra("tag", "shipin");
                startActivity(intent);
            }
//        }

    }

    /*
     * 点击事件处理
     */
    public void clickHandler(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.video_entrance:// 常识
                clickIntent("changshi");
                break;
            case R.id.course_entrance:// 分析
                clickIntent("fenxi");
                break;
            case R.id.off_line_entrance:// 快讯
                clickIntent("kuaixun");
                break;
            case R.id.tiku_entrance:// 视频
                clickIntent("shipin");
//			} else {
//				Toast.makeText(MainTabActivity.this, "请打开您的网络",
//						Toast.LENGTH_SHORT).show();
//			}
                break;
            case R.id.mjl:
                // if (NetUtil.isConnected(MainTabActivity.this)) {
                if (MyApplication.logined == false) {
                    intent = new Intent(MainTabActivity.this,
                            LoginInputActivity.class);
                    intent.putExtra("key", 1);
                    Constant.next_page = new Intent(MainTabActivity.this,
                            RecordActivity.class);
                } else {
                    intent = new Intent(MainTabActivity.this, RecordActivity.class);
                }
                startActivity(intent);
                // } else {
                // Toast.makeText(MainTabActivity.this, "请打开您的网络",
                // Toast.LENGTH_SHORT).show();

                // }

                break;

            case R.id.mlx:
                // if (NetUtil.isConnected(MainTabActivity.this)) {
                if (MyApplication.logined == false) {
                    intent = new Intent(MainTabActivity.this,
                            LoginInputActivity.class);
                    intent.putExtra("key", 3);
                    Constant.next_page = new Intent(MainTabActivity.this,
                            PracticeActivity.class);
                } else {
                    intent = new Intent(MainTabActivity.this,
                            PracticeActivity.class);
                }
                startActivity(intent);
                // } else {
                // Toast.makeText(MainTabActivity.this, "请打开您的网络",
                // Toast.LENGTH_SHORT).show();
                // ;
                // }
                break;
            case R.id.mct:
                // if (NetUtil.isConnected(MainTabActivity.this)) {
                if (MyApplication.logined == false) {
                    intent = new Intent(MainTabActivity.this,
                            LoginInputActivity.class);
                    intent.putExtra("key", 4);
                    Constant.next_page = new Intent(MainTabActivity.this,
                            ErrorActivity.class);
                } else {
                    intent = new Intent(MainTabActivity.this, ErrorActivity.class);
                }
                startActivity(intent);
                // } else {
                // Toast.makeText(MainTabActivity.this, "请打开您的网络",
                // Toast.LENGTH_SHORT).show();

                // }

                break;

//		 case R.id.setting:
            case R.id.rl_setting:
                // if (NetUtil.isConnected(MainTabActivity.this)) {
                if (MyApplication.logined == false) {
                    intent = new Intent(MainTabActivity.this,
                            LoginInputActivity.class);
                    intent.putExtra("key", 4);
                    Constant.next_page = new Intent(MainTabActivity.this,
                            SettingActivity.class);
                } else {
                    intent = new Intent(MainTabActivity.this, SettingActivity.class);
                }
                startActivityForResult(intent, REQUEST_CODE);
                // } else {
                // Toast.makeText(MainTabActivity.this, "请打开您的网络",
                // Toast.LENGTH_SHORT).show();

                // }
                break;
            // 登录
            case R.id.myaccount:
                Constant.next_page = null;
                if (MyApplication.logined == false) {
                    // if (true) {
                    intent = new Intent(MainTabActivity.this,
                            LoginInputActivity.class);
                    intent.putExtra("key", 5);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
//				Intent intent2 = new Intent(MainTabActivity.this,
//						InfoEditActivity.class);
//				startActivity(intent2);
                }
                break;

            case R.id.rl_my:// 个人信息
                if (MyApplication.logined == false) {
                    intent = new Intent(MainTabActivity.this,
                            LoginInputActivity.class);
                    intent.putExtra("key", 31);
                    Constant.next_page = new Intent(MainTabActivity.this,
                            PersonInfoActivity.class);
                } else {
                    intent = new Intent(MainTabActivity.this,
                            PersonInfoActivity.class);
                }
                startActivity(intent);

                break;
            case R.id.news_list_rl:// 美业头条
                Intent intent2 = new Intent(MainTabActivity.this,
                        NewsListActivity.class);
                startActivity(intent2);
                break;

            case R.id.rl_work://我的作品
                if (MyApplication.logined == false) {
                    intent = new Intent(MainTabActivity.this,
                            LoginInputActivity.class);
                    intent.putExtra("key", 42);
                    Constant.next_page = new Intent(MainTabActivity.this,
                            FindItemDetailActivity.class);
                } else {
                    intent = new Intent(MainTabActivity.this,
                            FindItemDetailActivity.class);
                    intent.putExtra("other_user_id", sharedPreferences.getString("user_id",""));
                }
                startActivity(intent);
                break;
            case R.id.rl_my_car://我的购物车
                if (MyApplication.logined == false) {
                    intent = new Intent(MainTabActivity.this,
                            LoginInputActivity.class);
                    intent.putExtra("key", 31);
                    Constant.next_page = new Intent(MainTabActivity.this,
                            ShoppingCarActivity.class);
                } else {
                    intent = new Intent(MainTabActivity.this,
                            ShoppingCarActivity.class);
                }
                startActivity(intent);


                break;

            default:
                break;
        }

        overridePendingTransition(R.anim.anim_slider_right_in,
                R.anim.anim_slider_left_out);
    }

    /*
     * 打开视频列表页
     */
    @SuppressWarnings("unchecked")
    /*
	 * 消息处理
	 */
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    isExit = false;
                    break;
                case 1:
                    // mRefreshableView.finishRefresh();
                    // Toast.makeText(getApplicationContext(), "网络不给力，请检查网络",
                    // Toast.LENGTH_LONG).show();
                    break;
                // 进度条进度刷新
                case 3:
                    pb.incrementProgressBy(msg.arg1);
                    LoadInfo info1 = downloaders.get(urlstr).getDownloaderInfors();
                    loading_process = info1.getComplete() * 100 / info1.getFileSize();
                    tv.setText("已为您下载了：" + loading_process + "%");
                    if (pb.getProgress() == pb.getMax()) {
                        downloading = false;
                        dialogdown.dismiss();
                        String url = (String) msg.obj;
                        downloaders.get(url).delete(url);
                        downloaders.get(url).reset();
                        downloaders.remove(url);
                        // loading_process = 0;
                        // Log.d("345abc", "下载的MD5=" +
                        // MD5.md5sum(file_path_update));
//						if (MD5.md5sum(file_path_update).toLowerCase()
//								.equalsIgnoreCase(udi.md5)) {
//							// if(true){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setDataAndType(
                                Uri.fromFile(new File(SD_PATH + "huizhongjia"
                                        + udi.version + ".apk")),
                                "application/vnd.android.package-archive");
                        startActivity(intent);
//							if (udi.result.equals("2")) {
//								finish();
//							}
//						} else {
//							Toast.makeText(MainTabActivity.this, "文件下载出错，请重新下载",
//									Toast.LENGTH_SHORT).show();
//						}
                    }
                    break;
                // 下载完成安装
                case 4:
                    downloading = false;
                    String url = (String) msg.obj;
                    downloaders.remove(url);
                    // loading_process = 0;
//					if (MD5.md5sum(file_path_update).toLowerCase().equals(udi.md5)) {
                    // if(true){
                    // Toast.makeText(getApplicationContext(),
                    // MD5.md5sum(file_path_update), Toast.LENGTH_SHORT).show();
                    Intent mintent = new Intent(Intent.ACTION_VIEW);
                    mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mintent.setDataAndType(
                            Uri.fromFile(new File(SD_PATH + "huizhongjia" + udi.version + ".apk")),
                            "application/vnd.android.package-archive");
                    // startActivity(intent);
//						if (udi.result.equals("2")) {
//							finish();
//						}
//					} else {
//						Toast.makeText(MainTabActivity.this, "文件下载出错，请重新下载",
//								Toast.LENGTH_SHORT).show();
//					}
                    break;
                // 下载错误
                case 0x0005:
                    downloading = false;
                    String error = msg.getData().getString("error");
                    Toast.makeText(MainTabActivity.this, error, Toast.LENGTH_SHORT)
                            .show();
                    break;

                case 9:// 下载进度通知栏
                    try {
                        if (loading_process < 100) {
                            builder2.setProgress(100, loading_process, false);
                            builder2.setContentText("下载进度" + loading_process + "%");
                            manager.notify(0, builder2.build());
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // 需要做的事:发送消息
                                    // LoginUtil.login_handlered = false;
                                    Log.d("345abc", "循环等待-changeCode");
                                    Message msg_loop = mHandler.obtainMessage();
                                    msg_loop.what = 9;
                                    msg_loop.sendToTarget();
                                }
                            }, 1000);
                        } else {
                            manager.cancel(0);
                        }
                    } catch (Exception de) {
                        Log.d("345abc", "进度条异常" + de.toString());
                    }

                    break;
//				case 88:
//					if (TokenManager.handlered) {
//						Log.d("345abc", "主页获取新token");
//						TokenManager.handlered = false;
//						mToken = sharedPreferences.getString("mToken", null);
//						if (mToken != null) {
//							// getInfo();
//							getUpdateInfoNet();
//						} else {
//							version_checking = false;
//							Toast.makeText(MainTabActivity.this, "网络不佳，请稍后再试",
//									Toast.LENGTH_SHORT).show();
//						}
//
//					} else {
//						new Timer().schedule(new TimerTask() {
//							@Override
//							public void run() {
//								// 需要做的事:发送消息
//								Log.d("345abc", "首页循环等待");
//								Message msg_loop = handler.obtainMessage();
//								msg_loop.what = 88;
//								msg_loop.sendToTarget();
//							}
//						}, 1000);
//
//					}
//					break;
                case 77:// 下载出错
                    try {
                        manager.cancel(0);
                    } catch (Exception ed) {
                    }
                    downloading = false;
                    String url_a = (String) msg.obj;
                    downloaders.get(url_a).delete(url_a);
                    downloaders.get(url_a).reset();
                    downloaders.remove(url_a);
                    Toast.makeText(MainTabActivity.this, "下载出错,请重新下载",
                            Toast.LENGTH_SHORT).show();
                    if (dialogdown != null) {
                        dialogdown.dismiss();
                    }
                    // if(udi.)
//					if (udi.result.equals("2")) {
//						app.setUpdateinfo(null);
//						finish();
//					}
                    break;
                case 101:// 重新获取catid，打开列表页
                    // Toast.makeText(MainTabActivity.this, "获取cat_id回调",
                    // Toast.LENGTH_SHORT).show();
                    Log.d("123321", "catID回调" + MyApplication.catID);
                    // Log.d("123321", MyApplication.catID);
                    Intent intent = new Intent(MainTabActivity.this,
                            VideoListActivity.class);
                    startActivity(intent);
                    break;
                case 66:
                    version_checking = false;
                    break;
                default:
                    break;

            }

        }

    };
    private boolean isRecord;
    private long waitTime;

    private static TextView tvUnReadNum;

    public Handler getCatHandler() {
        return mHandler;
    }

    /*
     * 双击退出部分 (non-Javadoc)
     *
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    @Override
    public void onBackPressed() {
        if (isRecord) {
            long temp = System.currentTimeMillis();
            if (temp - waitTime <= 3000) {
                finish();
            } else {
                waitTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出应用 ", Toast.LENGTH_SHORT).show();
            }

        } else {
            waitTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出应用 ", Toast.LENGTH_SHORT).show();
            isRecord = true;
        }

    }

    /*
     * 检查更新及下载
     */
    public void Beginning() {
        LinearLayout ll = (LinearLayout) LayoutInflater.from(
                MainTabActivity.this).inflate(R.layout.layout_loadapk, null);
        pb = (ProgressBar) ll.findViewById(R.id.down_pb);
        // pb.setMax(Integer.parseInt(udi.fileSize));
        // pb.setMax(623457);
        tv = (TextView) ll.findViewById(R.id.tv);
//		if (udi.result.equals("1")) {
        Builder bd = new MyAlertDialog.Builder(MainTabActivity.this);
        bd.setTitle("版本更新进度提示")
                .setView(ll)
                .setNegativeButton("后台下载",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // Intent intent = new
                                // Intent(MainTabActivity.this,
                                // VersionService.class);
                                // startService(intent);
                                Message message = mHandler.obtainMessage();
                                message.what = 9;
                                message.sendToTarget();
                                dialogdown.dismiss();

                            }
                        });
        dialogdown = bd.show();
        dialogdown
                .setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface arg0) {
                        // TODO Auto-generated method stub
                        // Intent intent = new Intent(MainTabActivity.this,
                        // VersionService.class);
                        // startService(intent);
                        Message message = mHandler.obtainMessage();
                        message.what = 9;
                        message.sendToTarget();

                    }
                });
//		}

//		else {
//			Builder bd = new MyAlertDialog.Builder(MainTabActivity.this);
//			bd.setTitle("版本更新进度提示").setView(ll);
//			dialogdown = bd.show();
//			dialogdown.setCancelable(false);
//		}
        startDownload();

    }

    /*
     * 断点续传下载
     */
    public void startDownload() {
        // 得到textView的内容

        // String urlstr = udi.downloadUrl;
        // String localfile = SD_PATH + "huizhongjia"+udi.versionName+".apk";
        // urlstr =
        // "http://nj02.poms.baidupcs.com/file/6d2eb3aceb4b1c43ad5cfd365637f914?bkt=p2-nj02-359&fid=912570220-250528-290071215496407&time=1438236737&sign=FDTAXGERLBH-DCb740ccc5511e5e8fedcff06b081203-cFYjSscZ5iXBlp5OyiCz8cDh5IY%3D&to=n2b&fm=Nan,B,T,ny&sta_dx=1&sta_cs=0&sta_ft=apk&sta_ct=0&fm2=Nanjing02,B,T,ny&newver=1&newfm=1&secfm=1&flow_ver=3&pkey=14006d2eb3aceb4b1c43ad5cfd365637f914307ef6090000000c104b&sl=82313295&expires=8h&rt=pr&r=506498707&mlogid=3013873894&vuk=912570220&vbdid=1451657031&fin=huizhongjia2.apk&fn=huizhongjia2.apk&slt=pm&uta=0&rtype=1&iv=0&isw=0";

        file_path_update = SD_PATH + "huizhongjia" + udi.version + ".apk";
        // Log.d("123321", MD5.md5sum(file_path_update));
        // Log.d("123321", udi.md5);

        String check_md5 = MD5.md5sum(file_path_update);
        if (check_md5 != null && check_md5.toLowerCase().equals(udi.md5)) {
            // Log.d("123456", MD5.md5sum(file_path_update));
            dialogdown.dismiss();
            downloading = false;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(
                    Uri.fromFile(new File(SD_PATH + "huizhongjia" + udi.version + ".apk")),
                    "application/vnd.android.package-archive");
            startActivity(intent);
            // finish();
            // System.exit(0);
//			if (udi.result.equals("2")) {
//				finish();
//			}
            // 设置下载线程数为4，这里是我为了方便随便固定的

        } else {
            downloading = true;
            loading_process = 0;
            String threadcount = "1";
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(urlstr, file_path_update, threadcount);
        }
    }

    ;

    class DownloadTask extends AsyncTask<String, Integer, LoadInfo> {
        Downloader downloader = null;
        View v = null;
        String urlstr = null;

        public DownloadTask() {

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected LoadInfo doInBackground(String... params) {
            urlstr = params[0];

            String localfile = params[1];
            int threadcount = Integer.parseInt(params[2]);
            // 初始化一个downloader下载器
            downloader = downloaders.get(urlstr);
            if (downloader == null) {
                downloader = new Downloader(urlstr, localfile, threadcount,
                        MainTabActivity.this, mHandler);
                downloaders.put(urlstr, downloader);
            }
            if (downloader.isdownloading())
                return null;
            // 得到下载信息类的个数组成集合
            return downloader.getDownloaderInfors();
        }

        @Override
        protected void onPostExecute(LoadInfo loadInfo) {
            if (loadInfo != null) {
                // 显示进度条
                // sendMsg(0x0003, loadInfo.getComplete());
                pb.setMax(loadInfo.getFileSize());
                pb.setProgress(loadInfo.getComplete());
                // 调用方法开始下载
                downloader.download();
            }
        }
    }

    //获取版本更新信息
    private void getUpdateInfoNet() {
        String upUrl = Constant.phpUrl
                + "/wap/api.php?action=GET_ANDROID_VERSION";
        Log.e("345abc", "首页   升级信息地址为                    " + upUrl);
        NetHelper.get(upUrl, new SimpleSingleBeanNetHandler<Captcha>(
                this) {

            @Override
            protected void onSuccess(Captcha bean) {
                // 这里是ui线程，可进行ui操作
                // version_checking = false;
                Log.d("345abc", " 升级信息为" + bean.result);
                try {
                    if ("0".equals(bean.result)) {
                        JSONObject jsonObject = new JSONObject(bean.info);
                        udi = GsonUtils.parseJSON(jsonObject.toString(), UpDateInfo.class);
                        app.setUpdateinfo(udi);
                        if (udi != null && udi.version_code != null) {
                            if (getVersionCode() < udi.version_code) {
                                sb = new StringBuilder();
                                sb.append("检查到可升级版本：").append(udi.version).append("\n")
                                        .append("大小：").append(udi.size).append("M\n")
                                        .append("本次更新内容：\n").append(udi.detail);

                                final CommonDialog commonDialog = new CommonDialog(MainTabActivity.this, sb.toString(), "立即升级", "暂不升级");
                                commonDialog.setListener(new DialogOnClickListener() {
                                    @Override
                                    public void sureOnClick() {
                                        urlstr = udi.url;
                                        if(urlstr!=null){
                                            Beginning();
                                        }
                                        commonDialog.dismiss();
                                    }

                                    @Override
                                    public void cancleOnClick() {
                                        commonDialog.dismiss();
                                    }
                                });
                                commonDialog.show();
                            }
                        }
                    } else {
                        version_checking = false;
                        Toast.makeText(MainTabActivity.this, "获取更新信息失败",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    version_checking = false;
                    Toast.makeText(MainTabActivity.this, "获取更新信息失败",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected void onError(int errorCode, String errorMsg) {
                // TODO Auto-generated method stub
                // super.onError(errorCode, errorMsg);
                version_checking = false;
                try {
                    Log.d("1221", "升级错误信息" + errorMsg);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                Toast.makeText(MainTabActivity.this, "获取更新信息失败",
                        Toast.LENGTH_SHORT).show();
                Log.d("345abc", "获取更新信息失败");
            }

        });
    }
    private void showConflictDialog() {
        LoginUtil.logout(MainTabActivity.this);
        String st = getResources().getString(R.string.Logoff_notification);
        if (!isFinishing()) {
            showAlertDialog(st, getString(R.string.connect_conflict));

        }

    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    private void showAlertDialog(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainTabActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startActivity(new Intent(MainTabActivity.this, LoginInputActivity.class));
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.e("退出登入返回requestCode=" + requestCode + "\nresultCode=" + resultCode);
        if (requestCode == MainTabActivity.CHAT_CORD
                && resultCode == LoginInputActivity.RESULT_CODE) {
            MainTabActivity.mTabHost.setCurrentTab(0);

            if (FragmentPer.tvLogin != null) {
                FragmentPer.tvLogin.setText(R.string.dlzc);
                FragmentPer.tvLogin.setVisibility(View.VISIBLE);
            }
            if (FragmentPer.set_head_icon != null) {
                FragmentPer.set_head_icon.setImageResource(R.drawable.default_face);
            }
        }

    }

    @Override
    public void finish() {
        Log.d("123321", "取消定时前");
        try {
            manager.cancel(0);
        } catch (Exception e) {
            Log.d("345abc", "通知栏取消异常" + e.toString());
        }
        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.setTab1 == true) {// 设置网页点击个人,返回naive
            mTabHost.setCurrentTab(1);
            MyApplication.setTab1 = false;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(receiver);
    }
}