/*
 * APICallback      2015-11-24
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.webapi;


import com.hujiang.restvolley.webapi.request.RestVolleyRequest;

import java.util.Map;

/**
 * RestVolley request callback.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-11-24
 * @param <T> callback data type.
 */
public abstract class RestVolleyCallback<T> {
    /**
     * start request callback.
     * @param request {@link RestVolleyRequest}
     */
    public void onStart(RestVolleyRequest request) {

    }

    /**
     * finish request callback.
     * @param request {@link RestVolleyRequest}
     */
    public void onFinished(RestVolleyRequest request) {

    }

    /**
     * request success callback.
     * @param statusCode http status code.
     * @param data response data.
     * @param headers response headers.
     * @param notModified data not modified.
     * @param networkTimeMs request times.
     * @param message request message.
     */
    public abstract void onSuccess(int statusCode, T data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message);

    /**
     * request fail callback.
     * @param statusCode http status code.
     * @param data response data.
     * @param headers response headers.
     * @param notModified data not modified.
     * @param networkTimeMs request times.
     * @param message request message.
     */
    public abstract void onFail(int statusCode, T data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message);
}