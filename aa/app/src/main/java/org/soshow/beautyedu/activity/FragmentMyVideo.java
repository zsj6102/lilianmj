package org.soshow.beautyedu.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.adapters.VideoCollectionAdapter;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.LectureList;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleMultiBeanNetHandler;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 视频收藏
 */
public class FragmentMyVideo extends BaseFragment implements OnHeaderRefreshListener, OnFooterRefreshListener {
	private View rootView;
	private ListView list_record;
	// public static final String ARG_SECTION_NUMBER = "section_number";
	private SharedPreferences sp;
	private Editor editor;
	private String mToken;
	public static FragmentMyVideo fmv_instance;
	private TextView list_no_record;
	private boolean isrefreshing = false;
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				getTokenLocal();
				break;
			case 2:
				if (LoginUtil.login_handlered) {
					Log.d("345abc", "获取新token-Myvideo");
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);
					if (mToken != null) {
						getInfo();
					} else {

						if (getActivity() != null) {
							isrefreshing = false;
							Toast.makeText(getActivity(), "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
							Log.d("345abc", "循环等待-Myvideo");
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 2;
							msg_loop.sendToTarget();
						}
					}, 1000);

				}
				break;
			case 3:
				try {
					String click;
					list_no_record.setVisibility(View.GONE);
					if (page_no == 1) {
						listItems = new ArrayList<Map<String, Object>>();
						// MyApplication.list_lecture.clear();
					}
					if (listItems_cache.size() > ((page_no - 1) * 10)) {// 当前页仍有数据
						if (listItems_cache.size() - ((page_no - 1) * 10) >= 10) {// 当前页数据数量足够10个
							for (int i = 0; i < 10; i++) {
								Map<String, Object> listItem = listItems_cache.get((page_no - 1) * 10 + i);
								click = getClickCount(listItem.get("lecture_id").toString());
								if (click != null) {
									listItem.put("lecture_click_count", click);
								}
								listItems.add(listItem);

							}
							if (page_no != 1) {
								Toast.makeText(getActivity(), "加载成功", Toast.LENGTH_SHORT).show();
							}
						} else {// 当前页数据不够10个
							for (int i = 0; i < listItems_cache.size() - ((page_no - 1) * 10); i++) {
								Map<String, Object> listItem = listItems_cache.get((page_no - 1) * 10 + i);
								click = getClickCount(listItem.get("lecture_id").toString());
								if (click != null) {
									listItem.put("lecture_click_count", click);
								}
								listItems.add(listItem);

							}
							if (page_no != 1) {
								Toast.makeText(getActivity(), "加载成功", Toast.LENGTH_SHORT).show();
							}
						}
					} else {// 本地无更多数据
						if (page_no != 1) {
							Toast.makeText(getActivity(), "无更多收藏记录,请下拉刷新获取更多", Toast.LENGTH_SHORT).show();
							page_no--;
							mPullToRefreshView.onFooterRefreshComplete();
						}
					}

					if (page_no == 1 && vca == null) {
						vca = new VideoCollectionAdapter(getActivity(), listItems);
						list_record.setAdapter(vca);
						list_cache = false;
						isrefreshing = true;
						getTokenLocal();

					} else {
						try {
							vca.notifyDataSetChanged();
							mPullToRefreshView.onFooterRefreshComplete();// 结束刷新
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				} catch (Exception e) {
					// TODO: handle exception
					list_cache = false;
					page_no = 1;
					// isrefreshing = true;
					getTokenLocal();
				}

				break;
			default:
				break;
			}
		}

	};
	PullToRefreshView mPullToRefreshView;
	private int page_no = 1;
	public static VideoCollectionAdapter vca;
	public static List<Map<String, Object>> listItems;
	// private Boolean refresh = true;
	private boolean mHasLoadedOnce;
	private boolean isPrepared;
	private boolean list_cache = true;
	private List<Map<String, Object>> listItems_cache = new ArrayList<Map<String, Object>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		fmv_instance = this;
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.activity_record_collect, null);
			sp = getActivity().getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
			editor = sp.edit();
			list_record = (ListView) rootView.findViewById(R.id.list_record);
			list_no_record = (TextView) rootView.findViewById(R.id.list_no_record);
			mPullToRefreshView = (PullToRefreshView) rootView.findViewById(R.id.main_pull_refresh_view);
			mPullToRefreshView.setOnHeaderRefreshListener(this);
			mPullToRefreshView.setOnFooterRefreshListener(this);
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
		super.onStart();
		Log.d("345abc", "video可见");

		// getTokenLocal();
	}

	@SuppressWarnings("unchecked")
	private void getTokenLocal() {
		if (list_cache) {// 使用本地数据
			try {
				if (listItems_cache == null || listItems_cache.isEmpty()) {// 第一次加载本地数据
					FileInputStream freader = new FileInputStream(
							getActivity().getCacheDir().toString() + "/myvideo_list.txt");
					ObjectInputStream objectInputStream = new ObjectInputStream(freader);
					listItems_cache = (List<Map<String, Object>>) objectInputStream.readObject();
					objectInputStream.close();
				}

				if (listItems_cache == null || listItems_cache.isEmpty()) {// 加载数据为空
					try {// 删除无效文件
							// Toast.makeText(getActivity(), "s删除1",
							// Toast.LENGTH_SHORT).show();
						File file = new File(getActivity().getCacheDir().toString() + "/myvideo_list.txt");
						file.delete();

					} catch (Exception e) {
						// TODO: handle exception
					}
					// 重新请求数据
					list_cache = false;
					page_no = 1;
					mToken = new TokenManager(getActivity()).getToken();
					if (mToken != null) {
						getInfo();
					} else {
						LoginUtil.login_handlered = false;
						Message msg_loop1 = handler.obtainMessage();
						msg_loop1.what = 2;
						msg_loop1.sendToTarget();
					}
				} else {// 本地数据正常
					Message msg_loop = handler.obtainMessage();
					msg_loop.what = 3;
					msg_loop.sendToTarget();
				}

			} catch (Exception e) {// 本地文件获取错误，重新请求数据
				list_cache = false;
				mToken = new TokenManager(getActivity()).getToken();
				if (mToken != null) {
					getInfo();
				} else {
					LoginUtil.login_handlered = false;
					Message msg_loop = handler.obtainMessage();
					msg_loop.what = 2;
					msg_loop.sendToTarget();
				}
			}
		} else {// 直接网络请求
			mToken = new TokenManager(getActivity()).getToken();
			if (mToken != null) {
				getInfo();
			} else {
				LoginUtil.login_handlered = false;
				Message msg_loop = handler.obtainMessage();
				msg_loop.what = 2;
				msg_loop.sendToTarget();
			}
		}
	}

	private void getInfo() {
		try {
			String app_nonce = null;
			if (getActivity() != null) {
				app_nonce = StringUtil.getPhoneIMEI(getActivity());
			} else {
				return;
			}

			// String mobile = Encryption.desEncryptd(Constant.code,
			// Constant.code,
			// sp.getString("user_name", null));
			String url_mytitle = Constant.phpUrl + "/wap/api.php?action=GET_REMARK_LECTURE&tocken=" + mToken
					+ "&app_nonce=" + app_nonce + "&page_no=1" + "&page_no=" + page_no;
			// CollectActivity.dialog.show();
			Log.d("1221", "视频收藏地址为     " + url_mytitle);
			NetHelper.get(url_mytitle, new SimpleMultiBeanNetHandler<LectureList>(getActivity()) {

				@Override
				protected void onSuccess(List<LectureList> list) {
					// TODO Auto-generated method stub
					// try {//网络请求成功，删除本地数据
					// File file = new
					// File(getActivity().getCacheDir().toString() +
					// "/record_list.txt");
					// file.delete();
					// listItems_cache = new ArrayList<Map<String, Object>>();

					// } catch (Exception e) {
					// TODO: handle exception
					// }
					isrefreshing = false;
					if (page_no != 1) {
						if (getActivity() != null) {
							Toast.makeText(getActivity(), "加载成功", Toast.LENGTH_SHORT).show();
						}
						mPullToRefreshView.onFooterRefreshComplete();
					} else {
						if (getActivity() != null) {
							Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_SHORT).show();
						}
						mPullToRefreshView.onHeaderRefreshComplete();
						// MyApplication.list_lecture.clear();
					}
					// Log.d("345abc", "视频收藏返回数据为 " + list.get(0).add_time);
					if (page_no == 1) {
						listItems = new ArrayList<Map<String, Object>>();
					}

					for (LectureList tc : list) {
						// VideoCollection tc = list.get(i);
						// MyApplication.list_lecture.add(tc);
						if (tc.video_status.equals("2") && tc.play_urls != null) {
							Map<String, Object> listItem = new HashMap<String, Object>();
							listItem.put("lecture_title", tc.lecture_title);
							listItem.put("lecture_click_count", Integer.parseInt(tc.lecture_click_count) + 30 + "");
							// listItem.put("lecture_time", tc.add_time);
							// listItem.put("image_url", Constant.phpUrl +
							// "/upload/video_pic/s_" + tc.head_image);
							listItem.put("image_url", tc.head_image);
							listItem.put("remark", tc.remark);
							listItem.put("lecture_cat1", tc.lecture_cat1);
							listItem.put("lecture_id", tc.lecture_id);
							listItem.put("lecture_cat3", tc.lecture_cat3);
							listItem.put("lecture_questions_id", tc.lecture_questions_id);
							listItem.put("url_s", tc.url_s);
							listItem.put("url_h", tc.url_h);
							listItem.put("url_p", tc.url_p);
							listItem.put("lecture_content_url", tc.lecture_content_url);
							listItem.put("vheight_s", tc.vheight_s);
							listItem.put("vwidth_s", tc.vwidth_s);
							// Log.d("123321", "remark为"+tc.remark);
							listItem.put("price", tc.price);
							listItem.put("lecture_operate", tc.lecture_operate);
							listItem.put("lecture_id", tc.lecture_id);
							// Log.d("345abc", "我的收藏标题为=" + tc.lecture_title);
							try {// 获取数据写入本地
								FileOutputStream outStream = new FileOutputStream(getActivity().getCacheDir().toString()
										+ "/lecturl_click" + tc.lecture_id + ".txt");
								ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
								Map<String, String> map = new HashMap<String, String>();
								map.put("lecture_click_count", (Integer.parseInt(tc.lecture_click_count) + 30) + "");
								objectOutputStream.writeObject(map);
								outStream.close();
							} catch (Exception e) {
								// TODO: handle exception

							}
							listItems.add(listItem);
						}
					}
					try {// 获取数据写入本地
						FileOutputStream outStream = new FileOutputStream(
								getActivity().getCacheDir().toString() + "/myvideo_list.txt");
						ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
						objectOutputStream.writeObject(listItems);
						outStream.close();
					} catch (Exception e) {
						// TODO: handle exception

					}
					// Constant.refresh_main=true;
					if (page_no == 1 && getActivity() != null) {
						vca = new VideoCollectionAdapter(getActivity(), listItems);
						list_record.setAdapter(vca);
						// CollectActivity.dialog.hide();
					} else {
						vca.notifyDataSetChanged();
						// CollectActivity.dialog.hide();
					}
				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					isrefreshing = false;
					Log.d("345abc", "Myvideo错误=" + errorMsg);
					try {
						JSONObject jo = new JSONObject(errorMsg);

						if (jo.optString("result").equals("2") && page_no != 1) {
							if (getActivity() != null) {
								Toast.makeText(getActivity(), "无更多视频收藏", Toast.LENGTH_SHORT).show();
							}
						} else if (jo.optString("result").equals("2") && page_no == 1) {
							list_no_record.setVisibility(View.VISIBLE);
							list_no_record.setText("您当前无视频收藏");
							if (listItems != null && vca != null) {
								listItems.clear();
								vca.notifyDataSetChanged();
							}
							if (getActivity() != null) {
								Toast.makeText(getActivity(), "您当前无视频收藏", Toast.LENGTH_SHORT).show();
							}
							try {// 网络请求成功，删除本地数据
									// Toast.makeText(getActivity(), "s删除缓存",
									// Toast.LENGTH_SHORT).show();
								File file = new File(getActivity().getCacheDir().toString() + "/myvideo_list.txt");
								file.delete();
								listItems_cache = new ArrayList<Map<String, Object>>();

							} catch (Exception e) {
								// TODO: handle exception
							}
						} else if (jo.optString("result").equals("99")) {
							editor.putBoolean("token_logined", false);
							editor.remove("mToken");
							editor.commit();
						} else if (jo.optString("result").equals("1")) {
							if (sp.getBoolean("logined", false)) {
								Toast.makeText(getActivity(), "网络错误，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
								editor.putBoolean("token_logined", false);
								editor.putBoolean("logined", false);
								MyApplication.logined = false;
								new LoginUtil(getActivity(), 20);
							}
						} else {
							String mistake = jo.optString("info");
							if (getActivity() != null) {
								Toast.makeText(getActivity(), mistake, Toast.LENGTH_SHORT).show();
							}
						}
						// try {// 网络请求成功，删除本地数据
						// Toast.makeText(getActivity(), "s删除缓存",
						// Toast.LENGTH_SHORT).show();
						// File file = new
						// File(getActivity().getCacheDir().toString() +
						// "/myvideo_list.txt");
						// file.delete();
						// listItems_cache = new ArrayList<Map<String,
						// Object>>();

						// } catch (Exception e) {
						// TODO: handle exception
						// }
					} catch (Exception e) {
						// TODO: handle exception
						try {
							if (listItems_cache != null && !listItems_cache.isEmpty()) {// 网络通信失败，重新使用本地数据
								list_cache = true;
							} else if ((listItems_cache == null || listItems_cache.isEmpty())
									&& (listItems == null || listItems.isEmpty())) {// 无缓存且通信失败
								try {
									list_no_record.setVisibility(View.VISIBLE);
									list_no_record.setText("通信失败");
									listItems.clear();
									vca.notifyDataSetChanged();
								} catch (Exception e2) {
									// TODO: handle exception
								}

							}
							Toast.makeText(getActivity(), "通信失败，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
						} catch (Exception e2) {
							// TODO: handle exception
						}
						// if (getActivity() != null) {
						// Toast.makeText(getActivity(), "通信失败",
						// Toast.LENGTH_SHORT).show();
						// }
					}
					// CollectActivity.dialog.hide();
					if (page_no != 1) {
						page_no--;
						mPullToRefreshView.onFooterRefreshComplete();
					} else {
						mPullToRefreshView.onHeaderRefreshComplete();
					}
				}

			});
		} catch (Exception e) {
			// TODO: handle exception
		}
		mHasLoadedOnce = true;
	}

	private String getClickCount(String id) {
		try {
			FileInputStream freader = new FileInputStream(
					getActivity().getCacheDir().toString() + "/lecturl_click" + id + ".txt");
			ObjectInputStream objectInputStream = new ObjectInputStream(freader);
			Map<String, String> map = (Map<String, String>) objectInputStream.readObject();
			objectInputStream.close();
			return map.get("lecture_click_count");
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}

	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		if (!isrefreshing) {
			page_no++;
			getTokenLocal();
		} else {
			Toast.makeText(getActivity(), "正在更新数据", Toast.LENGTH_SHORT).show();
			mPullToRefreshView.onFooterRefreshComplete();
		}

	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		if (!isrefreshing) {
			list_cache = false;
			page_no = 1;
			getTokenLocal();
		} else {
			Toast.makeText(getActivity(), "正在更新数据", Toast.LENGTH_SHORT).show();
			mPullToRefreshView.onHeaderRefreshComplete();
		}

	}

	@Override
	protected void lazyLoad() {
		// TODO Auto-generated method stub
		if (!isPrepared || !isVisible || mHasLoadedOnce) {
			return;
		}
		getTokenLocal();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		listItems = null;
		vca = null;
		try {
			handler.removeCallbacksAndMessages(null);
			// handler = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
		super.onDestroy();
	}

}