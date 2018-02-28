package org.soshow.beautyedu.login.sina;

public interface Sinas {

	public static final String CLIENT_ID = "client_id";

	public static final String RESPONSE_TYPE = "response_type";

	public static final String USER_REDIRECT_URL = "redirect_uri";

	public static final String DISPLAY = "display";

	public static final String USER_SCOPE = "scope";

	public static final String PACKAGE_NAME = "packagename";

	public static final String KEY_HASH = "key_hash";

	// 新浪微博======================================================

	// public static String CONSUMER_KEY = "3456387004";
	// public static String CONSUMER_ACC = "eeb4771d80471220e87c87ed758af9af";
	// public static String CONSUMER_KEY = "966056985";// 可用
	public static String CONSUMER_KEY = "1889307709";// 慧众家
	public static String CONSUMER_ACC = "4f257c06be3814a138d74e2f79f08d35";// 慧众家
	// public static String REDIRECT_URL =
	// "http://company.verified.weibo.com/verify/orgapply";// 返回微博调用的接口
	public static String REDIRECT_URL = "http://huizhongjia.com/";// 返回微博调用的接口//可用
	// public static String REDIRECT_URL =
	// "http://weibo.com/u/5111745423/home?wvr=5&uut=fin&from=reg";//
	// 返回微博调用的接口--慧众家
	public static String URL_USERS_SHOW = "https://api.weibo.com/2/users/show.json"; // 获取新浪资料
	public static String share_sina_URL = "https://api.weibo.com/2/statuses/upload_url_text.json"; // 新浪发布带图片的微博

	// 新支持scope 支持传入多个scope权限，用逗号分隔，暂时用不上
	String SINA_SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read," + "follow_app_official_microblog,"
			+ "invitation_write";

	/** 认证Code **/
	String SINA_CODE = "code";

	String SINA_ACCESS_TOKEN = "access_token";

	String SINA_EXPIRES_IN = "expires_in";

	String SINA_UID = "uid";

	String SINA_USER_NAME = "userName";

	String SINA_NAME = "name";

	String SINA_REMIND_IN = "remind_in";

	String SINA_DATE_PATTERN = "yyyy/MM/dd HH:mm:ss";

	String SINA_BASEURL = "https://api.weibo.com/oauth2/";

	String SINA_CLIENT_ID = "client_id";

	String SINA_CLIENT_SECRET = "client_secret";

	String SINA_GRANT_TYPE = "grant_type";

	String SINA_GRANT_TYPE_VALUE = "authorization_code";

	String SINA_REDIRECT_URI = "redirect_uri";

	// 新浪微博首选项

	String PREF_SINA_ACCESS_TOKEN = "SINA_ACCESS_TOKEN";

	String PREF_SINA_EXPIRES_TIME = "SINA_EXPIRES_TIME";

	String PREF_SINA_UID = "SINA_UID";

	String PREF_SINA_USER_NAME = "SINA_USER_NAME";

	String PREF_SINA_REMIND_IN = "SINA_REMIND_IN";

}
