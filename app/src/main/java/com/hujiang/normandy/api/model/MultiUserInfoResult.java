/*
 * MultiUserInfoResult      2015-07-02
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;
import com.hujiang.normandy.api.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-07-02
 */
public class MultiUserInfoResult extends BaseModel {

    @SerializedName("data")
    private List<UserInfo> mUserInfos = new ArrayList<UserInfo>();
}