/*
 * DemoApplication      2015-12-02
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.hujiang.normandy;

import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.hujiang.account.AccountManager;
import com.hujiang.account.AccountRunTime;
import com.hujiang.android.common.app.BaseApplication;
import com.hujiang.android.common.store.ObjectCache;
import com.hujiang.android.common.utils.LogUtils;
import com.hujiang.android.common.utils.NetworkUtils;
import com.hujiang.android.common.utils.ToastUtils;
import com.hujiang.framework.app.RunTimeManager;
import com.hujiang.framework.preference.PreferenceHelper;
import com.hujiang.imagerequest.HJImageLoader;
import com.hujiang.league.api.LeagueApiUtils;
import com.hujiang.league.utils.CircleUtil;
import com.hujiang.social.sdk.SocialSDK;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-12-02
 */
public class DemoApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        LogUtils.init(BuildConfig.DEBUG);
//        //BI 模块
//        AnalyticsAgent.init(this);
//
//        MobclickAgent.openActivityDurationTrack(false);

        // 注册bi实现监听回调
//        BIIntruder.instance().setHandler(new BIHandlerImpl());

        NetworkUtils.init(this);
        com.hujiang.android.common.preference.PreferenceHelper.init(this);
        CircleUtil.toggleClubInCircleHome(false);//社团首页是否加载"精品社刊"模块
        ToastUtils.init(this);
        ObjectCache.getInstance().init(this);
        HJImageLoader.initImageLoader(this);

        LeagueApiUtils.setNetworkProtocol(LeagueApiUtils.HTTPS);
        //初始化用户中心模块
        RunTimeManager.instance().init(this, "1.0", "hujiang");
        RunTimeManager.instance().load(AccountRunTime.class, AccountRunTime.instance());
        SocialSDK.setQQKey("1103425241", "all");
        SocialSDK.setWeiboKey("1291126770", "follow_app_official_microblog", "http://www.hujiang.com");
        SocialSDK.setWeixinKey("wxf81e2766c00e6dbc", "snsapi_userinfo", "c422dd21b01af643aa90da78af7aea53");

        AccountRunTime.instance().setLoginKey("e2a5bd46c1ae0d075b054027f68ec17a");
        AccountRunTime.instance().setLoginSecret("e208f86c1caeec92896acb4e4dca74bc");
        AccountRunTime.instance().setRegisterSource("a_hujiang");

        CookieSyncManager.createInstance(getApplicationContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);

        PreferenceHelper.init(this);

        AccountManager.instance().closeInterest();
        AccountManager.instance().openTrial();

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }
}