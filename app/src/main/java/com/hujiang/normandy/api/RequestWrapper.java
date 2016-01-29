package com.hujiang.normandy.api;

import android.text.TextUtils;

import com.hujiang.account.AccountRunTime;
import com.hujiang.common.util.ArrayUtils;
import com.hujiang.common.util.DeviceUtils;
import com.hujiang.common.util.SecurityUtils;
import com.hujiang.framework.app.RunTimeManager;
import com.hujiang.restvolley.webapi.request.RestVolleyRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by liuxiaoming on 2014/11/4.
 */
public class RequestWrapper {

    private static final String PARAM_APP_KEY = "hj_appkey";
    private static final String PARAM_APP_SIGN = "hj_appsign";
    private static final String PARAM_SIGN_METHOD = "hj_signmethod";
    private static final String PARAM_DEVICE_ID = "hj_deviceId";

    private RestVolleyRequest mAPIRequest;

    public RequestWrapper(RestVolleyRequest apiRequest) {
        mAPIRequest = apiRequest;

        mAPIRequest.addHeaders(buildAccountAPIRequestHeaders(apiRequest.getUrlParams()));
    }

    public static Map<String, String> buildAccountAPIRequestHeaders(Map<String, String> requestParams) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(PARAM_APP_KEY, AccountRunTime.instance().getLoginKey());
        headers.put(PARAM_APP_SIGN, getSignString(requestParams));
        headers.put(PARAM_SIGN_METHOD, "md5");
        headers.put(PARAM_DEVICE_ID, DeviceUtils.getDeviceID(AccountRunTime.instance().getContext()));

        //general http headers
        String token = com.hujiang.account.AccountManager.instance().getUserToken();
        headers.put(RunTimeManager.KEY_HTTP_HEAD_USER_AGENT, RunTimeManager.instance().getUserAgent());
        headers.put(RunTimeManager.KEY_HTTP_HEAD_ACCESS_TOKEN, token);
        headers.put(RunTimeManager.KEY_HTTP_HEAD_DEVICE_ID, DeviceUtils.getDeviceID(AccountRunTime.instance().getContext()));

        return headers;
    }

    public RestVolleyRequest getWrappedRequest() {
        return mAPIRequest;
    }

    /**
     * 签名算法
     * 将所有请求参数按key做升序排列，排除掉值为空的key，然后用&符号连接得到string1，
     * 再将app_secret附到string1末尾得到string2，
     * 最后对string2做md5或sha1哈希，即为签名。
     */
    private static String getSignString(Map<String, String> requestParams) {
        String tempStr;

        if (requestParams == null || requestParams.isEmpty()) {
            tempStr = "";
        } else {
            List<String> data = new ArrayList<String>();
            Set<String> keys = requestParams.keySet();
            for (String key : keys) {
                String v = requestParams.get(key);
                if (!TextUtils.isEmpty(v)) {
                    data.add(key + "=" + v);
                }
            }

            tempStr = getDataSortLinkStrings(data);
        }

        return SecurityUtils.MD5.get32MD5String(tempStr + AccountRunTime.instance().getLoginSecret());
    }

    /**
     * 对数组中的内容进行排序后用&连接起来
     */
    private static String getDataSortLinkStrings(List<String> data) {
        if (ArrayUtils.isEmpty(data)) {
            return "";
        }

        Collections.sort(data);

        StringBuilder stringBuilder = new StringBuilder();
        for (String s : data) {
            stringBuilder.append(s).append('&');
        }

        return stringBuilder.substring(0, stringBuilder.length() - 1);
    }
}
