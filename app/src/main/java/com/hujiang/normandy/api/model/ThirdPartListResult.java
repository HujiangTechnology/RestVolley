package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;
import com.hujiang.normandy.api.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuxiaoming on 2014/11/4.
 */
public class ThirdPartListResult extends BaseModel {

    @SerializedName("data")
    private List<ThirdPartInfo> mThirdPartInfos = new ArrayList<ThirdPartInfo>();

    public List<ThirdPartInfo> getThirdPartInfos() {
        return mThirdPartInfos;
    }
}
