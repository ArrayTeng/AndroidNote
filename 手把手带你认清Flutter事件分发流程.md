# 前言



# GestureDetector

之所以先谈 GestureDetector 主要是因为该组件封装了我们日常开发中的绝大多数手势可以完美解决80%手势相关的需求，比如说日常中单击、双击、竖直拖拽、水平拖拽、平移手势、长按、按压、缩放，但是也仅仅只限于此了，超出了这八大范畴 GestureDetector 就表示爱陌无助了，如果你想写一个特殊的手势如在界面上打一个勾号然后回调出这个手势成功的监听那么按照目前 GestureDetector 的封装是没法完成这个需求的，但其实 GestureDetector 中八大手势都是通过“手势识别器”来管理的，对于我们看 GestureDetector 源码只需要关注手势识别器是如何创建的也就是“手势识别器工厂”以及手势识别器是如何绑定监听的，这两个概念我在下文中会讲到。

首先我们来看 GestureDetector 组件源码，GestureDetector 作为一个 StatelessWidget 其中的内部源码并不复杂，对于一个 StatelessWidget 的组件而言我们仅仅只需要关注它的 build 函数，在 GestureDetector  的 build 函数中首先会去新建一个Map集合保存“手势识别工厂类 GestureRecognizerFactory ”。

![image-20210728100704404](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210728100704404.png)

可以看到这里是一个空集合也就说明在接下来的步骤中会有填充数据的操作，gestures 集合的 key 为 Type，value 为 GestureRecognizerFactory 也就是我在前文中提到的“手势识别器工厂”。

```dart
//手势识别器工厂
@optionalTypeArgs
abstract class GestureRecognizerFactory<T extends GestureRecognizer> {
  
  const GestureRecognizerFactory();

  T constructor();

 
  void initializer(T instance);

//......
}

//具体的手势识别器工厂
class GestureRecognizerFactoryWithHandlers<T extends GestureRecognizer> extends GestureRecognizerFactory<T> {
  
  const GestureRecognizerFactoryWithHandlers(this._constructor, this._initializer)
    : assert(_constructor != null),
      assert(_initializer != null);

  final GestureRecognizerFactoryConstructor<T> _constructor;

  final GestureRecognizerFactoryInitializer<T> _initializer;

  @override
  T constructor() => _constructor();

  @override
  void initializer(T instance) => _initializer(instance);
}
```

可以看到这个 GestureRecognizerFactory 是一个抽象类，也就意味肯定会有一个具体的实现子类，它的结构很简单，对我们有用的其实就两个函数以及一个泛型类型：

- 泛型类型 T 的上限是 GestureRecognizer ，GestureRecognizer 就是一个手势识别器的基类，先不用管什么是手势识别器，先记住手势识别器是具体手势的操作者，心中先要有这个概念
- constructor( ) 用于构建手势识别器并返回，这个在 build 函数接下来的代码中你会看到具体的实践
- initializer(T instance)  用于暴露构建好的手势识别器并交给我们来使用

手势识别器工厂类就这么简单，没用冗余代码清清爽爽职责明确，但我们发现它其实就是一个抽象类，在代码块中发现它的具体实现子类是 GestureRecognizerFactoryWithHandlers ，查看它的代码其实也就是重写了 GestureRecognizerFactory  的两个函数并给它们设置了两个回调  _constructor( ) 和 _initializer(instance)，并在构造函数中赋值，这也就意味着在新建 GestureRecognizerFactoryWithHandlers  对象的时候也就将 constructor 和 initializer 给暴露给调用方了，讲完了这些我们来看下具体的使用，由于它的 build 函数里的代码还是比较长的所以我这里就截取一部分展示（仅仅只展示单击手势）。

![image-20210728104854683](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210728104854683.png)

大家在写 Flutter 应用的时候肯定有使用过 GestureDetector 包裹在需要处理点击事件的控件外层，在截图中截取了处理单击事件的操作，不过这里其实不是真正的处理事件而是绑定监听以及添加映射关系，GestureDetector  中封装了**代码1**中 回调函数，如果你在代码中调用了这些函数会继续执行到**代码2**中，**代码2**添加了映射关系，key为你具体的手势识别器，这里看的是 TapGestureRecognizer 单击手势识别器，而 value 则是手势识别器工厂，你会发现在它的构造函数中分别新建了 TapGestureRecognizer  对象以及调用了  _initializer 方法，在 _initializer 中将 GestureDetector  中定义的点击回调绑定给了 TapGestureRecognizer  也就是所谓的绑定监听，到这里你会发现 GestureDetector  也就是一个壳子，具体的手势执行操作在其内部对应着不同的手势识别器，前文只是拿 TapGestureRecognizer  举例子，实际上 GestureDetector  中封装了八种不同的手势识别器分别对应着：单击手势识别器（TapGestureRecognizer）、双击手势识别器（DoubleTapGestureRecognizer）、长按手势识别器（LongPressGestureRecognizer）、竖直拖拽手势识别器（VerticalDragGestureRecognizer）、水平拖拽手势识别器（HorizontalDragGestureRecognizer）、平移手势手势识别器（PanGestureRecognizer）、缩放手势识别器（ScaleGestureRecognizer）、按压手势识别器（ForcePressGestureRecognizer），GestureDetector  的内容就这么多，它作为一个包装类封装了八大常用的手势识别器，不同的手势操作交代给不同的手势识别器，将手势识别器与其工厂类建立映射关系并在 build 函数中将这个 map 集合（gestures）交给 RawGestureDetector。

# RawGestureDetector

![image-20210728135418298](https://cdn.jsdelivr.net/gh/ArrayTeng/resource@main/img/image-20210728135418298.png)

前文中提到过 GestureDetector 是一个壳子，那么 RawGestureDetector 就是参与了具体的手势相关逻辑的执行者，GestureDetector 的返回值就是 RawGestureDetector  ，并且前面的 gestures 对象也被传递给了它，RawGestureDetector  是一个 StatefulWidget ，一般情况下我们只有两种情况会需要使用到 StatefulWidget 。

- 当前的控件在某种情况下需要改变自身的状态
- 需要借助 StatefulWidget 的生命周期函数完成

# 参考资料

[1、Flutter 手势探索 - 执掌天下](https://juejin.cn/book/6896378716427911181)

