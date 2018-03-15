package org.soshow.beautyedu.activity;

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
import org.soshow.beautydu.photo.SelectPhoto;
import org.soshow.beautydu.photo.localphoto.PictureShowActivity;
import org.soshow.beautyedu.FindItemDetailActivity;
import org.soshow.beautyedu.activity.dynamic.PerDynamicDetailActivity;
import org.soshow.beautyedu.activity.user.PersonInfoActivity;
import org.soshow.beautyedu.activity.dynamic.PublishDynamicActivity;
import org.soshow.beautyedu.R;
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
import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.SPUtils;
import org.soshow.beautyedu.utils.ScreenUtils;
import org.soshow.beautyedu.utils.SoftInputUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.CircleImageView;
import org.soshow.beautyedu.widget.MyCustomListView;
import org.soshow.beautyedu.widget.MyDialog;
import org.soshow.beautyedu.widget.NineGridTestLayout;

import org.soshow.beautyedu.widget.NoScrollGridView;
import org.soshow.beautyedu.widget.PullToRefreshView;
import org.soshow.beautyedu.widget.PullToRefreshView.OnFooterRefreshListener;
import org.soshow.beautyedu.widget.PullToRefreshView.OnHeaderRefreshListener;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chatuidemo.DemoHelper;


public class FragmentFind extends Fragment implements OnHeaderRefreshListener, OnFooterRefreshListener, OnClickListener, OnKeyListener {
    private String TAG = FragmentFind.class.getSimpleName();
    public static ImageView ivHead;
    private View fragmentView;
    private PullToRefreshView mPullToRefreshView;
    private SharedPreferences sharedPreferences;
    private Editor editor;
    private ListView list_find;
    private String app_nonce;
    private String mToken;
    private boolean hasMore;
    private int startPage = 1;
    private int pageSize = 10;
    private List<FindListInfo> findListInfos;
    private LinearLayout llAnim;
    private PopupWindow popupWindow;
    private String type = "";// goods_act:点赞 ,comment_act:评论
    private TextView tvZan;// 点赞
    private CommonAdapter<FindListInfo> adapter;
    private CommonAdapter<String> picAdapter;
    private TextView tvRemark;
    private NoScrollGridView gridView;
    List<CommentsEntity> midCommentLists;
    private int popLayoutHeight;//pop输入框的高度
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:// token获取
                    if (LoginUtil.login_handlered) {
                        LoginUtil.login_handlered = false;
                        mToken = sharedPreferences.getString("mToken", null);
                        if (mToken != null) {
                            findListInfos.clear();
                            getInfo(startPage);
                        } else {
                            Toast.makeText(getActivity(), "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
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
    };
    private Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainTabActivity.title_main.setText("念力");
        tvPublish = (TextView) getActivity().findViewById(R.id.over);
        tvPublish.setVisibility(View.VISIBLE);
        tvPublish.setText(getResources().getString(R.string.publicsh_work));
        tvPublish.setOnClickListener(this);

        //		if (MyApplication.logined == false|| !DemoHXSDKHelper.getInstance().isLogined()) {
        //			Intent intent = new Intent(getActivity(), LoginInputActivity.class);
        //			intent.putExtra("key", 1);
        //			startActivityForResult(intent, MainTabActivity.FIND_CORD);
        //		}
        if (fragmentView == null) {
            imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            popLayout = (getActivity()).getLayoutInflater().inflate(R.layout.view_pop, null);
            popBirth = new PopupWindow(popLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
            popBirth.setBackgroundDrawable(new BitmapDrawable());
            popBirth.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            popBirth.setFocusable(true);
            popBirth.setTouchable(true);
            dialog = ProgressDialogUtil.createLoadingDialog(getActivity(), null, true, false);
            dialog.show();
            fragmentView = inflater.inflate(R.layout.fragment_find, null);
            initView();
            getTokenLocal();
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) fragmentView.getParent();
        if (parent != null) {
            parent.removeView(fragmentView);
        }
        return fragmentView;
    }

    private void initView() {
        midCommentLists = new ArrayList<FindListInfo.CommentsEntity>();
        findListInfos = new ArrayList<FindListInfo>();
        sharedPreferences = getActivity().getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        list_find = (ListView) fragmentView.findViewById(R.id.list_find);
        // listView头部
        LayoutInflater headLayout = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = headLayout.inflate(R.layout.fragment_find_commom_head, null);
        ivHead = (ImageView) view.findViewById(R.id.fragment_find_iv_head);
        tvName = (TextView) view.findViewById(R.id.find_fragment_name);
        tvRemark = (TextView) view.findViewById(R.id.fragment_find_iv_remark);
        ivHead.setOnClickListener(this);
        iv_url = (String) SPUtils.get(getActivity(), "headUrl", "");
        LogUtils.e("发现：头像headUrl=" + iv_url);
        String signature = (String) SPUtils.get(getActivity(), "signature", "");
        String username = (String) SPUtils.get(getActivity(), "nickname", "");
        MyApplication.logined = sharedPreferences.getBoolean("logined", false);
        if (MyApplication.logined && PersonInfoActivity.isInfoModify) {
            UniversalImageLoadTool.disPlayTrue(iv_url, ivHead, R.drawable.head_account);
            tvRemark.setText(signature);
            tvName.setText(username);
            PersonInfoActivity.isInfoModify = false;
        } else {
            ivHead.setImageResource(R.drawable.head_account);
            tvRemark.setText("");
            tvName.setText("");
        }

        view.findViewById(R.id.find_detail_head_add_new_rl).setVisibility(View.GONE);
        list_find.addHeaderView(view);
        mPullToRefreshView = (PullToRefreshView) fragmentView.findViewById(R.id.find_pull_refresh_view);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        initAdapter();
    }

    private void initAdapter() {
        adapter = new FindListInfoCommonAdapter();
        list_find.setAdapter(adapter);
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        if (hasMore) {
            startPage++;
            getInfo(startPage);
        } else {
            Toast.makeText(getActivity(), "没有更多数据", Toast.LENGTH_SHORT).show();
            mPullToRefreshView.onFooterRefreshComplete();
        }
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        startPage = 1;
        findListInfos.clear();
        getInfo(startPage);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_find_iv_head:
                if (MyApplication.logined == false || !DemoHelper.getInstance().isLoggedIn()) {
                    Intent intent = new Intent(getActivity(), LoginInputActivity.class);
                    intent.putExtra("key", 1);
                    startActivityForResult(intent, MainTabActivity.FIND_CORD);
                    getActivity().overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
                } else {
                    Intent intent = new Intent(getActivity(), FindItemDetailActivity.class);
                    intent.putExtra("other_user_id", sharedPreferences.getString("user_id", ""));
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
                }
                break;
            case R.id.over://发表作品
                if (MyApplication.logined == false || !DemoHelper.getInstance().isLoggedIn()) {
                    Intent intent = new Intent(getActivity(), LoginInputActivity.class);
                    intent.putExtra("key", 1);
                    startActivityForResult(intent, MainTabActivity.FIND_CORD);
                    getActivity().overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
                } else {
                    Intent intent = new Intent(getActivity(), FindItemDetailActivity.class);
                    intent.putExtra("other_user_id", sharedPreferences.getString("user_id", ""));
//                    intent.putExtra("other_user_id", "");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
                }
                break;
            default:
                break;
        }
    }

    /*
     * 点赞、评论操作
     */
    private FindListInfo infos;
    private String iv_url;
    private InputMethodManager imm;
    private View popLayout;
    private PopupWindow popBirth;
    private TextView tvName;
    public static TextView tvPublish;

    private void remark(final FindListInfo infos, String type, String comment_id, String contentText, String commomtId, final CommonAdapter<CommentsEntity> commonAdapters, final List<CommentsEntity> commentLists, final int pos, final int position) {
        app_nonce = StringUtil.getPhoneIMEI(getActivity());
        mToken = new TokenManager(getActivity()).getToken();
        LogUtils.e("点赞评论===mToken=" + mToken);
        Map<String, String> params = new HashMap<String, String>();
//        params.put("tocken", mToken);
//        params.put("app_nonce", app_nonce);
        if (comment_id.equals("")) {
            params.put("dynamic_id", String.valueOf(infos.getId()));
            if (contentText.equals("")) {
                /* 点赞 */
                Log.e(TAG, "点赞");
                if (infos.getIs_liked().equals("1")) {
                    params.put("op", "down");
                } else if (infos.getIs_liked().equals("0")) {
                    params.put("op", "up");
                }
                Log.e(TAG, "参数：" + params.toString());
                dialog.show();
                NetHelper.post(Constant.DYNAMIC_LIKE, params, new SimpleSingleBeanNetHandler<Captcha>(getActivity()) {
                    @Override
                    protected void onSuccess(Captcha bean) {
                        LogUtils.e("点赞操作数据：result=" + bean.result + "\ninfo=" + bean.info);
                        Integer goods_count = findListInfos.get(pos).getGoods_num();
                        if (bean.isSuccess()) {
                            int txt;
                            if ("1".equals(infos.getIs_liked())) {
                                findListInfos.get(pos).setIs_liked("0");
                                goods_count--;
                                txt = R.string.cancel;
                                tvZan.setText(R.string.zan);
                            } else {
                                findListInfos.get(pos).setIs_liked("1");
                                goods_count++;
                                txt = R.string.zan;
                                tvZan.setText(R.string.cancel);
                            }
                            findListInfos.get(pos).setGoods_num(Math.max(goods_count, 0));
                            Toast.makeText(getActivity(), txt, Toast.LENGTH_SHORT).show();
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.getInstance().showToast(getActivity(), bean.message);
                        }
                    }

                    @Override
                    public void complete() {
                        super.complete();
                        hideProcessing();
                    }
                });
            } else {
                /* 对动态评论  */
                params.put("content", contentText);
                params.put("dynamic_id", String.valueOf(infos.getId()));
                Log.e(TAG, "参数：" + params.toString());
                NetHelper.post(Constant.DYNAMIC_COMMENT_ADD, params, new SimpleSingleBeanNetHandler<Captcha>(getActivity()) {
                    @Override
                    protected void onSuccess(Captcha bean) {
                        if (bean.isSuccess()) {
                            String info = bean.info;
                            try {
                                JSONObject jsonObject = new JSONObject(info);
                                CommentsEntity commentsEntity = GsonUtils.parseJSON(jsonObject.toString(), CommentsEntity.class);
                                commentLists.add(commentsEntity);
                                findListInfos.get(pos).setComments(commentLists);
                                commonAdapters.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtil.getInstance().showToast(getActivity(), bean.message);
                        }
                    }
                });
            }
        } else {
            Log.e(TAG, "删除评论");
//            params.put(type, "delete");
            if (type.equals("delete")) {
                params.put("comment_id", comment_id);
                NetHelper.post(Constant.DYNAMIC_COMMENT_DELETE, params, new SimpleSingleBeanNetHandler<Captcha>(getActivity()) {
                    @Override
                    protected void onSuccess(Captcha bean) {
                        if (bean.isSuccess()) {
                            commentLists.remove(pos);
                            findListInfos.get(position).setComments(commentLists);
                            commonAdapters.notifyDataSetChanged();
                            Log.e("", "删除评论：" + commentLists.size());
                            ToastUtil.getInstance().showToast(getActivity(), bean.message);
                        } else {
                            ToastUtil.getInstance().showToast(getActivity(), bean.message);
                        }
                    }
                });
            } else {
                params.put("content", contentText);
                params.put("dynamic_id", String.valueOf(infos.getId()));
                params.put("replyto_comment_id", comment_id);
                NetHelper.post(Constant.DYNAMIC_COMMENT_ADD, params, new SimpleSingleBeanNetHandler<Captcha>(getActivity()) {
                    @Override
                    protected void onSuccess(Captcha bean) {
                        if (bean.isSuccess()) {
                            String info = bean.info;
                            try {
                                JSONObject jsonObject = new JSONObject(info);
                                CommentsEntity commentsEntity = GsonUtils.parseJSON(jsonObject.toString(), CommentsEntity.class);
                                commentLists.add(commentsEntity);
                                findListInfos.get(pos).setComments(commentLists);
                                commonAdapters.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            ToastUtil.getInstance().showToast(getActivity(), bean.message);
                        }
                    }
                });
            }

        }
    }

    @Deprecated
    // 获取tocken
    private void getTokenLocal() {
        app_nonce = StringUtil.getPhoneIMEI(getActivity());
        mToken = new TokenManager(getActivity()).getToken();
        LogUtils.e("点赞评论===mToken=" + mToken);
        if (mToken != null) {
            findListInfos.clear();
            startPage = 1;
            getInfo(startPage);
        } else {
            TokenManager.handlered = false;
            Message msg_loop = handler.obtainMessage();
            msg_loop.what = 2;
            msg_loop.sendToTarget();
        }
    }

    private void getInfo(int page) {
        try {
            String url = Constant.DYNAMIC_LIST + "&page_no=" + page;
            NetHelper.get(url, new SimpleSingleBeanNetHandler<Captcha>(getActivity()) {
                @Override
                protected void onSuccess(Captcha bean) {
                    if (bean.isSuccess()) {
                        try {
                            String info = bean.info;
                            JSONArray jsonArray = new JSONObject(info).getJSONArray("data");
                            if (jsonArray.length() >= pageSize) {
                                hasMore = true;
                            } else {
                                hasMore = false;
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject dataObj = (JSONObject) jsonArray.get(i);
                                FindListInfo findListInfo = GsonUtils.parseJSON(dataObj.toString(), FindListInfo.class);
                                findListInfos.add(findListInfo);
                                Log.e("", "获取数据大小：" + findListInfos.size());
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
                        ToastUtil.getInstance().showToast(getActivity(), bean.message);
                    }
                    dialog.hide();
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (PublishDynamicActivity.isPublish) {
            PublishDynamicActivity.isPublish = false;
            findListInfos.clear();
            getInfo(startPage);
        }
        LogUtils.e("发现==onResume" + PersonInfoActivity.isInfoModify);
        if (!MyApplication.logined) {
            ivHead.setImageResource(R.drawable.head_account);
            tvRemark.setText("");
            tvName.setText("");
        } else {
            iv_url = (String) SPUtils.get(getActivity(), "headUrl", "");

                UniversalImageLoadTool.disPlayTrue(iv_url, ivHead, R.drawable.head_account);


            String signTure = (String) SPUtils.get(getActivity(), "signature", "");
            String username = (String) SPUtils.get(getActivity(), "nickname", "");
            tvRemark.setText(signTure);
            tvName.setText(username);
            if (!PersonInfoActivity.isInfoModify && FragmentPer.isBackToPer) {
                FragmentPer.isBackToPer = false;
            }
        }
        //单条说说详情被做过点赞或评论操作时
        if (PerDynamicDetailActivity.isZan || PerDynamicDetailActivity.isRecomment || FindItemDetailActivity.IS_DELETE_dYNAMIC) {
            getTokenLocal();
            PerDynamicDetailActivity.isZan = false;
            PerDynamicDetailActivity.isRecomment = false;
            FindItemDetailActivity.IS_DELETE_dYNAMIC = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("", "onActivityResult===========================================");
        if (requestCode == MainTabActivity.FIND_CORD) {
            MainTabActivity.mTabHost.setCurrentTab(0);
            fragmentView = null;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        LogUtils.e("发疯了。。。。。。。。。。");
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            popBirth.dismiss();
            popBirth = null;
            return true;
        }
        return false;
    }

    public void hideProcessing() {
        if (dialog != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            });
        }
    }

    public static void calGridViewSumWH(int numColumns, GridView gridView) {
        // 获取GridView对应的Adapter
        ListAdapter listAdapter = gridView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        int totalWidth = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalWidth = listItem.getMeasuredWidth();
            if ((i + 1) % numColumns == 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    totalHeight += listItem.getMeasuredHeight() + gridView.getVerticalSpacing(); // 统计所有子项的总高度
                } else {
                    totalHeight += listItem.getMeasuredHeight();
                }
            }

            if ((i + 1) == len && (i + 1) % numColumns != 0) {
                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight;
        params.width = totalWidth * listAdapter.getCount();
        gridView.setLayoutParams(params);
    }

    private class FindListInfoCommonAdapter extends CommonAdapter<FindListInfo> {
        private MyCustomListView listView;
        private View line;

        public FindListInfoCommonAdapter() {
            super(FragmentFind.this.getActivity(), FragmentFind.this.findListInfos, R.layout.fragment_find_item);
        }

        @Override
        public void convert(final ViewHolder holderList, final FindListInfo findListItem) {
            Log.e("", "--------------------------------------2");
            CircleImageView ivUserHead = holderList.getView(R.id.find_item_iv_head);
            TextView tvUserName = holderList.getView(R.id.find_item_tv_name);
            TextView tvContent = holderList.getView(R.id.find_item_tv_content);
            TextView tvTime = holderList.getView(R.id.find_item_tv_time);
            RelativeLayout layotGrid = holderList.getView(R.id.find_item_wrap);

            final ImageView ivRight = holderList.getView(R.id.find_item_iv_right);
            TextView tvZanCount = holderList.getView(R.id.zan_goods_count);// 点赞人
            line = holderList.getView(R.id.line_zan);
            line.setVisibility(View.GONE);
            listView = holderList.getView(R.id.find_item_listview);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ScreenUtils.getScreenWidth(mContext) / 2, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layotGrid.setLayoutParams(params);
            UniversalImageLoadTool.disPlayTrue(findListItem.getUser_photo_url(), ivUserHead, R.drawable.head_account);
            tvUserName.setText(findListItem.getNickname());
            LogUtils.e("动态内容：" + findListItem.getContent().trim());
            if (TextUtils.isEmpty(findListItem.getContent().trim()) || findListItem.getContent().trim().equals("")) {
                tvContent.setVisibility(View.GONE);
            } else {
                tvContent.setVisibility(View.VISIBLE);
                tvContent.setText(findListItem.getContent());
            }
            tvZanCount.setText(findListItem.getGoods_num().toString());
            //内容图片的点击浏览
            gridView = holderList.getView(R.id.find_item_gridView);


//            gridView.setIsShowAll(true);
            gridView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<String> imageUrls = new ArrayList<String>();
                    String photos_url = findListItem.getPhotos_url();
                    String[] photoarray = null;
                    photoarray = photos_url.split(";");
                    for (int i = 0; i < photoarray.length; i++) {
                        imageUrls.add(photoarray[i]);
                    }

                    Intent intent = new Intent(getActivity(), PictureShowActivity.class);
                    intent.putStringArrayListExtra(PictureShowActivity.EXTRA_IMAGEURLS, imageUrls);
                    intent.putExtra(PictureShowActivity.EXTRA_CURRENT, position);
                    startActivityForResult(intent, SelectPhoto.DELETE_IMAGE);
                    getActivity().overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
                }
            });
            // 动态头像点击监听
            ivUserHead.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MyApplication.logined == false || !DemoHelper.getInstance().isLoggedIn()) {
                        Intent intent = new Intent(getActivity(), LoginInputActivity.class);
                        intent.putExtra("key", 1);
                        startActivityForResult(intent, MainTabActivity.FIND_CORD);
                        getActivity().overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
                    } else {
                        Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                        intent.putExtra("other_user_id", findListItem.getUser_id());
                        Log.e("", "other_user_id==" + findListItem.getUser_id());
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
                    }
                }
            });
            // 动态发表的图片
            final ArrayList<String> imageUrls = new ArrayList<String>();
            String photos_url = findListItem.getPhotos_url();
            if (photos_url == null || photos_url.equals("")) {
                gridView.setVisibility(View.GONE);
            } else {
                gridView.setVisibility(View.VISIBLE);
                String[] photoarray = null;
                photoarray = photos_url.split(";");
                for (int i = 0; i < photoarray.length; i++) {
                    imageUrls.add(photoarray[i]);
                }
//                gridView.setUrlList(imageUrls);
            }

            picAdapter = new CommonAdapter<String>(
                    mContext, imageUrls,
                    R.layout.item_fragment_find_pic) {

                @Override
                public void convert(ViewHolder holder_pic, String pic) {
                    ImageView ivContent = holder_pic
                            .getView(R.id.fragmentfind_pics);
                    int magin = DensityUtil.dip2px(getActivity(), 2);
                    int width = ScreenUtils.getScreenWidth(getActivity()) * 3 / 5;
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
            // 发表时间
            String addtime = findListItem.getCreate_time();
            if (!TextUtils.isEmpty(addtime)) {
                tvTime.setText(addtime);
            }

            /*
             * 评论部分
             */
            final CommonAdapter<CommentsEntity> commonAdapters;
            final List<CommentsEntity> commentLists = new ArrayList<CommentsEntity>();
            LogUtils.e("评论大小=" + commentLists.size(), "评论获取的总数据：" + commentLists.toString());
            commentLists.addAll(findListItem.getComments());
            midCommentLists.clear();
            midCommentLists.addAll(commentLists);
            commonAdapters = new CommonAdapter<CommentsEntity>(getActivity(), commentLists, R.layout.comments) {
                @Override
                public void convert(ViewHolder holder, CommentsEntity commomt) {
                    LogUtils.e("findListItem.getComments().size()===========" + findListItem.getComments().size());
                    if (findListInfos.get(holderList.getPosition()).getComments().size() <= 0) {
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
            listView.setAdapter(commonAdapters);// 评论列表
            // 点赞与评论的显示与隐藏操作
            final RelativeLayout rlRight = holderList.getView(R.id.rl_right);
            ivRight.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (MyApplication.getInstance().logined == false) {
                        Intent intent = new Intent(getActivity(), LoginInputActivity.class);
                        startActivityForResult(intent, MainTabActivity.FIND_CORD);
                        getActivity().overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
                    } else {
                        if (findListItem.getIs_liked() == null) {
                            LoginUtil.logout(getActivity());
                            Intent intent = new Intent(getActivity(), LoginInputActivity.class);
                            startActivityForResult(intent, MainTabActivity.FIND_CORD);
                            getActivity().overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
                            return;
                        }
                        final View view = getActivity().getLayoutInflater().inflate(R.layout.popwindow_layout, null);
                        tvZan = (TextView) view.findViewById(R.id.zan);
                        if (findListItem.getIs_liked().equals("1")) {
                            tvZan.setText("取消");
                        } else if (findListItem.getIs_liked().equals("0")) {
                            tvZan.setText("给力");
                        }
                        int width = DensityUtil.dip2px(getActivity(), 141);
                        int height = DensityUtil.dip2px(getActivity(), 40);
                        popupWindow = new PopupWindow(view, width, height);
                        popupWindow.setFocusable(true);
                        popupWindow.setOutsideTouchable(true);
                        popupWindow.setBackgroundDrawable(new BitmapDrawable());
                        int[] location = new int[2];
                        ivRight.getLocationOnScreen(location);
                        popupWindow.showAtLocation(ivRight, Gravity.NO_GRAVITY, location[0] - popupWindow.getWidth(), location[1] - 6);
                        // 点赞操作
                        view.findViewById(R.id.find_item_ll_zan).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (MyApplication.logined == false || !DemoHelper.getInstance().isLoggedIn()) {
                                    Intent intent = new Intent(getActivity(), LoginInputActivity.class);
                                    intent.putExtra("key", 1);
                                    startActivityForResult(intent, MainTabActivity.FIND_CORD);
                                    getActivity().overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
                                } else {
                                    type = "goods_act";
                                    remark(findListItem, type, "", "", "", commonAdapters, commentLists, holderList.getPosition(), -1);
                                }
                            }
                        });
                        view.findViewById(R.id.find_item_ll_say).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popupWindow.dismiss();
                                EditText editText = (EditText) popLayout.findViewById(R.id.pop_et_comment);
                                editText.setHint("发表评论");
                                if (MyApplication.logined == false || !DemoHelper.getInstance().isLoggedIn()) {
                                    Intent intent = new Intent(getActivity(), LoginInputActivity.class);
                                    intent.putExtra("key", 1);
                                    startActivityForResult(intent, MainTabActivity.FIND_CORD);
                                    getActivity().overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
                                } else {
                                    //异步弹出软键盘
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            imm = (InputMethodManager) view.findViewById(R.id.find_item_ll_say).getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                                            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                                        }
                                    }, 0);
                                    //评论popwindow
                                    ViewTreeObserver vto = popLayout.getViewTreeObserver();
                                    vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {
                                            popLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                            popLayoutHeight = popLayout.getHeight();
                                        }
                                    });
                                    popLayout.findViewById(R.id.pop_view_top).setOnClickListener(new OnClickListener() {
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
                                                ToastUtil.getInstance().showToast(getActivity(), "评论不能为空");
                                            } else {
                                                remark(findListItem, type, "", contentText, "", commonAdapters, commentLists, holderList.getPosition(), -1);
                                                etRecomment.setText("");
                                                popBirth.dismiss();
                                            }
                                        }
                                    });
                                    popLayout.findViewById(R.id.pop_et_comment).getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                                        //当键盘弹出隐藏的时候会 调用此方法。
                                        @Override
                                        public void onGlobalLayout() {
                                            Rect r = new Rect();
                                            //获取当前界面可视部分
                                            holderList.getView(R.id.fragment_find_layout).getWindowVisibleDisplayFrame(r);
                                            popLayout.findViewById(R.id.recomment_input_layout_rl).getHeight();
                                            int position = holderList.getPosition();
                                            //获取屏幕的高度
                                            int screenHeight = ScreenUtils.getScreenHeight(mContext);
                                            //								                int screenHeight =  getActivity().getWindow().getDecorView().getRootView().getHeight();
                                            //此处就是用来获取键盘的高度的， 在键盘没有弹出的时候 此高度为0 键盘弹出的时候为一个正数
                                            int heightDifference = screenHeight - r.height();
                                            Log.e("screenHeight=" + screenHeight, "软键盘高度=" + heightDifference + "\nposition=" + position + "\n可见范围高度=" + r.height() + "\npopLayoutHeight=" + popLayoutHeight + "\n");
                                            if (heightDifference > 0) {
                                                LogUtils.e("holderList.getPosition()=" + holderList.getPosition());
                                                list_find.smoothScrollToPositionFromTop(holderList.getPosition() + 1, 0);
                                            }
                                            LogUtils.e("imm.isActive()===" + imm.isActive());
                                        }
                                    });
                                    popBirth.showAtLocation((getActivity()).findViewById(R.id.find_pull_refresh_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                                    list_find.smoothScrollToPositionFromTop(holderList.getPosition() + 1, 0);
                                }
                            }
                        });
                    }

                }
            });

            /*
             * 单击回复评论
             */
            listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                    if (MyApplication.logined == false || !DemoHelper.getInstance().isLoggedIn()) {
                        Intent intent = new Intent(getActivity(), LoginInputActivity.class);
                        intent.putExtra("key", 1);
                        startActivityForResult(intent, MainTabActivity.FIND_CORD);
                        getActivity().overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
                    } else {
                        EditText editText = (EditText) popLayout.findViewById(R.id.pop_et_comment);
                        editText.setHint("回复" + commentLists.get(position).getUsername());
                        //异步弹出软键盘
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                imm = (InputMethodManager) view.getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                            }
                        }, 0);
                        popLayout.findViewById(R.id.pop_view_top).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                popBirth.dismiss();
                            }
                        });
                        popLayout.findViewById(R.id.pop_tv_send).setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                type = "reply";
                                EditText etRecomment = (EditText) popLayout.findViewById(R.id.pop_et_comment);
                                String contentText = etRecomment.getText().toString().trim();
                                if (contentText.equals("")) {
                                    ToastUtil.getInstance().showToast(getActivity(), "评论不能为空");
                                } else {
                                    remark(findListItem, type, commentLists.get(position).getId().toString(), contentText, commentLists.get(position).getComment_id(), commonAdapters, commentLists, holderList.getPosition(), -1);
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
                                    SoftInputUtil.hideSoftInput(getActivity());
                                }
                            }
                        });
                        popBirth.showAtLocation((getActivity()).findViewById(R.id.find_pull_refresh_view), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                        list_find.smoothScrollToPositionFromTop(holderList.getPosition() + 1, 0);
                    }
                }
            });
            // 长按删除评论
            listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    String user_id = (String) SPUtils.get(getActivity(), "user_id", "");
                    LogUtils.e("长按删除评论 用户id====" + user_id + "\findListItem.getUser_id()=" + findListItem.getUser_id());
                    final CommentsEntity commentsEntity = commentLists.get(position);
                    if (user_id.equals(commentsEntity.getUser_id())) {
                        deleteComment(holderList, commonAdapters, commentsEntity, position);
                    }
                    return true;
                }

                private void deleteComment(final ViewHolder holderList, final CommonAdapter<CommentsEntity> commonAdapters, final CommentsEntity commentsEntity, final int position) {
                    type = "delete";
                    final MyDialog.MyBuilder dailog = new MyDialog.MyBuilder(getActivity());
                    dailog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            remark(null, type, commentsEntity.getId().toString(), "", "", commonAdapters, commentLists, position, holderList.getPosition());
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
                    tvTitle.setText("是否删除该评论？");
                    EditText et = (EditText) layout.findViewById(R.id.ed_input);
                    et.setVisibility(View.GONE);
                    creatDialog.show();
                }
            });
        }
    }
}
