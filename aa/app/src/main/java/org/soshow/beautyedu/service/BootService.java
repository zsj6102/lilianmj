package org.soshow.beautyedu.service;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.pushnonotification.ServiceManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootService extends BroadcastReceiver{

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		//开机启动推送服务
		ServiceManager serviceManager = new ServiceManager(arg0);
		serviceManager.setNotificationIcon(R.drawable.notification);
		serviceManager.startService();
		
	}

}
