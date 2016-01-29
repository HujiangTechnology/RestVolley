package com.hujiang.normandy.api.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hujiang.account.Prefs;
import com.hujiang.framework.preference.PreferenceHelper;

import java.io.Serializable;

/**
 * 2014-10-27 by heliangwei
 */

public class UserInfo implements Serializable {

    public static final UserInfo NULL = new UserInfo();

    @SerializedName("access_token")
	private String mAccessToken;

    @SerializedName("expire_in")
	private long mExpireIn;

    @SerializedName("user_id")
	private long mUserId;

    @SerializedName("user_name")
	private String mUserName = "";

    @SerializedName("avatar")
	private String mAvatar = "";

    @SerializedName("email")
	private String mEmail = "";

    @SerializedName("mobile")
	private String mMobile = "";

    @SerializedName("status")
    private int mStatus;

    @SerializedName("refresh_token")
    private String mRefreshToken;

    @SerializedName("union_id")
    private String mUionId = "";

    @SerializedName("is_anonymous")
    private boolean mIsGuest;

    @SerializedName("nick_name")
    private String mNickName = "";

    @SerializedName("signature")
    private String mSignature = "";

    @SerializedName("group_id")
    private int mGroupId;

    private String mAvatarTimeStamp;

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    public long getExpireIn() {
        return mExpireIn;
    }

    public void setExpireIn(long expireIn) {
        mExpireIn = expireIn;
    }

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long userId) {
        mUserId = userId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getAvatar() {
        if (mAvatarTimeStamp == null){
            mAvatarTimeStamp = PreferenceHelper.getString(Prefs.AVATAR_TIME_STAMP, String.valueOf(System.currentTimeMillis()));
            PreferenceHelper.putString(Prefs.AVATAR_TIME_STAMP, mAvatarTimeStamp);
        }
        return TextUtils.isEmpty(mAvatar) ? mAvatar
                : new StringBuilder(mAvatar).append("?timestamp=").append(mAvatarTimeStamp).toString();
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getMobile() {
        return mMobile;
    }
    public void setMobile(String mobile) {
        mMobile = mobile;
    }

    public void setAvatarTimeStamp(String avatarTimeStamp) {
        mAvatarTimeStamp = avatarTimeStamp;
        PreferenceHelper.putString(Prefs.AVATAR_TIME_STAMP, mAvatarTimeStamp);
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        mRefreshToken = refreshToken;
    }

    public String getUionId() {
        return mUionId;
    }

    public void setUionId(String uionId) {
        mUionId = uionId;
    }

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String nickName) {
        mNickName = nickName;
    }

    public int getGroupId() {
        return mGroupId;
    }

    public void setGroupId(int groupId) {
        mGroupId = groupId;
    }

    public String getSignature() {
        return mSignature;
    }

    public void setSignature(String signature) {
        mSignature = signature;
    }

    public boolean isGuest() {
        return mIsGuest;
    }

    public UserInfo() {

    }

    public static UserInfo from(ThirdPartLoginInfo info) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(info.getUserID());
        userInfo.setUserName(info.getUserName());
        userInfo.setAccessToken(info.getAccessToken());
        userInfo.setAvatar(info.getAvatar());
        userInfo.setEmail(info.getEmail());
        userInfo.setMobile(info.getMobile());
        return userInfo;
    }

    @Override
    public String toString() {
        return "JSUserInfo{" +
                "mAccessToken='" + mAccessToken + '\'' +
                ", mExpireIn=" + mExpireIn +
                ", mUserId=" + mUserId +
                ", mUserName='" + mUserName + '\'' +
                ", mAvatar='" + mAvatar + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mMobile='" + mMobile + '\'' +
                ", mStatus=" + mStatus +
                ", mRefreshToken='" + mRefreshToken + '\'' +
                ", mUionId='" + mUionId + '\'' +
                ", mIsGuest=" + mIsGuest +
                ", mNickName='" + mNickName + '\'' +
                ", mSignature='" + mSignature + '\'' +
                ", mGroupId=" + mGroupId +
                ", mAvatarTimeStamp='" + mAvatarTimeStamp + '\'' +
                '}';
    }
}
