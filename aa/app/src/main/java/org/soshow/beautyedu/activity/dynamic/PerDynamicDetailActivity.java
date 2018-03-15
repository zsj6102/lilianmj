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
import org.soshow.beautyedu.activity.LoginInputActivity;
import org.soshow.beautyedu.activity.MainTabActivity;
import org.soshow.beautyedu.activity.user.UserInfoActivity;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.bean.FindListInfo;
import org.soshow.beautyedu.bean.FindListInfo.CommentsEntity;

import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.DensityUtil;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.SPUtils;
import org.soshow.beautyedu.utils.ScreenUtils;
import org.soshow.beautyedu.utils.SoftInputUtil;
import org.soshow.beautyedu.utils.ToastUtil;

import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.CircleImageView;
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
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
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

import com.easemob.chatuidemo.DemoHelper;

import static org.soshow.beautyedu.activity.FragmentFind.calGridViewSumWH;
import static org.soshow.beautyedu.application.MyApplication.dialog;

public class PerDynamicDetailActivity extends BaseActivity implements OnClickListener {


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
    private CircleImageView ivHead;
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
    private String dynamic_id;
    private View line;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_per_dysnamic_detail);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.titlebar_logininput);

        context = PerDynamicDetailActivity.this;
        dynamic_id = getIntent().getStringExtra("dynamic_id");
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
        ivHead = (CircleImageView) findViewById(R.id.find_item_iv_head);
        tvName = (TextView) findViewById(R.id.find_item_tv_name);
        tvContent = (TextView) findViewById(R.id.find_item_tv_content);
        gridView = (NoScrollGridView) findViewById(R.id.find_item_gridView);
        tvAddTime = (TextView) findViewById(R.id.find_item_tv_time);
        ivRight = (ImageView) findViewById(R.id.find_item_iv_right);
        tvZanNum = (TextView) findViewById(R.id.zan_goods_count);
        listView = (MyCustomListView) findViewById(R.id.find_item_listview);
        RelativeLayout layotGrid = (RelativeLayout)findViewById(R.id.find_item_wrap);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ScreenUtils.getScreenWidth(PerDynamicDetailActivity.this) / 2, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layotGrid.setLayoutParams(params);
        line = (View)findViewById(R.id.line_zan);
        loading.setVisibility(View.VISIBLE);
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

    private void getContent() {
        String url_per_detail = Constant.DYNAMIC_BY_ID + "&dynamic_id=" + getIntent().getStringExtra("dynamic_id");
        NetHelper.get(url_per_detail, new SimpleSingleBeanNetHandler<Captcha>(context) {
            @Override
            protected void onSuccess(Captcha bean) {
                String result = bean.result;

                try {
                    if (result.equals("0")) {

                        JSONObject obj = new JSONObject(bean.info);
                        findListInfo = GsonUtils.parseJSON(obj.toString(), FindListInfo.class);
                        tvName.setText(findListInfo.getNickname());
                        if (TextUtils.isEmpty(findListInfo.getContent().trim()) || findListInfo.getContent().trim().equals("")) {
                            tvContent.setVisibility(View.GONE);
                        } else {
                            tvContent.setVisibility(View.VISIBLE);
                            tvContent.setText(findListInfo.getContent());
                        }
                        UniversalImageLoadTool.disPlayTrue(findListInfo.getUser_photo_url(), ivHead, R.drawable.head_account);
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
                        // 动态发表的图片
                        final ArrayList<String> imageUrls = new ArrayList<String>();
                        String photos_url = findListInfo.getPhotos_url();
                        if (photos_url == null || photos_url.equals("")) {
                            gridView.setVisibility(View.GONE);
                        } else {
                            gridView.setVisibility(View.VISIBLE);
                            String[] photoarray = null;
                            photoarray = photos_url.split(";");
                            for (int i = 0; i < photoarray.length; i++) {
                                imageUrls.add(photoarray[i]);
                            }
                        }

                        CommonAdapter<String> picAdapter = new CommonAdapter<String>(
                                PerDynamicDetailActivity.this, imageUrls,
                                R.layout.item_fragment_find_pic) {

                            @Override
                            public void convert(ViewHolder holder_pic, String pic) {
                                ImageView ivContent = holder_pic
                                        .getView(R.id.fragmentfind_pics);
                                int magin = DensityUtil.dip2px(PerDynamicDetailActivity.this, 2);
                                int width = ScreenUtils.getScreenWidth(PerDynamicDetailActivity.this) * 3 / 5;
                                if (imageUrls.size() == 1) {
                                    gridView.setNumColumns(1);
                                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                            width * 2 / 5,
                                            width * 2 / 5);
                                    layoutParams.setMargins(magin, magin, magin, magin);
                                    ivContent.setLayoutParams(layoutParams);
                                } else if (imageUrls.size() == 2) {
                                    gridView.setNumColumns(2);
                                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                            (width - 4 * magin) * 2 / 5,
                                            (width - 4 * magin) * 2 / 5);
                                    layoutParams.setMargins(magin, magin, magin, magin);
                                    ivContent.setLayoutParams(layoutParams);
                                } else {
                                    gridView.setNumColumns(3);
                                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                                            (width - 6 * magin) * 7 / 25,
                                            (width - 6 * magin) * 7 / 25);
                                    layoutParams.setMargins(magin, magin, magin, magin);
                                    ivContent.setLayoutParams(layoutParams);
                                }
                                UniversalImageLoadTool.disPlayTrue(
                                        pic, ivContent,
                                        R.drawable.defaultpic);
                            }
                        };
                        gridView.setAdapter(picAdapter);
                        if (photos_url != null && !photos_url.equals("")) {
                            if (imageUrls.size() >= 3) {
                                calGridViewSumWH(3, gridView);
                            } else {
                                calGridViewSumWH(imageUrls.size(), gridView);
                            }

                        }
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
                                PerDynamicDetailActivity.this, commentLists, R.layout.comments) {

                            @Override
                            public void convert(ViewHolder holder,
                                                CommentsEntity commomt) {
                                if (findListInfo.getComments().size() <= 0) {
                                    line.setVisibility(View.GONE);
                                } else {
                                    line.setVisibility(View.VISIBLE);
                                }
                                LinearLayout llTtype1 = holder.getView(R.id.commomts_tyle1);
                                TextView tvName = holder.getView(R.id.commomts_tv_name);
                                LinearLayout llTtype2 = holder.getView(R.id.commomts_tyle2);
                                TextView tvReplayFrom = holder.getView(R.id.commomts_tv_replay_from);
                                StringBuffer buffer = new StringBuffer();
                                String from = "";
                                String to = "";
                                if (commomt.getReplyto_user_nickname() != null) {
                                    llTtype1.setVisibility(View.GONE);
                                    llTtype2.setVisibility(View.VISIBLE);
                                    // 回复

                                    if (TextUtils.isEmpty(commomt.getUser_nickname())) {
                                        buffer.append(commomt.getUser_nickname());
                                        from = commomt.getUser_nickname();
                                    } else {
                                        buffer.append(commomt.getUser_nickname());
                                        from = commomt.getUser_nickname();
                                    }
                                    buffer.append("回复");
                                    if (TextUtils.isEmpty(commomt.getReplyto_user_nickname())) {
                                        buffer.append(commomt.getReplyto_user_nickname() + ":");
                                        to = commomt.getReplyto_user_nickname() + ":";
                                    } else {
                                        buffer.append(commomt.getReplyto_user_nickname() + ":");
                                        to = commomt.getReplyto_user_nickname() + ":";
                                    }
                                    buffer.append(commomt.getContent());
                                    String replayText = buffer.toString();
                                    SpannableString styledText = new SpannableString(replayText);
                                    styledText.setSpan(new TextAppearanceSpan(mContext, R.style.text_color_style1), 0, from.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    styledText.setSpan(new TextAppearanceSpan(mContext, R.style.text_color_style1), from.length() + 2, from.length() + 2 + to.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    tvReplayFrom.setText(styledText, TextView.BufferType.SPANNABLE);
                                } else {
                                    llTtype1.setVisibility(View.VISIBLE);
                                    llTtype2.setVisibility(View.GONE);
                                    // 评论
                                    if (TextUtils.isEmpty(commomt.getUser_nickname())) {
                                        buffer.append(commomt.getUser_nickname() + ":");
                                        from = commomt.getUser_nickname() + ":";
                                    } else {
                                        buffer.append(commomt.getUser_nickname() + ":");
                                        from = commomt.getUser_nickname() + ":";
                                    }
                                    buffer.append(commomt.getContent());
                                    String replayText = buffer.toString();
                                    SpannableString styledText = new SpannableString(replayText);
                                    styledText.setSpan(new TextAppearanceSpan(mContext, R.style.text_color_style1), 0, from.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    tvName.setText(styledText, TextView.BufferType.SPANNABLE);
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
                                if (findListInfo.getIs_liked() == null) {
                                    Intent intent = new Intent(PerDynamicDetailActivity.this, LoginInputActivity.class);
                                    startActivityForResult(intent, MainTabActivity.FIND_CORD);
                                    overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
                                    return;
                                }
                                if (findListInfo.getIs_liked().equals("1")) {
                                    tvZan.setText("取消");
                                } else if (findListInfo.getIs_liked().equals("0")) {
                                    tvZan.setText("给力");
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
                                                            ToastUtil.getInstance().showToast(PerDynamicDetailActivity.this, "评论不能为空");
                                                        } else {
                                                            remarkComment(contentText, commentLists);
                                                            etRecomment.setText("");
                                                            popBirth.dismiss();
                                                        }

                                                    }
                                                });

                                                popLayout.findViewById(R.id.pop_et_comment).setOnFocusChangeListener(new OnFocusChangeListener() {

                                                    @Override
                                                    public void onFocusChange(View v, boolean hasFocus) {
                                                        LogUtils.e("hasFocus==========" + hasFocus);
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
                                EditText editText = (EditText) popLayout.findViewById(R.id.pop_et_comment);
                                editText.setHint("回复" + commentLists.get(position).getUsername());
                                popLayout.findViewById(R.id.pop_tv_send).setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        type = "comment_act";
                                        EditText etRecomment = (EditText) popLayout.findViewById(R.id.pop_et_comment);
                                        String contentText = etRecomment.getText().toString().trim();

                                        if (contentText.equals("")) {
                                            ToastUtil.getInstance().showToast(PerDynamicDetailActivity.this, "评论不能为空");
                                        } else {

                                            remarkReply(contentText,commentLists,position);
                                            etRecomment.setText("");
                                            popBirth.dismiss();
                                        }

                                    }
                                });

                                popLayout.findViewById(R.id.pop_et_comment).setOnFocusChangeListener(new OnFocusChangeListener() {

                                    @Override
                                    public void onFocusChange(View v, boolean hasFocus) {
                                        LogUtils.e("hasFocus==========" + hasFocus);
                                        EditText et = (EditText) v;
                                        if (hasFocus) {
                                            et.requestFocus();
                                        } else {
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
                                if (user_id.equals(commentsEntity.getUser_id())) {
                                    deleteRecomment(commentLists, position);
                                }

                                return true;
                            }

                            private boolean deleteRecomment(
                                    final List<CommentsEntity> commentLists,
                                    final int position) {
                                final CommentsEntity commentsEntity = commentLists
                                        .get(position);
                                final MyDialog.MyBuilder dailog = new MyDialog.MyBuilder(context);
                                dailog.setPositiveButton("确定",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                remarkDel(String.valueOf(commentLists.get(position).getId()), commentLists, position);
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
                    } else {
                        ToastUtil.getInstance().showToast(context, bean.message);
                    }
                    loading.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 多评论进行评论
     */
    private void remarkReply(String content,final List<CommentsEntity> commentLists,int position){
        Map<String, String> params = new HashMap<String, String>();
        params.put("content", content);
        params.put("dynamic_id", String.valueOf(findListInfo.getId()));
        params.put("replyto_comment_id", String.valueOf(commentLists.get(position).getId()));
        NetHelper.post(Constant.DYNAMIC_COMMENT_ADD, params, new SimpleSingleBeanNetHandler<Captcha>(context) {
            @Override
            protected void onSuccess(Captcha bean) {
                if (bean.isSuccess()) {
                    String info = bean.info;
                    try {
                        JSONObject jsonObject = new JSONObject(info);
                        CommentsEntity commentsEntity = GsonUtils.parseJSON(jsonObject.toString(), CommentsEntity.class);
                        commentLists.add(commentsEntity);
                        findListInfo.setComments(commentLists);
                        commonAdapters.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    ToastUtil.getInstance().showToast(context, bean.message);
                }
            }
        });
    }
    /*
     * 点赞操作
     */
    private void remarkZan() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("dynamic_id", String.valueOf(findListInfo.getId()));
        /* 点赞 */
        if (findListInfo.getIs_liked().equals("1")) {
            params.put("op", "down");
        } else if (findListInfo.getIs_liked().equals("0")) {
            params.put("op", "up");
        }
//		Log.e(TAG, "参数：" + params.toString());
        NetHelper.post(Constant.DYNAMIC_LIKE, params, new SimpleSingleBeanNetHandler<Captcha>(this.context) {
            @Override
            protected void onSuccess(Captcha bean) {
                LogUtils.e("点赞操作数据：result=" + bean.result + "\ninfo=" + bean.info);
                Integer goods_count = findListInfo.getGoods_num();
                if (bean.isSuccess()) {
                    int txt;
                    if ("1".equals(findListInfo.getIs_liked())) {
                        findListInfo.setIs_liked("0");
                        goods_count--;
                        txt = R.string.cancel;
                        tvZan.setText(R.string.zan);
                    } else {
                        findListInfo.setIs_liked("1");
                        goods_count++;
                        txt = R.string.zan;
                        tvZan.setText(R.string.cancel);
                    }

                    findListInfo.setGoods_num(Math.max(goods_count, 0));
                    tvZanNum.setText(goods_count + "");

                    Toast.makeText(getContext(), txt, Toast.LENGTH_SHORT).show();
                } else {
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
    private void remarkComment(String contentText, final List<CommentsEntity> commentLists) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("dynamic_id", String.valueOf(findListInfo.getId()));
        params.put("content", contentText);
        NetHelper.post(Constant.DYNAMIC_COMMENT_ADD, params,
                new SimpleSingleBeanNetHandler<Captcha>(context) {

                    @Override
                    protected void onSuccess(Captcha bean) {
                        LogUtils.e("添加评论返回的数据：" + bean.info);
                        if (bean.result.equals("0")) {
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
                        } else {
                            ToastUtil.getInstance().showToast(context, bean.message);
                        }
                    }

                });
    }

    /*
     * 长按删除评论
     */
    private void remarkDel(String commentId, final List<CommentsEntity> commentLists, final int position) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("comment_id", commentId);
//		Log.e(TAG, "参数：" + params.toString());
        NetHelper.post(Constant.DYNAMIC_COMMENT_DELETE, params, new SimpleSingleBeanNetHandler<Captcha>(context) {
            @Override
            protected void onSuccess(Captcha bean) {

                if(bean.isSuccess()){
                    isRecomment = true;
                    commentLists.remove(position);
                    findListInfo.setComments(commentLists);
                    commonAdapters.notifyDataSetChanged();
                    Log.e("", "删除评论：" + commentLists.size());
                    ToastUtil.getInstance().showToast(context, bean.message);
                } else {
                    ToastUtil.getInstance().showToast(context, bean.message);
                }

            }
        });
    }

    public void hideProcessing() {
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
