package com.hujiang.restvolley.demo;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.hujiang.restvolley.image.ImageDisplayer;
import com.hujiang.restvolley.image.ImageLoadOption;
import com.hujiang.restvolley.image.LoadFrom;
import com.hujiang.restvolley.image.RestVolleyImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ImageListActivity extends AppCompatActivity {

    private ListView mImageListView;
    private ImageListAdapter mImageListAdapter;
    private List<String> mImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        mImageListView = (ListView)findViewById(R.id.image_list_view);

        String[] projection = {"_id", "_data"};
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor c = MediaStore.Images.Media.query(getContentResolver(), uri, projection, null, MediaStore.Images.Media.DATE_ADDED);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String path = c.getString(c.getColumnIndexOrThrow("_data"));
                    mImages.add(path);
                } while (c.moveToNext());
            }

            c.close();
        }

        mImageListAdapter = new ImageListAdapter(mImages);
        mImageListView.setAdapter(mImageListAdapter);

    }

    class ImageListAdapter extends BaseAdapter {

        private List<String> paths = new ArrayList<>();

        public ImageListAdapter(List<String> paths) {
            if (paths != null) {
                this.paths = paths;
            }
        }

        @Override
        public int getCount() {
            return paths.size();
        }

        @Override
        public String getItem(int position) {
            return paths.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(ImageListActivity.this).inflate(R.layout.image_list_item, null, false);
                viewHolder = new ViewHolder((ImageView)convertView.findViewById(R.id.image_item));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            RestVolleyImageLoader.instance(ImageListActivity.this).displayImage("file://" + paths.get(position), viewHolder.image
                    , ImageLoadOption.create().defaultImgResId(R.drawable.octobiwan_default));

            return convertView;
        }

        class ViewHolder {
            ImageView image;

            public ViewHolder(ImageView image) {
                this.image = image;
            }
        }
    }
}
