/*
 * RestVolleyRequestWithBody      2015-12-16
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.webapi.request;

import android.content.Context;
import android.text.TextUtils;

import com.hujiang.restvolley.RestVolley;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * request with body.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-12-16
 * @param <R> type extends {@link RestVolleyRequest}
 */
public class RestVolleyRequestWithBody<R extends RestVolleyRequest> extends RestVolleyRequest<R> {
    /**
     * stream params map.
     */
    protected final Map<String, StreamWrapper> mStreamParams = new HashMap<String, StreamWrapper>();
    /**
     * file params map.
     */
    protected final Map<String, FileWrapper> mFileParams = new HashMap<String, FileWrapper>();

    private HttpEntity mHttpEntity;
    private byte[] mBody;

    /**
     * constructor.
     * @param context Context.
     * @param method method.
     */
    public RestVolleyRequestWithBody(Context context, int method) {
        super(context, method);
    }

    @Override
    protected String onBuildUrl() {
        return mUrl;
    }

    @Override
    protected String onBuildBodyContentType() {
        if (mHttpEntity != null) {
            return mHttpEntity.getContentType().getValue();
        } else {
            return super.onBuildBodyContentType();
        }
    }

    @Override
    protected byte[] onBuildBody() {
        if (mBody != null) {
            return mBody;
        } else {
            if (mHttpEntity == null) {
                if (mStreamParams.isEmpty() && mFileParams.isEmpty()) {
                    if (RestVolley.APPLICATION_JSON.equals(mContentType)) {
                        try {
                            mHttpEntity = createJsonStreamerEntity(null);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mHttpEntity = createFormEntity();
                    }
                } else {
                    try {
                        mHttpEntity = createMultipartEntity(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            ByteArrayOutputStream byteArrayOutputStream = null;
            try {
                byteArrayOutputStream = new ByteArrayOutputStream();
                mHttpEntity.writeTo(byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (byteArrayOutputStream != null) {
                    try {
                        byteArrayOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    /**
     * set request body.
     * @param body request body
     * @return {@link RestVolleyRequestWithBody}
     */
    public R setBody(byte[] body) {
        mBody = body;
        return (R)this;
    }

    /**
     * set request body.
     * @param body body
     * @return {@link RestVolleyRequestWithBody}
     */
    public R setBody(String body) {
        if (!TextUtils.isEmpty(body)) {
            try {
                mBody = body.getBytes(mCharset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return (R)this;
    }

    /**
     * set request body.
     * @param entityBody {@link HttpEntity}
     * @return {@link RestVolleyRequestWithBody}
     */
    public R setBody(HttpEntity entityBody) {
        if (entityBody != null) {
            mHttpEntity = entityBody;
        }
        return (R)this;
    }

    /**
     * convert params to Form Entity.
     * @return {@link RestVolleyRequestWithBody}
     */
    public R paramsToFormEntity() {
        mHttpEntity = createFormEntity();

        return (R)this;
    }

    /**
     * convert params to Json Entity.
     * @return {@link RestVolleyRequestWithBody}
     */
    public R paramsToJsonEntity() {
        try {
            mHttpEntity = createJsonStreamerEntity(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (R)this;
    }

    /**
     * convert params to Json Entity.
     * @param elapsedFieldInJsonStreamer elapsed field.
     * @return {@link RestVolleyRequestWithBody}
     */
    public R paramsToJsonEntity(String elapsedFieldInJsonStreamer) {
        try {
            mHttpEntity = createJsonStreamerEntity(elapsedFieldInJsonStreamer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (R)this;
    }

    /**
     * convert params to multipart Entity.
     * @return {@link RestVolleyRequestWithBody}
     */
    public R paramsToMultipartEntity() {
        try {
            mHttpEntity = createMultipartEntity(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (R)this;
    }

    /**
     * convert params to multipart Entity.
     * @param isrepeatable multipart repeatable or not.
     * @return {@link RestVolleyRequestWithBody}
     */
    public R paramsToMultipartEntity(boolean isrepeatable) {
        try {
            mHttpEntity = createMultipartEntity(isrepeatable);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (R)this;
    }

    /**
     * add file params.
     * @param key key
     * @param file file
     * @return {@link RestVolleyRequestWithBody}
     */
    public R addParams(String key, File file) {
        addParams(key, file, null, null);
        return (R)this;
    }

    /**
     * add file params.
     * @param key key
     * @param customFileName file name
     * @param file file
     * @return {@link RestVolleyRequestWithBody}
     */
    public R addParams(String key, String customFileName, File file) {
        addParams(key, file, null, customFileName);

        return (R)this;
    }

    /**
     * add file params.
     * @param key key
     * @param file file
     * @param contentType file content type
     * @return {@link RestVolleyRequestWithBody}
     */
    public R addParams(String key, File file, String contentType) {
        addParams(key, file, contentType, null);

        return (R)this;
    }

    /**
     * add file params.
     * @param key key
     * @param file file
     * @param contentType file content type
     * @param customFileName file name
     * @return {@link RestVolleyRequestWithBody}
     */
    public R addParams(String key, File file, String contentType, String customFileName) {
        if (key != null) {
            mFileParams.put(key, new FileWrapper(file, contentType, customFileName));
        }

        return (R)this;
    }

    /**
     * add inputstream params.
     * @param key key
     * @param stream inputStream
     * @return {@link RestVolleyRequestWithBody}
     */
    public R addParams(String key, InputStream stream) {
        addParams(key, stream, null);

        return (R)this;
    }

    /**
     * add inputstream params.
     * @param key key
     * @param stream InputStream
     * @param name name
     * @return {@link RestVolleyRequestWithBody}
     */
    public R addParams(String key, InputStream stream, String name) {
        addParams(key, stream, name, null);

        return (R)this;
    }

    /**
     * add inputstream params.
     * @param key key
     * @param stream stream
     * @param name name
     * @param contentType content type
     * @return {@link RestVolleyRequestWithBody}
     */
    public R addParams(String key, InputStream stream, String name, String contentType) {
        addParams(key, stream, name, contentType, true);

        return (R)this;
    }

    /**
     * add input stream params.
     * @param key key
     * @param stream input stream
     * @param name name
     * @param contentType content type
     * @param autoClose auto close or not
     * @return {@link RestVolleyRequestWithBody}
     */
    public R addParams(String key, InputStream stream, String name, String contentType, boolean autoClose) {
        if (key != null && stream != null) {
            mStreamParams.put(key, StreamWrapper.newInstance(stream, name, contentType, autoClose));
        }
        return (R)this;
    }

    private HttpEntity createJsonStreamerEntity(String elapsedFieldInJsonStreamer) throws IOException {
        JsonStreamerEntity entity = new JsonStreamerEntity(!mFileParams.isEmpty() || !mStreamParams.isEmpty(), elapsedFieldInJsonStreamer);

        // Add string params
        for (ConcurrentHashMap.Entry<String, String> entry : mUrlParams.entrySet()) {
            entity.addPart(entry.getKey(), entry.getValue());
        }

        // Add file params
        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : mFileParams.entrySet()) {
            entity.addPart(entry.getKey(), entry.getValue());
        }

        // Add stream params
        for (ConcurrentHashMap.Entry<String, StreamWrapper> entry : mStreamParams.entrySet()) {
            StreamWrapper stream = entry.getValue();
            if (stream.inputStream != null) {
                entity.addPart(entry.getKey(), StreamWrapper.newInstance(stream.inputStream, stream.name, stream.contentType, stream.autoClose));
            }
        }

        return entity;
    }

    private HttpEntity createFormEntity() {
        try {
            return new UrlEncodedFormEntity(getParamsList(), mCharset);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpEntity createMultipartEntity(boolean isRepeatable) throws IOException {
        SimpleMultipartEntity entity = new SimpleMultipartEntity();
        entity.setIsRepeatable(isRepeatable);

        // Add string params
        for (ConcurrentHashMap.Entry<String, String> entry : mUrlParams.entrySet()) {
            entity.addPartWithCharset(entry.getKey(), entry.getValue(), mCharset);
        }

        // Add stream params
        for (ConcurrentHashMap.Entry<String, StreamWrapper> entry : mStreamParams.entrySet()) {
            StreamWrapper stream = entry.getValue();
            if (stream.inputStream != null) {
                entity.addPart(entry.getKey(), stream.name, stream.inputStream, stream.contentType);
            }
        }

        // Add file params
        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : mFileParams.entrySet()) {
            FileWrapper fileWrapper = entry.getValue();
            entity.addPart(entry.getKey(), fileWrapper.file, fileWrapper.contentType, fileWrapper.customFileName);
        }

        return entity;
    }

    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

        for (ConcurrentHashMap.Entry<String, String> entry : mUrlParams.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return lparams;
    }
}