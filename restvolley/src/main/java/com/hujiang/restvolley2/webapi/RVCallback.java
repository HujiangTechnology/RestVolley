/*
 * APICallback      2015-11-24
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley2.webapi;


import com.hujiang.restvolley2.webapi.request.RVRequest;

import java.util.Map;

/**
 * RestVolley request callback.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-11-24
 * @param <T> callback data type.
 */
public abstract class RVCallback<T> {

    protected Exception mException;

    public void setException(Exception e) {
        mException = e;
    }

    public Exception getException() {
        return mException;
    }

    /**
     * start request callback.
     * @param request {@link RVRequest}
     */
    public void onStart(RVRequest request) {

    }

    /**
     * finish request callback.
     * @param request {@link RVRequest}
     */
    public void onFinished(RVRequest request) {

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