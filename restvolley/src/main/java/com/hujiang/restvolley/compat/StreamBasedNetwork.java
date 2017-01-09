/*
 * RestVolleyNetwork      2016-08-01
 * Copyright (c) 2016 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.hujiang.restvolley.compat;

import android.os.SystemClock;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.RedirectError;
import com.android.volley.Request;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ByteArrayPool;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.PoolingByteArrayOutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2016-08-01
 */
public class StreamBasedNetwork extends RVNetwork {

    /**
     * @param httpStack HTTP stack to be used
     */
    public StreamBasedNetwork(HttpStack httpStack) {
        super(httpStack);
    }

    @Override
    public NetworkResponse performRequest(Request<?> request) throws VolleyError {
        long requestStart = SystemClock.elapsedRealtime();
        while (true) {
            HttpResponse httpResponse = null;
//            byte[] responseContents = null;
            InputStream responseStream = null;
            Map<String, String> responseHeaders = Collections.emptyMap();
            try {
                // Gather headers.
                Map<String, String> headers = new HashMap<String, String>();
                addCacheHeaders(headers, request.getCacheEntry());
                httpResponse = mHttpStack.performRequest(request, headers);
                StatusLine statusLine = httpResponse.getStatusLine();
                int statusCode = statusLine.getStatusCode();

                responseHeaders = convertHeaders(httpResponse.getAllHeaders());

                // Handle moved resources. process redirect event.
                if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                    String newUrl = responseHeaders.get("Location");
                    request.setRedirectUrl(newUrl);
                }

                // Some responses such as 204s do not have content.  We must check.
                if (httpResponse.getEntity() != null) {
                    responseStream = httpResponse.getEntity().getContent();
                }

                if (statusCode < 200 || statusCode > 299) {
                    throw new NetworkError();
                }
                return new StreamBasedNetworkResponse(statusCode, responseStream, httpResponse.getEntity().getContentLength(), responseHeaders, false, SystemClock.elapsedRealtime() - requestStart);
            } catch (SocketTimeoutException e) {
                attemptRetryOnException("socket", request, new TimeoutError());
            } catch (ConnectTimeoutException e) {
                attemptRetryOnException("connection", request, new TimeoutError());
            } catch (MalformedURLException e) {
                throw new RuntimeException("Bad URL " + request.getUrl(), e);
            } catch (IOException e) {
                int statusCode = 0;
                NetworkResponse networkResponse = null;
                if (httpResponse != null) {
                    statusCode = httpResponse.getStatusLine().getStatusCode();
                } else {
                    throw new NoConnectionError(e);
                }
                if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                    VolleyLog.e("Request at %s has been redirected to %s", request.getOriginUrl(), request.getUrl());
                } else {
                    VolleyLog.e("Unexpected response code %d for %s", statusCode, request.getUrl());
                }
                if (responseStream != null) {
                    networkResponse = new StreamBasedNetworkResponse(statusCode, responseStream, httpResponse.getEntity().getContentLength(), responseHeaders, false, SystemClock.elapsedRealtime() - requestStart);
                    if (statusCode == HttpStatus.SC_UNAUTHORIZED || statusCode == HttpStatus.SC_FORBIDDEN) {
                        attemptRetryOnException("auth", request, new AuthFailureError(networkResponse));
                    } else if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                        attemptRetryOnException("redirect", request, new RedirectError(networkResponse));
                    } else {
                        // TODO: Only throw ServerError for 5xx status codes.
                        throw new ServerError(networkResponse);
                    }
                } else {
                    throw new NetworkError(e);
                }
            }
        }
    }

    /** Reads the contents of HttpEntity into a byte[]. */
    public static byte[] entityToBytes(InputStream inputStream, long contentLength, int bufferSize) throws IOException, ServerError {
        ByteArrayPool bytePool = new ByteArrayPool(bufferSize);
        PoolingByteArrayOutputStream bytes = new PoolingByteArrayOutputStream(bytePool, (int)contentLength);
        byte[] buffer = null;
        try {
            if (inputStream == null) {
                throw new ServerError();
            }
            buffer = bytePool.getBuf(1024);
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                bytes.write(buffer, 0, count);
            }
            return bytes.toByteArray();
        } finally {
            try {
                // Close the InputStream and release the resources by "consuming the content".
                inputStream.close();
            } catch (IOException e) {
                // This can happen if there was an exception above that left the entity in
                // an invalid state.
                VolleyLog.v("Error occured when calling consumingContent");
            }
            bytePool.returnBuf(buffer);
            bytes.close();
        }
    }
}