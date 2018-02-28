package org.soshow.beautyedu.activity.dynamic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.basketball.common.adapter.CommonAdapter;
import org.soshow.basketball.common.adapter.ViewHolder;
import org.soshow.beautydu.photo.SelectPhoto;
import org.soshow.beautydu.photo.localphoto.PictureShowActivity;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.bean.FindListInfo;
import org.soshow.beautyedu.bean.FindListInfo.CommentsEntity;
import org.soshow.beautyedu.bean.FindListInfo.CommentsEntity.ReplyEntity;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.login.CircularImage;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.DensityUtil;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.SPUtils;
import org.soshow.beautyedu.utils.ScreenUtils;
import org.soshow.beautyedu.utils.SoftInputUtil;

import org.soshow.beautyedu.utils.TimeUtil;
import org.soshow.beautyedu.utils.ToastUtil;

import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.MyCustomListView;
import org.soshow.beautyedu.widget.MyDialog;
import org.soshow.beautyedu.widget.NoScrollGridView;


import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static org.soshow.beautyedu.application.MyApplication.dialog;
public class PerDynamicDetailActivity extends BaseActivity implements OnClickListener {

	private String TAG = "PerDynamicDetailActivity";
	public static boolean isZan;
	public static boolean isRecomment;
	private Context context;
//	private String app_nonce;
	private String mToken;
	private LinearLayout loading;
	private SharedPreferences sp;
	private Editor editor;
	private FindListInfo findListInfo;
	
	private PopupWindow popupWindow;
	private String type = "";// goods_act:点赞 ,comment_act:评论
	private TextView tvZan;// 点赞

	public Context getContext() {
		return this;
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:// token获取
				if (LoginUtil.login_handlered) {
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
//						getInfo();
					} else {
						Toast.makeText(PerDynamicDetailActivity.this, "网络不佳，请稍后再试",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 2;
							msg_loop.sendToTarget();
						}
					}, 1000);

				}
				break;
			default:
				break;
			}
		}

	};
	private CircularImage ivHead;
	private TextView tvName;
	private TextView tvContent;
	private NoScrollGridView gridView;
	private TextView tvAddTime;
	private TextView tvZanNum;
	private MyCustomListView listView;
	private ImageView ivRight;
	private CommonAdapter<CommentsEntity> commonAdapters;//评论adapter
	private InputMethodManager imm;
	private View popLayout;
	private PopupWindow popBirth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_per_dysnamic_detail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		context = PerDynamicDetailActivity.this;
		initView();

	}

	private void initView() {
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		popLayout = getLayoutInflater().inflate(
                R.layout.view_pop, null);
		popBirth = new PopupWindow(popLayout,
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		popBirth.setBackgroundDrawable(new BitmapDrawable());
        popBirth.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popBirth.setFocusable(true);
        popBirth.setTouchable(true);
        
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		
		TextView title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText("动态详情");
		
		loading = (LinearLayout) findViewById(R.id.linear_load);
		ivHead = (CircularImage) findViewById(R.id.per_detail_iv_head);
		tvName = (TextView) findViewById(R.id.per_detail_tv_name);
		tvContent = (TextView) findViewById(R.id.per_detail_tv_content);
		gridView = (NoScrollGridView) findViewById(R.id.per_detail_gridView);
		tvAddTime = (TextView) findViewById(R.id.per_detail_tv_time);
		ivRight = (ImageView) findViewById(R.id.per_detail_iv_right);
		tvZanNum = (TextView) findViewById(R.id.zan_goods_count);
		listView = (MyCustomListView) findViewById(R.id.per_detail_listview);
//		loading.setVisibility(View.VISIBLE);
//		getInfo();
		getContent();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_search_logininput:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
			break;
		case R.id.rl_right:
			
			break;

		default:
			break;
		}
	}
    private void getContent(){
		String url_per_detail = Constant.DYNAMIC_BY_ID+"&dynamic_id=" + getIntent().getStringExtra("dynamic_id");
		NetHelper.get(url_per_detail, new SimpleSingleBeanNetHandler<Captcha>(context) {
			@Override
			protected void onSuccess(Captcha bean) {
				String result = bean.result;
				try{
					if(result.equals("0")){
						JSONObject obj = new JSONObject(bean.info);
						findListInfo = GsonUtils.parseJSON(obj.toString(), FindListInfo.class);
						tvName.setText(findListInfo.getNickname());
						if(TextUtils.isEmpty(findListInfo.getContent().trim()) || findListInfo.getContent().trim().equals("")){
							tvContent.setVisibility(View.GONE);
						}else{
							tvContent.setVisibility(View.VISIBLE);
							tvContent.setText(findListInfo.getContent());
						}
						tvZanNum.setText(findListInfo.getGoods_num().toString());
						gridView.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								ArrayList<String> imageUrls = new ArrayList<String>();
								String photos_url = findListInfo.getPhotos_url();
								String[] photoarray = null;
								photoarray = photos_url.split(";");
								for (int i = 0; i < photoarray.length; i++) {
									imageUrls.add(photoarray[i]);
								}

								Intent intent = new Intent(PerDynamicDetailActivity.this, PictureShowActivity.class);
								intent.putStringArrayListExtra(PictureShowActivity.EXTRA_IMAGEURLS, imageUrls);
								intent.putExtra(PictureShowActivity.EXTRA_CURRENT, position);
								startActivityForResult(intent, SelectPhoto.DELETE_IMAGE);
								 overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
							}
						});
						ArrayList<String> imageUrls = new ArrayList<String>();
						String photos_url = findListInfo.getPhotos_url();
						String[] photoarray = null;
						photoarray = photos_url.split(";");
						for (int i = 0; i < photoarray.length; i++) {
							imageUrls.add(photoarray[i]);
						}
						CommonAdapter<String>  picAdapter = new CommonAdapter<String>(context, imageUrls, R.layout.item_fragment_find_pic) {

							@Override
							public void convert(ViewHolder holder_pic, String pic) {
								ImageView ivContent = holder_pic.getView(R.id.fragmentfind_pics);
								int magin = DensityUtil.dip2px(PerDynamicDetailActivity.this, 2);
								int left = DensityUtil.dip2px(PerDynamicDetailActivity.this, 6);
								int right = DensityUtil.dip2px(PerDynamicDetailActivity.this, 13);
								int screenWidth = ScreenUtils.getScreenWidth(PerDynamicDetailActivity.this);
								int width = screenWidth - left - right;
								ArrayList<String> imageUrls = new ArrayList<String>();

								String[] photoarray = null;
								photoarray = findListInfo.getPhotos_url().split(";");
								for (int i = 0; i < photoarray.length; i++) {
									imageUrls.add(photoarray[i]);
								}
								if (imageUrls.size() == 1) {
									gridView.setNumColumns(2);
									ivContent.setScaleType(ScaleType.CENTER_CROP);
									LayoutParams layoutParams = new LayoutParams(screenWidth / 2, screenWidth / 2);
									ivContent.setLayoutParams(layoutParams);
									UniversalImageLoadTool.disPlayTrue(pic, ivContent, R.drawable.defaultpic);
								} else if (imageUrls.size() == 4) {
									gridView.setNumColumns(2);
									ivContent.setScaleType(ScaleType.CENTER_CROP);
									LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, screenWidth / 3);
									layoutParams.setMargins(0, 0, magin, magin);
									ivContent.setLayoutParams(layoutParams);
									UniversalImageLoadTool.disPlayTrue(pic, ivContent, R.drawable.defaultpic);
								} else {
									gridView.setNumColumns(3);
									ivContent.setScaleType(ScaleType.CENTER_CROP);
									LayoutParams layoutParams = new LayoutParams(screenWidth / 4, screenWidth / 4);
									layoutParams.setMargins(0, 0, magin, magin);
									ivContent.setLayoutParams(layoutParams);
									UniversalImageLoadTool.disPlayTrue(pic, ivContent, R.drawable.defaultpic);

								}
							}
						};
						gridView.setAdapter(picAdapter);
						String addtime = findListInfo.getCreate_time();
						if (!TextUtils.isEmpty(addtime)) {
							tvAddTime.setText(addtime);
						}
						final List<CommentsEntity> commentLists = new ArrayList<FindListInfo.CommentsEntity>();
						commentLists.clear();
						LogUtils.e("评论大小=" + commentLists.size(), "评论获取的总数据："
								+ commentLists.toString());

						commentLists.addAll(findListInfo.getComments());
						commonAdapters = new CommonAdapter<CommentsEntity>(
								PerDynamicDetailActivity.this, commentLists, R.layout.comments_detail) {

							@Override
							public void convert(ViewHolder holder,
												CommentsEntity commomt) {

								RelativeLayout llTtype1 = holder.getView(R.id.commomts_detail_tyle1);
								ImageView iv1 = holder.getView(R.id.comment_detail_iv1);
								TextView tvRecommentName = holder.getView(R.id.commomts_detail_tv_name);
								TextView tvContent = holder.getView(R.id.commomts_detail_tv_content);
								TextView tvCommentTime = holder.getView(R.id.commomts_detail_tv_time);
								RelativeLayout llTtype2 = holder.getView(R.id.commomts_detail_tyle2);
								ImageView iv2 = holder.getView(R.id.comment_detail_iv2);
								TextView tvReplayFrom = holder.getView(R.id.commomts_detail_tv_replay_from);
								TextView tvReplayTo = holder.getView(R.id.commomts_detail_tv_replay_to);
								TextView tvreplayContent = holder.getView(R.id.commomts_detail_tv_replay_content);
								TextView tvreplayTime = holder.getView(R.id.commomts_detail_replay_tv_time);

								ReplyEntity reply = commomt.getReply();
								final String replyid = commomt.getReplyid();
								int replayId = Integer.valueOf(replyid);
								if (replayId > 0) {
									llTtype1.setVisibility(View.GONE);
									llTtype2.setVisibility(View.VISIBLE);

									// 回复
									//UniversalImageLoadTool.disPlayTrue(commomt.getPhoto(), iv2, R.drawable.default_face);
									if (reply != null) {
										if (TextUtils.isEmpty(reply.getReal_name())) {
											tvReplayTo.setText(reply.getUsername()+":");
										} else {
											tvReplayTo.setText(reply.getReal_name()+":");
										}
									}

									if (TextUtils.isEmpty(commomt.getReal_name())) {
										tvReplayFrom.setText(commomt.getUsername());
									} else {
										tvReplayFrom.setText(commomt.getReal_name());
									}
									tvreplayContent.setText(commomt.getContent());
									String addtime = commomt.getCreate_time_pre();
									if(!TextUtils.isEmpty(addtime)){
										tvreplayTime.setText(TimeUtil.getStringTime(Long.valueOf(addtime)));
									}

								} else {
									llTtype1.setVisibility(View.VISIBLE);
									llTtype2.setVisibility(View.GONE);

									// 评论
									//UniversalImageLoadTool.disPlayTrue(commomt.getPhoto(), iv1, R.drawable.default_face);
									if (TextUtils.isEmpty(commomt.getReal_name())) {
										tvRecommentName.setText(commomt.getUsername()+":");
									} else {
										tvRecommentName.setText(commomt.getReal_name()+":");
									}
									tvContent.setText(commomt.getCreate_time_pre());
									String addtime = commomt.getCreate_time_pre();
									if(!TextUtils.isEmpty(addtime)){
										tvCommentTime.setText(TimeUtil.getStringTime(Long.valueOf(addtime)));
									}

								}

							}
						};

						listView.setAdapter(commonAdapters);//评论列表

						// 点赞与评论的显示与隐藏操作
						ivRight.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								final View view = PerDynamicDetailActivity.this.getLayoutInflater().inflate(
										R.layout.popwindow_layout, null);
								tvZan = (TextView) view.findViewById(R.id.zan);
								if (findListInfo.getIs_liked().equals("1")) {
									tvZan.setText("取消");
								} else if (findListInfo.getIs_liked().equals("0")) {
									tvZan.setText("赞");
								}

								int width = DensityUtil.dip2px(context, 141);
								int height = DensityUtil.dip2px(context, 40);
								popupWindow = new PopupWindow(view, width, height);
								popupWindow.setFocusable(true);
								popupWindow.setOutsideTouchable(true);
								popupWindow.setBackgroundDrawable(new BitmapDrawable());
								int[] location = new int[2];
								ivRight.getLocationOnScreen(location);
								popupWindow.showAtLocation(ivRight, Gravity.NO_GRAVITY,
										location[0] - popupWindow.getWidth(),
										location[1] - 6);

								// 点赞操作
								view.findViewById(R.id.find_item_ll_zan)
										.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												type = "goods_act";
												remarkZan();
											}
										});

								// 评论操作
								view.findViewById(R.id.find_item_ll_say)
										.setOnClickListener(new OnClickListener() {

											@Override
											public void onClick(View v) {
												popupWindow.dismiss();
//											final MyDialog.MyBuilder dailog = new MyDialog.MyBuilder(context);
//											dailog.setPositiveButton(
//													"确定",
//													new DialogInterface.OnClickListener() {
//
//														@Override
//														public void onClick(
//																DialogInterface dialog,
//																int which) {
//															type = "comment_act";
//															EditText editText = (EditText) dailog
//																	.getView()
//																	.findViewById(
//																			R.id.ed_input);
//															String contentText = editText
//																	.getText()
//																	.toString()
//																	.trim();
//															if (contentText
//																	.equals("")) {
//																ToastUtil
//																		.getInstance()
//																		.showToast(context,
//																				"评论不能为空");
//															} else {
//																remarkComment(contentText,"",commentLists);
//																dialog.dismiss();
//															}
//
//														}
//													});
//
//											dailog.setNagetiveButton(
//													"取消",
//													new DialogInterface.OnClickListener() {
//
//														@Override
//														public void onClick(
//																DialogInterface dialog,
//																int which) {
//															dialog.dismiss();
//
//														}
//													});
//
//											dailog.creatDialog().show();

												//异步弹出软键盘
												handler.postDelayed(new Runnable() {

													@Override
													public void run() {
														imm = (InputMethodManager) view.findViewById(R.id.find_item_ll_say).getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
														imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
													}
												}, 0);

												popLayout.findViewById(R.id.pop_view_top).setOnClickListener(
														new OnClickListener() {

															@Override
															public void onClick(View v) {
																popBirth.dismiss();
															}
														});
												popLayout.findViewById(R.id.pop_tv_send).setOnClickListener(new OnClickListener() {

													@Override
													public void onClick(View v) {
														type = "comment_act";
														EditText etRecomment = (EditText) popLayout.findViewById(R.id.pop_et_comment);
														String contentText = etRecomment.getText().toString().trim();
														if (contentText.equals("")) {
															ToastUtil.getInstance().showToast(PerDynamicDetailActivity.this,"评论不能为空");
														} else {
															remarkComment(contentText,"",commentLists);
															etRecomment.setText("");
															popBirth.dismiss();
														}

													}
												});

												popLayout.findViewById(R.id.pop_et_comment).setOnFocusChangeListener(new OnFocusChangeListener() {

													@Override
													public void onFocusChange(View v, boolean hasFocus) {
														LogUtils.e("hasFocus=========="+hasFocus);
														if (hasFocus) {
															imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
														} else {
															imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
																	InputMethodManager.HIDE_NOT_ALWAYS);
														}

//													EditText et = (EditText) v;
//													if(hasFocus){
//														et.requestFocus();
//													}else{
//														SoftInputUtil.hideSoftInput(PerDynamicDetailActivity.this);
//													}
													}
												});

												popBirth.showAtLocation(findViewById(R.id.per_dysnamic_activity_detail_rl), Gravity.BOTTOM
														| Gravity.CENTER_HORIZONTAL, 0, 0);

											}
										});
							}
						});

					/*
					 * 单击回复评论
					 */
						listView.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent, final View view,
													final int position, long id) {
//							final MyDialog.MyBuilder dailog = new MyDialog.MyBuilder(context);
//							dailog.setPositiveButton("确定",
//									new DialogInterface.OnClickListener() {
//
//										@Override
//										public void onClick(DialogInterface dialog,
//												int which) {
//											type = "comment_act";
//											EditText editText = (EditText) dailog
//													.getView().findViewById(
//															R.id.ed_input);
//											String contentText = editText.getText()
//													.toString().trim();
//											if (contentText.equals("")) {
//												ToastUtil.getInstance().showToast(context, "评论不能为空");
//											} else {
//												remarkComment(contentText,commentLists.get(position).getComment_id(),commentLists);
//												dialog.dismiss();
//											}
//
//										}
//									});
//
//							dailog.setNagetiveButton("取消",
//									new DialogInterface.OnClickListener() {
//
//										@Override
//										public void onClick(DialogInterface dialog,
//												int which) {
//											dialog.dismiss();
//
//										}
//									});
//
//							dailog.creatDialog().show();


								//异步弹出软键盘
								handler.postDelayed(new Runnable() {

									@Override
									public void run() {
										imm = (InputMethodManager) view.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
										imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
									}
								}, 0);

								popLayout.findViewById(R.id.pop_view_top).setOnClickListener(
										new OnClickListener() {

											@Override
											public void onClick(View v) {
												popBirth.dismiss();
											}
										});
								popLayout.findViewById(R.id.pop_tv_send).setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										type = "comment_act";
										EditText etRecomment = (EditText) popLayout.findViewById(R.id.pop_et_comment);
										String contentText = etRecomment.getText().toString().trim();
										if (contentText.equals("")) {
											ToastUtil.getInstance().showToast(PerDynamicDetailActivity.this,"评论不能为空");
										} else {
											remarkComment(contentText,commentLists.get(position).getComment_id(),commentLists);
											etRecomment.setText("");
											popBirth.dismiss();
										}

									}
								});

								popLayout.findViewById(R.id.pop_et_comment).setOnFocusChangeListener(new OnFocusChangeListener() {

									@Override
									public void onFocusChange(View v, boolean hasFocus) {
										LogUtils.e("hasFocus=========="+hasFocus);
										EditText et = (EditText) v;
										if(hasFocus){
											et.requestFocus();
										}else{
											SoftInputUtil.hideSoftInput(PerDynamicDetailActivity.this);
										}
									}
								});

								popBirth.showAtLocation(
										(PerDynamicDetailActivity.this).findViewById(R.id.per_dysnamic_activity_detail_rl), Gravity.BOTTOM
												| Gravity.CENTER_HORIZONTAL, 0, 0);

							}
						});


						// 长按删除评论
						listView.setOnItemLongClickListener(new OnItemLongClickListener() {

							@Override
							public boolean onItemLongClick(AdapterView<?> parent,
														   View view, final int position, long id) {
								String user_id = (String) SPUtils.get(PerDynamicDetailActivity.this, "user_id", "");
								final CommentsEntity commentsEntity = commentLists
										.get(position);
								if(user_id.equals(commentsEntity.getUser_id())){
									deleteRecomment(commentLists, position);
								}

								return true;
							}

							private boolean deleteRecomment(
									final List<CommentsEntity> commentLists,
									final int position) {
								final CommentsEntity commentsEntity = commentLists
										.get(position);
								type = "comment_act";
								final MyDialog.MyBuilder dailog = new MyDialog.MyBuilder(context);
								dailog.setPositiveButton("确定",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog,
																int which) {
												remarkDel(commentLists.get(position).getComment_id(),commentLists,position);
												dialog.dismiss();
											}
										});

								dailog.setNagetiveButton("取消",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(DialogInterface dialog,
																int which) {
												dialog.dismiss();

											}
										});

								MyDialog creatDialog = dailog.creatDialog();
								View layout = dailog.getView();
								TextView tvTitle = (TextView) layout
										.findViewById(R.id.pop_title);
								tvTitle.setText("是否删除该评论？");
								EditText et = (EditText) layout
										.findViewById(R.id.ed_input);
								et.setVisibility(View.GONE);
								creatDialog.show();
								return true;
							}
						});
					}else {
						ToastUtil.getInstance().showToast(context, bean.message);
					}
				}
				catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}


	/*
	 * 点赞操作
	 */
	private void remarkZan(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("dynamic_id", String.valueOf(findListInfo.getId()));
		/* 点赞 */


//		Log.e(TAG, "参数：" + params.toString());
		NetHelper.post(Constant.DYNAMIC_LIKE, params, new SimpleSingleBeanNetHandler<Captcha>(this.context) {
			@Override
			protected void onSuccess(Captcha bean) {
				LogUtils.e("点赞操作数据：result=" + bean.result + "\ninfo=" + bean.info);
				Integer goods_count = findListInfo.getGoods_num();
				if( bean.isSuccess()){
					int txt;
					if ("1".equals(findListInfo.getIs_liked())) {
						findListInfo.setIs_liked("0");
						goods_count--;
						txt = R.string.cancel;
					} else{
						findListInfo.setIs_liked("1");
						goods_count++;
						txt = R.string.zan;
					}

					findListInfo.setGoods_num(Math.max(goods_count, 0));
					tvZanNum.setText(goods_count+"");
					Toast.makeText(getContext(), txt, Toast.LENGTH_SHORT).show();
				}else{
					ToastUtil.getInstance().showToast(getContext(), bean.message);
				}
			}

			@Override
			public void complete() {
				super.complete();
				hideProcessing();
			}
		});
	}
	
	/* 添加评论操作 */
	private void remarkComment(String contentText,String commentId,final List<CommentsEntity> commentLists){
		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);

		params.put("dynamic_id", String.valueOf(findListInfo.getId()));
		params.put(type, "add");
		params.put("comment", contentText);
		params.put("replyid", commentId);
		
		NetHelper.post(Constant.DYNAMIC_COMMENT_DELETE, params,
				new SimpleSingleBeanNetHandler<Captcha>(context) {

					@Override
					protected void onSuccess(Captcha bean) {
						LogUtils.e("添加评论返回的数据："+bean.info);
						if(bean.result.equals("0")){
							isRecomment = true;
							String info = bean.info;
							try {
								JSONObject jsonObject = new JSONObject(info);
								CommentsEntity commentsEntity = GsonUtils
										.parseJSON(jsonObject.toString(),
												CommentsEntity.class);
								commentLists.add(commentsEntity);
								findListInfo.setComments(commentLists);
								commonAdapters.notifyDataSetChanged();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}else{
							ToastUtil.getInstance().showToast(context, bean.message);
						}
					}

				});
	}
	
	/*
	 * 长按删除评论
	 */
	private void remarkDel(String commentId,final List<CommentsEntity> commentLists,final int position){
		Map<String, String> params = new HashMap<String, String>();
		params.put("tocken", mToken);

		params.put("dynamic_id", String.valueOf(findListInfo.getId()));
		params.put(type, "delete");
		params.put("comment_id", commentId);
//		Log.e(TAG, "参数：" + params.toString());
		NetHelper.post(Constant.DYNAMIC_COMMENT_DELETE, params, new SimpleSingleBeanNetHandler<Captcha>(context) {
			@Override
			protected void onSuccess(Captcha bean) {
				isRecomment = true;
				commentLists.remove(position);
				findListInfo.setComments(commentLists);
				commonAdapters.notifyDataSetChanged();
				Log.e("", "删除评论：" + commentLists.size());
				ToastUtil.getInstance().showToast(context, bean.message);
			}
		});
	}

	public void hideProcessing(){
		if (dialog != null) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					dialog.dismiss();
				}
			});
		}
	}

}
