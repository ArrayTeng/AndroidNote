# Gradle Wrapper
- Gradle Wrapper用来配置开发过程中用到的Gradle构建工具版本，避免因为Gradle不统一带来的不必要的问题
- 在工程目录下使用CMD命令生成Wrapper： gradle wrapper
- 标准的gradle工程目录
  - gradlew 和 gradlew.bat 分别是Linux和Windows下的可执行脚本，具体的业务逻辑是在/gradle/wrapper/gradle-wrapper.jar中实现的，gradlew最终还是使用java包来执行相关的gradle操作的
  
  ```
  //gradle-wrapper.properties 
  distributionBase=GRADLE_USER_HOME//下载Gradle压缩包解压后存储的主目录
  distributionPath=wrapper/dists//相对于distributionBase的解压后的Gradle压缩包的路径
  distributionUrl=https\://services.gradle.org/distributions/gradle-6.5-bin.zip//gradle发行版压缩包的下载地址  -bin：二进制版本包 -all：bin基础上还包含了源码和文档
  zipStoreBase=GRADLE_USER_HOME//同distributionBase但存放的是zip压缩包
  zipStorePath=wrapper/dists//同distributionPath但存放的是zip压缩包

  ```
  
# Gradle 命令行
- gradlew -? / -h / -help 使用帮助
- gradlew tasks 查看所有可执行Tasks
- gradlew --refresh-dependencies assemble 强制刷新依赖 
- gradlew cBC 等价于执行 Task cleanBuildCache
- gradlew :app:dependencies 查找APP工程依赖树

# Gradle 构建机制
## settings.gradle
- Gradle支持多工程构建，使用settings.gradle来配置添加子工程（模块）
- settings文件在初始化阶段执行，创建Settings对象，在执行脚本时调用该对象的方法
- Settings.include(String... projectPaths):
  - 将给定的目录添加到项目构建中，':app'表示文件相对路径，相当于'./app'文件夹
  - 多项目架构进行分层，把同层次的子工程放在同一文件夹下便于管理，使用':xxx:yyy'表示
  
## Gradle生命周期
### initialization
Gradle支持单项目和多项目构建，在初始化阶段Gradle确定哪些项目会参与构建，并为每个项目创建Project实例
### Configuration
配置阶段，解析每个工程的build.gradle文件，创建要执行的任务子集和确定任务之间的关系，并对关系做一些初始化配置
### Execution
运行阶段，Gradle根据配置阶段创建和配置的要执行的任务子集，执行任务


# 自定义任务
在build.gradle中自定义任务
- task <任务名>{ } 
- { }执行的是配置阶段的代码，执行阶段要处理的逻辑需要调用doFirst、doLast方法，在闭包中实现，doFirst{}表示任务执行开始的时候调用的方法，doLast{}表示任务调用结束调用的方法
- task A(dependsOn:[B]){} 表示任务A依赖于任务B，那么B执行在A之前
- 自定义的任务默认分组到other中
  
