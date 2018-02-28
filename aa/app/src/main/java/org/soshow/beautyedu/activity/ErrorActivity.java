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
import org.soshow.beautyedu.json.bean.MyError;
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
//import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
//import com.listviewdemo_imageanddata.MainActivity.MyTask;
/**
 * 我的错题
 */
public class ErrorActivity extends Activity implements OnHeaderRefreshListener, OnFooterRefreshListener {
	@SuppressWarnings("unused")
	private Intent intent;
	private ListView list_record;

	// private Dialog dialog;
	// private SearchView search_view;
	// private List<Map<String, Object>> list = new ArrayList<Map<String,
	// Object>>();
	// private static final String PATH =
	// "http://phone.manle.com/yaodian.php?mod=yy_list_by_city&q=%E6%B5%8E%E5%8D%97&start=0";
	private TextView title_name;
	// private int count = 0;
	// 输入框
	// private InputMethodManager imm;
	private List<MyError> list_practice;
	// private InputMethodManager imm;
	private SharedPreferences sp;
	private Editor editor;
	// private TextView pull_to_refresh_updated_at;
	// 静态实例
	public static ErrorActivity error_instance;
	// 更新UI
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
					for (MyError mp : list_practice) {
						// MyError mp = list_practice.get(i);
						Map<String, Object> listItem = new HashMap<String, Object>();

						if (!mp.cat1_name.equals("null") && !mp.cat2_name.equals("null")) {
							// Toast.makeText(ErrorActivity.this, "加载",
							// Toast.LENGTH_SHORT).show();
							listItem.put("paper_id", mp.paper_id);
							listItem.put("add_time", mp.add_time);
							listItem.put("cat_name", mp.cat1_name + mp.cat2_name);

							listItem.put("error_quest_count", "(" + mp.error_quest_count + "道" + ")");

							// Log.d("345abc", "error_quest_count=" +
							// mp.add_time);
							listItems.add(listItem);
						}
					}
					try {// 获取数据写入本地
						FileOutputStream outStream = new FileOutputStream(
								getCacheDir().toString() + "/myerror_list.txt");
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
						objectOutputStream.writeObject(listItems);
						outStream.close();
					} catch (Exception e) {
						// TODO: handle exception

					}

					if (page_no == 1) {

						sa = new SimpleAdapter(ErrorActivity.this, listItems, R.layout.list_my_error,
								new String[] { "cat_name", "error_quest_count", "add_time" },
								new int[] { R.id.title_error, R.id.count_error, R.id.error_date }) {

							@Override
							public View getView(final int position, View convertView, ViewGroup parent) {
								// TODO Auto-generated method stub
								if (convertView == null) {
									convertView = View.inflate(ErrorActivity.this, R.layout.list_my_error, null);
								}
								final Button read_error = (Button) convertView.findViewById(R.id.read_error);

								read_error.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										if (NetUtil.isConnected(ErrorActivity.this)) {
											// TODO Auto-generated method stub
											// Log.d("345abc", "控件被点击");
											String paper_id = listItems.get(position).get("paper_id").toString();
											// String mToken =
											// sp.getString("mToken",
											// null);
											Intent intent = new Intent(ErrorActivity.this, WebActivity.class);
											intent.putExtra("catID", 6);
											intent.putExtra("paper_id", paper_id);
											startActivity(intent);
										} else {
											Toast.makeText(ErrorActivity.this, "您当前无网络连接", Toast.LENGTH_SHORT).show();
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
						// try {
						// if (dialog != null) {
						// dialog.dismiss();
						// }
						// } catch (Exception e) {
						// TODO: handle exception
						// }
					} else {
						sa.notifyDataSetChanged();
						// try {
						// if (dialog != null) {
						// dialog.dismiss();
						// }
						// } catch (Exception e) {
						// TODO: handle exception
						// }
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				// setListAdapter(sa);
				break;
			case 2:
				if (LoginUtil.login_handlered) {
					Log.d("345abc", "获取新token-Error");
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
						getInfo();
					} else {

						Toast.makeText(ErrorActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
							Log.d("345abc", "循环等待-Error");
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
								Toast.makeText(ErrorActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
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
								Toast.makeText(ErrorActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
							}
						}
					} else {// 本地无更多数据
						if (page_no != 1) {
							Toast.makeText(ErrorActivity.this, "无更多错题记录,请下拉刷新获取更多", Toast.LENGTH_SHORT).show();
							page_no--;
							mPullToRefreshView.onFooterRefreshComplete();
						}
					}

					if (page_no == 1 && sa == null) {
						sa = new SimpleAdapter(ErrorActivity.this, listItems, R.layout.list_my_error,
								new String[] { "cat_name", "error_quest_count", "add_time" },
								new int[] { R.id.title_error, R.id.count_error, R.id.error_date }) {

							@Override
							public View getView(final int position, View convertView, ViewGroup parent) {
								// TODO Auto-generated method stub
								if (convertView == null) {
									convertView = View.inflate(ErrorActivity.this, R.layout.list_my_error, null);
								}
								final Button read_error = (Button) convertView.findViewById(R.id.read_error);

								read_error.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										// Log.d("345abc", "控件被点击");
										if (NetUtil.isConnected(ErrorActivity.this)) {
											String paper_id = listItems.get(position).get("paper_id").toString();
											// String mToken =
											// sp.getString("mToken",
											// null);
											Intent intent = new Intent(ErrorActivity.this, WebActivity.class);
											intent.putExtra("catID", 6);
											intent.putExtra("paper_id", paper_id);
											startActivity(intent);

											// String url_my_practice =
											// Constant.phpUrl
											// +
											// "/wap/api.php?action=GET_CAT&id=";
										} else {
											Toast.makeText(ErrorActivity.this, "您当前无网络连接", Toast.LENGTH_SHORT).show();
										}
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
	private String mToken;
	PullToRefreshView mPullToRefreshView;
	private int page_no = 1;
	private List<Map<String, Object>> listItems;
	private SimpleAdapter sa;
	private TextView list_no_record;
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
		error_instance = this;
		// search_view = (SearchView) findViewById(R.id.search_view);
		// search_result = (TextView) findViewById(R.id.search_result);
		// imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText("我的错题");
		// list_search = (ListView) findViewById(R.id.list_search);
		// dialog = ProgressDialogUtil.createLoadingDialog(ErrorActivity.this,
		// null, true, false);
		// pull_to_refresh_updated_at=(TextView)findViewById(R.id.pull_to_refresh_updated_at);
		list_record = (ListView) findViewById(R.id.list_record);
		list_no_record = (TextView) findViewById(R.id.list_no_record);
		// new MyTask().execute(PATH);
		sp = ErrorActivity.this.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
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
		// Log.d("345abc", "handlered的值为 " + TokenManager.handlered);
		// mToken = new TokenManager(ErrorActivity.this).getToken();
		// if (mToken != null) {
		// getInfo();
		// } else {
		// Log.d("345abc", "获取为空，开始等待");
		// LoginUtil.login_handlered = false;
		// Message msg_loop = handler.obtainMessage();
		// msg_loop.what = 2;
		// msg_loop.sendToTarget();
		// }
		if (list_cache) {// 使用本地数据
			try {
				if (listItems_cache == null || listItems_cache.isEmpty()) {// 第一次加载本地数据
					FileInputStream freader = new FileInputStream(getCacheDir().toString() + "/myerror_list.txt");
					ObjectInputStream objectInputStream = new ObjectInputStream(freader);
					listItems_cache = (List<Map<String, Object>>) objectInputStream.readObject();
					objectInputStream.close();
				}

				if (listItems_cache == null || listItems_cache.isEmpty()) {// 加载数据为空
					try {// 删除无效文件
							// Toast.makeText(getActivity(), "s删除1",
							// Toast.LENGTH_SHORT).show();
						File file = new File(getCacheDir().toString() + "/myerror_list.txt");
						file.delete();

					} catch (Exception e) {
						// TODO: handle exception
					}
					// 重新请求数据
					list_cache = false;
					page_no = 1;
					mToken = new TokenManager(ErrorActivity.this).getToken();
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
				mToken = new TokenManager(ErrorActivity.this).getToken();
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
			mToken = new TokenManager(ErrorActivity.this).getToken();
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
		// Log.d("345abc", "新Token=" + mToken);
		String mobile = Encryption.desEncryptd(Constant.code_local, Constant.code_local,
				sp.getString("user_name", null));
		String app_nonce = StringUtil.getPhoneIMEI(ErrorActivity.this);
		String url_mypractice = Constant.phpUrl + "/wap/api.php?action=GET_ERROR_EXERCISES&app_nonce=" + app_nonce
				+ "&mobile=" + mobile + "&tocken=" + mToken + "&page_no=" + page_no;
		Log.d("345abc", "url_error=" + url_mypractice);
		try {
			// try {
			// if (dialog != null) {
			// dialog.show();
			// }
			// } catch (Exception e) {
			// TODO: handle exception
			// }
			// Log.d("123456", "page-no= " + page_no);
			NetHelper.get(url_mypractice, new SimpleMultiBeanNetHandler<MyError>(this) {
				@Override
				protected void onSuccess(List<MyError> list) {
					list_practice = list;
					if (list_no_record != null) {
						list_no_record.setVisibility(View.GONE);
					}
					Log.d("345abc", "Error获取的错误信息=" + list.get(0).cat1_id);
					Message msg = new Message();
					msg.what = 1;
					handler.handleMessage(msg);

					if (page_no != 1) {
						Toast.makeText(ErrorActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
						mPullToRefreshView.onFooterRefreshComplete();
					} else {
						Toast.makeText(ErrorActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
						mPullToRefreshView.onHeaderRefreshComplete();
					}

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					// super.onError(errorCode, errorMsg);
					Log.d("345abc", "errorMsg=" + errorMsg);
					try {
						JSONObject jo = new JSONObject(errorMsg);

						if (jo.optString("result").equals("2") && page_no != 1) {
							Toast.makeText(ErrorActivity.this, "无更多错题", Toast.LENGTH_SHORT).show();
						} else if (jo.optString("result").equals("2") && page_no == 1) {
							list_no_record.setVisibility(View.VISIBLE);
							if (listItems != null && sa != null) {
								listItems.clear();
								sa.notifyDataSetChanged();
							}
							Toast.makeText(ErrorActivity.this, "您当前无错题记录", Toast.LENGTH_SHORT).show();
							try {// 网络请求成功，删除本地数据
									// Toast.makeText(getActivity(), "s删除缓存",
									// Toast.LENGTH_SHORT).show();
								File file = new File(getCacheDir().toString() + "/myerror_list.txt");
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
							if(sp.getBoolean("logined", false)){
								Toast.makeText(ErrorActivity.this, "网络错误，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
								editor.putBoolean("token_logined", false);
								editor.putBoolean("logined", false);
								MyApplication.logined=false;
								new LoginUtil(ErrorActivity.this, 20);
							}
						}else {

							String mistake = jo.optString("info");
							// list_no_record.setVisibility(View.VISIBLE);
							Toast.makeText(ErrorActivity.this, mistake, Toast.LENGTH_SHORT).show();
						}

					} catch (Exception e) {
						// TODO: handle exception
						if (listItems_cache != null && !listItems_cache.isEmpty()) {// 网络通信失败，重新使用本地数据
							list_cache = true;
						} else if ((listItems_cache == null || listItems_cache.isEmpty())) {// 无缓存且通信失败
							try {
								list_no_record.setVisibility(View.VISIBLE);
								list_no_record.setText("通信失败");
								listItems.clear();
								sa.notifyDataSetChanged();
							} catch (Exception e2) {
								// TODO: handle exception
							}

						}
						Toast.makeText(ErrorActivity.this, "通信失败，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
					}
					// try {
					// if (dialog != null) {
					// dialog.dismiss();
					// }
					// } catch (Exception e) {
					// TODO: handle exception
					// }
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
