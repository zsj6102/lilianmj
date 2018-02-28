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
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.TitleCollect;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleMultiBeanNetHandler;
import org.soshow.beautyedu.json.utils.NetUtil;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.Encryption;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 题目收藏
 */
public class FragmentMyTitle extends BaseFragment implements OnHeaderRefreshListener, OnFooterRefreshListener {
	private View rootView;
	private ListView list_record;
	// public static final String ARG_SECTION_NUMBER = "section_number";
	private SharedPreferences sp;
	private Editor editor;
	// private List<TitleCollect> list_titleCollect;
	public static List<Map<String, Object>> listItems;
	private String mToken;
	public static FragmentMyTitle fmt;
	private TextView list_no_record;
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
					Log.d("345abc", "获取新token-MyTitle");
					LoginUtil.login_handlered = false;
					mToken = sp.getString("mToken", null);

					if (mToken != null) {
						getInfo();
					} else {

						// if (CollectActivity.dialog != null) {
						// CollectActivity.dialog.hide();
						// }
						if (getActivity() != null) {// && !open_wap
							Toast.makeText(getActivity(), "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
						} // else if (open_wap) {
							// open_wap = false;
							// }
					}
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							// 需要做的事:发送消息
							// LoginUtil.login_handlered = false;
							Log.d("345abc", "循环等待-Mytitle");
							Message msg_loop = handler.obtainMessage();
							msg_loop.what = 2;
							msg_loop.sendToTarget();
						}
					}, 1000);

				}
				break;
			case 3:
				try {
					list_no_record.setVisibility(View.GONE);
					if (page_no == 1) {
						listItems = new ArrayList<Map<String, Object>>();
						// MyApplication.list_lecture.clear();
					}
					if (listItems_cache.size() > ((page_no - 1) * 10)) {// 当前页仍有数据
						if (listItems_cache.size() - ((page_no - 1) * 10) >= 10) {// 当前页数据数量足够10个
							for (int i = 0; i < 10; i++) {
								Map<String, Object> listItem = listItems_cache.get((page_no - 1) * 10 + i);
								listItems.add(listItem);

							}
							if (page_no != 1) {
								Toast.makeText(getActivity(), "加载成功", Toast.LENGTH_SHORT).show();
							}
						} else {// 当前页数据不够10个
							for (int i = 0; i < listItems_cache.size() - ((page_no - 1) * 10); i++) {
								Map<String, Object> listItem = listItems_cache.get((page_no - 1) * 10 + i);
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

					if (page_no == 1 && sa == null) {
						sa = new SimpleAdapter(getActivity(), listItems, R.layout.list_my_collect_title,
								new String[] { "quest_title" }, new int[] { R.id.title_collect }) {
							@Override
							public View getView(final int position, View convertView, ViewGroup parent) {
								// TODO Auto-generated method stub
								if (convertView == null) {
									convertView = View.inflate(getActivity(), R.layout.list_my_collect_title, null);
								}
								final Button read_title = (Button) convertView.findViewById(R.id.read_title);
								read_title.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										if (NetUtil.isConnected(getActivity())) {
											String quest_id = listItems.get(position).get("quest_id").toString();
											// String mToken =
											// sp.getString("mToken",
											// null);
											position_title = position;
											// listItem_cache =
											// listItems.get(position);
											Intent intent = new Intent(getActivity(), WebActivity.class);
											intent.putExtra("catID", 9);
											intent.putExtra("quest_id", quest_id);
											startActivity(intent);
											// open_wap = true;
											// mHasLoadedOnce=false;
											// String url_my_practice =
											// Constant.phpUrl
											// +
											// "/wap/api.php?action=GET_CAT&id=";
										} else {
											Toast.makeText(getActivity(), "您当前无网络连接", Toast.LENGTH_SHORT).show();
										}
									}
								});
								return super.getView(position, convertView, parent);

							}
						};
						list_record.setAdapter(sa);

					} else {
						try {
							sa.notifyDataSetChanged();
							mPullToRefreshView.onFooterRefreshComplete();// 结束刷新
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				break;
			default:
				break;
			}
		}

	};
	PullToRefreshView mPullToRefreshView;
	private int page_no = 1;
	public static SimpleAdapter sa;
	public static int position_title;
	// public static Map<String, Object> listItem_cache = new HashMap<String,
	// Object>();
	// private Boolean refresh = true;
	private boolean mHasLoadedOnce;
	// private boolean open_wap = false;
	private boolean isPrepared;
	private boolean list_cache = false;
	private List<Map<String, Object>> listItems_cache = new ArrayList<Map<String, Object>>();

	// private boolean openWeb=false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		fmt = this;
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
		// Log.d("345abc", "title onstart");
		super.onStart();
		// if (open_wap) {
		// open_wap = false;
		// page_no = 1;
		// getTokenLocal();
		// }
		// getTokenLocal();
	}

	@SuppressWarnings("unchecked")
	private void getTokenLocal() {
		// if (getActivity() != null) {
		// mToken = new TokenManager(getActivity()).getToken();
		// } else {
		// return;
		// }
		// if (mToken != null) {
		// getInfo();
		// } else {
		// LoginUtil.login_handlered = false;
		// Message msg_loop = handler.obtainMessage();
		// msg_loop.what = 2;
		// msg_loop.sendToTarget();
		// }
		if (list_cache) {// 使用本地数据
			try {
				if (listItems_cache == null || listItems_cache.isEmpty()) {// 第一次加载本地数据
					FileInputStream freader = new FileInputStream(
							getActivity().getCacheDir().toString() + "/mytitle_list.txt");
					ObjectInputStream objectInputStream = new ObjectInputStream(freader);
					listItems_cache = (List<Map<String, Object>>) objectInputStream.readObject();
					objectInputStream.close();
				}

				if (listItems_cache == null || listItems_cache.isEmpty()) {// 加载数据为空
					try {// 删除无效文件
							// Toast.makeText(getActivity(), "s删除1",
							// Toast.LENGTH_SHORT).show();
						File file = new File(getActivity().getCacheDir().toString() + "/mytitle_list.txt");
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

			String mobile = Encryption.desEncryptd(Constant.code_local, Constant.code_local,
					sp.getString("user_name", null));
			String url_mytitle = Constant.phpUrl + "/wap/api.php?action=GET_REMARK_QUEST&app_nonce=" + app_nonce
					+ "&mobile=" + mobile + "&tocken=" + mToken + "&page_no=" + page_no;
			// CollectActivity.dialog.show();
			Log.d("345abc", "url_mytitle=  " + url_mytitle);
			NetHelper.get(url_mytitle, new SimpleMultiBeanNetHandler<TitleCollect>(getActivity()) {

				@Override
				protected void onSuccess(List<TitleCollect> list) {
					// TODO Auto-generated method stub
					try {
						if (page_no != 1) {

							if (getActivity() != null) {// && !open_wap
								Toast.makeText(getActivity(), "加载成功", Toast.LENGTH_SHORT).show();

							}
							mPullToRefreshView.onFooterRefreshComplete();
						} else {
							if (getActivity() != null) {// &&!open_wap
								Toast.makeText(getActivity(), "刷新成功", Toast.LENGTH_SHORT).show();
							}
							mPullToRefreshView.onHeaderRefreshComplete();
						}
						// open_wap = false;
						// Log.d("345abc", "题目收藏返回数据为 " + list.get(0).add_time);
						if (page_no == 1) {
							listItems = new ArrayList<Map<String, Object>>();
						}

						for (TitleCollect tc : list) {
							// TitleCollect mp = list.get(i);
							Map<String, Object> listItem = new HashMap<String, Object>();
							listItem.put("quest_id", tc.quest_id);
							listItem.put("quest_title", tc.quest_title);
							// Log.d("345abc", "我的收藏标题为=" + tc.quest_title);
							listItems.add(listItem);

						}
						try {// 获取数据写入本地
							FileOutputStream outStream = new FileOutputStream(
									getActivity().getCacheDir().toString() + "/mytitle_list.txt");
							ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
							objectOutputStream.writeObject(listItems);
							outStream.close();
						} catch (Exception e) {
							// TODO: handle exception

						}
						if (page_no == 1 && getActivity() != null) {
							sa = new SimpleAdapter(getActivity(), listItems, R.layout.list_my_collect_title,
									new String[] { "quest_title" }, new int[] { R.id.title_collect }) {
								@Override
								public View getView(final int position, View convertView, ViewGroup parent) {
									// TODO Auto-generated method stub
									if (convertView == null) {
										convertView = View.inflate(getActivity(), R.layout.list_my_collect_title, null);
									}
									final Button read_title = (Button) convertView.findViewById(R.id.read_title);
									read_title.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
											if (NetUtil.isConnected(getActivity())) {
												String quest_id = listItems.get(position).get("quest_id").toString();
												// String mToken =
												// sp.getString("mToken",
												// null);
												position_title = position;
												// listItem_cache =
												// listItems.get(position);
												Intent intent = new Intent(getActivity(), WebActivity.class);
												intent.putExtra("catID", 9);
												intent.putExtra("quest_id", quest_id);
												startActivity(intent);
												// open_wap = true;
												// mHasLoadedOnce=false;
												// String url_my_practice =
												// Constant.phpUrl
												// +
												// "/wap/api.php?action=GET_CAT&id=";
											} else {
												Toast.makeText(getActivity(), "您当前无网络连接", Toast.LENGTH_SHORT).show();
											}
										}
									});
									return super.getView(position, convertView, parent);

								}
							};

							list_record.setAdapter(sa);
							// CollectActivity.dialog.hide();
						} else {
							sa.notifyDataSetChanged();
							// CollectActivity.dialog.hide();
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

				}

				@Override
				protected void onError(int errorCode, String errorMsg) {
					// TODO Auto-generated method stub
					Log.d("345abc", "Title错误=" + errorMsg);
					try {
						JSONObject jo = new JSONObject(errorMsg);

						if (jo.optString("result").equals("2") && page_no != 1) {
							if (getActivity() != null) {// && !open_wap
								Toast.makeText(getActivity(), "无更多题目收藏", Toast.LENGTH_SHORT).show();
							}
						} else if (jo.optString("result").equals("2") && page_no == 1) {

							list_no_record.setVisibility(View.VISIBLE);
							list_no_record.setText("您当前无题目收藏");
							if (listItems != null && sa != null) {
								listItems.clear();
								sa.notifyDataSetChanged();
							}
							if (getActivity() != null) {// && !open_wap
								Toast.makeText(getActivity(), "您当前无题目收藏", Toast.LENGTH_SHORT).show();
							}
							try {// 网络请求成功，删除本地数据
									// Toast.makeText(getActivity(), "s删除缓存",
									// Toast.LENGTH_SHORT).show();
								File file = new File(getActivity().getCacheDir().toString() + "/mytitle_list.txt");
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
							if (getActivity() != null) {// && !open_wap
								Toast.makeText(getActivity(), mistake, Toast.LENGTH_SHORT).show();
							}
						}

					} catch (Exception e) {
						// TODO: handle exception
						Log.d("345abc", "通信异常信息" + e.toString());
						try {
							if (listItems_cache != null && !listItems_cache.isEmpty()) {// 网络通信失败，重新使用本地数据
								list_cache = true;
							} else if ((listItems_cache == null || listItems_cache.isEmpty())
									&& (listItems == null || listItems.isEmpty())) {// 无缓存且通信失败
								try {
									list_no_record.setVisibility(View.VISIBLE);
									list_no_record.setText("通信失败");
									listItems.clear();
									sa.notifyDataSetChanged();
								} catch (Exception e2) {
									// TODO: handle exception
								}

							}
							Toast.makeText(getActivity(), "通信失败，请检查您的网络或稍后再试", Toast.LENGTH_SHORT).show();
						} catch (Exception e2) {
							// TODO: handle exception
						}

					}
					// open_wap=false;
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

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		page_no++;
		getTokenLocal();
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		list_cache = false;
		page_no = 1;
		getTokenLocal();
	}

	@Override
	protected void lazyLoad() {
		// TODO Auto-generated method stub
		// Toast.makeText(getActivity(), "加载", Toast.LENGTH_SHORT).show();
		if (!isPrepared || !isVisible || mHasLoadedOnce) {
			return;
		}
		getTokenLocal();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			handler.removeCallbacksAndMessages(null);
			// handler = null;
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

}