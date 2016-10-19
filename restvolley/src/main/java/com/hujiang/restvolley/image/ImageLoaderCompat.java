/*
 * ImageLoaderCompat      2016-01-04
 * Copyright (c) 2016 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.RequestFuture;
import com.hujiang.restvolley.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * extends the ability of the {@link ImageLoader}.
 * <p></p>
 * support http image request, local bitmap request that on the sdcard, assets, res/drawable, res/raw, using the system ContentProvider.<br>
 * http image request, uri: http://www.google.com/xxx/xxx.png<br>
 * sdcard image request, uri: file:///mnt/sdcard/xxx.png<br>
 * assets image request, uri: assets://assets_default.png<br>
 * res image request, uri: “drawable://” + R.drawable.drawable_default, or "drawable://" + R.raw.defaut<br>
 * ContentProvider image request, uri: content://media/external/images/media/27916<br>
 *
 * @author simon
 * @version 1.0.0
 * @since 2016-01-04
 */
public class ImageLoaderCompat {

    private static final int STREAM_BUFFER_SIZE = 4096;
    private static final int BATCH_RESPONSE_DELAY = 100;
    /** RequestQueue for dispatching ImageRequests onto. */
    private final RequestQueue mRequestQueue;

    /** Amount of time to wait after first response arrives before delivering all responses. */
    private int mBatchResponseDelayMs = BATCH_RESPONSE_DELAY;

    /** The cache implementation to be used as an L1 cache before calling into volley. */
    private final ImageCache mCache;

    /**
     * HashMap of Cache keys -> BatchedImageRequest used to track in-flight requests so
     * that we can coalesce multiple requests to the same uri into a single network request.
     */
    private final ConcurrentHashMap<String, BatchedImageRequest> mInFlightRequests = new ConcurrentHashMap<String, BatchedImageRequest>();

    /** HashMap of the currently pending responses (waiting to be delivered). */
    private final ConcurrentHashMap<String, BatchedImageRequest> mBatchedResponses = new ConcurrentHashMap<String, BatchedImageRequest>();

    /** Handler to the main thread. */
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    /** Runnable for in-flight response delivery. */
    private Runnable mRunnable;

    private Context mContext;

    /**
     * Simple cache adapter interface. If provided to the ImageLoader, it
     * will be used as an L1 cache before dispatch to Volley. Implementations
     * must not block. Implementation with an LruCache is recommended.
     */
    public interface ImageCache {
        /**
         * has image been cached withe the specified key.
         * @param cacheKey cache key
         * @return is cached or not
         */
        public boolean isCached(String cacheKey);

        /**
         * remove the bitmap cache with the specified key.
         * @param cacheKey cache key
         * @return cache removed or not
         */
        public boolean remove(String cacheKey);

        /**
         * get the bitmap from the cache with the cache key.
         * @param cacheKey cache key
         * @return {@link Bitmap}
         */
        public Bitmap getBitmap(String cacheKey);

        /**
         * put the bitmap input cache with the specified key.
         * @param cacheKey cache key
         * @param bitmap {@link Bitmap}
         */
        public void putBitmap(String cacheKey, Bitmap bitmap);
    }

    /**
     * Constructs a new ImageLoader.
     * @param context {@link Context}
     * @param queue The RequestQueue to use for making image requests.
     * @param imageCache The cache to use as an L1 cache.
     */
    public ImageLoaderCompat(Context context, RequestQueue queue, ImageCache imageCache) {
        mRequestQueue = queue;
        mCache = imageCache;
        mContext = context;
    }

    /**
     * The default implementation of ImageListener which handles basic functionality
     * of showing a default image until the network response is received, at which point
     * it will switch to either the actual image or the error image.
     * @param uri image uri
     * @param view The imageView that the listener is associated with.
     * @param imageLoadOption image display option.
     * @return {@link ImageListener}
     */
    public static ImageListener getImageListener(final String uri, final ImageView view, final ImageLoadOption imageLoadOption) {
        view.setTag(R.id.restvolley_image_imageview_tag, uri);
        return new ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (imageLoadOption != null) {
                    view.setImageResource(imageLoadOption.errorImgResId);
                }
            }

            @Override
            public void onResponse(ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    if (!TextUtils.isEmpty(uri)
                            && uri.equals(view.getTag(R.id.restvolley_image_imageview_tag))) {
                        view.setImageBitmap(response.getBitmap());

                        if (imageLoadOption != null && imageLoadOption.imgLoadAnimation != null) {
                            view.startAnimation(imageLoadOption.imgLoadAnimation);
                        }
                    } else {
                        view.setImageResource(imageLoadOption != null ? imageLoadOption.defaultImgResId : 0);
                    }
                } else if (imageLoadOption != null) {
                    view.setImageResource(imageLoadOption.defaultImgResId);
                }
            }
        };
    }

    /**
     * Interface for the response handlers on image requests.
     *<p>
     * The call flow is this:
     * 1. Upon being  attached to a request, onResponse(response, true) will
     * be invoked to reflect any cached data that was already available. If the
     * data was available, response.getBitmap() will be non-null.
     *
     * 2. After a network response returns, only one of the following cases will happen:
     *   - onResponse(response, false) will be called if the image was loaded.
     *   or
     *   - onErrorResponse will be called if there was an error loading the image.
     * </p>
     */
    public interface ImageListener extends Response.ErrorListener {
        /**
         * Listens for non-error changes to the loading of the image request.
         *
         * @param response Holds all information pertaining to the request, as well as the bitmap (if it is loaded).
         * @param isImmediate True if this was called during ImageLoader.get() variants.
         *                    This can be used to differentiate between a cached image loading and a network
         *                    image loading in order to, for example, run an animation to fade in network loaded images.
         */
        public void onResponse(ImageContainer response, boolean isImmediate);
    }

    /**
     * Checks if the item is available in the cache.
     * @param requestUri The uri of the remote image
     * @param maxWidth The maximum width of the returned image.
     * @param maxHeight The maximum height of the returned image.
     * @return True if the item exists in cache, false otherwise.
     */
    public boolean isCached(String requestUri, int maxWidth, int maxHeight) {
        return isCached(requestUri, maxWidth, maxHeight, ImageView.ScaleType.CENTER_INSIDE);
    }

    /**
     * Checks if the item is available in the cache.
     *
     * @param requestUri The uri of the remote image
     * @param maxWidth   The maximum width of the returned image.
     * @param maxHeight  The maximum height of the returned image.
     * @param scaleType  The scaleType of the imageView.
     * @return True if the item exists in cache, false otherwise.
     */
    public boolean isCached(String requestUri, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
        String cacheKey = generateCacheKey(requestUri, maxWidth, maxHeight, scaleType);
        return mCache.isCached(cacheKey);
    }

    /**
     * remove bitmap cache.
     * @param requestUri bitmap uri
     * @param maxWidth max width
     * @param maxHeight max height
     */
    public void removeCache(String requestUri, int maxWidth, int maxHeight) {
        removeCache(requestUri, maxWidth, maxHeight, ImageView.ScaleType.CENTER_INSIDE);
    }

    /**
     * remove bitmap cache.
     * @param requestUri bitmap uri
     * @param maxWidth max width
     * @param maxHeight max height
     * @param scaleType {@link android.widget.ImageView.ScaleType}
     */
    public void removeCache(String requestUri, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
        String cacheKey = generateCacheKey(requestUri, maxWidth, maxHeight, scaleType);
        mCache.remove(cacheKey);
    }

    /**
     * Returns an ImageContainer for the requested uri.
     * <br>
     * The ImageContainer will contain either the specified default bitmap or the loaded bitmap.
     * If the default was returned, the {@link ImageLoader} will be invoked when the
     * request is fulfilled.
     *
     * @param requestUri The uri of the image to be loaded.
     * @param listener {@link ImageListener}
     * @return {@link ImageContainer}
     */
    public ImageContainer load(String requestUri, final ImageListener listener) {
        return load(requestUri, listener, null);
    }

    /**
     * Issues a bitmap request with the given uri if that image is not available
     * in the cache, and returns a bitmap container that contains all of the data
     * relating to the request (as well as the default image if the requested
     * image is not available).
     * @param requestUri The uri of the remote image
     * @param imageListener The listener to call when the remote image is loaded
     * @param imageLoadOption image load option.
     * @return A container object that contains all of the properties of the request, as well as
     *     the currently available image (default if remote is not loaded).
     */
    public ImageContainer load(String requestUri, ImageListener imageListener, ImageLoadOption imageLoadOption) {

        int maxWidth = getMaxWidth(imageLoadOption);
        int maxHeight = getMaxHeight(imageLoadOption);
        ImageView.ScaleType scaleType = getScaleType(imageLoadOption);
        boolean isCacheEnable = isCacheEnable(imageLoadOption);

        final String cacheKey = generateCacheKey(requestUri, maxWidth, maxHeight, scaleType);

        if (isCacheEnable) {
            // Try to look up the request in the cache of remote images.
            Bitmap cachedBitmap = mCache.getBitmap(cacheKey);
            if (cachedBitmap != null) {
                // Return the cached bitmap.
                ImageContainer container = new ImageContainer(cachedBitmap, requestUri, null, null);
                responseOnUiThread(container, true, imageListener);
                return container;
            }
        }

        // The bitmap did not exist in the cache, fetch it!
        ImageContainer imageContainer = new ImageContainer(null, requestUri, cacheKey, imageListener);

        // Update the caller to let them know that they should use the default bitmap.
        responseOnUiThread(imageContainer, true, imageListener);

        if (isLocalRequest(requestUri)) {
            //load bitmap from local
            Bitmap bitmap = loadFromLocal(requestUri, cacheKey, maxWidth, maxHeight, scaleType);
            if (bitmap == null) {
                responseErrorOnUiThread(imageContainer, new VolleyError("bitmap is null"), imageListener);
            } else {
                if (isCacheEnable) {
                    mCache.putBitmap(cacheKey, bitmap);
                }

                imageContainer = new ImageContainer(bitmap, requestUri, cacheKey, imageListener);
                responseOnUiThread(imageContainer, true, imageListener);
            }
        } else {
            //load bitmap from network
            // Check to see if a request is already in-flight.
            BatchedImageRequest request = mInFlightRequests.get(cacheKey);
            if (request != null) {
                // If it is, add this request to the list of listeners.
                request.addContainer(imageContainer);
                return imageContainer;
            }

            // The request is not already in flight. Send the new request to the network and
            // track it.
            Request<Bitmap> newRequest = makeImageRequest(requestUri, imageLoadOption, cacheKey);

            mRequestQueue.add(newRequest);
            mInFlightRequests.put(cacheKey, new BatchedImageRequest(newRequest, imageContainer));
        }

        return imageContainer;
    }

    private byte[] inputStream2Bytes(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[STREAM_BUFFER_SIZE];
        int count;
        try {
            while ((count = inputStream.read(data, 0, STREAM_BUFFER_SIZE)) != -1) {
                byteArrayOutputStream.write(data, 0, count);
            }

            data = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return data;
    }

    /**
     * load image from local.
     * @param uri local image uri
     * @param cacheKey cache key
     * @param width with
     * @param height height
     * @param scaleType {@link android.widget.ImageView.ScaleType}
     */
    private Bitmap loadFromLocal(String uri, String cacheKey, int width, int height, ImageView.ScaleType scaleType) {

        BufferedInputStream imageStream = null;
        String filePath = null;
        switch (Scheme.ofUri(uri)) {
            case FILE:
                filePath = Scheme.FILE.crop(uri);
                try {
                    imageStream = new BufferedInputStream(new FileInputStream(filePath));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                break;
            case CONTENT:
                try {
                    imageStream = new BufferedInputStream(mContext.getContentResolver().openInputStream(Uri.parse(uri)));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case ASSETS:
                filePath = Scheme.ASSETS.crop(uri);
                try {
                    imageStream = new BufferedInputStream(mContext.getAssets().open(filePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case DRAWABLE:
                String drawableIdString = Scheme.DRAWABLE.crop(uri);
                int drawableId = Integer.parseInt(drawableIdString);
                imageStream = new BufferedInputStream(mContext.getResources().openRawResource(drawableId));
                break;
            case UNKNOWN:
                default:
                break;
        }
        if (imageStream != null) {
            Bitmap bitmap = ImageProcessor.doParse(inputStream2Bytes(imageStream), width, height, scaleType, Bitmap.Config.RGB_565);

            try {
                imageStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        return null;
    }

    private boolean isLocalRequest(String uri) {
        Scheme scheme = Scheme.ofUri(uri);

        return Scheme.ASSETS == scheme
                || Scheme.CONTENT == scheme
                || Scheme.DRAWABLE == scheme
                || Scheme.FILE == scheme;
    }

    /**
     * sync load the bitmap with uri.
     * @param requestUri uri
     * @return {@link Bitmap}
     */
    public Bitmap syncLoad(String requestUri) {
        return syncLoad(requestUri, null);
    }

    /**
     * sync load the bitmap.
     * @param requestUri uri
     * @param imageLoadOption ImageLoadOption
     * @return {@link Bitmap}
     */
    public Bitmap syncLoad(String requestUri, ImageLoadOption imageLoadOption) {

        int maxWidth = getMaxWidth(imageLoadOption);
        int maxHeight = getMaxHeight(imageLoadOption);
        ImageView.ScaleType scaleType = getScaleType(imageLoadOption);
        boolean isCacheEnable = isCacheEnable(imageLoadOption);

        final String cacheKey = generateCacheKey(requestUri, maxWidth, maxHeight, scaleType);

        if (isCacheEnable) {
            // Try to look up the request in the cache of remote images.
            Bitmap cachedBitmap = mCache.getBitmap(cacheKey);
            if (cachedBitmap != null) {
                return cachedBitmap;
            }
        }

        Bitmap bitmap = null;

        if (isLocalRequest(requestUri)) {
            //load bitmap from local
            bitmap = loadFromLocal(requestUri, cacheKey, maxWidth, maxHeight, scaleType);
        } else {
            //load bitmap from network
            RequestFuture<Bitmap> future = RequestFuture.newFuture();
            Request<Bitmap> bitmapRequest = new ImageRequest(requestUri, future, maxWidth, maxHeight, scaleType, Bitmap.Config.RGB_565, future);
            future.setRequest(bitmapRequest);
            mRequestQueue.add(bitmapRequest);

            try {
                bitmap = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        if (bitmap != null && isCacheEnable) {
            mCache.putBitmap(cacheKey, bitmap);
        }

        return bitmap;
    }

    protected Request<Bitmap> makeImageRequest(String requestUri, final ImageLoadOption imageLoadOption, final String cacheKey) {
        return new ImageRequest(requestUri, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                onGetImageSuccess(cacheKey, response, isCacheEnable(imageLoadOption));
            }
        }, getMaxWidth(imageLoadOption), getMaxHeight(imageLoadOption), getScaleType(imageLoadOption), Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onGetImageError(cacheKey, error);
            }
        });
    }

    private void responseOnUiThread(final ImageContainer container, final boolean isImmediate, final ImageListener listener) {
        if (listener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onResponse(container, isImmediate);
                }
            });
        }
    }

    private void responseErrorOnUiThread(final ImageContainer container, final VolleyError volleyError, final ImageListener listener) {
        if (listener != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onErrorResponse(volleyError);
                }
            });
        }
    }

    private int getMaxWidth(ImageLoadOption imageLoadOption) {
        return imageLoadOption == null ? 0 : imageLoadOption.maxWidth;
    }

    private int getMaxHeight(ImageLoadOption imageLoadOption) {
        return imageLoadOption == null ? 0 : imageLoadOption.maxHeight;
    }

    private ImageView.ScaleType getScaleType(ImageLoadOption imageLoadOption) {
        return imageLoadOption == null ? ImageView.ScaleType.CENTER_INSIDE : imageLoadOption.scaleType;
    }

    private boolean isCacheEnable(ImageLoadOption imageLoadOption) {
        return imageLoadOption == null ? true : imageLoadOption.isCacheEnable;
    }

    /**
     * Sets the amount of time to wait after the first response arrives before delivering all
     * responses. Batching can be disabled entirely by passing in 0.
     * @param newBatchedResponseDelayMs The time in milliseconds to wait.
     */
    public void setBatchedResponseDelay(int newBatchedResponseDelayMs) {
        if (newBatchedResponseDelayMs >= 0) {
            mBatchResponseDelayMs = newBatchedResponseDelayMs;
        }
    }

    /**
     * Handler for when an image was successfully loaded.
     * @param cacheKey The cache key that is associated with the image request.
     * @param response The bitmap that was returned from the network.
     */
    protected void onGetImageSuccess(final String cacheKey, final Bitmap response, boolean isCacheEnable) {
        if (isCacheEnable) {
            // cache the image that was fetched.
            mCache.putBitmap(cacheKey, response);
        }

        // remove the request from the list of in-flight requests.
        BatchedImageRequest request = mInFlightRequests.remove(cacheKey);

        if (request != null) {
            // Update the response bitmap.
            request.mResponseBitmap = response;

            // Send the batched response
            batchResponse(cacheKey, request);
        }
    }

    /**
     * Handler for when an image failed to load.
     * @param cacheKey The cache key that is associated with the image request.
     */
    protected void onGetImageError(String cacheKey, VolleyError error) {
        // Notify the requesters that something failed via a null result.
        // Remove this request from the list of in-flight requests.
        BatchedImageRequest request = mInFlightRequests.remove(cacheKey);

        if (request != null) {
            // Set the error for this request
            request.setError(error);

            // Send the batched response
            batchResponse(cacheKey, request);
        }
    }

    /**
     * Container object for all of the data surrounding an image request.
     */
    public class ImageContainer {
        /**
         * The most relevant bitmap for the container. If the image was in cache, the
         * Holder to use for the final bitmap (the one that pairs to the requested uri).
         */
        private Bitmap mBitmap;

        private final ImageListener mListener;

        /** The cache key that was associated with the request. */
        private final String mCacheKey;

        /** The request uri that was specified. */
        private final String mrequestUri;

        /**
         * Constructs a BitmapContainer object.
         * @param bitmap The final bitmap (if it exists).
         * @param requestUri The requested uri for this container.
         * @param cacheKey The cache key that identifies the requested uri for this container.
         * @param listener {@link ImageListener}
         */
        public ImageContainer(Bitmap bitmap, String requestUri, String cacheKey, ImageListener listener) {
            mBitmap = bitmap;
            mrequestUri = requestUri;
            mCacheKey = cacheKey;
            mListener = listener;
        }

        /**
         * Releases interest in the in-flight request (and cancels it if no one else is listening).
         */
        public void cancelRequest() {
            if (mListener == null) {
                return;
            }

            BatchedImageRequest request = mInFlightRequests.get(mCacheKey);
            if (request != null) {
                boolean canceled = request.removeContainerAndCancelIfNecessary(this);
                if (canceled) {
                    mInFlightRequests.remove(mCacheKey);
                }
            } else {
                // check to see if it is already batched for delivery.
                request = mBatchedResponses.get(mCacheKey);
                if (request != null) {
                    request.removeContainerAndCancelIfNecessary(this);
                    if (request.mContainers.size() == 0) {
                        mBatchedResponses.remove(mCacheKey);
                    }
                }
            }
        }

        /**
         * Returns the bitmap associated with the request uri if it has been loaded, null otherwise.
         * @return {@link Bitmap}
         */
        public Bitmap getBitmap() {
            return mBitmap;
        }

        /**
         * Returns the requested uri for this container.
         * @return uri
         */
        public String getRequestUri() {
            return mrequestUri;
        }
    }

    /**
     * Wrapper class used to map a Request to the set of active ImageContainer objects that are
     * interested in its results.
     */
    private class BatchedImageRequest {
        /** The request being tracked. */
        private final Request<?> mRequest;

        /** The result of the request being tracked by this item. */
        private Bitmap mResponseBitmap;

        /** Error if one occurred for this response. */
        private VolleyError mError;

        /** List of all of the active ImageContainers that are interested in the request. */
        private final LinkedList<ImageContainer> mContainers = new LinkedList<ImageContainer>();

        /**
         * Constructs a new BatchedImageRequest object.
         * @param request The request being tracked
         * @param container The ImageContainer of the person who initiated the request.
         */
        public BatchedImageRequest(Request<?> request, ImageContainer container) {
            mRequest = request;
            mContainers.add(container);
        }

        /**
         * Set the error for this response.
         */
        public void setError(VolleyError error) {
            mError = error;
        }

        /**
         * Get the error for this response.
         */
        public VolleyError getError() {
            return mError;
        }

        /**
         * Adds another ImageContainer to the list of those interested in the results of
         * the request.
         */
        public void addContainer(ImageContainer container) {
            mContainers.add(container);
        }

        /**
         * Detatches the bitmap container from the request and cancels the request if no one is
         * left listening.
         * @param container The container to remove from the list
         * @return True if the request was canceled, false otherwise.
         */
        public boolean removeContainerAndCancelIfNecessary(ImageContainer container) {
            mContainers.remove(container);
            if (mContainers.size() == 0) {
                mRequest.cancel();
                return true;
            }
            return false;
        }
    }

    /**
     * Starts the runnable for batched delivery of responses if it is not already started.
     * @param cacheKey The cacheKey of the response being delivered.
     * @param request The BatchedImageRequest to be delivered.
     */
    private void batchResponse(String cacheKey, BatchedImageRequest request) {
        mBatchedResponses.put(cacheKey, request);
        // If we don't already have a batch delivery runnable in flight, make a new one.
        // Note that this will be used to deliver responses to all callers in mBatchedResponses.
        if (mRunnable == null) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    for (BatchedImageRequest bir : mBatchedResponses.values()) {
                        for (ImageContainer container : bir.mContainers) {
                            // If one of the callers in the batched request canceled the request
                            // after the response was received but before it was delivered,
                            // skip them.
                            if (container.mListener == null) {
                                continue;
                            }
                            if (bir.getError() == null) {
                                container.mBitmap = bir.mResponseBitmap;
                                container.mListener.onResponse(container, false);
                            } else {
                                container.mListener.onErrorResponse(bir.getError());
                            }
                        }
                    }
                    mBatchedResponses.clear();
                    mRunnable = null;
                }

            };
            // Post the runnable.
            mHandler.postDelayed(mRunnable, mBatchResponseDelayMs);
        }
    }

    /**
     * Creates a cache key for use with the L1 cache.
     * @param uri The uri of the request.
     * @param maxWidth The max-width of the output.
     * @param maxHeight The max-height of the output.
     * @param scaleType The scaleType of the imageView.
     */
    static String generateCacheKey(String uri, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
        return new StringBuilder(uri.length()).append("#W").append(maxWidth)
                .append("#H").append(maxHeight).append("#S").append(scaleType.ordinal()).append(uri)
                .toString();
    }

    /**
     * image load uri scheme.
     */
    public enum Scheme {
        /**
         * http uri scheme.
         */
        HTTP("http")
        /**
         * https uri scheme.
         */
        , HTTPS("https")
        /**
         * sdcard file scheme.
         */
        , FILE("file")
        /**
         * content uri scheme.
         */
        , CONTENT("content")
        /**
         * assets uri scheme.
         */
        , ASSETS("assets")
        /**
         * drawable uri scheme.
         */
        , DRAWABLE("drawable")
        /**
         * default scheme.
         */
        , UNKNOWN("");

        private String scheme;
        private String uriPrefix;

        Scheme(String scheme) {
            this.scheme = scheme;
            uriPrefix = scheme + "://";
        }

        /**
         * Defines scheme of incoming URI.
         *
         * @param uri URI for scheme detection
         * @return Scheme of incoming URI
         */
        public static Scheme ofUri(String uri) {
            if (uri != null) {
                for (Scheme s : values()) {
                    if (s.belongsTo(uri)) {
                        return s;
                    }
                }
            }
            return UNKNOWN;
        }

        private boolean belongsTo(String uri) {
            return uri.toLowerCase(Locale.US).startsWith(uriPrefix);
        }

        /**
         * Appends scheme to incoming path.
         * @param path path
         * @return wrapped path
         */
        public String wrap(String path) {
            return uriPrefix + path;
        }

        /**
         * Removed scheme part ("scheme://") from incoming URI.
         * @param uri uri
         * @return cropped uri that without the scheme
         */
        public String crop(String uri) {
            if (!belongsTo(uri)) {
                throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, scheme));
            }
            return uri.substring(uriPrefix.length());
        }
    }
}