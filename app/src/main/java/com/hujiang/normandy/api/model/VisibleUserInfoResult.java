/*
 * ExtraUserInfoResult      2015-07-02
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;
import com.hujiang.normandy.api.BaseModel;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-07-02
 */
public class VisibleUserInfoResult extends BaseModel {

    @SerializedName("data")
    private UserInfo mUserInfo;

    public UserInfo getUserInfo() {
        return mUserInfo;
    }
}