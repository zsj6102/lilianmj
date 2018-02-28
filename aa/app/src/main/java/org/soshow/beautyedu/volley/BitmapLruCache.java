package org.soshow.beautyedu.volley;


import android.graphics.Bitmap;

import android.support.v4.util.LruCache;
import com.android.volley.toolbox.ImageLoader;

public class BitmapLruCache extends LruCache<String, Bitmap>implements ImageLoader.ImageCache {
	public BitmapLruCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(String key, Bitmap bitmap) {
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	@Override
	public Bitmap getBitmap(String url) {
		//Log.d("1221", "缓存调用..."+url+"          图片对象为..."+get(url));
		return get(url);
	}



	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		//Log.d("1221", "图片缓存URL..."+url+"图片对象为"+bitmap);
		put(url, bitmap);

	}
	
}
