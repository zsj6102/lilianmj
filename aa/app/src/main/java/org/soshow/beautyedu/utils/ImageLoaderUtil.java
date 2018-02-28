package org.soshow.beautyedu.utils;

import java.lang.reflect.Field;




import org.soshow.beautyedu.volley.RequestManager;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;

/**
 * 
 * volley框架获取图片
 */
public class ImageLoaderUtil {
	public static void getImage(Context context, ImageView v, String iamgeUrl, int defaultImageResId,
			int errorImageResId) {
		getImage(context, v, iamgeUrl, defaultImageResId, errorImageResId, 0, 0);
	}

	public static void getImage(Context context, ImageView v, String iamgeUrl, int defaultImageResId,
			int errorImageResId, int maxWidth, int maxHeight) {
		try {
			/* volley */
			ImageLoader imageLoader = RequestManager.getImageLoader();
			ImageListener listener = ImageLoader.getImageListener(v, defaultImageResId, errorImageResId);
			ImageSize imageSize = getImageViewWidth(v, context);

			if (maxWidth != 0 && maxHeight != 0) {
				imageSize.width = maxWidth;
				imageSize.height = maxHeight;
			}

			//Log.d("imageSize", "width>>" + imageSize.width + "height>>" + imageSize.height);
			imageLoader.get(iamgeUrl, listener, imageSize.width, imageSize.height);
			
			//imageLoader.get(iamgeUrl, listener);

		} catch (Exception e) {
			// TODO: handle exception
			Log.d("1221", "获取图片错误信息"+e.toString());
		}

	}

	/**
	 * 根据ImageView获得适当的压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	private static ImageSize getImageViewWidth(ImageView imageView, Context context) {
		ImageSize imageSize = new ImageSize();
		//final DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
		final LayoutParams params = imageView.getLayoutParams();
		DisplayMetrics dm = new DisplayMetrics();
		try {
			Activity activity = (Activity) context;
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		} catch (Exception e) {
			// TODO: handle exception
		}

		int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView.getWidth(); // Get
																							// actual
																							// image
																							// width
		//Log.d("123456", "长为" + imageView.getWidth() + "高为" + imageView.getHeight());
		if (width <= 0)
			width = params.width; // Get layout width parameter
		if (width <= 0)
			width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
																	// maxWidth
																	// parameter
		if (width <= 0)
			// width = displayMetrics.widthPixels;

			width = dm.widthPixels;
		int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView.getHeight(); // Get
																								// actual
																								// image
																								// height
		if (height <= 0)
			height = params.height; // Get layout height parameter
		if (height <= 0)
			height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
																		// maxHeight
																		// parameter
		if (height <= 0)
			// height = displayMetrics.heightPixels;
			height = width / 3;
		imageSize.width = width;
		imageSize.height = height;
		//Log.d("123456", "长为" + imageSize.width + "高为" + imageSize.height);
		return imageSize;

	}

	private static class ImageSize {
		int width;
		int height;
	}

	/**
	 * 反射获得ImageView设置的最大宽度和高度
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
		}
		return value;
	}

}
