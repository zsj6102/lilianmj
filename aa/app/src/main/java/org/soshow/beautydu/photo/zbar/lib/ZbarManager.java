package org.soshow.beautydu.photo.zbar.lib;

/**
 * 
 * @Author: huangenqiang
 * @Time: 2015年12月2日 上午10:35:49
 * @Description: Zbar调用类
 */
public class ZbarManager {

	static {
		System.loadLibrary("zbar");
	}

	public native String decode(byte[] data, int width, int height, boolean isCrop, int x, int y, int cwidth, int cheight);
}
