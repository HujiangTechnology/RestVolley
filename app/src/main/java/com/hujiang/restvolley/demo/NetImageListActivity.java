package com.hujiang.restvolley.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hujiang.restvolley.image.ImageLoadOption;
import com.hujiang.restvolley.image.RestVolleyImageLoader;

import java.util.ArrayList;
import java.util.List;

public class NetImageListActivity extends AppCompatActivity {

    private ListView mListView;
    private NetImageAdapter mAdapter;
    private List<String> mImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_image_list);

//        http://placehold.it/350x150
        String mainUrl = "https://dummyimage.com/";
        int width = 100;
        int height = 100;
        int maxCount = 100;
        for (int i = 0; i < maxCount; i++) {
            mImages.add(mainUrl + (width + 10 * i) + "x" + (height + 10 * i));
        }

        mListView = (ListView)findViewById(R.id.net_image_listview);

        mAdapter = new NetImageAdapter();
        mListView.setAdapter(mAdapter);
    }

    class NetImageAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mImages.size();
        }

        @Override
        public String getItem(int position) {
            return mImages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(NetImageListActivity.this).inflate(R.layout.image_list_item, null, false);
                viewHolder = new ViewHolder((ImageView)convertView.findViewById(R.id.image_item), (TextView)convertView.findViewById(R.id.image_title));
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            RestVolleyImageLoader.instance(NetImageListActivity.this).displayImage(mImages.get(position), viewHolder.image
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
