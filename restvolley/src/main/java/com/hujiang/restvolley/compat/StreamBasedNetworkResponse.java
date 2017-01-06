/*
 * RVNetworkResponse      2017-01-04
 * Copyright (c) 2017 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.hujiang.restvolley.compat;

import com.android.volley.NetworkResponse;

import java.io.InputStream;
import java.util.Map;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2017-01-04
 */
public class StreamBasedNetworkResponse extends NetworkResponse {

    /** Raw data inputStream from this response. */
    public final InputStream inputStream;
    public final long contentLength;

    public StreamBasedNetworkResponse(int statusCode, InputStream inputStream, long contentLength, Map<String, String> headers, boolean notModified, long networkTimeMs) {
        super(statusCode, null, headers, notModified, networkTimeMs);
        this.inputStream = inputStream;
        this.contentLength = contentLength;
    }

    public StreamBasedNetworkResponse(int statusCode, InputStream inputStream, long contentLength, Map<String, String> headers, boolean notModified) {
        super(statusCode, null, headers, notModified);
        this.inputStream = inputStream;
        this.contentLength = contentLength;
    }

    public StreamBasedNetworkResponse(InputStream inputStream, long contentLength) {
        super(null);
        this.inputStream = inputStream;
        this.contentLength = contentLength;
    }

    public StreamBasedNetworkResponse(InputStream inputStream, long contentLength, Map<String, String> headers) {
        super(null, headers);
        this.inputStream = inputStream;
        this.contentLength = contentLength;
    }

    public StreamBasedNetworkResponse(int statusCode, byte[] data, Map<String, String> headers, boolean notModified, long networkTimeMs) {
        super(statusCode, data, headers, notModified, networkTimeMs);
        this.inputStream = null;
        this.contentLength = data != null ? data.length : 0;
    }

    public StreamBasedNetworkResponse(int statusCode, byte[] data, Map<String, String> headers, boolean notModified) {
        super(statusCode, data, headers, notModified);
        this.inputStream = null;
        this.contentLength = data != null ? data.length : 0;
    }

    public StreamBasedNetworkResponse(byte[] data) {
        super(data);
        this.inputStream = null;
        this.contentLength = data != null ? data.length : 0;
    }

    public StreamBasedNetworkResponse(byte[] data, Map<String, String> headers) {
        super(data, headers);
        this.inputStream = null;
        this.contentLength = data != null ? data.length : 0;
    }
}