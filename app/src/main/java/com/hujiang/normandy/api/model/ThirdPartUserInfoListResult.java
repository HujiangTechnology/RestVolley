package com.hujiang.normandy.api.model;

import com.google.gson.annotations.SerializedName;
import com.hujiang.normandy.api.BaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 2014-11-13 by heliangwei
 */

public class ThirdPartUserInfoListResult extends BaseModel {
	private static final long serialVersionUID = 6291475118870379836L;

	@SerializedName("data")
	private List<ThirdPartUserInfo> mThirdPartUserInfo = new ArrayList<ThirdPartUserInfo>();

	public List<ThirdPartUserInfo> getThirdPartUserInfo() {
		return mThirdPartUserInfo;
	}

	public void setThirdPartUserInfo(List<ThirdPartUserInfo> mThirdPartUserInfo) {
		this.mThirdPartUserInfo = mThirdPartUserInfo;
	}


}
