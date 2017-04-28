/*
 * BaseAPIRequest      2015-11-24
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.webapi.request;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.RequestFuture;
import com.google.gson.JsonSyntaxException;
import com.hujiang.restvolley.CertificateUtils;
import com.hujiang.restvolley.GsonUtils;
import com.hujiang.restvolley.RequestEngine;
import com.hujiang.restvolley.RestVolley;
import com.hujiang.restvolley.compat.RVNetwork;
import com.hujiang.restvolley.compat.StreamBasedNetwork;
import com.hujiang.restvolley.compat.StreamBasedNetworkResponse;
import com.hujiang.restvolley.webapi.RestVolleyCallback;
import com.hujiang.restvolley.webapi.RestVolleyModel;
import com.hujiang.restvolley.webapi.RestVolleyResponse;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.OkHttpClient;

import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

/**
 * http request core.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-11-24
 * @param <R> type extends {@link RestVolleyRequest}
 */
public abstract class RestVolleyRequest<R extends RestVolleyRequest> {

    private static final String WEBAPI_REQUEST_ENGINE = "webapi";
    /**
     * http request engine {@link RequestEngine}.
     */
    protected RequestEngine mRequestEngine;
    /**
     * volley request {@link VolleyRequest}.
     */
    protected VolleyRequest mVolleyRequest;
    /**
     * request body content type.
     */
    protected String mContentType = RestVolley.APPLICATION_JSON;
    /**
     * request body char set.
     */
    protected String mCharset = HTTP.UTF_8;
    /**
     * method.
     */
    protected int mMethod;
    /**
     * request url.
     */
    protected String mUrl;
    /**
     * redirect url.
     */
    protected String mRedirectUrl;
    /**
     * sequence id.
     */
    protected int mSequence;
    /**
     * should cache or not.
     */
    protected boolean mShouldCache = false;
    /**
     * {@link RetryPolicy}.
     */
    protected RetryPolicy mRetryPolicy = new DefaultRetryPolicy(10000, 0, 1);
    /**
     * {@link com.android.volley.Cache.Entry}.
     */
    protected Cache.Entry mCacheEntry;
    /**
     * request tag.
     */
    protected Object mTag;
    /**
     * request marker.
     */
    protected String mMarker;
    /**
     * request priority {@link com.android.volley.Request.Priority}, default is {@link com.android.volley.Request.Priority#NORMAL}.
     */
    protected Request.Priority mPriority = Request.Priority.NORMAL;

    /**
     * request headers.
     */
    protected final Map<String, String> mHeaders = new HashMap<>();
    /**
     * request url params.
     */
    protected final Map<String, String> mUrlParams = new HashMap<String, String>();

    protected SSLSocketFactory mSSLSocketFactory = CertificateUtils.getDefaultSSLSocketFactory();
    protected HostnameVerifier mHostnameVerifier = CertificateUtils.ALLOW_ALL_HOSTNAME_VERIFIER;
    private Map<String, String> mPinners = new LinkedHashMap<>();
    protected Proxy mProxy;
    protected long mConnectTimeout = RestVolley.DEFAULT_HTTP_TIMEOUT;
    protected long mReadTimeout = RestVolley.DEFAULT_HTTP_TIMEOUT;
    protected long mWriteTimeout = RestVolley.DEFAULT_HTTP_TIMEOUT;

    /**
     * constructor.
     * @param context Context
     * @param method method
     */
    public RestVolleyRequest(Context context, int method) {
        mMethod = method;

        mRequestEngine = RestVolley.newRequestEngine(context, WEBAPI_REQUEST_ENGINE);
    }

    /**
     * set request engine, default is {@link RestVolley#newRequestEngine(Context, String)} with tag RestVolley.TAG_REQUEST_ENGINE_DEFAULT.
     * @param requestEngine {@link RequestEngine}
     * @return {@link RestVolleyRequest}
     */
    public R setRequestEngine(RequestEngine requestEngine) {
        if (requestEngine != null) {
            mRequestEngine = requestEngine;
        }

        return (R)this;
    }

    /**
     * get {@link RequestEngine}.
     * @return {@link RequestEngine}
     */
    public RequestEngine getRequestEngine() {
        return mRequestEngine;
    }

    public OkHttpClient getOkHttpClient() {
        return mRequestEngine.okHttpClient;
    }

    public R setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        mSSLSocketFactory = sslSocketFactory;
        return (R)this;
    }

    public R setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        mHostnameVerifier = hostnameVerifier;
        return (R)this;
    }

    public void addCertificatePinner(String hostname, String pinner) {
        if (!TextUtils.isEmpty(hostname) && !TextUtils.isEmpty(pinner)) {
            mPinners.put(hostname, pinner);
        }
    }
    /**
     * build request body as you need.
     * @return byte[]
     */
    protected abstract byte[] onBuildBody();

    /**
     * build request body content type.
     * @return String
     */
    protected String onBuildBodyContentType() {
        StringBuilder contentTypeBuilder = new StringBuilder(mContentType);
        contentTypeBuilder.append("; charset=").append(mCharset);
        return contentTypeBuilder.toString();
    }

    private void bindOkHttpClient() {
        mRequestEngine.okHttpClient.setSslSocketFactory(mSSLSocketFactory);
        mRequestEngine.okHttpClient.setHostnameVerifier(mHostnameVerifier);

        mRequestEngine.okHttpClient.setConnectTimeout(mConnectTimeout, TimeUnit.MILLISECONDS);
        mRequestEngine.okHttpClient.setReadTimeout(mReadTimeout, TimeUnit.MILLISECONDS);
        mRequestEngine.okHttpClient.setWriteTimeout(mWriteTimeout, TimeUnit.MILLISECONDS);
        mRequestEngine.okHttpClient.setProxy(mProxy);

        if (!mPinners.isEmpty()) {
            Set<Map.Entry<String, String>> sets = mPinners.entrySet();
            CertificatePinner.Builder cBuilder = new CertificatePinner.Builder();
            for (Map.Entry<String, String> entry : sets) {
                cBuilder.add(entry.getKey(), entry.getValue());
            }

            mRequestEngine.okHttpClient.setCertificatePinner(cBuilder.build());
        }
    }

    /**
     * execute request asynchronous.
     * @param clazz request class type.
     * @param callback request callback.
     * @param <DATA> request data type.
     */
    public <DATA> void execute(final Class<DATA> clazz, final RestVolleyCallback<DATA> callback) {
        if (callback == null) {
            throw new NullPointerException("callback should not be null");
        }
        ResponseListener<RestVolleyResponse<DATA>> responseListener = new ResponseListener<RestVolleyResponse<DATA>>() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                String content = "";
                Exception exception = error != null ? error : new Exception();
                try {
                    content = convertNetworkResponseData2String(response, mCharset);
                } catch (IOException e) {
                    exception = e;
                } catch (ServerError serverError) {
                    exception = serverError;
                }

                int httpStatus = response == null ? -1 : response.statusCode;
                DATA d = createDefaultResponseData(clazz, content);
                Map<String, String> headers = response == null ? null : response.headers;
                boolean notModified = response == null ? false : response.notModified;
                long networkTime = response == null ? 0 : response.networkTimeMs;

                callback.setException(error);
                callback.onFail(httpStatus, d, headers, notModified, networkTime, exception.toString());

                //finish callback
                callback.onFinished(RestVolleyRequest.this);

                error.printStackTrace();
            }

            @Override
            public void onResponse(RestVolleyResponse<DATA> response) {
                DATA responseData = response.data;
                if (responseData instanceof RestVolleyModel) {
                    if (((RestVolleyModel) responseData).getCode() == ((RestVolleyModel) responseData).successCode()) {
                        callback.onSuccess(response.statusCode, responseData, response.headers, response.notModified, response.networkTimeMs, response.message);
                    } else {
                        callback.setException(response.exception);
                        callback.onFail(response.statusCode, responseData, response.headers, response.notModified, response.networkTimeMs, response.message);
                    }
                } else if (responseData == null) {
                    responseData = createDefaultResponseData(clazz, response.message);
                    callback.setException(response.exception);
                    callback.onFail(response.statusCode, responseData, response.headers, response.notModified, response.networkTimeMs, response.message);
                } else {
                    callback.onSuccess(response.statusCode, responseData, response.headers, response.notModified, response.networkTimeMs, response.message);
                }
                //finish callback
                callback.onFinished(RestVolleyRequest.this);
            }
        };

        bindOkHttpClient();
        mVolleyRequest = new VolleyRequest(mMethod, onBuildUrl(), clazz, responseListener, responseListener);
        mVolleyRequest.setTag(mTag)
                .setRetryPolicy(mRetryPolicy)
                .setSequence(mSequence)
                .setShouldCache(mShouldCache)
                .setCacheEntry(mCacheEntry)
                .setRequestQueue(mRequestEngine.requestQueue)
                .setRedirectUrl(mRedirectUrl);
        mVolleyRequest.addMarker(mMarker);

        //start callback
        callback.onStart(this);

        mRequestEngine.requestQueue.add(mVolleyRequest);
    }

    /**
     * String data callback if only String data needed.
     * @param callback RestVolleyCallback that return String.
     */
    public void execute(final RestVolleyCallback<String> callback) {
        execute(String.class, callback);
    }

    /**
     * execute request synchronous.
     * @param clazz response data type
     * @param <DATA> response data type
     * @return {@link RestVolleyResponse}
     */
    public <DATA> RestVolleyResponse<DATA> syncExecute(Class<DATA> clazz) {
        bindOkHttpClient();
        RequestFuture<RestVolleyResponse<DATA>> future = RequestFuture.newFuture();
        mVolleyRequest = new VolleyRequest(mMethod, onBuildUrl(), clazz, future, future);
        mVolleyRequest.setTag(mTag)
                .setRetryPolicy(mRetryPolicy)
                .setSequence(mSequence)
                .setShouldCache(mShouldCache)
                .setCacheEntry(mCacheEntry)
                .setRequestQueue(mRequestEngine.requestQueue)
                .setRedirectUrl(mRedirectUrl);
        mVolleyRequest.addMarker(mMarker);

        future.setRequest(mVolleyRequest);

        mRequestEngine.requestQueue.add(mVolleyRequest);

        RestVolleyResponse<DATA> response = new RestVolleyResponse<DATA>(-1, null, null, false, 0, null);
        try {
            response = future.get();
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof VolleyError) {
                response.exception = (Exception) cause;
                response.message = cause.getMessage();
                NetworkResponse networkResponse = ((VolleyError) cause).networkResponse;
                response.statusCode = networkResponse != null ? networkResponse.statusCode : -1;
                response.headers = networkResponse != null ? networkResponse.headers : response.headers;
                response.networkTimeMs = networkResponse != null ? networkResponse.networkTimeMs : response.networkTimeMs;
                response.notModified = networkResponse != null ? networkResponse.notModified : response.notModified;
                response.data = networkResponse != null ? (DATA)mVolleyRequest.parseNetworkResponse2RVResponse(networkResponse).data : null;
            }
        }

        return response;
    }

    /**
     * execute request synchronous, return String.
     * @return {@link RestVolleyResponse}
     */
    public RestVolleyResponse<String> syncExecute() {
        return syncExecute(String.class);
    }

    /**
     * set url.
     * @param url request url
     * @return {@link RestVolleyRequest}
     */
    public R url(String url) {
        mUrl = url;

        return (R)this;
    }

    /**
     * set url.
     * @param host url host
     * @param path url path
     * @return {@link RestVolleyRequest}
     */
    public R url(String host, String path) {
        if (TextUtils.isEmpty(host)) {
            throw new IllegalArgumentException("hostUrl is null");
        }

        if (TextUtils.isEmpty(path)) {
            mUrl = host;
        } else {
            StringBuilder urlBuilder = new StringBuilder(host);
            if (host.endsWith("/")) {
                if (path.startsWith("/")) {
                    urlBuilder.append(path.substring(1));
                } else {
                    urlBuilder.append(path);
                }
            } else {
                if (path.startsWith("/")) {
                    urlBuilder.append(path);
                } else {
                    urlBuilder.append("/").append(path);
                }
            }

            mUrl = urlBuilder.toString();
        }

        return (R)this;
    }

    /**
     * build valid url.
     * @return url string
     */
    protected abstract String onBuildUrl();

    /**
     * get url params.
     * @return url param's map
     */
    public Map<String, String> getUrlParams() {
        return mUrlParams;
    }

    /**
     * set request connect timeout.
     * @param timeMillis time in mills
     * @return {@link RestVolleyRequest}
     */
    public R setConnectTimeout(long timeMillis) {
        if (timeMillis > 0) {
            mConnectTimeout = timeMillis;
        }

        return (R)this;
    }

    /**
     * set read timeout.
     * @param timeMillis time in mills
     * @return {@link RestVolleyRequest}
     */
    public R setReadTimeout(long timeMillis) {
        if (timeMillis > 0) {
            mReadTimeout = timeMillis;
        }

        return (R)this;
    }

    /**
     * set write timeout.
     * @param timeMillis time in mills
     * @return {@link RestVolleyRequest}
     */
    public R setWriteTimeout(long timeMillis) {
        if (timeMillis > 0) {
            mWriteTimeout = timeMillis;
        }

        return (R)this;
    }

    /**
     * set connect timeout, read timeout, write timeout with the same time in mills.
     * @param timeMillis time in mills.
     * @return {@link RestVolleyRequest}
     */
    public R setTimeout(long timeMillis) {
        if (timeMillis > 0) {
            setConnectTimeout(timeMillis);
            setReadTimeout(timeMillis);
            setWriteTimeout(timeMillis);
        }
        return (R)this;
    }

    /**
     * set request tag that you can cancel the request with it, eg: {@link RestVolley#cancelAll(RequestEngine, Object)}.
     * @param tag request tag
     * @return {@link RestVolleyRequest}
     * @see com.android.volley.Request#setTag(Object)
     */
    public R setTag(Object tag) {
        mTag = tag;
        return (R)this;
    }

    /**
     * get tag.
     * @return tag object
     * @see Request#getTag()
     */
    public Object getTag() {
        return mVolleyRequest == null ? mTag : mVolleyRequest.getTag();
    }

    /**
     * set the retry policy, default is {@link DefaultRetryPolicy()}.
     * @param retryPolicy see {@link DefaultRetryPolicy}
     * @return {@link RestVolleyRequest}
     * @see Request#setRetryPolicy(RetryPolicy)
     */
    public R setRetryPolicy(RetryPolicy retryPolicy) {
        if (mRetryPolicy != null) {
            mRetryPolicy = retryPolicy;
        }
        return (R)this;
    }

    /**
     * get {@link RetryPolicy}.
     * @return {@link RetryPolicy}
     * @see Request#getRetryPolicy()
     */
    public RetryPolicy getRetryPolicy() {
        return mVolleyRequest == null ? mRetryPolicy : mVolleyRequest.getRetryPolicy();
    }

    /**
     * add marker.
     * @param marker marker
     * @return {@link RestVolleyRequest}
     * @see Request#addMarker(String)
     */
    public R addMarker(String marker) {
        mMarker = marker;
        return (R)this;
    }

    /**
     * set request sequence.
     * @param sequence sequence
     * @return {@link RestVolleyRequest}
     * @see Request#setSequence(int)
     */
    public R setSequence(int sequence) {
        mSequence = sequence;
        return (R)this;
    }

    /**
     * get Sequence.
     * @return sequence
     * @see Request#getSequence()
     */
    public int getSequence() {
        return mVolleyRequest == null ? mSequence : mVolleyRequest.getSequence();
    }

    /**
     * get request url.
     * @return url.
     * @see Request#getUrl()
     */
    public String getUrl() {
        return mVolleyRequest == null ? mUrl : mVolleyRequest.getUrl();
    }

    /**
     * get origin url.
     * @return origin url
     * @see Request#getOriginUrl()
     */
    public String getOriginUrl() {
        return mVolleyRequest == null ? mUrl : mVolleyRequest.getOriginUrl();
    }

    /**
     * get identifier.
     * @return identifier.
     * @see Request#getIdentifier()
     */
    public String getIdentifier() {
        return mVolleyRequest == null ? getUrl() : mVolleyRequest.getIdentifier();
    }

    /**
     * set redirect url.
     * @param redirectUrl redirect url.
     * @return {@link RestVolleyRequest}
     * @see Request#setRedirectUrl(String)
     */
    public R setRedirectUrl(String redirectUrl) {
        mRedirectUrl = redirectUrl;
        return (R)this;
    }

    /**
     * get cache key.
     * @return cache key
     * @see Request#getCacheKey()
     */
    public String getCacheKey() {
        return mVolleyRequest == null ? mUrl : mVolleyRequest.getCacheKey();
    }

    /**
     * set cache entry.
     * @param entry {@link com.android.volley.Cache.Entry}
     * @return {@link RestVolleyRequest}
     * @see Request#setCacheEntry(Cache.Entry)
     */
    public R setCacheEntry(Cache.Entry entry) {
        mCacheEntry = entry;
        return (R)this;
    }

    /**
     * get cache entry.
     * @return {@link com.android.volley.Cache.Entry}
     * @see Request#getCacheEntry()
     */
    public Cache.Entry getCacheEntry() {
        return mVolleyRequest == null ? mCacheEntry : mVolleyRequest.getCacheEntry();
    }

    /**
     * cancel request.
     * @return {@link RestVolleyRequest}
     * @see Request#cancel()
     */
    public R cancel() {
        if (mVolleyRequest != null) {
            mVolleyRequest.cancel();
        }
        return (R)this;
    }

    /**
     * is this request canceled or not.
     * @return is this request canceled or not.
     * @see Request#isCanceled()
     */
    public boolean isCanceled() {
        return mVolleyRequest == null ? false : mVolleyRequest.isCanceled();
    }

    /**
     * set should cache or not.
     * @param shouldCache should cache or not.
     * @return {@link RestVolleyRequest}
     * @see Request#setShouldCache(boolean)
     */
    public R setShouldCache(boolean shouldCache) {
        mShouldCache = shouldCache;
        return (R)this;
    }

    /**
     * return should cache or not.
     * @return should cache or not
     * @see Request#shouldCache()
     */
    public boolean shouldCache() {
        return mVolleyRequest == null ? mShouldCache : mVolleyRequest.shouldCache();
    }

    /**
     * get priority.
     * @return {@link com.android.volley.Request.Priority}
     * @see Request#getPriority()
     */
    public Request.Priority getPriority() {
        return mPriority;
    }

    /**
     * set {@link com.android.volley.Request.Priority}
     * @param priority {@link com.android.volley.Request.Priority}
     * @return {@link RestVolleyRequest}
     */
    public R setPriority(Request.Priority priority) {
        if (priority != null) {
            mPriority = priority;
        }
        return (R)this;
    }

    /**
     * set mark delivered or not.
     * @return {@link RestVolleyRequest}
     * @see Request#markDelivered()
     */
    public R markDelivered() {
        if (mVolleyRequest != null) {
            mVolleyRequest.markDelivered();
        }
        return (R)this;
    }

    /**
     * is response delivered.
     * @return is response delivered or not.
     */
    public boolean hasHadResponseDelivered() {
        return mVolleyRequest == null ? false : mVolleyRequest.hasHadResponseDelivered();
    }

    /**
     * request charset, eg:"UTF-8".
     * @param charset charset, eg: "UTF-8"
     * @return {@link RestVolleyRequest}
     */
    public final R setCharset(String charset) {
        if (!TextUtils.isEmpty(charset)) {
            mCharset = charset;
        }

        return (R)this;
    }

    /**
     * set content type, eg:"application/json".
     * @param contentType content type.
     * @return {@link RestVolleyRequest}
     */
    public final R setContentType(String contentType) {
        if (!TextUtils.isEmpty(contentType)) {
            mContentType = contentType;
        }

        return (R)this;
    }

    /**
     * add request header.
     * @param key header key
     * @param value header value
     * @return {@link RestVolleyRequest}
     */
    public R addHeader(String key, String value) {
        this.mHeaders.put(key, value);
        return (R)this;
    }

    /**
     * add more request headers.
     * @param headers more headers in map
     * @return {@link RestVolleyRequest}
     */
    public R addHeaders(Map<String, String> headers) {
        if (headers != null) {
            Set<String> keySet = headers.keySet();
            for (String key : keySet) {
                this.mHeaders.put(key, headers.get(key));
            }
        }
        return (R)this;
    }

    /**
     * set request user agent.
     * @param userAgent user agent.
     * @return {@link RestVolleyRequest}
     */
    public R setUserAgent(String userAgent) {
        if (!TextUtils.isEmpty(userAgent)) {
            mHeaders.put(RestVolley.HEADER_USER_AGENT, userAgent);
        }

        return (R)this;
    }

    /**
     * add request String url params.
     * @param key url param's key
     * @param value url param's value
     * @return {@link RestVolleyRequest}
     */
    public R addParams(String key, String value) {
        if (key != null && value != null) {
            mUrlParams.put(key, value);
        }

        return (R)this;
    }

    /**
     * add request Object url params.
     * @param key param's key
     * @param value param's value
     * @return {@link RestVolleyRequest}
     */
    public R addParams(String key, Object value) {
        if (key != null && value != null) {
            mUrlParams.put(key, value.toString());
        }

        return (R)this;
    }

    /**
     * add request int url params.
     * @param key param's key
     * @param value param's value
     * @return {@link RestVolleyRequest}
     */
    public R addParams(String key, int value) {
        if (key != null) {
            mUrlParams.put(key, String.valueOf(value));
        }

        return (R)this;
    }

    /**
     * add request long url params.
     * @param key param's key
     * @param value param's value
     * @return {@link RestVolleyRequest}
     */
    public R addParams(String key, long value) {
        if (key != null) {
            mUrlParams.put(key, String.valueOf(value));
        }

        return (R)this;
    }

    private interface ResponseListener<K> extends Response.Listener<K>, Response.ErrorListener {

    }

    private static <T> T createDefaultResponseData(Class<T> clazz, String message) {
        if (clazz == String.class) {
            return (T)message;
        } else if (clazz == byte[].class && !TextUtils.isEmpty(message)) {
            return (T)message.getBytes();
        } else {
            T data = null;
            try {
                data = clazz.newInstance();
                if (data instanceof RestVolleyModel) {
                    ((RestVolleyModel) data).setCode(RestVolleyModel.INVALID_CODE);
                    ((RestVolleyModel) data).setMessage(message);
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return data;
        }
    }

    private String convertNetworkResponseData2String(NetworkResponse response, String encoding) throws IOException, ServerError {
        String content = new String();

        if (response == null) {
            return content;
        }

        byte[] datas;
        if (response instanceof StreamBasedNetworkResponse) {
            InputStream inputStream = ((StreamBasedNetworkResponse) response).inputStream;
            if (inputStream != null) {
                datas = StreamBasedNetwork.entityToBytes(inputStream, ((StreamBasedNetworkResponse) response).contentLength, RVNetwork.DEFAULT_POOL_SIZE);
            } else {
                datas = response.data;
            }
        } else {
            datas = response.data;
        }

        if (datas != null) {
            try {
                content = new String(datas, HttpHeaderParser.parseCharset(response.headers, encoding));
            } catch (Exception e) {
                content = new String(datas);
            }
        }

        return content;
    }

    private class VolleyRequest<T> extends Request<RestVolleyResponse<T>> {

        private Response.Listener<RestVolleyResponse<T>> mListener;
        private Class<T> mClassT;

        public VolleyRequest(int method, String url, Class<T> classT, Response.Listener<RestVolleyResponse<T>> listener, Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            mListener = listener;
            mClassT = classT;
        }

        protected RestVolleyResponse<T> parseNetworkResponse2RVResponse(NetworkResponse response) {
            RestVolleyResponse<T> result;

            if (response == null) {
                Exception e = new NoConnectionError();
                return new RestVolleyResponse<T>(0, null, null, false, 0, e.toString(), e);
            }

            if (mClassT == String.class) {
                //return data as string
                String content = "";
                Exception exception = new Exception();
                try {
                    content = convertNetworkResponseData2String(response, getParamsEncoding());
                } catch (IOException e) {
                    exception = e;
                } catch (ServerError serverError) {
                    exception = serverError;
                }
                result = new RestVolleyResponse<T>(response.statusCode, (T)content, response.headers, response.notModified, response.networkTimeMs, exception.toString(), exception);
            } else if (mClassT == byte[].class) {
                //return data as bytes
                byte[] datas = new byte[0];
                Exception exception = new Exception();
                if (response instanceof StreamBasedNetworkResponse) {
                    InputStream s = ((StreamBasedNetworkResponse) response).inputStream;
                    if (s != null) {
                        try {
                            datas = StreamBasedNetwork.entityToBytes(s, ((StreamBasedNetworkResponse) response).contentLength, RVNetwork.DEFAULT_POOL_SIZE);
                        } catch (IOException e) {
                            exception = e;
                        } catch (ServerError serverError) {
                            exception = serverError;
                        }
                    } else {
                        datas = response.data;
                    }
                } else {
                    datas = response.data;
                }
                result = new RestVolleyResponse<T>(response.statusCode, (T)datas, response.headers, response.notModified, response.networkTimeMs, exception.toString(), exception);
            } else {
                //return data as specified type
                String content = "";
                Exception exception = new Exception();
                try {
                    content = convertNetworkResponseData2String(response, getParamsEncoding());
                } catch (IOException e) {
                    exception = e;
                } catch (ServerError serverError) {
                    exception = serverError;
                }
                T data = null;

                if (!TextUtils.isEmpty(content)) {
                    try {
                        data = GsonUtils.fromJsonStringThrowEx(content, mClassT);
                    } catch (JsonSyntaxException e) {
                        exception = e;
                    }
                }

                result = new RestVolleyResponse<T>(response.statusCode, data, response.headers, response.notModified, response.networkTimeMs, exception.toString(), exception);
            }

            return result;
        }

        @Override
        protected Response<RestVolleyResponse<T>> parseNetworkResponse(NetworkResponse response) {
            return Response.success(parseNetworkResponse2RVResponse(response), HttpHeaderParser.parseCacheHeaders(response));
        }

        @Override
        protected void deliverResponse(RestVolleyResponse<T> response) {
            if (mListener != null) {
                mListener.onResponse(response);
            }
        }

        @Override
        public void deliverError(VolleyError error) {
            super.deliverError(error);
        }

        @Override
        protected Map<String, String> getParams() throws AuthFailureError {
            return mUrlParams;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return mHeaders;
        }

        @Override
        public String getUrl() {
            return super.getUrl();
        }

        @Override
        public String getOriginUrl() {
            return super.getOriginUrl();
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            return onBuildBody();
        }

        @Override
        public String getBodyContentType() {
            return onBuildBodyContentType();
        }

        @Override
        protected String getParamsEncoding() {
            return mCharset;
        }

        @Override
        public Priority getPriority() {
            return mPriority;
        }
    }
}