package com.hujiang.normandy.api;

import com.google.gson.annotations.SerializedName;
import com.hujiang.restvolley.webapi.RestVolleyModel;

/**
 * Created by liuxiaoming on 2014/11/4.
 */
public abstract class BaseModel extends RestVolleyModel {

	@SerializedName("code")
    private int mCode;

    @SerializedName("message")
    private String mMessage;

    public int getCode() {
        return mCode;
    }

    @Override
    public String getMessage() {
        return mMessage;
    }

    @Override
    public int successCode() {
        return ResponseCode.CODE_SUCCESS;
    }

    @Override
    public void setCode(int i) {
        mCode = i;
    }

    @Override
    public void setMessage(String s) {
        mMessage = s;
    }
}
