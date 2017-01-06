
### v1.0.12(2016-12-29)
* 优化图片内存问题
* 解决清图片缓存是DiskLruCache出现IllegalStateException:cache is closed
* RestVolleyImageLoader支持流式图片下载，降低内存占用率
* 优化https支持, 默认信任所有请求，安全要求较高的app可以通过接口设置SSLSocketFactory和HostnameVerifier，或者添加证书锁定CertificatePinner来增加安全性
* 提供常量CertificateUtils.DEFAULT_HOSTNAME_VERIFIER; CertificateUtils.ALLOW_ALL_HOSTNAME_VERIFIER
* 提供接口CertificateUtils.getDefaultSSLSocketFactory()
* 支持内置自签名证书CertificateUtils.getSSLSocketFactory(InputStream certificateInputStream)
* 优化下载逻辑，支持HEAD头不传Content-Length的下载

### v1.0.11(2016-12-28)
* 解决RestVolleyImageLoader加载本地大图出现OOM的问题，但是网络大图下载的问题暂时没有解决，后续会重写网络图片下载那块逻辑，解决大图下载问题

### v1.0.10(2016-12-21)
 * 解决当网络请求发生VolleyError时同步接口syncExecute(...)返回数据异常的Bug

### v1.0.9(2016-11-28)
* fix: no onDownloadFailure callback when SDCard is full
* recover API RestVolleyDownload#cancel(tag)

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


