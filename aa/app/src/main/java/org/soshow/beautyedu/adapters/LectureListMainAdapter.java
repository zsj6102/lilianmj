package org.soshow.beautyedu.adapters;

import java.util.List;
import java.util.Map;

import org.soshow.beautyedu.R;

import org.soshow.beautyedu.activity.MainTabActivity;
import org.soshow.beautyedu.activity.NewsDetailsActivity;
import org.soshow.beautyedu.bean.RecommendItem;
import org.soshow.beautyedu.utils.ImageLoaderUtil;

import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * ListView适配器
 *
 */
public class LectureListMainAdapter extends BaseAdapter {
	private String TAG = "LectureListMainAdapter";
	private Context context;
	private List<RecommendItem> videoCourses;
	// private int position_list;
	// private String mToken;
	private SharedPreferences sp;
	private Editor editor;
	private String click;
	private int comment_num;
	private Map<String, String> map;
//	private String tocken;
	private String app_nonce;

	public LectureListMainAdapter(Context context,
								  List<RecommendItem> videoCourses, String app_nonce) {
		this.context = context;
		this.videoCourses = videoCourses;
		sp = context.getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
//		this.tocken = tocken;
		this.app_nonce = app_nonce;
	}

	@Override
	public int getCount() {
		return videoCourses.size();
	}

	@Override
	public Object getItem(int position) {
		return videoCourses.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_lecture_list_main, null);
			holder = new ViewHolder();

			holder.record_pic = (ImageView) convertView
					.findViewById(R.id.record_pic);
			holder.record_title = (TextView) convertView
					.findViewById(R.id.record_title);
			holder.record_count = (TextView) convertView
					.findViewById(R.id.record_count);
			holder.lecture_charge = (TextView) convertView
					.findViewById(R.id.lecture_charge);
			holder.lecture_tag = (TextView) convertView
					.findViewById(R.id.lecture_tag);
			holder.comment_count = (TextView)convertView.findViewById(R.id.comment_count);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.record_title.setText(videoCourses.get(position).getArticle_title());
		comment_num =  videoCourses.get(position).getClick_num();
		holder.record_count.setText(String.valueOf(comment_num));
		holder.comment_count.setText(String.valueOf(videoCourses.get(position).getComment_num()));

//		LogUtils.e("积分====="+lecture_integral);
//		if (TextUtils.isEmpty(lecture_integral)||lecture_integral.equals("0")) {
//			holder.lecture_charge.setText("免费");
//		} else {
//			holder.lecture_charge.setText(videoCourses.get(position).getLecture_integral());
//		}

		String photos_url =  videoCourses.get(position).getPhotos_url();
		String[]   photoarray = null;
		photoarray = photos_url.split(";");
		if(photoarray!=null && photoarray.length>0){
			ImageLoaderUtil.getImage(context, holder.record_pic,photoarray[0]
							 , R.drawable.defaultpic,
					R.drawable.defaultpic, 0, 0);
		}

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, NewsDetailsActivity.class);
				intent.putExtra("url", videoCourses.get(position).getH5_url());
				intent.putExtra("content",videoCourses.get(position).getArticle_title());
				intent.putExtra("title",videoCourses.get(position).getArticle_keyword());
				intent.putExtra("images",videoCourses.get(position).getPhotos_url());
				context.startActivity(intent);
				MainTabActivity activity = (MainTabActivity) context;
				activity.overridePendingTransition(R.anim.anim_slider_right_in,
						R.anim.anim_slider_left_out);
			}
		});
//		convertView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				Constant.next_page = null;
//				
//				LogUtils.e("is_vip="+videoCourses.get(position).isIs_vip()+"\n是否支付过："+videoCourses.get(position).getOrder_state());
//				String lecture_integral = videoCourses.get(position).getLecture_integral();
//					if (TextUtils.isEmpty(lecture_integral)||lecture_integral.equals("0")) {
//						holder.lecture_charge.setText("免费");
//						if (MyApplication.logined == false) {
//							Intent intent = new Intent(context,
//									LoginInputActivity.class);
//							context.startActivity(intent);
//							MainTabActivity activity = (MainTabActivity) context;
//							activity.overridePendingTransition(R.anim.anim_slider_right_in,
//					                R.anim.anim_slider_left_out);
//						} else {
//							if (videoCourses.get(position).getLecture_type().equals("1")) {//本地视频
//								playVideoInfo(position);
//							}else{
//								addClick(position);
//							}
//						}
//					} else {
////						boolean is_vip = videoCourses.get(position).isIs_vip();
////						if(is_vip){//vip
////							if (MyApplication.logined == false) {
////								Intent intent = new Intent(context,
////										LoginInputActivity.class);
////								context.startActivity(intent);
////								MainTabActivity activity = (MainTabActivity) context;
////								activity.overridePendingTransition(R.anim.anim_slider_right_in,
////						                R.anim.anim_slider_left_out);
////							} else {
////							if(videoCourses.get(position).getLecture_type().equals("1")){//本地观看
////								playVideoInfo(position);
////							}else if(videoCourses.get(position).getLecture_type().equals("2")){//网页视频
////								
////								addClick(position);
////							}
////							}
////						}else{//不是vip
////							if(videoCourses.get(position).getOrder_state().equals("0")){//未支付
//								if(videoCourses.get(position).getLecture_type().equals("1")){//本地付费观看
//									Intent intent = new Intent(context, VipPayActivity.class);
//									intent.putExtra("title", "推荐视频");
//									intent.putExtra("image_url", videoCourses.get(position).getHead_image());
//									intent.putExtra("lectureTitle", videoCourses.get(position).getLecture_title());
//									intent.putExtra("look", videoCourses.get(position)
//											.getLecture_click_count());
//									intent.putExtra("price", videoCourses.get(position).getPrice());
//									intent.putExtra("lecture_content_url", videoCourses
//											.get(position).getLecture_content_url());
//									intent.putExtra("lecture_id",
//											videoCourses.get(position).getLecture_id());
//									intent.putExtra("lecture_integral",videoCourses.get(position).getLecture_integral());
//									intent.putExtra("lecture_type","1");//本地视频
//									context.startActivity(intent);
//									MainTabActivity activity = (MainTabActivity) context;
//									activity.overridePendingTransition(R.anim.anim_slider_right_in,
//							                R.anim.anim_slider_left_out);
//								}else if(videoCourses.get(position).getLecture_type().equals("2")){//网页付费视频
//									Intent intent = new Intent(context, VipPayActivity.class);
//									intent.putExtra("title", "推荐视频");
//									intent.putExtra("image_url", videoCourses.get(position).getHead_image());
//									intent.putExtra("lectureTitle", videoCourses.get(position).getLecture_title());
//									intent.putExtra("look", videoCourses.get(position)
//											.getLecture_click_count());
//									intent.putExtra("price", videoCourses.get(position).getPrice());
//									intent.putExtra("lecture_content_url", videoCourses
//											.get(position).getLecture_content_url());
//									intent.putExtra("lecture_id",
//											videoCourses.get(position).getLecture_id());
//									intent.putExtra("lecture_integral",
//											videoCourses.get(position).getLecture_integral());
//									intent.putExtra("lecture_type","2");//网页视频
//									context.startActivity(intent);
//									MainTabActivity activity = (MainTabActivity) context;
//									activity.overridePendingTransition(R.anim.anim_slider_right_in,
//							                R.anim.anim_slider_left_out);
//								
//								}
////							}else if(videoCourses.get(position).getOrder_state().equals("1")){//已支付
////								if (MyApplication.logined == false) {
////									Intent intent = new Intent(context,
////											LoginInputActivity.class);
////									context.startActivity(intent);
////									MainTabActivity activity = (MainTabActivity) context;
////									activity.overridePendingTransition(R.anim.anim_slider_right_in,
////							                R.anim.anim_slider_left_out);
////								} else {
////								if(videoCourses.get(position).getLecture_type().equals("1")){//本地观看
////									playVideoInfo(position);
////								}else if(videoCourses.get(position).getLecture_type().equals("2")){//网页视频
////									addClick(position);
////								}
////								}
////							}
////						}
//					}
//					
//				
//
//			}
//		});
		return convertView;
	}
	
	/**
	 * 添加观看人数
//	 */
//	private void addClick(final int position) {
//		LogUtils.e("=========================2");
//		app_nonce = StringUtil.getPhoneIMEI(context);
////		tocken = new TokenManager(context).getToken();
//		String url_click = Constant.phpUrl
//				+ "/wap/api.php?action=ADD_LECTURE_CLICK&tocken="
//				+ "&app_nonce=" + app_nonce + "&lecture_id=" + videoCourses.get(position).getLecture_id();
//		Log.d("234abc", "点击地址" + url_click);
//
//		NetHelper.get(url_click, new SimpleSingleBeanNetHandler<Captcha>(context) {
//
//			@Override
//			protected void onSuccess(Captcha bean) {
//				try {
//					int resule = Integer.parseInt(bean.result);
//					LogUtils.e("=========================resule="+resule);
//					switch (resule) {
//					case 0:
//						String lecture_click_count = videoCourses.get(position).getLecture_click_count();
//						if(!TextUtils.isEmpty(lecture_click_count)&&lecture_click_count !=null){
//							int clickCount = Integer.valueOf(lecture_click_count);
//							videoCourses.get(position).setLecture_click_count(clickCount+1+"");
//							notifyDataSetChanged();
//						}
//
//						//非全屏播放
//						Intent intent = new Intent(context,WebPlayVideoActivity.class);
//						intent.putExtra("url", videoCourses.get(position)
//								.getLecture_link());
//						intent.putExtra("title","推荐视频");
//
//						//全屏播放
////						Intent intent = new Intent(context,
////								WebActivity.class);
////						intent.putExtra("catID", 12);
////						intent.putExtra("url", videoCourses.get(position)
////								.getLecture_link());
//
//
//						context.startActivity(intent);
//						MainTabActivity activity = (MainTabActivity) context;
//						activity.overridePendingTransition(R.anim.anim_slider_right_in,
//				                R.anim.anim_slider_left_out);
//
//
//						break;
//					case 99:
//						editor.putBoolean("token_logined", false);
//						editor.remove("mToken");
//						editor.commit();
//						break;
//					default:
//
//						break;
//					}
//				} catch (Exception e) {
//				}
//
//			}
//
//			@Override
//			protected void onError(int errorCode, String errorMsg) {
//				ToastUtil.getInstance().showToast(context, errorMsg);
//			}
//
//		});
//	}
//
	class ViewHolder {
		TextView record_title, record_count, lecture_charge, lecture_tag,comment_count;
		ImageView record_pic;// , lecture_remark
	}

//	private String getClickCount(String id) {
//		try {
//			Log.d("1221", "遍历点击" + id);
//			FileInputStream freader = new FileInputStream(context.getCacheDir()
//					.toString() + "/lecturl_click" + id + ".txt");
//			ObjectInputStream objectInputStream = new ObjectInputStream(freader);
//			map = (Map<String, String>) objectInputStream.readObject();
//			freader.close();
//			objectInputStream.close();
//			return map.get("lecture_click_count");
//		} catch (Exception e) {
//			// TODO: handle exception
//			return null;
//		}
//
//	}

}
