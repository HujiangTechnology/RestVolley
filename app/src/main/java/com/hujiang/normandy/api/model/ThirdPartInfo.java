package com.hujiang.normandy.api.model;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.hujiang.social.sdk.SocialSDK;

import java.io.Serializable;

/**
 * Created by liuxiaoming on 2014/11/4.
 */
public class ThirdPartInfo implements Serializable {

    public static final int AUTH_TYPE_SSO = 1000;
    public static final int AUTH_TYPE_AUTH = 1001;

    public static final int PLATFORM_QQ = 1000;
    public static final int PLATFORM_SINA = 1001;
    public static final int PLATFORM_WEIXIN = 1002;

    public static final String QQ = "qq";
    public static final String WE_CHAT = "wechat";
    public static final String WEIBO = "weibo";

    public static String getPlatformName(int platformValue) {
        String platformName;
        switch (platformValue) {
            case PLATFORM_SINA:
                platformName = WEIBO;
                break;
            case PLATFORM_WEIXIN:
                platformName = WE_CHAT;
                break;
            case PLATFORM_QQ:
                platformName = QQ;
                break;
            default:
                platformName = "";
        }
        return platformName;
    }

    public static int getValue(String platformName) {
        if (TextUtils.equals(platformName, WEIBO)) {
            return PLATFORM_SINA;
        } else if (TextUtils.equals(platformName, WE_CHAT)) {
            return PLATFORM_WEIXIN;
        } else if (TextUtils.equals(platformName, QQ)) {
            return PLATFORM_QQ;
        }
        return 0;
    }


    public static String getAppID(Context context, int platformValue) {
        String platformName;
        switch (platformValue) {
            case PLATFORM_SINA:
                platformName = SocialSDK.getWeiboKey(context);
                break;
            case PLATFORM_WEIXIN:
                platformName = SocialSDK.getWeixinKey(context);
                break;
            case PLATFORM_QQ:
                platformName = SocialSDK.getQQKey(context);
                break;
            default:
                platformName = "";
        }
        return platformName;
    }

    @SerializedName("third_party")
    private int mThirdPart;

    @SerializedName("user_name")
    private String mUserName;

    @SerializedName("avatar")
    private String mAvatar;

    public int getThirdPart() {
        return mThirdPart;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getAvatar() {
        return mAvatar;
    }
}
