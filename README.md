# RestVolley
-------

A http request engine based on [Volley] and [OkHttp], giving up Apache HttpClient request. supports image loading, restful api requesting, and file downloading. [READ MORE]

# Dependency
-------
dependent with Maven:

```
<dependency>
  <groupId>com.hujiang.restvolley</groupId>
  <artifactId>restvolley</artifactId>
  <version>1.0.1</version>
</dependency>
```
or Gradle:

```
compile 'com.hujiang.restvolley:restvolley:1.0.0'
```

# Functions
-------
## Restful API request
RestVolley make restful API requesting conveniently.It support the most http method request, such as `GET`,`HEAD`,`POST`,`DELETE`,`OPTIONS`,`PATCH`,`PUT`,`TRACE`.

|| GET | POST | DELETE | PUT | HEAD | PATCH | OPTIONS | TRACE |
|:---:| :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
|Cacheable|Y|N|N|N|Y|N|Y|Y|
|requireBody|N|Y|N|Y|N|Y|N|N|
|permitBody|||Y||||||

* create a request

```
//Get request
new GetRequest(context).url("").execute(String.class, new RestVolleyCallback<String>());
//post request
new PostRequest(context).url("").execute(String.class, new RestVolleyCallback<String>());
//Delete request
new DeleteRequest(context).url("").execute(String.class, new RestVolleyCallback<String>());
//Put request
new PutRequest(context).url("").execute(String.class, new RestVolleyCallback<String>());
//Head request
new HeadRequest(context).url("").execute(String.class, new RestVolleyCallback<String>());
//Patch request
new PatchRequest(context).url("").execute(String.class, new RestVolleyCallback<String>());
//Options request
new OptionsRequest(context).url("").execute(String.class, new RestVolleyCallback<String>());
//Trace request
new TraceRequest(context).url("").execute(String.class, new RestVolleyCallback<String>());
```
* set http request params: header, urlparams, requestbody, contentType, charset

```
new GetRequest(context).url("")
	.setTag("")
	.setRetryPolicy(new DefaultRetryPolicy())
	.setShouldCache(true)
	.setPriority(Priority.Normal)
	//default is "application/json"
	.setContentType("application/json")
	//default is "UTF-8"
	.setCharset("UTF-8")
	.addHeader("userId", "12345")
	.addHeaders(map)
	.setUserAgent("")
	.addParams(key, value)
	.setCacheEntry(...)
	.execute(.....);
```
* Cache

```
//default false
setShouldCache(true);
setCacheEntry(...)
```
* RetryPolicy

```
setRetryPolicy(new DefaultRetryPolicy())
```
* Priority

```
//default is Priority.Normal
setPriority(Priority.Normal)
```
* ContentType and Charset

```
	//default is "application/json"
	setContentType("application/json")
	//default is "UTF-8"
	setCharset("UTF-8")

```

* Tag

```
setTag(tag);
```

* UserAgent

```
setUserAgent("")
```
* timeout

```
setConnectTimeout(...);
setReadTimeout(...);
setWriteTimeout(...);

//for all timout
setTimeout(...);

```
* header

```
addHeader(key, value);
//or
addHeaders(map);
```
* params

```
//add url params
addParams(key, value);

//for post/put/patch or delete
addParams(key, file);
addParams(key, inputstream);
```
* body and HttpEntity

```
//body and HttpEntity for post/put/patch or delete
setBody(byte[] bytes);
setBody(HttpEntity httpEntity);
setBody(String s);

//convert params to form data entity.
paramsToFormEntity();
//convert params to json entity.
paramsToJsonEntity();
//convert params to multipart entity.
paramsToMultipartEntity();

```

* execute and syncExecute

```
//async execute with callback.
new GetRequest(context).url().execute(String.class, new RestVolleyCallback<String>());

//sync execute.
RestVolleyResponse<String> response = new GetRequest(context).url().syncExecute(String.class);
```
* request with new RequestEngine

suggest using the default RequestEngine, only if the request need proxy.

```
//
new GetRequest(context).setRequestEngine(...);

//create a new RequestEngine
RestVolley.newRequestEngine(context, engineTag);
```
## Image load
RestVolleyImageLoader has memory cache and disk cache, supports not only network image request, but sdcard/assets/res/ContentProvider Uri image request, uri scheme like:

|||
|:---|:---|
|http/https|http://www.google.com/xxx/xxx.png|
|file|file:///mnt/sdcard/xxx.png|
|assets|assets://assets_default.png|
|res|“drawable://” + R.drawable.drawable_default, or "drawable://" + R.raw.defaut|
|ContentProvider|content://media/external/images/media/27916|

* set global config

```
RestVolleyImageLoader.instance(context).setConfig(ImageLoaderGlobalConfig cfg);
```
* ImageLoaderGlobalConfig

```
ImageLoaderGlobalConfig.create().memCacheSize(..).diskCacheSize(..).diskCacheDir(..).requestEngine(..);
```
* ImageLoadOption

```
ImageLoadOption.create().defaultImgResId(..)
	.errorImgResId(..)
	.imgLoadAnimation(..)
	.scaleType(..)
	.maxWidth(..)
	.maxHeight(..)
	.cacheEnable(..);
```

* displayImage

```
RestVolleyImageLoader.instance(context).displayImage(uri, imageView);
//
RestVolleyImageLoader.instance(context).displayImage(uri, imageView, imageLoadOption);
```

* loadImage

```
RestVolleyImageLoader.instance(context).loadImage(uri, ImageListener);
//
RestVolleyImageLoader.instance(context).loadImage(uri, ImageLoadOption, ImageListener);
```
* syncLoadImage

```
RestVolleyImageLoader.instance(context).syncLoadImage(String uri);
//
RestVolleyImageLoader.instance(context).syncLoadImage(uri, ImageLoadOption);
```
* others

```
RestVolleyImageLoader.instance(context).isCached(...);
RestVolleyImageLoader.instance(context).removeCache(...);

```

## File download

```
new RestVolleyDownload(context)
	.url(url)
	.addHeader(key, value)
	.tag(tag)
	.setProxy(...)
	.setTimeout(...)
	.setIsAppend(..)
	.download(......)
	//or
	.syncdownload(....)
```

simple static download.

```
RestVolleyDownload.download(....)
//or
RestVolleyDownload.syncDownload(....)
```

## Data upload
Using RestVolleyUpload as PostRequest.

```
new RestVolleyUpload(context).url(..).execute(...);
//or
new RestVolleyUpload(context).url(..).syncExecute(...);
```

# License
-------
```
Copyright (C) 2016 Hujiang, Inc.
Copyright (C) 2014 Xiaoke Zhang.
Copyright (C) 2011 The Android Open Source Project.
Copyright (C) 2016 Square, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

# Change Log
------

* 1.0.1
    1. fix bintrayUpload error

* 1.0.0
    1. release for rest api requesting, image loading, downloading, uploading.





























[Volley]:https://github.com/mcxiaoke/android-volley
[OkHttp]:https://github.com/square/okhttp
[READ MORE]:docs/OverView.md