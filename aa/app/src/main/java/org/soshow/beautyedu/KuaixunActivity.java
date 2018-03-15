package org.soshow.beautyedu;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautyedu.activity.BaseActivity;

import org.soshow.beautyedu.bean.KuaixunBean;

import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.ScreenUtils;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.widget.ExpandableTextView;
import org.soshow.beautyedu.widget.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/6 0006.
 */

public class KuaixunActivity extends BaseActivity implements
        PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

    private String TAG = "TuwenActivity";
    private SharedPreferences sp;
    private int startPage = 1;
    private int pageSize = 10;
    private boolean hasMore;
    private List<KuaixunBean> videoCourses;
    private Context context;
    private TextView tvNodata;
    private ListView list_course;
    private PullToRefreshView mPullToRefreshView;
    private TheBaseAdapter adapterCourse;
    private LinearLayout loading;
    private ShareAction action;
    private PopupWindow window;
    private View mView;
    private UMImage image;
    private Bitmap bitmap;
    ScrollView scrollView;
    TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_tuwen);
        mView = LayoutInflater.from(this).inflate(R.layout.activity_tuwen, null);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.titlebar_logininput);
        context = KuaixunActivity.this;
        action = new ShareAction(this);
        sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
        TextView title_name = (TextView) findViewById(R.id.title_name);
        title_name.setText(R.string.zixun);
        getInfo(startPage);
        initView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    private void initView() {
        videoCourses = new ArrayList<KuaixunBean>();
        loading = (LinearLayout) findViewById(R.id.linear_load);
        tvNodata = (TextView) findViewById(R.id.list_no_tuwen);
        list_course = (ListView) findViewById(R.id.list_tuwen);
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.tuwen_pull_refresh_view);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        adapterCourse = new TheBaseAdapter(context, videoCourses);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
//        int width = metric.widthPixels;
//        int height = metric.heightPixels;
//        view = (ScrollView)findViewById(R.id.scroll_view);
//        layoutView(view, width, height);
        list_course.setAdapter(adapterCourse);
        adapterCourse.setShareClick(new ShareClick() {
            @Override
            public void click(String s, String time) {

                showPopupWindow(s, time);
            }
        });
    }

    private void layoutView(ScrollView v, int width, int height) {
        // 整个View的大小 参数是左上角 和右下角的坐标
        v.layout(0, 0, width, height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.UNSPECIFIED);
        /** 当然，measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局。
         * 按示例调用layout函数后，View的大小将会变成你想要设置成的大小。
         */
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
    }

    private Bitmap loadBitmapFromView(ScrollView v) {
        int w = v.getWidth();
        int h = 0;
        for (int i = 0; i < v.getChildCount(); i++) {
            h += v.getChildAt(i).getMeasuredHeight();
        }
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            if(bitmap.isRecycled()){
                bitmap.recycle();
            }
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {

        }
    };

    private void showPopupWindow(final String s, final String time) {
        window = new PopupWindow(initPop(s, time),
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setFocusable(true);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        window.setBackgroundDrawable(new BitmapDrawable());
        window.showAtLocation(mView, Gravity.BOTTOM, 100, 0);
        window.showAsDropDown(mView);
    }

    private View initPop(final String s, final String time) {
        View view = LayoutInflater.from(this).inflate(R.layout.popupwindow_share, null);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvContent = (TextView) view.findViewById(R.id.tv_content);
        scrollView = (ScrollView)view.findViewById(R.id.scroll_view);
        tvTime.setText(time);
        tvContent.setText(s);
        int h = ScreenUtils.getScreenHeight(this);
        int w = ScreenUtils.getScreenWidth(this);
        layoutView(scrollView,w,h);
//
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissSharePop();
            }
        });
        view.findViewById(R.id.ll_share_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(SHARE_MEDIA.WEIXIN, s, time);
            }
        });
        view.findViewById(R.id.ll_share_circle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(SHARE_MEDIA.WEIXIN_CIRCLE, s, time);
            }
        });
        view.findViewById(R.id.ll_share_fav).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(SHARE_MEDIA.WEIXIN_FAVORITE, s, time);
            }
        });
        return view;
    }

    private void dismissSharePop() {
        if (window != null) {
            window.dismiss();
        }
    }

    private void share(SHARE_MEDIA platform, String s, String time) {

        bitmap = loadBitmapFromView(scrollView);
        image = new UMImage(this, bitmap);
        image.compressStyle = UMImage.CompressStyle.QUALITY;
        action.setPlatform(platform)
                .setCallback(umShareListener)
                .withMedia(image)
                .share();
        dismissSharePop();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_search_logininput:
                finish();
                overridePendingTransition(R.anim.anim_slider_left_in,
                        R.anim.anim_slider_right_out);
                break;
            case R.id.right_title:

                break;

            default:
                break;
        }
    }


    // 图文课程列表数据
    private void getInfo(int page) {


        String url_video_course_list = Constant.ARTICLE_LIST + "&page_no=" + page;
        NetHelper.get(url_video_course_list,
                new SimpleSingleBeanNetHandler<Captcha>(context) {

                    @Override
                    protected void onSuccess(Captcha bean) {
                        String result = bean.result;
                        try {
                            if (result.equals("0")) {
                                String info = bean.info;
                                JSONArray jsonArray = new JSONArray(info);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = (JSONObject) jsonArray
                                            .get(i);
                                    KuaixunBean videoCourse = GsonUtils
                                            .parseJSON(obj.toString(),
                                                    KuaixunBean.class);
                                    videoCourses.add(videoCourse);
                                }
                                if (videoCourses.size() > 0) {
                                    tvNodata.setVisibility(View.GONE);
                                } else {
                                    tvNodata.setVisibility(View.VISIBLE);
                                }
                                Log.e(TAG, "数据大小=" + videoCourses.size());
                                if (jsonArray.length() >= pageSize) {
                                    hasMore = true;
                                } else {
                                    hasMore = false;
                                }
                                adapterCourse.notifyDataSetChanged();

                                if (startPage != 1) {
                                    mPullToRefreshView
                                            .onFooterRefreshComplete();
                                } else {
                                    mPullToRefreshView
                                            .onHeaderRefreshComplete();
                                }
                            } else {
                                ToastUtil.getInstance().showToast(context,
                                        bean.message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loading.setVisibility(View.GONE);
                    }

                });
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        if (hasMore) {
            startPage++;
            getInfo(startPage);
        } else {
            Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
            mPullToRefreshView.onFooterRefreshComplete();
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        startPage = 1;
        videoCourses.clear();
        getInfo(startPage);
    }

    class TheBaseAdapter extends BaseAdapter implements ExpandableTextView.OnExpandListener {

        private SparseArray<Integer> mPositionsAndStates = new SparseArray<>();
        private List<KuaixunBean> mList;
        private LayoutInflater inflater;
        ShareClick shareClick;

        public TheBaseAdapter(Context context, List<KuaixunBean> list) {
            mList = list;
            this.inflater = LayoutInflater.from(context);
        }

        public void setShareClick(ShareClick click) {
            shareClick = click;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        //只要在getview时为其赋值为准确的宽度值即可，无论采用何种方法
        private int etvWidth;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if (null == convertView) {
                viewHolder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_layout_kuaixun, parent, false);
                viewHolder.etv = (ExpandableTextView) convertView.findViewById(R.id.etv);
                viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                viewHolder.tvShare = (TextView) convertView.findViewById(R.id.tv_share);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String content = mList.get(position).getContent();
            if (etvWidth == 0) {
                viewHolder.etv.post(new Runnable() {
                    @Override
                    public void run() {
                        etvWidth = viewHolder.etv.getWidth();
                    }
                });
            }
            viewHolder.etv.setTag(position);
            viewHolder.etv.setExpandListener(this);
            Integer state = mPositionsAndStates.get(position);
            viewHolder.tvTime.setText(mList.get(position).getCreateTime());
            viewHolder.etv.updateForRecyclerView(content.toString(), etvWidth, state == null ? 0 : state);//第一次getview时肯定为etvWidth为0
            viewHolder.tvShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareClick.click(mList.get(position).getContent(), mList.get(position).getCreateTime());
                }
            });
            return convertView;
        }

        @Override
        public void onExpand(ExpandableTextView view) {
            Object obj = view.getTag();
            if (obj != null && obj instanceof Integer) {
                mPositionsAndStates.put((Integer) obj, view.getExpandState());
            }
        }

        @Override
        public void onShrink(ExpandableTextView view) {
            Object obj = view.getTag();
            if (obj != null && obj instanceof Integer) {
                mPositionsAndStates.put((Integer) obj, view.getExpandState());
            }
        }
    }

    public interface ShareClick {
        void click(String s, String time);
    }

    static class ViewHolder {
        ExpandableTextView etv;
        TextView tvTime;
        TextView tvShare;
    }
}
