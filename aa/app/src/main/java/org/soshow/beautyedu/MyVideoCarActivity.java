package org.soshow.beautyedu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.basketball.common.adapter.CommonAdapter;
import org.soshow.basketball.common.adapter.ViewHolder;
import org.soshow.beautyedu.activity.BaseActivity;

import org.soshow.beautyedu.activity.MainTabActivity;
import org.soshow.beautyedu.activity.NewsDetailsActivity;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.bean.News;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;

import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 理链头条
 * @author chenjiaming
 *
 */
public class MyVideoCarActivity extends BaseActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {

	private Context context;
	private LinearLayout loading;
	private PullToRefreshView mPullToRefreshView;
	private CommonAdapter<News> adapter;
	private int startPage = 1;
	private int pageSize = 10;
	private boolean hasMore;
	private SharedPreferences sp;
	private Editor editor;
	private List<News> newsList;

	private TextView tvNoData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_find_item_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		context = MyVideoCarActivity.this;
		newsList = new ArrayList<News>();
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		getMyCard(startPage);
		initView();
	}

	private void initView() {
		TextView title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText(R.string.my_video_card);
		loading = (LinearLayout) findViewById(R.id.linear_load);
		tvNoData = (TextView) findViewById(R.id.list_no_find_detail);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.find_detail_pull_refresh_view);
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);

		ListView listView = (ListView) findViewById(R.id.list_find_detail);
		adapter = new CommonAdapter<News>(context, newsList,
				R.layout.item_video_car) {

			@Override
			public void convert(ViewHolder viewHolder, News listInfo) {

				TextView tvTitle = viewHolder.getView(R.id.tv_video_car_title);
				TextView tvMoney = viewHolder.getView(R.id.tv_time);
				ImageView ivPic = viewHolder.getView(R.id.iv_video_car);
				String[] imgarray = null;
				imgarray = listInfo.getPhotos_url().split(";");
				if(imgarray!=null && imgarray.length>0){
					UniversalImageLoadTool.disPlayTrue(imgarray[0], ivPic, R.drawable.defaultpic);
				}
				tvTitle.setText(listInfo.getArticle_title());
				tvMoney.setText(listInfo.getCreate_time());

			}
		};
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

						Intent intent = new Intent(context, NewsDetailsActivity.class);
						intent.putExtra("url", newsList.get(position).getH5_url());
						context.startActivity(intent);
						MyVideoCarActivity activity = (MyVideoCarActivity) context;
						activity.overridePendingTransition(R.anim.anim_slider_right_in,
								R.anim.anim_slider_left_out);
					}
		});
		listView.setAdapter(adapter);

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


	private void getMyCard(final int startPage) {
		String url_news = Constant.GET_HOME_PAGE + "&page=" + startPage;
		NetHelper.get(url_news,
				new SimpleSingleBeanNetHandler<Captcha>(this) {

					@Override
					protected void onSuccess(Captcha bean) {
						LogUtils.e("我的观看卡列表：result=" + bean.result + "\ninfo="
								+ bean.info);
						if (bean.result.equals("0")) {

							if (startPage != 1) {
								mPullToRefreshView.onFooterRefreshComplete();
							} else {
								mPullToRefreshView.onHeaderRefreshComplete();
							}
							String info = bean.info;
							try {

								JSONObject jsonObject = new JSONObject(info);
								JSONArray newsArray = jsonObject.getJSONArray("news");
								for (int i = 0; i < newsArray.length(); i++) {
									JSONObject obj = (JSONObject) newsArray.get(i);
									News news = GsonUtils.parseJSON(obj.toString(), News.class);
									newsList.add(news);
								}
								if (newsArray.length() >= pageSize) {
									hasMore = true;
								} else {
									hasMore = false;
								}

								if(newsList.size() > 0){
									tvNoData.setVisibility(View.GONE);
								}else{
									tvNoData.setVisibility(View.VISIBLE);
								}
								adapter.notifyDataSetChanged();
							} catch (JSONException e) {
								e.printStackTrace();
							}
							
						} else {
							Toast.makeText(context, bean.message + "",
									Toast.LENGTH_SHORT).show();
						}
						loading.setVisibility(View.GONE);
					}

				});
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		if (hasMore) {
			startPage++;
			getMyCard(startPage);
		} else {
			Toast.makeText(this, "没有更多数据", Toast.LENGTH_SHORT).show();
			mPullToRefreshView.onFooterRefreshComplete();
		}

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		startPage = 1;
		newsList.clear();
		getMyCard(startPage);
	}

}
