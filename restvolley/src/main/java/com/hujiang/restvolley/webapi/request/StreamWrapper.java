/*
 * StreamWrapper1      2015-12-16
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.webapi.request;

import com.hujiang.restvolley.RestVolley;

import java.io.InputStream;

/**
 * stream wrapper.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-12-16
 */
public class StreamWrapper {
    /**
     * inputStream.
     */
    public final InputStream inputStream;
    /**
     * stream name.
     */
    public final String name;
    /**
     * content type.
     */
    public final String contentType;
    /**
     * auto close or not.
     */
    public final boolean autoClose;

    /**
     * constructor.
     * @param inputStream InputStream.
     * @param name stream name.
     * @param contentType Content Type.
     * @param autoClose auto close or not.
     */
    public StreamWrapper(InputStream inputStream, String name, String contentType, boolean autoClose) {
        this.inputStream = inputStream;
        this.name = name;
        this.contentType = contentType;
        this.autoClose = autoClose;
    }

    /**
     * new StreamWrapper instance.
     * @param inputStream InputStream.
     * @param name name.
     * @param contentType Content Type.
     * @param autoClose auto close or not.
     * @return {@link StreamWrapper}
     */
    public static StreamWrapper newInstance(InputStream inputStream, String name, String contentType, boolean autoClose) {
        return new StreamWrapper(
                inputStream
                , name
                , contentType == null ? RestVolley.APPLICATION_OCTET_STREAM : contentType
                , autoClose);
    }
}