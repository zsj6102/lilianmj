package org.soshow.beautyedu.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.adapters.DisplayRecordNewAdapter;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.LectureList;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleMultiBeanNetHandler;
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
import android.content.SharedPreferences.Editor;
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
 * 播放记录
 */
public class RecordActivity extends Activity implements OnHeaderRefreshListener, OnFooterRefreshListener {
	// private Intent intent;
	private ListView list_record;

	// private Dialog dialog;
	// private SearchView search_view;
	// private List<Map<String, Object>> list = new ArrayList<Map<String,
	// Object>>();
	// private static final String PATH =
	// "http://phone.manle.com/yaodian.php?mod=yy_list_by_city&q=%E6%B5%8E%E5%8D%97&start=0";
	private TextView title_name;
	// private int count = 0;
	private List<LectureList> list_displayRecord;
	// 输入框
	// private InputMethodManager imm;
	private SharedPreferences sp;
	private Editor editor;
	private String mToken;
	private int page_no = 1;
	public static List<Map<String, Object>> listItems;
	private List<Map<String, Object>> listItems_cache = new ArrayList<Map<String, Object>>();
	public static DisplayRecordNewAdapter dra;
	private TextView list_no_record;
	private boolean isrefreshing = false;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				try {
					if (page_no == 1) {
						listItems = new ArrayList<Map<String, Object>>();
						// MyApplication.list_lecture.clear();
					}
					for (LectureList dr : list_displayRecord) {
						if (dr.play_urls != null) {
							Map<String, Object> listItem = new HashMap<String, Object>();
							listItem.put("lecture_title", dr.lecture_title);
							listItem.put("lecture_click_count", Integer.parseInt(dr.lecture_click_count) + 30 + "");
							listItem.put("image_url", dr.head_image);
							listItem.put("lecture_id", dr.lecture_id);
							listItem.put("lecture_cat1", dr.lecture_cat1);
							listItem.put("lecture_cat3", dr.lecture_cat3);
							listItem.put("lecture_questions_id", dr.lecture_questions_id);
							listItem.put("url_s", dr.url_s);
							listItem.put("url_h", dr.url_h);
							listItem.put("url_p", dr.url_p);
							Log.e("dr.video_local_name=="+dr.video_local_name, "");
							listItem.put("video_local_name", dr.video_local_name);
							listItem.put("lecture_content_url", dr.lecture_content_url);
							listItem.put("vheight_s", dr.vheight_s);
							listItem.put("vwidth_s", dr.vwidth_s);
							listItem.put("price", dr.price);
							// Log.d("1221", "价格为 "+dr.lecture_operate);
							listItem.put("lecture_operate", dr.lecture_operate);
							try {// 获取数据写入本地
								FileOutputStream outStream = new FileOutputStream(
										getCacheDir().toString() + "/lecturl_click" + dr.lecture_id + ".txt");
								ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
								Map<String, String> map = new HashMap<String, String>();
								map.put("lecture_click_count", (Integer.parseInt(dr.lecture_click_count) + 30) + "");
								objectOutputStream.writeObject(map);
								outStream.close();
							} catch (Exception e) {
								// TODO: handle exception

							}
							listItems.add(listItem);
						}
					}
					// Constant.refresh_main=true;
					try {// 获取数据写入本地
						FileOutputStream outStream = new FileOutputStream(
								getCacheDir().toString() + "/record_list.txt");
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
						objectOutputStream.writeObject(listItems);
						outStream.close();
					} catch (Exception e) {
						// TODO: handle exception

					}
					if (page_no == 1) {
						dra = new DisplayRecordNewAdapter(RecordActivity.this, listItems);
						list_record.setAdapter(dra);

					} else {

						try {
							dra.notifyDataSetChanged();
							// dialog.hide();
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
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
						isrefreshing = false;
						Toast.makeText(RecordActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
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

					String click;
					list_no_record.setVisibility(View.GONE);
					if (page_no == 1) {
						listItems = new ArrayList<Map<String, Object>>();
						// MyApplication.list_lecture.clear();
					}
					if (listItems_cache.size() > ((page_no - 1) * 10)) {// 当前页仍有数据
						if (listItems_cache.size() - ((page_no - 1) * 10) >= 10) {// 当前页数据数量足够10个
							for (int i = 0; i < 10; i++) {
								Map<String, Object> listItem = listItems_cache.get((page_no - 1) * 10 + i);
								Log.d("1221", "执行到此" + listItem.get("lecture_title"));
								click = getClickCount(listItem.get("lecture_id").toString());
								if (click != null) {

									listItem.put("lecture_click_count", click);
								}
								listItems.add(listItem);

							}
							if (page_no != 1) {
								Toast.makeText(RecordActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
							}
						} else {// 当前页数据不够10个
							for (int i = 0; i < listItems_cache.size() - ((page_no - 1) * 10); i++) {
								Map<String, Object> listItem = listItems_cache.get((page_no - 1) * 10 + i);
								click = getClickCount(listItem.get("lecture_id").toString());
								if (click != null) {
									listItem.put("lecture_click_count", click);
								}
								listItems.add(listItem);

							}
							if (page_no != 1) {
								Toast.makeText(RecordActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
							}
						}
					} else {// 本地无更多数据
						if (page_no != 1) {
							Toast.makeText(RecordActivity.this, "无更多播放记录，请下拉刷新获取更多", Toast.LENGTH_SHORT).show();
							page_no--;
							mPullToRefreshView.onFooterRefreshComplete();
						}
					}

					if (page_no == 1 && dra == null) {
						dra = new DisplayRecordNewAdapter(RecordActivity.this, listItems);
						list_record.setAdapter(dra);
						list_cache = false;
						isrefreshing = true;
						getTokenLocal();

					} else {
						try {
							dra.notifyDataSetChanged();
							mPullToRefreshView.onFooterRefreshComplete();// 结束刷新
						} catch (Exception e) {
							// TODO: handle exception
							Log.d("1221", "执行到此异常2  " + e.toString());
						}

					}
				} catch (Exception e) {
					// TODO: handle exception
					Log.d("1221", "执行到此异常1  " + e.toString());
					list_cache = false;
					page_no = 1;
					// isrefreshing = true;
					getTokenLocal();
				}

				break;
			default:
				break;
			}
		}

	};
	PullToRefreshView mPullToRefreshView;
	private boolean list_cache = true;

	// @SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_record);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_logininput);
		// getLayoutInflater().inflate(R.layout.titlebar_search,null);

		// search_view = (SearchView) findViewById(R.id.search_view);
		// search_result = (TextView) findViewById(R.id.search_result);
		// imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText("播放记录");
		list_record = (ListView) findViewById(R.id.list_record);
		// dialog = ProgressDialogUtil.createLoadingDialog(RecordActivity.this,
		// null, true, false);
		list_no_record = (TextView) findViewById(R.id.list_no_record);
		sp = RecordActivity.this.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		getTokenLocal();

		// new MyTask().execute(PATH);
	}

	private String getClickCount(String id) {
		try {
			FileInputStream freader = new FileInputStream(getCacheDir().toString() + "/lecturl_click" + id + ".txt");
			ObjectInputStream objectInputStream = new ObjectInputStream(freader);
			Map<String, String> map = (Map<String, String>) objectInputStream.readObject();
			objectInputStream.close();
			return map.get("lecture_click_count");
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}

	}

	public void onClick(View v) {
		// intent = new Intent(SearchActivity.this, MainActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		// startActivity(intent);
		switch (v.getId()) {
		case R.id.back_search_logininput:

			finish();
			break;

		default:
			break;
		}

	}

	@SuppressWarnings("unchecked")
	private void getTokenLocal() {

		if (list_cache) {// 使用本地数据
			try {
				if (listItems_cache == null || listItems_cache.isEmpty()) {// 第一次加载本地数据
					FileInputStream freader = new FileInputStream(getCacheDir().toString() + "/record_list.txt");
					ObjectInputStream objectInputStream = new ObjectInputStream(freader);
					listItems_cache = (List<Map<String, Object>>) objectInputStream.readObject();
					objectInputStream.close();
				}

				if (listItems_cache == null || listItems_cache.isEmpty()) {// 加载数据为空
					try {// 删除无效文件
							// Toast.makeText(RecordActivity.this, "b删除缓存1",
							// Toast.LENGTH_SHORT).show();
						File file = new File(getCacheDir().toString() + "/record_list.txt");
						file.delete();

					} catch (Exception e) {
						// TODO: handle exception
					}
					// 重新请求数据
					list_cache = false;
					page_no = 1;
					mToken = new TokenManager(RecordActivity.this).getToken();
					if (mToken != null) {
						getInfo();
					} else {
						LoginUtil.login_handlered = false;
						Message msg_loop1 = handler.obtainMessage();
						msg_loop1.what = 2;
						msg_loop1.sendToTarget();
					}
				} else {// 本地数据正常

					Message msg_loop = handler.obtainMessage();
					msg_loop.what = 3;
					msg_loop.sendToTarget();
				}

			} catch (Exception e) {// 本地文件获取错误，重新请求数据
				list_cache = false;
				mToken = new TokenManager(RecordActivity.this).getToken();
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
			mToken = new TokenManager(RecordActivity.this).getToken();
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
			String app_nonce = StringUtil.getPhoneIMEI(RecordActivity.this);
			String url_myrecord = Constant.phpUrl + "/wap/api.php?action=GET_MY_LECTURE&tocken=" + mToken
					+ "&app_nonce=" + app_nonce + "&page_no=" + page_no;
			Log.e("345abc", "播放记录url==========        " + url_myrecord);
			// dialog.show();
			NetHelper.get(url_myrecord, new SimpleMultiBeanNetHandler<LectureList>(this) {
				@Override
				protected void onSuccess(List<LectureList> list) {
					// try {//网络请求成功，删除本地数据
					// File file = new File(getCacheDir().toString() +
					// "/record_list.txt");
					// file.delete();
					// listItems_cache = new ArrayList<Map<String, Object>>();

					// } catch (Exception e) {
					// TODO: handle exception
					// }
					isrefreshing = false;
					list_displayRecord = list;
					// Log.d("345abc", "获取的image_url=" +
					// list.get(0).head_image);
					if (list_no_record != null) {
						list_no_record.setVisibility(View.GONE);
					}
					if (page_no != 1) {
						Toast.makeText(RecordActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
						mPullToRefreshView.onFooterRefreshComplete();
					} else {

						Toast.makeText(RecordActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
						mPullToRefreshView.onHeaderRefreshComplete();
					}
					handler.sendEmptyMessage(1);

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					// super.onError(errorCode, errorMsg);
					Log.d("345abc", "播放记录错误=" + errorMsg);
					isrefreshing = false;
					try {

						JSONObject jo = new JSONObject(errorMsg);

						if (jo.optString("result").equals("2") && page_no != 1) {
							Toast.makeText(RecordActivity.this, "无更多播放记录", Toast.LENGTH_SHORT).show();
						} else if (jo.optString("result").equals("2") && page_no == 1) {
							list_no_record.setVisibility(View.VISIBLE);
							list_no_record.setText("您当前无播放记录");
							if (listItems != null && dra != null) {
								listItems.clear();
								dra.notifyDataSetChanged();
							}
							Toast.makeText(RecordActivity.this, "您当前无播放记录", Toast.LENGTH_SHORT).show();
							try {// 网络请求成功，删除本地数据

								File file = new File(getCacheDir().toString() + "/record_list.txt");
								file.delete();
								listItems_cache = new ArrayList<Map<String, Object>>();

							} catch (Exception e) {
								// TODO: handle exception
							}
						} else if (jo.optString("result").equals("99")) {
							editor.putBoolean("token_logined", false);
							editor.remove("mToken");
							editor.commit();
						} else if (jo.optString("result").equals("1")) {
							if (sp.getBoolean("logined", false)) {
								Toast.makeText(RecordActivity.this, "网络错误，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
								editor.putBoolean("token_logined", false);
								editor.putBoolean("logined", false);
								MyApplication.logined = false;
								new LoginUtil(RecordActivity.this, 20);
							}
						} else {
							String mistake = jo.optString("info");
							Toast.makeText(RecordActivity.this, mistake, Toast.LENGTH_SHORT).show();
						}
						// try {// 网络请求成功，删除本地数据
						// Toast.makeText(RecordActivity.this, "d删除缓存",
						// Toast.LENGTH_SHORT).show();
						// File file = new File(getCacheDir().toString() +
						// "/record_list.txt");
						// file.delete();
						// listItems_cache = new ArrayList<Map<String,
						// Object>>();

						// } catch (Exception e) {
						// TODO: handle exception
						// }
					} catch (Exception e) {
						// TODO: handle exception
						if (listItems_cache != null && !listItems_cache.isEmpty()) {// 网络通信失败，重新使用本地数据
							list_cache = true;
						} else if ((listItems_cache == null || listItems_cache.isEmpty())
								&& (listItems == null || listItems.isEmpty())) {// 无缓存,无网页加载，且通信失败
							try {
								list_no_record.setVisibility(View.VISIBLE);
								list_no_record.setText("通信失败");
								listItems.clear();
								dra.notifyDataSetChanged();
							} catch (Exception e2) {
								// TODO: handle exception
							}

						}
						Toast.makeText(RecordActivity.this, "通信失败，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
						// if (page_no == 1 && list_cache) {// 网络刷新失败，重新使用本地数据
						// Message msg_loop = handler.obtainMessage();
						// msg_loop.what = 3;
						// msg_loop.sendToTarget();
						// }
					}
					// dialog.hide();
					if (page_no != 1) {
						page_no--;
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
		// TODO Auto-generated method stub
		if (!isrefreshing) {
			page_no++;
			getTokenLocal();
		} else {
			Toast.makeText(RecordActivity.this, "正在更新数据", Toast.LENGTH_SHORT).show();
			mPullToRefreshView.onFooterRefreshComplete();
		}

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		if (!isrefreshing) {
			list_cache = false;// 重新请求网络数据
			page_no = 1;
			getTokenLocal();
		} else {
			Toast.makeText(RecordActivity.this, "正在更新数据", Toast.LENGTH_SHORT).show();
			mPullToRefreshView.onHeaderRefreshComplete();
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		dra = null;
		listItems = null;
		try {
			page_no = 1;
			listItems_cache = null;
			listItems = null;
			list_record = null;
			dra = null;
			// if (dialog != null) {
			// dialog.dismiss();
			// }
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			handler.removeCallbacksAndMessages(null);
			// handler = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.finish();

		// MyApplication.list_lecture.clear();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// if(listItems!=null&&!listItems.isEmpty()&&dra!=null&&list_record!=null&&dra!=null){
		// Toast.makeText(RecordActivity.this,
		// "更新数据"+listItems.get(0).get("lecture_title").toString(),
		// Toast.LENGTH_SHORT).show();
		// dra.notifyDataSetChanged();
		// }
		super.onResume();
	}

}
