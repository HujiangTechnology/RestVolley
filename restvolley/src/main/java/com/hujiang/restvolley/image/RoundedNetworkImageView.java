package com.hujiang.restvolley.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.AttributeSet;

import com.hujiang.restvolley.R;

/**
 * rounded network ImageView.
 *
 */
public class RoundedNetworkImageView extends NetworkImageViewCompat {
    private boolean mIsCircle = true;
    private int mCornerRadius = 0;

    /**
     * constructor.
     * @param context Context
     */
    public RoundedNetworkImageView(Context context) {
        this(context, null);
    }

    /**
     * constructor.
     * @param context Context
     * @param attrs {@link AttributeSet}
     */
    public RoundedNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * constructor.
     * @param context Context
     * @param attrs {@link AttributeSet}
     * @param defStyle style
     */
    public RoundedNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initCustomAttrs(context, attrs);
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedNetworkImageView);
        final int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            initCustomAttr(typedArray.getIndex(i), typedArray);
        }
        typedArray.recycle();
    }

    private void initCustomAttr(int attr, TypedArray typedArray) {
        if (attr == R.styleable.RoundedNetworkImageView_android_src) {
            setDefaultImageResId(typedArray.getResourceId(attr, 0));
        } else if (attr == R.styleable.RoundedNetworkImageView_rniv_errorImage) {
            setErrorImageResId(typedArray.getResourceId(attr, 0));
        } else if (attr == R.styleable.RoundedNetworkImageView_rniv_isCircle) {
            mIsCircle = typedArray.getBoolean(attr, mIsCircle);
        } else if (attr == R.styleable.RoundedNetworkImageView_rniv_cornerRadius) {
            mCornerRadius = typedArray.getDimensionPixelSize(attr, mCornerRadius);
        }
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        if (resId != 0 && mCornerRadius > 0) {
            setImageDrawable(RoundedNetworkImageView.getRoundedDrawable(getContext(), resId, mCornerRadius));
        } else if (resId != 0 && mIsCircle) {
            setImageDrawable(RoundedNetworkImageView.getCircleDrawable(getContext(), resId));
        } else {
            super.setImageResource(resId);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        if (bitmap != null && mCornerRadius > 0) {
            setImageDrawable(RoundedNetworkImageView.getRoundedDrawable(getContext(), bitmap, mCornerRadius));
        } else if (bitmap != null && mIsCircle) {
            setImageDrawable(RoundedNetworkImageView.getCircleDrawable(getContext(), bitmap));
        } else {
            super.setImageBitmap(bitmap);
        }
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (drawable instanceof BitmapDrawable && mCornerRadius > 0) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null) {
                super.setImageDrawable(RoundedNetworkImageView.getRoundedDrawable(getContext(), bitmap, mCornerRadius));
            } else {
                super.setImageDrawable(drawable);
            }
        } else if (drawable instanceof BitmapDrawable && mIsCircle) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (bitmap != null) {
                super.setImageDrawable(RoundedNetworkImageView.getCircleDrawable(getContext(), bitmap));
            } else {
                super.setImageDrawable(drawable);
            }
        } else {
            super.setImageDrawable(drawable);
        }
    }

    /**
     * get circle drawable.
     * @param context Context
     * @param bitmap Bitmap
     * @return {@link RoundedBitmapDrawable}
     */
    public static RoundedBitmapDrawable getCircleDrawable(Context context, Bitmap bitmap) {
        RoundedBitmapDrawable circleDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        circleDrawable.setAntiAlias(true);
        circleDrawable.setCornerRadius(Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
        return circleDrawable;
    }

    /**
     * get circle drawable.
     * @param context Context
     * @param resId res id
     * @return {@link RoundedBitmapDrawable}
     */
    public static RoundedBitmapDrawable getCircleDrawable(Context context, @DrawableRes int resId) {
        return getCircleDrawable(context, BitmapFactory.decodeResource(context.getResources(), resId));
    }

    /**
     * get rounded drawable.
     * @param context Context
     * @param bitmap Bitmap
     * @param cornerRadius corner radius
     * @return {@link RoundedBitmapDrawable}
     */
    public static RoundedBitmapDrawable getRoundedDrawable(Context context, Bitmap bitmap, float cornerRadius) {
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        roundedBitmapDrawable.setAntiAlias(true);
        roundedBitmapDrawable.setCornerRadius(cornerRadius);
        return roundedBitmapDrawable;
    }

    /**
     * get rounded drawable.
     * @param context Context
     * @param resId res id
     * @param cornerRadius corner radius
     * @return {@link RoundedBitmapDrawable}
     */
    public static RoundedBitmapDrawable getRoundedDrawable(Context context, @DrawableRes int resId, float cornerRadius) {
        return getRoundedDrawable(context, BitmapFactory.decodeResource(context.getResources(), resId), cornerRadius);
    }
}