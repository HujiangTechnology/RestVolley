/*
 * RVImageRequest      2017-01-04
 * Copyright (c) 2017 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.hujiang.restvolley.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.hujiang.restvolley.MD5Utils;
import com.hujiang.restvolley.compat.StreamBasedNetworkResponse;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.hujiang.restvolley.image.ImageProcessor.BYTE_IN_PIX;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2017-01-04
 */
public class RVImageRequest extends Request<Bitmap> {
    /**
     * Socket timeout in milliseconds for image requests
     */
    private static final int IMAGE_TIMEOUT_MS = 10000;

    /**
     * Default number of retries for image requests
     */
    private static final int IMAGE_MAX_RETRIES = 2;

    /**
     * Default backoff multiplier for image requests
     */
    private static final float IMAGE_BACKOFF_MULT = 2f;

    private static final int BUFFER_SIZE = 10240;

    private final Response.Listener<Bitmap> mListener;
    private final Bitmap.Config mDecodeConfig;
    private final int mMaxWidth;
    private final int mMaxHeight;
    private ImageView.ScaleType mScaleType;
    private Context mContext;

    /**
     * Decoding lock so that we don't decode more than one image at a time (to avoid OOM's)
     */
    private static final Object sDecodeLock = new Object();

    /**
     * Creates a new image request, decoding to a maximum specified width and
     * height. If both width and height are zero, the image will be decoded to
     * its natural size. If one of the two is nonzero, that dimension will be
     * clamped and the other one will be set to preserve the image's aspect
     * ratio. If both width and height are nonzero, the image will be decoded to
     * be fit in the rectangle of dimensions width x height while keeping its
     * aspect ratio.
     *
     * @param url           URL of the image
     * @param listener      Listener to receive the decoded bitmap
     * @param maxWidth      Maximum width to decode this bitmap to, or zero for none
     * @param maxHeight     Maximum height to decode this bitmap to, or zero for
     *                      none
     * @param scaleType     The ImageViews ScaleType used to calculate the needed image size.
     * @param decodeConfig  Format to decode the bitmap to
     * @param errorListener Error listener, or null to ignore errors
     */
    public RVImageRequest(Context context, String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight,
                          ImageView.ScaleType scaleType, Bitmap.Config decodeConfig, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(IMAGE_TIMEOUT_MS, IMAGE_MAX_RETRIES, IMAGE_BACKOFF_MULT));
        mListener = listener;
        mDecodeConfig = decodeConfig;
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mScaleType = scaleType;
        mContext = context;
        setShouldCache(false);
    }

    @Override
    public Priority getPriority() {
        return Priority.LOW;
    }

    /**
     * Scales one side of a rectangle to fit aspect ratio.
     *
     * @param maxPrimary      Maximum size of the primary dimension (i.e. width for
     *                        max width), or zero to maintain aspect ratio with secondary
     *                        dimension
     * @param maxSecondary    Maximum size of the secondary dimension, or zero to
     *                        maintain aspect ratio with primary dimension
     * @param actualPrimary   Actual size of the primary dimension
     * @param actualSecondary Actual size of the secondary dimension
     * @param scaleType       The ScaleType used to calculate the needed image size.
     */
    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                           int actualSecondary, ImageView.ScaleType scaleType) {

        // If no dominant value at all, just return the actual.
        if ((maxPrimary == 0) && (maxSecondary == 0)) {
            return actualPrimary;
        }

        // If ScaleType.FIT_XY fill the whole rectangle, ignore ratio.
        if (scaleType == ImageView.ScaleType.FIT_XY) {
            if (maxPrimary == 0) {
                return actualPrimary;
            }
            return maxPrimary;
        }

        // If primary is unspecified, scale primary to match secondary's scaling ratio.
        if (maxPrimary == 0) {
            double ratio = (double) maxSecondary / (double) actualSecondary;
            return (int) (actualPrimary * ratio);
        }

        if (maxSecondary == 0) {
            return maxPrimary;
        }

        double ratio = (double) actualSecondary / (double) actualPrimary;
        int resized = maxPrimary;

        // If ScaleType.CENTER_CROP fill the whole rectangle, preserve aspect ratio.
        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            if ((resized * ratio) < maxSecondary) {
                resized = (int) (maxSecondary / ratio);
            }
            return resized;
        }

        if ((resized * ratio) > maxSecondary) {
            resized = (int) (maxSecondary / ratio);
        }
        return resized;
    }

    @Override
    protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
        // Serialize all decode on a global lock to reduce concurrent heap usage.
        synchronized (sDecodeLock) {
            try {
                return doParse(response);
            } catch (OutOfMemoryError e) {
                VolleyLog.e("Caught OOM for %d byte image, url=%s", response.data.length, getUrl());
                return Response.error(new ParseError(e));
            }
        }
    }

    /**
     * The real guts of parseNetworkResponse. Broken out for readability.
     */
    private Response<Bitmap> doParse(NetworkResponse response) {
        Bitmap bitmap = null;
        if (response instanceof StreamBasedNetworkResponse) {
            InputStream bitmapStream = ((StreamBasedNetworkResponse) response).inputStream;
            if (bitmapStream != null) {
                //parse bitmap stream
                bitmap = doParseStreamSafe(bitmapStream, ((StreamBasedNetworkResponse) response).contentLength, response.headers.get("Content-Type"));
            } else {
                //parse bitmap bytes
                bitmap = doParseBytes(response.data);
            }
        } else {
            //parse bitmap bytes
            bitmap = doParseBytes(response.data);
        }

        if (bitmap == null) {
            return Response.error(new ParseError(response));
        } else {
            return Response.success(bitmap, null);
        }
    }

    private Bitmap doParseBytes(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        Bitmap bitmap;
        if (mMaxWidth == 0 && mMaxHeight == 0) {
            decodeOptions.inPreferredConfig = mDecodeConfig;
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, decodeOptions);
        } else {
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, decodeOptions);
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight, actualWidth, actualHeight, mScaleType);
            int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth, actualHeight, actualWidth, mScaleType);

            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, decodeOptions);
        }

        return bitmap;
    }

    private Bitmap doParseStreamSafe(InputStream bitmapStream, long contentLength, String contentType) {
        if (bitmapStream == null) {
            return null;
        }

        Bitmap bitmap;
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();

        String lowerContentType = TextUtils.isEmpty(contentType) ? "" : contentType.toLowerCase();
        if (!"image/webp".equals(lowerContentType) && contentLength <= RestVolleyImageCache.MAX_BITMAP_CACHE_SIZE) {
            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inPreferredConfig = mDecodeConfig;

            bitmap = BitmapFactory.decodeStream(bitmapStream, null, decodeOptions);

            try {
                bitmapStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        String diskCachePath = RestVolleyImageLoader.instance(mContext).getDiskCachePath();
        String bitmapCachePath = diskCachePath + File.separator + MD5Utils.hashKeyForDisk(getUrl());
        File bitmapCache = new File(bitmapCachePath);
        if (bitmapCache.exists()) {
            bitmapCache.delete();
        }

        BufferedOutputStream bitmapOutputStream = null;
        try {
            bitmapOutputStream = new BufferedOutputStream(new FileOutputStream(bitmapCache));
            byte[] buffer = new byte[BUFFER_SIZE];
            int len;
            while ((len = bitmapStream.read(buffer)) != -1) {
                bitmapOutputStream.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bitmapStream != null) {
                    bitmapStream.close();
                }
                if (bitmapOutputStream != null) {
                    bitmapOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        decodeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bitmapCachePath);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;
        if (mMaxWidth == 0 && mMaxHeight == 0) {
            int sampleSize = 1;
            long desired_size = actualWidth * actualHeight * BYTE_IN_PIX;
            while (desired_size > RestVolleyImageCache.MAX_BITMAP_CACHE_SIZE) {
                desired_size = actualWidth * actualHeight * BYTE_IN_PIX / sampleSize / sampleSize;
                sampleSize *= 2;
            }

            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inSampleSize = sampleSize;
            decodeOptions.inPreferredConfig = mDecodeConfig;

            bitmap = BitmapFactory.decodeFile(bitmapCachePath, decodeOptions);
        } else {
            int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight, actualWidth, actualHeight, mScaleType);
            int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth, actualHeight, actualWidth, mScaleType);

            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
            bitmap = BitmapFactory.decodeFile(bitmapCachePath, decodeOptions);
        }

        return bitmap;
    }

    @Override
    protected void deliverResponse(Bitmap response) {
        mListener.onResponse(response);
    }

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     *
     * @param actualWidth   Actual width of the bitmap
     * @param actualHeight  Actual height of the bitmap
     * @param desiredWidth  Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     */
    // Visible for testing.
    static int findBestSampleSize(
            int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }
}