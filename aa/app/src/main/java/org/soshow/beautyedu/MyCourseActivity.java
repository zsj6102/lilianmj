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
import org.soshow.beautyedu.activity.FragmentMyTitle;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.bean.VideoCourseInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.bean.MyCourseInfo;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleMultiBeanNetHandler;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.json.utils.NetUtil;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.MyAlertDialog;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.FragmentAdapter;
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MyCourseActivity extends FragmentActivity implements OnHeaderRefreshListener, OnFooterRefreshListener{

	protected String TAG = "MyCourseActivity";
	private Context context;
	private SharedPreferences sp;
	private Editor editor;
	private TextView title_name;
	private ListView list_line;
	private PullToRefreshView mPullToRefreshView;
	private String app_nonce;
	private String mToken;
	private boolean hasMore;
	private int startPage = 1;
	private int pageSize = 10;
	private TextView tvNodata;
	private List<VideoCourseInfo> myLectures;
	private CommonAdapter<VideoCourseInfo> adapter;
	
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:// token获取
				if (LoginUtil.login_handlered) {
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
						getInfo(startPage );
					} else {
						Toast.makeText(MyCourseActivity.this, "网络不佳，请稍后再试",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
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
	private LinearLayout loading;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_record_collect);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		context = MyCourseActivity.this;
		initView();
		getTokenLocal();
	}


	private void initView() {
		myLectures = new ArrayList<VideoCourseInfo>();
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		
		title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText("我的课程");
		
		loading = (LinearLayout) findViewById(R.id.linear_load);
		tvNodata = (TextView) findViewById(R.id.list_no_course);
		list_line = (ListView) findViewById(R.id.mycourse_list_record);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.mycourse_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		adapter = new CommonAdapter<VideoCourseInfo>(context,
				myLectures, R.layout.item_mycourse_list) {
			@Override
			public void convert(ViewHolder holder, VideoCourseInfo info) {
				ImageView iv = holder.getView(R.id.my_lecture_record_pic);
				TextView tvTitle = holder.getView(R.id.my_lecture_tv_title);
				TextView tvCount = holder.getView(R.id.my_lecture_tv_look);
				TextView tvCharge = holder.getView(R.id.my_lecture_tv_price);
				holder.getView(R.id.lecture_tag).setVisibility(View.INVISIBLE);;
				UniversalImageLoadTool.disPlayTrue(info.getHead_image(), iv, R.drawable.defaultpic);
				tvTitle.setText(info.getLecture_title());
				tvCount.setText(info.getLecture_click_count()+"人观看");
				tvCharge.setText("￥"+info.getPrice());
			}
		};
		list_line.setAdapter(adapter);
		
		list_line.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				LogUtils.e("","Lecture_type=="+myLectures.get(position).getLecture_type());
				if(myLectures.get(position).getLecture_type().equals("1")){
					Log.e("培训视频列表播放地址：", myLectures.get(position).getVideo_local_name().toString());
					
					
					if ((NetUtil.isConnected(context) && NetUtil.isWifi(context)) || NetUtil.isConnected(context)
							&& !NetUtil.isWifi(context) && sp.getInt("allow_net_local", 0) == 1) {
						if (Build.VERSION.SDK_INT >= 16) {

							Intent intent = new Intent(context, CoursesPlayActivity.class);
							intent.putExtra("hasPay", true);
							intent.putExtra("title", "我的课程");
							intent.putExtra("look", myLectures.get(position).getLecture_click_count());
							intent.putExtra("position", position);
							intent.putExtra("lecture_cat1", "");
							intent.putExtra("lecture_questions_id","");
							intent.putExtra("lecture_cat3", "");
//							intent.putExtra("url_s", myLectures.get(position).getVideo_local_name());
//							intent.putExtra("url_h", myLectures.get(position).getVideo_local_name());
//							intent.putExtra("url_p", myLectures.get(position).getVideo_local_name());
//							intent.putExtra("video_local_name", myLectures.get(position).getVideo_local_name());
							
							if(myLectures.get(position).getPlay_urls().size() > 0){
								intent.putExtra("url_s", myLectures.get(position).getPlay_urls().get(0).getUrl());
								intent.putExtra("url_h", myLectures.get(position).getPlay_urls().get(0).getUrl());
								intent.putExtra("url_p", myLectures.get(position).getPlay_urls().get(0).getUrl());
								intent.putExtra("video_local_name", myLectures.get(position).getPlay_urls().get(0).getUrl());
							}else{
								intent.putExtra("url_s", "");
								intent.putExtra("url_h", "");
								intent.putExtra("url_p", "");
								intent.putExtra("video_local_name", "");
							}
							
							intent.putExtra("lecture_title", myLectures.get(position).getLecture_title());
							intent.putExtra("lecture_content",
									myLectures.get(position).getLecture_content());
							intent.putExtra("lecture_id", myLectures.get(position).getLecture_id());
							intent.putExtra("course_id", "");
							intent.putExtra("vheight_s", "9");
							intent.putExtra("vwidth_s", "16");
							intent.putExtra("lecture_click_count",
									myLectures.get(position).getLecture_click_count());
							intent.putExtra("image_url", myLectures.get(position).getImage_url());
							intent.putExtra("price", myLectures.get(position).getPrice());
							intent.putExtra("lecture_operate","");
							intent.putExtra("course_pack_id", "");
							try {
								intent.putExtra("play_duration","");
							} catch (Exception e) {
							}

							if (sp.getBoolean("logined", false)) {
									intent.putExtra("learn_status", 0);
									intent.putExtra("my_duration", 0);
							}
							context.startActivity(intent);
							overridePendingTransition(R.anim.anim_slider_right_in,
					                R.anim.anim_slider_left_out);
						} else {
							Toast.makeText(context, "您当前系统版本过低,请升级到安卓4.1及以上", Toast.LENGTH_SHORT).show();
						}
					} else if (NetUtil.isConnected(context) && sp.getInt("allow_net_local", 0) == 0
							&& !NetUtil.isWifi(context)) {
						Builder bd = new MyAlertDialog.Builder(context);
						bd.setTitle("网络提示").setMessage(R.string.video_net_warn)
								.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								arg0.dismiss();
								if (Build.VERSION.SDK_INT >= 16) {
									Intent intent = new Intent(context, CoursesPlayActivity.class);
									intent.putExtra("hasPay", true);
									intent.putExtra("title", "我的课程");
									intent.putExtra("look", myLectures.get(position).getLecture_click_count());
									intent.putExtra("position", position);
									intent.putExtra("lecture_cat1","");
									intent.putExtra("lecture_questions_id","");
									intent.putExtra("lecture_cat3","");
//									intent.putExtra("url_s", myLectures.get(position).getVideo_local_name());
//									intent.putExtra("url_h", myLectures.get(position).getVideo_local_name());
//									intent.putExtra("url_p", myLectures.get(position).getVideo_local_name());
//									intent.putExtra("url_real", myLectures.get(position).getVideo_local_name());
									
									if(myLectures.get(position).getPlay_urls().size() > 0){
										intent.putExtra("url_s", myLectures.get(position).getPlay_urls().get(0).getUrl());
										intent.putExtra("url_h", myLectures.get(position).getPlay_urls().get(0).getUrl());
										intent.putExtra("url_p", myLectures.get(position).getPlay_urls().get(0).getUrl());
										intent.putExtra("video_local_name", myLectures.get(position).getPlay_urls().get(0).getUrl());
									}else{
										intent.putExtra("url_s", "");
										intent.putExtra("url_h", "");
										intent.putExtra("url_p", "");
										intent.putExtra("video_local_name", "");
									}
									
									intent.putExtra("lecture_title",
											myLectures.get(position).getLecture_title());
									intent.putExtra("lecture_content","");
									intent.putExtra("lecture_id", myLectures.get(position).getLecture_id());
									intent.putExtra("course_id", "");
									// intent.putExtra("remark",
									// list.get(position).get("remark").toString());
									intent.putExtra("vheight_s", "9");
									intent.putExtra("vwidth_s", "16");
									intent.putExtra("lecture_click_count",
											myLectures.get(position).getLecture_click_count());
									intent.putExtra("image_url", myLectures.get(position).getImage_url());
									intent.putExtra("price", myLectures.get(position).getPrice());
									intent.putExtra("lecture_operate","");
									intent.putExtra("course_pack_id","");
									try {
										intent.putExtra("play_duration","");
									} catch (Exception e) {
										// TODO: handle exception
									}

									if (sp.getBoolean("logined", false)) {
											intent.putExtra("learn_status", 0);
											intent.putExtra("my_duration", 0);
									}
									editor.putInt("allow_net_local", 1);
									editor.commit();
									Constant.allow_net_other = true;
									context.startActivity(intent);
									overridePendingTransition(R.anim.anim_slider_right_in,
							                R.anim.anim_slider_left_out);
								} else {
									Toast.makeText(context, "您当前系统版本过低,请升级到安卓4.1及以上", Toast.LENGTH_SHORT).show();
								}
							}
						}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								dialog.dismiss();

							}
						}).show();
					} else if (!NetUtil.isConnected(context)) {
						Toast.makeText(context, "您当前无网络", Toast.LENGTH_SHORT).show();
					}
				}else if(myLectures.get(position).getLecture_type().equals("2")){
					Intent intent = new Intent(MyCourseActivity.this, WebPlayVideoActivity.class);
					intent.putExtra("url", myLectures.get(position).getLecture_link());
					intent.putExtra("title", "我的课程");
					startActivity(intent);
					overridePendingTransition(R.anim.anim_slider_right_in,
			                R.anim.anim_slider_left_out);
				}else if(myLectures.get(position).getLecture_type().equals("3")){
					Intent intent = new Intent(MyCourseActivity.this,
							OfflineCoursePayActivity.class);
					intent.putExtra("content_url",myLectures.get(position).getLecture_graphic_content_url()+"&tocken="+ mToken + "&app_nonce=" + app_nonce);
					intent.putExtra("offline_id",myLectures.get(position).getLecture_id());
					intent.putExtra("from",TuwenActivity.TU_WEN);
					startActivity(intent);
				}
				
				
				
				
				
				
				
				
				
				
				
			}
		});
	}

	/*
	 * 数据获取
	 */
	private void getTokenLocal() {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(MyCourseActivity.this).getToken();
		Log.e(TAG, "getTokenLocal  token = "+mToken);
		if (mToken != null) {
			startPage = 1;
			loading.setVisibility(View.VISIBLE);
			getInfo(startPage);
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}
	
	private void getInfo(final int page) {
		try {
			String url_my_lecture = Constant.MY_LECTURES+"&tocken="+mToken+"&app_nonce="+app_nonce+"&page_no=" + page;
			Log.e(TAG, "我的课程url="+url_my_lecture);
			NetHelper.get(url_my_lecture , new SimpleSingleBeanNetHandler<Captcha>(context) {

				@Override
				protected void onSuccess(Captcha bean) {
					String result = bean.result;
					try {
						if(result.equals("0")){
							JSONArray jsonArray = new JSONObject(bean.info).getJSONArray("data");
							if(jsonArray.length()>=10){
								hasMore = true;
							}else{
								hasMore = false;
							}
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject obj = (JSONObject) jsonArray.get(i);
								VideoCourseInfo myLecture = GsonUtils.parseJSON(obj.toString(), VideoCourseInfo.class);
								myLectures.add(myLecture);
							}
							if (page != 1) {
								mPullToRefreshView.onFooterRefreshComplete();
							} else {
								mPullToRefreshView.onHeaderRefreshComplete();
							}
							if(myLectures.size()>0){
								tvNodata.setVisibility(View.GONE);
							}else{
								tvNodata.setVisibility(View.VISIBLE);
							}
							adapter.notifyDataSetChanged();
						}else{
							ToastUtil.getInstance().showToast(context, bean.message);
						}
						
						loading.setVisibility(View.GONE);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
		}

	}



	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		if (hasMore) {
			startPage++;
			getInfo(startPage);
		} else {
			Toast.makeText(MyCourseActivity.this, "没有更多数据",
					Toast.LENGTH_SHORT).show();
			mPullToRefreshView.onFooterRefreshComplete();
		}

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		startPage = 1;
		myLectures.clear();
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
		if (myLectures != null) {
			myLectures.clear();
			myLectures = null;
		}

		try {
			handler.removeCallbacksAndMessages(null);
		} catch (Exception e) {
		}
	}

	@Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slider_left_in,
                R.anim.anim_slider_right_out);
    }

}
