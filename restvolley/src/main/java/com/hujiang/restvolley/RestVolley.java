/*
 * RestVolley      2015-12-16
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.OkHttpClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * restvolley tool.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-12-16
 */
public class RestVolley {

    /**
     * http header Content-Type.
     */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    /**
     * http header Content-Range.
     */
    public static final String HEADER_CONTENT_RANGE = "Content-Range";
    /**
     * http header Content-Encoding.
     */
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    /**
     * http header Content-Length.
     */
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    /**
     * http header Range.
     */
    public static final String HEADER_RANGE = "Range";
    /**
     * http header Content-Disposition.
     */
    public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    /**
     * http header Accept-Encoding.
     */
    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    /**
     * http header User-Agent.
     */
    public static final String HEADER_USER_AGENT = "User-Agent";
    /**
     * encoding gzip.
     */
    public static final String ENCODING_GZIP = "gzip";
    /**
     * mimetype application/octet-stream.
     */
    public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";
    /**
     * mimetype application/json.
     */
    public final static String APPLICATION_JSON = "application/json";
    /**
     * mimetype application/x-www-form-urlencoded.
     */
    public final static String APPLICATION_X_WWW_FORM_URL_ENCODED = "application/x-www-form-urlencoded";

    /**
     * default http timeout 10000.
     */
    public static final int DEFAULT_HTTP_TIMEOUT = 10 * 1000;

    /**
     * default request engine tag.
     */
    public static final String TAG_REQUEST_ENGINE_DEFAULT = "restvolley_default_request_engine_Tag";

    private static Map<String, RequestEngine> sRequestEngineMap = new HashMap<String, RequestEngine>();

    /**
     * create a new http engine with tag that contains OkHttpClient and RequestQueue.
     * <br>
     * <br>
     * if the http engine with the special tag exists, return the existing http engine, otherwise create a new http engine and return.
     * @param context Context.
     * @param engineTag http engine Tag related to the http engine.
     * @return HttpEngine.
     */
    public static RequestEngine newRequestEngine(Context context, String engineTag) {
        RequestEngine requestEngine = sRequestEngineMap.get(engineTag);
        if (requestEngine == null) {
            OkHttpClient okHttpClient = new OkHttpClient();

            okHttpClient.setConnectTimeout(DEFAULT_HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            okHttpClient.setReadTimeout(DEFAULT_HTTP_TIMEOUT, TimeUnit.MILLISECONDS);
            okHttpClient.setWriteTimeout(DEFAULT_HTTP_TIMEOUT, TimeUnit.MILLISECONDS);

            okHttpClient.setSslSocketFactory(SSLManager.instance().getSSLSocketFactory());
            okHttpClient.setHostnameVerifier(SSLManager.instance().getHostnameVerifier());

            RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext(), new OkHttpStack(okHttpClient));
            requestQueue.start();

            requestEngine = new RequestEngine(requestQueue, okHttpClient);

            sRequestEngineMap.put(engineTag, requestEngine);
        }

        return requestEngine;
    }

    /**
     * return default request engine.
     * @param context Context
     * @return RequestEngine
     */
    public static RequestEngine getDefaultRequestEngine(Context context) {
        return newRequestEngine(context, TAG_REQUEST_ENGINE_DEFAULT);
    }

    /**
     * cancel all request with the tag that int the specified {@link RequestEngine}
     * @param requestEngine {@link RequestEngine}
     * @param tag request tag.
     */
    public static void cancelAll(RequestEngine requestEngine, Object tag) {
        if (requestEngine != null) {
            requestEngine.cancelAll(tag);
        }
    }

    /**
     * cancel all request in the specified {@link RequestEngine}.
     * @param requestEngine {@link RequestEngine}
     */
    public static void cancelAll(RequestEngine requestEngine) {
        if (requestEngine != null) {
            requestEngine.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
        }
    }

    /**
     * stop the request int the specified {@link RequestEngine}.
     * @param requestEngine {@link RequestEngine}
     */
    public static void stop(RequestEngine requestEngine) {
        if (requestEngine != null) {
            requestEngine.stop();
        }
    }
}