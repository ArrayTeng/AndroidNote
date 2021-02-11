由于Http天生明文的特点，在整个传输过程中是完全透明的，任何人都能够在链路中截获、修改或者伪造请求或者响应报文，所谓Http是不安全的，如果一个请求具备了机密性、完整性、身份认证和不可否认那么就可以认为是安全的。



Https是一个非常简单的协议，协议名是https，默认端口号是443，其它的都完全沿用Http，没有任何新的东西，https把http的下层传输协议由TCP/IP换成了SSL/TLS，让http运行在安全的SSL/TLS协议上，收发报文不在使用Socket API而是调用专门的安全接口。

<img src="https://cdn.jsdelivr.net/gh/ArrayTeng/resources/50d57e18813e18270747806d5d73f0a3.png" style="zoom:24%;" />



