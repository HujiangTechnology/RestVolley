/*
 * HttpEngine      2016-01-05
 * Copyright (c) 2016 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.squareup.okhttp.OkHttpClient;

/**
 * contains {@link RequestQueue} of the {@link com.android.volley.toolbox.Volley} and the {@link OkHttpClient} that requesting data from the network.
 *
 * @author simon
 * @version 1.0.0
 * @since 2016-01-05
 */
public class RequestEngine {
    /**
     * {@link RequestQueue} of the {@link com.android.volley.toolbox.Volley}.
     */
    public RequestQueue requestQueue;
    /**
     * {@link OkHttpClient}.
     */
    public OkHttpClient okHttpClient;

    RequestEngine(RequestQueue requestQueue, OkHttpClient okHttpClient) {
        this.requestQueue = requestQueue;
        this.okHttpClient = okHttpClient;
    }

    /**
     * cancel all the request associated wht the tag from the {@link com.android.volley.toolbox.Volley} {@link RequestQueue}.
     * @param tag request tag.
     */
    public void cancelAll(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

    /**
     * cancel all the request in the {@link RequestQueue} of the {@link com.android.volley.toolbox.Volley}.
     */
    public void cancelAll() {
        if (requestQueue != null) {
            requestQueue.cancelAll(new RequestQueue.RequestFilter() {
                @Override
                public boolean apply(Request<?> request) {
                    return true;
                }
            });
        }
    }

    /**
     * {@link RequestQueue} stop work.
     */
    public void stop() {
        if (requestQueue != null) {
            requestQueue.stop();
        }
    }
}