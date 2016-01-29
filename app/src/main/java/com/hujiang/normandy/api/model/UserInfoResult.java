package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;
import com.hujiang.normandy.api.BaseModel;

/**
 * Created by liuxiaoming on 2014/11/4.
 */
public class UserInfoResult extends BaseModel {

    public static final int LOGIN_TYPE_ACCOUNT = 1000;
    public static final int LOGIN_TYPE_SMS = 1001;

    @SerializedName("data")
    private UserInfo mUserInfo;

    public UserInfo getUserInfo() {
        return mUserInfo;
    }
}
