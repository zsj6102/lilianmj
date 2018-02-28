package org.soshow.beautyedu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.basketball.common.adapter.CommonAdapter;
import org.soshow.basketball.common.adapter.ViewHolder;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.activity.LoginInputActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.bean.CommentInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TimeUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AllCommentActivity extends BaseActivity implements OnHeaderRefreshListener, OnFooterRefreshListener, OnClickListener {

	public static boolean IS_ADD_RECOMMENT;
	public static String RECOMMENT_NUM;
	private SharedPreferences sp;
	private Editor editor;
	private int startPage = 1;
	private int pageSize = 10;
	private View tvView;
	private String app_nonce;
	private String mToken;
	private TextView title;
	private boolean hasMore;
	private ListView list_comment;
	private PullToRefreshView mPullToRefreshView;
	private CommonAdapter<CommentInfo> commomtAdapter;
	private Context context;
	
	private List<CommentInfo> commentInfos;
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 2:// token获取
				if (LoginUtil.login_handlered) {
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
						initRemark(startPage);
					} else {
						Toast.makeText(context, "网络不佳，请稍后再试",
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
	private LinearLayout loading;
	private EditText et;
	private Dialog dialog;
	private String from;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_all_comment);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_logininput);
		context = AllCommentActivity.this;
		dialog = ProgressDialogUtil.createLoadingDialog(context, null, true, false);
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		from = getIntent().getStringExtra("from");
		initView();
		getTocken();
	}

	private void initView() {
		commentInfos = new ArrayList<CommentInfo>();
		title = (TextView) findViewById(R.id.title_name);
		
		loading = (LinearLayout) findViewById(R.id.linear_load);
		list_comment = (ListView) findViewById(R.id.list_all_comment);
		mPullToRefreshView = (PullToRefreshView) findViewById(R.id.all_comment_refreshListView);
		et = (EditText) findViewById(R.id.all_commemt_et);
		findViewById(R.id.all_commemt_tv_send).setOnClickListener(this);;
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		
		commomtAdapter = new CommonAdapter<CommentInfo>(context,commentInfos,R.layout.view_offline_commomt) {

			@Override
			public void convert(ViewHolder holder, CommentInfo comment) {
				TextView tvName = holder.getView(R.id.off_line_recommemt_tv_name);
				TextView tvContent = holder.getView(R.id.off_line_recommemt_tv_content);
				TextView tvTime = holder.getView(R.id.off_line_recommemt_tv_time);
				tvName.setText(comment.getUser_nickname());
				tvContent.setText(comment.getContent());
				String strTime = comment.getCreate_time();
				if(!TextUtils.isEmpty(strTime)&&strTime != null){
					String time = TimeUtil.getStringTime(Long.valueOf(strTime));
					tvTime.setText(time);
				}else{
					tvTime.setText(strTime);
				}
			}
		};
		list_comment.setAdapter(commomtAdapter);
	}
	
	private void getTocken() {
		startPage  = 1;
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(this).getToken();
		if (mToken != null) {
			loading.setVisibility(View.VISIBLE);
			initRemark(startPage);
		} else {
			LoginUtil.login_handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}
		
	}
	
	/*
	 * 初始化评论列表
	 */
	public void initRemark(int page){
		String type = "";
		String url = "";
		if(!TextUtils.isEmpty(from)&&from !=null){
			if(from.equals(TuwenActivity.TU_WEN)){//图文
				url = Constant.LECTURE_COMMENT_LIST + "&tocken=" + mToken
						+ "&app_nonce=" + app_nonce + "&lecture_id=" + getIntent().getStringExtra("type_id")+"&page_no="+page+"&page_size="+pageSize;
				Log.e("", "图文课程评论列表URL=" + url);
			}else if(from.equals(LineCourseActivity.OFF_LINE)){//线下
				url = Constant.OFFLINE_COMMENT_LIST + "&tocken=" + mToken
						+ "&app_nonce=" + app_nonce + "&offline_id=" + getIntent().getStringExtra("type_id")+"&page_no="+page+"&page_size="+pageSize;
				Log.e("", "线下课程评论列表URL=" + url);
			}
			
		}else{//培训
			url = Constant.LECTURE_COMMENT_LIST + "&tocken=" + mToken
					+ "&app_nonce=" + app_nonce + "&lecture_id=" + getIntent().getStringExtra("lecture_id")+"&page_no="+page+"&page_size="+pageSize;
			Log.e("", "培训课程评论列表URL=" + url);
		}
		NetHelper.get(url, new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				String result = bean.result;
				try {
					if(result.equals("0")){
						String info = bean.info;
						JSONObject jsonObject = new JSONObject(info);
						JSONArray dataObj = jsonObject.getJSONArray("data");
						String count = jsonObject.getString("count");
						String commentText = getResources().getString(R.string.all_comment);
						title.setText(commentText+"("+count+")");
						RECOMMENT_NUM = count;
						if(dataObj.length() >= pageSize ){
							hasMore = true;
						}else{
							hasMore = false;
						}
						
						for (int i = 0; i < dataObj.length(); i++) {
							JSONObject obj = (JSONObject) dataObj.get(i);
							CommentInfo commentInfo = GsonUtils.parseJSON(obj.toString(), CommentInfo.class);
							commentInfos.add(commentInfo);
						}
						LogUtils.e("数据大小="+commentInfos.size());
						if (startPage != 1) {
							mPullToRefreshView.onFooterRefreshComplete();
						} else {
							mPullToRefreshView.onHeaderRefreshComplete();
						}
						commomtAdapter.notifyDataSetChanged();
						loading.setVisibility(View.GONE);
					}else{
						ToastUtil.getInstance().showToast(AllCommentActivity.this, "数据获取异常");
					}
					
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		if (hasMore) {
			startPage++;
			initRemark(startPage);
		} else {
			Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
			mPullToRefreshView.onFooterRefreshComplete();
		}

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		startPage = 1;
		commentInfos.clear();
		initRemark(startPage);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_search_logininput:
			finish();
			overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
			break;
		case R.id.all_commemt_tv_send:
			if (MyApplication.logined == false) {
				Intent intent = new Intent(context,
						LoginInputActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.anim_slider_right_in,
		                R.anim.anim_slider_left_out);
			} else {
			String content = et.getText().toString().trim();
			if(!TextUtils.isEmpty(content)||!content.equals("")){
				if(content.length() < 200){
					dialog.show();
					addComment(content);
				}else{
					ToastUtil.getInstance().showToast(this, getResources().getString(R.string.can_no_over));	
				}
			}else{
				ToastUtil.getInstance().showToast(this, getResources().getString(R.string.can_no_empty));
			}
			}
			break;

		default:
			break;
		}
	}

	/*
	 * 添加培训、图文课程评论
	 */
	private void addComment(String content) {
		String type = "";
		String typeUrl = "";
		String url = "";
		Map<String, String> params = new HashMap<String, String>();
		if(!TextUtils.isEmpty(from)&&from !=null){
			if(from.equals(TuwenActivity.TU_WEN)){//图文
				url = Constant.ADD_LECTURE_COMMENT + "&tocken=" + mToken
						+ "&app_nonce=" + app_nonce + "&lecture_id=" + getIntent().getStringExtra("type_id")+"&content="+content;
				typeUrl = Constant.ADD_LECTURE_COMMENT;
				params.put("lecture_id", getIntent().getStringExtra("type_id"));
				Log.e("", "添加图文课程评论URL=" + url);
			}else if(from.equals(LineCourseActivity.OFF_LINE)){//线下
				type = "offline_id";
				url = Constant.ADD_OFFLINE_COMMENT + "&tocken=" + mToken
						+ "&app_nonce=" + app_nonce + "&offline_id=" + getIntent().getStringExtra("type_id")+"&content="+content;
				typeUrl = Constant.ADD_OFFLINE_COMMENT;
				params.put("offline_id", getIntent().getStringExtra("type_id"));
				Log.e("", "添加线下课程评论URL=" + url);
			}
		}else{//培训
			url = Constant.ADD_LECTURE_COMMENT + "&tocken=" + mToken
					+ "&app_nonce=" + app_nonce + "&lecture_id=" + getIntent().getStringExtra("lecture_id")+"&content="+content;
			typeUrl = Constant.ADD_LECTURE_COMMENT;
			params.put("lecture_id", getIntent().getStringExtra("lecture_id"));
			Log.e("", "添加培训课程评论URL=" + url);
		}
		params.put("tocken", mToken);
		params.put("app_nonce", app_nonce);
		params.put("content", content);
		NetHelper.post(typeUrl, params , new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				IS_ADD_RECOMMENT = true;
				LogUtils.e("bean.result="+bean.result);
				dialog.dismiss();
				et.setText("");
				ToastUtil.getInstance().showToast(context, bean.message);
				commentInfos.clear();
				initRemark(startPage);
				
			}
			
		});
		
	}
	
	

}
