package org.soshow.beautyedu.activity.dynamic;

import java.io.File;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.soshow.beautydu.photo.localphoto.adapter.GridImageAdapter;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.application.MyApplication;
import org.soshow.beautyedu.json.bean.Captcha;
import org.soshow.beautyedu.json.core.NetHelper;
import org.soshow.beautyedu.json.handler.SimpleSingleBeanNetHandler;

import org.soshow.beautyedu.utils.Constant;

import org.soshow.beautyedu.utils.ProgressDialogUtil;
import org.soshow.beautyedu.utils.ToastUtil;
import org.soshow.common.adapter.ChatMessage;

import android.app.Dialog;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.Bundle;


import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;
import com.easemob.chatuidemo.ui.BaseActivity;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;

/**
 * @Author: caixiaozhen
 * @Time: 2016年5月28日 下午5:17:14
 * @Description: 相册多图片选择及预览和删除
 */
public class PublishDynamicActivity extends BaseActivity implements OnClickListener, AdapterView.OnItemClickListener {
    private ArrayList<ChatMessage> mDatas = new ArrayList<ChatMessage>();
    private GridView gridView;
    private ArrayList<ImageItem> defaultDataArray = new ArrayList<>();
    private int maxImgCount = 9;               //允许选择图片最大数
    private ImagePicker imagePicker;
    private GridImageAdapter gridImageAdapter;
    private Context context;
    private SharedPreferences sp;
    private Editor editor;
    private EditText et;

    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public static boolean isPublish = false;
    private List<File> fileList;
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
//        SelectPhotoActivity.hasList.clear();
        initImagePicker();
        gridImageAdapter = new GridImageAdapter(this, defaultDataArray);
        gridView.setAdapter(gridImageAdapter);
        gridView.setOnItemClickListener(this);
        // 发表动态
        findViewById(R.id.publish_dynamic_tv_submit).setOnClickListener(this);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id){
        if(position == defaultDataArray.size()){
            //打开选择,本次允许选择的数量
            imagePicker.setSelectLimit(maxImgCount - defaultDataArray.size());
            Intent intent = new Intent(PublishDynamicActivity.this, ImageGridActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SELECT);
        }else{
            //打开预览
            Intent intentPreview = new Intent(PublishDynamicActivity.this, ImagePreviewDelActivity.class);
            intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, defaultDataArray);
            intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
            intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
            startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
        }
    }
    private void initImagePicker() {
        imagePicker = MyApplication.getInstance().getImagePicker();
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                defaultDataArray.addAll(images);
                GridImageAdapter gridImageAdapter1 = new GridImageAdapter(this,defaultDataArray);
                gridView.setAdapter(gridImageAdapter1);
                fileList = new ArrayList<>();
                for (int i = 0; i < defaultDataArray.size(); i++) {
                    fileList.add(new File(defaultDataArray.get(i).path));
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                defaultDataArray.clear();
                defaultDataArray.addAll(images);
                gridImageAdapter.notifyDataSetChanged();
                fileList = new ArrayList<>();
                for (int i = 0; i < defaultDataArray.size(); i++) {
                    fileList.add(new File(defaultDataArray.get(i).path));
                }
            }
        }
    }


    private void dynamic_add() {
        dialog = ProgressDialogUtil.createLoadingDialog(this, "提交中...", true, true);
        dialog.show();
        Map param = new HashMap();
//            param.put("tocken", (mToken));
//            param.put("app_nonce", (app_nonce));
        param.put("content", (et.getText().toString().trim()));
        if(fileList!=null){
            for (int i = 0; i < fileList.size(); i++) {
                param.put("file" + (i + 1), fileList.get(i));
            }
        }
        NetHelper.postUpload(Constant.DYNAMIC_ADD, param, new SimpleSingleBeanNetHandler<Captcha>(this) {
            @Override
            protected void onSuccess(Captcha bean) {
                if (bean.isSuccess()) {
                    ToastUtil.getInstance().showToast(PublishDynamicActivity.this, "发表成功");
                    PublishDynamicActivity.isPublish = true;//TODO
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
