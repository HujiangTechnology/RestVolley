package com.hujiang.restvolley.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hujiang.restvolley.webapi.RestVolleyCallback;
import com.hujiang.restvolley.webapi.request.GetRequest;

import java.util.Map;

public class RestfulAPIActivity extends AppCompatActivity {

    private static final String TAG = RestfulAPIActivity.class.getSimpleName();

    private static final String[] CONCURRENCE_URLS = {"http://www.baidu.com"
            , "http://stackoverflow.com/"
            , "http://www.hujiang.com"
            , "https://bintray.com/"
            , "http://www.infoq.com/cn/"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restful_api);

        for (String s : CONCURRENCE_URLS) {
            new GetRequest(RestfulAPIActivity.this).url(s).execute(new RestVolleyCallback<String>() {
                @Override
                public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                    Log.i(TAG, statusCode + "::" + data);
                }

                @Override
                public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                    Log.i(TAG, statusCode + "::" + message);
                }
            });
        }
    }
}
