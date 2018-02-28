package org.soshow.beautydu.photo.localphoto;

import java.util.ArrayList;
import java.util.List;

import org.soshow.beautydu.photo.localphoto.PhotoFolderFragment.OnPageLodingClickListener;
import org.soshow.beautydu.photo.localphoto.PhotoFragment.OnPhotoSelectClickListener;
import org.soshow.beautydu.photo.localphoto.bean.PhotoInfo;
import org.soshow.beautydu.photo.localphoto.bean.PhotoSerializable;
import org.soshow.beautydu.photo.localphoto.util.CheckImageLoaderConfiguration;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.application.MyApplication;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @title SelectPhotoActivity.java
 * 选择照片
 */
public class SelectPhotoActivity extends FragmentActivity implements
        OnPageLodingClickListener, OnPhotoSelectClickListener {

    private PhotoFolderFragment photoFolderFragment;

    private Button btnright;
    
    private ImageView btnback;

    private TextView title;

    public static List<PhotoInfo> hasList = new ArrayList<PhotoInfo>();
    // public static List<PhotoInfo> hasSave = new ArrayList<PhotoInfo>();

    private FragmentManager manager;
    private int backInt = 0;
    /**
     * 已选择图片数量
     */
    private int count;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectphoto);


        count = getIntent().getIntExtra("count", 0);

        manager = getSupportFragmentManager();

        // hasList = new ArrayList<PhotoInfo>();

        btnback = (ImageView) findViewById(R.id.btnback);
        btnright = (Button) findViewById(R.id.btnright);
        title = (TextView) findViewById(R.id.title);
        btnback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (backInt == 0) {
                    finish();
                    overridePendingTransition(R.anim.anim_slider_left_in,
                            R.anim.anim_slider_right_out);
                } else if (backInt == 1) {
                    backInt--;
                    // hasList.clear();
                    // hasSave.addAll(hasList);
                    title.setText("请选择相册");
                    btnright.setVisibility(View.GONE);
                    FragmentTransaction transaction = manager
                            .beginTransaction();
                    transaction.show(photoFolderFragment).commit();
                    manager.popBackStack(0, 0);
                }
            }
        });
        btnright.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (hasList.size() > 0) {
                    setResult(20);
                    finish();
                    overridePendingTransition(R.anim.anim_slider_left_in,
                            R.anim.anim_slider_right_out);
                } else {
                    Toast.makeText(SelectPhotoActivity.this, "至少选择一张图片",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        title.setText("请选择相册");
        btnright.setVisibility(View.GONE);

        photoFolderFragment = new PhotoFolderFragment();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.body, photoFolderFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        CheckImageLoaderConfiguration.checkImageLoaderConfiguration(this);
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onPageLodingClickListener(List<PhotoInfo> list) {
        // TODO Auto-generated method stub
    	btnright.setVisibility(View.VISIBLE);
        title.setText("已选择" + hasList.size() + "张");
        FragmentTransaction transaction = manager.beginTransaction();
        PhotoFragment photoFragment = new PhotoFragment();
        Bundle args = new Bundle();
        PhotoSerializable photoSerializable = new PhotoSerializable();
        for (PhotoInfo photoInfoBean : list) {
            photoInfoBean.setChoose(false);
        }
        photoSerializable.setList(list);
        args.putInt("count", count);
        args.putSerializable("list", photoSerializable);
        photoFragment.setArguments(args);
        transaction = manager.beginTransaction();
        transaction.hide(photoFolderFragment).commit();
        transaction = manager.beginTransaction();
        transaction.add(R.id.body, photoFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
        backInt++;
    }

    @Override
    public void onPhotoSelectClickListener(PhotoInfo photoInfo) {
        // TODO Auto-generated method stub
        // hasList.clear();
        // for (PhotoInfo photoInfoBean : list) {
        // if(photoInfoBean.isChoose()){
        // hasList.add(photoInfoBean);
        // }
        // }
        // // hasList.addAll(hasSave);
        // title.setText("已选择"+hasList.size()+"张");
        if (photoInfo.isChoose()) {
            hasList.add(photoInfo);
        } else {
            for (int i = 0; i < hasList.size(); i++) {
                PhotoInfo photoItem = hasList.get(i);
                if (photoInfo.getPath_absolute().equals(
                        photoItem.getPath_absolute())) {
                    hasList.remove(i);
                }
            }
        }
        title.setText("已选择" + hasList.size() + "张");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && backInt == 0) {
            finish();
            overridePendingTransition(R.anim.anim_slider_left_in,
                    R.anim.anim_slider_right_out);
        } else if (keyCode == KeyEvent.KEYCODE_BACK && backInt == 1) {
            backInt--;
            // hasList.clear();
            title.setText("请选择相册");
            btnright.setVisibility(View.GONE);
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.show(photoFolderFragment).commit();
            manager.popBackStack(0, 0);
        }
        return false;
    }
}
