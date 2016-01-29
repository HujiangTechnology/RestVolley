package com.hujiang.normandy.api;

import android.content.Context;

import com.hujiang.account.AccountManager;
import com.hujiang.account.AccountRunTime;
import com.hujiang.common.util.DeviceUtils;
import com.hujiang.common.util.LogUtils;
import com.hujiang.framework.app.RunTimeManager;
import com.hujiang.framework.env.HJEnvironment;
import com.hujiang.restvolley.RestVolley;
import com.hujiang.restvolley.SSLManager;
import com.hujiang.restvolley.webapi.RestVolleyCallback;
import com.hujiang.restvolley.webapi.RestVolleyResponse;
import com.hujiang.restvolley.webapi.request.GetRequest;
import com.hujiang.restvolley.webapi.request.PostRequest;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuxiaoming on 2014/11/3.
 */
public class DemoAPI {

    private static final String ACCOUNT_URL_ALPHA = "http://beta.pass.hjapi.com/v1.1";
    private static final String ACCOUNT_URL_BETA = "http://yz.pass.hjapi.com/v1.1";
    private static final String ACCOUNT_URL_RELEASE = "https://pass.hjapi.com/v1.1";

    private static final String PATH_ANONYMOUS = "/Anonymous";
    private static final String PARAM_SOURCE = "source";

    private static String getURL() {
        HJEnvironment env = AccountRunTime.instance().getEnvironment();
        switch (env) {
            case ENV_ALPHA:
                return ACCOUNT_URL_ALPHA;
            case ENV_BETA:
                return ACCOUNT_URL_BETA;
            default:
                return ACCOUNT_URL_RELEASE;
        }
    }

    private static Context getContext() {
        return RunTimeManager.instance().getApplicationContext();
    }

    /**
     * 获取匿名账户
     *
     * @param callback 回调接口
     */
    public static void requestGuestAccount(RestVolleyCallback<String> callback) {
        new RequestWrapper(new PostRequest(getContext())
                .url(getURL(), PATH_ANONYMOUS)
                .addParams(PARAM_SOURCE, AccountRunTime.instance().getAppSource())).getWrappedRequest().execute(String.class, callback);
    }

    public static RestVolleyResponse<String> syncPostGuestAccount() {
        return new RequestWrapper(new PostRequest(getContext())
                .url(getURL(), PATH_ANONYMOUS)
                .addParams(PARAM_SOURCE, AccountRunTime.instance().getAppSource())).getWrappedRequest().syncExecute(String.class);
    }

    public static void getSquareToics(int offset, int pageSize, RestVolleyCallback<String> callback) {
        new GetRequest(getContext())
                .url("https://union.hjapi.com/Circle/SquareTopicList")
                .addHeaders(getGeneralHeaders())
                .addParams("start", offset)
                .addParams("limit", pageSize)
                .execute(String.class, callback);
    }

    public static void getDetail(long circleId, RestVolleyCallback<String> callback) {
        new GetRequest(getContext())
                .url("https://union.hjapi.com/Circle/Detail")
                .addHeaders(getGeneralHeaders())
                .addParams("circleId", circleId)
                .execute(String.class, callback);
    }

    public static void getTags(long circleId, RestVolleyCallback<String> callback) {
        new GetRequest(getContext())
                .url("https://union.hjapi.com/Circle/Topic/Tag")
                .addHeaders(getGeneralHeaders())
                .addParams("circleId", circleId)
                .execute(String.class, callback);
    }

    public static RestVolleyResponse<String> getTagSync(long circleId) {
        return new GetRequest(getContext())
                .url("https://union.hjapi.com/Circle/Topic/Tag")
                .addHeaders(getGeneralHeaders())
                .addParams("circleId", circleId)
                .syncExecute(String.class);
    }

    public static void requestUserHotCircles(int offset, int pageSize, String token, RestVolleyCallback<String> callback) {
        new GetRequest(getContext()).url("https://union.hjapi.com/User/Circles/Hot")
                .addHeaders(getGeneralHeaders())
                .addParams("start", offset)
                .addParams("top", 9)
                .addParams("limit", pageSize)
                .execute(String.class, callback);
//
    }

//    static SSLManager sslManager = new SSLManager();
    public static void requestRecommendCircles(int offset, int pageSize, String token, RestVolleyCallback<String> callback) {
        new GetRequest(getContext()).url("https://group.hjapi.com/v1/league/recommend")
                .addHeaders(getGeneralHeaders())
                .addParams("start", offset)
                .addParams("limit", pageSize)
                .execute(String.class, callback);

//        final Request request = new Request.Builder()
//                .get()
//                .url("https://group.hjapi.com/v1/league/recommend?start=0&limit=20")
//                .header("User-Agent", RunTimeManager.instance().getUserAgent())
//                .header("pragma-uuid", DeviceUtils.getDeviceID(getContext()))
//                .header("pragma-token", AccountManager.instance().getUserToken())
//                .build();

//        OkHttpClient okHttpClient = RestVolley.newRequestEngine(getContext(), RestVolley.TAG_REQUEST_ENGINE_DEFAULT).okHttpClient;
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                LogUtils.i("liuxiaoming", e.getMessage());
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                LogUtils.i("liuxiaoming", response.message());
//            }
//        });
    }

    public static Map<String, String> getGeneralHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", RunTimeManager.instance().getUserAgent());
        headers.put("pragma-uuid", DeviceUtils.getDeviceID(getContext()));
        headers.put("pragma-token", AccountManager.instance().getUserToken());

        return headers;
    }
}
