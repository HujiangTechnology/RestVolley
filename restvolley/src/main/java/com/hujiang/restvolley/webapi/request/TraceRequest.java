/*
 * TraceRequest      2015-11-25
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.webapi.request;

import android.content.Context;

import com.android.volley.Request;

/**
 * Trace request.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-11-25
 */
public class TraceRequest extends RestVolleyRequestWithNoBody<TraceRequest> {

    /**
     * constructor.
     * @param context Context.
     */
    public TraceRequest(Context context) {
        super(context, Request.Method.TRACE);
    }
}