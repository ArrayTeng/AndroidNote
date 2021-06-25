# 前言

因为我的好同事（博客达人）在跟我一起搭档干活后只要有空闲时间就会去写博客总结最近的知识点，把我这个渣渣搞的有点小压力，在他的带动下我也开始慢慢的写博客，感谢涛哥带动了慵懒的我，不过在写博客的过程中发现如何维护自己的图床是个非常棘手的问题，开始我使用的是聚合图床，但是有限制，后来我看有小伙伴使用的是阿里云的对象存储服务OSS来做图床，可惜太费钱，我是搞了一个阿里云的图床，但是没有自己的域名，担心这么搞哪天就失效了，索性干脆放弃这个做法，最后还是选择了使用 Github 来做图床，本篇文章我就带大家手把手的搭建自己的 Github 图床搭配 Typora 提高自己的写作效率。

> 2021年6月25日更新：新建token权限选择、解决上传图片失败的一个坑

# 动手搭建Github图床

**第一步** 新建 Github 仓库，我建议你将它命名为 resource 因为我已经有了个 resource 仓库所以这里就用 picture 来举例子。

![image-20210618154742038](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210618154742038.png)

**第二步** 建立你的仓库之后我建议你在第一时间发布一个 release 版本，因为一些不可避的原因你的图片放在 Github 里是没法直接访问到的，所以这里需要 CDN 加速，这里用到了 jsdelivr ，主要因为它免费而且大厂加持一时半会也倒不了稳定性好，**但是 jsdelivr 有个比较坑爹的是仅能针对50M以下的文件CDN加速** 。

![image-20210618155046304](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210618155046304.png)

按照我上图的步骤点击   Create a new release ，跳转到下图中的界面，按照 1、2、3的步骤依次执行，不要问为什么 2 中要写master ，我也不知道，反正这么干就OK了，记住必须是要在主分支下也就是 main 分支，设置完成后点击 Publish release 发布，发布完成之后你的图床也就设置完成了。

![image-20210618160736256](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210618160736256.png)

图床设置完成后也就剩下了如何访问，既然我们使用了 jsdelivr 来做 CDN 加速，那么访问图片时也就只需要按照 

>  https://cdn.jsdelivr.net/gh/【guithub用户名】/【仓库名按我们的例子这里是 picture】@main/【图片地址】的访问地址来访问图片就可以了

假设现在仓库中有一张图片（hello.png）放在 img 文件夹下，那么具体的访问路径就是

 https://cdn.jsdelivr.net/gh/ArrayTeng/picture@main/img/hello.png

在浏览器中打开你会发现速度66的，不过我这里只是举个例子，你们可以按照上面的步骤试试

# 使用PicGo

现在我们已经有了图床，但是自己手动上传图片难免是个非常低效的做法，这里给大家推荐 [PicGo](https://github.com/Molunerfinn/PicGo) 这个神器

![image-20210618162235695](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210618162235695.png)

你会发现它这里是可以配置Github图床的，所以在使用它的时候你首先得先配置下，我下面教大家它的用法

![image-20210618162542801](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210618162542801.png)

点击左侧的Github图床进行图床配置，按照我们文章的例子 **1** 中应该修改为 【用户名】/picture ,你也可以自己自定义，这个没啥好讲的，第二步第四步就按图片中的例子写，你也可以自己指定存储路径，我是觉得这么命名比较ok，第五步设置自定义域名格式就按 **https://cdn.jsdelivr.net/gh/【用户名】/【仓库名】@main**  来设置就可以了，至于第三步你需要按照下面的步骤获取：、

![image-20210618163405262](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210618163405262.png)

打开你的 Github 点击 setting 进入到 Developer settings 中

![image-20210618163534988](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210618163534988.png)

点击 Generate new token 来获取你的 Github token，记住一定要找个地方保存好这个 token ，不然你忘记了只能重置这个 token ，我就因为忘记了又重新重置了一下，不过问题不大，获取 token 之后就可以填入到 PicGo 中啦， 接下来你在上传区选择 Github 图床就可以愉快的上传图片了。

![image-20210625091842439](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210625091842439.png)

补充说明下，在你点击**Generate new token**按钮会跳转到上面的页面，Note你可以随便输入，下面的**Selecte scropes**按我图中所示选中，最后下拉到页面最低端点击**update token**按钮创建你的token。

# PicGo 结合 Typora



![image-20210618164048656](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210618164048656.png)

打开你的 Typora 选择文件选择偏好设置

![image-20210618164207782](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210618164207782.png)

选择图像将你的上传服务设置为 PicGo ，接下来你就可以愉快的在 Typora 中高效的上传图床了，你可以先将本地图片插入到 Typora 中，然后右击图片选中上传图片就ok了，就这么简单，你的图片会上传到Github中。

![image-20210618164518613](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210618164518613.png)

结束，到此为止，你的高效图床工具已经完美的设置完毕，总体搭建时间不超过30分钟。

# 图片上传失败的坑

## 验证失败 <font color = red>Failed to fetch</font>

![image-20210625093757262](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210625093757262.png)

今天在使用Typora上传图片的时候直接失败了，后来百度了下发现是PicGo的监听端口问题，我在Typora上验证图片上传告诉我目前的端口是36677，于是我打开PicGo设置将监听端口设置为36677，也就是修改下图中的**设置监听端口**，设置完成后发现图片上传成功，

![image-20210625093949818](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210625093949818.png)