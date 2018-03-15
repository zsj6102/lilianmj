package org.soshow.beautydu.photo.localphoto;

import java.io.File;
import java.util.ArrayList;

import org.soshow.beautydu.photo.localphoto.imageaware.HackyViewPager;
import org.soshow.beautydu.photo.localphoto.imageaware.RotateImageViewAware;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.BaseActivity;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;
import org.soshow.beautyedu.widget.MyDialog;

import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


/**
 * @Package: com.soshow.dfhon.forum
 * 
 * @File: ImageDelActivity.java
 * 
 *
 * @Author: cjm Date: 2015年5月6日
 * 
 */
public class PictureDeleteActivity extends BaseActivity implements OnClickListener {

    public static boolean isChange;
    public static final String IS_NO_DELETE = "noDelete";
    public static final String EXTRA_CURRENT = "currentIndex";
    public static final String EXTRA_IMAGEURLS = "imageUrls";
    private static final String ISLOCKED_ARG = "isLocked";

    private ViewPager mViewPager;
    private int currentIndex;
    private ArrayList<String> imageUrls;
    private TextView tvIndicate;
    private Context context;
    private SamplePagerAdapter samplePagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_delete);
        context = PictureDeleteActivity.this;

        Intent intent = getIntent();
        currentIndex = intent.getIntExtra(EXTRA_CURRENT, 0);
        imageUrls = intent.getStringArrayListExtra(EXTRA_IMAGEURLS);

        ImageView ivBack = (ImageView) findViewById(R.id.common_back);
        ivBack.setVisibility(View.VISIBLE);
        ivBack.setOnClickListener(this);
        TextView tvRight = (TextView) findViewById(R.id.common_right);
        tvRight.setText(R.string.delete);
        tvRight.setOnClickListener(this);
        if(intent.getBooleanExtra(IS_NO_DELETE, false)){
        	tvRight.setVisibility(View.GONE);
        }else{
        	tvRight.setVisibility(View.VISIBLE);
        }
        tvIndicate = (TextView) findViewById(R.id.imageDel_tv_indicate);

        mViewPager = (HackyViewPager) findViewById(R.id.imageDel_viewpage);
        samplePagerAdapter = new SamplePagerAdapter();
        mViewPager.setAdapter(samplePagerAdapter);
        mViewPager.setOnPageChangeListener(new PicturePageChangeListener());
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(currentIndex);

        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG,
                    false);
            ((HackyViewPager) mViewPager).setLocked(isLocked);
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
//            UniversalImageLoadTool.disPlay(imageUrls.get(position),
//                    new RotateImageViewAware(photoView, imageUrls.get(position)
//                            .replace("file:/", "")), R.drawable.defaultpic);
            Glide.with(PictureDeleteActivity.this).load(new File(imageUrls.get(position))).into(photoView);
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

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // getMenuInflater().inflate(R.menu.viewpager_menu, menu);
    // return super.onCreateOptionsMenu(menu);
    // }

    // @Override
    // public boolean onPrepareOptionsMenu(Menu menu) {
    // menuLockItem = menu.findItem(R.id.menu_lock);
    // toggleLockBtnTitle();
    // menuLockItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
    // @Override
    // public boolean onMenuItemClick(MenuItem item) {
    // toggleViewPagerScrolling();
    // toggleLockBtnTitle();
    // return true;
    // }
    // });
    //
    // return super.onPrepareOptionsMenu(menu);
    // }
    //
    // private void toggleViewPagerScrolling() {
    // if (isViewPagerActive()) {
    // ((HackyViewPager) mViewPager).toggleLock();
    // }
    // }
    //
    // private void toggleLockBtnTitle() {
    // boolean isLocked = false;
    // if (isViewPagerActive()) {
    // isLocked = ((HackyViewPager) mViewPager).isLocked();
    // }
    // String title = (isLocked) ? getString(R.string.menu_unlock) :
    // getString(R.string.menu_lock);
    // if (menuLockItem != null) {
    // menuLockItem.setTitle(title);
    // }
    // }

    private boolean isViewPagerActive() {
        return (mViewPager != null && mViewPager instanceof HackyViewPager);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean(ISLOCKED_ARG,
                    ((HackyViewPager) mViewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.common_back:
            finish();
            overridePendingTransition(R.anim.anim_slider_left_in,
					R.anim.anim_slider_right_out);
            break;
        case R.id.common_right:
            deletePicuture();
            break;
        default:
            break;
        }
    }

    // 删除图片
    private void deletePicuture() {
    	final MyDialog.MyBuilder dailog = new MyDialog.MyBuilder(this);
		dailog.setPositiveButton("确定",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
                        imageUrls.remove(currentIndex);
                        SelectPhotoActivity.hasList.remove(currentIndex);
                        isChange = true;
                        samplePagerAdapter.notifyDataSetChanged();
                        finish();
                        if (imageUrls.size() > 0) {
                            Intent intent = new Intent(context,
                                    PictureDeleteActivity.class);
                            intent.putStringArrayListExtra(
                                    PictureDeleteActivity.EXTRA_IMAGEURLS,
                                    imageUrls);
                            if (currentIndex >= imageUrls.size()) {
                                currentIndex--;
                            }
                            intent.putExtra(
                                    PictureDeleteActivity.EXTRA_CURRENT,
                                    currentIndex);
                            startActivity(intent);
                            overridePendingTransition(R.anim.anim_slider_right_in,
                                    R.anim.anim_slider_left_out);
                        }
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
		tvTitle.setText("是否删除该照片？");
		EditText et = (EditText) layout
				.findViewById(R.id.ed_input);
		et.setVisibility(View.GONE);
		creatDialog.show();
    	
    	
    	
    	
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(R.string.delete);
//        builder.setMessage(R.string.confirm_delete);
//        builder.setPositiveButton(R.string.confirm,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        imageUrls.remove(currentIndex);
//                        SelectPhotoActivity.hasList.remove(currentIndex);
//                        isChange = true;
//                        samplePagerAdapter.notifyDataSetChanged();
//                        finish();
//                        if (imageUrls.size() > 0) {
//                            Intent intent = new Intent(context,
//                                    PictureDeleteActivity.class);
//                            intent.putStringArrayListExtra(
//                                    PictureDeleteActivity.EXTRA_IMAGEURLS,
//                                    imageUrls);
//                            if (currentIndex >= imageUrls.size()) {
//                                currentIndex--;
//                            }
//                            intent.putExtra(
//                                    PictureDeleteActivity.EXTRA_CURRENT,
//                                    currentIndex);
//                            startActivity(intent);
//                        }
//                    }
//                });
//        builder.setNegativeButton(R.string.cancel,
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                });
//        builder.create().show();
    }
}
