package com.hujiang.restvolley.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hujiang.restvolley.Task;
import com.hujiang.restvolley.TaskScheduler;
import com.hujiang.restvolley.webapi.RestVolleyResponse;
import com.hujiang.restvolley.webapi.request.GetRequest;

public class SyncRequestActivity extends AppCompatActivity {

    private Button mButton;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_request);


        mButton = (Button)findViewById(R.id.btn_sync_request);
        mTextView = (TextView)findViewById(R.id.sync_request_content);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskScheduler.execute(new Task<Object, RestVolleyResponse<String>>(null) {
                    @Override
                    protected RestVolleyResponse<String> onDoInBackground(Object param) {
                        RestVolleyResponse<String> restVolleyResponse = new GetRequest(SyncRequestActivity.this)
                                .url("http://www.baidu.com")
                                .syncExecute();
                        return restVolleyResponse;
                    }

                    @Override
                    protected void onPostExecuteForeground(RestVolleyResponse<String> result) {
                        mTextView.setText(result.statusCode + ":::" + result.data + ":::" + result.message);
                    }
                });


            }
        });
    }
}
