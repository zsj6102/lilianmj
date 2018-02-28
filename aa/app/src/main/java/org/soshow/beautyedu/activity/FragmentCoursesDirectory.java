package org.soshow.beautyedu.activity;
/**
 * 标准课程播放界面列表
 */
import java.util.Timer;

import org.soshow.beautyedu.CoursesPlayActivity;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.utils.StringUtil;

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
import android.widget.Toast;

public class FragmentCoursesDirectory extends BaseFragment {
	private View rootView;
	// private SharedPreferences sp;
	public static FragmentCoursesDirectory fmt;
	private TextView list_no_video;
	private RelativeLayout pull_to_refresh_header;
	// private static int count = 0;
	public static boolean mHasLoadedOnce;
	private boolean isPrepared;
	private ListView list_video;
	// private TextView cache;
	private SimpleAdapter sa;

	// private List<Map<String, String>> listItems = new ArrayList<Map<String,
	// String>>();

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
			pull_to_refresh_header.setVisibility(View.GONE);
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
	public void lazyLoad() {
		// TODO Auto-generated method stub
		if (!isPrepared || !isVisible || mHasLoadedOnce) {
			return;
		}
		if (CoursesListTwoActivity.listItems != null && !CoursesListTwoActivity.listItems.isEmpty()) {
			// for(Map<String, Object> map: CoursesListTwoActivity.listItems){

			// }
			sa = new SimpleAdapter(getActivity(), CoursesListTwoActivity.listItems,
					R.layout.item_video_directory_course, new String[] { "lecture_title" },
					new int[] { R.id.video_title }) {

				@Override
				public View getView(final int position, View convertView, ViewGroup parent) {
					// TODO Auto-generated method stub
					if (convertView == null) {
						convertView = View.inflate(getActivity(), R.layout.item_video_directory_course, null);
					}
					final TextView video_title = (TextView) convertView.findViewById(R.id.video_title);
					final TextView video_title_mid = (TextView) convertView.findViewById(R.id.video_title_mid);
					final TextView video_title_large = (TextView) convertView.findViewById(R.id.video_title_large);
					final TextView course_continue = (TextView) convertView.findViewById(R.id.course_continue);
					final ImageView lecture_remark = (ImageView) convertView.findViewById(R.id.lecture_remark);
					try {
						if (CoursesListTwoActivity.listItems.get(position).get("course_pack_type").toString()
								.equals("1")) {
							convertView.setBackgroundResource(R.drawable.item_pressed_directory);
							video_title_mid.setVisibility(View.GONE);
							video_title_large.setVisibility(View.GONE);
							video_title.setVisibility(View.VISIBLE);
							course_continue.setVisibility(View.VISIBLE);
							lecture_remark.setVisibility(View.VISIBLE);
							video_title.setText(
									CoursesListTwoActivity.listItems.get(position).get("lecture_title").toString());
							try {
								int status = Integer.parseInt(
										CoursesListTwoActivity.listItems.get(position).get("learn_status").toString());
								switch (status) {
								case 0:
									lecture_remark.setImageResource(R.drawable.bfh);
									course_continue.setText("未学习");
									break;
								case 1:
									lecture_remark.setImageResource(R.drawable.bfy);
									course_continue.setText(
											StringUtil.timeFormat(Integer.parseInt(CoursesListTwoActivity.listItems
													.get(position).get("my_duration").toString())));
									break;
								case 2:
									lecture_remark.setImageResource(R.drawable.zcxx);
									course_continue.setText("再次学习");
									break;
								default:
									break;
								}
							} catch (Exception e) {
							}
							if (CoursesListTwoActivity.listItems.get(position).get("lecture_id")
									.equals(CoursesPlayActivity.lecture_id)) {
								lecture_remark.setImageResource(R.drawable.play_yel);
								video_title.setTextColor(getResources().getColor(R.color.main));
								course_continue.setText("正在学习");
								course_continue.setTextColor(getResources().getColor(R.color.main));
							} else {
								course_continue.setTextColor(getResources().getColor(R.color.word_grey));
								video_title.setTextColor(getResources().getColor(R.color.black));
							}
						} else if (CoursesListTwoActivity.listItems.get(position).get("course_pack_type").toString()
								.equals("3")) {
							video_title_mid.setVisibility(View.GONE);
							video_title_large.setVisibility(View.VISIBLE);
							video_title.setVisibility(View.GONE);
							course_continue.setVisibility(View.GONE);
							lecture_remark.setVisibility(View.GONE);
							// video_title.setTextColor(getResources().getColor(R.color.black));
							video_title_large.setText(
									CoursesListTwoActivity.listItems.get(position).get("lecture_title").toString());
							convertView.setBackgroundColor(getResources().getColor(R.color.grey_low));
						} else if (CoursesListTwoActivity.listItems.get(position).get("course_pack_type").toString()
								.equals("4")) {

							// video_title.setTextColor(getResources().getColor(R.color.black));
							video_title_mid.setVisibility(View.VISIBLE);
							video_title_large.setVisibility(View.GONE);
							video_title.setVisibility(View.GONE);
							course_continue.setVisibility(View.GONE);
							lecture_remark.setVisibility(View.GONE);
							video_title_mid.setText(
									CoursesListTwoActivity.listItems.get(position).get("lecture_title").toString());
							convertView.setBackgroundColor(getResources().getColor(R.color.grey_light));
						}

						if (CoursesListTwoActivity.listItems.get(position).get("course_pack_type").toString()
								.equals("1")) {

							if (!CoursesListTwoActivity.listItems.get(position).get("lecture_id")
									.equals(CoursesPlayActivity.lecture_id)
									&& CoursesListTwoActivity.listItems.get(position).get("course_pack_type").toString()
											.equals("1")) {
								convertView.setClickable(true);
								convertView.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										try {
											CoursesPlayActivity.lecture_id = CoursesListTwoActivity.listItems
													.get(position).get("lecture_id").toString();
//											CoursesPlayActivity.course_id = CoursesListTwoActivity.listItems
//													.get(position).get("course_id").toString();
//											CoursesPlayActivity.course_pack_id = CoursesListTwoActivity.listItems
//													.get(position).get("course_pack_id").toString();
//											CoursesPlayActivity.video_height = Integer
//													.parseInt(CoursesListTwoActivity.listItems.get(position)
//															.get("vheight_s").toString());
//											CoursesPlayActivity.video_width = Integer
//													.parseInt(CoursesListTwoActivity.listItems.get(position)
//															.get("vwidth_s").toString());
											CoursesPlayActivity.pva_instance.setPlayerSize();
//											CoursesPlayActivity.lecture_questions_id_o = CoursesListTwoActivity.listItems
//													.get(position).get("lecture_questions_id").toString();
//											CoursesPlayActivity.lecture_questions_id = CoursesListTwoActivity.listItems
//													.get(position).get("lecture_questions_id").toString().split(",");
											CoursesPlayActivity.lecture_content_url = CoursesListTwoActivity.listItems
													.get(position).get("lecture_content_url").toString();

											CoursesPlayActivity.lecture_title = CoursesListTwoActivity.listItems
													.get(position).get("lecture_title").toString();
											CoursesPlayActivity.url_h = CoursesListTwoActivity.listItems.get(position)
													.get("url_h").toString();
											CoursesPlayActivity.url_s = CoursesListTwoActivity.listItems.get(position)
													.get("url_s").toString();
											CoursesPlayActivity.url_p = CoursesListTwoActivity.listItems.get(position)
													.get("url_p").toString();
											CoursesPlayActivity.position_video = position;
											if (MyApplication.logined) {
												try {
//													CoursesPlayActivity.learn_status = Integer
//															.parseInt(CoursesListTwoActivity.listItems.get(position)
//																	.get("learn_status").toString());
//													CoursesPlayActivity.my_duration = Integer
//															.parseInt(CoursesListTwoActivity.listItems.get(position)
//																	.get("my_duration").toString());
													CoursesPlayActivity.play_duration = Integer
															.parseInt(CoursesListTwoActivity.listItems.get(position)
																	.get("play_duration").toString());
												} catch (Exception e) {
													// TODO: handle exception
//													CoursesPlayActivity.learn_status = 0;
//													CoursesPlayActivity.my_duration = 0;
													CoursesPlayActivity.play_duration = Integer
															.parseInt(CoursesListTwoActivity.listItems.get(position)
																	.get("play_duration").toString());
												}

											}
											list_video.setAdapter(sa);
											if (CoursesPlayActivity.timer_progress != null) {
												try {
													CoursesPlayActivity.timer_progress.cancel();
													CoursesPlayActivity.timer_progress.purge();
													CoursesPlayActivity.timer_progress = new Timer();
												} catch (Exception e) {
													// TODO: handle exception
												}
											}

											CoursesPlayActivity.hashId = null;

											CoursesPlayActivity.pva_instance.setVideoTitle();
											CoursesPlayActivity.pva_instance.setVideoInfo();
											// CoursesPlayActivity.pva_instance.setRemark();
											FragmentXiti.xiti_index = 0;
											CoursesPlayActivity.pva_instance.getTokenLocal(1);
										} catch (Exception e) {
											// TODO: handle exception
											Toast.makeText(getActivity(), "课程加载中，请稍后", Toast.LENGTH_SHORT).show();
										}

									}
								});
							}
						} else {

							convertView.setClickable(false);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

					return super.getView(position, convertView, parent);

				}

			};
			list_video.setAdapter(sa);
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
		mHasLoadedOnce = false;
		try {
			fmt = null;
			// listItems.clear();
			// listItems = null;
			sa = null;
		} catch (Exception e) {
			// TODO: handle exception
		}

		super.onDestroy();
	}

}