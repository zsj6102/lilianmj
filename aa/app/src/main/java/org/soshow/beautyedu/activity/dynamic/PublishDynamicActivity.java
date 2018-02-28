package org.soshow.beautyedu.activity.dynamic;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.soshow.beautydu.http.AndroidMultiPartEntity;
import org.soshow.beautydu.photo.SelectPhoto;
import org.soshow.beautydu.photo.localphoto.PictureDeleteActivity;
import org.soshow.beautydu.photo.localphoto.SelectPhotoActivity;
import org.soshow.beautydu.photo.localphoto.adapter.GridImageAdapter;
import org.soshow.beautydu.photo.localphoto.bean.PhotoInfo;
import org.soshow.beautydu.photo.localphoto.util.CameraUtil;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;
import org.soshow.beautyedu.json.utils.LogUtils;
import org.soshow.beautyedu.utils.Constant;
import org.soshow.beautyedu.utils.LoginUtil;
import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.StringUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.beautyedu.utils.TokenManager;
import org.soshow.common.adapter.ChatMessage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import static org.jivesoftware.smackx.packet.MultipleAddresses.TO;
/**
 * @Author: caixiaozhen
 * @Time: 2016年5月28日 下午5:17:14
 * @Description: 相册多图片选择及预览和删除
 */
public class PublishDynamicActivity extends BaseActivity implements OnClickListener, OnItemClickListener {
    private ArrayList<ChatMessage> mDatas = new ArrayList<ChatMessage>();
    private GridView gridView;
    private ArrayList<PhotoInfo> photoInfos;
    private GridImageAdapter gridImageAdapter;
    private Context context;
    private SharedPreferences sp;
    private Editor editor;
    private EditText et;
    private String app_nonce;
    private String mToken;
    public static boolean isPublish = false;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    gridImageAdapter.notifyDataSetChanged();
                    break;
                case 2:// token获取
                    if (LoginUtil.login_handlered) {
                        LoginUtil.login_handlered = false;
                        mToken = sp.getString("mToken", null);
                        if (mToken != null) {
                            photoInfos.clear();
                            dynamic_add();
                        } else {
                            Toast.makeText(PublishDynamicActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
                            if (PublishDynamicActivity.this != null) {
                                Toast.makeText(PublishDynamicActivity.this, "网络不佳，请稍后再试", Toast.LENGTH_SHORT).show();
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
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_dynamic);
        sp = getSharedPreferences("huizhongjia", Context.MODE_PRIVATE);
        editor = sp.edit();
        initView();
    }

    private void initView() {
        findViewById(R.id.publish_dynamic_iv_back).setOnClickListener(this);
        et = (EditText) findViewById(R.id.activity_dynamic_et);
        final RelativeLayout view = (RelativeLayout) findViewById(R.id.uploadPictureLayout);
        context = PublishDynamicActivity.this;
        gridView = (GridView) findViewById(R.id.review_gridview);
        SelectPhotoActivity.hasList.clear();
        photoInfos = new ArrayList<PhotoInfo>();
        gridImageAdapter = new GridImageAdapter(this, photoInfos);
        gridView.setAdapter(gridImageAdapter);
        gridView.setOnItemClickListener(this);
        // 发表动态
        findViewById(R.id.publish_dynamic_tv_submit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.publish_dynamic_iv_back:
                finish();
                overridePendingTransition(R.anim.anim_slider_left_in, R.anim.anim_slider_right_out);
                break;
            case R.id.publish_dynamic_tv_submit:
                dynamic_add();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == photoInfos.size()) {
            SelectPhoto.getInstance(this).selectPictureStyle(this, CameraUtil.sdCardIsExit());
        } else {
            ArrayList<String> imageUrls = new ArrayList<String>();
            for (int i = 0; i < photoInfos.size(); i++) {
                imageUrls.add(photoInfos.get(i).getPath_file());
            }
            Intent intent = new Intent(context, PictureDeleteActivity.class);
            intent.putStringArrayListExtra(PictureDeleteActivity.EXTRA_IMAGEURLS, imageUrls);
            intent.putExtra(PictureDeleteActivity.EXTRA_CURRENT, position);
            startActivityForResult(intent, SelectPhoto.DELETE_IMAGE);
            overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 20) {
            photoInfos.clear();
            List<PhotoInfo> photoes = SelectPhotoActivity.hasList;
            Collection<PhotoInfo> c = photoes;
            photoInfos.addAll(c);
            gridImageAdapter = new GridImageAdapter(context, photoInfos);
            gridView.setAdapter(gridImageAdapter);
        } else if (resultCode == RESULT_OK) {
            if (requestCode == SelectPhoto.CODE_CAMERA) {
                String fileName = CameraUtil.getRealFilePath();
                if (new File(fileName).exists()) {
                    PhotoInfo photoInfo = new PhotoInfo();
                    photoInfo.setPath_file("file://" + fileName);
                    photoInfo.setPath_absolute(fileName);
                    photoInfos.add(photoInfo);
                    SelectPhotoActivity.hasList.add(photoInfo);
                    gridImageAdapter = new GridImageAdapter(context, photoInfos);
                    gridView.setAdapter(gridImageAdapter);
                }
            }
        }
        if (requestCode == SelectPhoto.DELETE_IMAGE) {
            photoInfos.clear();
            List<PhotoInfo> photoes = SelectPhotoActivity.hasList;
            Collection<PhotoInfo> c = photoes;
            photoInfos.addAll(c);
            gridImageAdapter.notifyDataSetChanged();
            PictureDeleteActivity.isChange = false;
        }
    }

    private void dynamic_add() {
        dialog = ProgressDialogUtil.createLoadingDialog(this, "提交中...", true, true);
        dialog.show();
        Map param  = new HashMap();
//            param.put("tocken", (mToken));
//            param.put("app_nonce", (app_nonce));
        param.put("content", (et.getText().toString().trim()));
        for (int i = 0; i < photoInfos.size(); i++) {
            param.put("file" + (i + 1), new File(photoInfos.get(i).getPath_absolute()));
        }
        NetHelper.postUpload(Constant.DYNAMIC_ADD, param, new SimpleSingleBeanNetHandler<Captcha>(this) {
            @Override
            protected void onSuccess(Captcha bean) {
                if (bean.isSuccess()) {
                    ToastUtil.getInstance().showToast(PublishDynamicActivity.this, "发表成功");
                    PublishDynamicActivity.isPublish=true;//TODO
                    finish();
                } else {
                    ToastUtil.getInstance().showToast(PublishDynamicActivity.this, bean.getMessage_());
                }
            }

            @Override
            public void complete() {
                super.complete();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        });
    }
}
