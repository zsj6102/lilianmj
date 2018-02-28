package org.soshow.beautyedu.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.adapters.LectureListAdapter;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.LectureList;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleMultiBeanNetHandler;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.volley.RequestManager;
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
//import com.listviewdemo_imageanddata.MainActivity.MyTask;
/**
 * 视频列表
 */
public class VideoListActivity extends BaseActivity
		implements OnClickListener, OnHeaderRefreshListener, OnFooterRefreshListener {
	// private Intent intent;
	private ListView list_record;

	// private Dialog dialog;
	// private SearchView search_view;
	// private List<Map<String, Object>> list = new ArrayList<Map<String,
	// Object>>();
	private TextView title_name;
	// private int count = 0;
	// public static List<LectureList> list_lecture;
	// 输入框
	// private InputMethodManager imm;
	private SharedPreferences sp;
	private Editor editor;
	private String mToken;
	private int page_no = 1;
	private List<Map<String, Object>> listItems;
	private List<LectureList> listCache;
	private LectureListAdapter dra;
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
					for (LectureList dr : listCache) {
						if (dr.play_urls != null) {
							Map<String, Object> listItem = new HashMap<String, Object>();
							listItem.put("lecture_title", dr.lecture_title);
							listItem.put("lecture_click_count", Integer.parseInt(dr.lecture_click_count) + 30 + "");
							listItem.put("lecture_cat1", dr.lecture_cat1);
							// listItem.put("remark", dr.remark);
							// listItem.put("image_url", Constant.phpUrl +
							// "/upload/video_pic/s_" + dr.head_image);
							listItem.put("image_url", dr.head_image);
							listItem.put("lecture_id", dr.lecture_id);

							listItem.put("lecture_cat3", dr.lecture_cat3);
							listItem.put("lecture_questions_id", dr.lecture_questions_id);
							listItem.put("url_s", dr.url_s);
							listItem.put("url_h", dr.url_h);
							listItem.put("url_p", dr.url_p);
							listItem.put("lecture_content_url", dr.lecture_content_url);
							listItem.put("vheight_s", dr.vheight_s);
							listItem.put("vwidth_s", dr.vwidth_s);
							listItem.put("price", dr.price);
							listItem.put("lecture_operate", dr.lecture_operate);
							try {// 获取数据写入本地
								FileOutputStream outStream = new FileOutputStream(
										getCacheDir().toString() + "/lecturl_click" + dr.lecture_id + ".txt");
								ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
								Map<String, String> map = new HashMap<String, String>();
								map.put("lecture_click_count", Integer.parseInt(dr.lecture_click_count) + 30 + "");
								objectOutputStream.writeObject(map);
								outStream.close();
							} catch (Exception e) {
								// TODO: handle exception

							}
							listItems.add(listItem);
						}
					}
					try {// 获取数据写入本地
						FileOutputStream outStream = new FileOutputStream(
								getCacheDir().toString() + "/videolist_id" + current_id + "_cache.txt");
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
						objectOutputStream.writeObject(listItems);
						outStream.close();
					} catch (Exception e) {
						// TODO: handle exception

					}
					// Constant.refresh_main=true;
					if (page_no == 1) {
						dra = new LectureListAdapter(VideoListActivity.this, listItems);
						list_record.setAdapter(dra);
						// dialog.hide();
					} else {
						dra.notifyDataSetChanged();
						// dialog.hide();
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				break;
			case 2:
				if (LoginUtil.login_handlered) {
					Log.d("345abc", "列表获取新token");
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
						getTokenLocal(index);
					} else {
						isrefreshing = false;
						Toast.makeText(VideoListActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
							Log.d("345abc", "视频列表循环等待");
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 2;
							msg_loop.sendToTarget();
						}
					}, 1000);

				}
				break;
			case 3:
				Log.d("1221", "第三级目录");
				initViewThird(msg.obj.toString());
				break;
			case 4:
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
								// click =
								// getClickCount(listItem.get("lecture_id").toString());
								// if (click != null) {
								// listItem.put("lecture_click_count", click);
								// }
								listItems.add(listItem);

							}
							if (page_no != 1) {
								Toast.makeText(VideoListActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
							}
						} else {// 当前页数据不够10个
							for (int i = 0; i < listItems_cache.size() - ((page_no - 1) * 10); i++) {
								Map<String, Object> listItem = listItems_cache.get((page_no - 1) * 10 + i);
								// click =
								// getClickCount(listItem.get("lecture_id").toString());
								// if (click != null) {
								// listItem.put("lecture_click_count", click);
								// }
								listItems.add(listItem);

							}
							if (page_no != 1) {
								Toast.makeText(VideoListActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
							}
						}
					} else {// 本地无更多数据
						if (page_no != 1) {
							Toast.makeText(VideoListActivity.this, "无更多视频，请下拉刷新获取更多", Toast.LENGTH_SHORT).show();
							page_no--;
							mPullToRefreshView.onFooterRefreshComplete();
						}
					}

					if (page_no == 1) {
						dra = new LectureListAdapter(VideoListActivity.this, listItems);
						list_record.setAdapter(dra);
						// list_cache = false;
						// isrefreshing = true;
						// getTokenLocal();
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
			case 5:

				String type = (String) msg.obj;
				Log.d("1221", "得到消息" + type);
				course_type = type.trim();
				current_id = MyApplication.catID.get("first").get(type.trim());
				int size_second = second_layout.getChildCount();
				// Toast.makeText(VideoListActivity.this, "按钮个数" + abc,
				// Toast.LENGTH_SHORT).show();
				if (size_second > 1) {
					second_layout.removeViewsInLayout(1, size_second - 1);
				}
				second_layout.setVisibility(View.GONE);
				int size_third = third_layout.getChildCount();
				// Toast.makeText(VideoListActivity.this, "按钮个数" + abc,
				// Toast.LENGTH_SHORT).show();
				if (size_third > 1) {
					third_layout.removeViewsInLayout(1, size_third - 1);
				}
				third_layout.setVisibility(View.GONE);
				initView();
				index = 1;
				listItems_cache.clear();
				getTokenLocal(1);
				break;
			default:
				break;
			}
		}

	};
	PullToRefreshView mPullToRefreshView;

	//
	private String course_type;
	// private ListView third_video;
	private LinearLayout third_layout;
	private LinearLayout second_layout;
	// private FixGridLayout second_layout;
	// private FixGridLayout third_layout;
	private ImageView back_search_logininput;
	private Button second_quanbu, third_quanbu;
	private View previous_second, previous_third;
	private String id_first;
	private String id_second;
	private String id_third;

	private String url_myrecord;
	private int index;
	private DisplayMetrics dm;
	private Timer timer = new Timer();
	// 列表缓存变量
	private boolean list_cache = false;
	private List<Map<String, Object>> listItems_cache = new ArrayList<Map<String, Object>>();
	private String current_id;
	public static VideoListActivity vla_fmt;
	private boolean isrefreshing = false;
	private PopupWindow mPopup;
	private ListView mListView;
	private static final int DEFAULT_ELEVATION = 16;;
	private List<Map<String, String>> list;
	private LinearLayout title_list;
	private RelativeLayout title1, title2, title3;
	private TextView title_choice1, title_choice2, title_choice3;
	private ImageView title_selected1, title_selected2, title_selected3;

	// @SuppressLint("NewApi")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_video_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_videodetails);
		// getLayoutInflater().inflate(R.layout.titlebar_search,null);
		vla_fmt = this;

		// search_view = (SearchView) findViewById(R.id.search_view);
		// search_result = (TextView) findViewById(R.id.search_result);
		// imm = (InputMethodManager)
		// getSystemService(Context.INPUT_METHOD_SERVICE);
		// list_lecture = new ArrayList<LectureList>();
		title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText(getIntent().getStringExtra("cat_name"));
		title_name.setOnClickListener(this);
		TypedArray typedArray = VideoListActivity.this.obtainStyledAttributes(null, R.styleable.NiceSpinner);
		Drawable basicDrawable = ContextCompat.getDrawable(VideoListActivity.this, R.drawable.arrow);
		int resId = typedArray.getColor(R.styleable.NiceSpinner_arrowTint, -1);
		Drawable mDrawable;
		if (basicDrawable != null) {
			mDrawable = DrawableCompat.wrap(basicDrawable);

			if (resId != -1) {
				DrawableCompat.setTint(mDrawable, resId);
			}
			title_name.setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawable, null);
		}

		typedArray.recycle();

		list = new ArrayList<Map<String, String>>();
		// list.add("");
		if (MyApplication.catID == null || MyApplication.catID.isEmpty()) {
			Log.d("123321", "崩溃后重新获取catid");
			try {
				FileInputStream freader = new FileInputStream(getCacheDir().toString() + "/cat_id.txt");
				ObjectInputStream objectInputStream = new ObjectInputStream(freader);
				MyApplication.catID = (Map<String, LinkedHashMap<String, String>>) objectInputStream.readObject();
				objectInputStream.close();
				MyApplication.logined = sp.getBoolean("logined", false);
				RequestManager.init(VideoListActivity.this);
			} catch (Exception e) {
				// TODO: handle exception
				Log.d("123321", "崩溃后获取catid异常" + e.toString());
				return;

			}

		}
		title_list = (LinearLayout) getLayoutInflater().inflate(R.layout.list_video_title_new, null)
				.findViewById(R.id.title_list);
		title1 = (RelativeLayout) title_list.findViewById(R.id.title1);
		title2 = (RelativeLayout) title_list.findViewById(R.id.title2);
		title3 = (RelativeLayout) title_list.findViewById(R.id.title3);
		title_choice1 = (TextView) title_list.findViewById(R.id.title_choice1);
		title_choice2 = (TextView) title_list.findViewById(R.id.title_choice2);
		title_choice3 = (TextView) title_list.findViewById(R.id.title_choice3);
		title_selected1 = (ImageView) title_list.findViewById(R.id.title_selected1);
		title_selected2 = (ImageView) title_list.findViewById(R.id.title_selected2);
		title_selected3 = (ImageView) title_list.findViewById(R.id.title_selected3);
		try {
			Map<String, String> map = MyApplication.catID.get("first");
			// id_first = map.get(course_type);
			// current_id = id_first;
			Set<String> kset = map.keySet();
			for (String key : kset) {
				if (key.contains("专业课程")) {
					// list.add(" " + key + " ");
					// list.add(key);
					Map<String, String> map2 = new HashMap<String, String>();
					map2.put("name", key);
					map2.put("id", map.get(key));
					Log.d("abccba", "数据name  " + key + "   id" + map.get(key));
					title_choice1.setText(key);
					title_choice1.setTag(map.get(key));
					title_selected1.setVisibility(View.VISIBLE);
					// RelativeLayout
					// title1=(RelativeLayout)title_list.findViewById(R.id.title_1);
					list.add(map2);
					// list.add(arg0)
				} else if (key.contains("")) {
					// list.add(" "+key+" ");
					// list.add(key);
					Map<String, String> map2 = new HashMap<String, String>();
					map2.put("name", key);
					map2.put("id", map.get(key));
					title_choice2.setText(key);
					title_choice2.setTag(map.get(key));
					Log.d("abccba", "数据name  " + key + "   id" + map.get(key));
					list.add(map2);
				} else if (key.contains("养老")) {
					title_choice3.setText(key);
					title_choice3.setTag(map.get(key));
					Log.d("abccba", "数据name  " + key + "   id" + map.get(key));
				}

			}
			mListView = (ListView) getLayoutInflater().inflate(R.layout.listview_p, null).findViewById(R.id.list_t);

			// mListView = new ListView(VideoListActivity.this);
			mListView.setDivider(null);
			// mListView.setPadding(0, 0, 0, 0);
			// LayoutParams params=new LayoutParams(LayoutParams.WRAP_CONTENT,
			// LayoutParams.WRAP_CONTENT);
			// mListView.setLayoutParams(params);
			// Toast.makeText(VideoListActivity.this,
			// "高度"+mListView.getDividerHeight(), Toast.LENGTH_SHORT).show();
			// mListView.setLayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			// mListView.setDividerHeight(0);
			mListView.setItemsCanFocus(true);

			SimpleAdapter sa = new SimpleAdapter(VideoListActivity.this, list, R.layout.list_video_title,
					new String[] { "name" }, new int[] { R.id.title_choice }) {

				@Override
				public View getView(final int position, View convertView, ViewGroup parent) {
					// TODO Auto-generated method stub

					if (convertView == null) {
						convertView = View.inflate(VideoListActivity.this, R.layout.list_video_title, null);
					}

					final ImageView title_selected = (ImageView) convertView.findViewById(R.id.title_selected);
					if (title_name.getText().toString().equals(list.get(position).get("name").toString())) {
						title_selected.setVisibility(View.VISIBLE);
					} else {
						title_selected.setVisibility(View.GONE);
					}
					convertView.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							if (!title_name.getText().toString().equals(list.get(position).get("name").toString())) {
								title_name.setText(list.get(position).get("name").toString());
								mPopup.dismiss();
								Message message = handler.obtainMessage();
								message.what = 5;
								message.obj = list.get(position).get("name").toString();
								message.sendToTarget();
							} else {
								mPopup.dismiss();
							}
						}
					});
					return super.getView(position, convertView, parent);
				}

			};
			mListView.setAdapter(sa);

			mPopup = new PopupWindow(VideoListActivity.this);
			dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);

			mPopup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
			mPopup.setWidth(dm.widthPixels);
			// mPopup.showat
			// mPopup.show
			// mPopup.setBackgroundDrawable(getResources().getDrawable(R.drawable.shapes_pop));
			// mPopup.setContentView(mListView);
			mPopup.setContentView(title_list);
			mPopup.setOutsideTouchable(true);
			// Toast.makeText(VideoListActivity.this, "初始化",
			// Toast.LENGTH_SHORT).show();
			mPopup.setFocusable(true);

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				mPopup.setElevation(DEFAULT_ELEVATION);
				mPopup.setBackgroundDrawable(
						ContextCompat.getDrawable(VideoListActivity.this, R.drawable.spinner_drawable));
			} else {
				mPopup.setBackgroundDrawable(
						ContextCompat.getDrawable(VideoListActivity.this, R.drawable.drop_down_shadow));

			}

			mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
				@Override
				public void onDismiss() {
					// animateArrow(false);
				}
			});
			/*
			 * try { mPopup.showAsDropDown(title_name); } catch (Exception e) {
			 * // TODO: handle exception Log.d("123321", "异常  ---"
			 * +e.toString()); }
			 */

		} catch (Exception e) {
			// TODO: handle exception
			Log.d("123321", "获取第一级ID异常" + e.toString());
		}
		// NiceSpinnerAdapter<String> niceSpinnerAdapter = new
		// NiceSpinnerAdapter<String>(VideoListActivity.this, list);

		// title_name.setAdapter(niceSpinnerAdapter);

		course_type = getIntent().getStringExtra("cat_name");
		list_record = (ListView) findViewById(R.id.list_record);
		// dialog =
		// ProgressDialogUtil.createLoadingDialog(VideoListActivity.this, null,
		// true, false);
		list_no_record = (TextView) findViewById(R.id.list_no_record);

		sp = VideoListActivity.this.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.main_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		// Log.d("123321", "MyApplication.catID.toString()=" +
		// MyApplication.catID.toString());

		second_layout = (LinearLayout) findViewById(R.id.second_layout);
		third_layout = (LinearLayout) findViewById(R.id.third_layout);
		// third_video = (ListView) findViewById(R.id.third_video);
		// Log.d("123456", "获取的catid为" + MyApplication.catID.toString());
		back_search_logininput = (ImageView) findViewById(R.id.back_search_logininput);
		back_search_logininput.setOnClickListener(this);
		initView();
		index = 1;
		getTokenLocal(1);
		// title_name.requestFocusFromTouch();
		// upDateUrl(1);

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

	@Override
	public void onClick(View v) {
		// intent = new Intent(SearchActivity.this, MainActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		// startActivity(intent);

		final int vid = v.getId();
		// Log.d("123321", "点击id为" + vid);
		switch (vid) {
		case R.id.back_search_logininput:
			// Intent intent = new Intent(VideoListActivity.this,
			// MainTabActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			// startActivity(intent);

			finish();

			break;
		case R.id.search_button:
//			Intent intent = new Intent(VideoListActivity.this, SearchActivity.class);
//			startActivity(intent);
			break;
		case R.id.title_name:

			if (mPopup.isShowing()) {
				// Toast.makeText(VideoListActivity.this, "点击1",
				// Toast.LENGTH_SHORT).show();
				mPopup.dismiss();
			} else {
				mPopup.showAsDropDown(title_name);
				// title_name.get
				// mPopup.showAsDropDown(title_name, 0, 0, Gravity.CENTER);\
				// mPopup.showAtLocation(title_name, Gravity.CENTER,0, 0);
				// Toast.makeText(VideoListActivity.this, "点击2",
				// Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.title1:
			if (title_name.getText().toString().equals(title_choice1.getText().toString())) {
				mPopup.dismiss();
			} else {
				title_name.setText(title_choice1.getText());
				title_selected1.setVisibility(View.VISIBLE);
				title_selected2.setVisibility(View.GONE);
				title_selected3.setVisibility(View.GONE);
				mPopup.dismiss();
				Message message = handler.obtainMessage();
				message.what = 5;
				message.obj = title_choice1.getText().toString();
				message.sendToTarget();
			}
			break;
		case R.id.title2:
			if (title_name.getText().toString().equals(title_choice2.getText().toString())) {
				mPopup.dismiss();
			} else {
				title_name.setText(title_choice2.getText());
				title_selected2.setVisibility(View.VISIBLE);
				title_selected1.setVisibility(View.GONE);
				title_selected3.setVisibility(View.GONE);
				mPopup.dismiss();
				Message message = handler.obtainMessage();
				message.what = 5;
				message.obj = title_choice2.getText().toString();
				message.sendToTarget();
			}
			break;
		case R.id.title3:
			if (title_name.getText().toString().equals(title_choice3.getText().toString())) {
				mPopup.dismiss();
			} else {
				title_name.setText(title_choice3.getText());
				title_selected3.setVisibility(View.VISIBLE);
				title_selected2.setVisibility(View.GONE);
				title_selected1.setVisibility(View.GONE);
				mPopup.dismiss();
				Message message = handler.obtainMessage();
				message.what = 5;
				message.obj = title_choice3.getText().toString();
				message.sendToTarget();
			}
			break;

		default:

			page_no = 1;
			// if (v.getParent().toString().contains("second_layout")) {
			if (vid == R.id.second_quanbu || v.getTag().toString().equals("second")) {
				isrefreshing = false;
				list_cache = false;
				listItems_cache = new ArrayList<Map<String, Object>>();
				try {
					listItems.clear();
					dra.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO: handle exception
				}

				// Log.d("123321", "捕捉2层次为" + v.getParent().toString());
				// Toast.makeText(VideoListActivity.this, "第二层按钮点击",
				// Toast.LENGTH_SHORT).show();
				if (previous_second != null) {
					previous_second.setSelected(false);
					previous_second.setClickable(true);
				}
				v.setSelected(true);
				v.setClickable(false);
				switch (vid) {
				case R.id.second_quanbu:
					// id_second =
					// MyApplication.catID.get("first").get(course_type);
					third_layout.setVisibility(View.GONE);
					// second_quanbu.setTextColor(getResources().getColor(R.color.white));
					index = 1;
					getTokenLocal(1);
					break;
				default:
					// if (previous_third != null) {
					// previous_third.setSelected(false);
					// }
					// previous_third = third_quanbu;
					int abc = third_layout.getChildCount();
					// Toast.makeText(VideoListActivity.this, "按钮个数" + abc,
					// Toast.LENGTH_SHORT).show();
					if (abc > 1) {
						third_layout.removeViewsInLayout(1, abc - 1);
					}
					third_layout.setVisibility(View.GONE);
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
							Log.d("345abc", "循环等待-Error");
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 3;
							msg_loop.obj = Integer.toString(vid);
							msg_loop.sendToTarget();
						}
					}, 1);

					// initViewThird(Integer.toString(vid));
					id_second = Integer.toString(vid);
					index = 2;
					getTokenLocal(2);
					break;
				}
				// second_quanbu.setTextColor(getResources().getColor(R.color.word_grey_liu));
				previous_second = v;
				// } else if (v.getParent().toString().contains("third_layout"))
				// {
			} else if (vid == R.id.third_quanbu || v.getTag().toString().equals("third")) {
				isrefreshing = false;
				list_cache = false;
				listItems_cache = new ArrayList<Map<String, Object>>();
				try {
					listItems.clear();
					dra.notifyDataSetChanged();
				} catch (Exception e) {
					// TODO: handle exception
				}

				// Log.d("123321", "捕捉3层次为" + v.getParent().toString());
				// Toast.makeText(VideoListActivity.this, "第三层按钮点击",
				// Toast.LENGTH_SHORT).show();
				if (previous_third != null) {
					previous_third.setSelected(false);
					previous_third.setClickable(true);
				}
				v.setSelected(true);
				v.setClickable(false);
				switch (vid) {
				case R.id.third_quanbu:
					// third_layout.setVisibility(View.GONE);
					// Log.d("123321", "章节全部");
					index = 2;
					getTokenLocal(2);
					break;
				default:
					id_third = Integer.toString(vid);
					index = 3;
					getTokenLocal(3);
					// initViewThird(Integer.toString(vid));
					break;
				}
				previous_third = v;
			}
			// Toast.makeText(VideoListActivity.this, "未捕捉到按钮层次",
			// Toast.LENGTH_SHORT).show();
			// Log.d("123321", "捕捉未知层次为" + v.getParent().toString());
			break;
		}

	}

	@SuppressWarnings("unchecked")
	private void initView() {
		// Log.d("1221", "catid为"+MyApplication.catID.toString());
		// PowerManager pm =
		// (PowerManager)getSystemService(Context.POWER_SERVICE);
		// PowerManager.WakeLock wl =
		// pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyTag");
		// wl.acquire();
		try {
			second_layout.setVisibility(View.VISIBLE);
			second_quanbu = (Button) findViewById(R.id.second_quanbu);
			second_quanbu.setSelected(true);
			previous_second = second_quanbu;
			// Log.d("123321", "catid---" + MyApplication.catID.toString());
			if (MyApplication.catID == null || MyApplication.catID.isEmpty()) {
				Log.d("123321", "崩溃后重新获取catid");
				try {
					FileInputStream freader = new FileInputStream(getCacheDir().toString() + "/cat_id.txt");
					ObjectInputStream objectInputStream = new ObjectInputStream(freader);
					MyApplication.catID = (Map<String, LinkedHashMap<String, String>>) objectInputStream.readObject();
					objectInputStream.close();
					MyApplication.logined = sp.getBoolean("logined", false);
					RequestManager.init(VideoListActivity.this);
				} catch (Exception e) {
					// TODO: handle exception
					Log.d("123321", "崩溃后获取catid异常" + e.toString());
					return;

				}

			}
			try {
				Map<String, String> map = MyApplication.catID.get("first");
				id_first = map.get(course_type);
				current_id = id_first;
			} catch (Exception e) {
				// TODO: handle exception
				Log.d("123321", "获取第一级ID异常" + e.toString());
			}
			// id_first = MyApplication.catID.get("first").get(course_type);
			// Log.d("1221", "");
			int count = 0;
			if (MyApplication.catID != null && !MyApplication.catID.isEmpty()
					&& getIntent().getStringExtra("cat_id") != null
					&& MyApplication.catID.get(getIntent().getStringExtra("cat_id")) != null) {
				count = MyApplication.catID.get(getIntent().getStringExtra("cat_id")).size();
			} else {
				Toast.makeText(VideoListActivity.this, "数据出错，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
				finish();
			}
			int number = 0;
			int[] catid_array = new int[count];
			for (Map.Entry<String, String> entry : MyApplication.catID
					.get(MyApplication.catID.get("first").get(course_type)).entrySet()) {
				// Log.d("123456", "catid_array[number] =" +
				// Integer.parseInt(entry.getKey()));
				catid_array[number] = Integer.parseInt(entry.getKey());
				number++;
				// System.out.println("key= " + entry.getKey() + " and value= "
				// +
				// entry.getValue());

			}
			dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			// Arrays.sort(catid_array);
			String[] catname_array = new String[count];

			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			if (dm.widthPixels >= 720) {
				params.setMargins(20, 40, 20, 40);
			} else {
				params.setMargins(10, 20, 10, 20);
			}
			// second_layout.setmCellHeight(30);
			// second_layout.setmCellWidth(100);

			for (int i = 0; i < count; i++) {
				catname_array[i] = MyApplication.catID.get(MyApplication.catID.get("first").get(course_type))
						.get(Integer.toString(catid_array[i]));
				Button button = (Button) getLayoutInflater().inflate(R.layout.button_new, null)
						.findViewById(R.id.second_quanbu);
				button.setId(catid_array[i]);
				button.setText(catname_array[i]);
				button.setTag("second");
				button.setOnClickListener(this);

				second_layout.addView(button, params);

			}

		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(VideoListActivity.this, "数据出错，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	private void initViewThird(String second_id) {
		// third_layout.removeAllViews();
		// third_layout.setVisibility(View.GONE);

		// third_layout.setVisibility(View.GONE);
		try {
			third_layout.setVisibility(View.VISIBLE);
			third_quanbu = (Button) findViewById(R.id.third_quanbu);
			third_quanbu.setSelected(true);
			previous_third = third_quanbu;
			// third_layout.removeAllViews();

			if (MyApplication.catID.get(second_id) != null && !MyApplication.catID.get(second_id).isEmpty()) {
				int count = MyApplication.catID.get(second_id).size();
				int number = 0;
				int[] catid_array = new int[count];
				for (Map.Entry<String, String> entry : MyApplication.catID.get(second_id).entrySet()) {
					catid_array[number] = Integer.parseInt(entry.getKey());
					number++;

				}
				// Arrays.sort(catid_array);
				String[] catname_array = new String[count];
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				if (dm.widthPixels >= 720) {
					params.setMargins(20, 0, 20, 40);
				} else {
					params.setMargins(10, 0, 10, 15);
				}

				for (int i = 0; i < count; i++) {
					catname_array[i] = MyApplication.catID.get(second_id).get(Integer.toString(catid_array[i]));
					Button button = (Button) getLayoutInflater().inflate(R.layout.button_new, null)
							.findViewById(R.id.second_quanbu);
					button.setId(catid_array[i]);
					button.setText(catname_array[i]);
					button.setOnClickListener(this);
					button.setTag("third");
					third_layout.addView(button, params);

				}
			} else {
				// Toast.makeText(VideoListActivity.this, "隐藏章",
				// Toast.LENGTH_SHORT).show();
				third_layout.setVisibility(View.GONE);
				list_no_record.setVisibility(View.VISIBLE);
				list_no_record.setText("暂无视频");
			}
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(VideoListActivity.this, "数据出错，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	@SuppressWarnings("unchecked")
	private void getTokenLocal(int index) {
		Log.d("1221", "刷新数据");
		// mToken = new TokenManager(VideoListActivity.this).getToken();
		// if (mToken != null) {
		// upDateUrl(index);
		// getInfo();
		// } else {
		// LoginUtil.login_handlered = false;
		// Message msg_loop = handler.obtainMessage();
		// msg_loop.what = 2;
		// msg_loop.sendToTarget();
		// }
		upDateUrl(index);
		if (list_cache) {// 使用本地数据
			try {
				if (listItems_cache == null || listItems_cache.isEmpty()) {// 第一次加载本地数据
					FileInputStream freader = new FileInputStream(
							getCacheDir().toString() + "/videolist_id" + current_id + "_cache.txt");
					ObjectInputStream objectInputStream = new ObjectInputStream(freader);
					listItems_cache = (List<Map<String, Object>>) objectInputStream.readObject();
					objectInputStream.close();
				}

				if (listItems_cache == null || listItems_cache.isEmpty()) {// 加载数据为空
					try {// 删除无效文件
							// Toast.makeText(RecordActivity.this, "b删除缓存1",
							// Toast.LENGTH_SHORT).show();
						File file = new File(getCacheDir().toString() + "/videolist_id" + current_id + "_cache.txt");
						file.delete();

					} catch (Exception e) {
						// TODO: handle exception
					}
					// 重新请求数据
					list_cache = false;
					page_no = 1;
					mToken = new TokenManager(VideoListActivity.this).getToken();
					if (mToken != null) {
						upDateUrl(index);
						getInfo();
					} else {
						LoginUtil.login_handlered = false;
						Message msg_loop1 = handler.obtainMessage();
						msg_loop1.what = 2;
						msg_loop1.sendToTarget();
					}
				} else {// 本地数据正常
					Message msg_loop = handler.obtainMessage();
					msg_loop.what = 4;
					msg_loop.sendToTarget();
				}

			} catch (Exception e) {// 本地文件获取错误，重新请求数据
				list_cache = false;
				mToken = new TokenManager(VideoListActivity.this).getToken();
				if (mToken != null) {
					upDateUrl(index);
					getInfo();
				} else {
					LoginUtil.login_handlered = false;
					Message msg_loop = handler.obtainMessage();
					msg_loop.what = 2;
					msg_loop.sendToTarget();
				}
			}
		} else {// 直接网络请求
			mToken = new TokenManager(VideoListActivity.this).getToken();
			if (mToken != null) {
				upDateUrl(index);
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
			// dialog.show();

			NetHelper.get(url_myrecord, new SimpleMultiBeanNetHandler<LectureList>(this) {
				@Override
				protected void onSuccess(List<LectureList> list) {
					isrefreshing = false;
					if (page_no == 1) {
						// MyApplication.list_lecture.clear();
					}
					for (int i = 0; i < list.size(); i++) {
						LectureList ll = list.get(i);
						if (ll.video_status.equals("2")) {
							// MyApplication.list_lecture.add(ll);
						} else {
							list.remove(ll);
						}
					}
					listCache = list;
					// list_lecture = list;
					if (list_no_record != null) {
						list_no_record.setVisibility(View.GONE);
					}
					// Log.d("345abc", "获取的image_url=" +
					// list.get(0).head_image);

					if (page_no != 1) {
						Toast.makeText(VideoListActivity.this, "加载成功", Toast.LENGTH_SHORT).show();
						mPullToRefreshView.onFooterRefreshComplete();
					} else {
						Toast.makeText(VideoListActivity.this, "刷新成功", Toast.LENGTH_SHORT).show();
						mPullToRefreshView.onHeaderRefreshComplete();
					}
					handler.sendEmptyMessage(1);

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					// super.onError(errorCode, errorMsg);
					Log.d("345abc", "列表错误=" + errorMsg);
					isrefreshing = false;
					try {
						JSONObject jo = new JSONObject(errorMsg);

						if (jo.optString("result").equals("2") && page_no != 1) {
							Toast.makeText(VideoListActivity.this, "无更多视频", Toast.LENGTH_SHORT).show();
						} else if (jo.optString("result").equals("2") && page_no == 1) {
							list_no_record.setVisibility(View.VISIBLE);
							list_no_record.setText("暂无视频");
							if (listItems != null && dra != null) {
								listItems.clear();
								dra.notifyDataSetChanged();
							}
							Toast.makeText(VideoListActivity.this, "暂无视频", Toast.LENGTH_SHORT).show();
							try {// 网络请求成功，删除本地数据
									// Toast.makeText(RecordActivity.this,
									// "d删除缓存", Toast.LENGTH_SHORT).show();
								File file = new File(
										getCacheDir().toString() + "/videolist_id" + current_id + "_cache.txt");
								file.delete();
								listItems_cache = new ArrayList<Map<String, Object>>();

							} catch (Exception e) {
								// TODO: handle exception
							}
						} else if (jo.optString("result").equals("0")) {
							Toast.makeText(VideoListActivity.this, "数据错误", Toast.LENGTH_SHORT).show();
						} else if (jo.optString("result").equals("99")) {
							editor.putBoolean("token_logined", false);
							editor.remove("mToken");
							editor.commit();
						} else {
							String mistake = jo.optString("info");
							// Log.d("123456", "数据错误" + jo.optString("result") +
							// mistake);
							Toast.makeText(VideoListActivity.this, mistake, Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						// TODO: handle exception
						// list_no_record.setVisibility(View.VISIBLE);
						// list_no_record.setText("暂无视频");
						// if (listItems != null && dra != null) {
						// listItems.clear();
						// dra.notifyDataSetChanged();
						// }
						// Toast.makeText(VideoListActivity.this,
						// "通信失败，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
						if (listItems_cache != null && !listItems_cache.isEmpty()) {// 网络通信失败，重新使用本地数据
							list_cache = false;
						} else if ((listItems_cache == null || listItems_cache.isEmpty())
								&& (listItems == null || listItems.isEmpty())) {// 无缓存且通信失败
							try {
								list_no_record.setVisibility(View.VISIBLE);
								list_no_record.setText("通信失败");
								listItems.clear();
								dra.notifyDataSetChanged();
							} catch (Exception e2) {
								// TODO: handle exception
							}

						}
						Toast.makeText(VideoListActivity.this, "通信失败，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
						// if (page_no == 1 && list_cache) {// 网络刷新失败，重新使用本地数据
						// Message msg_loop = handler.obtainMessage();
						// msg_loop.what = 4;
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

	private void upDateUrl(int index) {
		String app_nonce = StringUtil.getPhoneIMEI(VideoListActivity.this);
		switch (index) {
		case 1:
			url_myrecord = Constant.phpUrl + "/wap/api.php?action=GET_VIDEO_LIST&tocken=" + mToken + "&app_nonce="
					+ app_nonce + "&page_no=" + page_no + "&id=" + id_first;
			current_id = id_first;
			Log.d("123321", "微课1列表url=        " + url_myrecord);
			break;
		case 2:
			url_myrecord = Constant.phpUrl + "/wap/api.php?action=GET_VIDEO_LIST&tocken=" + mToken + "&app_nonce="
					+ app_nonce + "&page_no=" + page_no + "&cat2_id=" + id_second;
			current_id = id_second;
			Log.d("123321", "微课2列表url=        " + url_myrecord);
			break;
		case 3:
			url_myrecord = Constant.phpUrl + "/wap/api.php?action=GET_VIDEO_LIST&tocken=" + mToken + "&app_nonce="
					+ app_nonce + "&page_no=" + page_no + "&cat3_id=" + id_third;
			current_id = id_third;
			Log.d("123321", "微课2列表url=        " + url_myrecord);
			break;
		default:
			break;
		}
	}

	public Handler getHandler() {
		return handler;
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		if (!isrefreshing) {
			page_no++;
			getTokenLocal(index);
		} else {
			Toast.makeText(VideoListActivity.this, "正在更新数据", Toast.LENGTH_SHORT).show();
			mPullToRefreshView.onFooterRefreshComplete();
		}

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		if (!isrefreshing) {
			list_cache = false;
			page_no = 1;
			getTokenLocal(index);
		} else {
			Toast.makeText(VideoListActivity.this, "正在更新数据", Toast.LENGTH_SHORT).show();
			mPullToRefreshView.onHeaderRefreshComplete();
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		listItems = null;
		try {
			if (timer != null) {
				timer.cancel();
				timer.purge();
				// timeing = false;
			}
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
		vla_fmt = null;
		// MyApplication.list_lecture.clear();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d("123321", "返回键点击");
		}
		return super.onKeyDown(keyCode, event);
	}

}
