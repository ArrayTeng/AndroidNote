###  定义：

Hypertext Transfer Protocol，超⽂本传输协议，和 HTML (Hypertext Markup Language 超⽂本标记语⾔) ⼀起诞⽣，⽤于在⽹络上请求和传输 HTML 内容。 超⽂本，即「扩展型⽂本」，指的是 HTML 中可以有链向别的⽂本的链接

### HTTP 的⼯作⽅式 

#### 浏览器： 

⽤户输⼊地址后回⻋或点击链接 -> 浏览器拼装 HTTP 报⽂并发送请求给服务器 -> 服 务器处理请求后发送响应报⽂给浏览器 -> 浏览器解析响应报⽂并使⽤渲染引擎显示 到界⾯ 

#### ⼿机 App：

⽤户点击或界⾯⾃动触发联⽹需求 -> Android 代码调⽤拼装 HTTP 报⽂并发送请求 到服务器 -> 服务器处理请求后发送响应报⽂给⼿机 -> Android 代码处理响应报⽂并 作出相应处理（如储存数据、加⼯数据、显示数据到界⾯）

### URL 和 HTTP 报⽂ 

#### URL 格式 

三部分：协议类型、服务器地址(和端⼝号)、路径(Path) 

协议类型://服务器地址[:端⼝号]路径

 http://hencoder.com/users?gender=male

### 报⽂格式 

#### 请求报⽂


![](https://img.imgdb.cn/item/6017c0af3ffa7d37b3dd7b45.jpg)


 #### 响应报⽂


![](https://img.imgdb.cn/item/6017c12a3ffa7d37b3dddfd2.jpg)


#### Status Code 状态码

三位数字，⽤于对响应结果做出类型化描述（如「获取成功」「内容未找到」）。

- 1xx：临时性消息。如：100 （继续发送）、101（正在切换协议） 
- 2xx：成功。最典型的是 200（OK）、201（创建成功）。 
- 3xx：重定向。如 301（永久移动）、302（暂时移动）、304（内容未改变）。 
- 4xx：客户端错误。如 400（客户端请求错误）、401（认证失败）、403（被禁 ⽌）、404（找不到内容）。
- 5xx：服务器错误。如 500（服务器内部错误）。

#### Header ⾸部 

作⽤：HTTP 消息的 metadata。 

#### Host 

⽬标主机。注意：不是在⽹络上⽤于寻址的，⽽是在⽬标服务器上⽤于定位⼦服务 器的。

#### Content-Type 

指定 Body 的类型

#### Content-Length 

指定 Body 的⻓度（字节）。

#### Transfer: chunked (分块传输编码 Chunked Transfer Encoding) 

⽤于当响应发起时，内容⻓度还没能确定的情况下。和 Content-Length 不同时使 ⽤。⽤途是尽早给出响应，减少⽤户等待。

#### Location 

指定重定向的⽬标 URL 

#### User-Agent 

⽤户代理，即是谁实际发送请求、接受响应的，例如⼿机浏览器、某款⼿机 App。 

#### Range / Accept-Range 

按范围取数据 

Accept-Range: bytes 响应报⽂中出现，表示服务器⽀持按字节来取范围数据 

Range: bytes=- 请求报⽂中出现，表示要取哪段数据 

Content-Range:-/total 响应报⽂中出现，表示发送的是哪段 数据 作⽤：断点续传、多线程下载。 

#### 其他 Headers

- Accept: 客户端能接受的数据类型。如 text/html
- Accept-Charset: 客户端接受的字符集。如 utf-8
- Accept-Encoding: 客户端接受的压缩编码类型。如 gzip
- Content-Encoding：压缩类型。如 gzip

#### Cache 

作⽤：在客户端或中间⽹络节点缓存数据，降低从服务器取数据的频率，以提⾼⽹络性能。 

#### REST 

- REST HTTP 即正确使⽤ HTTP。包括： 
- 使⽤资源的格式来定义 URL 
- 规范地使⽤ method 来定义⽹络请求操作 
- 规范地使⽤ status code 来表示响应状态 
- 其他符合 HTTP 规范的设计准则            









