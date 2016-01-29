/*
 * OptionsRequest      2015-11-25
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.webapi.request;

import android.content.Context;

import com.android.volley.Request;

/**
 * Option method request.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-11-25
 */
public class OptionsRequest extends RestVolleyRequestWithNoBody<OptionsRequest> {
    /**
     * constructor.
     * @param context {@link Context}
     */
    public OptionsRequest(Context context) {
        super(context, Request.Method.OPTIONS);
    }
}