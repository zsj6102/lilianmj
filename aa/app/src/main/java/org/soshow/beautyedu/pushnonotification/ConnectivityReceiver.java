package org.soshow.beautyedu.pushnonotification;

import org.soshow.beautyedu.CoursesPlayActivity;
import org.soshow.beautyedu.activity.PlayVideoActivity;
import org.soshow.beautyedu.json.utils.NetUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * A broadcast receiver to handle the changes in network connectiion states.
 * 
 * @author
 */
public class ConnectivityReceiver extends BroadcastReceiver {

	private static final String LOGTAG = LogUtil.makeLogTag(ConnectivityReceiver.class);

	private NotificationService notificationService;
	// private Context context;
	// private String mToken;
	// private int net_flag_now;
	private SharedPreferences sp;
	// private Editor editor;

	public ConnectivityReceiver(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// this.context = context;
		Log.d("345abc", "网络变化");
		Log.d(LOGTAG, "ConnectivityReceiver.onReceive()...");
		String action = intent.getAction();
		Log.d(LOGTAG, "action=" + action);

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null) {
			Log.d(LOGTAG, "Network Type  = " + networkInfo.getTypeName());
			Log.d(LOGTAG, "Network State = " + networkInfo.getState());
			if (networkInfo.isConnected()) {

				Log.i(LOGTAG, "Network connected");
				notificationService.connect();
				Log.d("345abc", "有网络");

				sp = context.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
				// editor = sp.edit();
				if (PlayVideoActivity.pva_instance != null && !NetUtil.isWifi(context)
						&& PlayVideoActivity.pva_instance.getPlayer() != null
						&& (sp.getInt("allow_net_local", 0) != 1)) {
					Log.d("1221", "网络改变，发送消息");
					PlayVideoActivity.pva_instance.getHandler().obtainMessage(5).sendToTarget();
				} else if (CoursesPlayActivity.pva_instance != null && !NetUtil.isWifi(context)
						&& CoursesPlayActivity.pva_instance.getPlayer() != null
						&& (sp.getInt("allow_net_local", 0) != 1)) {
					CoursesPlayActivity.pva_instance.getHandler().obtainMessage(5).sendToTarget();
				}
			}
		} else {
			Log.e(LOGTAG, "Network unavailable");
			// if (FragmentSet.allow_net != null) {
			// FragmentSet.allow_net.setEnabled(false);
			// }
			notificationService.disconnect();
			// sp = context.getSharedPreferences("huizhongjia",
			// Context.MODE_PRIVATE);
			// editor = sp.edit();
			// if (PlayVideoActivity.pva_instance != null
			// && PlayVideoActivity.pva_instance.getPlayer() != null
			// && (sp.getInt("allow_net_local", 0) != 1)) {
			// Log.d("1221", "网络改变，发送消息");
			// PlayVideoActivity.pva_instance.getHandler().obtainMessage(5).sendToTarget();
			// }
		}
	}
	/*
	 * private void getTokenLocal() { mToken = new
	 * TokenManager(context).getToken(); if (mToken != null) { getInfo(); } else
	 * { TokenManager.handlered = false; Message msg_loop =
	 * handler.obtainMessage(); msg_loop.what = 2; msg_loop.sendToTarget(); } }
	 * 
	 * private void getInfo() {
	 * 
	 * String app_nonce = StringUtil.getPhoneIMEI(context); int allow_net_local
	 * = sp.getInt("allow_net_local", 0); String url_setnet = Constant.phpUrl +
	 * "/wap/api.php?action=SET_NET&app_nonce=" + app_nonce + "&tocken=" +
	 * mToken + "&flag=" + net_flag_now + "&allow=" + allow_net_local;
	 * NetHelper.get(url_setnet, new
	 * SimpleSingleBeanNetHandler<Captcha>(context) {
	 * 
	 * @Override protected void onSuccess(Captcha bean) { // TODO Auto-generated
	 * method stub Log.d("345abc", bean.info); switch
	 * (Integer.parseInt(bean.result)) { case 0: editor.putInt("net_flag",
	 * net_flag_now); // editor.putInt("allow_net_local", allow);
	 * editor.commit(); // Toast.makeText(context, "设置成功", //
	 * Toast.LENGTH_SHORT).show();
	 * 
	 * break; // case 99: // Toast.makeText(context, "设置失败",
	 * Toast.LENGTH_SHORT).show(); // break; default: // Toast.makeText(context,
	 * "设置失败," + bean.info, // Toast.LENGTH_SHORT).show(); // Log.d("345abc",
	 * "设置错误信息为 " + bean.info); break; } }
	 * 
	 * @Override protected void onError(int errorCode, String errorMsg) { //
	 * TODO Auto-generated method stub // super.onError(errorCode, errorMsg); }
	 * 
	 * }); }
	 * 
	 * private Handler handler = new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) { // TODO Auto-generated
	 * method stub super.handleMessage(msg); switch (msg.what) { case 2: if
	 * (TokenManager.handlered) { Log.d("345abc", "处理完毕，获取新token");
	 * TokenManager.handlered = false; mToken = sp.getString("mToken", null); if
	 * (mToken != null) { getInfo(); } } else { new Timer().schedule(new
	 * TimerTask() {
	 * 
	 * @Override public void run() { // 需要做的事:发送消息 // LoginUtil.login_handlered
	 * = false; Log.d("345abc", "服务循环等待"); Message msg_loop =
	 * handler.obtainMessage(); msg_loop.what = 2; msg_loop.sendToTarget(); } },
	 * 1000);
	 * 
	 * } break;
	 * 
	 * default: break; } }
	 * 
	 * };
	 */
}
