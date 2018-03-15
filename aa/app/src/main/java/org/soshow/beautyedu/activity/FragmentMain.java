package org.soshow.beautyedu.activity;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.MyVideoCarActivity;
import org.soshow.beautyedu.R;


import org.soshow.beautyedu.adapters.LectureListMainAdapter;
import org.soshow.beautyedu.application.MyApplication;

import org.soshow.beautyedu.bean.LunBoInfo;
import org.soshow.beautyedu.bean.RecommendItem;
import org.soshow.beautyedu.bean.VideoCourseInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.bean.News;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;

import org.soshow.beautyedu.utils.Constant;

import org.soshow.beautyedu.utils.GsonUtils;

import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.SPUtils;

import org.soshow.beautyedu.utils.ToastUtil;

import org.soshow.beautyedu.volley.Advertisements;
import org.soshow.beautyedu.volley.RequestManager;
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
import android.support.v4.app.Fragment;

import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/**
 * 首页
 */
public class FragmentMain extends Fragment implements OnHeaderRefreshListener, OnFooterRefreshListener {
    private String TAG = "FragmentMain";
    //判断是否有更多内容
    private boolean hasMore;
    private int pageSize = 10;
    // 轮播图
    private LinearLayout llAdvertiseBoard;
    private LayoutInflater inflater;
    private View fragmentView;
    private int xp;
    private int yp;
    private String mSwitcherArray[];
    private String mIdArray[];
    private TextSwitcher textSwitcher;
    // 轮播图信息
    private MyApplication app;
    private SharedPreferences sp;
    private Editor editor;
    public static FragmentMain fm_instance;
    public String app_nonce;
    private int count = 0;
    private Timer timer = new Timer();
    private Timer timer_scroll = new Timer();
    private ListView list_tuijian;
    private LectureListMainAdapter dra;
    private List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
    public static Map<String, Boolean> refresh = new HashMap<String, Boolean>();
    private List<Map<String, Object>> listItems_cache = new ArrayList<Map<String, Object>>();
    private List<RecommendItem> videoCourses;
    private int scorll_height;
    private float refresh_height;
    int Pos[] = {-1, -1};
    private ImageButton toptohead;
    private List<News> newsList;
    private List<LunBoInfo> lunBos;
    static final int Msg_runboOk_1 = 1;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Msg_runboOk_1:// 新闻列表轮播显示

                    if (first_show) {
                        scorll_height = textSwitcher.getHeight();
                        BigDecimal b1 = new BigDecimal(scorll_height);
                        BigDecimal b2 = new BigDecimal(24);
                        Double d1 = b1.divide(b2, 10, BigDecimal.ROUND_HALF_UP).doubleValue();
                        // Log.d("1221", "倍数"+d1);
                        BigDecimal b3 = new BigDecimal(68);
                        BigDecimal b4 = new BigDecimal(d1);
                        refresh_height = (int) b3.multiply(b4).doubleValue();
                        first_show = false;
                    }
                    textSwitcher.getLocationOnScreen(Pos);
                    if (Pos[1] >= refresh_height) {
                        LogUtils.e("mSwitcherArray=" + mSwitcherArray + "\nmSwitcherArray.length=" + mSwitcherArray.length);
                        textSwitcher.setText(mSwitcherArray[count % mSwitcherArray.length]);
                    }

                    if (mSwitcherArray != null) {
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                // 需要做的事:发送消息
                                // LoginUtil.login_handlered = false;
                                // Log.d("345abc", "搜索循环等待");
                                if (count < mSwitcherArray.length - 1) {
                                    count++;
                                } else {
                                    count = 0;
                                }
                                Message msg_loop = handler.obtainMessage();
                                msg_loop.what = 1;
                                msg_loop.sendToTarget();
                            }
                        }, 5000);
                    }
                    break;
                case 3:// 新闻列表数据处理
                    synchronized (this) {
                        List<News> list_news = (List<News>) msg.obj;
                        if (list_news != null && !list_news.isEmpty()) {
                            mSwitcherArray = new String[list_news.size()];
                            mIdArray = new String[list_news.size()];
                            for (int i = 0; i < list_news.size(); i++) {
                                mSwitcherArray[i] = list_news.get(i).getArticle_title();
                                Log.e("1221", "标题为====================" + mSwitcherArray[i]);
                                mIdArray[i] = String.valueOf(list_news.get(i).getId());
                            }

                        }
                        try {// 获取数据写入本地
                            FileOutputStream outStream = new FileOutputStream(
                                    getActivity().getCacheDir().toString() + "/news_first_list.txt");
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outStream);
                            objectOutputStream.writeObject(list_news);
                            outStream.close();
                        } catch (Exception e) {
                            // TODO: handle exception

                        }
                        if (first_news) {
                            Message message = handler.obtainMessage();
                            message.what = 1;
                            message.sendToTarget();
                            first_news = false;
                        }
                    }
                    break;
                case 5:
                    if (first_show) {
                        scorll_height = textSwitcher.getHeight();
                        BigDecimal b1 = new BigDecimal(scorll_height);
                        BigDecimal b2 = new BigDecimal(24);
                        Double d1 = b1.divide(b2, 10, BigDecimal.ROUND_HALF_UP).doubleValue();
                        // Log.d("1221", "倍数"+d1);
                        BigDecimal b3 = new BigDecimal(68);
                        BigDecimal b4 = new BigDecimal(d1);
                        // Log.d("1221", "隐藏点数"+b3.multiply(b4).doubleValue());
                        refresh_height = (int) b3.multiply(b4).doubleValue();
                        // textSwitcher.getLocationOnScreen(Pos);
                        // scroll_y = Pos[1];
                        first_show = false;
                        // Pos = { -1, -1 };
                    }
                    textSwitcher.getLocationOnScreen(Pos);
                    if (Pos[1] >= refresh_height) {
                        toptohead.setVisibility(View.GONE);
                    } else {
                        toptohead.setVisibility(View.VISIBLE);
                    }

                    timer_scroll.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            Message msg_loop = handler.obtainMessage();
                            msg_loop.what = 5;
                            msg_loop.sendToTarget();
                        }
                    }, 100);
                    break;
                default:
                    break;
            }
        }

    };
    private TextView title_list;
    PullToRefreshView mPullToRefreshView;
    private int page_no = 1;
    //	private boolean list_cache = false;
    private boolean first = true;
    private boolean first_news = true;
    private boolean first_show = true;
    private boolean lunbo_by_hand = false;
    private TextView tv;
    private DisplayMetrics dm;
    private Dialog dialog;
    private LinearLayout llWork;
    private HorizontalScrollView hsv;

    @Override
    public View onCreateView(LayoutInflater inflater1, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.e("=====================================首页");
        MainTabActivity.title_main.setText("理念");
        if (fragmentView == null) {
            getNews();
            videoCourses = new ArrayList<RecommendItem>();
            newsList = new ArrayList<News>();
            lunBos = new ArrayList<LunBoInfo>();
            dialog = ProgressDialogUtil.createLoadingDialog(getActivity(), null, true, false);
            if (1 == 2)
                dialog.show();
            app = (MyApplication) this.getActivity().getApplication();
            inflater = inflater1;
            fragmentView = inflater1.inflate(R.layout.fragment_main, null);
            list_tuijian = (ListView) fragmentView.findViewById(R.id.list_tuijian);
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.header_main, null);
            list_tuijian.addHeaderView(view);
            dra = new LectureListMainAdapter(getActivity(), videoCourses, app_nonce);
            list_tuijian.setAdapter(dra);
//            list_tuijian.setOnItemClickListener(this);
            RequestManager.init(this.getActivity());
            mPullToRefreshView = (PullToRefreshView) fragmentView.findViewById(R.id.main_pull_refresh_view);
            mPullToRefreshView.setOnHeaderRefreshListener(this);
            mPullToRefreshView.setOnFooterRefreshListener(this);
            hsv = (HorizontalScrollView) view.findViewById(R.id.horizontalScrollView);
            llWork = (LinearLayout) view.findViewById(R.id.main_work_ll);
            llAdvertiseBoard = (LinearLayout) view.findViewById(R.id.llAdvertiseBoard);
            textSwitcher = (TextSwitcher) view.findViewById(R.id.textSwitcher);
            textSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
                public View makeView() {
                    tv = new TextView(getActivity());
                    tv.setTextColor(getResources().getColor(R.color.word_grey_liu));
                    tv.setSingleLine();
                    tv.setEllipsize(TruncateAt.END);

                    return tv;
                }
            });
            view.findViewById(R.id.news_rl_detail).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MyVideoCarActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.anim_slider_right_in,
                            R.anim.anim_slider_left_out);
                }
            });

            dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            xp = dm.widthPixels;

            yp = xp * 1 / 2;

            RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) llAdvertiseBoard.getLayoutParams(); // 取控件textView当前的布局参数
            linearParams.height = yp;// 控件的高强制设成yp
            linearParams.width = xp;// 控件的宽强制设成xp
            llAdvertiseBoard.setLayoutParams(linearParams);
            llAdvertiseBoard.setPadding(0, 0, 0, 0);
            sp = getActivity().getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
            editor = sp.edit();
            title_list = (TextView) view.findViewById(R.id.title_list);

            MainTabActivity.titlebar_main.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    if (MainTabActivity.mTabHost.getCurrentTab() == 0) {
                        list_tuijian.setSelection(0);
                        toptohead.setVisibility(View.GONE);
                    }
                }
            });
            toptohead = (ImageButton) fragmentView.findViewById(R.id.toptohead);
            toptohead.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    // scroll_list.smoothScrollTo(0, 0);
                    // list_tuijian.smoothScrollToPosition(0, 0);
                    list_tuijian.setSelection(0);
                    toptohead.setVisibility(View.GONE);
                }
            });
            Message message = handler.obtainMessage();
            message.what = 5;
            message.sendToTarget();

        }
        // 缓存的fragmentView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个fragmentView已经有parent的错误。
        ViewGroup parent = (ViewGroup) fragmentView.getParent();
        if (parent != null) {
            parent.removeView(fragmentView);
        }


        return fragmentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm_instance = this;

    }

    private void getNews() {
        try {
            String url_news = Constant.GET_HOME_PAGE + "&page=" + page_no;
            Log.e(TAG, "首页数据url=" + url_news);
            NetHelper.get(url_news, new SimpleSingleBeanNetHandler<Captcha>(getActivity()) {

                @Override
                protected void onSuccess(Captcha bean) {
                    try {
                        if ("0".equals(bean.result)) {
                            if (page_no != 1) {
                                mPullToRefreshView.onFooterRefreshComplete();
                            } else {
                                mPullToRefreshView.onHeaderRefreshComplete();
                            }
                            String info = bean.info;
                            JSONObject jsonObject = new JSONObject(bean.info);
                            JSONArray lunboArray = jsonObject.getJSONArray("carouselData");
                            JSONArray newsArray = jsonObject.getJSONArray("news");
                            JSONArray recommendArray = jsonObject.getJSONArray("recommend");
                            //轮播
                            for (int i = 0; i < lunboArray.length(); i++) {
                                JSONObject objLunbo = (JSONObject) lunboArray.get(i);
                                LunBoInfo lunBo = GsonUtils.parseJSON(objLunbo.toString(), LunBoInfo.class);
                                lunBos.add(lunBo);
                            }
                            if (FragmentMain.fm_instance != null) {
                                FragmentMain.fm_instance.initViews();
                            }
                            //判断是否有更多内容
                            if (recommendArray.length() > pageSize) {
                                hasMore = true;
                            } else {
                                hasMore = false;
                            }
                            //为您推荐
                            for (int i = 0; i < recommendArray.length(); i++) {
                                JSONObject objRecommend = (JSONObject) recommendArray.get(i);
                                RecommendItem item = GsonUtils.parseJSON(objRecommend.toString(), RecommendItem.class);
                                videoCourses.add(item);
                            }
                            dra.notifyDataSetChanged();
                            //新闻
                            LogUtils.e("新闻数据大小=" + newsArray.length());
                            for (int i = 0; i < newsArray.length(); i++) {
                                JSONObject obj = (JSONObject) newsArray.get(i);
                                News news = GsonUtils.parseJSON(obj.toString(), News.class);
                                newsList.add(news);
                            }
                            Message message = handler.obtainMessage();
                            message.what = 3;
                            message.obj = newsList;
                            message.sendToTarget();
                        } else {
                            ToastUtil.getInstance().showToast(getActivity(), bean.message);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                protected void onError(int errorCode, String errorMsg) {
                    //轮播
                    try {
                        JSONObject jo = new JSONObject(errorMsg);
                        ToastUtil.getInstance().showToast(getActivity(), (String) jo.opt("message"));
                        if (jo.optString("result").equals("99")) {
                            editor.putBoolean("token_logined", false);
                            editor.remove("mToken");
                            editor.commit();
                        }
                    } catch (Exception e) {
                    }
                }

            });
        } catch (Exception e) {
            // TODO: handle exception
        }

    }


    @SuppressWarnings("unchecked")
    public synchronized void initViews() {
        llAdvertiseBoard.removeAllViews();
        JSONArray advertiseArray = new JSONArray();
        if (lunBos != null && !lunBos.isEmpty()) {// 获取到新轮播图
            try {
                for (int i = 0; i < lunBos.size(); i++) {
                    JSONObject src_j = new JSONObject();
                    src_j.put("src", lunBos.get(i).getPhoto_url());
                    advertiseArray.put(src_j);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (lunBos == null || lunBos.isEmpty()) {// 无轮播图
            try {
                JSONObject src0 = new JSONObject();
                src0.put("src", "0");
                advertiseArray.put(src0);

            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        llAdvertiseBoard.addView(new Advertisements(this.getActivity(), false, inflater, 3000, lunBos).initView(advertiseArray));
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if (timer != null) {
            try {
                timer.cancel();
                timer.purge();
            } catch (Exception e) {
                // TODO: handle exception
            }

        }

        if (timer_scroll != null) {
            try {
                timer_scroll.cancel();
                timer_scroll.purge();
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
        try {
            refresh.clear();
        } catch (Exception e) {
            // TODO: handle exception
        }
        try {
            handler.removeCallbacksAndMessages(null);
            // handler = null;
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void onResume() {
        boolean is_loginout = (Boolean) SPUtils.get(getActivity(), "is_loginout", false);
        LogUtils.e("已经退出过=" + is_loginout);
        if (is_loginout && MyApplication.logined == true) {
            videoCourses.clear();
            newsList.clear();
            lunBos.clear();
            SPUtils.put(getActivity(), "is_loginout", false);
            getNews();
        }
        super.onResume();
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        if (hasMore) {
            page_no++;
            newsList.clear();
            lunBos.clear();
            getNews();
        } else {
            Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
            mPullToRefreshView.onFooterRefreshComplete();
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        // TODO Auto-generated method stub
        page_no = 1;
        lunbo_by_hand = true;
        Log.d("1221", "下拉");
        videoCourses.clear();
        newsList.clear();
        lunBos.clear();
        getNews();
    }
}
