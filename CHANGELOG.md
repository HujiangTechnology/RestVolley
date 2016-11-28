### v1.0.9(2016-11-28)
* fix: no onDownloadFailure callback when SDCard is full

### v1.0.8(2016-11-07)
* add method RestVolleyImageLoader#getDiskCacheDir(), RestVolleyImageLoader#getDiskCachePath()
* add method RestVolleyImageLoader#displayImage(final String uri, final ImageView imageView, final ImageDisplayer imageDisplayer)
* add metnod RestVolleyImageLoader#displayImage(final String uri, final ImageView imageView, final ImageLoadOption option, final ImageDisplayer displayer)
* remove ImageDisplayer from ImageLoadOption
* RestVolleyDownload增加下载临时文件，后缀为.tmp
* 增加取消下载的接口RestVolleyDownload#cancel(), 删除接口RestVolleyDownload#cancel(Object tag)
* fix bug: java.lang.IllegalStateException: cache is closed on DiskLruCache.java:614
* add method RestVolleyImageLoader#getDiskCachePath(......)

### v1.0.7(2016-10-26)
* add ImageLoadOption#imageDisplayer, ImageLoadOption#bitmapConfig
* add method RestVolleyImageLoader#removeAllCache(), RestVolleyImageLoader#removeDiskCache()
* add method RestVolleyImageLoader#start(), RestVolleyImageLoader#stop()

### v1.0.6(2016-10-19)
* 解决磁盘IO异常导致磁盘缓存失效，并导致程序崩溃的问题

### v1.0.5
* 添加接口RestVolleyCallback#getException()获取异常信息

### v1.0.4
* 解决同步下载接口不能下载文件的问题

### v1.0.1
* fix bintrayUpload error

### v1.0.0
* release for rest api requesting, image loading, downloading, uploading.


