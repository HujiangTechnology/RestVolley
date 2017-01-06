package com.hujiang.restvolley.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hujiang.restvolley.webapi.RestVolleyCallback;
import com.hujiang.restvolley.webapi.request.GetRequest;

import java.util.Map;

public class HTTPSActivity extends AppCompatActivity {

    private TextView mRequestContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_https);

        mRequestContentView = (TextView)findViewById(R.id.txt_request_content);

        findViewById(R.id.btn_office_ca).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetRequest(HTTPSActivity.this).url("https://pass.hjapi.com/v1.1").execute(new RestVolleyCallback<String>() {
                    @Override
                    public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                        mRequestContentView.setText(statusCode + "===>" + data + "===>" + message);
                    }

                    @Override
                    public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                        mRequestContentView.setText(statusCode + "===>" + data + "===>" + message);
                    }
                });
            }
        });
        findViewById(R.id.btn_self_ca).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetRequest(HTTPSActivity.this).url("https://app.m.hjfile.cn/android/appsbar/").execute(new RestVolleyCallback<String>() {
                    @Override
                    public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                        mRequestContentView.setText(statusCode + "===>" + data + "===>" + message);
                    }

                    @Override
                    public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                        mRequestContentView.setText(statusCode + "===>" + data + "===>" + message);
                    }
                });
            }
        });
        findViewById(R.id.btn_no_ca).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetRequest(HTTPSActivity.this).url("http://api.mobile.hujiang.com/mobileapp/appUpmobile.ashx").execute(new RestVolleyCallback<String>() {
                    @Override
                    public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                        mRequestContentView.setText(statusCode + "===>" + data + "===>" + message);
                    }

                    @Override
                    public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                        mRequestContentView.setText(statusCode + "===>" + data + "===>" + message);
                    }
                });
            }
        });
    }
}
