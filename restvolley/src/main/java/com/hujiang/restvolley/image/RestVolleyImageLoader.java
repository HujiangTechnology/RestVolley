/*
 * HJImageLoader      2015-11-24
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.hujiang.restvolley.RequestEngine;
import com.hujiang.restvolley.RestVolley;

import java.io.File;

/**
 * image loader.support http image request, local bitmap request that on the sdcard, assets, res/drawable, res/raw, using the system ContentProvider.
 * <p></p>
 * http image request, uri: http://www.google.com/xxx/xxx.png<br>
 * sdcard image request, uri: file:///mnt/sdcard/xxx.png<br>
 * assets image request, uri: assets://assets_default.png<br>
 * res image request, uri: “drawable://” + R.drawable.drawable_default, or "drawable://" + R.raw.defaut<br>
 * ContentProvider image request, uri: content://media/external/images/media/27916<br>
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-11-24
 */
public class RestVolleyImageLoader {

    private static final String IMAGE_REQUEST_ENGINE_TAG = "image";
    private static RestVolleyImageLoader sInstance;

    private RequestEngine mRequestEngine;
    private ImageLoaderCompat mImageLoader;
    private RestVolleyImageCache mRestVolleyImageCache;

    private Context mContext;

    private RestVolleyImageLoader(Context context) {
        mContext = context.getApplicationContext();

        mRequestEngine = RestVolley.newRequestEngine(context, IMAGE_REQUEST_ENGINE_TAG, true);

        mRestVolleyImageCache = new RestVolleyImageCache(context);
        mImageLoader = new ImageLoaderCompat(mContext, mRequestEngine.requestQueue, mRestVolleyImageCache);
    }

    /**
     * singleton of {@link RestVolleyImageLoader}.
     * @param context {@link Context}
     * @return {@link RestVolleyImageLoader}
     */
    public static RestVolleyImageLoader instance(Context context) {
        if (sInstance == null) {
            synchronized (RestVolleyImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new RestVolleyImageLoader(context);
                }
            }
        }

        return sInstance;
    }

    /**
     * set the global image loader config.
     * @param config {@link ImageLoaderGlobalConfig}
     */
    public void setConfig(ImageLoaderGlobalConfig config) {
        if (config != null) {
            if (config.requestEngine != null) {
                mRequestEngine = config.requestEngine;
            }

            mRestVolleyImageCache = new RestVolleyImageCache(mContext, config.memCacheSize, config.diskCacheSize, config.diskCacheDir);
            mImageLoader = new ImageLoaderCompat(mContext, mRequestEngine.requestQueue, mRestVolleyImageCache);
        }
    }

    /**
     * display image.
     * @param uri image uri
     * @param imageView image view
     */
    public void displayImage(final String uri, final ImageView imageView) {
        mImageLoader.load(uri, ImageLoaderCompat.getImageListener(uri, imageView, null, null));
    }

    /**
     * display image with the specified {@link ImageLoadOption}.
     * @param uri uri
     * @param imageView image view
     * @param imageLoadOption {@link ImageLoadOption}
     */
    public void displayImage(final String uri, final ImageView imageView, final ImageLoadOption imageLoadOption) {
        mImageLoader.load(uri, ImageLoaderCompat.getImageListener(uri, imageView, imageLoadOption, null), imageLoadOption);
    }

    /**
     * bind view
     * @param uri uri
     * @param imageView image view to bind bitmap
     * @param imageDisplayer ImageDisplayer
     */
    public void displayImage(final String uri, final ImageView imageView, final ImageDisplayer imageDisplayer) {
        mImageLoader.load(uri, ImageLoaderCompat.getImageListener(uri, imageView, null, imageDisplayer));
    }

    /**
     * bind view
     * @param uri uri
     * @param imageView view to bind bitmap
     * @param option {@link ImageLoadOption}
     * @param displayer {@link ImageDisplayer}
     */
    public void displayImage(final String uri, final ImageView imageView, final ImageLoadOption option, final ImageDisplayer displayer) {
        mImageLoader.load(uri, ImageLoaderCompat.getImageListener(uri, imageView, option, displayer), option);
    }

    /**
     * just load the image.
     * @param uri uri
     * @param listener {@link ImageLoaderCompat.ImageListener}
     */
    public void loadImage(final String uri, final ImageLoaderCompat.ImageListener listener) {
        mImageLoader.load(uri, listener);
    }

    /**
     * just load the image with the specified {@link ImageLoadOption}.
     * @param uri uri
     * @param imageLoadOption {@link ImageLoadOption}
     * @param listener {@link ImageLoaderCompat.ImageListener}
     */
    public void loadImage(final String uri, final ImageLoadOption imageLoadOption, final ImageLoaderCompat.ImageListener listener) {
        mImageLoader.load(uri, listener, imageLoadOption);
    }

    /**
     * sync load the image.
     * @param uri image uri
     * @return Bitmap
     */
    public Bitmap syncLoadImage(String uri) {
        return mImageLoader.syncLoad(uri);
    }

    /**
     * sync load the image.
     * @param uri image uri
     * @param imageLoadOption {@link ImageLoadOption}
     * @return Bitmap
     */
    public Bitmap syncLoadImage(String uri, ImageLoadOption imageLoadOption) {
        return mImageLoader.syncLoad(uri, imageLoadOption);
    }

    /**
     * is the specified uri image cached or not.
     * @param uri image uri.
     * @param maxWidth max width
     * @param maxHeight max height
     * @return is cached or not
     */
    public boolean isCached(String uri, int maxWidth, int maxHeight) {
     return mImageLoader.isCached(uri, maxWidth, maxHeight);
    }

    /**
     * is the specified uri image cached or not.
     * @param uri uri
     * @param maxWidth max width
     * @param maxHeight max height
     * @param scaleType {@link android.widget.ImageView.ScaleType}
     * @return is cached or not
     */
    public boolean isCached(String uri, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
        return mImageLoader.isCached(uri, maxWidth, maxHeight, scaleType);
    }

    /**
     * is the specified uri image cached or not.
     * @param uri image uri
     * @return is cached or not
     */
    public boolean isCached(String uri) {
        return mImageLoader.isCached(uri, 0, 0);
    }

    /**
     * get the disk cache path with the specified uri
     * @param uri image requested uri
     * @return disk cache path
     */
    public String getDiskCachePath(String uri) {
        return getDiskCachePath(uri, 0, 0);
    }

    /**
     * get the disk cache path with the specified uri, width, height
     * @param uri image requested uri
     * @param maxWidth width
     * @param maxHeight height
     * @return disk cache path
     */
    public String getDiskCachePath(String uri, int maxWidth, int maxHeight) {
        return getDiskCachePath(uri, maxWidth, maxHeight, ImageView.ScaleType.CENTER_INSIDE);
    }

    /**
     * get the disk cache path with the specified uri, width, height, scaleType
     * @param uri image request uri
     * @param maxWidth width
     * @param maxHeight height
     * @param scaleType scale type
     * @return disk cache path
     */
    public String getDiskCachePath(String uri, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
        return mRestVolleyImageCache.getDiskCachePath(ImageLoaderCompat.generateCacheKey(uri, maxWidth, maxHeight, scaleType));
    }

    /**
     * remove the specified uri image cache.
     * @param uri image uri
     */
    public void removeCache(String uri) {
        mImageLoader.removeCache(uri, 0, 0);
    }

    /**
     * remove the specified uri image cache.
     * @param uri image uri
     * @param maxWidth max width
     * @param maxHeight max height
     */
    public void removeCache(String uri, int maxWidth, int maxHeight) {
        mImageLoader.removeCache(uri, maxWidth, maxHeight);
    }

    /**
     * remove the specified uri image cache.
     * @param uri image uri
     * @param maxWidth max width
     * @param maxHeight max height
     * @param scaleType {@link android.widget.ImageView.ScaleType}
     */
    public void removeCache(String uri, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
        mImageLoader.removeCache(uri, maxWidth, maxHeight, scaleType);
    }

    /**
     * remove all mem cache and disk cache
     */
    public void removeAllCache() {
        mRestVolleyImageCache.removeAll();
    }

    /**
     * remove disk cache only
     */
    public void removeDiskCache() {
        mRestVolleyImageCache.removeDiskCache();
    }

    /**
     * start image load and request
     */
    public void start() {
        mRequestEngine.requestQueue.start();
    }

    /**
     * stop image load and request
     */
    public void stop() {
        mRequestEngine.requestQueue.stop();
    }

    /**
     * return disk cache directory
     * @return disk cache directory
     */
    public File getDiskCacheDir() {
        return mRestVolleyImageCache.getDiskCacheDir();
    }

    /**
     * return disk cache path
     * @return disk cache path
     */
    public String getDiskCachePath() {
        return mRestVolleyImageCache.getDiskCachePath();
    }
}