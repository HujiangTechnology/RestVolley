/*
 * DeleteRequest      2015-11-25
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.webapi.request;

import android.content.Context;
import android.net.Uri;

import com.android.volley.Request;

import java.util.Set;

/**
 * Delete method request.
 * <br>
 * delete method request can not support body request if you create instance with {@link #DeleteRequest(Context)}. <br>
 * otherwise than {@link #DeleteRequest(Context, boolean)}.
 * @author simon
 * @version 1.0.0
 * @since 2015-11-25
 */
public class DeleteRequest extends RestVolleyRequestWithBody<DeleteRequest> {

    private boolean bodyRequest = false;

    /**
     * constructor, can not support body request.
     * @param context {@link Context}
     */
    public DeleteRequest(Context context) {
        super(context, Request.Method.DELETE);
    }

    /**
     * constructor, support body request if you set bodyrequest as true.
     * @param context {@link Context}
     * @param bodyRequest support body request or not
     */
    public DeleteRequest(Context context, boolean bodyRequest) {
        super(context, Request.Method.DELETE);
        this.bodyRequest = bodyRequest;
    }

    @Override
    protected String onBuildUrl() {
        if (bodyRequest) {
            return mUrl;
        } else {
            String url = mUrl;
            Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
            Set<String> keySet = mUrlParams.keySet();
            for (String s : keySet) {
                uriBuilder.appendQueryParameter(s, mUrlParams.get(s));
            }

            return uriBuilder.toString();
        }
    }

    @Override
    protected byte[] onBuildBody() {
        if (bodyRequest) {
            return super.onBuildBody();
        } else {
            return null;
        }
    }
}