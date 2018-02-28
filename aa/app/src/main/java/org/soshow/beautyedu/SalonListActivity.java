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
import org.soshow.beautyedu.bean.VideoCourseInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
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
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author caixiaozhen
 * @deprecated 美容列表
 *
 */
public class SalonListActivity extends BaseActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {
	private String TAG = "SalonListActivity";
	private Context context;
	private String mToken;
	private String app_nonce;
	private String type_name;
	private String category_id;
	private SharedPreferences sp;
	private Editor editor;
	private int startPage = 1;
	private int pageSize = 10;
	private boolean hasMore;
	private TextView tvNodata;
	private LinearLayout loading;
	private ListView list_course;
	private PullToRefreshView mPullToRefreshView;
	private CommonAdapter<VideoCourseInfo> adapterCourse;
	private List<VideoCourseInfo> videoCourses;
	private String currenType;//跳到详情的是本地还是视频
	private int currenPosition;//跳到详情的position

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
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
						Toast.makeText(
								context,
								getResources()
										.getString(R.string.generic_error),
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
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_salon_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		initView();
		getTokenLocal();

	}
	
	@Override
	protected void onRestart() {
		if(VipPayActivity.is_course_pay){
			getTokenLocal();
			VipPayActivity.is_course_pay = false;
		}
		if(VipPayActivity.PER_INTEGERAL_PAY_SUCCESS){
			if(currenType.equals("1")){
				playVideoInfo(currenPosition);
			}else if(currenType.equals("2")){
				addClick(currenPosition);
			}
			
			VipPayActivity.PER_INTEGERAL_PAY_SUCCESS = false;
		}
		super.onRestart();
	}

	private void initView() {
		context = SalonListActivity.this;
		category_id = getIntent().getStringExtra("category_id");
		type_name = getIntent().getStringExtra("type_name");
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		videoCourses = new ArrayList<VideoCourseInfo>();

		TextView title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText(type_name.substring(0, type_name.length() - 1));

		loading = (LinearLayout) findViewById(R.id.linear_load);
		tvNodata = (TextView) findViewById(R.id.list_no_salon);
		list_course = (ListView) findViewById(R.id.list_salon);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.salon_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		initItemListenner();
		adapterCourse = new CommonAdapter<VideoCourseInfo>(context, videoCourses,
				R.layout.item_tuwen_course) {

			@Override
			public void convert(ViewHolder holder, VideoCourseInfo course) {
				ImageView iv = holder.getView(R.id.tuwen_iv);
				TextView tvTitle = holder.getView(R.id.tuwen_tv_title);
				TextView tvState = holder.getView(R.id.tuwen_tv_state);
				TextView tvRmb = holder.getView(R.id.tuwen_rmb);
				TextView tvPrice = holder.getView(R.id.tuwen_rmb);
				TextView tvLook = holder.getView(R.id.tuwen_tv_look);
				TextView tvLike = holder.getView(R.id.tuwen_tv_like);
				TextView tvSay = holder.getView(R.id.tuwen_tv_say);

				UniversalImageLoadTool.disPlayTrue(course.getHead_image(), iv,
						R.drawable.defaultpic);
				tvTitle.setText(course.getLecture_title());

				tvState.setGravity(Gravity.CENTER);
				
					String price = course.getPrice();
					String lecture_integral = course.getLecture_integral();
					LogUtils.e("价格==="+price+"\n积分==="+lecture_integral);
						if (TextUtils.isEmpty(lecture_integral)||lecture_integral.equals("0")) {
							tvState.setText("免费学习");
							tvState.setBackgroundResource(R.drawable.bg_btn_vip_red);
							tvRmb.setVisibility(View.GONE);
							tvPrice.setVisibility(View.GONE);
						} else {
							
//							if (is_vip) {
//								tvState.setText("VIP");
//								tvState.setBackgroundResource(R.drawable.bg_btn_vip_red);
//								tvPrice.setText(course.getPrice());
//								tvRmb.setVisibility(View.VISIBLE);
//								tvPrice.setVisibility(View.VISIBLE);
//							} else {
//							if (course.getOrder_state().equals("0")) {
								tvState.setText("付费学习");
								tvState.setBackgroundResource(R.drawable.bg_btn_unselect_gray);
								tvRmb.setVisibility(View.VISIBLE);
								tvPrice.setVisibility(View.VISIBLE);
								tvPrice.setText("积分："+lecture_integral);
//							} else if (course.getOrder_state().equals("1")) {
//								tvState.setText("已购买");
//								tvState.setBackgroundResource(R.drawable.bg_btn_sure_green);
//								tvPrice.setText(course.getPrice());
//								tvRmb.setVisibility(View.VISIBLE);
//								tvPrice.setVisibility(View.VISIBLE);
//							}
//							}
						}

				tvLook.setText(course.getLecture_click_count() + "人观看");
				String playUrl = course.getVideo_local_name();

			}
		};

		list_course.setAdapter(adapterCourse);

	}

	/*
	 * item的点击监听
	 */
	private void initItemListenner() {
		list_course.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				
				String lecture_type = videoCourses.get(position).getLecture_type();
				LogUtils.e("", "Lecture_type==="+ lecture_type);
				boolean is_vip = videoCourses.get(position).isIs_vip();
				String hasPay = videoCourses.get(position).getOrder_state();
				String lecture_integral = videoCourses.get(position).getLecture_integral();
				if (lecture_type.equals("1")) {//本地视频
					if (TextUtils.isEmpty(lecture_integral)||lecture_integral.equals("0")) {//直接观看
						if (MyApplication.logined == false) {
							Intent intent = new Intent(context,
									LoginInputActivity.class);
							startActivity(intent);
							overridePendingTransition(R.anim.anim_slider_right_in,
					                R.anim.anim_slider_left_out);
						} else {
						playVideoInfo(position);
						}
					} else{//付费观看
							Intent intent = new Intent(SalonListActivity.this, VipPayActivity.class);
							intent.putExtra("title", type_name.substring(0,type_name.length() - 1));
							intent.putExtra("image_url", videoCourses.get(position).getHead_image());
							intent.putExtra("lectureTitle", videoCourses.get(position).getLecture_title());
							intent.putExtra("look", videoCourses.get(position).getLecture_click_count());
							intent.putExtra("price", videoCourses.get(position).getPrice());
							intent.putExtra("lecture_content_url", videoCourses
									.get(position).getLecture_content_url());
							intent.putExtra("lecture_id",videoCourses.get(position).getLecture_id());
							intent.putExtra("lecture_integral",videoCourses.get(position).getLecture_integral());
							intent.putExtra("lecture_type","1");//本地视频
							currenType = "1";
							currenPosition = position;
							Bundle bundle = new Bundle();
							VideoCourseInfo videoCourseInfo = videoCourses.get(position);
							bundle.putSerializable("VideoCourseInfo", videoCourseInfo);
							intent.putExtras(bundle);
							startActivity(intent);
							overridePendingTransition(R.anim.anim_slider_right_in,
					                R.anim.anim_slider_left_out);
					}
					
				} else if (lecture_type.equals("2")) {//网页视频
					if (TextUtils.isEmpty(lecture_integral)||lecture_integral.equals("0")) {//直接观看
						if (MyApplication.logined == false) {
							Intent intent = new Intent(context,
									LoginInputActivity.class);
							startActivity(intent);
							overridePendingTransition(R.anim.anim_slider_right_in,
					                R.anim.anim_slider_left_out);
						} else {
						addClick(position);
						}
					} else{//付费观看
						Intent intent = new Intent(SalonListActivity.this, VipPayActivity.class);
						intent.putExtra("title", type_name.substring(0,
								type_name.length() - 1));
						intent.putExtra("image_url", videoCourses.get(position).getHead_image());
						intent.putExtra("lectureTitle", videoCourses.get(position).getLecture_title());
						intent.putExtra("look", videoCourses.get(position)
								.getLecture_click_count());
						intent.putExtra("price", videoCourses.get(position).getPrice());
						intent.putExtra("lecture_content_url", videoCourses
								.get(position).getLecture_content_url());
						intent.putExtra("lecture_id",
								videoCourses.get(position).getLecture_id());
						intent.putExtra("lecture_integral",
								videoCourses.get(position).getLecture_integral());
						intent.putExtra("lecture_type","2");//网页视频
						currenType = "2";
						currenPosition = position;
						Bundle bundle = new Bundle();
						VideoCourseInfo videoCourseInfo = videoCourses.get(position);
						bundle.putSerializable("VideoCourseInfo", videoCourseInfo);
						intent.putExtras(bundle);
						startActivity(intent);
						overridePendingTransition(R.anim.anim_slider_right_in,
				                R.anim.anim_slider_left_out);
					}
					
				}

			}

		});
	}
	
	private void playVideoInfo(final int position) {
		Log.e("培训视频列表播放地址：", videoCourses.get(position)
				.getVideo_local_name().toString());

		if ((NetUtil.isConnected(context) && NetUtil
				.isWifi(context))
				|| NetUtil.isConnected(context)
				&& !NetUtil.isWifi(context)
				&& sp.getInt("allow_net_local", 0) == 1) {
			if (Build.VERSION.SDK_INT >= 16) {

				Intent intent = new Intent(context,
						CoursesPlayActivity.class);
				intent.putExtra("title", type_name.substring(0,
						type_name.length() - 1));
				
				String count = videoCourses.get(position).getLecture_click_count();
				intent.putExtra("look", count);
				if(!TextUtils.isEmpty(count)&&!count.equals("")){
					int click_count = Integer.valueOf(count)+1;
					LogUtils.e("点击人数=============="+click_count);
					videoCourses.get(position).setLecture_click_count(click_count+"");
					adapterCourse.notifyDataSetChanged();
				}
				
				intent.putExtra("payState", "1");
				
				intent.putExtra("position", position);
				intent.putExtra("lecture_cat1", "");
				intent.putExtra("lecture_questions_id", "");
				intent.putExtra("lecture_cat3", "");
//				intent.putExtra("url_s", videoCourses.get(position)
//						.getVideo_local_name());
//				intent.putExtra("url_h", videoCourses.get(position)
//						.getVideo_local_name());
//				intent.putExtra("url_p", videoCourses.get(position)
//						.getVideo_local_name());
//
//				intent.putExtra("video_local_name", videoCourses
//						.get(position).getVideo_local_name());
				if(videoCourses.get(position).getPlay_urls().size() > 0){
					intent.putExtra("url_s", videoCourses.get(position).getPlay_urls().get(0).getUrl());
					intent.putExtra("url_h", videoCourses.get(position).getPlay_urls().get(0).getUrl());
					intent.putExtra("url_p", videoCourses.get(position).getPlay_urls().get(0).getUrl());
					intent.putExtra("video_local_name", videoCourses.get(position).getPlay_urls().get(0).getUrl());
				}else{
					intent.putExtra("url_s", "");
					intent.putExtra("url_h", "");
					intent.putExtra("url_p", "");
					intent.putExtra("video_local_name", "");
				}

				intent.putExtra("lecture_title",
						videoCourses.get(position)
								.getLecture_title());
				intent.putExtra("lecture_content_url", videoCourses
						.get(position).getLecture_content_url());
				intent.putExtra("lecture_id",
						videoCourses.get(position).getLecture_id());
				intent.putExtra("course_id", "");
				intent.putExtra("vheight_s", "9");
				intent.putExtra("vwidth_s", "16");
				intent.putExtra("lecture_click_count", videoCourses
						.get(position).getLecture_click_count());
				intent.putExtra("image_url",
						videoCourses.get(position).getImage_url());
				intent.putExtra("price", videoCourses.get(position)
						.getPrice());
				intent.putExtra("lecture_operate", "");
				intent.putExtra("course_pack_id", "");
				try {
					intent.putExtra("play_duration", "");
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
				Toast.makeText(context, "您当前系统版本过低,请升级到安卓4.1及以上",
						Toast.LENGTH_SHORT).show();
			}
		} else if (NetUtil.isConnected(context)
				&& sp.getInt("allow_net_local", 0) == 0
				&& !NetUtil.isWifi(context)) {
			Builder bd = new MyAlertDialog.Builder(context);
			bd.setTitle("网络提示")
					.setMessage(R.string.video_net_warn)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(
										DialogInterface arg0,
										int arg1) {
									arg0.dismiss();
									if (Build.VERSION.SDK_INT >= 16) {
										Intent intent = new Intent(
												context,
												CoursesPlayActivity.class);
										intent.putExtra(
												"title",
												type_name
														.substring(
																0,
																type_name
																		.length() - 1));
										
										String count = videoCourses.get(position).getLecture_click_count();
										intent.putExtra("look", count);
										if(!TextUtils.isEmpty(count)&&!count.equals("")){
											int click_count = Integer.valueOf(count)+1;
											LogUtils.e("点击人数=============="+click_count);
											videoCourses.get(position).setLecture_click_count(click_count+"");
											adapterCourse.notifyDataSetChanged();
										}
										
										intent.putExtra("payState", "1");
										intent.putExtra("position",
												position);
										intent.putExtra(
												"lecture_cat1", "");
										intent.putExtra(
												"lecture_questions_id",
												"");
										intent.putExtra(
												"lecture_cat3", "");
//										intent.putExtra(
//												"url_s",
//												videoCourses
//														.get(position)
//														.getVideo_local_name());
//										intent.putExtra(
//												"url_h",
//												videoCourses
//														.get(position)
//														.getVideo_local_name());
//										intent.putExtra(
//												"url_p",
//												videoCourses
//														.get(position)
//														.getVideo_local_name());
//
//										intent.putExtra(
//												"url_real",
//												videoCourses
//														.get(position)
//														.getVideo_local_name());
										
										if(videoCourses.get(position).getPlay_urls().size() > 0){
											intent.putExtra("url_s", videoCourses.get(position).getPlay_urls().get(0).getUrl());
											intent.putExtra("url_h", videoCourses.get(position).getPlay_urls().get(0).getUrl());
											intent.putExtra("url_p", videoCourses.get(position).getPlay_urls().get(0).getUrl());
											intent.putExtra("video_local_name", videoCourses.get(position).getPlay_urls().get(0).getUrl());
										}else{
											intent.putExtra("url_s", "");
											intent.putExtra("url_h", "");
											intent.putExtra("url_p", "");
											intent.putExtra("video_local_name", "");
										}

										intent.putExtra(
												"lecture_title",
												videoCourses
														.get(position)
														.getLecture_title());
										intent.putExtra(
												"lecture_content_url",
												videoCourses
														.get(position)
														.getLecture_content_url());
										intent.putExtra(
												"lecture_id",
												videoCourses
														.get(position)
														.getLecture_id());
										intent.putExtra(
												"course_id", "");
										// intent.putExtra("remark",
										// list.get(position).get("remark").toString());
										intent.putExtra(
												"vheight_s", "9");
										intent.putExtra("vwidth_s",
												"16");
										intent.putExtra(
												"lecture_click_count",
												videoCourses
														.get(position)
														.getLecture_click_count());
										intent.putExtra(
												"image_url",
												videoCourses
														.get(position)
														.getImage_url());
										intent.putExtra(
												"price",
												videoCourses.get(
														position)
														.getPrice());
										intent.putExtra(
												"lecture_operate",
												"");
										intent.putExtra(
												"course_pack_id",
												"");
										try {
											intent.putExtra(
													"play_duration",
													"");
										} catch (Exception e) {
											// TODO: handle
											// exception
										}

										if (sp.getBoolean(
												"logined", false)) {
											intent.putExtra(
													"learn_status",
													0);
											intent.putExtra(
													"my_duration",
													0);
										}
										editor.putInt(
												"allow_net_local",
												1);
										editor.commit();
										Constant.allow_net_other = true;
										context.startActivity(intent);
										overridePendingTransition(R.anim.anim_slider_right_in,
								                R.anim.anim_slider_left_out);
									} else {
										Toast.makeText(
												context,
												"您当前系统版本过低,请升级到安卓4.1及以上",
												Toast.LENGTH_SHORT)
												.show();
									}
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int whichButton) {
									dialog.dismiss();

								}
							}).show();
		} else if (!NetUtil.isConnected(context)) {
			Toast.makeText(context, "您当前无网络", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	/**
	 * 添加观看人数
	 */
	private void addClick(final int position) {
		String url_click = Constant.phpUrl
				+ "/wap/api.php?action=ADD_LECTURE_CLICK&tocken=" + mToken
				+ "&app_nonce=" + app_nonce + "&lecture_id=" + videoCourses.get(position).getLecture_id();
		Log.d("234abc", "点击地址" + url_click);

		NetHelper.get(url_click, new SimpleSingleBeanNetHandler<Captcha>(
				SalonListActivity.this) {

			@Override
			protected void onSuccess(Captcha bean) {
				try {
					int resule = Integer.parseInt(bean.result);
					switch (resule) {
					case 0:
						String lecture_click_count = videoCourses.get(position).getLecture_click_count();
						if(!TextUtils.isEmpty(lecture_click_count)&&lecture_click_count !=null){
							int clickCount = Integer.valueOf(lecture_click_count);
							videoCourses.get(position).setLecture_click_count(clickCount+1+"");
							adapterCourse.notifyDataSetChanged();
						} 
						 
						//非全屏播放
						Intent intent = new Intent(SalonListActivity.this,
								WebPlayVideoActivity.class);
						intent.putExtra("url", videoCourses.get(position)
								.getLecture_link());
						intent.putExtra("title",
								type_name.substring(0, type_name.length() - 1));
						
						//全屏播放
//						Intent intent = new Intent(SalonListActivity.this,
//								WebActivity.class);
//						intent.putExtra("catID", 12);
//						intent.putExtra("url", videoCourses.get(position)
//								.getLecture_link());
						
						startActivity(intent);
						overridePendingTransition(R.anim.anim_slider_right_in,
				                R.anim.anim_slider_left_out);
						break;
					case 99:
						editor.putBoolean("token_logined", false);
						editor.remove("mToken");
						editor.commit();
						break;
					default:

						break;
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}

			@Override
			protected void onError(int errorCode, String errorMsg) {
				// TODO Auto-generated method stub
				// super.onError(errorCode, errorMsg);
			}

		});
	}
	

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		if (hasMore) {
			startPage++;
			getInfo(startPage);
		} else {
			Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
			mPullToRefreshView.onFooterRefreshComplete();
		}

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		startPage = 1;
		videoCourses.clear();
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

	private void getTokenLocal() {
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(context).getToken();
		if (mToken != null) {
			loading.setVisibility(View.VISIBLE);
			videoCourses.clear();
			getInfo(startPage);
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}

	private void getInfo(int page) {
		String url_video_course_list = Constant.COURSE_LIST + "&tocken="
				+ mToken + "&app_nonce=" + app_nonce + "&page_no=" + page
				+ "&lecture_type=" + "&category_id=" + category_id;
		Log.e(TAG, "培训课程列表url=" + url_video_course_list);
		NetHelper.get(url_video_course_list,
				new SimpleSingleBeanNetHandler<Captcha>(context) {

					@Override
					protected void onSuccess(Captcha bean) {
						String result = bean.result;
						try {
							if (result.equals("0")) {
								String info = bean.info;
								JSONArray jsonArray = new JSONObject(info)
										.getJSONArray("data");
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject obj = (JSONObject) jsonArray
											.get(i);
									VideoCourseInfo videoCourse = GsonUtils
											.parseJSON(obj.toString(),
													VideoCourseInfo.class);
									videoCourses.add(videoCourse);
								}
								if (videoCourses.size() > 0) {
									tvNodata.setVisibility(View.GONE);
								} else {
									tvNodata.setVisibility(View.VISIBLE);
								}
								Log.e(TAG, "数据大小=" + videoCourses.size());
								if (jsonArray.length() >= pageSize) {
									hasMore = true;
								} else {
									hasMore = false;
								}
								adapterCourse.notifyDataSetChanged();

								if (startPage != 1) {
									mPullToRefreshView
											.onFooterRefreshComplete();
								} else {
									mPullToRefreshView
											.onHeaderRefreshComplete();
								}
							} else {
								ToastUtil.getInstance().showToast(
										SalonListActivity.this, bean.message);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

						loading.setVisibility(View.GONE);
					}

				});
	}
}
