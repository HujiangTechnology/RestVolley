/*
 * RestVolleyDownload      2016-01-14
 * Copyright (c) 2016 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.download;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.hujiang.restvolley.CertificateUtils;
import com.hujiang.restvolley.RequestEngine;
import com.hujiang.restvolley.RestVolley;
import com.hujiang.restvolley.webapi.request.RestVolleyRequest;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.CertificatePinner;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;


/**
 * download tool depends on Okhttp.
 *
 * @author simon
 * @version 1.0.0
 * @since 2016-01-14
 * TODO Callback 替换为自己的Callback
 */
public class RestVolleyDownload {

    public static final String SUFIX_TMP = ".tmp";

    private static final int BUFFER_SIZE = 10240;

    private static final String DOWNLOAD_REQUEST_ENGINE_TAG = "download";

    private Handler mMainHandler = new Handler(Looper.getMainLooper());

    private Request.Builder mRequestBuilder;
    private OkHttpClient mOkHttpClient;
    private boolean mIsAppend;
    private boolean mIsCanceled;

    private SSLSocketFactory mSSLSocketFactory = CertificateUtils.getDefaultSSLSocketFactory();
    private HostnameVerifier mHostnameVerifier = CertificateUtils.DEFAULT_HOSTNAME_VERIFIER;
    private Map<String, String> mPinners = new LinkedHashMap<>();
    private Proxy mProxy;
    private long mConnectTimeout = RestVolley.DEFAULT_HTTP_TIMEOUT;
    private long mReadTimeout = RestVolley.DEFAULT_HTTP_TIMEOUT;
    private long mWriteTimeout = RestVolley.DEFAULT_HTTP_TIMEOUT;

    /**
     * constructor with default {@link RequestEngine} that created by {@link RestVolley#getDefaultRequestEngine(Context)}.
     * @param context Context
     */
    public RestVolleyDownload(Context context) {
        this(RestVolley.newRequestEngine(context, DOWNLOAD_REQUEST_ENGINE_TAG));
    }

    /**
     * constructor with specified {@link RequestEngine}.
     * @param requestEngine {@link RequestEngine}
     */
    public RestVolleyDownload(RequestEngine requestEngine) {
        mRequestBuilder = new Request.Builder();
        mRequestBuilder.get();
        mOkHttpClient = requestEngine.okHttpClient;
    }

    public RestVolleyDownload setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        mSSLSocketFactory = sslSocketFactory;

        return this;
    }

    public RestVolleyDownload setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        mHostnameVerifier = hostnameVerifier;

        return this;
    }

    public void addCertificatePinner(String hostname, String pinner) {
        if (!TextUtils.isEmpty(hostname) && !TextUtils.isEmpty(pinner)) {
            mPinners.put(hostname, pinner);
        }
    }

    /**
     * set download url.
     * @param url download url
     * @return {@link RestVolleyDownload}
     */
    public RestVolleyDownload url(String url) {
        mRequestBuilder.url(url);

        return this;
    }

    /**
     * add header.
     * @param key header key
     * @param value header value
     * @return {@link RestVolleyDownload}
     */
    public RestVolleyDownload addHeader(String key, String value) {
        mRequestBuilder.addHeader(key, value);

        return this;
    }

    /**
     * add header.
     * @param headers {@link Headers}
     * @return {@link RestVolleyDownload}
     */
    public RestVolleyDownload addHeader(Headers headers) {
        mRequestBuilder.headers(headers);

        return this;
    }

    /**
     * set request user agent.
     * @param userAgent user agent.
     * @return
     */
    public RestVolleyDownload setUserAgent(String userAgent) {
        if (!TextUtils.isEmpty(userAgent)) {
            mRequestBuilder.addHeader(RestVolley.HEADER_USER_AGENT, userAgent);
        }

        return this;
    }

    /**
     * set tag that associated to the download.
     * @param tag tag
     * @return {@link RestVolleyDownload}
     */
    public RestVolleyDownload tag(Object tag) {
        mRequestBuilder.tag(tag);

        return this;
    }

    /**
     * set download proxy.
     * @param proxy {@link Proxy}
     * @return {@link RestVolleyDownload}
     */
    public RestVolleyDownload setProxy(Proxy proxy) {
        mProxy = proxy;
        return this;
    }

    /**
     * set proxy.
     * @param host proxy host
     * @param port proxy port
     * @return {@link RestVolleyDownload}
     */
    public RestVolleyDownload setProxy(String host, int port) {
        if (!TextUtils.isEmpty(host)) {
            SocketAddress address = new InetSocketAddress(host, port);
            mProxy = new Proxy(Proxy.Type.HTTP, address);
        }

        return this;
    }

    /**
     * set connect timeout.
     * @param timeout time in mills
     * @return {@link RestVolleyDownload}
     */
    public RestVolleyDownload setConnectTimeout(long timeout) {
        mConnectTimeout = timeout;
        return this;
    }

    /**
     * set read timeout.
     * @param timeout time in mills
     * @return {@link RestVolleyDownload}
     */
    public RestVolleyDownload setReadTimeout(long timeout) {
        mReadTimeout = timeout;
        return this;
    }

    /**
     * set write timeout.
     * @param timeout time in mills
     * @return {@link RestVolleyDownload}
     */
    public RestVolleyDownload setWriteTimeout(long timeout) {
        mWriteTimeout = timeout;
        return this;
    }

    /**
     * set connect, read, write timeout.
     * @param timeout time in mills
     * @return {@link RestVolleyDownload}
     */
    public RestVolleyDownload setTimeout(long timeout) {
        setConnectTimeout(timeout);
        setReadTimeout(timeout);
        setWriteTimeout(timeout);
        return this;
    }

    /**
     * set is appending the data at the end of the file or not.
     * @param isAppend is append or not
     * @return {@link RestVolleyDownload}
     */
    public RestVolleyDownload setIsAppend(boolean isAppend) {
        mIsAppend = isAppend;

        return this;
    }

    /**
     * is append the data at the end of the file or not.
     * @return is append or not.
     */
    public boolean isAppend() {
        return mIsAppend;
    }

    public void cancel() {
        mIsCanceled = true;
        mOkHttpClient.cancel(mRequestBuilder.build().tag());
    }

    /**
     * cancel the download event with the specified tag.
     * @param tag tag.
     */
    @Deprecated
    public void cancel(Object tag) {
        mIsCanceled = true;
        mOkHttpClient.cancel(tag);
    }

    /**
     * cancel the download task with the specified tag on the specified {@link OkHttpClient}.
     * @param okHttpClient {@link OkHttpClient}
     * @param tag tag
     */
    public static void cancel(OkHttpClient okHttpClient, Object tag) {
        if (okHttpClient != null) {
            okHttpClient.cancel(tag);
        }
    }

    /**
     * get the {@link OkHttpClient}.
     * @return {@link OkHttpClient}
     */
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    private void bindOkHttpClient() {
        mOkHttpClient.setSslSocketFactory(mSSLSocketFactory);
        mOkHttpClient.setHostnameVerifier(mHostnameVerifier);
        mOkHttpClient.setConnectTimeout(mConnectTimeout, TimeUnit.MILLISECONDS);
        mOkHttpClient.setReadTimeout(mReadTimeout, TimeUnit.MILLISECONDS);
        mOkHttpClient.setWriteTimeout(mWriteTimeout, TimeUnit.MILLISECONDS);
        mOkHttpClient.setProxy(mProxy);

        if (!mPinners.isEmpty()) {
            Set<Map.Entry<String, String>> sets = mPinners.entrySet();
            CertificatePinner.Builder cBuilder = new CertificatePinner.Builder();
            for (Map.Entry<String, String> entry : sets) {
                cBuilder.add(entry.getKey(), entry.getValue());
            }

            mOkHttpClient.setCertificatePinner(cBuilder.build());
        }
    }

    /**
     * download.
     * @param localPath local path
     * @param callback {@link Callback}
     */
    public void download(final String localPath, Callback callback) {
        bindOkHttpClient();
        mOkHttpClient.newCall(mRequestBuilder.get().build()).enqueue(callback);
    }

    /**
     * download.
     * @param localPath local file path
     * @param listener {@link OnDownloadListener}
     */
    public void download(final String localPath, final OnDownloadListener listener) {

        final Request request = mRequestBuilder.build();

        notifyDownloadStart(request.urlString(), listener);
        bindOkHttpClient();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                notifyDownloadError(request.urlString(), e, -1, null, listener);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    //new download file
                    final File localFile = newFile(localPath + SUFIX_TMP);
                    //write stream
                    if (response.isSuccessful() && writeStream2File(response, localFile, mIsAppend, listener)) {
                        notifyDownloadSuccess(request.urlString(), new File(localPath), response.code(), response.headers(), listener);
                    } else {
                        notifyDownloadError(request.urlString(), new Exception("download does not complete:" + localFile.getAbsolutePath())
                                , response.code(), response.headers(), listener);
                    }
                } catch (Exception e) {
                   notifyDownloadError(request.urlString(), e, response.code(), response.headers(), listener);
                }
            }
        });
    }

    /**
     * static download.
     * @param context Context
     * @param url url
     * @param localPath local file path
     * @param listener {@link OnDownloadListener}
     */
    public static void download(Context context, String url, final String localPath, final OnDownloadListener listener) {
        new RestVolleyDownload(context).url(url).download(localPath, listener);
    }

    /**
     * sync download.
     * @param localPath local file path
     * @return {@link DownloadResponse}
     */
    public DownloadResponse syncDownload(String localPath) {
        File localFile = newFile(localPath + SUFIX_TMP);
        DownloadResponse downloadResponse = new DownloadResponse();
        bindOkHttpClient();
        try {
            Response result = mOkHttpClient.newCall(mRequestBuilder.build()).execute();
            //write stream to file
            File resultFile  = writeStream2File(result, localFile, mIsAppend, null) ? new File(localPath) : localFile;
            downloadResponse = new DownloadResponse(resultFile, result.headers(), result.code(), result.message());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return downloadResponse;
    }

    /**
     * static sync download.
     * @param context Context
     * @param url download url
     * @param localPath local file path
     * @return {@link DownloadResponse}
     */
    public static DownloadResponse syncDownload(Context context, String url, String localPath) {
        return new RestVolleyDownload(context).url(url).syncDownload(localPath);
    }

    /**
     * delete file if exists, create a new file and the parent dir.
     * @param localPath local file path
     * @return File new file
     */
    private static synchronized File newFile(String localPath) {
        final File localFile = new File(localPath);
        if (localFile.exists()) {
            localFile.delete();
        } else {
            File parentFile = localFile.getParentFile();
            if (parentFile != null && (parentFile.isDirectory() || parentFile.mkdirs())) {
                try {
                    localFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return localFile;
    }

    private boolean writeStream2File(final Response response, final File localFile, final boolean isAppend
            , final OnDownloadListener listener) throws Exception {
        ResponseBody body = response.body();
        InputStream inputStream = body.byteStream();

        boolean isErrorOccurs = false;
        long totalBytes = 0;
        try {
            totalBytes = Long.parseLong(response.header(RestVolley.HEADER_CONTENT_LENGTH));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (inputStream != null) {
            FileOutputStream buffer = new FileOutputStream(localFile, isAppend);
            long count = 0;
            try {
                byte[] tmp = new byte[BUFFER_SIZE];
                int l;
                // do not send messages if request has been cancelled
                while (!Thread.currentThread().isInterrupted()) {
                    if (mIsCanceled) {
                        break;
                    }
                    l = inputStream.read(tmp);
                    if (l == -1) {
                        break;
                    }

                    count += l;
                    buffer.write(tmp, 0, l);

                    notifyDownloadProgress(response.request().urlString(), count, totalBytes, localFile, response.code()
                            , response.headers(), listener);
                }
            } catch(Exception e) {
                isErrorOccurs = true;
                throw e;
            } finally {
                if (!mIsCanceled && !isErrorOccurs) {
                    if (totalBytes == 0) {
                        totalBytes = count;
                        notifyDownloadProgress(response.request().urlString(), count, totalBytes, localFile, response.code()
                                , response.headers(), listener);
                    }

                    String tmpFilePath = localFile.getAbsolutePath();
                    localFile.renameTo(new File(tmpFilePath.substring(0, tmpFilePath.indexOf(SUFIX_TMP))));
                }
                inputStream.close();

                if (buffer != null) {
                    buffer.flush();
                    buffer.close();
                }
            }

            return !mIsCanceled && !isErrorOccurs;
        }
        return false;
    }

    private void notifyDownloadProgress(final String url, final long downloadBytes, final long totalBytes, final File localFile
            , final int httpCode, final Headers headers, final OnDownloadListener listener) {
        if (listener != null) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onDownloadProgress(url, downloadBytes, totalBytes, localFile, httpCode, headers);
                }
            });
        }
    }

    private void notifyDownloadStart(final String url, final OnDownloadListener listener) {
        if (listener != null) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onDownloadStart(url);
                }
            });
        }
    }

    private void notifyDownloadError(final String url, final Exception e, final int httpCode, final Headers headers, final OnDownloadListener listener) {
        if (listener != null) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onDownloadFailure(url, e, httpCode, headers);
                }
            });
        }
    }

    private void notifyDownloadSuccess(final String url, final File file, final int httpCode, final Headers headers, final OnDownloadListener listener) {
        if (listener != null) {
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onDownloadSuccess(url, file, httpCode, headers);
                }
            });
        }
    }

    /**
     * download response.
     */
    public static class DownloadResponse {
        /**
         * download file.
         */
        public File downloadFile;
        /**
         * response headers.
         */
        public Headers headers;
        /**
         * response http code.
         */
        public int httpCode;
        /**
         * response message.
         */
        public String message;

        /**
         * constructor.
         */
        public DownloadResponse() {

        }

        /**
         * constructor.
         * @param downloadFile download file
         * @param headers {@link Headers}
         * @param httpCode response http code
         * @param message response message
         */
        public DownloadResponse(File downloadFile, Headers headers, int httpCode, String message) {
            this.downloadFile = downloadFile;
            this.headers = headers;
            this.httpCode = httpCode;
            this.message = message;
        }
    }

    /**
     * 下载回调监听接口.
     */
    public static interface OnDownloadListener {
        /**
         * start callback.
         * @param url url
         */
        public void onDownloadStart(String url);

        /**
         * success callback.
         * @param url download url
         * @param file download local file
         * @param httpCode http status code
         * @param headers http response headers
         */
        public void onDownloadSuccess(String url, File file, int httpCode, Headers headers);

        /**
         * failure callback.
         * @param url url
         * @param e Exception
         * @param httpCode http code
         * @param headers {@link Headers}
         */
        public void onDownloadFailure(String url, Exception e, int httpCode, Headers headers);

        /**
         * progress callback.
         * @param url url
         * @param downloadBytes download bytes
         * @param contentLength content length
         * @param file file
         * @param httpCode http code
         * @param headers {@link Headers}
         */
        public void onDownloadProgress(String url, long downloadBytes, long contentLength, File file, int httpCode, Headers headers);
    }
}