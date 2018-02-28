package org.soshow.beautyedu;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.basketball.common.adapter.CommonAdapter;
import org.soshow.basketball.common.adapter.ViewHolder;
import org.soshow.beautyedu.bean.MyLecture;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.bean.MyCourseInfo;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VideoLectureFragment extends Fragment implements OnHeaderRefreshListener, OnFooterRefreshListener {

	private String TAG = "VideoLectureFragment";
	private SharedPreferences sp;
	private Editor editor;
	private ListView list_record;
	private TextView list_no_record;
	private PullToRefreshView mPullToRefreshView;
	private CommonAdapter<MyLecture> adapter;
	private String mToken;
	private String app_nonce;
	private int startPage = 1;
	private int pageSize = 10;
	private boolean hasMore;
	
	private List<MyLecture> myLectures;
	
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
						getInfo(startPage);
					} else {
						Toast.makeText(getActivity(), "网络不佳，请稍后再试",
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = null;
		if(rootView == null){
			rootView = inflater.inflate(R.layout.activity_record_collect, null);
			sp = getActivity().getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
			editor = sp.edit();
			myLectures= new ArrayList<MyLecture>();
			list_record = (ListView) rootView.findViewById(R.id.list_record);
			list_no_record = (TextView) rootView.findViewById(R.id.list_no_record);
			mPullToRefreshView = (PullToRefreshView) rootView.findViewById(R.id.main_pull_refresh_view);
			mPullToRefreshView.setOnHeaderRefreshListener(this);
			mPullToRefreshView.setOnFooterRefreshListener(this);
		}
		getTockenLocal();
		adapter = new CommonAdapter<MyLecture>(getActivity(),
				myLectures, R.layout.item_mycourse_list) {


			@Override
			public void convert(ViewHolder holder, MyLecture info) {
				ImageView iv = holder.getView(R.id.my_lecture_record_pic);
				TextView tvTitle = holder.getView(R.id.my_lecture_tv_title);
				TextView tvCount = holder.getView(R.id.my_lecture_tv_look);
				TextView tvCharge = holder.getView(R.id.my_lecture_tv_price);
				holder.getView(R.id.lecture_tag).setVisibility(View.INVISIBLE);;
				UniversalImageLoadTool.disPlayTrue(info.getImage_url(), iv, R.drawable.defaultpic);
				tvTitle.setText(info.getLecture_title());
				tvCount.setText(info.getLecture_click_count()+"人观看");
				tvCharge.setText("￥"+info.getPrice());
			}
		};
		list_record.setAdapter(adapter);
		return rootView;
	}

	private void getTockenLocal() {
		app_nonce = StringUtil.getPhoneIMEI(getActivity());
		mToken = new TokenManager(getActivity()).getToken();
		if (mToken != null) {
			getInfo(startPage);
		} else {
			LoginUtil.login_handlered = false;
			Message msg_loop = handler.obtainMessage();
			msg_loop.what = 2;
			msg_loop.sendToTarget();
		}
		
	}

	private void getInfo(int page) {
		String url_my_lecture = Constant.MY_LECTURES+"&tocken="+mToken+"&app_nonce="+app_nonce+"&page_no=" + page;
		Log.e(TAG, "我的课程url="+url_my_lecture);
		NetHelper.get(url_my_lecture , new SimpleSingleBeanNetHandler<Captcha>(getActivity()) {

			@Override
			protected void onSuccess(Captcha bean) {
				String result = bean.result;
				try {
					if(result.equals("0")){
						JSONArray jsonArray = new JSONObject(bean.info).getJSONArray("data");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject obj = (JSONObject) jsonArray.get(i);
							MyLecture myLecture = GsonUtils.parseJSON(obj.toString(), MyLecture.class);
							myLectures.add(myLecture);
						}
						adapter.notifyDataSetChanged();
					}else{
						ToastUtil.getInstance().showToast(getActivity(), bean.message);
					}
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}


	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		
	}
}
