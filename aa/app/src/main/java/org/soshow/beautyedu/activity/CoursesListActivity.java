package org.soshow.beautyedu.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.SalonListActivity;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.DensityUtil;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 标准课程工种选择
 */
public class CoursesListActivity extends BaseActivity implements OnClickListener {
	private String TAG = "CoursesListActivity";
	public static CoursesListActivity context;
	private TextView title_name;
	private LinearLayout llType;
	private String app_nonce;
	private String mToken;
	private SharedPreferences sp;
	private Editor editor;
	private List<String> typeDatas;
	private LinearLayout loading;
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
						getType();
					} else {
						Toast.makeText(CoursesListActivity.this, getResources().getString(R.string.generic_error),
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_video_course);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar_logininput);
		iniView();
		getTokenLocal();

	}

	private void iniView() {
		context = CoursesListActivity.this;
		sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
		editor = sp.edit();
		title_name = (TextView) findViewById(R.id.title_name);
		title_name.setText(R.string.vip_title);
		loading = (LinearLayout) findViewById(R.id.linear_load);
		llType = (LinearLayout) findViewById(R.id.video_course_ll);
	}

	private void getTokenLocal() {
		typeDatas = new ArrayList<String>();
		app_nonce = StringUtil.getPhoneIMEI(this);
		mToken = new TokenManager(CoursesListActivity.this).getToken();
		if (mToken != null) {
			loading.setVisibility(View.VISIBLE);
			getType();
		} else {
			TokenManager.handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}

	}

	// 获取培训课程的类别
	private void getType() {
		String url_type = Constant.OFF_LINE_TYPE + "&tocken=" + mToken
				+ "&app_nonce=" + app_nonce + "&type=lecture";
		Log.e(TAG, "培训课程类别url=" + url_type);
		typeDatas.add("");
		NetHelper.get(url_type, new SimpleSingleBeanNetHandler<Captcha>(this) {

			@Override
			protected void onSuccess(Captcha bean) {
				String result = bean.result;
				if (result.equals("0")) {
					String info = bean.info;
					int padding_top = DensityUtil.dip2px(CoursesListActivity.this, 13);
					int padding_left = DensityUtil.dip2px(CoursesListActivity.this, 8);
					int padding_bottom = DensityUtil.dip2px(CoursesListActivity.this, 10);
					int h = DensityUtil.dip2px(CoursesListActivity.this, 120);
					int w = DensityUtil.dip2px(CoursesListActivity.this, 600);
					DisplayMetrics dm = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(dm);
					int xp = dm.widthPixels;
					int w1 = xp-padding_bottom*2;
					int yp = w1 * 2/5;
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w1,yp);
					params.setMargins(0, 0, 0, 0);
					llType.removeAllViews();

					try {
						JSONArray jsonArray = new JSONArray(info);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject obj = (JSONObject) jsonArray.get(i);
							final String category_id = obj
									.getString("category_id");
							final String type_name = obj.getString("name");
							String imgUrl = obj.getString("pic_url");
							typeDatas.add(category_id);

							View view = LayoutInflater.from(CoursesListActivity.this).inflate(R.layout.view_vip_type, null);
							ImageView imageView = (ImageView) view.findViewById(R.id.video_activity_major);
//							ImageView imageView = new ImageView(context);
							imageView.setScaleType(ScaleType.CENTER_CROP);
							imageView.setLayoutParams(params);
							UniversalImageLoadTool.disPlayTrue(imgUrl,imageView, R.drawable.defaultpic);
//							imageView.setBackgroundResource(R.drawable.shape_bg_iv_gray);
							llType.addView(view);
							
							//点击事件跳转页面
							view.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									Intent intent = new Intent(context,
											SalonListActivity.class);
									intent.putExtra("category_id", category_id);
									intent.putExtra("type_name", type_name);
									Log.e("", "category_id =" + category_id);
									startActivity(intent);
									overridePendingTransition(R.anim.anim_slider_right_in,
							                R.anim.anim_slider_left_out);
								}
							});
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else{
					ToastUtil.getInstance().showToast(context, bean.message);
				}
				loading.setVisibility(View.GONE);
			}

		});
	}


	public void onClick(View v) {
		Intent intent = null;
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


	public Handler getHandler() {
		return handler;
	}

	@Override
	public void finish() {
		super.finish();
		context = null;
		try {
			handler.removeCallbacksAndMessages(null);
		} catch (Exception e) {
		}
	}

}
