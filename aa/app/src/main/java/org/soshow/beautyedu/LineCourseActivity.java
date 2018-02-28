package org.soshow.beautyedu;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.basketball.common.adapter.CommonAdapter;
import org.soshow.basketball.common.adapter.ViewHolder;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.activity.LoginInputActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.bean.SchoolList;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.bean.LineCourseInfo;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.DensityUtil;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.ScreenUtils;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TimeUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LineCourseActivity extends BaseActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener, OnClickListener {
	public static String OFF_LINE = "off_line";
	public static String OFF_LINE_CODE = "offline_id";
	private String TAG = "LineCourseActivity";
	private TextView title_name;
	private ListView list_line;
	private TextView list_no_line;
	private PullToRefreshView mPullToRefreshView;
	private String app_nonce;
	private String mToken;
	private CommonAdapter<LineCourseInfo> adapterCourse;
	private CommonAdapter<SchoolList> adapterSchool;
	private boolean hasMoreCourse;
	private boolean hasMoreSchool;
	private int startPage = 1;
	private int pageSize = 10;
	
	private boolean first = true;
	private SharedPreferences sp;
	private Editor editor;
	private List<String> typeDatas;
	private List<LineCourseInfo> lineCourseDatas;
	private List<SchoolList> lineSchoolDatas;
	private List<TextView> views;
	private String category_id = "";

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:// token获取
				if (LoginUtil.login_handlered) {
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					Log.e(TAG, "handleMessage  token = " + mToken);
					if (mToken != null) {
						getInfo(startPage);
					} else {
						Toast.makeText(LineCourseActivity.this, "网络不佳，请稍后再试",
								Toast.LENGTH_SHORT).show();
						if (LineCourseActivity.this != null) {
							Toast.makeText(LineCourseActivity.this,
									"网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 2;
							msg_loop.sendToTarget();
						}
					}, 1000);

				}
				break;
			default:
				break;
			}
		}

	};
	private TextView tvCourse;
	private TextView tvSchool;
	private Context context;
	private LinearLayout llType;
	private boolean isCourse = true;//判断当前页面是课程还是学校
	private LinearLayout loading;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_line_course);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		context = LineCourseActivity.this;
		initView();
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		getTokenLocal();

	}

	private void initView() {
		if(lineCourseDatas == null){
			lineCourseDatas = new ArrayList<LineCourseInfo>();
		}
		LogUtils.e("线下课程lineCourseDatas="+lineCourseDatas.size());
		
		views = new ArrayList<TextView>();
		typeDatas = new ArrayList<String>();
		lineSchoolDatas = new ArrayList<SchoolList>();

		title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText("线下课程");

		tvCourse = (TextView) findViewById(R.id.off_line_tv_course);
		tvSchool = (TextView) findViewById(R.id.off_line_tv_school);
		llType = (LinearLayout) findViewById(R.id.course_choose_ll);
		tvCourse.setOnClickListener(this);
		tvSchool.setOnClickListener(this);

		loading = (LinearLayout) findViewById(R.id.linear_load);
		list_line = (ListView) findViewById(R.id.list_linecourse);
		list_no_line = (TextView) findViewById(R.id.list_no_linecourse);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.linecourse_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		adapterCourse = new CommonAdapter<LineCourseInfo>(context,
				lineCourseDatas, R.layout.line_course_list) {

			@Override
			public void convert(final ViewHolder holder, final LineCourseInfo info) {
				ImageView iv = holder.getView(R.id.line_course_iv);
				TextView tvTitle = holder.getView(R.id.line_course_tv_title);
				TextView tvSchool = holder.getView(R.id.line_course_tv_school);
				TextView tvTime = holder.getView(R.id.line_course_tv_time);
				TextView tvstate = holder.getView(R.id.line_course_sign_up);
				TextView tvDetail = holder.getView(R.id.line_course_detail);
				TextView tvCount = holder.getView(R.id.line_course_tv_count);
				
				UniversalImageLoadTool.disPlayTrue(info.img_url, iv,
						R.drawable.defaultpic);
				tvTitle.setText(info.title);
				tvSchool.setText(info.name);
				if (!TextUtils.isEmpty(info.start_time)) {
					tvTime.setText(TimeUtil.getStringdatas(Long
							.valueOf(info.start_time)));
				}
				final String is_sign = info.is_sign;
				Log.e("", "sign_up=" + is_sign);
				if (is_sign.equals("0")) {
					tvstate.setText(R.string.un_sign_up);
					tvstate.setBackgroundResource(R.color.line_gray);
				} else if (is_sign.equals("1")) {
					tvstate.setText(R.string.sign_up);
					tvstate.setBackgroundResource(R.color.bg_green);
				}
				tvCount.setText(info.max_person);
				
				//课程详情
				tvDetail.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = null;
						if (is_sign.equals("1")) {
							if (MyApplication.logined == false) {
								intent = new Intent(context,
										LoginInputActivity.class);
								startActivity(intent);
								overridePendingTransition(R.anim.anim_slider_right_in,
						                R.anim.anim_slider_left_out);
							} else {
							intent = new Intent(LineCourseActivity.this,
									OfflineCoursePayActivity.class);
							intent.putExtra("content_url",info.content_url);
							}
						} else if (is_sign.equals("0")) {
							intent = new Intent(LineCourseActivity.this,
									OfflineCourseUnpayActivity.class);
							intent.putExtra("describe_url",info.describe_url);
							intent.putExtra("img_url", info.img_url);
							LogUtils.e("线下列表图url="+info.img_url);
						}
						intent.putExtra("offline_id",info.offline_id);
						intent.putExtra("from", OFF_LINE);
						startActivity(intent);
						overridePendingTransition(R.anim.anim_slider_right_in,
				                R.anim.anim_slider_left_out);
					}
				});

			}
		};
		list_line.setAdapter(adapterCourse);

		adapterSchool = new CommonAdapter<SchoolList>(context, lineSchoolDatas,
				R.layout.line_school_list) {

			@Override
			public void convert(final ViewHolder holder, final SchoolList schoolList) {
				ImageView iv = holder.getView(R.id.line_school_iv);
				TextView tvTitle = holder.getView(R.id.line_school_tv_title);
				TextView tvPhone = holder.getView(R.id.line_school_tv_phone);
				TextView tvAdress = holder.getView(R.id.line_school_tv_adress);

				UniversalImageLoadTool.disPlayTrue(schoolList.getPic_url(), iv,
						R.drawable.defaultpic);
				tvTitle.setText(schoolList.getName());
				tvPhone.setText(schoolList.getPhone());
				tvAdress.setText(schoolList.getAddress());
				
				//学校介绍
				holder.getView(R.id.line_school_sign_up).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(LineCourseActivity.this, SchoolWebActivity.class);
								intent.putExtra("url", schoolList.getIntroduction_url());
								intent.putExtra("name", "学校介绍");
								startActivity(intent);
								overridePendingTransition(R.anim.anim_slider_right_in,
						                R.anim.anim_slider_left_out);
							}
						});
				//师资力量
				holder.getView(R.id.line_school_power).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(LineCourseActivity.this, SchoolWebActivity.class);
								intent.putExtra("url", schoolList.getFaculty_url());
								intent.putExtra("name", "师资力量");
								startActivity(intent);
								overridePendingTransition(R.anim.anim_slider_right_in,
						                R.anim.anim_slider_left_out);

							}
						});
				//最新排课
				holder.getView(R.id.line_school_new).setOnClickListener(
						new OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(LineCourseActivity.this, LineSchoolActivity.class);
								intent.putExtra("school_id", lineSchoolDatas.get(holder.getPosition()).getSchool_id());
								startActivity(intent );
								overridePendingTransition(R.anim.anim_slider_right_in,
						                R.anim.anim_slider_left_out);
							}
						});
			}
		};
		
		list_line.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = null;
				if(isCourse){
					if (lineCourseDatas.get(position).is_sign.equals("1")) {
						intent = new Intent(LineCourseActivity.this,
								OfflineCoursePayActivity.class);
					} else if (lineCourseDatas.get(position).is_sign.equals("0")) {
						intent = new Intent(LineCourseActivity.this,
								OfflineCourseUnpayActivity.class);
						intent.putExtra("describe_url",lineCourseDatas.get(position).describe_url);
						intent.putExtra("img_url", lineCourseDatas.get(position).img_url);
						LogUtils.e("线下列表图url="+lineCourseDatas.get(position).describe_url);
					}
					intent.putExtra("content_url",lineCourseDatas.get(position).content_url);
					intent.putExtra("offline_id",lineCourseDatas.get(position).offline_id);
					intent.putExtra("from", OFF_LINE);
					startActivity(intent);
					overridePendingTransition(R.anim.anim_slider_right_in,
			                R.anim.anim_slider_left_out);
					
				
				}else{
					intent = new Intent(LineCourseActivity.this, LineSchoolActivity.class);
					intent.putExtra("school_id", lineSchoolDatas.get(position).getSchool_id());
					startActivity(intent);
					overridePendingTransition(R.anim.anim_slider_right_in,
			                R.anim.anim_slider_left_out);
				}
			}
		});

	}
	
	@Override
	protected void onRestart() {
		if(OfflineCourseUnpayActivity.is_tuwenAndLine_pay){
			loading.setVisibility(View.VISIBLE);
			app_nonce = StringUtil.getPhoneIMEI(this);
			mToken = new TokenManager(LineCourseActivity.this).getToken();
			lineCourseDatas.clear();
			getInfo(startPage);
			OfflineCourseUnpayActivity.is_tuwenAndLine_pay = false;
		}
		super.onRestart();
	}

	
	private void getTokenLocal() {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(LineCourseActivity.this).getToken();
		Log.e(TAG, "getTokenLocal  token = " + mToken);
		if (mToken != null) {
			loading.setVisibility(View.VISIBLE);
			getType();
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}

	
	// 课程类别的点击监听
	public class TvOnclickListenner implements OnClickListener {
		private int pos;

		public TvOnclickListenner(int pos) {
			this.pos = pos;
		}

		@Override
		public void onClick(View v) {
			for (int i = 0; i < views.size(); i++) {
				TextView tv = views.get(i);
				if (pos == i) {
					tv.setBackgroundResource(R.drawable.bg_btn_sure_green);
				} else {
					tv.setBackgroundResource(R.drawable.bg_btn_unselect_gray);
				}
			}
			category_id = typeDatas.get(pos);
			startPage = 1;
			lineCourseDatas.clear();
			getInfo(startPage);
		}

	}
	
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		if(isCourse){
			if (hasMoreCourse) {
				startPage++;
				getInfo(startPage);
			} else {
				Toast.makeText(LineCourseActivity.this, R.string.no_more_data,
						Toast.LENGTH_SHORT).show();
				mPullToRefreshView.onFooterRefreshComplete();
			}
		}else{
			if (hasMoreSchool) {
				startPage++;
				getSchool(startPage);
			} else {
				Toast.makeText(LineCourseActivity.this, R.string.no_more_data,
						Toast.LENGTH_SHORT).show();
				mPullToRefreshView.onFooterRefreshComplete();
			}	
		}
		
		
		

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		startPage = 1;
		if(isCourse){
			lineCourseDatas.clear();
			getInfo(startPage);
		}else{
			lineSchoolDatas.clear();
			getSchool(startPage);
		}
		
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_search_logininput:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
			break;
		case R.id.off_line_tv_course:
			tvCourse.setClickable(false);
			tvSchool.setClickable(true);
			isCourse = true;
			llType.setVisibility(View.VISIBLE);
			tvCourse.setBackgroundResource(R.drawable.bg_btn_sure_green);
			tvSchool.setBackgroundResource(R.drawable.bg_btn_unselect_gray);
			category_id = "";
			startPage = 1;
			list_line.setAdapter(adapterCourse);
			lineCourseDatas.clear();
			lineSchoolDatas.clear();
			loading.setVisibility(View.VISIBLE);
			getInfo(startPage);
			break;
		case R.id.off_line_tv_school:
			tvCourse.setClickable(true);
			tvSchool.setClickable(false);
			isCourse = false;
			llType.setVisibility(View.GONE);
			tvCourse.setBackgroundResource(R.drawable.bg_btn_unselect_gray);
			tvSchool.setBackgroundResource(R.drawable.bg_btn_sure_green);
			startPage = 1;
			list_line.setAdapter(adapterSchool);
			lineCourseDatas.clear();
			lineSchoolDatas.clear();
			loading.setVisibility(View.VISIBLE);
			getSchool(startPage);
			break;
		default:
			break;
		}

	}

	// 获取线下课程的类别
		private void getType() {
			String url_type = Constant.OFF_LINE_TYPE + "&tocken=" + mToken
					+ "&app_nonce=" + app_nonce + "&type=offline";
			Log.e(TAG, "线下课程类别url=" + url_type);
			typeDatas.add("");
			NetHelper.get(url_type, new SimpleSingleBeanNetHandler<Captcha>(this) {

				@Override
				protected void onSuccess(Captcha bean) {
					String info = bean.info;
					int padding_top = DensityUtil.dip2px(context, 3);
					int padding = DensityUtil.dip2px(context, 10);
					int h = DensityUtil.dip2px(context, 25);

					int screenWidth = ScreenUtils.getScreenWidth(context);
					int width = screenWidth - padding * 2 - padding * 3;
					int per = width / 4;
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							per, h);
					params.setMargins(0, 0, padding, 0);
					llType.removeAllViews();
					TextView tvAll = new TextView(context);
					tvAll.setGravity(Gravity.CENTER);
					tvAll.setLayoutParams(params);
					tvAll.setText(R.string.all);
					tvAll.setTextColor(getResources().getColor(R.color.text_white));
					tvAll.setBackgroundResource(R.drawable.bg_btn_sure_green);
					llType.addView(tvAll);
					views.add(tvAll);
					tvAll.setOnClickListener(new TvOnclickListenner(0));
					try {
						JSONArray jsonArray = new JSONArray(info);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject obj = (JSONObject) jsonArray.get(i);
							String category_id = obj.getString("category_id");
							String type_name = obj.getString("name");
							typeDatas.add(category_id);

							TextView tv = new TextView(context);
							tv.setGravity(Gravity.CENTER);
							tv.setLayoutParams(params);
							tv.setText(type_name);
							tv.setTextColor(getResources().getColor(
									R.color.text_white));
							tv.setBackgroundResource(R.drawable.bg_btn_unselect_gray);
							llType.addView(tv);
							views.add(tv);
							tv.setOnClickListener(new TvOnclickListenner(i + 1));
						}

						getInfo(startPage);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			});
		}


	//获取线下课程列表
	private synchronized void getInfo(int page) {
		try {
			String url_line = Constant.OFF_LINE_LIST + "&tocken=" + mToken
					+ "&app_nonce=" + app_nonce + "&page_no=" + page
					+ "&page_size=" + pageSize + "&category_id=" + category_id;
			Log.e(TAG, "线下课程url=" + url_line);
			NetHelper.get(url_line, new SimpleSingleBeanNetHandler<Captcha>(
					this) {

				@Override
				protected void onSuccess(Captcha bean) {
					String result = bean.result;
					if (result.equals("0")) {
						try {
							String info = bean.info;
							JSONArray jsonArray = new JSONObject(info)
									.getJSONArray("data");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject dataObj = (JSONObject) jsonArray
										.get(i);
								LineCourseInfo lineInfo = GsonUtils.parseJSON(
										dataObj.toString(),
										LineCourseInfo.class);
								lineCourseDatas.add(lineInfo);
							}
							if (lineCourseDatas.size() > 0) {
								list_no_line.setVisibility(View.GONE);
							} else {
								list_no_line.setVisibility(View.VISIBLE);
							}
							Log.e(TAG, "数据大小=" + lineCourseDatas.size());
							if (jsonArray.length() >= pageSize) {
								hasMoreCourse = true;
							} else {
								hasMoreCourse = false;
							}
							adapterCourse.notifyDataSetChanged();

							if (startPage != 1) {
								mPullToRefreshView.onFooterRefreshComplete();
							} else {
								mPullToRefreshView.onHeaderRefreshComplete();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					} else {
						String mistake = bean.message;
						Toast.makeText(LineCourseActivity.this, mistake,
								Toast.LENGTH_SHORT).show();

					}
					
					loading.setVisibility(View.GONE);

				}

			});
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	
	// 获取学校列表
	private void getSchool(int page) {
		try {
			String url_school = Constant.SCHOOL_LIST + "&tocken=" + mToken
					+ "&app_nonce=" + app_nonce;
			Log.e(TAG, "学校列表url=" + url_school);
			NetHelper.get(url_school, new SimpleSingleBeanNetHandler<Captcha>(
					this) {

				@Override
				protected void onSuccess(Captcha bean) {
					String result = bean.result;
					if (result.equals("0")) {
						try {
							String info = bean.info;
							JSONArray jsonArray = new JSONObject(info)
									.getJSONArray("data");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject dataObj = (JSONObject) jsonArray
										.get(i);
								SchoolList lineInfo = GsonUtils.parseJSON(
										dataObj.toString(), SchoolList.class);
								lineSchoolDatas.add(lineInfo);
							}
							if (lineSchoolDatas.size() > 0) {
								list_no_line.setVisibility(View.GONE);
							} else {
								list_no_line.setVisibility(View.VISIBLE);
							}
							Log.e(TAG, "数据大小=" + lineSchoolDatas.size());
							if (jsonArray.length() >= pageSize) {
								hasMoreSchool = true;
							} else {
								hasMoreSchool = false;
							}
							adapterSchool.notifyDataSetChanged();

							if (startPage != 1) {
								mPullToRefreshView.onFooterRefreshComplete();
							} else {
								mPullToRefreshView.onHeaderRefreshComplete();
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					} else {
						String mistake = bean.message;
						Toast.makeText(LineCourseActivity.this, mistake,
								Toast.LENGTH_SHORT).show();

					}
					
					loading.setVisibility(View.GONE);

				}

			});
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	

	@Override
	public void finish() {
		super.finish();
//		if (adapterCourse != null) {
//			adapterCourse = null;
//		}
//		if (lineCourseDatas != null) {
//			lineCourseDatas.clear();
//			lineCourseDatas = null;
//		}
//		if (adapterSchool != null) {
//			adapterSchool = null;
//		}
//		if (lineSchoolDatas != null) {
//			lineSchoolDatas.clear();
//			lineSchoolDatas = null;
//		}

		try {
			handler.removeCallbacksAndMessages(null);
		} catch (Exception e) {
		}
	}

}
