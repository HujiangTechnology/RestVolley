/*
 * ResponseResult      2015-12-10
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.webapi;

import java.util.Map;

/**
 * rest volley network response.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-12-10
 * @param <T> class Type
 */
public class RestVolleyResponse<T> {
    /**
     * http status code.
     */
    public int statusCode;
    /**
     * response data.
     */
    public T data;
    /**
     * response headers.
     */
    public Map<String, String> headers;
    /**
     * response not modified.
     */
    public boolean notModified;
    /**
     * response time.
     */
    public long networkTimeMs;
    /**
     * response message.
     */
    public String message;

    /**
     * constructor.
     * @param statusCode http status code.
     * @param data response data.
     * @param headers response headers.
     * @param notModified response not modified.
     * @param networkTimeMs responese time.
     * @param message response message.
     */
    public RestVolleyResponse(int statusCode, T data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
        this.statusCode = statusCode;
        this.data = data;
        this.headers = headers;
        this.notModified = notModified;
        this.networkTimeMs = networkTimeMs;
        this.message = message;
    }
}