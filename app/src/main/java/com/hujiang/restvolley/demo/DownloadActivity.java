package com.hujiang.restvolley.demo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hujiang.restvolley.download.RestVolleyDownload;
import com.squareup.okhttp.Headers;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class DownloadActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = "OCSDemo";

    private Button mAddDownloadTaskButton;

    private static final String DOWNLOAD_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";
    private static final String[] DOWNLOAD_URLS = {
            "http://app.m.hjfile.cn/android/tingliku_hjpc.apk"
            , "http://app.m.hjfile.cn/android/hjdict_hjpc.apk"
            , "http://app.m.hjfile.cn/android/hjwordgames_hjpc.apk"
            , "http://app.m.hjfile.cn/android/cctalk_hjpc.apk"
            , "http://app.m.hjfile.cn/android/hujiangclass3_hjpc.apk"
            , "http://app.m.hjfile.cn/android/normandy_hjpc.apk"
    };

    private int mDownloadUrlIndex = 0;
    private ListView mDownloadListView;
    private DownloadAdapter mDownloadAdapter;
    private List<DownloadInfo> mDownloadInfos = new ArrayList<>();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_layout);
        mAddDownloadTaskButton = (Button)findViewById(R.id.add_download_task1);
        mDownloadListView = (ListView)findViewById(R.id.download_list1);

        mDownloadAdapter = new DownloadAdapter();
        mDownloadListView.setAdapter(mDownloadAdapter);
        mDownloadListView.setOnItemClickListener(this);

        mAddDownloadTaskButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.add_download_task1) {
            if (mDownloadUrlIndex >= DOWNLOAD_URLS.length) {
                Toast.makeText(this, "没有更多下载任务", Toast.LENGTH_SHORT).show();
                return;
            }
            String url = DOWNLOAD_URLS[mDownloadUrlIndex];
            final String taskName = getFileNameFromUrl(url);
            final String path = DOWNLOAD_DIR + taskName;
            String mimeType = "application/apk";

            RestVolleyDownload.download(DownloadActivity.this, url, path, new RestVolleyDownload.OnDownloadListener() {
                @Override
                public void onDownloadStart(String url) {
                    DownloadInfo downloadInfo = new DownloadInfo();
                    downloadInfo.id = url.hashCode();
                    downloadInfo.downloadBytes = 0;
                    downloadInfo.totalSize = 0;
                    downloadInfo.url = url;
                    downloadInfo.path = path;
                    downloadInfo.name = taskName;
                    downloadInfo.status = DownloadInfo.STATUS_DOWNLOADING;
                    mDownloadInfos.add(downloadInfo);
                    mDownloadAdapter.notifyDataSetChanged();
                }

                @Override
                public void onDownloadSuccess(String url, File file, int httpCode, Headers headers) {
                    DownloadInfo info = getDownloadInfo(url);
                    info.status = DownloadInfo.STATUS_COMPLETE;
                    info.downloadBytes = info.totalSize;
                    mDownloadAdapter.notifyDataSetChanged();
                }

                @Override
                public void onDownloadFailure(String url, Exception e, int httpCode, Headers headers) {
                    getDownloadInfo(url).status = DownloadInfo.STATUS_ERROR;
                    mDownloadAdapter.notifyDataSetChanged();
                }

                @Override
                public void onDownloadProgress(String url, int downloadBytes, int contentLength, File file, int httpCode, Headers headers) {
                    DownloadInfo info = getDownloadInfo(url);
                    info.status = DownloadInfo.STATUS_DOWNLOADING;
                    info.downloadBytes = downloadBytes;
                    info.totalSize = contentLength;
                    mDownloadAdapter.notifyDataSetChanged();
                }
            });

            mDownloadUrlIndex++;
        }
    }


    private DownloadInfo getDownloadInfo(String url) {
        int id = url.hashCode();
        for (DownloadInfo info : mDownloadInfos) {
            if (info.id == id) {
                return info;
            }
        }

        return DownloadInfo.NULL_OBJ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DownloadInfo downloadInfo = mDownloadAdapter.getItem(position);

        if (downloadInfo.status == DownloadInfo.STATUS_COMPLETE) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(downloadInfo.path)), "application/vnd.android.package-archive");
            startActivity(intent);
        }
        mDownloadAdapter.notifyDataSetChanged();
    }

    class DownloadAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDownloadInfos.size();
        }

        @Override
        public DownloadInfo getItem(int position) {

            return mDownloadInfos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_layout, null, false);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder)convertView.getTag();
            }

            final DownloadInfo info = getItem(position);
            holder.mName.setText(info.name);
            holder.mProTxt.setText(String.format("%s/%s", Formatter.formatFileSize(DownloadActivity.this, info.downloadBytes)
                    , Formatter.formatFileSize(DownloadActivity.this, info.totalSize)));
            holder.mProgress.setMax(100);
            holder.mProgress.setProgress(info.downloadBytes == 0 ? 0 : (int) (100 * info.downloadBytes / info.totalSize));

            String statusHint;
            switch (info.status) {
                case DownloadInfo.STATUS_DOWNLOADING:
                    statusHint = "下载中..";
                    break;
                case DownloadInfo.STATUS_ERROR:
                    statusHint = "失败";
                    break;
                case DownloadInfo.STATUS_COMPLETE:
                    statusHint = "完成";
                    break;
                case DownloadInfo.STATUS_PAUSE:
                    statusHint = "暂停";
                    break;
                default:
                    statusHint = "下载中..";
                    break;
            }
            holder.mStatus.setText(statusHint);
            return convertView;
        }
    }

    class Holder {
        public TextView mName;
        public TextView mProTxt;
        public ProgressBar mProgress;
        public TextView mStatus;

        public Holder(View parent) {
            mName = (TextView)parent.findViewById(R.id.download_name);
            mProTxt = (TextView)parent.findViewById(R.id.download_progress_txt);
            mProgress = (ProgressBar)parent.findViewById(R.id.download_progress);
            mStatus = (TextView)parent.findViewById(R.id.download_status);
        }
    }

    public static class DownloadInfo {
        public static final DownloadInfo NULL_OBJ = new DownloadInfo();
        static final int STATUS_DOWNLOADING = 0;
        static final int STATUS_COMPLETE = 1;
        static final int STATUS_ERROR = 2;
        static final int STATUS_PAUSE = 3;
        int id = 0;
        String url = "";
        String path = "";
        String name = "";
        int downloadBytes = 0;
        int totalSize = 0;
        int status = STATUS_DOWNLOADING;

        public DownloadInfo() {

        }
    }

    public static String getFileNameFromUrl(String url) {
        int index = url.lastIndexOf('?');
        String filename;
        if (index > 1) {
            filename = url.substring(url.lastIndexOf('/') + 1, index);
        } else {
            filename = url.substring(url.lastIndexOf('/') + 1);
        }

        if (filename == null || "".equals(filename.trim())) {
            filename = System.currentTimeMillis() + "";
        }
        return filename;
    }
}
