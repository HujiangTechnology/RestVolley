package com.hujiang.restvolley.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_restful_api).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RestfulAPIActivity.class));
            }
        });

        findViewById(R.id.btn_local_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LocalImageListActivity.class));
            }
        });

        findViewById(R.id.btn_net_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NetImageListActivity.class));
            }
        });

        findViewById(R.id.btn_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DownloadActivity.class));
            }
        });

        findViewById(R.id.btn_sync_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SyncRequestActivity.class));
            }
        });

        findViewById(R.id.btn_https_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HTTPSActivity.class));
            }
        });

        Log.i(TAG, "CPU_COUNT:" + Runtime.getRuntime().availableProcessors());
    }
}
