package com.hujiang.restvolley.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.hujiang.restvolley.TaskScheduler;
import com.hujiang.restvolley.image.ImageLoaderCompat;
import com.hujiang.restvolley.image.RestVolleyImageLoader;

import java.io.File;

public class MainActivity extends AppCompatActivity {

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
