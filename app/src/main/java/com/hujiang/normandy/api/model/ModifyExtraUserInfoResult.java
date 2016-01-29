/*
 * ModifyExtraUserInfoResult      2015-07-02
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
 * @since 2015-07-02
 */
public class ModifyExtraUserInfoResult extends BaseModel {

    @SerializedName("data")
    private Extra mExtra = new Extra();

    public boolean isModifyNickNameSuccessful() {
        return mExtra.getModifyNickNameResult().isSuccessful();
    }

    public boolean isModifyBirthdaySuccessful() {
        return mExtra.getModifyBirthdayResult().isSuccessful();
    }

    public boolean isModifyGenderSuccessful() {
        return mExtra.getModifyGenderResult().isSuccessful();
    }

    public boolean isModifySignatureSuccessful() {
        return mExtra.getModifySignatureResult().isSuccessful();
    }

    public static class Extra implements Serializable {

        @SerializedName("nick_name_status")
        private ModifyResultInfo mModifyNickNameResult = new ModifyResultInfo();
        @SerializedName("birthday_status")
        private ModifyResultInfo mModifyBirthdayResult = new ModifyResultInfo();
        @SerializedName("gender_status")
        private ModifyResultInfo mModifyGenderResult = new ModifyResultInfo();
        @SerializedName("signature_status")
        private ModifyResultInfo mModifySignatureResult = new ModifyResultInfo();

        public ModifyResultInfo getModifyNickNameResult() {
            return mModifyNickNameResult;
        }

        public ModifyResultInfo getModifyBirthdayResult() {
            return mModifyBirthdayResult;
        }

        public ModifyResultInfo getModifyGenderResult() {
            return mModifyGenderResult;
        }

        public ModifyResultInfo getModifySignatureResult() {
            return mModifySignatureResult;
        }
    }

    public static class ModifyResultInfo implements Serializable {

        @SerializedName("success")
        private boolean mIsSuccessful;

        public boolean isSuccessful() {
            return mIsSuccessful;
        }
    }
}