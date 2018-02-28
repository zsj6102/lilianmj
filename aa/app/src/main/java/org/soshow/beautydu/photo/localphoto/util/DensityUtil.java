package org.soshow.beautydu.photo.localphoto.util;

/**
 * @author zzy
 * @date 2015年4月17日 类说明:屏幕分辨率px跟dip互相转，屏幕长宽比和屏幕宽度和高度
 */

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

/*
 * 屏幕分辨率px跟dip互相转化
 */
public class DensityUtil {

	/*
	 * 将dip转化为px像素
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/*
	 * 将px像素转化为dip
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取屏幕宽度和高度，单位为px
	 * 
	 * @param context
	 * @return
	 */
	public static Point getScreenMetrics(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int w_screen = dm.widthPixels;
		int h_screen = dm.heightPixels;
		// L.i(TAG, "Screen---Width = " + w_screen + " Height = " + h_screen +
		// " densityDpi = " + dm.densityDpi);
		return new Point(w_screen, h_screen);

	}

	/**
	 * 获取屏幕长宽比
	 * 
	 * @param context
	 * @return
	 */
	public static float getScreenRate(Context context) {
		Point P = getScreenMetrics(context);
		float H = P.y;
		float W = P.x;
		return (H / W);
	}
}
