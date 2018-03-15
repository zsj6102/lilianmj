package org.soshow.beautyedu.utils;

import android.content.Intent;

public class Constant {

	public static String phpUrl = "http://www.ruzhect.com/lilian";
//	public static String phpUrl = "http://192.168.0.18:8080/webLiLian";
	public static final String PROXY_HOST ="";//"10.0.2.2"; //bluestack 
	public static final int PROXY_PORT = 8888 ;
	
	public static String code = "7539518795123456";
	public static String code_local = "8439431230495320";
	public static int wait = 1000;
	// public static final String sdcardPath = "/mnt/sdcard/";
	public static String appid_weixin = "wxf90ea5c4262932ea";
	public static String secret_weixin = "b5dd6fd3165023f64c191a98883b467f";
	public static boolean allow_net_other = false;
	public static Intent next_page;
	public static String total = null;
	// public static boolean refresh_main = true;
	
	//-----------------------------------微信支付-----------------------------------------------------------------
    //请同时修改  androidmanifest.xml里面，.PayActivityd里的属性<data android:scheme="wxb4ba3c02aa476ea1"/>为新设置的appid
    public static final String APP_ID = "";

    //商户号
     public static final String MCH_ID = "";

  //  API密钥，在商户平台设置  bb73a4bac65a44db9a529a0bf2ed64ec
      public static final  String API_KEY= "";
      
      
      //-----------------------------------微信END-------------------------------------------------------------------
	
	//--------------------------------------环信-------------------------------------------------------------------
	public static final String NEW_FRIENDS_USERNAME = "item_new_friends";
	public static final String GROUP_USERNAME = "item_groups";
	public static final String CHAT_ROOM = "item_chatroom";
	public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
	public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
	public static final String ACCOUNT_REMOVED = "account_removed";
	public static final String CHAT_ROBOT = "item_robots";
	public static final String MESSAGE_ATTR_ROBOT_MSGTYPE = "msgtype";
	
	//--------------------------------------环信END--------------------------------------------------------------------
//TODO Constant.APi.

	/**
	 * 微信登录接口
	 */
	public static final String LoginWxUrl = phpUrl+"/wap/LOGIN_WX";
//	public static final String LoginWxUrl = phpUrl;
	/**
	 * 二次开发
	 */
	private static final String API = phpUrl+"/wap/api.php";
    private static final String APITEST = "http://www.ruzhect.com/lilian"+"/wap/api.php";
	/**
	 * 登录
	 */
	public static final String LoginUrl = API+"?action=LOGIN";
	 
	/**
	 * vip回调地址
	 */
	public static final String NOTIFY_URL_VIP = phpUrl+"/wap/api/vip_notify_url.php";
	
	/**
	 * 课程回调地址
	 */
	public static final String NOTIFY_URL_LECTURE = phpUrl+"/wap/api/lecture_notify_url.php";
	
	/**
	 * 线下课程回调地址
	 */
	public static final String NOTIFY_URL_OFF_LINE = phpUrl+"/wap/api/offline_notify_url.php";
	
	/**
	 * 微信购买积分卡回调地址
	 */
	public static final String NOTIFY_URL_INTEGER_WEIXIN = phpUrl+"/wap/api/videocard_notify_url.php";
	
	/**
	 * 微信单词购买积分回调地址
	 */
	public static final String NOTIFY_URL_PER_INTEGER_WEIXIN = phpUrl+"/wap/api/Integral_notify_url.php";


	/**
	 * 个人资料
	 */
    public static final String ACTION_USER_INFO = API+"?action=USER_INFO";
	/**
	 * 线下课程类别列表
	 */
	public static String OFF_LINE_TYPE = API+"?action=GET_CETEGORY";
	
	/**
	 * 线下课程列表
	 */
	public static String OFF_LINE_LIST = API+"?action=OFFLINE_COURSE_LIST";
	/**
	 * 线下课程列表 报名操作
	 */
	public static String LINE_LIST_DETAIL = API+"?action=OFFLINE_COURSE_SIGN";
	/**
	 * 个人资料
	 */
	public static String PERSON_INFO = API+"?action=PROFILE";
	/**
	 * 我的课程
	 */
	public static String MY_LECTURES = API+"?action=MY_LECTURES";
	
	/**
	 * 我的报名列表
	 */
	public static String MY_SIGN_UP = API+"?action=MY_OFFLINE_SIGN";
	/**
	 * 获取课程支付状态
	 */
	public static String GET_PAY_STATUS = API+"?action=COURSE_PAY_STATE";
	//{ Dynamic   start ------------------------------------------------------------
	/**获取朋友圈的动态列表	 */
	public static String DYNAMIC_LIST = API+"?action=DYNAMIC_LIST";
	/** 我的的动态列表 */
	public static String USER_DYNAMIC_LIST = API+"?action=USER_DYNAMIC_LIST";
	/*** 发表动态*/
	public static String DYNAMIC_ADD = API+"?action=DYNAMIC_ADD";
	/** 点赞  */
	public static String DYNAMIC_LIKE = API+"?action=DYNAMIC_LIKE";
	/** 添加评论 */
	public static String DYNAMIC_COMMENT_ADD = API+"?action=DYNAMIC_COMMENT_ADD";
	/** 删除评论 */
	public static String DYNAMIC_COMMENT_DELETE = API+"?action=DYNAMIC_COMMENT_DELETE";

	/**
	 * 获取环信号
	 */
     public static String GET_HX = API+"?action=USER_INFO_GET_HX";

	/**
	 * 环信号获取昵称
	 */
	 public static String GET_NICK = API+"?action=USER_INFO_GET_NICKNAME";
	//} Dynamic   End ============================================================
	/**
	 * 学校列表
	 */
	public static String SCHOOL_LIST = API+"?action=GET_SCHOOL";
	
	/**
	 * 线下课程评论列表
	 */
	public static String OFFLINE_COMMENT_LIST = API+"?action=OFFLINE_COMMENT_LIST";
	   
    /** 修改用户信息    */
    public static String MODIFY_UESR_INFO = API+"?action=MODIFY_USER_INFO";
    
    /** 朋友圈的个人资料   */
    public static String USER_INFO = API+"?action=USER_INFO";
    
    /**
     * 线下课程详情
     */
    public static String OFFLINE_INFO = API+"?action=OFFLINE_INFO";
    
    /**
     * 视频、图文课程列表
     */
    public static String COURSE_LIST = API+"?action=GET_LECTURE_LIST";

	/**
	 * 常识，分析， 视频
	 */
    public static String NEWS_LIST = API+"?action=NEWS_LIST";

	/**
	 * 快讯
	 */

	public static String ARTICLE_LIST = APITEST+"?action=ARTICLE_LIST";
    /**
	 * 培训课程、图文课程评论列表
	 */
	public static String LECTURE_COMMENT_LIST = API+"?action=LECTURE_COMMENT_LIST";
	
	/**
	 * VIP充值价格列表
	 */
	public static String VIP_PRICE_LIST = API+"?action=VIP_PRICE_LIST";
	
	/**
	 * VIP充值生成订单
	 */
	public static String VIP_ORDER = API+"?action=VIP_ORDER";
	
	/**
	 * 培训课程生成订单
	 */
	public static String LECTURE_ORDER = API+"?action=LECTURE_ORDER";
	
	/**
	 * 线下课程生成订单
	 */
	public static String OFFLINE_ORDER = API+"?action=OFFLINE_COURSE_SIGN";
	
	/**
	 * 首页的视频推荐
	 */
	public static String GET_HOME_LECTURE = API+"?action=GET_HOME_LECTURE";
	
	/**
	 * 新增培训课程、图文课程评论
	 */
	public static String ADD_LECTURE_COMMENT = API+"?action=ADD_LECTURE_COMMENT";
	
	/**
	 * 新增线下课程评论
	 */
	public static String ADD_OFFLINE_COMMENT = API+"?action=ADD_OFFLINE_COMMENT";
	
	/**
	 * 单个动态详情
	 */
	public static String DYNAMIC_BY_ID = API+"?action=DYNAMIC_BY_ID";
	
	/**
	 * 新增、获取绑定的银行卡
	 */
	public static String BANK_CARD = API+"?action=BANK_CARD";
	
	/**
	 * 删除银行卡
	 */
	public static String DELETE_BANK_CARD = API+"?action=DELETE_BANK_CARD";
	
	/**
	 * 银行卡的归属地
	 */
	public static String CHECK_CARD_INFO = API+"?action=CHECK_CARD_INFO";
	
	/**
	 * 我的余额
	 */
	public static String MY_INCOME = API+"?action=MY_INCOME";
	
	/**
	 * 提现操作
	 */
	public static String TAKE_CASH = API+"?action=TAKE_CASH";
	
	/**
	 * 收支明细
	 */
	public static String MY_INCOME_RECORD= API+"?action=MY_INCOME_RECORD";
	
	/**
	 * 首页轮播+新闻
	 */
	public static String GET_HOME_PAGE= API+"?action=GET_HOME_PAGE";
	
	/**
	 * vip充值时间段
	 */
	public static String USER_VIP_TIME= API+"?action=USER_VIP_TIME";
	
	/**
	 * 删除个人动态
	 */
	public static String DELETE_MY_DYNAMIC= API+"?action=DYNAMIC_DELETE";
	
	
	/**
	 * 我的观看卡列表
	 */
	public static String VIDEOCARDU= API+"?action=VIDEOCARDU";
	
	/**
	 * 我的购物车
	 */
	public static String GETCART= API+"?action=GETCART";
	
	/**
	 * 加入、删除购物车
	 */
	public static String VIDEOCARDCART= API+"?action=VIDEOCARDCART";
	
	/**
	 * 购物车商品数量的加减
	 * 
	 */
	public static String GETCARTNUM= API+"?action=GETCARTNUM";
	
	
	/**
	 * 积分卡商城列表
	 */
	public static String VIDEOCARDURL= API+"?action=VIDEOCARDURL";
	
	/**
	 * 立即购买详情信息
	 */
	public static String GETGOODSINFO= API+"?action=GETGOODSINFO";
	
	/**
	 * 立即购买、购物车生成订单
	 */
	public static String VIDEOCARDORDER= API+"?action=VIDEOCARDORDER";
	
	/**
	 * 单次购买视频
	 */
	public static String GEINTEGRALINFO= API+"?action=GEINTEGRALINFO";
	
	/**
	 * 单次购买生成订单
	 */
	public static String INTEGRAL= API+"?action=INTEGRAL";
	
	/**
	 * 扣除积分
	 */
	public static String VIDEOCARD= API+"?action=VIDEOCARD";
	
}
