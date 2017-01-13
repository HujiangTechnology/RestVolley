/*
 * OkhttpStack      2015-11-18
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley2;

import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.OkUrlFactory;

/**
 * using the okhttp as the transport layer.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-11-18
 */
public class OkHttpStack extends HurlStack {

    private OkUrlFactory mOkUrlFactory;

    /**
     * constructor.
     */
    public OkHttpStack() {
        this(new OkHttpClient());
    }

    /**
     * constructor.
     * @param okHttpClient {@link OkHttpClient}
     */
    public OkHttpStack(OkHttpClient okHttpClient) {

        mOkUrlFactory = new OkUrlFactory(okHttpClient);
    }

    /**
     * {@link OkHttpClient}.
     * @return {@link OkHttpClient}
     */
    public OkHttpClient getClient() {
        return mOkUrlFactory.client();
    }

    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        return mOkUrlFactory.open(url);
    }
}