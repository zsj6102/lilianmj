package org.soshow.beautyedu.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetBroadcastReceiver extends BroadcastReceiver {

	//private static final String TAG = "NetChanged";

	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
			Toast.makeText(context.getApplicationContext(), "网络不给力，请检查网络", Toast.LENGTH_LONG).show();
			// 改变背景或者 处理网络的全局变量
		} else {
			// 改变背景或者 处理网络的全局变量
		}

	}

}
