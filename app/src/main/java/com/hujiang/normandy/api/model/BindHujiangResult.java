package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;
import com.hujiang.normandy.api.BaseModel;

import java.io.Serializable;

/**
 * Created by liuxiaoming on 2014/11/4.
 */
public class BindHujiangResult extends BaseModel {

    @SerializedName("data")
    private BindHujiangInfo mBindHujiangInfo = new BindHujiangInfo();

    public UserInfo getUserInfo() {
        mBindHujiangInfo.mUserInfo.setAccessToken(mBindHujiangInfo.mAccessToken);
        return mBindHujiangInfo.mUserInfo;
    }

    private class BindHujiangInfo implements Serializable {

        @SerializedName("access_token")
        private String mAccessToken;

        @SerializedName("user_info")
        private UserInfo mUserInfo;
    }
}
