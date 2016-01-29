package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;
import com.hujiang.normandy.api.BaseModel;

import java.io.Serializable;

/**
 * Created by liuxiaoming on 2014/11/4.
 */
public class UpdateUserInfoResult extends BaseModel {

    @SerializedName("data")
    private UpdateUserInfo mUpdateUserInfo = new UpdateUserInfo();

    public boolean isUpdateUserNameSuccess() {
        return mUpdateUserInfo.mIsUpdateUserNameSucess;
    }

    public boolean isUpdateEmailSuccess() {
        return mUpdateUserInfo.mIsUpdateEmailSuccess;
    }

    private class UpdateUserInfo implements Serializable {

        @SerializedName("update_username_status")
        private boolean mIsUpdateUserNameSucess;

        @SerializedName("update_email_status")
        private boolean mIsUpdateEmailSuccess;
    }
}
