package org.soshow.beautyedu.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.adapters.CoursesListAdapter;
import org.soshow.beautyedu.json.bean.CourseInfo;
import org.soshow.beautyedu.json.bean.CourseList;
import org.soshow.beautyedu.json.bean.LectureDetalils;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleMultiBeanNetHandler;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//import com.listviewdemo_imageanddata.MainActivity.MyTask;
/**
 * 标准课程列表页
 */
public class CoursesListTwoActivity extends Activity implements OnHeaderRefreshListener, OnFooterRefreshListener {

	private ListView list_record;
	private TextView title_name;
	private List<CourseList> list_displayRecord;
	private SharedPreferences sp;
	private String mToken;
	public static List<Map<String, Object>> listItems;
	public static CoursesListAdapter dra;
	private TextView list_no_record;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				try {
					listItems = new ArrayList<Map<String, Object>>();
					for (CourseList dr : list_displayRecord) {

						if (dr.course_pack_type != null && !dr.course_pack_type.equals("2")) {
							Map<String, Object> listItem = new HashMap<String, Object>();
							listItem.put("course_id", dr.course_id);
							listItem.put("course_pack_type", dr.course_pack_type);
							listItem.put("lecture_title", dr.title);
							Log.d("1221", "标题" + dr.title + "type" + dr.course_pack_type);
							listItem.put("course_pack_id", dr.course_pack_id);
							listItem.put("weight", dr.weight);
							listItem.put("lecture_id", dr.lecture_id);
							listItem.put("learn_status", dr.learn_status);
							listItem.put("my_duration", dr.my_duration);
							listItem.put("play_duration", dr.play_duration);
							// listItem.put("video_static", "0");
							// listItem.put("duration", null);
							// Log.d("123456", "状态---------" + dr.learn_status +
							// "时间----"+dr.my_duration);
							listItems.add(listItem);
							try {
//								if (CoursesPlayActivity.pva_instance != null
//										&& CoursesPlayActivity.course_pack_id != null
//										&& dr.course_pack_id.equals(CoursesPlayActivity.course_pack_id)
//										&& sp.getBoolean("logined", false)) {
//									CoursesPlayActivity.learn_status = Integer.parseInt(dr.learn_status);
//									CoursesPlayActivity.my_duration = Integer.parseInt(dr.my_duration);
//									Message message = CoursesPlayActivity.pva_instance.getHandler().obtainMessage();
//									message.what = 10;
//									message.sendToTarget();
//								}
							} catch (Exception e) {
								// TODO: handle exception
							}

						}
					}
					try {// 获取数据写入本地
						FileOutputStream outStream = new FileOutputStream(getCacheDir().toString()
								+ "/courses_list_cat1_id" + cat1_id + "_catq_id" + cat2_id + ".txt");
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
						objectOutputStream.writeObject(listItems);
						outStream.close();
					} catch (Exception e) {
						// TODO: handle exception

					}

					Log.d("1221", "数据加载");
					dra = new CoursesListAdapter(CoursesListTwoActivity.this, listItems);
					list_record.setAdapter(dra);

				} catch (Exception e) {
					// TODO: handle exception
				}

				break;
			case 2:
				if (LoginUtil.login_handlered) {
					Log.d("345abc", "播放记录获取新token");
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
						getInfo();
					} else {
						Toast.makeText(CoursesListTwoActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							Log.d("345abc", "播放记录循环等待");
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 2;
							msg_loop.sendToTarget();
						}
					}, 1000);

				}
				break;
			case 3:// 加载显示本地数据
				try {
					Log.d("1221", "加载显示本地数据");
					dra = new CoursesListAdapter(CoursesListTwoActivity.this, listItems);
					list_record.setAdapter(dra);
					for (Map<String, Object> map : listItems) {
						try {
							int type = Integer.parseInt(map.get("course_pack_type").toString());
							int id = Integer.parseInt(map.get("lecture_id").toString());
							switch (type) {
							case 1:
								try {
									Log.d("1221", "加载显示本地数据刷新前" + map.toString());
									FileInputStream freader = new FileInputStream(
											getCacheDir().toString() + "/course_" + id + ".txt");
									ObjectInputStream objectInputStream = new ObjectInputStream(freader);
									map = (Map<String, Object>) objectInputStream.readObject();
									objectInputStream.close();
									// map.clear();
									// dra.notifyDataSetChanged();
									// map = item;

									dra.notifyDataSetChanged();
									Log.d("1221", "加载显示本地数据正确刷新" + map.get("lecture_title"));

								} catch (Exception e) {
									Log.d("1221", "本地无微课信息" + e.toString());
									// TODO: handle exception
									NetHelper.get(Constant.phpUrl + "/wap/api.php?action=GET_LECTURE&lecture_id=" + id,
											new SimpleSingleBeanNetHandler<LectureDetalils>(
													CoursesListTwoActivity.this) {

										@Override
										protected void onSuccess(LectureDetalils bean) {
											// TODO Auto-generated method stub
											// Log.d("1221",
											// "获取的id为"+bean.lecture_id);
											try {
												Message message = handler.obtainMessage();
												message.obj = bean;
												message.what = 4;
												handler.sendMessageDelayed(message, 1000);
											} catch (Exception e) {

											}

										}

										@Override
										protected void onError(int errorCode, String errorMsg) {
											// TODO Auto-generated method stub
											// super.onError(errorCode,
											// errorMsg);
										}

									});
								}
								break;

							default:
								break;
							}
						} catch (Exception e) {
							// TODO: handle exception
							Log.d("1221", "本地读取错误" + e.toString());
						}

					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case 4:// 微课信息更新
				try {
					synchronized (this) {
						LectureDetalils bean = (LectureDetalils) msg.obj;
						// list_lecture.add(bean);
						Log.d("1221", "数据更新" + listItems);
						loop: for (Iterator<Map<String, Object>> it = listItems.iterator(); it.hasNext();) {

							Map<String, Object> listItem = it.next();
							// Log.d("1221","数据遍历");
							// Log.d("1221",
							// "数据遍历，当前id" + bean.lecture_id + "遍历id" +
							// listItem.get("lecture_id").toString());
							if (listItem.get("course_pack_type").toString().equals("1")
									&& listItem.get("lecture_id").toString().equals(bean.lecture_id)) {
								// Log.d("123456",
								// "匹配"+bean.lecture_id+bean.lecture_title);
								listItem.put("lecture_title", bean.lecture_title);
								listItem.put("lecture_click_count",
										Integer.parseInt(bean.lecture_click_count) + 30 + "");

								listItem.put("image_url", bean.head_image);
								listItem.put("lecture_id", bean.lecture_id);
								listItem.put("lecture_cat1", bean.lecture_cat1);
								listItem.put("lecture_cat3", bean.lecture_cat3);
								listItem.put("lecture_questions_id", bean.lecture_questions_id);
								listItem.put("url_s", bean.url_s);
								listItem.put("url_h", bean.url_h);
								listItem.put("url_p", bean.url_p);
								
								listItem.put("video_local_name", bean.video_local_name);
								
								listItem.put("lecture_content", bean.lecture_content);
								listItem.put("vheight_s", bean.vheight_s);
								listItem.put("vwidth_s", bean.vwidth_s);
								listItem.put("price", bean.price);
								// Log.d("1221", "价格为 "+dr.lecture_operate);
								listItem.put("lecture_operate", bean.lecture_operate);

								try {// 获取数据写入本地
									FileOutputStream outStream = new FileOutputStream(getCacheDir().toString()
											+ "/courses_list_cat1_id" + cat1_id + "_catq_id" + cat2_id + ".txt");
									ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
									objectOutputStream.writeObject(listItems);
									outStream.close();
								} catch (Exception e) {
									// TODO: handle exception

								}
								try {
									dra.notifyDataSetChanged();
									// dialog.hide();
								} catch (Exception e) {
									// TODO: handle exception
									Log.d("1221", "数据异常");
								}
								if(FragmentCoursesDirectory.fmt!=null){
									try {
										FragmentCoursesDirectory.mHasLoadedOnce=false;
										FragmentCoursesDirectory.fmt.lazyLoad();
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
								// break loop;
								break loop;
							}
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.d("1221", "更新异常" + e.toString());
				}
				Log.d("123321", "微课数据更新");
				break;
			case 5:// 课程学习记录
				try {
					List<CourseInfo> bean = (List<CourseInfo>) msg.obj;
					for (Iterator<Map<String, Object>> it = listItems.iterator(); it.hasNext();) {

						Map<String, Object> listItem = it.next();
						// Log.d("1221","数据遍历");
						// Log.d("1221",
						// "数据遍历，当前id" + bean.lecture_id + "遍历id" +
						// listItem.get("lecture_id").toString());
						for (Iterator<CourseInfo> ici = bean.iterator(); ici.hasNext();) {
							CourseInfo ci = ici.next();
							if (listItem.get("course_pack_type").toString().equals("1")
									&& listItem.get("course_pack_id").toString().equals(ci.course_pack_id)) {
								// Log.d("123456",
								// "匹配"+bean.lecture_id+bean.lecture_title);
								listItem.put("duration", ci.duration);

								listItem.put("video_static", ci.video_static);
								Log.d("111", "数据更新duration" + ci.duration + "      static" + ci.video_static);
								/*
								 * try {// 获取数据写入本地 FileOutputStream outStream =
								 * new FileOutputStream(getCacheDir().toString()
								 * + "/courses_list_cat1_id" + cat1_id +
								 * "_catq_id" + cat2_id + ".txt");
								 * ObjectOutputStream objectOutputStream = new
								 * ObjectOutputStream(outStream);
								 * objectOutputStream.writeObject(listItems);
								 * outStream.close(); } catch (Exception e) { //
								 * TODO: handle exception
								 * 
								 * }
								 */
								try {// 获取数据写入本地
									FileOutputStream outStream = new FileOutputStream(getCacheDir().toString()
											+ "/courses_list_cat1_id" + cat1_id + "_catq_id" + cat2_id + ".txt");
									ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
									objectOutputStream.writeObject(listItems);
									outStream.close();
								} catch (Exception e) {
									// TODO: handle exception

								}
								try {
									Log.d("111", "数据更新-------------------");
									dra.notifyDataSetChanged();
									// dialog.hide();
								} catch (Exception e) {
									// TODO: handle exception
									Log.d("1221", "数据异常");
								}
								// break loop;
								// break loop;
							}
						}

					}
					Log.d("123321", "播放进度更新");
				} catch (Exception e) {

				}

				break;
			case 6:// 微课播放进度获取失败
				try {
					for (Iterator<Map<String, Object>> it = listItems.iterator(); it.hasNext();) {
						Map<String, Object> listItem = it.next();
						listItem.put("video_static", "0");
						listItem.put("duration", "0");
					}
					dra.notifyDataSetChanged();
				} catch (Exception e) {

				}

				break;
			default:
				break;
			}
		}

	};
	PullToRefreshView mPullToRefreshView;
	private boolean list_cache = false;
	public static int cat1_id;
	public static int cat2_id;
	public static CoursesListTwoActivity clt_ins;
    public static String title;
	// @SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_courses_list_two);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_logininput);

		title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText(getIntent().getStringExtra("cat1_name"));
		title=getIntent().getStringExtra("cat1_name");
		list_record = (ListView) findViewById(R.id.list_record);

		list_no_record = (TextView) findViewById(R.id.list_no_record);

		sp = CoursesListTwoActivity.this.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		cat1_id = Integer.parseInt(getIntent().getStringExtra("cat1_id"));
		cat2_id = Integer.parseInt(getIntent().getStringExtra("cat2_id"));
		listItems = new ArrayList<Map<String, Object>>();
		getTokenLocal();
		clt_ins = this;
		
		// new MyTask().execute(PATH);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_search_logininput:

			finish();
			break;

		default:
			break;
		}

	}

	@SuppressWarnings("unchecked")
	public void getTokenLocal() {
		if (list_cache) {// 使用本地数据
			try {
				if (listItems == null || listItems.isEmpty()) {// 第一次加载本地数据
					FileInputStream freader = new FileInputStream(getCacheDir().toString() + "/courses_list_cat1_id"
							+ cat1_id + "_catq_id" + cat2_id + ".txt");
					ObjectInputStream objectInputStream = new ObjectInputStream(freader);
					listItems = (List<Map<String, Object>>) objectInputStream.readObject();
					objectInputStream.close();
				}

				if (listItems == null || listItems.isEmpty()) {// 加载数据为空
					try {// 删除无效文件
							// Toast.makeText(RecordActivity.this, "b删除缓存1",
							// Toast.LENGTH_SHORT).show();
						File file = new File(getCacheDir().toString() + "/courses_list_cat1_id" + cat1_id + "_catq_id"
								+ cat2_id + ".txt");
						file.delete();

					} catch (Exception e) {
						// TODO: handle exception
					}
					// 重新请求数据
					list_cache = false;
					// page_no = 1;
					mToken = new TokenManager(CoursesListTwoActivity.this).getToken();
					if (mToken != null) {
						getInfo();
					} else {
						LoginUtil.login_handlered = false;
						try {
							Message msg_loop1 = handler.obtainMessage();
							msg_loop1.what = 2;
							msg_loop1.sendToTarget();
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				} else {// 本地数据正常
					Message msg_loop = handler.obtainMessage();
					msg_loop.what = 3;
					msg_loop.sendToTarget();
				}

			} catch (Exception e) {// 本地文件获取错误，重新请求数据
				list_cache = false;
				mToken = new TokenManager(CoursesListTwoActivity.this).getToken();
				if (mToken != null) {
					getInfo();
				} else {
					LoginUtil.login_handlered = false;
					Message msg_loop = handler.obtainMessage();
					msg_loop.what = 2;
					msg_loop.sendToTarget();
				}
			}
		} else {// 直接网络请求
			mToken = new TokenManager(CoursesListTwoActivity.this).getToken();
			if (mToken != null) {
				getInfo();
			} else {
				LoginUtil.login_handlered = false;
				Message msg_loop = handler.obtainMessage();
				msg_loop.what = 2;
				msg_loop.sendToTarget();
			}
		}
	}

	private void getInfo() {
		try {
			final String app_nonce = StringUtil.getPhoneIMEI(CoursesListTwoActivity.this);
			String url_myrecord = Constant.phpUrl + "/wap/api.php?action=GET_COURSE_PACKAGE&cat1_id=" + cat1_id
					+ "&cat2_id=" + cat2_id + "&tocken=" + mToken + "&app_nonce=" + app_nonce;
			Log.e("123456", "培训课程列表url=====" + url_myrecord);
			// dialog.show();
			NetHelper.get(url_myrecord, new SimpleMultiBeanNetHandler<CourseList>(this) {
				@Override
				protected void onSuccess(List<CourseList> list) {
					try {
						list_displayRecord = list;

						if (list_no_record != null) {
							list_no_record.setVisibility(View.GONE);
						}

						Toast.makeText(CoursesListTwoActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
						mPullToRefreshView.onHeaderRefreshComplete();
						// }

						handler.sendEmptyMessage(1);

						for (CourseList cl : list) {
							try {
								int type = Integer.parseInt(cl.course_pack_type);
								switch (type) {
								case 1:
									Log.e("-----------------", "培训列表视频url==========" + Constant.phpUrl
											+ "/wap/api.php?action=GET_LECTURE&lecture_id=" + cl.lecture_id);
									NetHelper.get(
											Constant.phpUrl + "/wap/api.php?action=GET_LECTURE&lecture_id="
													+ cl.lecture_id,
											new SimpleSingleBeanNetHandler<LectureDetalils>(
													CoursesListTwoActivity.this) {

										@Override
										protected void onSuccess(LectureDetalils bean) {
											// TODO Auto-generated method stub
											// Log.d("1221",

											Message message = handler.obtainMessage();
											message.obj = bean;
											message.what = 4;
											handler.sendMessageDelayed(message, 1000);
										}

										@Override
										protected void onError(int errorCode, String errorMsg) {
											// TODO Auto-generated method stub
											// super.onError(errorCode,
											// errorMsg);
										}

									});
									break;

								default:
									break;
								}
							} catch (Exception e) {
								// TODO: handle exception
							}

						}
						/*
						 * if (sp.getBoolean("logined", false)) { Log.d("111",
						 * "数据获取地址" + Constant.phpUrl +
						 * "/wap/api.php?action=GET_MY_COURSE&course_id=" +
						 * list.get(0).course_id + "&tocken=" + mToken +
						 * "&app_nonce=" + app_nonce); NetHelper.get(
						 * Constant.phpUrl +
						 * "/wap/api.php?action=GET_MY_COURSE&course_id=" +
						 * list.get(0).course_id + "&tocken=" + mToken +
						 * "&app_nonce=" + app_nonce, new
						 * SimpleMultiBeanNetHandler<CourseInfo>(
						 * CoursesListTwoActivity.this) {
						 * 
						 * @Override protected void onSuccess(List<CourseInfo>
						 * list) { // TODO Auto-generated method stub
						 * Log.d("111", "数据获取成功"); try { Message message =
						 * handler.obtainMessage(); message.what = 5;
						 * message.obj = list; message.sendToTarget(); } catch
						 * (Exception e) { // TODO: handle exception }
						 * 
						 * }
						 * 
						 * @Override protected void onError(int errorCode,
						 * String errorMsg) { // TODO Auto-generated method stub
						 * // super.onError(errorCode, errorMsg); Log.d("111",
						 * "数据获取失败"); Message message = handler.obtainMessage();
						 * message.what = 6;
						 * 
						 * message.sendToTarget(); }
						 * 
						 * }); } else { Message message =
						 * handler.obtainMessage(); message.what = 6;
						 * message.sendToTarget(); }
						 */
					} catch (Exception e) {
						// TODO: handle exception
					}

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					// super.onError(errorCode, errorMsg);
					Log.d("345abc", "微课列表错误=" + errorMsg);
					try {

						JSONObject jo = new JSONObject(errorMsg);

						if (jo.optString("result").equals("2")) {
							list_no_record.setVisibility(View.VISIBLE);
							list_no_record.setText("当前无相关课程");
							if (listItems != null && dra != null) {
								listItems.clear();
								dra.notifyDataSetChanged();
							}
							Toast.makeText(CoursesListTwoActivity.this, "当前无相关课程", Toast.LENGTH_SHORT).show();
							try {// 网络请求成功，删除本地数据

								File file = new File(getCacheDir().toString() + "/courses_list_cat1_id" + cat1_id
										+ "_catq_id" + cat2_id + ".txt");
								file.delete();

								/// listItems = new ArrayList<Map<String,
								/// Object>>();

							} catch (Exception e) {
								// TODO: handle exception
							}
						} else {
							String mistake = jo.optString("info");
							Toast.makeText(CoursesListTwoActivity.this, mistake, Toast.LENGTH_SHORT).show();
						}

					} catch (Exception e) {
						// TODO: handle exception
						if (listItems != null && !listItems.isEmpty()) {// 网络通信失败，重新使用本地数据
							list_cache = false;
						} else if ((listItems == null || listItems.isEmpty())) {// 无缓存,无网页加载，且通信失败
							try {
								list_no_record.setVisibility(View.VISIBLE);
								list_no_record.setText("通信失败");
								listItems.clear();
								dra.notifyDataSetChanged();
							} catch (Exception e2) {
								// TODO: handle exception
							}

						}
						Toast.makeText(CoursesListTwoActivity.this, "通信失败，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();

					}

					/// if (list_cache) {
					// page_no--;
					/// mPullToRefreshView.onFooterRefreshComplete();
					/// } else {
					mPullToRefreshView.onHeaderRefreshComplete();
					/// }
				}

			});
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		// page_no++;
		// getTokenLocal();
		mPullToRefreshView.onFooterRefreshComplete();
		Toast.makeText(CoursesListTwoActivity.this, "无更多课程", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		list_cache = false;// 重新请求网络数据
		// page_no = 1;
		getTokenLocal();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

		if (dra != null) {
			try {
				
				dra = null;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		// listItems = null;
		if (listItems != null) {
			try {
				listItems.clear();
				listItems = null;
				/// list_record = null;
				/// dra = null;
				// if (dialog != null) {
				// dialog.dismiss();
				// }
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		try {
			handler.removeCallbacksAndMessages(null);
			// handler = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
		clt_ins = null;
		super.finish();

		// MyApplication.list_lecture.clear();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub

		super.onResume();
	}

}
