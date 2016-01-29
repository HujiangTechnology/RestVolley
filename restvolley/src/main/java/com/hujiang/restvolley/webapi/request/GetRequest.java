/*
 * GetRequest      2015-11-24
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.webapi.request;

import android.content.Context;

import com.android.volley.Request;

/**
 * Get method request.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-11-24
 */
public class GetRequest extends RestVolleyRequestWithNoBody<GetRequest> {
    /**
     * constructor.
     * @param context {@link Context}
     */
    public GetRequest(Context context) {
        super(context, Request.Method.GET);
    }
}