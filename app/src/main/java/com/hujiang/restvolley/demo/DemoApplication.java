/*
 * DemoApplication      2015-12-02
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.hujiang.restvolley.demo;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
//import android.support.multidex.MultiDex;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-12-02
 */
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

//        MultiDex.install(this);
    }
}