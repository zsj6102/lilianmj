package org.soshow.beautyedu.adapters;

import java.util.List;
import java.util.Map;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.NewsDetailsActivity;
import org.soshow.beautyedu.activity.NewsListActivity;
import org.soshow.beautyedu.json.utils.NetUtil;
import org.soshow.beautyedu.utils.ImageLoaderUtil;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
public class NewsAdapter extends BaseAdapter {
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
	public NewsAdapter(Context context, List<Map<String, Object>> list) {
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
			convertView = LayoutInflater.from(context).inflate(R.layout.item_news_list, null);
			holder = new ViewHolder();

			holder.record_pic = (ImageView) convertView.findViewById(R.id.record_pic);
			holder.record_title = (TextView) convertView.findViewById(R.id.record_title);
			holder.record_count = (TextView) convertView.findViewById(R.id.record_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// holder.record_pic.setImageResource(R.drawable.ic_launcher);
		holder.record_title.setText(list.get(position).get("article_title").toString());
		// Log.d("1221",
		// "新闻标题"+list.get(position).get("article_title").toString());
		holder.record_count.setText(list.get(position).get("article_add_time").toString());
		if (list.get(position).get("image_id_list") != null) {
			ImageLoaderUtil.getImage(context, holder.record_pic, list.get(position).get("image_id_list").toString(),
					R.drawable.newsphoto, R.drawable.newsphoto, 0, 0);
		} else {
			holder.record_pic.setImageResource(R.drawable.newsphoto);
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// Log.d("345abc", "convert被点击");
				if (NetUtil.isConnected(context)) {

					Intent intent = new Intent(context, NewsDetailsActivity.class);
					intent.putExtra("article_id", list.get(position).get("article_id").toString());
					context.startActivity(intent);
					NewsListActivity activity = (NewsListActivity) context;
					activity.overridePendingTransition(R.anim.anim_slider_right_in,
			                R.anim.anim_slider_left_out);

				} else {
					Toast.makeText(context, "您当前无网络", Toast.LENGTH_SHORT).show();
				}
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView record_title, record_count;// , record_time
		ImageView record_pic;// , lecture_remark
	}

}
