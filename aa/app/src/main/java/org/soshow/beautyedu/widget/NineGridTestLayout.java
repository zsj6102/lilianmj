package org.soshow.beautyedu.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.soshow.beautydu.photo.SelectPhoto;
import org.soshow.beautydu.photo.localphoto.PictureShowActivity;
import org.soshow.beautyedu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/12 0012.
 */

public class NineGridTestLayout extends NineGridLayout {
    protected static final int MAX_W_H_RATIO = 3;

    public NineGridTestLayout(Context context) {
        super(context);
    }

    public NineGridTestLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean displayOneImage(final RatioImageView imageView, String url, final int parentWidth) {

        ImageLoaderUtil.displayImage(mContext, imageView, url, ImageLoaderUtil.getPhotoImageOption(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                if(bitmap!=null){
                    int w = bitmap.getWidth();
                    int h = bitmap.getHeight();
                    int newW;
                    int newH;
                    if (h > w * MAX_W_H_RATIO) {//h:w = 5:3
                        newW = parentWidth / 2;
                        newH = newW * 5 / 3;
                    } else if (h < w) {//h:w = 2:3
                        newW = parentWidth * 2 / 3;
                        newH = newW * 2 / 3;
                    } else {//newH:h = newW :w
                        newW = parentWidth / 2;
                        newH = h * newW / w;
                    }
                    setOneImageLayoutParams(imageView, newW, newH);
                }



            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
        return false;
    }

    @Override
    protected void displayImage(RatioImageView imageView, String url) {
        ImageLoaderUtil.getImageLoader(mContext).displayImage(url, imageView, ImageLoaderUtil.getPhotoImageOption());
    }

    @Override
    protected void onClickImage(int i, String url, List<String> urlList) {

        Activity activity = (Activity)mContext;
        ArrayList<String> imageUrls = new ArrayList<String>();
        for(int m = 0; m < urlList.size();m++){
          imageUrls.add(urlList.get(m));
        }
        Intent intent = new Intent(activity, PictureShowActivity.class);
                    intent.putStringArrayListExtra(PictureShowActivity.EXTRA_IMAGEURLS, imageUrls);
                    intent.putExtra(PictureShowActivity.EXTRA_CURRENT, i);
        activity.startActivityForResult(intent, SelectPhoto.DELETE_IMAGE);
        activity.overridePendingTransition(R.anim.anim_slider_right_in, R.anim.anim_slider_left_out);
    }
}
