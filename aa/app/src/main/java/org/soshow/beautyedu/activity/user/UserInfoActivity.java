package org.soshow.beautyedu.activity.user;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoHelper;
import com.easemob.chatuidemo.ui.ChatActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.basketball.common.adapter.CommonAdapter;
import org.soshow.basketball.common.adapter.ViewHolder;
import org.soshow.beautyedu.FindItemDetailActivity;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.bean.PersonInfo;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.DensityUtil;
import org.soshow.beautyedu.utils.GsonUtils;
import org.soshow.beautyedu.utils.ScreenUtils;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.NoScrollGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/2/12 0012.
 */

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    private TextView title_name;
    private ImageView ivHead;
    private TextView tv_username;
    private TextView tv_mobile;
    private TextView tv_region;
    private NoScrollGridView gridView;
    private String user_id;
    private Context context;
    private PersonInfo personInfo;
    private ImageView ivBack;
    private View loading;
    private CommonAdapter<String> picAdapter;
    private String userName;
    private String headUrl;
    private String mobile;
    private Button sendBtn;
    ArrayList<String> imageUrls = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.user_info_layout);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.titlebar_main);
        context = UserInfoActivity.this;
        title_name = (TextView) findViewById(R.id.title_main);
        user_id = getIntent().getStringExtra("other_user_id");
        title_name.setText("个人资料");
        initView();
        getInfo();
    }
    private void initView(){
        loading = (LinearLayout) findViewById(R.id.linear_load);
        loading.setVisibility(View.VISIBLE);
        ivBack = (ImageView) findViewById(R.id.back_search);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(this);
        sendBtn = (Button)findViewById(R.id.btn_feedback);
        sendBtn.setOnClickListener(this);
        ivHead = (ImageView)findViewById(R.id.persin_info_iv);
        tv_username = (TextView)findViewById(R.id.tv_touxiang);
        tv_mobile = (TextView)findViewById(R.id.mobile);
        tv_region = (TextView)findViewById(R.id.person_info_tv_email);
        gridView = (NoScrollGridView)findViewById(R.id.find_item_gridView);
    }
    private void getInfo(){
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", user_id);
        NetHelper.post(Constant.ACTION_USER_INFO, params,
                new SimpleSingleBeanNetHandler<Captcha>(context) {

                    @Override
                    protected void onSuccess(Captcha bean) {
                        LogUtils.e("添加评论返回的数据："+bean.info);
                        if(bean.result.equals("0")){
                            String data = bean.info;
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(data);
                                personInfo = GsonUtils.parseJSON( jsonObject.toString(), PersonInfo.class);
                                userName = personInfo.getNickname();
                                headUrl = personInfo.photo_url;
                                UniversalImageLoadTool.disPlayTrue( personInfo.photo_url, ivHead,
                                        R.drawable.default_face);
                                tv_username.setText(personInfo.getNickname());
                                mobile = personInfo.getMobile();
                                tv_mobile.setText(personInfo.getMobile());
                                tv_region.setText(personInfo.getArea());

                                String photos_url = personInfo.getAlbum_photos_url();

                                String[] photoarray = null;
                                photoarray = photos_url.split(";");
                                for (int i = 0; i < photoarray.length; i++) {
                                        imageUrls.add(photoarray[i]);
                                }
                                List<String> newlist = new ArrayList<String>();
                                if(imageUrls.size()<=3){
                                    newlist = imageUrls;
                                }else{
                                    newlist = imageUrls.subList(0,2);
                                }
                                setPicAdapter(newlist);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            loading.setVisibility(View.GONE);
                        }else{
                            ToastUtil.getInstance().showToast(context, bean.message);
                        }
                    }
                });
    }
    private void setPicAdapter(final List<String> newlist){
        picAdapter = new CommonAdapter<String>(context, newlist, R.layout.item_fragment_find_pic) {

            @Override
            public void convert(ViewHolder holder_pic, String pic) {
                ImageView ivContent = holder_pic.getView(R.id.fragmentfind_pics);
                int magin = DensityUtil.dip2px(UserInfoActivity.this, 2);
                int screenWidth = ScreenUtils.getScreenWidth(UserInfoActivity.this);
                gridView.setNumColumns(3);
                ivContent.setScaleType(ImageView.ScaleType.CENTER_CROP);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screenWidth /5, screenWidth / 5);
                layoutParams.setMargins(0, 0, magin, magin);
                gridView.setHorizontalSpacing(10);
                ivContent.setLayoutParams(layoutParams);
                UniversalImageLoadTool.disPlayTrue(pic, ivContent, R.drawable.defaultpic);
            }
        };
        gridView.setAdapter(picAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(UserInfoActivity.this, FindItemDetailActivity.class);
                intent.putExtra("other_user_id",  user_id);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
            }
        });
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_search:
                finish();
                overridePendingTransition(R.anim.anim_slider_left_in,
                        R.anim.anim_slider_right_out);
                break;
            case R.id.btn_feedback:
                if(mobile.equals(EMChatManager.getInstance().getCurrentUser())){
                    Toast.makeText(UserInfoActivity.this,"不能跟自己聊天",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(UserInfoActivity.this, ChatActivity.class);
                    intent.putExtra(com.easemob.chatuidemo.Constant.EXTRA_USER_ID, mobile);
                    startActivity(intent);
                }

                break;
        }
    }
}
