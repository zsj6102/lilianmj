package org.soshow.beautyedu;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.basketball.common.adapter.CommonAdapter;
import org.soshow.basketball.common.adapter.ViewHolder;
import org.soshow.beautyedu.activity.BaseActivity;

import org.soshow.beautyedu.activity.NewsDetailsActivity;

import org.soshow.beautyedu.bean.RecommendItem;

import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;

import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;


import org.soshow.beautyedu.utils.ImageLoaderUtil;
import org.soshow.beautyedu.utils.ToastUtil;

import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;


/**
 * 常识，分析，视频共用的activity
 *
 */
public class TuwenActivity extends BaseActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	public static String TU_WEN = "tuwen";
	public static String TU_WEN_CODE = "lecture_id";
	private String TAG = "TuwenActivity";
	private SharedPreferences sp;
	private int startPage = 1;
	private int pageSize = 10;
	private boolean hasMore;
	private List<RecommendItem> videoCourses;
	private Context context;
	private TextView tvNodata;
	private ListView list_course;
	private PullToRefreshView mPullToRefreshView;
	private CommonAdapter<RecommendItem> adapterCourse;
	private LinearLayout loading;
    private String tag;
	private String cat_code;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_tuwen);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		context = TuwenActivity.this;
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);

//		editor = sp.edit();
		TextView title_name = (TextView) findViewById(R.id.title_name);

		tag = getIntent().getStringExtra("tag");
		if(tag!=null){
			if(tag.equals("changshi")){
				cat_code = "CHANGSHI";
				title_name.setText(R.string.changshi);
			}else if(tag.equals("fenxi")){
				cat_code = "FENXI";
				title_name.setText(R.string.fenxi);
			}else if(tag.equals("kuaixun")){
				cat_code = "ZHIXUN";
				title_name.setText(R.string.zixun);
			}else if(tag.equals("shipin")){
				cat_code = "SHIPING";
				title_name.setText(R.string.shipin);
			}
		}
		getInfo(startPage);
		initView();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	private void initView() {
		videoCourses = new ArrayList<RecommendItem>();
		loading = (LinearLayout) findViewById(R.id.linear_load);
		tvNodata = (TextView) findViewById(R.id.list_no_tuwen);
		list_course = (ListView) findViewById(R.id.list_tuwen);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.tuwen_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		adapterCourse = new CommonAdapter<RecommendItem>(context, videoCourses,
				R.layout.item_lecture_list_main) {

			@Override
			public void convert(ViewHolder holder, final RecommendItem course) {
				TextView record_count =  holder.getView(R.id.record_count);
				TextView record_title =  holder.getView(R.id.record_title);
				ImageView record_pic  =   holder.getView(R.id.record_pic);
				TextView comment_count =  holder.getView(R.id.comment_count);
				record_title.setText(course.getArticle_title());
				record_count.setText( String.valueOf(course.getClick_num()));
				comment_count.setText(String.valueOf(course.getComment_num()));
				String photos_url =  course.getPhotos_url();
				String[]   photoarray = null;
				photoarray = photos_url.split(";");
				if(photoarray!=null && photoarray.length>0){
					ImageLoaderUtil.getImage(context, record_pic,photoarray[0]
							, R.drawable.defaultpic,
							R.drawable.defaultpic, 0, 0);
				}
			}
		};
		list_course.setAdapter(adapterCourse);
		list_course.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(context, NewsDetailsActivity.class);
				intent.putExtra("url", videoCourses.get(position).getH5_url());
				intent.putExtra("content",videoCourses.get(position).getArticle_title());
				intent.putExtra("title",videoCourses.get(position).getArticle_keyword());
				intent.putExtra("images",videoCourses.get(position).getPhotos_url());
				context.startActivity(intent);
				TuwenActivity activity = (TuwenActivity) context;
				activity.overridePendingTransition(R.anim.anim_slider_right_in,
						R.anim.anim_slider_left_out);
			}
		});
	}

	public void onClick(View v) {
		switch (v.getId()) {
		    case R.id.back_search_logininput:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
					R.anim.anim_slider_right_out);
			break;
			case R.id.right_title:

				break;

		default:
			break;
		}
	}



	// 图文课程列表数据
	private void getInfo(int page) {
		String url_video_course_list = null;
		if(tag.equals("kuaixun")){
			url_video_course_list = Constant.ARTICLE_LIST+ "&page_no=" + page;
		}else{
			url_video_course_list = Constant.NEWS_LIST + "&cat_code="
					+ cat_code  + "&page_no=" + page;
		}

//		Log.e(TAG, "常识分析视频=" + url_video_course_list);
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
									RecommendItem videoCourse = GsonUtils
											.parseJSON(obj.toString(),
													RecommendItem.class);
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
								ToastUtil.getInstance().showToast(context,
										bean.message);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						loading.setVisibility(View.GONE);
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
}
