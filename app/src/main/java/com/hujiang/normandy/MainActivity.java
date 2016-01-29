package com.hujiang.normandy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.hujiang.account.AccountManager;
import com.hujiang.account.api.AccountAPI;
import com.hujiang.account.api.AccountApiCallBack;
import com.hujiang.account.api.BaseAccountModel;
import com.hujiang.account.api.model.UserInfo;
import com.hujiang.account.app.LoginActivity;
import com.hujiang.account.app.MyAccountActivity;
import com.hujiang.account.html5.LoginJSEvent;
import com.hujiang.common.concurrent.TaskScheduler;
import com.hujiang.common.util.LogUtils;
import com.hujiang.common.util.ToastUtils;
import com.hujiang.normandy.api.DemoAPI;
import com.hujiang.restvolley.download.RestVolleyDownload;
import com.hujiang.restvolley.image.ImageLoaderCompat;
import com.hujiang.restvolley.image.RestVolleyImageLoader;
import com.hujiang.restvolley.webapi.RestVolleyCallback;
import com.hujiang.restvolley.webapi.RestVolleyResponse;
import com.squareup.okhttp.Headers;

import java.io.File;
import java.util.Map;

//circleid 136651365l
public class MainActivity extends AppCompatActivity implements AccountManager.AccountObserver {

    private Button mAccountButton;
    private Button mLeagueButton;
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

        AccountManager.instance().registerAccountObserver(this);
        LoginJSEvent.getInstance().registerContext(this);

        mAccountButton = (Button)findViewById(R.id.account_btn);

        mLeagueButton = (Button)findViewById(R.id.league_btn);
        mFileImgButton = (Button)findViewById(R.id.file_img_btn);
        mAssetsImgButton = (Button)findViewById(R.id.asset_img_btn);
        mContentImgButton = (Button)findViewById(R.id.content_img_btn);
        mDrawableImgButton = (Button)findViewById(R.id.drawable_btn);
        mRawImgButton = (Button)findViewById(R.id.raw_btn);
        mDownloadButton = (Button)findViewById(R.id.download_btn);

        mLeagueButton.setOnClickListener(mOnClickListener);
        mFileImgButton.setOnClickListener(mOnClickListener);
        mAssetsImgButton.setOnClickListener(mOnClickListener);
        mContentImgButton.setOnClickListener(mOnClickListener);
        mDrawableImgButton.setOnClickListener(mOnClickListener);
        mRawImgButton.setOnClickListener(mOnClickListener);
        mDownloadButton.setOnClickListener(mOnClickListener);

        mAccountButton.setText(AccountManager.instance().isLogin() ? "个人中心" : "登录");
        mAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManager.instance().isLogin()) {
                    MyAccountActivity.start(MainActivity.this, true, true, true, true);
                } else {
                    LoginActivity.start(MainActivity.this);
                }
            }
        });

        findViewById(R.id.get_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DemoAPI.getDetail(136651365l, new RestVolleyCallback<String>() {
//                    @Override
//                    public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
//                        showMessage(data);
//                    }
//
//                    @Override
//                    public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
//                        showMessage(data);
//                    }
//                });
                DemoAPI.requestRecommendCircles(0, 20, AccountManager.instance().getUserToken(), new RestVolleyCallback<String>() {
                    @Override
                    public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                        showMessage(data);
                    }

                    @Override
                    public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                        showMessage(data);
                    }
                });
            }
        });

        findViewById(R.id.post_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DemoAPI.requestGuestAccount(new RestVolleyCallback<String>() {
                    @Override
                    public void onSuccess(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                        showMessage(data);
                    }

                    @Override
                    public void onFail(int statusCode, String data, Map<String, String> headers, boolean notModified, long networkTimeMs, String message) {
                        showMessage(data);
                    }
                });
            }
        });

        findViewById(R.id.sync_get_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskScheduler.execute(new Runnable() {
                    @Override
                    public void run() {
                        TaskScheduler.execute(new Runnable() {
                            @Override
                            public void run() {
                                final RestVolleyResponse<String> result = DemoAPI.getTagSync(136651365l);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showMessage(result.data);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        findViewById(R.id.sync_post_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskScheduler.execute(new Runnable() {
                    @Override
                    public void run() {
                        final RestVolleyResponse<String> result = DemoAPI.syncPostGuestAccount();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showMessage(result.data);
                            }
                        });
                    }
                });
            }
        });

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
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            Context context = MainActivity.this;
            if (id == R.id.league_btn) {
                if (!AccountManager.instance().isLogin()) {
                    LoginActivity.start(MainActivity.this);
                } else {
                    startActivity(new Intent(MainActivity.this, LeagueActivity.class));
                }
            } else if (id == R.id.file_img_btn) {
                String path = "/mnt/sdcard/img_default.jpg";
                if (!new File(path).exists()) {
                    ToastUtils.show(MainActivity.this, "缺少磁盘文件/sdcard/img_default.jpg");
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
                //download event
//                String url = "http://app.m.hjfile.cn/android/hjwordgames_hjpc.apk";
//                String localPath = "/mnt/sdcard/hjwordgames_hjpc.apk";
                String url = "https://fbookmedia.files.wordpress.com/2015/09/360-video-in-news-feed-screenshots.zip";
                String localPath = "/mnt/sdcard/360-video-in-news-feed-screenshots.zip";
                RestVolleyDownload.download(MainActivity.this, url, localPath, new RestVolleyDownload.OnDownloadListener() {

                    @Override
                    public void onDownloadStart(String url) {

                    }

                    @Override
                    public void onDownloadSuccess(String url, File file, int httpCode, Headers headers) {
                        showMessage("success" + file.getAbsolutePath());
                    }

                    @Override
                    public void onDownloadFailure(String url, Exception e, int httpCode, Headers headers) {
                        showMessage("fail" + e.getMessage());
                    }

                    @Override
                    public void onDownloadProgress(String url, int downloadBytes, int contentLength, File file, int httpCode, Headers headers) {
                        Log.i("liuxiaoming", "progress:" + downloadBytes + "/" + contentLength);
                    }
                });
            }
        }
    };

    @Override
    public void onLogin(UserInfo userInfo) {
        updateUserProfileButtonText(AccountManager.instance().isLogin());
    }

    @Override
    public void onLogout() {
        updateUserProfileButtonText(AccountManager.instance().isLogin());
    }

    @Override
    public void onModifyAccount(UserInfo userInfo) {

    }

    private void updateUserProfileButtonText(boolean isLogin) {
        mAccountButton.setText(isLogin ? "个人中心" : "登录");
    }

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
