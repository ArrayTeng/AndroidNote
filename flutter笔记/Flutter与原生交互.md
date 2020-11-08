##### 在Android项目中集成Flutter页面

1、创建Flutter module

假设你原生项目工程的目录在 /Users/tengfei/AndroidStudy/Android-Amazing/flutter_hybrid/Native,创建Flutter module的时候需要你切换到 flutter_hybrid 目录，并执行以下命令：

flutter create -t module flutter_module 

创建moudle名为 ‘  flutter_module   ’的 Flutter module，在被创建的文件中有两个隐藏文件分别为 .android 和 .iOS 前者为该module的Android宿主工程，后者为iOS的宿主工程

2、集成与调用

在你的Android项目中 settings.gradle 添加：

```groovy
rootProject.name='FlutterHybridAndroid'
setBinding(new Binding([gradle: this]))                                 
evaluate(new File(                                                     
        settingsDir.parentFile,                                                
        'flutter_module/.android/include_flutter.groovy'
))                                                                     
include ':flutter_module'
project(':flutter_module').projectDir = new File('../flutter_module')
```

在app build.gradle 中添加

```groovy
//添加java8支持
compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
//添加对flutter的依赖
    implementation project(':flutter')
```

因为flutter SDK最小支持的Android版本是 API 16 所以你的 minSdkVersion 要>=16

3、在Java代码中调用flutter模块

- 直接启动一个FlutterActivity 的方式（无法自定义插件，没法实现flutter与Native之间的通信）

  在清单文件中添加 FlutterActivity 标签

  ```java
  <activity
   	android:name="io.flutter.embedding.android.FlutterActivity"
   android:configChanges="orientation|keyboardHidden|keyboard|screenSize|locale|layoutDirection|fontScale|screenLayout|density|uiMode"
     android:hardwareAccelerated="true"
     android:windowSoftInputMode="adjustResize"/>
  ```

  ```java
          startActivity(
                  FlutterActivity
                          .withNewEngine()
                          .initialRoute("route1")
                          .build(MainActivity.this));
  ```

- 使用复写FlutterActivity 的方式（可以自定义插件）

  ```java
  public class FlutterAppActivity extends FlutterActivity {
  
      public static final String KEY = "initParams";
  
      private String initParams;
  
      public static void startFlutterAppActivity(Context context,String params){
          Intent intent = new Intent(context,FlutterAppActivity.class);
          intent.putExtra(KEY,params);
          context.startActivity(intent);
      }
  
      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          initParams = getIntent().getStringExtra(KEY);
      }
  
      /**
       * 重写 getInitialRoute 函数将native的数据携带到flutter模块中，在flutter模块中通过
       * window.defaultRouteName 获取携带过来的数据，记得flutter中导入'dart:ui'
       */
      @NonNull
      @Override
      public String getInitialRoute() {
          return initParams == null ? super.getInitialRoute() : initParams;
      }
  
  }
  ```

4、Android项目中开启Flutter热重载

- 打开一个模拟器或者连接一个设备到电脑上

- 关闭我们的APP，然后在flutter模块的根目录运行 flutter attach 

  如果在 flutter attach  命令执行之后发现连接了很多设备，那么需要执行 flutter attach -d '设备名'来指定一个设备

5、优化Flutter页面启动速度

​	新版Flutter支持通过预加载Flutter引擎的方式来提升Flutter模块的打开速度，经过验证下面的这种方式启动速	度是大幅度提升了但是没法将native的数据携带到flutter中

```java
public class MyApplication extends Application {

    public static final String CACHED_ENGINE_ID = "MY_CACHED_ENGINE_ID";

    @Override
    public void onCreate() {
        super.onCreate();
        //在MyApplication中预先初始化Flutter引擎以提升Flutter页面打开速度
        FlutterEngine flutterEngine = new FlutterEngine(this);

        // Start executing Dart code to pre-warm the FlutterEngine.
        flutterEngine.getDartExecutor().executeDartEntrypoint(DartExecutor.DartEntrypoint.createDefault());
        // Cache the FlutterEngine to be used by FlutterActivity.
        FlutterEngineCache.getInstance().put(CACHED_ENGINE_ID, flutterEngine);


    }
}
//方式一
        startActivity(
                FlutterActivity
                        .withCachedEngine("MY_CACHED_ENGINE_ID")
                        .build(MainActivity.this));
//方式二
//使用在MyApplication预先初始化好的Flutter引擎以提升Flutter页面打开速度，注意：在这种模式下回导致getInitialRoute 不被调用所以无法设置初始化参数
    @Nullable
    @Override
    public String getCachedEngineId() {
        return "MY_CACHED_ENGINE_ID";
    }
```



##### Flutter 与 Native 通信  

1、Flutter与Android之间的通信

- 初始化Flutter的时候Native向Dart传递数据
- Native发送数据给Dart
- Dart发送数据给Native
- Dart发送数据给Native，然后Native回传数据给Dart
- Flutter中消息的传递是完全异步的

Channel所支持的数据类型对照表：

| Dart                       | Android             | iOS                                            |
| :------------------------- | :------------------ | :--------------------------------------------- |
| null                       | null                | nil (NSNull when nested)                       |
| bool                       | java.lang.Boolean   | NSNumber numberWithBool:                       |
| int                        | java.lang.Integer   | NSNumber numberWithInt:                        |
| int, if 32 bits not enough | java.lang.Long      | NSNumber numberWithLong:                       |
| double                     | java.lang.Double    | NSNumber numberWithDouble:                     |
| String                     | java.lang.String    | NSString                                       |
| Uint8List                  | byte[]              | FlutterStandardTypedData typedDataWithBytes:   |
| Int32List                  | int[]               | FlutterStandardTypedData typedDataWithInt32:   |
| Int64List                  | long[]              | FlutterStandardTypedData typedDataWithInt64:   |
| Float64List                | double[]            | FlutterStandardTypedData typedDataWithFloat64: |
| List                       | java.util.ArrayList | NSArray                                        |
| Map                        | java.util.HashMap   | NSDictionary                                   |

Flutter定义了三种不同类型的Channel：

- BasicMessageChannel：用于传递字符串和半结构化的信息，持续通信，收到消息后可以回复此次消息，如：Native将遍历到的文件信息陆续传递到Dart，在比如：Flutter将从服务端陆陆续获取到信息交个Native加工，Native处理完返回等；
- MethodChannel：用于传递方法调用（method invocation）一次性通信：如Flutter调用Native拍照；
- EventChannel: 用于数据流（event streams）的通信，持续通信，收到消息后无法回复此次消息，通过长用于Native向Dart的通信，如：手机电量变化，网络连接变化，陀螺仪，传感器等；

这三种类型的类型的Channel都是全双工通信，即A <=> B，Dart可以主动发送消息给platform端，并且platform接收到消息后可以做出回应，同样，platform端可以主动发送消息给Dart端，dart端接收数后返回给platform端。



##### 在Flutter中集成一个Native SDK



