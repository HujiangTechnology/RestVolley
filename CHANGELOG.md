### v1.1.11(2018-06-22)
* remove duplicated item `Content-Type` in request headers

### v1.1.10(2018-05-29)
* fix: NullPointException on LinkedList$Node.next

### v1.1.9(2018-04-02)
* [fix] StringIndexOfBoundException: length=0;index=-1

### v1.1.8(2018-04-02)
* [update]默认不信任签名证书

### v1.1.7(2018-01-08)
* [fix]解决将json字符串反序列化为map时integer被强制转成double的问题

### v1.1.6(2017-10-13)
* [fix] NullPointException occurs while adding null cookie to cookie store

### v1.1.5(2017-09-21)
* [fix] cookie null check

### v1.1.4(2017-09-19)
* public RestVolleyRequest.WEBAPI_REQUEST_ENGINE

### v1.1.3(2017-09-13)
* cookie supported

### v1.1.2(2017-08-30)
* fix protocol exception

### v1.1.1(2017-08-18)
* add call.setException when request fail

### v1.1.0(2017-08-14)
* [add] add callback to syncExecute methods

### v.1.0.22(2017-06-13)
* [add] add method setProxy to RestVolleyRequest

### v1.0.21(2017-05-08)
*[fix] NullPointException on RestVolleyImageCache.getDiskCacheDir(Context context, String uniqueName)

## v1.0.20(2017-04-28)
* [add]`DeleteRequest`支持`body`，使用构造函数`DeleteRequest(Context context, boolean isBodyEnable)`支持`body`

### v1.0.19(2017-03-09)
* 修复接口RestVolleyImageLoader.isCached(..)返回状态不对的Bug

### v1.0.18(2017-03-08)
* 修复gzip 压缩参数Content-Encoding, Accept-Encoding支持的Bug

### v1.0.17(2017-02-22)
* 添加api GsonUtils.fromJsonStringThrowEx， GsonUtils.optFromJsonString，GsonUtils.toJsonStringThrowEx，GsonUtils.optToJsonString
* 优化Demo
* 优化联网代理问题

### v1.0.16(2017-02-07)
* 解决部分4.+手机固件上webp图片加载的问题

### v1.0.15(2017-01-16)
* 解决NullPointerException的问题

### v1.0.14(2017-01-13)
* 修复无网络情况下同步接口出现NullException的Bug

### v1.0.13(2017-01-09)
* 修复1.0.12版本引起的超时重试时请求队列被阻塞的问题

### v1.0.12(2016-12-29)
* 优化图片内存问题
* 解决清除图片缓存时DiskLruCache出现异常：IllegalStateException:cache is closed
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


