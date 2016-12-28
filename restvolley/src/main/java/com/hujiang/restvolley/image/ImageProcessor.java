/*
 * ImageProcessor      2016-01-07
 * Copyright (c) 2016 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * parse and resize the bitmap as needed.
 *
 * @author simon
 * @version 1.0.0
 * @since 2016-01-07
 */
public class ImageProcessor {

    public static final int BYTE_IN_PIX = 4;
    public static long MAX_BMP_SIZE = 0;

    /**
     * Scales one side of a rectangle to fit aspect ratio.
     *
     * @param maxPrimary Maximum size of the primary dimension (i.e. width for
     *        max width), or zero to maintain aspect ratio with secondary
     *        dimension
     * @param maxSecondary Maximum size of the secondary dimension, or zero to
     *        maintain aspect ratio with primary dimension
     * @param actualPrimary Actual size of the primary dimension
     * @param actualSecondary Actual size of the secondary dimension
     * @param scaleType The ScaleType used to calculate the needed image size.
     */
    private static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary, int actualSecondary, ImageView.ScaleType scaleType) {

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

    /**
     * decode uri to bitmap
     * @param context Context
     * @param uri uri
     * @param maxWidth width
     * @param maxHeight height
     * @param scaleType scale type
     * @param config Bitmap.Config
     * @return
     */
    public static Bitmap decode(Context context, String uri, int maxWidth, int maxHeight, ImageView.ScaleType scaleType, Bitmap.Config config) {
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inJustDecodeBounds = true;

        InputStream stream = decode2Stream(context, uri);
        if (stream == null) {
            return null;
        }
        BitmapFactory.decodeStream(stream, null, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stream = decode2Stream(context, uri);
        Bitmap bitmap;
        if (maxWidth == 0 && maxHeight == 0) {
            int sampleSize = 1;
            long desired_size = actualWidth * actualHeight * BYTE_IN_PIX;
            while (desired_size > MAX_BMP_SIZE) {
                desired_size = actualWidth * actualHeight * BYTE_IN_PIX / sampleSize / sampleSize;
                sampleSize *= 2;
            }

            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inSampleSize = sampleSize;
            decodeOptions.inPreferredConfig = config;

            bitmap = BitmapFactory.decodeStream(stream, null, decodeOptions);
        } else {
            // Then compute the dimensions we would ideally like to decode to.
            int desiredWidth = getResizedDimension(maxWidth, maxHeight, actualWidth, actualHeight, scaleType);
            int desiredHeight = getResizedDimension(maxHeight, maxWidth, actualHeight, actualWidth, scaleType);

            // Decode to the nearest power of two scaling factor.
            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inPreferredConfig = config;
            decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
            Bitmap tempBitmap = BitmapFactory.decodeStream(stream, null, decodeOptions);

            // If necessary, scale down to the maximal acceptable size.
            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
        }

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private static InputStream decode2Stream(Context context, String uri) {
        BufferedInputStream imageStream = null;
        String filePath;
        try {
            switch (ImageLoaderCompat.Scheme.ofUri(uri)) {
                case FILE:
                    filePath = ImageLoaderCompat.Scheme.FILE.crop(uri);
                    try {
                        imageStream = new BufferedInputStream(new FileInputStream(filePath));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    break;
                case CONTENT:
                    try {
                        imageStream = new BufferedInputStream(context.getContentResolver().openInputStream(Uri.parse(uri)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case ASSETS:
                    filePath = ImageLoaderCompat.Scheme.ASSETS.crop(uri);
                    try {
                        imageStream = new BufferedInputStream(context.getAssets().open(filePath));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case DRAWABLE:
                    String drawableIdString = ImageLoaderCompat.Scheme.DRAWABLE.crop(uri);
                    int drawableId = Integer.parseInt(drawableIdString);
                    imageStream = new BufferedInputStream(context.getResources().openRawResource(drawableId));
                    break;
                case UNKNOWN:
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageStream;
    }

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap that will not result in the scaling past the desired dimensions.
     *
     * @param actualWidth Actual width of the bitmap
     * @param actualHeight Actual height of the bitmap
     * @param desiredWidth Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     * @return best sample size.
     */
    public static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        if (desiredHeight == 0 || desiredWidth == 0) {
            return 1;
        }

        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }

    /**
     * decode local bitmap file
     * @param filePath local bitmap file path
     * @return Bitmap
     */
    public static Bitmap decode(String filePath) {
        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(filePath, decodeOptions);
        int actualWidth = decodeOptions.outWidth;
        int actualHeight = decodeOptions.outHeight;

        int sampleSize = 1;
        long desired_size = actualWidth * actualHeight * ImageProcessor.BYTE_IN_PIX;
        while (desired_size > ImageProcessor.MAX_BMP_SIZE) {
            desired_size = actualWidth * actualHeight * ImageProcessor.BYTE_IN_PIX / sampleSize / sampleSize;
            sampleSize *= 2;
        }

        decodeOptions.inJustDecodeBounds = false;
        decodeOptions.inSampleSize = sampleSize;

        return BitmapFactory.decodeFile(filePath, decodeOptions);
    }
}