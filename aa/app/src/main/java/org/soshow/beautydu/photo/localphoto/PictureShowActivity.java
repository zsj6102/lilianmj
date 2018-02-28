/**
 * Copyright © 1999－2015 闲闪(厦门)互动文化传播有限公司(Soshow.org). All rights reserved.
 * you may not use this file except in compliance with the License.
 * http://www.soshow.org
 */
package org.soshow.beautydu.photo.localphoto;

import java.util.ArrayList;
import java.util.List;

import org.soshow.beautydu.photo.localphoto.imageaware.HackyViewPager;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;

import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class PictureShowActivity extends BaseActivity {

    public static final String EXTRA_CURRENT = "currentIndex";
    public static final String EXTRA_IMAGEURLS = "imageUrls";

    private static final String ISLOCKED_ARG = "isLocked";

    private ViewPager viewPager;
    private int currentIndex;
    private ArrayList<String> imageUrls;
    private TextView tvIndicate;
    private Context context;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_show);
        context = PictureShowActivity.this;

        Intent intent = getIntent();
        currentIndex = intent.getIntExtra(EXTRA_CURRENT, 0);
        imageUrls = intent.getStringArrayListExtra(EXTRA_IMAGEURLS);

        findViewById(R.id.picture_iv_back).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(R.anim.anim_slider_left_in,
                                R.anim.anim_slider_right_out);
                    }
                });
        progressBar = (ProgressBar) findViewById(R.id.picture_proBar);
        tvIndicate = (TextView) findViewById(R.id.picture_tv_indicate);
        viewPager = (HackyViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new SamplePagerAdapter());
        viewPager.setCurrentItem(currentIndex);
        viewPager.setOnPageChangeListener(new PicturePageChangeListener());
        tvIndicate.setText(String.format(
                context.getResources().getString(R.string.page_indicator),
                currentIndex + 1, imageUrls.size()));

        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG,
                    false);
            ((HackyViewPager) viewPager).setLocked(isLocked);
        }
    }

    private class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageUrls.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());

            UniversalImageLoadTool.disPlay(imageUrls.get(position), photoView,
                    R.color.bg_black, new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingComplete(String imageUri,
                                View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                FailReason failReason) {
                            super.onLoadingFailed(imageUri, view, failReason);
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            super.onLoadingStarted(imageUri, view);
                            progressBar.setVisibility(View.VISIBLE);
                        }

                    });
            container.addView(photoView, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    private class PicturePageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            currentIndex = arg0;
            tvIndicate.setText(String.format(
                    context.getResources().getString(R.string.page_indicator),
                    arg0 + 1, imageUrls.size()));
        }

    }

    private boolean isViewPagerActive() {
        return (viewPager != null && viewPager instanceof HackyViewPager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean(ISLOCKED_ARG,
                    ((HackyViewPager) viewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }

}
