package com.hujiang.restvolley.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.hujiang.restvolley.webapi.RestVolleyCallback;
import com.hujiang.restvolley.webapi.request.GetRequest;

import java.util.Map;

public class RestfulAPIActivity extends AppCompatActivity {

    private static final String TAG = RestfulAPIActivity.class.getSimpleName();

    private static final String[] CONCURRENCE_URLS = {
            "https://qastudy.hjapi.com/api/v1/galleries/index"
            , "https://www.google.co.in/"
            , "http://www.baidu.com"
            , "http://stackoverflow.com/"
            , "http://www.hujiang.com"
            , "https://bintray.com/"
            , "http://www.infoq.com/cn/"
            ,"https://mobile.hjapi.com/mobileapp/appUpmobile.ashx"
    };

    private LinearLayout mRestGroup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restful_api);

        mRestGroup = (LinearLayout) findViewById(R.id.rest_group);

        findViewById(R.id.btn_restful_api).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (final String s : CONCURRENCE_URLS) {
                    new GetRequest(RestfulAPIActivity.this)
                            .url(s)
                            .setTag(s)
                            .setTimeout(5000)
                            .setShouldCache(false)
                            .setRetryPolicy(new DefaultRetryPolicy(2000, 3, 1.0f))
                            .execute(String.class, new RestVolleyCallback<String>() {
                                @Override
                                public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                    StringBuilder builder = new StringBuilder();
                                    builder.append("\n+++++++++++++++++++++++++++++\n")
                                            .append(s).append("::").append(statusCode).append(":SUCCESSFUL:").append('\n');
                                    TextView textView = new TextView(RestfulAPIActivity.this);
                                    textView.setText(builder.toString());
                                    mRestGroup.addView(textView);
                                }

                                @Override
                                public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                    StringBuilder builder = new StringBuilder();
                                    builder.append("\n+++++++++++++++++++++++++++++\n")
                                            .append(s).append("::").append(statusCode).append(":FAILURE:").append('\n').append(data).append("::\n").append(message);
                                    TextView textView = new TextView(RestfulAPIActivity.this);
                                    textView.setText(builder.toString());
                                    mRestGroup.addView(textView);
                                }
                            });
                }
            }
        });
    }
}
