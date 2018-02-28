package org.soshow.beautyedu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.basketball.common.adapter.CommonAdapter;
import org.soshow.basketball.common.adapter.ViewHolder;
import org.soshow.beautydu.photo.SelectPhoto;
import org.soshow.beautydu.photo.localphoto.PictureShowActivity;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.activity.dynamic.PerDynamicDetailActivity;
import org.soshow.beautyedu.activity.dynamic.PublishDynamicActivity;
import org.soshow.beautyedu.bean.FindListInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.SPUtils;
import org.soshow.beautyedu.utils.ToastUtil;

import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.MyDialog;
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
public class FindItemDetailActivity extends BaseActivity implements OnHeaderRefreshListener, OnFooterRefreshListener, OnClickListener, OnItemClickListener {
    public static boolean IS_DELETE_dYNAMIC;
    private String TAG = "FindItemDetailActivity";
    private PullToRefreshView mPullToRefreshView;
    private List<FindListInfo> findListInfos;
    private SharedPreferences sp;
    private Editor editor;
    private String app_nonce;
    private String mToken;
    private boolean hasMore;
    private int startPage = 1;
    private int pageSize = 10;
    private String other_user_id;
    private CommonAdapter<FindListInfo> adapter;
    private Context context;
    private LinearLayout loading;
    private boolean is_vip;
    private String userName;
    private String headUrl;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:// token获取
                    if (LoginUtil.login_handlered) {
                        LoginUtil.login_handlered = false;
                        mToken = sp.getString("mToken", null);
                        if (mToken != null) {
                            // photoInfos.clear();
                            getMyDynmic(startPage);
                        } else {
                            Toast.makeText(FindItemDetailActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
                            if (FindItemDetailActivity.this != null) {
                                Toast.makeText(FindItemDetailActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
                            }
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

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_find_item_detail);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_logininput);
        context = FindItemDetailActivity.this;
        sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
        editor = sp.edit();
        other_user_id = getIntent().getStringExtra("other_user_id");
        if(getIntent().getStringExtra("userName")!=null){
            userName = getIntent().getStringExtra("userName");
        }
        if(getIntent().getStringExtra("headUrl")!=null){
            headUrl = getIntent().getStringExtra("headUrl");
        }
        initView();
        getMyDynmic(startPage);
//        getTokenLocal();
    }

//    // 获取tocken
//    private void getTokenLocal() {
//        app_nonce = StringUtil.getPhoneIMEI(this);
//        mToken = new TokenManager(this).getToken();
//        if (mToken != null) {
//            loading.setVisibility(View.VISIBLE);
//            //			getIs_vip();//获取是否为会员
//            getMyDynmic(startPage);
//        } else {
//            TokenManager.handlered = false;
//            Message msg_loop = handler.obtainMessage();
//            msg_loop.what = 2;
//            msg_loop.sendToTarget();
//        }
//    }

    private void initView() {
        findListInfos = new ArrayList<FindListInfo>();
        TextView title_name = (TextView) findViewById(R.id.title_name);
        loading = (LinearLayout) findViewById(R.id.linear_load);
        mPullToRefreshView = (PullToRefreshView) findViewById(R.id.find_detail_pull_refresh_view);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        ListView listView = (ListView) findViewById(R.id.list_find_detail);
        View headView = getLayoutInflater().inflate(R.layout.fragment_find_commom_head, null);
        ImageView headIv = (ImageView) headView.findViewById(R.id.fragment_find_iv_head);
        TextView tvSignature = (TextView) headView.findViewById(R.id.fragment_find_iv_remark);
        headView.findViewById(R.id.find_detail_item_head_iv_new).setOnClickListener(this);
        RelativeLayout rlNew = (RelativeLayout) headView.findViewById(R.id.find_detail_head_add_new_rl);
        headView.findViewById(R.id.find_fragment_name).setVisibility(View.GONE);
//        if (!TextUtils.isEmpty(other_user_id)) {
//            rlNew.setVisibility(View.GONE);
//            String url = (String) SPUtils.get(this, "headUrl", "");
//            String signature = (String) SPUtils.get(this, "other_signature", "");
//            String other_name = (String) SPUtils.get(this, "username", "");
//            title_name.setText(other_name);
//            tvSignature.setText(signature);
//            UniversalImageLoadTool.disPlayTrue(url, headIv, R.drawable.head_account);
//        } else {
        rlNew.setVisibility(View.VISIBLE);
        String url;
        if(headUrl!=null){
            url = headUrl;
        }else{
            url = (String) SPUtils.get(this, "headUrl", "");
        }


        String signature = (String) SPUtils.get(this, "signature", "");
        SharedPreferences sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
        if(userName != null){
            title_name.setText(userName);
        }else{
            title_name.setText(sp.getString("username", null));
        }
        tvSignature.setText(signature);
        UniversalImageLoadTool.disPlayTrue(url, headIv, R.drawable.head_account);
//        }
        listView.addHeaderView(headView);
        adapter = new CommonAdapter<FindListInfo>(context, findListInfos, R.layout.item_find_detail) {
            @Override
            public void convert(ViewHolder viewHolder, FindListInfo listInfo) {
                TextView tvTime = viewHolder.getView(R.id.find_detail_item_tv_time);
                // 发表时间
                String addtime = listInfo.getCreate_time();
                LogUtils.e("发表动态时间：" + addtime);
                if (!TextUtils.isEmpty(addtime)) {
                    String date = addtime;
                    LogUtils.e("发表动态时间：" + date);
                    String ss = date.substring(5,7)+"月"+date.substring(8,10)+"日";
//                    SpannableString styledText = new SpannableString(time);
//                    styledText.setSpan(new TextAppearanceSpan(context, R.style.text_style1), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    styledText.setSpan(new TextAppearanceSpan(context, R.style.text_style2), 2, styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvTime.setText(ss);
                    if (viewHolder.getPosition() > 0) {
//                        String addtime2 = findListInfos.get(viewHolder.getPosition() - 1).getCreate_time();
//                        String date2 = TimeUtil.getStringDays(Long.valueOf(addtime2));
//                        LogUtils.e("date=" + date + "\ndate2=" + date2);
//                        if (date.equals(date2)) {
//                            tvTime.setVisibility(View.INVISIBLE);
//                        } else {
                        tvTime.setVisibility(View.VISIBLE);
//                        }
                    }
                }
                // 动态内容
                RelativeLayout rl = viewHolder.getView(R.id.item_find_detail_rl);
                TextView tvContent = viewHolder.getView(R.id.item_find_detail_tv_content);
                tvContent.setText(listInfo.getContent());
                // 图片数量
                TextView tvNum = viewHolder.getView(R.id.item_find_tv_how_match);
                ArrayList<String> imageUrls = new ArrayList<String>();
                String photos_url = listInfo.getPhotos_url();
                String[]   photoarray = null;
                photoarray = photos_url.split(";");
                for(int i = 0;i<photoarray.length;i++){
                    imageUrls.add(photoarray[i]);
                }
                tvNum.setText(imageUrls.size() + "张");
                // 动态图片
                ImageView iv = viewHolder.getView(R.id.item_find_iv);
                if (imageUrls.size() > 0) {
                    UniversalImageLoadTool.disPlayTrue(imageUrls.get(0), iv, R.drawable.defaultpic);
                    iv.setOnClickListener(new PicOnclick(FindItemDetailActivity.this, listInfo, viewHolder.getPosition()));
                    iv.setVisibility(View.VISIBLE);
                    tvNum.setVisibility(View.VISIBLE);
                    rl.setBackgroundResource(R.color.bg_white);
                } else {
                    iv.setVisibility(View.GONE);
                    tvNum.setVisibility(View.GONE);
                    rl.setBackgroundResource(R.color.bg_litle_gray);
                }
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        if (TextUtils.isEmpty(other_user_id)) {
            listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    if (position > 0) {
                        final MyDialog.MyBuilder dailog = new MyDialog.MyBuilder(FindItemDetailActivity.this);
                        dailog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteDynsaic(position);
                                dialog.dismiss();
                            }
                        });
                        dailog.setNagetiveButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        MyDialog creatDialog = dailog.creatDialog();
                        View layout = dailog.getView();
                        TextView tvTitle = (TextView) layout.findViewById(R.id.pop_title);
                        tvTitle.setText("是否删除该动态？");
                        EditText et = (EditText) layout.findViewById(R.id.ed_input);
                        et.setVisibility(View.GONE);
                        creatDialog.show();
                    }
                    return true;
                }
            });
        }
    }

    /*
     * 判断属否为数字
     */
    private boolean isNum(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher matcher = pattern.matcher(str);
        if (!matcher.matches()) {
            return false;
        }
        return true;
    }

    // 长按删除个人动态
    private void deleteDynsaic(final int position) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("dynamic_id", String.valueOf(findListInfos.get(position - 1).getId()));
        params.put("app_nonce", app_nonce);
        params.put("tocken", mToken);
        Log.e(TAG, "参数：" + params.toString());
        NetHelper.post(Constant.DELETE_MY_DYNAMIC, params, new SimpleSingleBeanNetHandler<Captcha>(this) {
            @Override
            protected void onSuccess(Captcha bean) {
                if (bean.result.equals("0")) {
                    if (position > 0) {
                        findListInfos.remove(position - 1);
                        IS_DELETE_dYNAMIC = true;
                    }
                    adapter.notifyDataSetChanged();
                    ToastUtil.getInstance().showToast(FindItemDetailActivity.this, bean.message);
                } else {
                    ToastUtil.getInstance().showToast(FindItemDetailActivity.this, bean.message);
                }
            }
        });
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        if (hasMore) {
            startPage++;
            getMyDynmic(startPage);
        } else {
            Toast.makeText(this, "没有更多数据", Toast.LENGTH_SHORT).show();
            mPullToRefreshView.onFooterRefreshComplete();
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        startPage = 1;
        findListInfos.clear();
        getMyDynmic(startPage);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_search_logininput:
                finish();
                overridePendingTransition(R.anim.anim_slider_left_in, R.anim.anim_slider_right_out);
                break;
            case R.id.find_detail_item_head_iv_new:// 发表动态
                if (1==1||is_vip) {//如果是会员
                    Intent intent = new Intent(this, PublishDynamicActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
                } else {
//                    Builder logout_confirm = new MyAlertDialog.Builder(FindItemDetailActivity.this);
//                    logout_confirm.setTitle("提示").setMessage("您还不是会员,不能晒作品").setPositiveButton("成为会员", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int arg1) {
//                            Intent intent = new Intent(FindItemDetailActivity.this, RechargesActivity.class);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
//                            dialog.dismiss();
//                        }
//                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int whichButton) {
//                            dialog.dismiss();
//                        }
//                    }).show();
                    //				final MyDialog.MyBuilder dailog = new MyDialog.MyBuilder(FindItemDetailActivity.this);
                    //				dailog.setPositiveButton("成为会员",
                    //						new DialogInterface.OnClickListener() {
                    //
                    //					@Override
                    //					public void onClick(DialogInterface dialog,
                    //							int which) {
                    //						Intent intent = new Intent(FindItemDetailActivity.this, RechargesActivity.class);
                    //						startActivity(intent);
                    //						overridePendingTransition(R.anim.anim_slider_right_in,
                    //				                R.anim.anim_slider_left_out);
                    //						dialog.dismiss();
                    //					}
                    //				});
                    //
                    //				dailog.setNagetiveButton("取消",
                    //						new DialogInterface.OnClickListener() {
                    //
                    //					@Override
                    //					public void onClick(DialogInterface dialog,
                    //							int which) {
                    //						dialog.dismiss();
                    //
                    //					}
                    //				});
                    //
                    //				MyDialog creatDialog = dailog.creatDialog();
                    //				View layout = dailog.getView();
                    //				TextView tvTitle = (TextView) layout.findViewById(R.id.pop_title);
                    //				tvTitle.setText("您还不是会员？不能晒作品");
                    //				EditText et = (EditText) layout.findViewById(R.id.ed_input);
                    //				et.setVisibility(View.GONE);
                    //				creatDialog.show();
                }
                break;
            case R.id.item_find_iv:// 动态发表的图片
                break;
            default:
                break;
        }
    }

    class PicOnclick implements OnClickListener {
        private FindListInfo listInfo;
        private Context context;
        private int position;

        public PicOnclick(Context context, FindListInfo listInfo, int position) {
            this.listInfo = listInfo;
            this.context = context;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            ArrayList<String> imageUrls = new ArrayList<String>();
            String photos_url = listInfo.getPhotos_url();
            String[]   photoarray = null;
            photoarray = photos_url.split(";");
            for(int i = 0;i<photoarray.length;i++){
                imageUrls.add(photoarray[i]);
            }
            //			Intent intent = new Intent(context, PictureDeleteActivity.class);
            //			intent.putStringArrayListExtra(
            //					PictureDeleteActivity.EXTRA_IMAGEURLS, imageUrls);
            //			intent.putExtra(PictureDeleteActivity.EXTRA_CURRENT, 0);
            //			intent.putExtra(PictureDeleteActivity.IS_NO_DELETE, true);
            //			startActivityForResult(intent, SelectPhoto.DELETE_IMAGE);
            //			overridePendingTransition(R.anim.anim_slider_right_in,
            //	                R.anim.anim_slider_left_out);
            Intent intent = new Intent(context, PictureShowActivity.class);
            intent.putStringArrayListExtra(PictureShowActivity.EXTRA_IMAGEURLS, imageUrls);
            intent.putExtra(PictureShowActivity.EXTRA_CURRENT, position);
            startActivityForResult(intent, SelectPhoto.DELETE_IMAGE);
            overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
        }
    }

    private void getMyDynmic(int page) {
        String url_find = Constant.USER_DYNAMIC_LIST +  "&user_id=" + other_user_id + "&page_no=" + page;
        Log.e(TAG, "我的动态url=" + url_find);
        NetHelper.get(url_find, new SimpleSingleBeanNetHandler<Captcha>(this) {
            @Override
            protected void onSuccess(Captcha bean) {
                if (bean.isSuccess()) {
                    String result = bean.result;
                    String info = bean.info;
                    try {
                        JSONArray jsonArray = new JSONObject(info).getJSONArray("data");
                        if (jsonArray.length() >= pageSize) {
                            hasMore = true;
                        } else {
                            hasMore = false;
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = (JSONObject) jsonArray.get(i);
                            FindListInfo findListInfo = GsonUtils.parseJSON(obj.toString(), FindListInfo.class);
                            findListInfos.add(findListInfo);
                        }
                        adapter.notifyDataSetChanged();
                        if (startPage != 1) {
                            mPullToRefreshView.onFooterRefreshComplete();
                        } else {
                            mPullToRefreshView.onHeaderRefreshComplete();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.getInstance().showToast(context, bean.message);
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PublishDynamicActivity.isPublish) {
            findListInfos.clear();
            getMyDynmic(startPage);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
            Intent intent = new Intent(this, PerDynamicDetailActivity.class);
            LogUtils.e("点击的位置=" + position);
            intent.putExtra("dynamic_id",String.valueOf( findListInfos.get(position - 1).getId()));
            startActivity(intent);
            overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
        }
    }
}
