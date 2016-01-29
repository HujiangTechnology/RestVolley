/*
 * LeagueActivity      2015-12-28
 * Copyright (c) 2015 hujiang Co.Ltd. All right reserved(http://www.hujiang.com).
 * 
 */
package com.hujiang.normandy;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.hujiang.league.app.topic.MyCirclesFragment;

/**
 * class description here
 *
 * @author simon
 * @version 1.0.0
 * @since 2015-12-28
 */
public class LeagueActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_league_layout);

        MyCirclesFragment fragment = (MyCirclesFragment) MyCirclesFragment.instantiate(this, MyCirclesFragment.class.getName());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, fragment);
        transaction.addToBackStack(null).commitAllowingStateLoss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}