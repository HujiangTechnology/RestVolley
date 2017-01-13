/*
 * LruBitmapCache      2015-11-23
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley2.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.VolleyLog;
import com.hujiang.restvolley2.MD5Utils;
import com.hujiang.restvolley2.TaskScheduler;
import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * image cache that contains memory cache and disk cache.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-11-23
 */
public class RVImageCache extends ImageLoaderCompat.ImageCache {

    private static final int BYTES_IN_PIX = 4;
    private static final int PAGE_CACHE_COUNT = 3;


    private static final long DEF_DISK_CACHE_SIZE = 128 * 1024 * 1024;
    private static final String DISK_CACHE_DIR = "restvolley_image";

    public static long MEM_CACHE_SIZE = 0;
    public static long DISK_CACHE_SIZE = 0;
    public static long MAX_BITMAP_CACHE_SIZE = 0;

    LruCache<String, Bitmap> mMemCache;
    DiskLruCache mDiskCache;
    File mCacheDir;

    /**
     * constructor with default cache config.
     * @param context {@link Context}
     */
    public RVImageCache(Context context) {
        this(context, getDefaultCacheSize(context, PAGE_CACHE_COUNT), DEF_DISK_CACHE_SIZE, DISK_CACHE_DIR);
    }

    /**
     * constructor with the specified config.
     * @param context {@link Context}
     * @param maxMemCacheSize max memory cache size.
     * @param maxDiskCacheSize max disk cache size.
     * @param diskCacheDir disk cache dir.
     */
    public RVImageCache(Context context, long maxMemCacheSize, long maxDiskCacheSize, String diskCacheDir) {
        MEM_CACHE_SIZE = maxMemCacheSize > 0 ? maxMemCacheSize : getDefaultCacheSize(context, PAGE_CACHE_COUNT);
        MAX_BITMAP_CACHE_SIZE = getMaxBitmapCacheSize(context);
        DISK_CACHE_SIZE = maxDiskCacheSize > 0 ? maxDiskCacheSize : DEF_DISK_CACHE_SIZE;
        String cacheDirStr = TextUtils.isEmpty(diskCacheDir) ? DISK_CACHE_DIR : diskCacheDir;

        //create mem cache
        mMemCache = new LruCache<String, Bitmap>((int) MEM_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return getBitmapSize(value);
            }
        };

        //create disk cache dir
        mCacheDir = getDiskCacheDir(context, cacheDirStr);
        if (!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }

        //create disk cache
        try {
            mDiskCache = DiskLruCache.open(mCacheDir, 1, 1, DISK_CACHE_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DiskLruCache getDiskCache() {
        if (mDiskCache == null) {
            //create disk cache
            try {
                mDiskCache = DiskLruCache.open(mCacheDir, 1, 1, DISK_CACHE_SIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mDiskCache;
    }

    public String getDiskCachePath() {
        if (getDiskCache() != null) {
            return getDiskCache().getDirectory().getAbsolutePath();
        }

        return "";
    }

    public File getDiskCacheDir() {
        if (getDiskCache() != null) {
            return getDiskCache().getDirectory();
        }

        return null;
    }

    @Override
    public boolean isCached(String cacheKey) {
        String key = generateKey(cacheKey);
        boolean isInMem = mMemCache.get(key) != null;
        boolean isInDisk = false;
        if (getDiskCache() != null) {
            try {
                DiskLruCache.Snapshot snapshot = getDiskCache().get(key);
                if (snapshot != null) {
                    isInDisk = snapshot.getInputStream(0) != null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return isInMem && isInDisk;
    }

    @Override
    public Bitmap getBitmap(String cacheKey) {
        String key = generateKey(cacheKey);
        Bitmap bmp = mMemCache.get(key);
        if (bmp == null) {
            bmp = getBitmapFromDiskLruCache(key);
            //从磁盘读出后，放入内存
            if (bmp != null) {
                mMemCache.put(key, bmp);
            }
        }

        return bmp;
    }

    @Override
    public CacheItem getCache(String cacheKey) {
        String key = generateKey(cacheKey);
        LoadFrom lf = LoadFrom.UNKNOWN;
        Bitmap bmp = mMemCache.get(key);
        if (bmp == null) {
            bmp = getBitmapFromDiskLruCache(key);
            //从磁盘读出后，放入内存
            if (bmp != null) {
                lf = LoadFrom.DISC_CACHE;
                mMemCache.put(key, bmp);
            }
        } else {
            lf = LoadFrom.MEMORY_CACHE;
        }

        return new CacheItem(cacheKey, bmp, lf);
    }

    @Override
    public void putBitmap(String cacheKey, Bitmap bitmap) {
        String key = generateKey(cacheKey);
        mMemCache.put(cacheKey, bitmap);
        putBitmapToDiskLruCache(key, bitmap);
    }

    @Override
    public boolean remove(String url) {
        String key = generateKey(url);
        mMemCache.remove(key);
        if (getDiskCache() != null) {
            try {
                return getDiskCache().remove(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * get the disk cache path with the specified cache key
     * @param cacheKey cache key generated by {@link ImageLoaderCompat#generateCacheKey(String, int, int, ImageView.ScaleType)}
     * @return disk cache path
     */
    public String getDiskCachePath(String cacheKey) {
        return new StringBuilder(getDiskCachePath())
                .append(File.separator)
                .append(generateKey(cacheKey))
                .append(".0")
                .toString();
    }

    /**
     * remove all mem cache and disk cache
     * @return remove successful or not
     */
    public boolean removeAll() {
        boolean isRemoved = true;

        mMemCache.evictAll();
        try {
            if (getDiskCache() != null) {
                getDiskCache().delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            isRemoved = false;
        }

        return isRemoved;
    }

    /**
     * just remove disk cache only
     * @return remove successful or not
     */
    public boolean removeDiskCache() {
        boolean isRemoved = true;
        try {
            if (getDiskCache() != null) {
                getDiskCache().delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            isRemoved = false;
        }

        return isRemoved;
    }

    /**
     * get default memory cache size.
     *
     * @param context Context
     * @return cacheSize
     */
    public static int getDefaultCacheSize(Context context, int pageCacheCount) {
        pageCacheCount = pageCacheCount <= 0 ? PAGE_CACHE_COUNT : pageCacheCount;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWith = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        int screenBytes = screenWith * screenHeight * BYTES_IN_PIX;
        return screenBytes * pageCacheCount;
    }

    public static int getMaxBitmapCacheSize(Context context) {
        return getDefaultCacheSize(context, 2);
    }

    /**
     * get bitmap size.
     *
     * @param bitmap Bitmap
     * @return bitmapSize
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            //API 12
            return bitmap.getByteCount();
        }
        //earlier version
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    private void putBitmapToDiskLruCache(final String key, final Bitmap bitmap) {
        TaskScheduler.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if (getDiskCache() != null) {
                        DiskLruCache.Editor editor = getDiskCache().edit(key);
                        if (editor != null) {
                            OutputStream outputStream = editor.newOutputStream(0);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
                            editor.commit();

                            outputStream.close();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Bitmap getBitmapFromDiskLruCache(String key) {
        if (getDiskCache() == null) {
            return null;
        }

        String cachePath = new StringBuilder(getDiskCachePath()).append(File.separator).append(key).append(".0").toString();
        File cacheFile = new File(cachePath);
        try {
            if (cacheFile.exists()) {
                return ImageProcessor.decode(cachePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            VolleyLog.e("Caught OOM for %d byte image, uri=%s", cacheFile.length(), cachePath);
        }

        return null;
    }

    private String generateKey(String target) {
        return MD5Utils.hashKeyForDisk(target);
    }

    /**
     * get the disk cache dir with the unique name.
     * @param context {@link Context}
     * @param uniqueName unique dir name
     * @return dir file
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);

    }

}