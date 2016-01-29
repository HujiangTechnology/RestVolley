/*
 * RestVolleyRequestWithNoBody      2015-12-16
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.webapi.request;

import android.content.Context;
import android.net.Uri;

import java.util.Set;

/**
 * request with no body.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-12-16
 * @param <R> Type extends {@link RestVolleyRequest}
 */
public class RestVolleyRequestWithNoBody<R extends RestVolleyRequest> extends RestVolleyRequest<R> {
    /**
     * constructor.
     * @param context Context.
     * @param method request method.
     */
    public RestVolleyRequestWithNoBody(Context context, int method) {
        super(context, method);
    }

    @Override
    protected String onBuildUrl() {
        String url = mUrl;
        Uri.Builder uriBuilder = Uri.parse(url).buildUpon();
        Set<String> keySet = mUrlParams.keySet();
        for (String s : keySet) {
            uriBuilder.appendQueryParameter(s, mUrlParams.get(s));
        }

        return uriBuilder.toString();
    }

    @Override
    protected byte[] onBuildBody() {
        return null;
    }
}