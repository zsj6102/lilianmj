package org.soshow.beautyedu.adapters;

import java.util.List;
import java.util.Map;

import org.soshow.beautyedu.CoursesPlayActivity;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.json.utils.NetUtil;
import org.soshow.beautyedu.utils.Constant;

import org.soshow.beautyedu.utils.ImageLoaderUtil;
import org.soshow.beautyedu.utils.MyAlertDialog;
import org.soshow.beautyedu.utils.StringUtil;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ListView适配器
 *
 */
public class CoursesListAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> list;
	// private int remark;
	// private String lecture_id;
	// private Dialog dialog;
	// private String mToken;
	private SharedPreferences sp;
	private Editor editor;

	// private int position_list;
	/*
	 * private Handler handler = new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) { // TODO Auto-generated
	 * method stub super.handleMessage(msg); switch (msg.what) { case 2: if
	 * (LoginUtil.login_handlered) { Log.d("345abc", "处理完毕，获取新token");
	 * LoginUtil.login_handlered = false; mToken = sp.getString("mToken", null);
	 * if (mToken != null) { getInfo(); } else {
	 * 
	 * Toast.makeText(context, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show(); } }
	 * else { new Timer().schedule(new TimerTask() {
	 * 
	 * @Override public void run() { // 需要做的事:发送消息 // LoginUtil.login_handlered
	 * = false; Log.d("345abc", "我的题库收藏循环等待"); Message msg_loop =
	 * handler.obtainMessage(); msg_loop.what = 2; msg_loop.sendToTarget(); } },
	 * 1000);
	 * 
	 * } break;
	 * 
	 * default: break; } }
	 * 
	 * };
	 */
	public CoursesListAdapter(Context context, List<Map<String, Object>> list) {
		this.context = context;
		this.list = list;
		sp = context.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		// dialog = ProgressDialogUtil.createLoadingDialog(context, "请稍等", true,
		// false);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_courses_list, null);
			holder = new ViewHolder();

			holder.record_pic = (ImageView) convertView.findViewById(R.id.record_pic);
			holder.record_title = (TextView) convertView.findViewById(R.id.record_title);
			holder.record_count = (TextView) convertView.findViewById(R.id.record_count);
			holder.llT = (LinearLayout) convertView.findViewById(R.id.item_courst_ll);
			holder.llB = (LinearLayout) convertView.findViewById(R.id.item_courst_ll_b);
			// holder.lecture_charge = (TextView)
			// convertView.findViewById(R.id.lecture_charge);
			holder.chapter_title = (TextView) convertView.findViewById(R.id.chapter_title);
			holder.chapter_title_f = (TextView) convertView.findViewById(R.id.chapter_title_f);
			holder.chapter_layout = (LinearLayout) convertView.findViewById(R.id.chapter_layout);
			holder.lecture_remark = (ImageView) convertView.findViewById(R.id.lecture_remark);
			holder.course_continue = (TextView) convertView.findViewById(R.id.course_continue);
			holder.divider_p = (View) convertView.findViewById(R.id.divider_p);
			// holder.record_time = (TextView)
			// convertView.findViewById(R.id.record_time);
			// holder.lecture_remark = (ImageView)
			// convertView.findViewById(R.id.lecture_remark);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// holder.record_pic.setImageResource(R.drawable.ic_launcher);
		if (list.get(position).get("course_pack_type").toString().equals("1")
				&& list.get(position).get("lecture_title") != null) {
			holder.record_title.setVisibility(View.VISIBLE);
			holder.record_title.setText(list.get(position).get("lecture_title").toString());
			// Log.d("123456",
			// "getView" + list.get(position).get("lecture_id").toString()
			// + list.get(position).get("lecture_title").toString() + " ope"
			// + list.get(position).get("lecture_operate"));
		} else {
		
			holder.record_title.setText("");
			holder.record_title.setVisibility(View.GONE);
		}
		if (list.get(position).get("course_pack_type").toString().equals("1")
				&& list.get(position).get("lecture_click_count") != null) {
			holder.record_count.setVisibility(View.VISIBLE);
			holder.record_count.setText(list.get(position).get("lecture_click_count").toString() + "人观看");
			holder.llT.setVisibility(View.VISIBLE);
			holder.llB.setVisibility(View.VISIBLE);
		} else {
			holder.record_count.setText("");
			holder.record_count.setVisibility(View.GONE);
			holder.llT.setVisibility(View.GONE);
			holder.llB.setVisibility(View.GONE);
		}
		// Log.d("111", "状态" + list.get(position).get("video_static"));
		if (list.get(position).get("course_pack_type").toString().equals("1")
				&& list.get(position).get("learn_status") != null && list.get(position).get("my_duration") != null) {
			holder.lecture_remark.setVisibility(View.VISIBLE);
			holder.course_continue.setVisibility(View.VISIBLE);
			// Log.d("111",
			// "状态"+list.get(position).get("video_static").toString());
			if (list.get(position).get("learn_status").toString().equals("0")) {
				holder.lecture_remark.setImageResource(R.drawable.bfh);
				holder.course_continue.setText("未学习");
				holder.course_continue.setTextColor(context.getResources().getColor(R.color.play_gray));
			} else if (list.get(position).get("learn_status").toString().equals("1")) {
				holder.lecture_remark.setImageResource(R.drawable.bfy);
				holder.course_continue.setText(
						StringUtil.timeFormat(Integer.parseInt(list.get(position).get("my_duration").toString())));
				holder.course_continue.setTextColor(context.getResources().getColor(R.color.play_yel));
			} else if (list.get(position).get("learn_status").toString().equals("2")) {

				holder.lecture_remark.setImageResource(R.drawable.zcxx);
				holder.course_continue.setText("再次学习");
				holder.course_continue.setTextColor(context.getResources().getColor(R.color.play_blue));
			}
		} else {
			holder.lecture_remark.setVisibility(View.GONE);
			holder.course_continue.setVisibility(View.GONE);
		}
		/*
		 * if (list.get(position).get("course_pack_type").toString().equals("1")
		 * && list.get(position).get("price") != null) { if
		 * (list.get(position).get("price").toString().equals("0.00")) {
		 * Spannable span = new SpannableString("免费   " +
		 * list.get(position).get("lecture_operate")); span.setSpan(new
		 * ForegroundColorSpan(context.getResources().getColor(R.color.green_my)
		 * ), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); span.setSpan(new
		 * ForegroundColorSpan(context.getResources().getColor(R.color.
		 * word_grey_liu)), 5, span.length(),
		 * Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		 * holder.lecture_charge.setVisibility(View.VISIBLE);
		 * holder.lecture_charge.setText(span); } else { Spannable span = new
		 * SpannableString(list.get(position).get("price").toString() + "元   " +
		 * list.get(position).get("lecture_operate")); span.setSpan(new
		 * ForegroundColorSpan(Color.RED), 0, new
		 * String(list.get(position).get("price").toString() + "元").length(),
		 * Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); span.setSpan(new
		 * ForegroundColorSpan(context.getResources().getColor(R.color.
		 * word_grey_liu)), new
		 * String(list.get(position).get("price").toString() + "元").length() +
		 * 3, span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		 * holder.lecture_charge.setVisibility(View.VISIBLE);
		 * holder.lecture_charge.setText(span); } } else {
		 * holder.lecture_charge.setText("");
		 * holder.lecture_charge.setVisibility(View.GONE); }
		 */

		if (list.get(position).get("course_pack_type").toString().equals("1")
				&& list.get(position).get("image_url") != null) {
			holder.record_pic.setVisibility(View.VISIBLE);
			ImageLoaderUtil.getImage(context, holder.record_pic, list.get(position).get("image_url").toString(),
					R.drawable.defaultpic, R.drawable.defaultpic, 0, 0);
		} else {
			holder.record_pic.setVisibility(View.GONE);
		}
		if (list.get(position).get("course_pack_type").toString().equals("1")) {
			convertView.setClickable(true);
			convertView.setBackgroundResource(R.drawable.item_pressed_light);
			holder.chapter_title.setText("");
			holder.chapter_title.setVisibility(View.GONE);
			holder.chapter_layout.setVisibility(View.GONE);
			holder.divider_p.setVisibility(View.VISIBLE);
			if (list.get(position).get("lecture_title") != null
					&& !StringUtil.isEmpty(list.get(position).get("lecture_title").toString())) {
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						
						Log.e("培训视频列表播放地址：", list.get(position).get("video_local_name").toString());
						// TODO Auto-generated method stub
						// Log.d("345abc", "convert被点击");
						if ((NetUtil.isConnected(context) && NetUtil.isWifi(context)) || NetUtil.isConnected(context)
								&& !NetUtil.isWifi(context) && sp.getInt("allow_net_local", 0) == 1) {
							if (Build.VERSION.SDK_INT >= 16) {

								Intent intent = new Intent(context, CoursesPlayActivity.class);
								intent.putExtra("position", position);
								intent.putExtra("lecture_cat1", list.get(position).get("lecture_cat1").toString());
								intent.putExtra("lecture_questions_id",
										list.get(position).get("lecture_questions_id").toString());
								intent.putExtra("lecture_cat3", list.get(position).get("lecture_cat3").toString());
								intent.putExtra("url_s", list.get(position).get("url_s").toString());
								intent.putExtra("url_h", list.get(position).get("url_h").toString());
								intent.putExtra("url_p", list.get(position).get("url_p").toString());
								
								intent.putExtra("video_local_name", list.get(position).get("video_local_name").toString());
								
								intent.putExtra("lecture_title", list.get(position).get("lecture_title").toString());
								intent.putExtra("lecture_content",
										list.get(position).get("lecture_content").toString());
								intent.putExtra("lecture_id", list.get(position).get("lecture_id").toString());
								intent.putExtra("course_id", list.get(position).get("course_id").toString());
								// intent.putExtra("remark",
								// list.get(position).get("remark").toString());
								intent.putExtra("vheight_s", list.get(position).get("vheight_s").toString());
								intent.putExtra("vwidth_s", list.get(position).get("vwidth_s").toString());
								intent.putExtra("lecture_click_count",
										list.get(position).get("lecture_click_count").toString());
								intent.putExtra("image_url", list.get(position).get("image_url").toString());
								intent.putExtra("price", list.get(position).get("price").toString());
								intent.putExtra("lecture_operate",
										list.get(position).get("lecture_operate").toString());
								intent.putExtra("course_pack_id", list.get(position).get("course_pack_id").toString());
								try {
									intent.putExtra("play_duration",
											Integer.parseInt(list.get(position).get("play_duration").toString()));
								} catch (Exception e) {
									// TODO: handle exception
								}

								if (sp.getBoolean("logined", false)) {
									if (list.get(position).get("my_duration") != null
											&& !StringUtil.isEmpty(list.get(position).get("my_duration").toString())
											&& list.get(position).get("learn_status") != null
											&& !StringUtil.isEmpty(list.get(position).get("learn_status").toString())) {
										intent.putExtra("learn_status",
												Integer.parseInt(list.get(position).get("learn_status").toString()));
										intent.putExtra("my_duration",
												Integer.parseInt(list.get(position).get("my_duration").toString()));
										Log.d("123321",
												"my_duration为" + list.get(position).get("my_duration").toString());
									} else {
										// Toast.makeText(context, "", duration)
										intent.putExtra("learn_status", 0);
										intent.putExtra("my_duration", 0);
									}
								}
								context.startActivity(intent);
							} else {
								Toast.makeText(context, "您当前系统版本过低,请升级到安卓4.1及以上", Toast.LENGTH_SHORT).show();
							}
						} else if (NetUtil.isConnected(context) && sp.getInt("allow_net_local", 0) == 0
								&& !NetUtil.isWifi(context)) {
							Builder bd = new MyAlertDialog.Builder(context);
							bd.setTitle("网络提示").setMessage(R.string.video_net_warn)
									.setPositiveButton("确定", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0, int arg1) {
									// TODO Auto-generated method stub
									arg0.dismiss();
									// allow_net.setChecked(true);
									if (Build.VERSION.SDK_INT >= 16) {
										Intent intent = new Intent(context, CoursesPlayActivity.class);
										intent.putExtra("position", position);
										intent.putExtra("lecture_cat1",
												list.get(position).get("lecture_cat1").toString());
										intent.putExtra("lecture_questions_id",
												list.get(position).get("lecture_questions_id").toString());
										intent.putExtra("lecture_cat3",
												list.get(position).get("lecture_cat3").toString());
										intent.putExtra("url_s", list.get(position).get("url_s").toString());
										intent.putExtra("url_h", list.get(position).get("url_h").toString());
										intent.putExtra("url_p", list.get(position).get("url_p").toString());
										
										intent.putExtra("url_real", list.get(position).get("video_local_name").toString());
										
										intent.putExtra("lecture_title",
												list.get(position).get("lecture_title").toString());
										intent.putExtra("lecture_content",
												list.get(position).get("lecture_content").toString());
										intent.putExtra("lecture_id", list.get(position).get("lecture_id").toString());
										intent.putExtra("course_id", list.get(position).get("course_id").toString());
										// intent.putExtra("remark",
										// list.get(position).get("remark").toString());
										intent.putExtra("vheight_s", list.get(position).get("vheight_s").toString());
										intent.putExtra("vwidth_s", list.get(position).get("vwidth_s").toString());
										intent.putExtra("lecture_click_count",
												list.get(position).get("lecture_click_count").toString());
										intent.putExtra("image_url", list.get(position).get("image_url").toString());
										intent.putExtra("price", list.get(position).get("price").toString());
										intent.putExtra("lecture_operate",
												list.get(position).get("lecture_operate").toString());
										intent.putExtra("course_pack_id",
												list.get(position).get("course_pack_id").toString());
										try {
											intent.putExtra("play_duration", Integer
													.parseInt(list.get(position).get("play_duration").toString()));
										} catch (Exception e) {
											// TODO: handle exception
										}

										if (sp.getBoolean("logined", false)) {
											if (list.get(position).get("my_duration") != null
													&& !StringUtil
															.isEmpty(list.get(position).get("my_duration").toString())
													&& list.get(position).get("learn_status") != null
													&& !StringUtil.isEmpty(
															list.get(position).get("learn_status").toString())) {
												intent.putExtra("learn_status", Integer
														.parseInt(list.get(position).get("learn_status").toString()));
												intent.putExtra("my_duration", Integer
														.parseInt(list.get(position).get("my_duration").toString()));
												Log.d("123321", "my_duration为"
														+ list.get(position).get("my_duration").toString());
											} else {
												// Toast.makeText(context, "",
												// duration)
												intent.putExtra("learn_status", 0);
												intent.putExtra("my_duration", 0);
											}
										}
										// intent.putParcelableArrayListExtra("listItems",
										// list);
										// intent.putExtra("listItems",
										// (Serializable)
										// list);
										editor.putInt("allow_net_local", 1);
										editor.commit();
										Constant.allow_net_other = true;
										/// if (sp.getBoolean("logined", false))
										/// {
										context.startActivity(intent);
										/// } else {
										/// Constant.next_page = intent;
										/// Intent intent1 = new Intent(context,
										/// LoginInputActivity.class);
										/// intent1.putExtra("key", 13);
										/// context.startActivity(intent1);
										/// }
										// getTokenLocal();
									} else {
										Toast.makeText(context, "您当前系统版本过低,请升级到安卓4.1及以上", Toast.LENGTH_SHORT).show();
									}
								}
							}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									dialog.dismiss();

								}
							}).show();
						} else if (!NetUtil.isConnected(context)) {
							Toast.makeText(context, "您当前无网络", Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		} else if (list.get(position).get("course_pack_type").toString().equals("3")) {
			try {
				Log.d("abccba", "进入3");
				convertView.setClickable(false);
				convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
				// Log.d("1221", "空指针检测" +
				// list.get(position).get("lecture_title"));
				holder.chapter_layout.setVisibility(View.GONE);
				holder.divider_p.setVisibility(View.GONE);
				holder.chapter_title.setVisibility(View.VISIBLE);
				// holder.chapter_title.setTextSize(R.dimen.videolist_tab_Size);
				holder.chapter_title.setText(list.get(position).get("lecture_title").toString());
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else if (list.get(position).get("course_pack_type").toString().equals("4")) {
			convertView.setClickable(false);
			convertView.setBackgroundColor(context.getResources().getColor(R.color.grey_new));
			// Log.d("1221", "空指针检测" +
			// list.get(position).get("lecture_title"));
			holder.chapter_title.setVisibility(View.GONE);
			holder.divider_p.setVisibility(View.GONE);
			holder.chapter_layout.setVisibility(View.VISIBLE);
			// holder.chapter_title_f.setTextSize(R.dimen.normalword_Size);
			holder.chapter_title_f.setText(list.get(position).get("lecture_title").toString());
		}
		return convertView;
	}

	class ViewHolder {
		TextView record_title, record_count, chapter_title, chapter_title_f, course_continue;// ,
		LinearLayout chapter_layout; // record_time
		ImageView record_pic, lecture_remark;//
		View divider_p;
		LinearLayout llT;
		LinearLayout llB;
	}
	/*
	 * private void getTokenLocal() { mToken = new
	 * TokenManager(context).getToken(); if (mToken != null) { getInfo(); } else
	 * { LoginUtil.login_handlered = false; Message msg_loop =
	 * handler.obtainMessage(); msg_loop.what = 2; msg_loop.sendToTarget(); } }
	 * 
	 * private void getInfo() { dialog.show(); String app_nonce =
	 * StringUtil.getPhoneIMEI(context); String url_shoucang = Constant.phpUrl +
	 * "/wap/api.php?action=GET_CHANGE_REMARK&app_nonce=" + app_nonce +
	 * "&tocken=" + mToken + "&lecture_id=" + lecture_id + "&remark=" + remark;
	 * Log.d("1221", "收藏地址为   " + url_shoucang); NetHelper.get(url_shoucang, new
	 * SimpleSingleBeanNetHandler<Captcha>(context) {
	 * 
	 * @Override protected void onSuccess(Captcha bean) { // TODO Auto-generated
	 * method stub dialog.dismiss(); try { switch
	 * (Integer.parseInt(bean.result)) { case 0: Toast.makeText(context,
	 * bean.info, Toast.LENGTH_SHORT).show(); if (bean.info.contains("取消")) { //
	 * list.get(position_list).put("remark", "2"); //
	 * MyApplication.list_lecture.get(position_list).remark // = "2"; } else {
	 * // list.get(position_list).put("remark", "1"); //
	 * MyApplication.list_lecture.get(position_list).remark // = "1"; }
	 * DisplayRecordNewAdapter.this.notifyDataSetChanged(); break;
	 * 
	 * default: Toast.makeText(context, bean.info, Toast.LENGTH_SHORT).show();
	 * break; } } catch (Exception e) { // TODO: handle exception }
	 * 
	 * }
	 * 
	 * @Override protected void onError(int errorCode, String errorMsg) { //
	 * TODO Auto-generated method stub // super.onError(errorCode, errorMsg);
	 * dialog.dismiss(); Toast.makeText(context, R.string.net_error_warning,
	 * Toast.LENGTH_SHORT).show(); }
	 * 
	 * }); }
	 */
}
