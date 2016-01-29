package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liuxiaoming on 2014/11/4.
 */
public class ThirdPartLoginInfo implements Serializable {

    @SerializedName("require_modify_username")
    private boolean mRequreModifyUserName;

    @SerializedName("is_first_login")
    private boolean mIsFirstLogin;

    @SerializedName("access_token")
    private String mAccessToken;

    public void setUserName(String userName) {
        mUserName = userName;
    }

    @SerializedName("user_name")
    private String mUserName;

    @SerializedName("avatar")
    private String mAvatar;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("mobile")
    private String mMobile;

    @SerializedName("user_id")
    private int mUserID;

    @SerializedName("third_party_nick_name")
    private String mThirdPartNickName;

    public boolean isRequreModifyUserName() {
        return mRequreModifyUserName;
    }

    public boolean isFirstLogin() {
        return mIsFirstLogin;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getMobile() {
        return mMobile;
    }

    public String getThirdPartNickName() {
        return mThirdPartNickName;
    }

    public int getUserID() {
        return mUserID;
    }

    public void setUserID(int userID) {
        mUserID = userID;
    }
}
