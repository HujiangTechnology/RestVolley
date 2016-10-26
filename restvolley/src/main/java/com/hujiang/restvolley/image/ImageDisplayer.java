/*
 * ImageDisplayer      2016-10-25
 * Copyright (c) 2016 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.hujiang.restvolley.image;

import android.graphics.Bitmap;
import android.view.View;

/**
 * image display interface
 *
 * @author simon
 * @version 1.0.0
 * @since 2016-10-25
 */
public interface ImageDisplayer {
    /**
     * bind the bitmap to the specified view with your style
     * @param bitmap bitmap to bind to the specified view
     * @param view view
     * @param loadFrom where is the bitmap from, memCache, diskcache, or network.
     */
    public void display(Bitmap bitmap, View view, LoadFrom loadFrom);
}