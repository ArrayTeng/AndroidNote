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

- 直接启动一个FlutterActivity 的方式（无法自定义插件）

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

  https://coding.imooc.com/learn/questiondetail/150166.html

- 使用复写FlutterActivity 的方式（可以自定义插件）



4、Android项目中开启Flutter热重载

- 打开一个模拟器或者连接一个设备到电脑上

- 关闭我们的APP，然后在flutter模块的根目录运行 flutter attach 

  如果在 flutter attach  命令执行之后发现连接了很多设备，那么需要执行 flutter attach -d '设备名'来指定一个设备



5、优化Flutter页面启动速度



##### Flutter 与 Native 通信

