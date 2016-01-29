/*
 * ImageLoaderConfig      2016-01-05
 * Copyright (c) 2016 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.image;

import android.content.Context;

import com.hujiang.restvolley.RequestEngine;

/**
 * config that effects on all the image loading.
 *
 * @author simon
 * @version 1.0.0
 * @since 2016-01-05
 */
public class ImageLoaderGlobalConfig {

    long memCacheSize;
    long diskCacheSize;
    String diskCacheDir;
    RequestEngine requestEngine;

    private ImageLoaderGlobalConfig() {

    }

    /**
     * create instance of {@link ImageLoaderGlobalConfig}.
     * @return {@link ImageLoaderGlobalConfig}
     */
    public static ImageLoaderGlobalConfig create() {
        return new ImageLoaderGlobalConfig();
    }

    /**
     * set memory cache size for {@link RestVolleyImageLoader}.
     * @param memCacheSize memory cache size
     * @return {@link ImageLoaderGlobalConfig}
     */
    public ImageLoaderGlobalConfig memCacheSize(long memCacheSize) {
        this.memCacheSize = memCacheSize;

        return this;
    }

    /**
     * set disk cache size for {@link RestVolleyImageLoader}.
     * @param diskCacheSize disk cache size
     * @return {@link ImageLoaderGlobalConfig}
     */
    public ImageLoaderGlobalConfig diskCacheSize(long diskCacheSize) {
        this.diskCacheSize = diskCacheSize;

        return this;
    }

    /**
     * set disk cache dir for {@link RestVolleyImageLoader}.
     * @param diskCacheDir disk cache dir
     * @return {@link ImageLoaderGlobalConfig}
     */
    public ImageLoaderGlobalConfig diskCacheDir(String diskCacheDir) {
        this.diskCacheDir = diskCacheDir;

        return this;
    }

    /**
     * set image {@link RequestEngine}.<br>
     * you can create instance of new {@link RequestEngine} with {@link com.hujiang.restvolley.RestVolley#newRequestEngine(Context, String)}<br>
     * default is {@link com.hujiang.restvolley.RestVolley#newRequestEngine(Context, String)}
     * with tag {@link com.hujiang.restvolley.RestVolley#TAG_REQUEST_ENGINE_DEFAULT}.
     * @param requestEngine {@link RequestEngine}
     * @return {@link ImageLoaderGlobalConfig}
     */
    public ImageLoaderGlobalConfig requestEngine(RequestEngine requestEngine) {
        this.requestEngine = requestEngine;

        return this;
    }

}