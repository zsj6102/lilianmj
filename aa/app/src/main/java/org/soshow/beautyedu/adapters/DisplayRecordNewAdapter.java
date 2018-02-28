package org.soshow.beautyedu.adapters;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.LoginInputActivity;
import org.soshow.beautyedu.activity.PlayVideoActivity;
import org.soshow.beautyedu.json.utils.NetUtil;
import org.soshow.beautyedu.utils.Constant;

import org.soshow.beautyedu.utils.ImageLoaderUtil;
import org.soshow.beautyedu.utils.MyAlertDialog;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ListView适配器
 *
 */
public class DisplayRecordNewAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> list;
	// private int remark;
	// private String lecture_id;
	// private Dialog dialog;
	// private String mToken;
	private SharedPreferences sp;
	private Editor editor;
	private String click;
	private Map<String, String> map;

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
	public DisplayRecordNewAdapter(Context context, List<Map<String, Object>> list) {
		this.context = context;
		this.list = list;
		for (int i = 0; i < list.size(); i++) {
			Log.e("list====="+list.get(i).get("video_local_name"), "");
			Log.e("list====="+list.get(i).get("url_p"), "");
		}
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
		// Log.d("1221", "记录getView");
		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.item_lecture_list, null);
			holder = new ViewHolder();

			holder.record_pic = (ImageView) convertView.findViewById(R.id.record_pic);
			holder.record_title = (TextView) convertView.findViewById(R.id.record_title);
			holder.record_count = (TextView) convertView.findViewById(R.id.record_count);
			holder.lecture_charge = (TextView) convertView.findViewById(R.id.lecture_charge);
			holder.lecture_tag = (TextView) convertView.findViewById(R.id.lecture_tag);
			// holder.record_time = (TextView)
			// convertView.findViewById(R.id.record_time);
			// holder.lecture_remark = (ImageView)
			// convertView.findViewById(R.id.lecture_remark);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// holder.record_pic.setImageResource(R.drawable.ic_launcher);
		holder.record_title.setText(list.get(position).get("lecture_title").toString());
		click = getClickCount(list.get(position).get("lecture_id").toString());
		if (click != null) {
			holder.record_count.setText(click + "人观看");
		} else {
			holder.record_count.setText(list.get(position).get("lecture_click_count").toString() + "人观看");
		}
		if (list.get(position).get("price").toString().equals("0.00")) {
			Spannable span = new SpannableString("免费   " + list.get(position).get("lecture_operate"));
			span.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.green_my)), 0, 2,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			span.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.word_grey_liu)), 5,
					span.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.lecture_charge.setText("免费");
			holder.lecture_tag.setText("美业教育");
		} else {
			Spannable span = new SpannableString(
					list.get(position).get("price").toString() + "元   " + list.get(position).get("lecture_operate"));
			span.setSpan(new ForegroundColorSpan(Color.RED), 0,
					new String(list.get(position).get("price").toString() + "元").length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			span.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.word_grey_liu)),
					new String(list.get(position).get("price").toString() + "元").length() + 3, span.length(),
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			holder.lecture_charge.setText(list.get(position).get("price").toString() + "元");
			holder.lecture_tag.setText("美业教育");
		}
		// holder.record_time.setText(list.get(position).get("lecture_time").toString());

		// if (list.get(position).get("remark").toString().equals("1")) {
		// holder.lecture_remark.setImageResource(R.drawable.ico_heartsc);
		// } else {
		// holder.lecture_remark.setImageResource(R.drawable.ico_heartscn);
		// }
		// holder.lecture_remark.setOnClickListener(new OnClickListener() {

		// @Override
		// public void onClick(View v) {

		// TODO Auto-generated method stub
		// position_list = position;
		// if (list.get(position).get("remark").toString().equals("1")) {
		// remark = 2;
		// } else {
		// remark = 1;
		// }
		// lecture_id = list.get(position).get("lecture_id").toString();
		// getTokenLocal();
		// }
		// });
		ImageLoaderUtil.getImage(context, holder.record_pic, list.get(position).get("image_url").toString(),
				R.drawable.defaultpic, R.drawable.defaultpic, 0, 0);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Log.d("345abc", "convert被点击");
				if ((NetUtil.isConnected(context) && NetUtil.isWifi(context)) || NetUtil.isConnected(context)
						&& !NetUtil.isWifi(context) && sp.getInt("allow_net_local", 0) == 1) {
					if (Build.VERSION.SDK_INT >= 16) {
						// if()
						Intent intent = new Intent(context, PlayVideoActivity.class);
						intent.putExtra("position", position);
						intent.putExtra("lecture_cat1", list.get(position).get("lecture_cat1").toString());
						intent.putExtra("lecture_questions_id",
								list.get(position).get("lecture_questions_id").toString());
						intent.putExtra("lecture_cat3", list.get(position).get("lecture_cat3").toString());
						intent.putExtra("url_s", list.get(position).get("url_s").toString());
						intent.putExtra("url_h", list.get(position).get("url_h").toString());
						intent.putExtra("url_p", list.get(position).get("url_p").toString());
						
						Log.e("调试----list.get(position)="+list.get(position).toString(),"");
						try {
							intent.putExtra("video_local_name", list.get(position).get("video_local_name").toString());
						} catch (Exception e) {
						}
						
						intent.putExtra("lecture_title", list.get(position).get("lecture_title").toString());
						intent.putExtra("lecture_content", list.get(position).get("lecture_content").toString());
						intent.putExtra("lecture_id", list.get(position).get("lecture_id").toString());
						Log.d("123456", "发送id为" + list.get(position).get("lecture_id").toString());
						// intent.putExtra("remark",
						// list.get(position).get("remark").toString());
						intent.putExtra("vheight_s", list.get(position).get("vheight_s").toString());
						intent.putExtra("vwidth_s", list.get(position).get("vwidth_s").toString());
						intent.putExtra("lecture_click_count",
								list.get(position).get("lecture_click_count").toString());
						intent.putExtra("image_url", list.get(position).get("image_url").toString());
						intent.putExtra("price", list.get(position).get("price").toString());
						intent.putExtra("lecture_operate", list.get(position).get("lecture_operate").toString());
						// intent.putExtra("lecture_id",
						// list.get(position).get("lecture_id").toString());
						// int clickPlus =
						// Integer.parseInt(list.get(position).get("lecture_click_count").toString())
						// + 1;

						String current_id = list.get(position).get("lecture_id").toString();
						click = getClickCount(list.get(position).get("lecture_id").toString());
						int clickPlus;
						if (click != null) {
							try {
								clickPlus = Integer.parseInt(click) + 1;
							} catch (Exception e) {
								// TODO: handle exception
								clickPlus = Integer.parseInt(list.get(position).get("lecture_click_count").toString())
										+ 1;
							}

						} else {
							clickPlus = Integer.parseInt(list.get(position).get("lecture_click_count").toString()) + 1;
						}
						list.get(position).put("lecture_click_count", Integer.toString(clickPlus));
						for (Map<String, Object> map : list) {
							if (map.get("lecture_id").toString().equals(current_id)) {
								map.put("lecture_click_count", Integer.toString(clickPlus));
							}
						}
						notifyDataSetChanged();
						// notifyDataSetInvalidated();
						try {// 获取数据写入本地
							FileOutputStream outStream = new FileOutputStream(context.getCacheDir().toString()
									+ "/lecturl_click" + list.get(position).get("lecture_id").toString() + ".txt");
							ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
							Map<String, String> map = new HashMap<String, String>();
							map.put("lecture_click_count", Integer.toString(clickPlus));
							objectOutputStream.writeObject(map);
							outStream.close();
						} catch (Exception e) {
							// TODO: handle exception

						}
						///if (sp.getBoolean("logined", false)) {
							context.startActivity(intent);
						///} else {
						///	Constant.next_page = intent;
						///	Intent intent1 = new Intent(context, LoginInputActivity.class);
						///	intent1.putExtra("key", 13);
						///	context.startActivity(intent1);
						///}
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
								click = getClickCount(list.get(position).get("lecture_id").toString());
								int clickPlus;
								if (click != null) {
									try {
										clickPlus = Integer.parseInt(click) + 1;
									} catch (Exception e) {
										// TODO: handle exception
										clickPlus = Integer
												.parseInt(list.get(position).get("lecture_click_count").toString()) + 1;
									}
								} else {
									clickPlus = Integer
											.parseInt(list.get(position).get("lecture_click_count").toString()) + 1;
								}
								list.get(position).put("lecture_click_count", Integer.toString(clickPlus));
								String current_id = list.get(position).get("lecture_id").toString();
								// list.get(position).put("lecture_click_count",
								// Integer.toString(clickPlus));
								for (Map<String, Object> map : list) {
									if (map.get("lecture_id").toString().equals(current_id)) {
										map.put("lecture_click_count", Integer.toString(clickPlus));
									}
								}
								notifyDataSetChanged();
								// notifyDataSetInvalidated();
								try {// 获取数据写入本地
									FileOutputStream outStream = new FileOutputStream(
											context.getCacheDir().toString() + "/lecturl_click"
													+ list.get(position).get("lecture_id").toString() + ".txt");
									ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
									Map<String, String> map = new HashMap<String, String>();
									map.put("lecture_click_count", Integer.toString(clickPlus));
									objectOutputStream.writeObject(map);
									outStream.close();
								} catch (Exception e) {
									// TODO: handle exception

								}
								Intent intent = new Intent(context, PlayVideoActivity.class);
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
								// intent.putParcelableArrayListExtra("listItems",
								// list);
								// intent.putExtra("listItems", (Serializable)
								// list);
								editor.putInt("allow_net_local", 1);
								editor.commit();
								Constant.allow_net_other = true;
								if (sp.getBoolean("logined", false)) {
									context.startActivity(intent);
								} else {
									Constant.next_page = intent;
									Intent intent1 = new Intent(context, LoginInputActivity.class);
									intent1.putExtra("key", 13);
									context.startActivity(intent1);
								}
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
		return convertView;
	}

	class ViewHolder {
		TextView record_title, record_count, lecture_charge,lecture_tag;// , record_time
		ImageView record_pic;// , lecture_remark
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
	private String getClickCount(String id) {
		try {
			Log.d("1221", "遍历点击" + id);
			FileInputStream freader = new FileInputStream(
					context.getCacheDir().toString() + "/lecturl_click" + id + ".txt");
			ObjectInputStream objectInputStream = new ObjectInputStream(freader);
			map = (Map<String, String>) objectInputStream.readObject();
			freader.close();
			objectInputStream.close();
			Log.d("123321", "返回数量" + map.get("lecture_click_count"));
			return map.get("lecture_click_count");
		} catch (Exception e) {
			// TODO: handle exception

			Log.d("123321", "返回数量异常");
			return null;
		}

	}
}
