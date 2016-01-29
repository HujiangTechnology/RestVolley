/*
 * RefreshTokenResult      2015-06-10
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;
import com.hujiang.normandy.api.BaseModel;

import java.io.Serializable;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-06-10
 */
public class RefreshTokenResult extends BaseModel {

    @SerializedName("data")
    private RefreshTokenInfo mRefreshTokenInfo = new RefreshTokenInfo();

    public String getRefreshToken() {
        return mRefreshTokenInfo.getRefreshToken();
    }

    public String getAccessToken() {
        return mRefreshTokenInfo.getAccessToken();
    }

    public long getExpireIn() {
        return mRefreshTokenInfo.getExpireIn();
    }

    public static class RefreshTokenInfo implements Serializable {

        @SerializedName("refresh_token")
        private String mRefreshToken;

        @SerializedName("access_token")
        private String mAccessToken;

        @SerializedName("expire_in")
        private long mExpireIn;

        public String getRefreshToken() {
            return mRefreshToken;
        }

        public String getAccessToken() {
            return mAccessToken;
        }

        public long getExpireIn() {
            return mExpireIn;
        }
    }
}