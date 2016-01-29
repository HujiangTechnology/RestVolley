package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;
import com.hujiang.normandy.api.BaseModel;

import java.io.Serializable;

/**
 * Created by liuxiaoming on 2014/11/4.
 */
public class ResetPasswordResult extends BaseModel {

    @SerializedName("data")
    private ResetPasswordInfo mResetPasswordInfo = new ResetPasswordInfo();

    public String getMobile() {
        return mResetPasswordInfo.mMobile;
    }

    public String getEmail() {
        return mResetPasswordInfo.mEmail;
    }

    public String getValidToken() {
        return mResetPasswordInfo.mValidToken;
    }

    public long getUserId() {
        return mResetPasswordInfo.mUserId;
    }

    public String getUserName() {
        return mResetPasswordInfo.mUserName;
    }

    private class ResetPasswordInfo implements Serializable {

        @SerializedName("mobile")
        private String mMobile;

        @SerializedName("email")
        private String mEmail;

        @SerializedName("valid_token")
        private String mValidToken;

        @SerializedName("user_id")
        private long mUserId;

        @SerializedName("user_name")
        private String mUserName;
    }
}
