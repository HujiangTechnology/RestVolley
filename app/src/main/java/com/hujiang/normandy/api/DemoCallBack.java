package com.hujiang.normandy.api;

import android.content.Context;
import android.text.TextUtils;

import com.hujiang.account.AccountRunTime;
import com.hujiang.account.R;
import com.hujiang.common.util.NetworkUtils;
import com.hujiang.common.util.ToastUtils;
import com.hujiang.restvolley.webapi.RestVolleyCallback;
import com.hujiang.restvolley.webapi.request.RestVolleyRequest;

import org.apache.http.HttpStatus;

import java.util.Map;

/**
 * 2014-11-13 by heliangwei
 */
public abstract class DemoCallBack<Data extends BaseModel> extends RestVolleyCallback<Data> {
	@Override
	public void onStart(RestVolleyRequest request) {
		super.onStart(request);
	}

	@Override
	public void onFinished(RestVolleyRequest request) {
		super.onFinished(request);
	}

	@Override
	public void onSuccess(int i, Data data, Map<String, String> map, boolean b, long l, String s) {
		onSuccessEvent(i, data, map, b, l, s);
	}

	@Override
	public void onFail(int i, Data data, Map<String, String> map, boolean b, long l, String s) {
		if (!beforeFail(data, i)) {
			onFailEvent(i, data, map, b, l, s);
		}
	}

	public abstract void onFailEvent(int i, Data data, Map<String, String> map, boolean b, long l, String s);
	public abstract void onSuccessEvent(int i, Data data, Map<String, String> map, boolean b, long l, String s);

	private boolean beforeFail(Data data, int httpStatus) {
		Context context = AccountRunTime.instance().getContext();
		int code = data.getCode();
		if (!NetworkUtils.isNetWorkAvailable(context)) {
			ToastUtils.show(context, R.string.account_code_no_network);
		} else if (httpStatus == HttpStatus.SC_BAD_REQUEST) {
			ToastUtils.show(context, R.string.account_code_token_error);
		} else if (code == ResponseCode.CODE_ACCESS_DENY) {
			ToastUtils.show(context, R.string.account_code_access_deny);
		} else if (code == ResponseCode.CODE_ANONYMOUS_USER_DISALLOWED) {
			ToastUtils.show(context, R.string.account_code_anonymous_user_disallowed);
		} else if (code == ResponseCode.CODE_EXCEED_QUOTA) {
			ToastUtils.show(context, R.string.account_code_exceed_quota);
		} else if (code == ResponseCode.CODE_INVALID_ACCESS_TOKEN) {
			ToastUtils.show(context, R.string.account_code_invalid_access_token);
		} else if (code == ResponseCode.CODE_NO_ACTIVE) {
			ToastUtils.show(context, R.string.account_code_no_active);
		} else if (code == ResponseCode.CODE_UNKNOWN_ERROR) {
			ToastUtils.show(context, R.string.account_code_unknown_error);
		} else {
			String msg = data.getMessage();
			if (TextUtils.isEmpty(msg)) {
				ToastUtils.show(context, R.string.account_code_no_network);
			} else {
				ToastUtils.show(context, msg);
			}
		}
		return false;
	}

//	@Override
//	protected boolean isTokenInValid(Data data, int httpStatus) {
//		return data.getCode() == AccountResponseCode.CODE_INVALID_ACCESS_TOKEN;
//	}
}
