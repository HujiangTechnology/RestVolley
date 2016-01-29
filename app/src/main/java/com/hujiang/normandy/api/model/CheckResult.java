package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;
import com.hujiang.normandy.api.BaseModel;

/**
 * Created by liuxiaoming on 2014/11/4.
 */
public class CheckResult extends BaseModel {

    @SerializedName("data")
    private boolean mIsValid;

    public boolean isValid() {
        return mIsValid;
    }
}
