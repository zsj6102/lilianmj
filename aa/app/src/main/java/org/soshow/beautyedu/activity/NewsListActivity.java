package org.soshow.beautyedu.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.adapters.NewsAdapter;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.bean.News;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleMultiBeanNetHandler;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
//import com.listviewdemo_imageanddata.MainActivity.MyTask;
/**
 * 新闻列表
 */
public class NewsListActivity extends BaseActivity implements OnHeaderRefreshListener, OnFooterRefreshListener {
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
	private List<News> list_displayRecord;
	// 输入框
	// private InputMethodManager imm;
	private SharedPreferences sp;
	private String mToken;
	private int page_no = 1;
	public static List<Map<String, Object>> listItems;
	private List<Map<String, Object>> listItems_cache = new ArrayList<Map<String, Object>>();
	public static NewsAdapter dra;
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
						// MyApplication.list_lecture.clear();
					}
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					for (News dr : list_displayRecord) {
						// for (int i = (list_displayRecord.size() - 1); i >= 0;
						// i--) {
						// DisplayRecord dr = list_displayRecord.get(i);
						// MyApplication.list_lecture.add(dr);
						if (String.valueOf(dr.getId()) != null) {
							Map<String, Object> listItem = new HashMap<String, Object>();

							listItem.put("article_add_time",
									format.format(new Date(Long.valueOf(dr.getId()) * 1000)));

							listItem.put("article_id", String.valueOf(dr.getId()));
							listItem.put("article_title", dr.getArticle_title());
//							listItem.put("image_id_list", dr.getImage_id_list());
							listItems.add(listItem);
						}
					}
					try {// 获取数据写入本地
						FileOutputStream outStream = new FileOutputStream(getCacheDir().toString() + "/newslist.txt");
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
						objectOutputStream.writeObject(listItems);
						outStream.close();
					} catch (Exception e) {

					}
					if (page_no == 1) {
						dra = new NewsAdapter(NewsListActivity.this, listItems);

						list_record.setAdapter(dra);
						Log.d("1221", "设置适配器");

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
						Toast.makeText(NewsListActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
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
					list_no_record.setVisibility(View.GONE);
					if (page_no == 1) {
						listItems = new ArrayList<Map<String, Object>>();
						// MyApplication.list_lecture.clear();
					}
					if (listItems_cache.size() > ((page_no - 1) * 10)) {// 当前页仍有数据
						if (listItems_cache.size() - ((page_no - 1) * 10) >= 10) {// 当前页数据数量足够10个
							for (int i = 0; i < 10; i++) {
								Map<String, Object> listItem = listItems_cache.get((page_no - 1) * 10 + i);
								listItems.add(listItem);

							}
							if (page_no != 1) {
								Toast.makeText(NewsListActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
							}
						} else {// 当前页数据不够10个
							for (int i = 0; i < listItems_cache.size() - ((page_no - 1) * 10); i++) {
								Map<String, Object> listItem = listItems_cache.get((page_no - 1) * 10 + i);
								listItems.add(listItem);

							}
							if (page_no != 1) {
								Toast.makeText(NewsListActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
							}
						}
					} else {// 本地无更多数据
						if (page_no != 1) {
							Toast.makeText(NewsListActivity.this, "无新闻，请下拉刷新获取更多", Toast.LENGTH_SHORT).show();
							page_no--;
							mPullToRefreshView.onFooterRefreshComplete();
						}
					}

					if (page_no == 1 && dra == null) {
						dra = new NewsAdapter(NewsListActivity.this, listItems);
						list_record.setAdapter(dra);

					} else {
						try {
							dra.notifyDataSetChanged();
							mPullToRefreshView.onFooterRefreshComplete();// 结束刷新
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				break;
			default:
				break;
			}
		}

	};
	PullToRefreshView mPullToRefreshView;
	private boolean list_cache = false;

	private LinearLayout loading;

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
		title_name.setText(R.string.news_title);
		list_record = (ListView) findViewById(R.id.list_record);
		// dialog = ProgressDialogUtil.createLoadingDialog(RecordActivity.this,
		// null, true, false);
		
		loading = (LinearLayout) findViewById(R.id.linear_load);
		loading.setVisibility(View.VISIBLE);
		list_no_record = (TextView) findViewById(R.id.list_no_record);

		sp = NewsListActivity.this.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		getTokenLocal();

		// new MyTask().execute(PATH);
	}

	public void onClick(View v) {
		// intent = new Intent(SearchActivity.this, MainActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		// startActivity(intent);
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

	@SuppressWarnings("unchecked")
	private void getTokenLocal() {
		list_displayRecord = new ArrayList<News>();
		if (list_cache) {// 使用本地数据
			try {
				if (listItems_cache == null || listItems_cache.isEmpty()) {// 第一次加载本地数据
					FileInputStream freader = new FileInputStream(getCacheDir().toString() + "/newslist.txt");
					ObjectInputStream objectInputStream = new ObjectInputStream(freader);
					listItems_cache = (List<Map<String, Object>>) objectInputStream.readObject();
					objectInputStream.close();
				}

				if (listItems_cache == null || listItems_cache.isEmpty()) {// 加载数据为空
					try {// 删除无效文件
							// Toast.makeText(RecordActivity.this, "b删除缓存1",
							// Toast.LENGTH_SHORT).show();
						File file = new File(getCacheDir().toString() + "/newslist.txt");
						file.delete();

					} catch (Exception e) {
						// TODO: handle exception
					}
					// 重新请求数据
					list_cache = false;
					page_no = 1;
					mToken = new TokenManager(NewsListActivity.this).getToken();
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
				mToken = new TokenManager(NewsListActivity.this).getToken();
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
			mToken = new TokenManager(NewsListActivity.this).getToken();
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
			String app_nonce = StringUtil.getPhoneIMEI(NewsListActivity.this);
			String url_myrecord = Constant.phpUrl + "/wap/api.php?action=GET_NEWS_LIST&page_no=" + page_no;
			Log.d("345abc", "新闻列表数据url=        " + url_myrecord);
			// dialog.show();
			NetHelper.get(url_myrecord, new SimpleSingleBeanNetHandler<Captcha>(this) {
				@Override
				protected void onSuccess(Captcha bean) {
					try {
						if(bean.result.equals("0")){
							String info = bean.info;
							JSONArray jsonArray = new JSONArray(info);
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject obj = (JSONObject) jsonArray.get(i);
								News news = GsonUtils.parseJSON(obj.toString(), News.class);
								list_displayRecord.add(news);
							}
							loading.setVisibility(View.GONE);
							
						}else{
							ToastUtil.getInstance().showToast(NewsListActivity.this, bean.message);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					
					if (list_no_record != null) {
						list_no_record.setVisibility(View.GONE);
					}
					if (page_no != 1) {
						mPullToRefreshView.onFooterRefreshComplete();
					} else {

						mPullToRefreshView.onHeaderRefreshComplete();
					}
					handler.sendEmptyMessage(1);

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					Log.d("345abc", "播放记录错误=" + errorMsg);
					try {

						JSONObject jo = new JSONObject(errorMsg);

						if (jo.optString("result").equals("1") && page_no != 1) {
							Toast.makeText(NewsListActivity.this, "无更多新闻", Toast.LENGTH_SHORT).show();
						} else if (jo.optString("result").equals("1") && page_no == 1) {
							list_no_record.setVisibility(View.VISIBLE);
							list_no_record.setText("当前无新闻");
							if (listItems != null && dra != null) {
								listItems.clear();
								dra.notifyDataSetChanged();
							}
							Toast.makeText(NewsListActivity.this, "当前无新闻", Toast.LENGTH_SHORT).show();
							try {// 网络请求成功，删除本地数据

								File file = new File(getCacheDir().toString() + "/newslist.txt");
								file.delete();
								listItems_cache = new ArrayList<Map<String, Object>>();

							} catch (Exception e) {
								// TODO: handle exception
							}
						} else {
							String mistake = jo.optString("info");
							Toast.makeText(NewsListActivity.this, mistake, Toast.LENGTH_SHORT).show();
						}
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
						Toast.makeText(NewsListActivity.this, "通信失败，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
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
		page_no++;
		getTokenLocal();

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		list_cache = false;// 重新请求网络数据
		page_no = 1;
		getTokenLocal();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		dra = null;
		listItems = null;
		try {
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
			//handler = null;
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
