package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;
import com.hujiang.normandy.api.BaseModel;

/**
 * Created by liuxiaoming on 2014/11/4.
 */
public class ThirdPartLoginResult extends BaseModel {

    @SerializedName("data")
    private ThirdPartLoginInfo mThirdPartLoginInfo = new ThirdPartLoginInfo();

    public ThirdPartLoginInfo getThirdPartLoginInfo() {
        return mThirdPartLoginInfo;
    }
}
