/*
 * RestVolleyUpload      2015-12-18
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.upload;

import android.content.Context;

import com.hujiang.restvolley.webapi.request.PostRequest;

/**
 * to upload file or stream.
 * <br>
 * used like {@link PostRequest}, not support big file upload, because it uses {@link PostRequest} to post file that depending on Volley.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-12-18
 */
public class RestVolleyUpload extends PostRequest {

    /**
     * constructor.
     * @param context {@link Context}
     */
    public RestVolleyUpload(Context context) {
        super(context);
    }
}