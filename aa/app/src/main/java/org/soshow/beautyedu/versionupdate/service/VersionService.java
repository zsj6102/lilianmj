package org.soshow.beautyedu.versionupdate.service;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.MainTabActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

public class VersionService extends Service {
	private NotificationManager notificationMrg;
	private int old_process = 0;
	private boolean isFirstStart = false;
	// private Thread t1;
	// private MyApplication app;
	private Notification notification;

	public void onCreate() {
		super.onCreate();
		isFirstStart = true;
		notificationMrg = (NotificationManager) this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		// System.out.println(MainTabActivity.loading_process + "==");
		// Message msg1=new Message();
		mHandler.handleMessage(new Message());
		Log.d("VersionService", "create");
		// Toast.makeText(VersionService.this, "启动通知服务",
		// Toast.LENGTH_SHORT).show();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			/*
			 * app = (MyApplication) getApplication(); if(app == null){
			 * stopSelf(); notificationMrg.cancel(0); }
			 */
			// 1为出现，2为隐藏
			if (MainTabActivity.loading_process > 99) {
				notificationMrg.cancel(0);
				stopSelf();
				return;
			}
			if (MainTabActivity.mta_instance == null) {
				Log.d("123456", "程序关闭");
				notificationMrg.cancel(0);
				stopSelf();
				return;
			}
			// if (MainTabActivity.loading_process > old_process) {
			if (true) {
				Log.d("123456", Integer.toString(MainTabActivity.loading_process));
				displayNotificationMessage(MainTabActivity.loading_process);

			}
			/*
			 * new Timer().schedule(new TimerTask() {
			 * 
			 * @Override public void run() { // 需要做的事:发送消息 //
			 * LoginUtil.login_handlered = false; Log.d("345abc", "Noti循环等待");
			 * isFirstStart = false; Message msg_loop =
			 * mHandler.obtainMessage(); msg_loop.sendToTarget(); } }, 100);
			 */
			Log.d("123321", "升级进度" + MainTabActivity.loading_process);
			old_process = MainTabActivity.loading_process;
			new Thread() {
				public void run() {
					isFirstStart = false;
					Message msg = mHandler.obtainMessage();
					mHandler.sendMessage(msg);
				}
			}.start();
		}
	};

	@Override
	public void onDestroy() {
		// t1.stop();
		// Toast.makeText(VersionService.this,
		// "通知服务关闭",Toast.LENGTH_SHORT).show();
		// notificationMrg.cancel(0);
		super.onDestroy();

	}

	private void displayNotificationMessage(int count) {

		// Notification的Intent，即点击后转向的Activity
		Intent notificationIntent1 = new Intent(this, this.getClass());
		notificationIntent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent1 = PendingIntent.getActivity(this, 0, notificationIntent1, 0);

		// 创建Notifcation
		getNotification();
		if (isFirstStart || MainTabActivity.loading_process > 97) {
			// notification.defaults |= Notification.DEFAULT_SOUND;// 设定是否振动
			// notification.defaults |= Notification.DEFAULT_VIBRATE;
		}

		notification.flags |= Notification.FLAG_ONGOING_EVENT;

		// 创建RemoteViews用在Notification中
		RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_version);

		contentView.setTextViewText(R.id.n_title, "升级提示");
		contentView.setTextViewText(R.id.n_text, "当前进度：" + count + "% ");
		contentView.setProgressBar(R.id.n_progress, 100, count, false);

		notification.contentView = contentView;
		notification.contentIntent = contentIntent1;

		notificationMrg.notify(0, notification);

	}

	/*
	 * // 创建一个启动其他Activity的Intent Intent intent = new Intent(VersionService.this
	 * , OtherActivity.class); PendingIntent pi = PendingIntent.getActivity(
	 * VersionService.this, 0, intent, 0); Notification notify = new
	 * Notification.Builder(this) // 设置打开该通知，该通知自动消失 .setAutoCancel(true) //
	 * 设置显示在状态栏的通知提示信息 .setTicker("有新消息") // 设置通知的图标
	 * .setSmallIcon(R.drawable.notify) // 设置通知内容的标题 .setContentTitle("一条新通知")
	 * // 设置通知内容 .setContentText("恭喜你，您加薪了，工资增加20%!") // // 设置使用系统默认的声音、默认LED灯
	 * // .setDefaults(Notification.DEFAULT_SOUND //
	 * |Notification.DEFAULT_LIGHTS) // 设置通知的自定义声音
	 * .setSound(Uri.parse("android.resource://com.puhuizhongjia.tongkao/" +
	 * R.raw.msg)) .setWhen(System.currentTimeMillis()) // 设改通知将要启动程序的Intent
	 * .setContentIntent(pi).build(); // 发送通知 nm.notify(NOTIFICATION_ID,
	 * notify); }
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	private Notification getNotification() {
		if (notification == null) {
			notification = new Notification(R.drawable.logo_new, "升级下载中", System.currentTimeMillis());// 设定Notification出现时的声音，一般不建议自定义

			return notification;
		} else {
			return notification;
		}
	}
}
