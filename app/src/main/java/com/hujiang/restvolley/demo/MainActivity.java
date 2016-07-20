package com.hujiang.restvolley.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.hujiang.restvolley.TaskScheduler;
import com.hujiang.restvolley.image.ImageLoaderCompat;
import com.hujiang.restvolley.image.RestVolleyImageLoader;
import com.hujiang.restvolley.webapi.RestVolleyCallback;
import com.hujiang.restvolley.webapi.request.GetRequest;
import com.hujiang.restvolley.webapi.request.RestVolleyRequest;

import java.io.File;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    
    private Button mFileImgButton;
    private Button mAssetsImgButton;
    private Button mContentImgButton;
    private Button mDrawableImgButton;
    private Button mRawImgButton;
    private Button mDownloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFileImgButton = (Button)findViewById(R.id.file_img_btn);
        mAssetsImgButton = (Button)findViewById(R.id.asset_img_btn);
        mContentImgButton = (Button)findViewById(R.id.content_img_btn);
        mDrawableImgButton = (Button)findViewById(R.id.drawable_btn);
        mRawImgButton = (Button)findViewById(R.id.raw_btn);
        mDownloadButton = (Button)findViewById(R.id.download_btn);

        mFileImgButton.setOnClickListener(mOnClickListener);
        mAssetsImgButton.setOnClickListener(mOnClickListener);
        mContentImgButton.setOnClickListener(mOnClickListener);
        mDrawableImgButton.setOnClickListener(mOnClickListener);
        mRawImgButton.setOnClickListener(mOnClickListener);
        mDownloadButton.setOnClickListener(mOnClickListener);

        findViewById(R.id.display_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://st.hujiang.com/images/bg11_s.jpg";
                RestVolleyImageLoader.instance(MainActivity.this).displayImage(url, showImage(null));
            }
        });

        findViewById(R.id.load_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "http://i2.hjfile.cn/f96/68/57/27316857.jpg";
                RestVolleyImageLoader.instance(MainActivity.this).loadImage(url, new ImageLoaderCompat.ImageListener() {
                    @Override
                    public void onResponse(ImageLoaderCompat.ImageContainer response, boolean isImmediate) {
                        showImage(response.getBitmap());
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
            }
        });

        findViewById(R.id.sync_load_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskScheduler.execute(new Runnable() {
                    @Override
                    public void run() {
                        String url = "http://i2.hjfile.cn/f96/67/87/9686787.jpg";
                        final Bitmap bitmap = RestVolleyImageLoader.instance(MainActivity.this).syncLoadImage(url);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showImage(bitmap);
                            }
                        });
                    }
                });
            }
        });

        findViewById(R.id.concurrency_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url1 = "http://qa.study.hjapi.com/api/v1/tasks/4689/subtasks/148817/comments/summary";
                final String url2 = "http://qa.study.hjapi.com/api/v1/tasks/4689/subtasks/148817/mark";
                final String url3 = "http://qa.study.hjapi.com/api/v1/tasks/4689/subtasks/148833";
                final String token = "0001fdeda2.7fb73c81b172a5fa7b72086f28dd0b85";
                final String ua = "HJApp%201.0/android/Google%20Nexus%205X%20-%206.0.0%20-%20API%2023%20-%201080x1920/B6653C8DE0A466192FFDBFB/6.0/com.hujiang.normandy/2.7.0.392/hujiang/";
                final String uuid = "B6653C8DE0A466192FFDBFB";

                final RestVolleyRequest request = new GetRequest(MainActivity.this);
                request.setTag("MainActivity");
                request.setShouldCache(false);
                request.setTimeout(10000);
//                request.setRetryPolicy(new DefaultRetryPolicy(2000, 3, 1.0f));


                request.url(url1)
                        .addHeader("pragma-token", token)
                        .addHeader("userAgent", ua)
                        .addHeader("pragma-uuid", uuid)
                        .addParams("token", token).execute(new RestVolleyCallback<String>() {
                    @Override
                    public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                        Log.i("MainActivity", statusCode + ":1::::" + message);
                    }

                    @Override
                    public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                        Log.i("MainActivity", statusCode + ":1::::" + message);
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        request.url(url2)
                                .addHeader("pragma-token", token)
                                .addHeader("userAgent", ua)
                                .addHeader("pragma-uuid", uuid)
                                .addParams("token", token).execute(new RestVolleyCallback<String>() {
                            @Override
                            public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                Log.i("MainActivity", statusCode + ":2::::" + message);
                            }

                            @Override
                            public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                Log.i("MainActivity", statusCode + ":2::::" + message);
                            }
                        });

                        request.url(url3)
                                .addHeader("pragma-token", token)
                                .addHeader("userAgent", ua)
                                .addHeader("pragma-uuid", uuid)
                                .addParams("token", token).execute(new RestVolleyCallback<String>() {
                            @Override
                            public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                Log.i("MainActivity", statusCode + ":3::::" + message);
                            }

                            @Override
                            public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                Log.i("MainActivity", statusCode + ":3::::" + message);
                            }
                        });

                        request.url("http://www.baidu.com").execute(new RestVolleyCallback<String>() {
                            @Override
                            public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                Log.i("MainActivity", statusCode + ":4::::" + message);
                            }

                            @Override
                            public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                Log.i("MainActivity", statusCode + ":4::::" + message);
                            }
                        });

                        request.url("http://stackoverflow.com/").execute(new RestVolleyCallback<String>() {
                            @Override
                            public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                Log.i("MainActivity", statusCode + ":5::::" + message);
                            }

                            @Override
                            public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                Log.i("MainActivity", statusCode + ":5::::" + message);
                            }
                        });
                        request.url("http://www.cnbeta.com").execute(new RestVolleyCallback<String>() {
                            @Override
                            public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                Log.i("MainActivity", statusCode + ":6::::" + message);
                            }

                            @Override
                            public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                Log.i("MainActivity", statusCode + ":6::::" + message);
                            }
                        });
                        request.url("https://bintray.com/").execute(new RestVolleyCallback<String>() {
                            @Override
                            public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                Log.i("MainActivity", statusCode + ":7::::" + message);
                            }

                            @Override
                            public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                Log.i("MainActivity", statusCode + ":7::::" + message);
                            }
                        });
                        request.url("http://www.infoq.com/cn/").execute(new RestVolleyCallback<String>() {
                            @Override
                            public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                Log.i("MainActivity", statusCode + ":8::::" + message);
                            }

                            @Override
                            public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                                Log.i("MainActivity", statusCode + ":8::::" + message);
                            }
                        });
                    }
                }, 500);
            }
        });

        Log.i("MainActivity", "CPU_COUNT:" + Runtime.getRuntime().availableProcessors());
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Context context = MainActivity.this;
            if (id == R.id.file_img_btn) {
                String path = "/mnt/sdcard/img_default.jpg";
                if (!new File(path).exists()) {
                    Toast.makeText(MainActivity.this, "缺少磁盘文件/sdcard/img_default.jpg", Toast.LENGTH_LONG).show();
                    return;
                }
                path = "file://" + path;
                RestVolleyImageLoader.instance(context).displayImage(path, showImage(null));
            } else if (id == R.id.asset_img_btn) {
                String path = "assets://assets_default.png";
                RestVolleyImageLoader.instance(context).displayImage(path, showImage(null));
            } else if (id == R.id.content_img_btn) {
                String path = "content://media/external/images/media/27916";
                RestVolleyImageLoader.instance(context).displayImage(path, showImage(null));
            } else if (id == R.id.drawable_btn) {
                String path = "drawable://" + R.drawable.drawable_default;
                RestVolleyImageLoader.instance(context).displayImage(path, showImage(null));
            } else if (id == R.id.raw_btn) {
                String path = "drawable://" + R.raw.raw_default;
                RestVolleyImageLoader.instance(context).displayImage(path, showImage(null));
            } else if (id == R.id.download_btn) {
               startActivity(new Intent(MainActivity.this, DownloadActivity.class));
            }
        }
    };

    public void showMessage(CharSequence content) {
        new AlertDialog.Builder(this).setMessage(content).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    public ImageView showImage(Bitmap bitmap) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View dialogView = layoutInflater.inflate(R.layout.dialog_image_layout, null);
        ImageView imageView = (ImageView)dialogView.findViewById(R.id.dialog_image);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }

        new AlertDialog.Builder(this).setView(dialogView).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();

        return imageView;
    }
}
