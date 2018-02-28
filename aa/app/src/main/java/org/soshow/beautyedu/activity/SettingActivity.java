package org.soshow.beautyedu.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.user.ChangeCodeActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.bean.UpDateInfo;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.NetUtil;
import org.soshow.beautyedu.updatedownload.entity.LoadInfo;
import org.soshow.beautyedu.updatedownload.service.Downloader;
import org.soshow.beautyedu.utils.CommonDialog;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.DialogOnClickListener;
import org.soshow.beautyedu.utils.Encryption;
import org.soshow.beautyedu.utils.FileSizeUtil;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.MD5;
import org.soshow.beautyedu.utils.MyAlertDialog;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TokenManager;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends FragmentActivity {

    private SettingActivity setting;
    private TextView title_name;
    private ProgressDialog progressDialog;
    private int tag = 0;
    private HashMap<String, Object> hashMap;
    private NotificationManager manager;
    private NotificationCompat.Builder builder2;
    private MyApplication app;
    // 两次返回键退出标志位
    private boolean isExit = false;
    private StringBuilder sb;
    private String newVContent;
    private ProgressBar pb; // 下载进度
    private Map<String, Downloader> downloaders = new HashMap<String, Downloader>();
    private static String urlstr;
    public static int loading_process = 0;
    private UpDateInfo udi;
    private boolean downloading = false;
    private boolean version_checking = false;
    private TextView tv; // 文本框"下载进度"
    private Dialog dialogdown;
    private String file_path_update;
    private String SD_PATH = "/mnt/sdcard/";

    // 保存登录信息
    private SharedPreferences sharedPreferences;
    private Editor editor;
    private String mToken;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (progressDialog != null) {
                try {
                    progressDialog.dismiss();
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
            if (msg.what == 0) {
                System.out.println(hashMap);
                Intent intent = new Intent();
                intent.putExtra("tag", tag);
                intent.putExtra("hashmap", hashMap);
                Toast.makeText(SettingActivity.this, hashMap.toString(),
                        Toast.LENGTH_SHORT).show();
                // intent.setClass(SettingActivity.this, MainActivity.class);
                // startActivity(intent);
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_setting);
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.titlebar_logininput);
        setting = this;
        title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText("设置");
        sharedPreferences = this.getSharedPreferences("huizhongjia",
                Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        init();
    }

    /*
     * LeftTitle 的返回键监听
     */
    public void onClick(View v) {
        // intent = new Intent(SearchActivity.this, MainActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        // startActivity(intent);
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

    private void init() {
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder2 = new NotificationCompat.Builder(this);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.logo_new);
        // builder2.setContentTitle("升级提示").setContentText("下载进度").setLargeIcon(icon);
        builder2.setContentTitle("升级提示").setContentText("下载进度")
                .setSmallIcon(R.drawable.logo_new);
        // Toast.makeText(SettingActivity.this, "Main创建",
        // Toast.LENGTH_SHORT).show();
        app = (MyApplication) getApplication();
        mHandler.sendEmptyMessageDelayed(8, 1500);

    }

    /*
     * 点击事件处理
     */
    public void clickHandler(View v) {
        Intent intent;

        switch (v.getId()) {
            // 设置部分
            case R.id.suggestion:
                intent = new Intent(SettingActivity.this, FeedbackActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slider_right_in,
                        R.anim.anim_slider_left_out);
                break;
            case R.id.modify_password:
                intent = new Intent(SettingActivity.this, ChangeCodeActivity.class);
//                intent.putExtra("account",
//                        Encryption.desEncryptd(Constant.code_local, Constant.code_local, sharedPreferences.getString("user_name", null)));
                intent.putExtra("account",
                        sharedPreferences.getString("user_name", null));
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slider_right_in,
                        R.anim.anim_slider_left_out);
                break;
            case R.id.info_version:
                if (NetUtil.isConnected(SettingActivity.this)) {
                    if (!downloading && !version_checking) {
                        getUpdateInfoNet();
                    } else if (downloading) {
                        Toast.makeText(SettingActivity.this, "软件更新下载中",
                                Toast.LENGTH_SHORT).show();
                    }
                    else if (version_checking) {
                        Toast.makeText(SettingActivity.this, "正在获取更新信息",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SettingActivity.this, "您当前无网络",
                            Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.clear_cache:
                final CommonDialog commonDialog = new CommonDialog(SettingActivity.this, "确定清除缓存？", "清除", "取消");
                commonDialog.setListener(new DialogOnClickListener() {
                    @Override
                    public void sureOnClick() {
                        clearCacheFolder(getCacheDir());
                        FragmentSet.cache_size.setText(FileSizeUtil
                                .getFileOrFilesSize(getCacheDir()
                                        .toString(), 3)
                                + "M");
                        Toast.makeText(SettingActivity.this,
                                "缓存已清除", Toast.LENGTH_SHORT).show();
                        commonDialog.dismiss();
                    }

                    @Override
                    public void cancleOnClick() {
                        commonDialog.dismiss();
                    }
                });
                commonDialog.show();
                break;
            case R.id.guanyu:
                intent = new Intent(SettingActivity.this, HuizhongjiaActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slider_right_in,
                        R.anim.anim_slider_left_out);
                break;
            case R.id.allow_net_layout:
                if (!NetUtil.isConnected(SettingActivity.this)) {
                    Toast.makeText(SettingActivity.this, "请先打开您的网络",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.logout_set:
                if (MyApplication.logined == true) {

                    final CommonDialog logoutdialog = new CommonDialog(SettingActivity.this, "退出当前账号?", "退出", "取消");
                    logoutdialog.setListener(new DialogOnClickListener() {
                        @Override
                        public void sureOnClick() {
                            LoginUtil.logout(SettingActivity.this);
                            finish();
                            overridePendingTransition(R.anim.anim_slider_left_in,
                                    R.anim.anim_slider_right_out);
                            logoutdialog.dismiss();
                        }

                        @Override
                        public void cancleOnClick() {
                            logoutdialog.dismiss();
                        }
                    });
                    logoutdialog.show();
                }
                break;

            default:
                break;
        }
    }

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
                case 2://有选择性的版本更新
                    newVContent = sb.toString();
                    Builder bd = new MyAlertDialog.Builder(SettingActivity.this);
                    bd.setTitle("升级提示")
                            .setMessage(newVContent)
                            .setPositiveButton("立即升级",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {
                                            Beginning();
                                            arg0.dismiss();
                                        }
                                    })
                            .setNegativeButton("暂不升级",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // 需要做的事:发送消息
                            Log.d("345abc", "首页循环等待");
                            Message msg_loop = mHandler.obtainMessage();
                            msg_loop.what = 66;
                            msg_loop.sendToTarget();
                        }
                    }, 500);
                    break;
                // 进度条进度刷新
                case 3:
                    pb.incrementProgressBy(msg.arg1);
                    LoadInfo info1 = downloaders.get(urlstr).getDownloaderInfors();
                    loading_process = info1.getComplete() * 100
                            / info1.getFileSize();
                    // Log.d("345abc", Integer.toString(loading_process));
                    // loading_process=info1.getFileSize();
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
                    Intent mintent = new Intent(Intent.ACTION_VIEW);
                    mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mintent.setDataAndType(
                            Uri.fromFile(new File(SD_PATH + "huizhongjia" + udi.version + ".apk")),
                            "application/vnd.android.package-archive");
                    break;
                // 下载错误
                case 0x0005:
                    downloading = false;
                    String error = msg.getData().getString("error");
                    Toast.makeText(SettingActivity.this, error, Toast.LENGTH_SHORT)
                            .show();
                    break;
                // 强制升级
                case 6:
                    // Html.fromHtml("欢迎大家收看<font
                    // color=blue>《Android开发从零开始》</font>系列课程。感谢大家的支持。")
                    // sb.append("\n您当前软件版本过低，请升级后再使用!");

                    sb.append("\n您当前软件版本过低，请升级后再使用!");
                    // sb.append(Html.fromHtml("a<font
                    // color=blue>您当前软件版本过低，请升级后再使用!</font>a"));
                    newVContent = sb.toString();
                    Builder bd6 = new MyAlertDialog.Builder(SettingActivity.this);
                    bd6.setTitle("升级提示")
                            .setMessage(newVContent)
                            .setPositiveButton("立即升级",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface arg0,
                                                            int arg1) {
                                            // TODO Auto-generated method stub
                                            Beginning();
                                            arg0.dismiss();
                                        }
                                    })
                            .setNegativeButton("暂不升级",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int whichButton) {
                                            dialog.dismiss();
                                            app.setUpdateinfo(null);
                                            finish();

                                        }
                                    });
                    final Dialog dialog6 = bd6.show();
                    dialog6.setCancelable(false);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // 需要做的事:发送消息
                            Log.d("345abc", "首页循环等待");
                            Message msg_loop = mHandler.obtainMessage();
                            msg_loop.what = 66;
                            msg_loop.sendToTarget();
                        }
                    }, 500);
                /*
				 * dialog6.setOnCancelListener(new
				 * DialogInterface.OnCancelListener() {
				 * 
				 * @Override public void onCancel(DialogInterface arg0) { //
				 * TODO Auto-generated method stub dialog6.show();
				 * 
				 * } });
				 */
                    break;
			/*
			 * case 7: JSONObject response = (JSONObject) msg.obj; if
			 * (response.has("openid")) { try { openid_qq =
			 * response.getString("openid"); //
			 * Toast.makeText(getApplicationContext(), openid_qq, //
			 * Toast.LENGTH_LONG).show(); } catch (JSONException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } } break;
			 */

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
                case 88:
                    if (TokenManager.handlered) {
                        Log.d("345abc", "主页获取新token");
                        TokenManager.handlered = false;
                        mToken = sharedPreferences.getString("mToken", null);
                        if (mToken != null) {
                            // getInfo();
                            getUpdateInfoNet();
                        } else {
                            version_checking = false;
                            Toast.makeText(SettingActivity.this, "网络不佳，请稍后再试",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                // 需要做的事:发送消息
                                Log.d("345abc", "首页循环等待");
                                Message msg_loop = handler.obtainMessage();
                                msg_loop.what = 88;
                                msg_loop.sendToTarget();
                            }
                        }, 1000);

                    }
                    break;
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
                    Toast.makeText(SettingActivity.this, "下载出错,请重新下载",
                            Toast.LENGTH_SHORT).show();
                    if (dialogdown != null) {
                        dialogdown.dismiss();
                    }
                    // if(udi.)
//                    if (udi.result.equals("2")) {
//                        app.setUpdateinfo(null);
//                        finish();
//                        overridePendingTransition(R.anim.anim_slider_left_in,
//                                R.anim.anim_slider_right_out);
//                    }
                    break;
                case 66:
                    version_checking = false;
                    break;
                default:
                    break;

            }

        }

    };

    // 删除缓存
    private int clearCacheFolder(File dir) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child);
                    }

                    child.delete();
                    // Log.d("123456", "删除的文件为" + child.getName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    /*
     * 检查更新及下载
     */
    public void Beginning() {
        LinearLayout ll = (LinearLayout) LayoutInflater.from(
                SettingActivity.this).inflate(R.layout.layout_loadapk, null);
        pb = (ProgressBar) ll.findViewById(R.id.down_pb);
        // pb.setMax(Integer.parseInt(udi.fileSize));
        // pb.setMax(623457);
        tv = (TextView) ll.findViewById(R.id.tv);
//        if (udi.result.equals("1")) {
            Builder bd = new MyAlertDialog.Builder(SettingActivity.this);
            bd.setTitle("版本更新进度提示")
                    .setView(ll)
                    .setNegativeButton("后台下载",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
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
                            Message message = mHandler.obtainMessage();
                            message.what = 9;
                            message.sendToTarget();

                        }
                    });
//        } else {
//            //强制更新
//            Builder bd = new MyAlertDialog.Builder(SettingActivity.this);
//            bd.setTitle("版本更新进度提示").setView(ll);
//            dialogdown = bd.show();
//            dialogdown.setCancelable(false);
//        }
        startDownload();

    }

    /*
     * 断点续传下载
     */
    public void startDownload() {
        // 得到textView的内容
        file_path_update = SD_PATH + "huizhongjia" + udi.version + ".apk";
        String check_md5 = MD5.md5sum(file_path_update);
        if (check_md5 != null && check_md5.toLowerCase().equals(udi.md5)) {
            // Log.d("123456", MD5.md5sum(file_path_update));
            dialogdown.dismiss();
            downloading = false;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(
                    Uri.fromFile(new File(SD_PATH + "huizhongjia"
                            + udi.version + ".apk")),
                    "application/vnd.android.package-archive");
            startActivity(intent);
            // finish();
            // System.exit(0);
//            if (udi.result.equals("2")) {
//                finish();
//            }
            // 设置下载线程数为4，这里是我为了方便随便固定的

        } else {
            downloading = true;
            loading_process = 0;
            String threadcount = "1";
            urlstr = udi.url;
            // urlstr = url_d;
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(urlstr, file_path_update, threadcount);
        }
    }

    ;

    // 设置部分方法
    private void getUpdateInfo() {

        // if (udi != null && !udi.equals("")) {
        // sb = new StringBuilder();
        // sb.append("检查到可升级版本：").append(udi.versionName).append("\n").append("大小：").append(udi.fileSize).append("M\n")
        // .append("本次更新内容：\n").append(udi.updateLog);
        // if (udi.result.equals("2"))
        // mHandler.sendEmptyMessage(6);
        // else
        // mHandler.sendEmptyMessage(2);
        // } else {

        mToken = new TokenManager(SettingActivity.this).getToken();
        if (mToken != null) {
            getUpdateInfoNet();
        } else {
            TokenManager.handlered = false;
            Message msg_loop = mHandler.obtainMessage();
            msg_loop.what = 88;
            msg_loop.sendToTarget();
        }
        // }

    }

    /*
     * 版本更新
     */
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

                                final CommonDialog commonDialog = new CommonDialog(SettingActivity.this, sb.toString(), "立即升级", "暂不升级");
                                commonDialog.setListener(new DialogOnClickListener() {
                                    @Override
                                    public void sureOnClick() {
                                        Beginning();
                                        commonDialog.dismiss();
                                    }

                                    @Override
                                    public void cancleOnClick() {
                                        commonDialog.dismiss();
                                    }
                                });
                                commonDialog.show();
                            }else{
                                Toast.makeText(SettingActivity.this, "已是最新版本",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        version_checking = false;
                        Toast.makeText(SettingActivity.this, "获取更新信息失败",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    version_checking = false;
                    Toast.makeText(SettingActivity.this, "获取更新信息失败",
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
                Toast.makeText(SettingActivity.this, "获取更新信息失败",
                        Toast.LENGTH_SHORT).show();
                Log.d("345abc", "获取更新信息失败");
            }

        });

    }
    private Integer getVersionCode() {
        PackageManager pm = SettingActivity.this.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(SettingActivity.this.getPackageName(), 0);
            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
                        SettingActivity.this, mHandler);
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slider_left_in,
                R.anim.anim_slider_right_out);
    }


}
