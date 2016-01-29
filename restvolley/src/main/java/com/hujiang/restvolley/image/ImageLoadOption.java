/*
 * ImageDisplayOption      2016-01-05
 * Copyright (c) 2016 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.image;

import android.view.animation.Animation;
import android.widget.ImageView;

/**
 * config of every image requesting and displaying.<br>
 * config default image, error image, max with, max height, cache enable, scale type, load animation.
 *
 * @author simon
 * @version 1.0.0
 * @since 2016-01-05
 */
public class ImageLoadOption {

    int defaultImgResId = 0;
    int errorImgResId = 0;
    int maxWidth = 0;
    int maxHeight = 0;
    boolean isCacheEnable = true;
    ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER_INSIDE;
    Animation imgLoadAnimation;

    private ImageLoadOption() {

    }

    /**
     * create instance of {@link ImageLoadOption}.
     * @return {@link ImageLoadOption}
     */
    public static ImageLoadOption create() {
        return new ImageLoadOption();
    }

    /**
     * set default image for image request.
     * @param defaultImgResId default image res id
     * @return {@link ImageLoadOption}
     */
    public ImageLoadOption defaultImgResId(int defaultImgResId) {
        this.defaultImgResId = defaultImgResId;

        return this;
    }

    /**
     * set error image for image request.
     * @param errorImgResId error image res id
     * @return {@link ImageLoadOption}
     */
    public ImageLoadOption errorImgResId(int errorImgResId) {
        this.errorImgResId = errorImgResId;

        return this;
    }

    /**
     * set image load animation.
     * @param imgLoadAnimation {@link Animation}
     * @return {@link ImageLoadOption}
     */
    public ImageLoadOption imgLoadAnimation(Animation imgLoadAnimation) {
        this.imgLoadAnimation = imgLoadAnimation;

        return this;
    }

    /**
     * set scale type.
     * @param scaleType {@link android.widget.ImageView.ScaleType}
     * @return {@link ImageLoadOption}
     */
    public ImageLoadOption scaleType(ImageView.ScaleType scaleType) {
        this.scaleType = scaleType;

        return this;
    }

    /**
     * set image max width.
     * @param maxWidth max width
     * @return {@link ImageLoadOption}
     */
    public ImageLoadOption maxWidth(int maxWidth) {
        this.maxWidth = maxWidth;

        return this;
    }

    /**
     * set image max height.
     * @param maxHeight max height
     * @return {@link ImageLoadOption}
     */
    public ImageLoadOption maxHeight(int maxHeight) {
        this.maxHeight = maxHeight;

        return this;
    }

    /**
     * set image cache enable.<br>
     * if you want to requesting image witout cache, set it with false.
     * @param isCacheEnable cache enable or not.
     * @return {@link ImageLoadOption}
     */
    public ImageLoadOption cacheEnable(boolean isCacheEnable) {
        this.isCacheEnable = isCacheEnable;

        return this;
    }

}