package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;
import com.hujiang.normandy.api.BaseModel;

import java.io.Serializable;

/**
 * Created by liuxiaoming on 2014/11/4.
 */
public class ChangePasswordResult extends BaseModel {

    @SerializedName("data")
    private ChangePasswordInfo mChangePasswordInfo = new ChangePasswordInfo();


    public boolean isSuccess() {
        return mChangePasswordInfo.mIsSuccess;
    }

    public String getAccessToken() {
        return mChangePasswordInfo.mAccessToken;
    }

    private class ChangePasswordInfo implements Serializable {

        @SerializedName("success")
        private boolean mIsSuccess;

        @SerializedName("access_token")
        private String mAccessToken;
    }
}
