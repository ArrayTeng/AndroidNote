# 前言

最近一直在学习Android Gradle 相关的知识点，今天刚好看到了 ProductFlavor 这节，ProductFlavor 表示产品风味，Google 相关的文档可以看 [Android developers ProductFlavor](https://developer.android.google.cn/reference/tools/gradle-api/7.1/com/android/build/api/dsl/ProductFlavor?hl=en) ,产品风味这词起的还是挺有意思的，乍看上去我一时半会也不理解这是干嘛的，如果说是用于区分打包的那么我 gradle 文件里的 buildTypes 不是就已经够用了吗，所以我花了一点时间重新看了下，按照我的理解如果你只是中小型的项目不涉及区分不同地区用户打不用的包的那么 ProductFlavor 基本上也没什么用处，但如果你项目里要区分国内版和国外版甚至还要根据用户是否是VIP会员加上收费和免费的版本，这种情况下就会出现国内收费、免费国外收费、免费的版本，在极端点，我收费和免费的版本在相同页面上甚至显示的UI布局和icon图标资源都不一样，这种情况又该怎么处理呢，ProductFlavor 的出现就非常友好的帮助我们开发者解决了上述的版本区分。

# productFlavors

<img src="https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210614133929588.png" alt="image-20210614133929588" style="zoom:50%;" />

productFlavors 的用法很简单，主要是用来多渠道打包使用，直接在 android 闭包下定义 productFlavors 就可以了，在我的例子中我的APP定位为收费和免费版本，所以我需要在 **2** 处定义我的“产品风味”也就是我要打包的渠道（收费和免费版本的两个不同APP的渠道），注意下在目前的AS中如果你仅仅只是定义了 **代码2** ，那么在构建的时候必然会报 **3** 处的错误，没有定义产品纬度，所以为了解决这个问题，我们在代码1处定义了一个“是否付费 isPaying ”的纬度。

<img src="https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210614143021396.png" alt="image-20210614143021396" style="zoom:50%;" />

如图，在我们定义好了是否付费的纬度以及付费和免费的产品风味之后，你会发现现在构建出来了4个构建变体，也就是在free和charge风味下各自构建了debug和release的变体，现在我们有了两个不同的风味，如果我想安装到手机上是两个不同的APK又改怎么处理呢？首先我们的包名就不能相同，在free和charge闭包下定义:

```groovy
productFlavors{

    free{
        applicationId 'com.example.gradle.free'

    }

    charge{
        applicationId 'com.example.gradle.charge'

    }

}
```

根据上面的配置最后在手机上跑的是两个APK，这个我已经验证过了，尽管如此还是没体现出差异化，我们接下来改变下代码的逻辑，在他们的app名以及首页修改下

<img src="https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210614160956036.png" alt="image-20210614160956036" style="zoom:50%;" />



看我们的红框处，我已经将他们的app的名称全改掉了，怎么处理的呢？

```groovy
productFlavors{

    free{
        applicationId 'com.example.gradle.free'
        resValue "string",'appName','免费版'
    }

    charge{
        applicationId 'com.example.gradle.charge'
        resValue "string",'appName','收费版'
    }

}
```

首先我还是修改了 productFlavors 中的代码，定义了一个 resValue，这个参数你可以理解为在不同的风味下定义标签的意思，比如说我在代码中分别为 free 和 charge 风味定义了 resValue 标签，那么在构建他们变体的时候就可以引用到这个标签

<img src="https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210614161802905.png" alt="image-20210614161802905" style="zoom:50%;" />

如图，我还新定义了两个文件夹“free”和“charge”，如果你想做差异化的话就必须根据产品风味来定义文件夹，这里因为应用的名称是在清单文件里配置的，所以各自copy了一份，我反正理解的是既然是差异化那么从main里面将有差异的文件copy出来修改就可以了，当然了里面的包名，路径之类的还是得要一致的，讲到这其实大家也就明白了，如果你想在不同的APP页面里做差异化，那么就按照这个套路来就可以了，所以“修改首页（MainActivity）”的任务就交给大家了，自己动手体会下。

# flavorDimensions多纬度

<img src="https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210614163908276.png" alt="image-20210614163908276" style="zoom:50%;" />

什么是多纬度，比说是否付费是一个纬度，国家是一个纬度，在上图中我又定义了一个 nation 纬度，注意如果你定义了一个纬度那么必须要使用到它，在各个产品风味下通过 dimension 来决定你的风味是使用的哪一个纬度，free 和 charge使用了 isPaying ，china 和 france 使用了 nation，那么多纬度的作用也就是为了更加精细的区分你的APP，如 **3** 通过这种方式，我完全可以可以打包出不同国家的差异化APP，是不是很灵活呢，不过一般情况下也用不到那么精细，不然这个项目光是维护就很头疼，好，到最后我们可以得出一个公式 【纬度1的产品风味数量】 * 【纬度2的产品风味数量】 * buildType数量 = 最终的APK变体数量。 