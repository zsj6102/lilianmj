package org.soshow.beautyedu;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;
import org.soshow.basketball.common.adapter.CommonAdapter;
import org.soshow.basketball.common.adapter.ViewHolder;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.LineCourseInfo;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleMultiBeanNetHandler;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TimeUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MySignUpActivity extends BaseActivity implements OnHeaderRefreshListener, OnFooterRefreshListener {

	private String TAG = "MySignUpActivity";
	private TextView title_name;
	private ListView list_line;
	private PullToRefreshView mPullToRefreshView;
	private SharedPreferences sp;
	private Editor editor;
	private CommonAdapter<LineCourseInfo> adapter;
	private String app_nonce;
	private String mToken;
	private int startPage = 1;
	protected int pageSize = 10;
	protected boolean hasMore;
	
	private List<LineCourseInfo> lineCourseDatas = new ArrayList<LineCourseInfo>();

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
					Log.e(TAG, "handleMessage  token = "+mToken);
					if (mToken != null) {
						getInfo(startPage);
					} else {
						Toast.makeText(MySignUpActivity.this, "网络不佳，请稍后再试",
								Toast.LENGTH_SHORT).show();
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
	private TextView tvNodata;
	private LinearLayout loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_line_course);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		initView();
		getTokenLocal();

		adapter = new CommonAdapter<LineCourseInfo>(MySignUpActivity.this,
				lineCourseDatas, R.layout.line_course_list) {
			@Override
			public void convert(ViewHolder holder, LineCourseInfo info) {
				Log.e("","我的报名================");
				ImageView iv = holder.getView(R.id.line_course_iv);
				TextView tvTitle = holder.getView(R.id.line_course_tv_title);
				TextView tvTime = holder.getView(R.id.line_course_tv_time);
				TextView tvstate = holder.getView(R.id.line_course_sign_up);
				TextView tvDetail = holder.getView(R.id.line_course_detail);
				TextView tvCount = holder.getView(R.id.line_course_tv_count);
				UniversalImageLoadTool.disPlayTrue(info.img_url, iv,
						R.drawable.defaultpic);
				tvTitle.setText(info.title);
				if(!TextUtils.isEmpty(info.start_time)){
					tvTime.setText(TimeUtil.getStringdatas(Long.valueOf(info.start_time)));
				}
				
				holder.getView(R.id.line_course_count).setVisibility(View.INVISIBLE);
				holder.getView(R.id.line_course_tv_count).setVisibility(View.INVISIBLE);
			}
		};
		list_line.setAdapter(adapter);
		
		list_line.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = null;
				intent = new Intent(MySignUpActivity.this,
						OfflineCoursePayActivity.class);
				intent.putExtra("content_url",lineCourseDatas.get(position).content_url);
				intent.putExtra("offline_id",lineCourseDatas.get(position).offline_id);
				intent.putExtra("from", LineCourseActivity.OFF_LINE);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slider_right_in,
		                R.anim.anim_slider_left_out);
			}
		});
		
	}

	private void initView() {
		title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText("我的报名");
		
		findViewById(R.id.ll_head).setVisibility(View.GONE);
		findViewById(R.id.horizontalScrollView1).setVisibility(View.GONE);
		findViewById(R.id.view_line).setVisibility(View.GONE);

		loading = (LinearLayout) findViewById(R.id.linear_load);
		tvNodata = (TextView) findViewById(R.id.list_no_linecourse);
		list_line = (ListView) findViewById(R.id.list_linecourse);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.linecourse_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
	}

	private void getTokenLocal() {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(MySignUpActivity.this).getToken();
		if (mToken != null) {
			loading.setVisibility(View.VISIBLE);
			getInfo(startPage);
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}
	
	/*
	 * 获取我的报名数据
	 */
	private void getInfo(int page){
		try {
			String url_line = Constant.MY_SIGN_UP+"&tocken="+mToken+"&app_nonce="+app_nonce+"&page_no=" + page;
			Log.e(TAG, "我的报名url="+url_line);
			NetHelper.get(url_line, new SimpleMultiBeanNetHandler<LineCourseInfo>(this) {

				@Override
				protected void onSuccess(List<LineCourseInfo> list) {
					if(list.size()>=pageSize){
						hasMore = true;
					}else{
						hasMore = false;
					}
					lineCourseDatas.addAll(list);
					adapter.notifyDataSetChanged();
					
					if (startPage != 1) {
						mPullToRefreshView.onFooterRefreshComplete();
					} else {
						mPullToRefreshView.onHeaderRefreshComplete();
					}
					
					if(lineCourseDatas.size()>0){
						tvNodata.setVisibility(View.GONE);
					}else{
						tvNodata.setVisibility(View.VISIBLE);
					}
					
					loading.setVisibility(View.GONE);
					
				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					try {
						JSONObject jo = new JSONObject(errorMsg);

						if (jo.optString("result").equals("2") && startPage != 1) {
							Toast.makeText(MySignUpActivity.this, "无更多报名记录", Toast.LENGTH_SHORT).show();
						} else if (jo.optString("result").equals("2") && startPage == 1) {
							if (lineCourseDatas != null && adapter != null) {
								lineCourseDatas.clear();
								adapter.notifyDataSetChanged();
							}
						} else if (jo.optString("result").equals("99")) {
							editor.putBoolean("token_logined", false);
							editor.remove("mToken");
							editor.commit();
						} else if (jo.optString("result").equals("1")) {
							if (sp.getBoolean("logined", false)) {
								Toast.makeText(MySignUpActivity.this, "网络错误，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
								editor.putBoolean("token_logined", false);
								editor.putBoolean("logined", false);
								MyApplication.logined = false;
								new LoginUtil(MySignUpActivity.this, 20);
							}
						} else {
							String mistake = jo.optString("info");
							Toast.makeText(MySignUpActivity.this, mistake, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						
					}
					if (startPage != 1) {
						startPage--;
						mPullToRefreshView.onFooterRefreshComplete();
					} else {
						mPullToRefreshView.onHeaderRefreshComplete();
					}
				}

			});
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
	
	

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		if (hasMore) {
			startPage++;
			getInfo(startPage);
		} else {
			Toast.makeText(MySignUpActivity.this, "没有更多数据",
					Toast.LENGTH_SHORT).show();
			mPullToRefreshView.onFooterRefreshComplete();
		}

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		startPage = 1;
		lineCourseDatas.clear();
		getInfo(startPage);
	}
	
	public void onClick(View v) {
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

	@Override
	public void finish() {
		super.finish();
		if (adapter != null) {
			adapter = null;
		}
		if (lineCourseDatas != null) {
			lineCourseDatas.clear();
			lineCourseDatas = null;
		}

		try {
			handler.removeCallbacksAndMessages(null);
		} catch (Exception e) {
		}
	}

}
