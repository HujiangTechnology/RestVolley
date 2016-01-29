package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * 2014-11-4 by heliangwei
 */

public class ThirdPartUserInfo implements Serializable {

    @SerializedName("third_party")
    private int mThirdParty; // 第三方的识别号，qq - 1000 新浪微博- 1001 微信 - 1002

    @SerializedName("user_name")
    private String mUserName;

    @SerializedName("avatar")
    private String mAvatar;

    @SerializedName("open_id")
    private String mOpenId;

    @SerializedName("third_party_access_token")
    private String mThirdPartyAccessToken;

    @SerializedName("access_token")
    private String mAccessToken; // 沪江账号 token

    public int getThirdParty() {
        return mThirdParty;
    }

    public void setThirdParty(int thirdParty) {
        this.mThirdParty = thirdParty;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        this.mAvatar = avatar;
    }

    public String getOpenId() {
        return mOpenId;
    }

    public void setOpenId(String openId) {
        mOpenId = openId;
    }

    public String getThirdPartyAccessToken() {
        return mThirdPartyAccessToken;
    }

    public void setThirdPartyAccessToken(String thirdPartyAccessToken) {
        mThirdPartyAccessToken = thirdPartyAccessToken;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

}
