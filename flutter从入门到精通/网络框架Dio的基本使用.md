# Future与FutureBuilder的使用技巧

Flutter是基于Dart语言实现的，而Dart中的代码是在一个线程中运行的（dart是单线程的），因此，flutter也是单线程的，Flutter给我们提供了 Future 对象以及 async 和 await 关键字来支持异步编程。

future对象表示异步操作的结果，可以看作是接下来的某一个值或者错误，通常通过then()来处理返回的结果

async用于标明函数是一个异步函数，其返回的类型是future类型

await用来等待耗时操作的返回结果，这个操作会阻塞到后面的代码

async和await可以像编写同步代码那样编写异步代码

只有在标记async的函数里才可以使用await，当函数执行到await的时候语句将会暂停知道await后的表达式执行完毕

使用try-catch可以捕捉await语句的错误

async函数里可以多次使用await

async函数返回Future对象，普通返回值将自动包装成Future对象

使用await for 来从Stream里读取序列值，如果想中止可使用break或者return

```dart
Future<Response> getHttp() async {
  Response response = await Dio().get("https://www.baidu.com/");
  return response;
}
```

```dart
              getHttp().timeout(Duration(seconds: 1),onTimeout:(){
                //设置future的超时时间，返回值为你定义future时的泛型的值
                return Response();
              } ).then((result) {
                //使用Future.then来获取future的值以及捕获future的异常
                print("异步任务返回的结果是${result.toString()}");
              },onError: (e){
								//如果onError和catchError同时存在那么只会执行onError
              }).whenComplete(() {
                //在future结束的时候会执行 whenComplete ，类似java的finally代码块
                print("异步任务处理完成");
              }).catchError(() {
                print("异步任务处理报错");
              });
```

FutureBuilder是一个将异步操作与异步UI更新结合在一起的类通过它可以将网络请求与数据库读取的操作更新在UI上

```dart
  _buildFutureBuilder()=>FutureBuilder(future:getHttp(),initialData:Response(),builder:(BuildContext buildContext,AsyncSnapshot<Response> asyncSnapshot){
    switch(asyncSnapshot.connectionState){
      case ConnectionState.none:
        return Text("input url to start");
      case ConnectionState.waiting:
        return Center(child: CircularProgressIndicator(),);
      case ConnectionState.active:
        return Text("");
      case ConnectionState.done:
        return Text("${asyncSnapshot.data.toString()}");
    }
  });

```

FutureBuilder的构造函数有三分别为 

future：这个FutureBuilder所连接的异步计算

 initialData：非空的Future完成前的初始化数据

 builder：AsyncWidgetBuilder类型的回调函数接收 BuildContext 和 AsyncSnapshot



# Dio的使用

