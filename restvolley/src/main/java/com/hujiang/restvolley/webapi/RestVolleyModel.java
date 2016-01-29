/*
 * BaseAPIModel      2015-12-10
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */

package com.hujiang.restvolley.webapi;

import java.io.Serializable;

/**
 * RestVolley Model.
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-12-10
 */
public abstract class RestVolleyModel implements Serializable {
    /**
     * invalid code.
     */
    public static final int INVALID_CODE = Integer.MIN_VALUE;

    /**
     * get response data code.
     * @return int code.
     */
    public abstract int getCode();

    /**
     * get response data message.
     * @return response data message.
     */
    public abstract String getMessage();

    /**
     * success code.
     * @return success code.
     */
    public abstract int successCode();

    /**
     * set code.
     * @param code code.
     */
    public abstract void setCode(int code);

    /**
     * set message.
     * @param message message.
     */
    public abstract void setMessage(String message);

}