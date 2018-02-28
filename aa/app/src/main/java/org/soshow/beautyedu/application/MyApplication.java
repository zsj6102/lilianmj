package org.soshow.beautyedu.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.soshow.beautyedu.bean.LunBoInfo;
import org.soshow.beautyedu.json.bean.LunBo;
import org.soshow.beautyedu.json.bean.UpDateInfo;
import org.soshow.beautyedu.utils.log.CrashHandler;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;


import com.easemob.EMCallBack;
import com.easemob.chatuidemo.DemoHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

/**
 * 应用程序Application全局控制
 * 
 * @author wuxiaohong
 * @version 2.3.8
 * @created 2015-7-15
 */
public class MyApplication extends Application {
	
	//----------------------环信------------------------------------
	public static Context applicationContext;
	private static MyApplication instance;
	// login user name
	public final String PREF_USERNAME = "username";
	
	/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick = "";
	public static DemoHelper hxSDKHelper = new DemoHelper();
	
	
	//-----------------------------------------------------------------------

	private static final String VALUE = "Harvey";

	private String value;
	private UpDateInfo updateinfo;
	private List<LunBo> lunBos;
	private List<LunBoInfo> lunBoInfos;
	//private String devicedID;
	public static boolean logined;
	public static Map<String, String> map_catID = new HashMap<String, String>();
	public static Map<String, LinkedHashMap<String, String>> catID = new HashMap<String, LinkedHashMap<String, String>>();
	public static boolean setTab1 = false;
	public static int currentVersion;
	// public static List<LectureList> list_lecture = new
	// ArrayList<LectureList>();

	public List<LunBo> getLunBo() {
		return lunBos;
	}

	public static Dialog dialog;

	public void setLunBo(List<LunBo> lunBo) {
		this.lunBos = lunBo;
	}
	
	public List<LunBoInfo> getLunBoInfo() {
		return lunBoInfos;
	}


	public void setLunBoInfo(List<LunBoInfo> lunBoInfos) {
		this.lunBoInfos = lunBoInfos;
	}

	public void setLunBoEmpty() {
		lunBos = new ArrayList<LunBo>();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler.getInstance().init(getApplicationContext());//TODO bugly
		setValue(VALUE); // 初始化全局变量

		UMShareAPI.get(this);
		PlatformConfig.setWeixin("wxf90ea5c4262932ea","b5dd6fd3165023f64c191a98883b467f");
		initImageLoader(getApplicationContext());
//		CrashReport.initCrashReport(MyApplication.this, "900011997", false);
//		CrashReport.setUserId(StringUtil.getPhoneIMEI(MyApplication.this));
		//环信
		applicationContext = this;
        instance = this;

        /**
         * this function will initialize the HuanXin SDK
         * 
         * @return boolean true if caller can continue to call HuanXin related APIs after calling onInit, otherwise false.
         * 
         * 环信初始化SDK帮助函数
         * 返回true如果正确初始化，否则false，如果返回为false，请在后续的调用中不要调用任何和环信相关的代码
         * 
         * for example:
         * 例子：
         * 
         * public class DemoHXSDKHelper extends HXSDKHelper
         * 
         * HXHelper = new DemoHXSDKHelper();
         * if(HXHelper.onInit(context)){
         *     // do HuanXin related work
         * }
         */
		DemoHelper.getInstance().init(applicationContext);
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public static void initImageLoader(Context context) {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)
                // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }


	public String getValue() {
		return value;
	}

	public UpDateInfo getUpdateinfo() {
		return updateinfo;
	}

	public void setUpdateinfo(UpDateInfo updateinfo) {
		this.updateinfo = updateinfo;
	}
	
	
	/*
	 * -------------------环信-------------------------
	 */
	public static MyApplication getInstance() {
		return instance;
	}
 

	/**
	 * 获取当前登陆用户名
	 *
	 * @return
	 */

	/**
	 * 设置用户名
	 */


	/**
	 * 退出登录,清空数据
	 */
	public void logout(final boolean isGCM,final EMCallBack emCallBack) {
		// 先调用sdk logout，在清理app中自己的数据
	    hxSDKHelper.logout(isGCM,emCallBack);
	}
	
	//END----------------------------------------------------------------------

}