package com.hujiang.restvolley2.demo;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hujiang.restvolley2.image.ImageLoadOption;
import com.hujiang.restvolley2.image.RVImageLoader;

import java.util.ArrayList;
import java.util.List;

public class LocalImageListActivity extends AppCompatActivity {

    private ListView mImageListView;
    private ImageListAdapter mImageListAdapter;
    private List<String> mImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        mImageListView = (ListView)findViewById(R.id.image_list_view);

        //add built-in images
        mImages.add("assets://assets_default.png");
        mImages.add("drawable://" + R.drawable.drawable_default);
        mImages.add("drawable://" + R.raw.raw_default);

        String[] projection = {"_id", "_data"};
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor c = MediaStore.Images.Media.query(getContentResolver(), uri, projection, null, MediaStore.Images.Media.DATE_ADDED);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String path = c.getString(c.getColumnIndexOrThrow("_data"));
                    mImages.add("file://" + path);
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
                convertView = LayoutInflater.from(LocalImageListActivity.this).inflate(R.layout.image_list_item, null, false);
                viewHolder = new ViewHolder((ImageView)convertView.findViewById(R.id.image_item), (TextView)convertView.findViewById(R.id.image_title));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            RVImageLoader.instance(LocalImageListActivity.this).displayImage(paths.get(position), viewHolder.image
                    , ImageLoadOption.create().defaultImgResId(R.drawable.restvolley_demo_app));
            viewHolder.text.setText(mImages.get(position));

            return convertView;
        }

        class ViewHolder {
            ImageView image;
            TextView text;

            public ViewHolder(ImageView image, TextView text) {
                this.image = image;
                this.text = text;
            }
        }
    }
}
