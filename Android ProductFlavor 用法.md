# 前言

最近一直在学习Android Gradle 相关的知识点，今天刚好看到了 ProductFlavor 这节，ProductFlavor 表示产品风味，Google 相关的文档可以看 [Android developers ProductFlavor](https://developer.android.google.cn/reference/tools/gradle-api/7.1/com/android/build/api/dsl/ProductFlavor?hl=en) ,产品风味这词起的还是挺有意思的，乍看上去我一时半会也不理解这是干嘛的，如果说是用于区分打包的那么我 gradle 文件里的 buildTypes 不是就已经够用了吗，所以我花了一点时间重新看了下，按照我的理解如果你只是中小型的项目不涉及区分不同地区用户打不用的包的那么 ProductFlavor 基本上也没什么用处，但如果你项目里要区分国内版和国外版甚至还要根据用户是否是VIP会员加上收费和免费的版本，这种情况下就会出现国内收费、免费国外收费、免费的版本，在极端点，我收费和免费的版本在相同页面上甚至显示的UI布局和icon图标资源都不一样，这种情况又该怎么处理呢，ProductFlavor 的出现就非常友好的帮助我们开发者解决了上述的版本区分。

# productFlavors

<img src="/Users/tengfei/Library/Application Support/typora-user-images/image-20210614133929588.png" alt="image-20210614133929588" style="zoom:50%;" />



# 实战



# 参考资料

