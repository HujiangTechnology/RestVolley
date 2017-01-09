/*
 * UserInfo      2017-01-06
 * Copyright (c) 2017 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.hujiang.restvolley.demo.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2017-01-06
 */
public class UserInfo implements Serializable {

    @SerializedName("user_id")
    private String mUserId;
    @SerializedName("avatar")
    private String mAvatar;
    @SerializedName("name")
    private String mName;


    public String getUserId() {
        return mUserId;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public String getName() {
        return mName;
    }
}