/*
 * PutRequest      2015-11-25
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.webapi.request;

import android.content.Context;

import com.android.volley.Request;

/**
 * put method request.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-11-25
 */
public class PutRequest extends RestVolleyRequestWithBody<PutRequest> {
    /**
     * constructor.
     * @param context {@link Context}
     */
    public PutRequest(Context context) {
        super(context, Request.Method.PUT);
    }
}