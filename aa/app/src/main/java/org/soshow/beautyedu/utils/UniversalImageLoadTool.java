/**
 * Copyright © 1999－2015 闲闪(厦门)互动文化传播有限公司(Soshow.org). All rights reserved.
 * you may not use this file except in compliance with the License.
 * http://www.soshow.org
 */
package org.soshow.beautyedu.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 图片加载工具类
 *
 * @author guilin
 */
public class UniversalImageLoadTool {
    /*
     * 如果经常出现OOM（别人那边看到的，觉得很有提的必要） ①减少配置之中线程池的大小，(.threadPoolSize).推荐1-5；
     * ②使用.bitmapConfig(Bitmap.config.RGB_565)代替ARGB_8888;
     * ③使用.imageScaleType(ImageScaleType.IN_SAMPLE_INT)或者
     * try.imageScaleType(ImageScaleType.EXACTLY)；
     * ④避免使用RoundedBitmapDisplayer.他会创建新的ARGB_8888格式的Bitmap对象；
     * ⑤使用.memoryCache(new WeakMemoryCache())，不要使用.cacheInMemory();
     */
    private static ImageLoader imageLoader = ImageLoader.getInstance();

    /**
     * 获取{@link ImageLoader}
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static ImageLoader getImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPoolSize(5).discCacheSize(100 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .discCacheFileCount(100)
                // .discCache(new TotalSizeLimitedDiscCache(CacheFile, 100 *
                // 1024 * 1024))
                .build();
        imageLoader.init(config);
        return imageLoader;
    }

    /**
     * 检查 {@linkplain (ImageLoaderConfiguration) is initialized
     * disPlayRoundOriginalwith configuration};
     */
    public static boolean checkImageLoader() {
        return imageLoader.isInited();
    }

    // 加载没有圆角的图片

    @SuppressWarnings("deprecation")
    public static void disPlayRoundOriginal(String uri, ImageView imageView,
            int default_pic) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(default_pic)
                .showImageForEmptyUri(default_pic).showImageOnFail(default_pic)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .cacheInMemory(false).cacheOnDisc(false)
                // .displayer(new RoundedBitmapDisplayer(5))
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();

        imageLoader.displayImage(uri, imageView, options);
    }

    /**
     * 加载image
     *
     * @param uri
     * @param imageView
     * @param default_pic
     */
    @SuppressWarnings("deprecation")
    public static void disPlay(String uri, ImageView imageView, int default_pic) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(default_pic)
                .showImageForEmptyUri(default_pic).showImageOnFail(default_pic)
                .cacheInMemory(false).cacheOnDisc(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
        imageLoader.displayImage(uri, imageView, options);
    }

    /**
     * 加载image
     *
     * @param uri
     * @param imageView
     * @param default_pic
     */
    public static void disPlayTrue(String uri, ImageView imageView,
            int default_pic) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(default_pic)
                .showImageForEmptyUri(default_pic).showImageOnFail(default_pic)
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();

        imageLoader.displayImage(uri, imageView, options);
    }

    /**
     * 加载image <br>
     * {@link SimpleImageLoadingListener} 加载监听
     *
     * @param uri
     * @param imageView
     * @param default_pic
     */
    public static void disPlay(String uri, ImageView imageView,
            int default_pic,
            SimpleImageLoadingListener simpleImageLoadingListener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(default_pic)
                .showImageForEmptyUri(default_pic).showImageOnFail(default_pic)
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
        imageLoader.displayImage(uri, imageView, options,
                simpleImageLoadingListener);
    }

    /**
     * 加载image <br>
     * {@link SimpleImageLoadingListener}加载监听<br>
     * {@link ImageLoadingProgressListener} 加载进度监听
     *
     * @param uri
     * @param imageView
     * @param default_pic
     * @param simpleImageLoadingListener
     * @param
     */
    public static void disPlay(String uri, ImageView imageView,
            int default_pic,
            SimpleImageLoadingListener simpleImageLoadingListener,
            ImageLoadingProgressListener imageLoadingProgressListener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(default_pic)
                .showImageForEmptyUri(default_pic).showImageOnFail(default_pic)
                .cacheInMemory(true).cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();

        imageLoader.displayImage(uri, imageView, options,
                simpleImageLoadingListener, imageLoadingProgressListener);
    }

    /**
     * 自定义{@link DisplayImageOptions}加载image
     *
     * @param uri
     * @param imageView
     * @param options
     */
    public static void disPlayDefined(String uri, ImageView imageView,
            DisplayImageOptions options) {
        imageLoader.displayImage(uri, imageView, options);
    }

    /**
     * 加载image
     *
     * @param uri
     * @param
     * @param default_pic
     */
    @SuppressWarnings("deprecation")
    public static void disPlay(String uri, ImageAware imageAware,
            int default_pic) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(default_pic)
                .showImageForEmptyUri(default_pic).showImageOnFail(default_pic)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
        imageLoader.displayImage(uri, imageAware, options);
    }

    /**
     * 加载圆角image
     *
     * @param uri
     * @param imageView
     * @param default_pic
     */
    @SuppressWarnings("deprecation")
    public static void disPlayRound(String uri, ImageView imageView,
            int default_pic) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(default_pic)
                .showImageForEmptyUri(default_pic).showImageOnFail(default_pic)
                .cacheInMemory(false).cacheOnDisc(false)
                .displayer(new RoundedBitmapDisplayer(20))
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
        imageLoader.displayImage(uri, imageView, options);
    }

    /**
     * 加载圆角image
     *
     * @param uri
     * @param imageView
     * @param default_pic
     */
    public static void disPlayRoundTrue(String uri, ImageView imageView,
            int default_pic) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(default_pic)
                .showImageForEmptyUri(default_pic).showImageOnFail(default_pic)
                .cacheInMemory(true).cacheOnDisk(true)
                .displayer(new RoundedBitmapDisplayer(5))
                .bitmapConfig(Bitmap.Config.RGB_565)
                // .displayer(new SimpleBitmapDisplayer())
                .build();

        imageLoader.displayImage(uri, imageView, options);
    }

    /**
     * 下载image
     *
     * @param uri
     */
    @SuppressWarnings("deprecation")
    public static void loadImage(String uri) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(false).cacheOnDisc(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
        imageLoader.loadImage(uri, options, null);
    }

    /**
     * uri获取图片返回bitmap
     *
     * @param uri
     * @return Bitmap
     */
    @SuppressWarnings("deprecation")
    public static Bitmap loadUriImage(String uri, int default_pic) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(default_pic)
                .showImageForEmptyUri(default_pic).showImageOnFail(default_pic)
                .cacheInMemory(false).cacheOnDisc(false)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new SimpleBitmapDisplayer()).build();
        return imageLoader.loadImageSync(uri, options);
    }

    /**
     * 下载图片<br>
     * {@link ImageLoadingListener}加载监听
     *
     * @param uri
     * @param imageLoadingListener
     */
    @SuppressWarnings("deprecation")
    public static void loadImage(String uri,
            ImageLoadingListener imageLoadingListener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(false).cacheOnDisc(false)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .displayer(new SimpleBitmapDisplayer()).build();
        imageLoader.loadImage(uri, options, imageLoadingListener);
    }

    /**
     * 下载图片<br>
     * {@link ImageLoadingListener}加载监听<br>
     * {@link ImageSize}图片大小
     *
     * @param uri
     * @param imageLoadingListener
     */
    public static void loadImage(String uri, ImageSize targetImageSize,
            ImageLoadingListener imageLoadingListener) {
        imageLoader.loadImage(uri, targetImageSize, imageLoadingListener);
    }

    /**
     * 下载图片<br>
     * {@link ImageLoadingListener}加载监听<br>
     * {@link ImageLoadingProgressListener}加载进度监听
     *
     * @param uri
     * @param imageLoadingListener
     */
    @SuppressWarnings("deprecation")
    public static void loadImage(String uri,
            ImageLoadingListener imageLoadingListener,
            ImageLoadingProgressListener imageLoadingProgressListener) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(false).cacheOnDisc(false)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer()).build();
        imageLoader.loadImage(uri, null, options, imageLoadingListener,
                imageLoadingProgressListener);
    }

    /**
     * 重命名
     * @param uri
     * @param targetUri
     */
    // public static void renameOnDiscCache(Context context,String uri,String
    // targetUri){
    // DiscCacheAware reserveDiscCache =
    // DefaultConfigurationFactory.createReserveDiscCache(new File(targetUri));
    // DiscCacheAware discCache = new
    // LimitedAgeDiscCache(StorageUtils.getCacheDirectory(context),new
    // Md5FileNameGenerator(), 3600*24*15L);
    // File imageFile = discCache.get(uri);
    // File imageTargetFile = discCache.get(targetUri);
    // File cacheDir = imageFile.getParentFile();
    // if (cacheDir == null || (!cacheDir.exists() && !cacheDir.mkdirs())) {
    // imageFile = reserveDiscCache.get(uri);
    // imageTargetFile = reserveDiscCache.get(targetUri);
    // cacheDir = imageFile.getParentFile();
    // if (cacheDir != null && !cacheDir.exists()) {
    // cacheDir.mkdirs();
    // }
    // }
    // imageFile.renameTo(imageTargetFile);
    // }

    /**
     * 清除缓存
     */
    @SuppressWarnings("deprecation")
    public static void clear() {
        imageLoader.clearMemoryCache();
        imageLoader.clearDiscCache();
    }

    /**
     * 重启加载
     */
    public static void resume() {
        imageLoader.resume();
    }

    /**
     * 暂停加载
     */
    public static void pause() {
        imageLoader.pause();
    }

    /**
     * 停止加载
     */
    public static void stop() {
        imageLoader.stop();
    }

    /**
     * 销毁加载
     */
    public static void destroy() {
        imageLoader.destroy();
    }
}
