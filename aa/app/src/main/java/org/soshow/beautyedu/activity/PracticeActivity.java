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
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.MyPractice;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleMultiBeanNetHandler;
import org.soshow.beautyedu.json.utils.NetUtil;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.Encryption;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TokenManager;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
//import com.listviewdemo_imageanddata.MainActivity.MyTask;
/**
 * 我的练习
 */
public class PracticeActivity extends BaseActivity implements OnHeaderRefreshListener, OnFooterRefreshListener {
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
	private List<MyPractice> list_practice;
	// private InputMethodManager imm;
	private SharedPreferences sp;
	private Editor editor;
	// 更新UI
	private String mToken;
	private TextView list_no_record;
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
					}
					for (MyPractice mp : list_practice) {

						// MyPractice mp = list_practice.get(i);
						Map<String, Object> listItem = new HashMap<String, Object>();
						if (!mp.cat1_name.equals("null") && !mp.cat2_name.equals("null")) {

							listItem.put("cat_name", mp.cat1_name + mp.cat2_name);
							listItem.put("paper_id", mp.paper_id);
							if (!mp.finish_time.startsWith("0000")) {
								listItem.put("date", mp.finish_time);
								listItem.put("finished", "1");
							} else if (!mp.last_exercise_time.equals("null")) {
								listItem.put("date", mp.last_exercise_time);
								listItem.put("cat1_id", mp.cat1_id);
								listItem.put("cat2_id", mp.cat2_id);
								listItem.put("finished", "0");
							} else {
								listItem.put("date", mp.last_exercise_time);
								listItem.put("cat1_id", mp.cat1_id);
								listItem.put("cat2_id", mp.cat2_id);
								listItem.put("finished", "0");
							}
							// Log.d("345abc", "date=" + mp.add_time);
							listItems.add(listItem);
						}
					}
					try {// 获取数据写入本地
						FileOutputStream outStream = new FileOutputStream(
								getCacheDir().toString() + "/mypractice_list.txt");
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
						objectOutputStream.writeObject(listItems);
						outStream.close();
					} catch (Exception e) {
						// TODO: handle exception

					}
					if (page_no == 1) {
						sa = new SimpleAdapter(PracticeActivity.this, listItems, R.layout.list_my_practice,
								new String[] { "cat_name", "date" },
								new int[] { R.id.course_practice, R.id.course_date }) {
							@Override
							public View getView(final int position, View convertView, ViewGroup parent) {
								// TODO Auto-generated method stub
								if (convertView == null) {
									convertView = View.inflate(PracticeActivity.this, R.layout.list_my_practice, null);
								}
								final Button read_report = (Button) convertView.findViewById(R.id.read_report);
								final String finished = listItems.get(position).get("finished").toString();
								if (finished.equals("0")) {
									read_report.setText("继续练习");
									read_report.setBackgroundResource(R.drawable.captcha_btn_selector);
								} else {
									read_report.setText("查看报告");
									read_report.setBackgroundResource(R.drawable.register_btn_selector);
								}
								read_report.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										if (NetUtil.isConnected(PracticeActivity.this)) {
											String paper_id = listItems.get(position).get("paper_id").toString();
											Intent intent = new Intent(PracticeActivity.this, WebActivity.class);
											intent.putExtra("catID", 5);
											intent.putExtra("paper_id", paper_id);
											intent.putExtra("finished", finished);
											if (finished.equals("0")) {
												intent.putExtra("cat1_id",
														listItems.get(position).get("cat1_id").toString());
												intent.putExtra("cat2_id",
														listItems.get(position).get("cat2_id").toString());
											}
											startActivity(intent);
										} else {
											Toast.makeText(PracticeActivity.this, "您当前无网络连接", Toast.LENGTH_SHORT)
													.show();
										}
										// String url_my_practice =
										// Constant.phpUrl
										// +
										// "/wap/api.php?action=GET_CAT&id=";
									}
								});
								return super.getView(position, convertView, parent);

							}

						};
						list_record.setAdapter(sa);
						// dialog.hide();
					} else {
						sa.notifyDataSetChanged();
						// dialog.hide();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				break;
			case 2:
				if (LoginUtil.login_handlered) {
					Log.d("345abc", "练习获取新token");
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
						getInfo();
					} else {
						Toast.makeText(PracticeActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
							Log.d("345abc", "我的练习循环等待");
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 2;
							msg_loop.sendToTarget();
						}
					}, 1000);

				}
				break;
			case 3:
				try {
					list_no_record.setVisibility(View.GONE);
					if (page_no == 1) {
						listItems = new ArrayList<Map<String, Object>>();
						// MyApplication.list_lecture.clear();
					}
					if (listItems_cache.size() > ((page_no - 1) * 10)) {// 当前页仍有数据
						if (listItems_cache.size() - ((page_no - 1) * 10) >= 10) {// 当前页数据数量足够10个
							for (int i = 0; i < 10; i++)

							{
								Map<String, Object> listItem = listItems_cache.get((page_no - 1) * 10 + i);
								listItems.add(listItem);

							}
							if (page_no != 1)

							{
								Toast.makeText(PracticeActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
							}
						} else {// 当前页数据不够10个
							for (

							int i = 0; i < listItems_cache.size() - ((page_no - 1) * 10); i++)

							{
								Map<String, Object> listItem = listItems_cache.get((page_no - 1) * 10 + i);
								listItems.add(listItem);

							}
							if (page_no != 1)

							{
								Toast.makeText(PracticeActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
							}
						}
					} else {// 本地无更多数据
						if (page_no != 1) {
							Toast.makeText(PracticeActivity.this, "无更多练习记录,请下拉刷新获取更多", Toast.LENGTH_SHORT).show();
							page_no--;
							mPullToRefreshView.onFooterRefreshComplete();
						}
					}

					if (page_no == 1 && sa == null) {
						sa = new SimpleAdapter(PracticeActivity.this, listItems, R.layout.list_my_practice,
								new String[] { "cat_name", "date" },
								new int[] { R.id.course_practice, R.id.course_date }) {

							@Override
							public View getView(final int position, View convertView, ViewGroup parent) {
								// TODO Auto-generated method stub
								if (convertView == null) {
									convertView = View.inflate(PracticeActivity.this, R.layout.list_my_practice, null);
								}
								final Button read_report = (Button) convertView.findViewById(R.id.read_report);
								final String finished = listItems.get(position).get("finished").toString();
								if (finished.equals("0")) {
									read_report.setText("继续练习");
									read_report.setBackgroundResource(R.drawable.captcha_btn_selector);
								} else {
									read_report.setText("查看报告");
									read_report.setBackgroundResource(R.drawable.register_btn_selector);
								}
								read_report.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										if (NetUtil.isConnected(PracticeActivity.this)) {
											String paper_id = listItems.get(position).get("paper_id").toString();
											Intent intent = new Intent(PracticeActivity.this, WebActivity.class);
											intent.putExtra("catID", 5);
											intent.putExtra("paper_id", paper_id);
											intent.putExtra("finished", finished);
											if (finished.equals("0")) {
												intent.putExtra("cat1_id",
														listItems.get(position).get("cat1_id").toString());
												intent.putExtra("cat2_id",
														listItems.get(position).get("cat2_id").toString());
											}
											startActivity(intent);
										} else {
											Toast.makeText(PracticeActivity.this, "您当前无网络连接", Toast.LENGTH_SHORT)
													.show();
										}
										// String url_my_practice =
										// Constant.phpUrl
										// +
										// "/wap/api.php?action=GET_CAT&id=";
									}
								});
								return super.getView(position, convertView, parent);

							}

						};
						list_record.setAdapter(sa);

					} else {
						try {
							sa.notifyDataSetChanged();
							mPullToRefreshView.onFooterRefreshComplete();// 结束刷新
						} catch (

						Exception e)

						{
							// TODO: handle exception
						}

					}
				} catch (

				Exception e)

				{
					// TODO: handle exception
				}
				break;
			default:
				break;
			}
		}

	};

	// 静态实例
	public static PracticeActivity pratice_instance;
	PullToRefreshView mPullToRefreshView;
	private int page_no = 1;
	private List<Map<String, Object>> listItems;
	private SimpleAdapter sa;
	private boolean list_cache = false;
	private List<Map<String, Object>> listItems_cache = new ArrayList<Map<String, Object>>();

	// @SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_record);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_logininput);
		// getLayoutInflater().inflate(R.layout.titlebar_search,null);
		pratice_instance = this;
		// search_view = (SearchView) findViewById(R.id.search_view);
		// search_result = (TextView) findViewById(R.id.search_result);
		// imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText("我的练习");
		list_record = (ListView) findViewById(R.id.list_record);
		list_no_record = (TextView) findViewById(R.id.list_no_record);
		// dialog =
		// ProgressDialogUtil.createLoadingDialog(PracticeActivity.this, null,
		// true, false);

		sp = PracticeActivity.this.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		getTokenLocal();
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
		// mToken = new TokenManager(PracticeActivity.this).getToken();
		// if (mToken != null) {
		// getInfo();
		// } else {
		// LoginUtil.login_handlered = false;
		// Message msg_loop = handler.obtainMessage();
		// msg_loop.what = 2;
		// msg_loop.sendToTarget();
		// }
		if (list_cache) {// 使用本地数据
			try {
				if (listItems_cache == null || listItems_cache.isEmpty()) {// 第一次加载本地数据
					FileInputStream freader = new FileInputStream(getCacheDir().toString() + "/mypractice_list.txt");
					ObjectInputStream objectInputStream = new ObjectInputStream(freader);
					listItems_cache = (List<Map<String, Object>>) objectInputStream.readObject();
					objectInputStream.close();
				}

				if (listItems_cache == null || listItems_cache.isEmpty()) {// 加载数据为空
					try {// 删除无效文件
							// Toast.makeText(getActivity(), "s删除1",
							// Toast.LENGTH_SHORT).show();
						File file = new File(getCacheDir().toString() + "/mypractice_list.txt");
						file.delete();

					} catch (Exception e) {
						// TODO: handle exception
					}
					// 重新请求数据
					list_cache = false;
					page_no = 1;
					mToken = new TokenManager(PracticeActivity.this).getToken();
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
				mToken = new TokenManager(PracticeActivity.this).getToken();
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
			mToken = new TokenManager(PracticeActivity.this).getToken();
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
			String mobile = Encryption.desEncryptd(Constant.code_local, Constant.code_local,
					sp.getString("user_name", null));
			String app_nonce = StringUtil.getPhoneIMEI(PracticeActivity.this);
			String url_mypractice = Constant.phpUrl + "/wap/api.php?action=GET_MY_EXERCISES&app_nonce=" + app_nonce
					+ "&mobile=" + mobile + "&tocken=" + mToken + "&page_no=" + page_no;
			Log.d("345abc", "url_praitice=" + url_mypractice);
			// dialog.show();
			NetHelper.get(url_mypractice, new SimpleMultiBeanNetHandler<MyPractice>(this) {
				@Override
				protected void onSuccess(List<MyPractice> list) {
					// for (MyPractice m : list) {
					// Log.d("345abc", m.cat1_name + m.cat2_name);
					// }
					list_practice = list;
					if (list_no_record != null) {
						list_no_record.setVisibility(View.GONE);
					}
					// Log.d("345abc", "获取的联系信息=" + list.get(0).cat1_id);
					Message msg = new Message();
					msg.what = 1;
					handler.handleMessage(msg);

					if (page_no != 1) {
						Toast.makeText(PracticeActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
						mPullToRefreshView.onFooterRefreshComplete();
					} else {
						Toast.makeText(PracticeActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
						mPullToRefreshView.onHeaderRefreshComplete();
					}

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					// super.onError(errorCode, errorMsg);
					Log.d("345abc", "我的练习错误=" + errorMsg);
					try {
						JSONObject jo = new JSONObject(errorMsg);

						if (jo.optString("result").equals("2") && page_no != 1) {
							Toast.makeText(PracticeActivity.this, "无更多练习记录", Toast.LENGTH_SHORT).show();
						} else if (jo.optString("result").equals("2") && page_no == 1) {

							list_no_record.setVisibility(View.VISIBLE);
							Log.d("345abc", "JSON转换成功" + jo.toString());
							list_no_record.setText("您当前无练习记录");
							if (listItems != null && sa != null) {
								listItems.clear();
								sa.notifyDataSetChanged();

							}
							Toast.makeText(PracticeActivity.this, "您当前无练习记录", Toast.LENGTH_SHORT).show();
							try {// 网络请求成功，删除本地数据
									// Toast.makeText(getActivity(), "s删除缓存",
									// Toast.LENGTH_SHORT).show();
								File file = new File(getCacheDir().toString() + "/mypractice_list.txt");
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
								Toast.makeText(PracticeActivity.this, "网络错误，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
								editor.putBoolean("token_logined", false);
								editor.putBoolean("logined", false);
								MyApplication.logined = false;
								new LoginUtil(PracticeActivity.this, 20);
							}
						} else {
							String mistake = jo.optString("info");
							Toast.makeText(PracticeActivity.this, mistake, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						// TODO: handle exception
						Log.d("345abc", "JSON转换失败" + e.toString());
						if (listItems_cache != null && !listItems_cache.isEmpty()) {// 网络通信失败，重新使用本地数据
							list_cache = true;
						} else if ((listItems_cache == null || listItems_cache.isEmpty())
								&& (listItems == null || listItems.isEmpty())) {// 无缓存且通信失败
							try {
								list_no_record.setVisibility(View.VISIBLE);
								list_no_record.setText("通信失败");
								listItems.clear();
								sa.notifyDataSetChanged();
							} catch (Exception e2) {
								// TODO: handle exception
							}

						}
						Toast.makeText(PracticeActivity.this, "通信失败，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
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
		page_no++;
		getTokenLocal();
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		list_cache = false;
		page_no = 1;
		getTokenLocal();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		// try {
		// if (dialog != null) {
		// dialog.dismiss();
		// }
		// } catch (Exception e) {
		// TODO: handle exception
		// }
		listItems = null;
		sa = null;
		try {
			handler.removeCallbacksAndMessages(null);
			// handler = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.finish();

	}

}
