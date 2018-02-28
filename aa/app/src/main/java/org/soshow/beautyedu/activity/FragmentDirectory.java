package org.soshow.beautyedu.activity;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.json.bean.LectureList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
/**
 * 视频播放界面列表
 */
public class FragmentDirectory extends BaseFragment {
	private View rootView;
	// private SharedPreferences sp;
	public static FragmentDirectory fmt;
	private TextView list_no_video;
	private RelativeLayout pull_to_refresh_header;
	// private static int count = 0;
	public static boolean mHasLoadedOnce;
	private boolean isPrepared;
	private ListView list_video;
	// private TextView cache;
	private SimpleAdapter sa;

	private List<Map<String, String>> listItems = new ArrayList<Map<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		fmt = this;
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_directory, null);
			list_no_video = (TextView) rootView.findViewById(R.id.list_no_video);
			list_video = (ListView) rootView.findViewById(R.id.list_video);
			pull_to_refresh_header = (RelativeLayout) rootView.findViewById(R.id.pull_to_refresh_header);
			isPrepared = true;
			lazyLoad();

		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}

		return rootView;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		// Log.d("345abc", "title onstart");
		super.onStart();
	}

	@Override
	protected void lazyLoad() {

		// TODO Auto-generated method stub
		if (!isPrepared || !isVisible || mHasLoadedOnce) {
			return;
		}
		// Toast.makeText(getActivity(), "刷新", Toast.LENGTH_LONG).show();
		// Toast.makeText(getActivity(), "目录加载", Toast.LENGTH_SHORT).show();
		// Log.d("123321", "视频列表为" +
		// PlayVideoActivity.list_lecture_play.toString());
		// Log.d("123321", "题目列表为" + PlayVideoActivity.quests.toString());
		if (PlayVideoActivity.list_lecture_play != null && !PlayVideoActivity.list_lecture_play.isEmpty()
				&& PlayVideoActivity.load_finished) {
			list_no_video.setVisibility(View.GONE);
			listItems.clear();
			pull_to_refresh_header.setVisibility(View.GONE);
			for (LectureList mp : PlayVideoActivity.list_lecture_play) {
				// MyError mp = list_practice.get(i);
				if (mp.video_status.equals("2") && mp.play_urls != null) {
					Map<String, String> listItem = new HashMap<String, String>();

					listItem.put("lecture_title", mp.lecture_title);
					listItem.put("lecture_id", mp.lecture_id);
					listItem.put("lecture_cat1", mp.lecture_cat1);
					listItem.put("lecture_questions_id", mp.lecture_questions_id);
					listItem.put("url_s", mp.url_s);
					listItem.put("url_h", mp.url_h);
					listItem.put("url_p", mp.url_p);
					
					listItem.put("video_local_name", mp.video_local_name);
					
					listItem.put("vheight_s", mp.vheight_s+"");
					listItem.put("vwidth_s", mp.vwidth_s+"");
					listItem.put("lecture_content_url", mp.lecture_content_url);
					// listItem.put("remark", mp.remark);
					try {// 获取数据写入本地
						FileOutputStream outStream = new FileOutputStream(
								getActivity().getCacheDir().toString() + "/lecturl_click" + mp.lecture_id + ".txt");
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
						Map<String, String> map = new HashMap<String, String>();
						map.put("lecture_click_count", Integer.parseInt(mp.lecture_click_count) + 30 + "");
						objectOutputStream.writeObject(map);
						outStream.close();
					} catch (Exception e) {
						// TODO: handle exception

					}
					// Log.d("345abc", "error_quest_count=" + mp.lecture_title);
					listItems.add(listItem);
				}
			}
			sa = new SimpleAdapter(getActivity(), listItems, R.layout.item_video_directory, new String[] { "lecture_title" },
					new int[] { R.id.video_title }) {

				@Override
				public View getView(final int position, View convertView, ViewGroup parent) {
					// TODO Auto-generated method stub
					if (convertView == null) {
						convertView = View.inflate(getActivity(), R.layout.item_video_directory, null);
					}
					final TextView video_title = (TextView) convertView.findViewById(R.id.video_title);
					final ImageView video_arrow = (ImageView) convertView.findViewById(R.id.video_arrow);
					if (listItems.get(position).get("lecture_id").equals(PlayVideoActivity.lecture_id)) {
						video_title.setTextColor(getResources().getColor(R.color.main));
						video_arrow.setImageResource(R.drawable.play_yel);
						// cache=video_title;
					} else {
						video_title.setTextColor(getResources().getColor(R.color.black));
						video_arrow.setImageResource(R.drawable.play_grey);
					}
					// video_title.setText(text);
					convertView.setBackgroundResource(R.drawable.item_pressed_directory);
					if (!listItems.get(position).get("lecture_id").equals(PlayVideoActivity.lecture_id)) {

						convertView.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								// Log.d("345abc", "控件被点击");
								PlayVideoActivity.lecture_id = listItems.get(position).get("lecture_id");
								PlayVideoActivity.lecture_questions_id_o = listItems.get(position)
										.get("lecture_questions_id");
								PlayVideoActivity.video_height=Integer.parseInt(listItems.get(position).get("vheight_s"));
								PlayVideoActivity.video_width=Integer.parseInt(listItems.get(position).get("vwidth_s"));
								PlayVideoActivity.lecture_questions_id = listItems.get(position)
										.get("lecture_questions_id").split(",");
								PlayVideoActivity.lecture_content = listItems.get(position).get("lecture_content");
								// PlayVideoActivity.remark =
								// listItems.get(position).get("remark");
								PlayVideoActivity.lecture_title = listItems.get(position).get("lecture_title");
								
//								PlayVideoActivity.url_h = listItems.get(position).get("url_h");
//								PlayVideoActivity.url_s = listItems.get(position).get("url_s");
//								PlayVideoActivity.url_p = listItems.get(position).get("url_p");
								
								PlayVideoActivity.url_h = listItems.get(position).get("video_local_name");
								PlayVideoActivity.url_s = listItems.get(position).get("video_local_name");
								PlayVideoActivity.url_p = listItems.get(position).get("video_local_name");
								PlayVideoActivity.url_real = listItems.get(position).get("video_local_name");
								
								PlayVideoActivity.position_video = position;
								
								list_video.setAdapter(sa);
								if (PlayVideoActivity.timer_progress != null) {
									try {
										PlayVideoActivity.timer_progress.cancel();
										PlayVideoActivity.timer_progress.purge();
										PlayVideoActivity.timer_progress = new Timer();
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
								PlayVideoActivity.hashId = null;
								PlayVideoActivity.pva_instance.setPlayerSize();
								PlayVideoActivity.pva_instance.setVideoTitle();
								PlayVideoActivity.pva_instance.setVideoInfo();
								// PlayVideoActivity.pva_instance.setRemark();
								FragmentXiti.xiti_index = 0;
								PlayVideoActivity.pva_instance.getTokenLocal(1);

							}
						});
					}
					return super.getView(position, convertView, parent);

				}

			};
			list_video.setAdapter(sa);
		} else if ((PlayVideoActivity.list_lecture_play == null || PlayVideoActivity.list_lecture_play.isEmpty())
				&& PlayVideoActivity.load_finished) {
			list_no_video.setVisibility(View.VISIBLE);
			pull_to_refresh_header.setVisibility(View.GONE);
		}

		mHasLoadedOnce = true;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void refresh() {
		pull_to_refresh_header.setVisibility(View.GONE);
		lazyLoad();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try {
			fmt = null;
			listItems.clear();
			listItems = null;
			sa = null;
		} catch (Exception e) {
			// TODO: handle exception
		}

		super.onDestroy();
	}

}