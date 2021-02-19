# 泛型

适用于多种数据类型执行相同的代码

泛型中的类型在使用时指定，不需要强制类型转换

#### 限定类型变量

有时候，我们需要对类型变量加以约束，比如计算两个变量的最小，最大值。请问，如果确保传入的两个变量一定有compareTo方法？那么解决这个问题的方案就是将T限制为实现了接口Comparable的类

```java
public static <T extends Comparable> T min(T a,T b){
    if(a.compareTo(b) > 0){
        return  b;
    }else {
        return  a;
    }
}  
```

T **extends** Comparable中

T表示应该绑定类型的子类型，Comparable表示绑定类型，子类型和绑定类型可以是类也可以是接口。

如果这个时候，我们试图传入一个没有实现接口Comparable的类的实例，将会发生编译错误。

#### 泛型中的约束和局限性

**不能用基本类型实例化类型参数**

```java
//Restrict<boolean> restrict = new Restrict<boolean>();这是错误的
Restrict<Boolean> restrict = new Restrict<Boolean>();
```

**运行时类型查询只适用于原始类型**

```java
//if (restrict instanceof Restrict<Boolean>) 错误
//if (restrict instanceof Restrict<T>) 错误
restrict.getClass() == restrictBoolean.getClass();
```

**泛型类的静态上下文中的类型变量失效**

静态域或方法里不能引用类型变量

静态方法本身是泛型方法就行

不能在静态域或方法中引用类型变量，因为泛型是要在对象创建的时候才知道是什么类型的，而对象创建的代码执行的先后顺序是static的部分，然后才是构造函数等等，所以在对象初始化之前static的部分已经执行了，如果你在静态部分引用的泛型，那么毫无疑问虚拟机根本不知道是什么东西，因为这个时候类还没初始化。

**不能创建参数化类型的数组**

**不能实例化类型变量**

//this.data = new T(); 错误

**不能捕获泛型类的实例**

泛型类不能extends Exception/Throwable

#### 泛型类型的继承规则

泛型类可以继承或者扩展其他泛型类

#### 通配符类型



？ extends x 表示类型的上界，类型参数是 x 的子类

？ extends X 表示类型的上界，类型参数是X的子类，那么可以肯定的说，get方法返回的一定是个X（不管是X或者X的子类）编译器是可以确定知道的。但是set方法只知道传入的是个X，至于具体是X的那个子类，不知道。

总结：主要用于安全地访问数据，可以访问X及其子类型，并且不能写入非null的数据。



？ super x 表示类型的下界，类型参数是 x 的超类

？ super X 表示类型的下界，类型参数是X的超类（包括X本身），那么可以肯定的说，get方法返回的一定是个X的超类，那么到底是哪个超类？不知道，但是可以肯定的说，Object一定是它的超类，所以get方法返回Object。编译器是可以确定知道的。对于set方法来说，编译器不知道它需要的确切类型，但是X和X的子类可以安全的转型为X。

总结：主要用于安全地写入数据，可以写入X及其子类型。

#### java虚拟机是如何实现泛型的

Java 中的泛型不过是一个语法糖，在编译时还会将实际类型给擦除掉，不过会新增一个 checkcast 指令来做编译时检查，如果类型不匹配就抛出 ClassCastException。



不过呢，**字节码中仍然存在泛型参数的信息，如方法声明里的 T foo(T)，以及方法签名 Signature 中的 "(TT;)TT"，这些信息可以通过反射 Api getGenericXxx 拿到。**



除此之外，需要注意的是，泛型结合数组会有一些容易忽视的问题。



数组是协变且具体化的，数组会在运行时才知道并检查它们的元素类型约束，可能出现编译时正常但运行时抛出 ArrayStoreException，所以尽可能的使用列表，这就是 Effective Java 中推荐的列表优先于数组的建议。



这在我们看集合源码时也能发现的到，比如 ArrayList，它里面存数据是一个 Object[]，而不是 E[]，只不过在取的时候进行了强转。



还有就是利用通配符来提升 API 的灵活性，简而言之即 PECS 原则，上取下存。



典型的案例即 Collections.copy 方法了：



```java
Collections.copy(List<? super T> dest, List<? extends T> src);
```



# 反射与代理

我们写的每一个类都可以看成一个对象，是java.lang.Class类的对象

**每一个类对应的Class放在哪里？里面都保存了些什么？**

我们写的每一个类都可以看作是一个 Class 类的对象，当写完一个类的java文件编译成class文件的时候，编译器会将这个类对应的class对象放在class文件的末尾，保存了类的元数据信息

**动态代理的实现原理**

调用Proxy的newProxyInstance ，核心代码主要有

final Class<?>[] intfs = interfaces.clone();//获取实现的接口

Class<?> cl = getProxyClass0(loader, intfs);//获取代理类

cons.newInstance(new Object[]{h})//生成代理类的实例

```java
private static Class<?> getProxyClass0(ClassLoader loader,
                                       Class<?>... interfaces) {
   //从缓存中获取代理类，同时生成的操作也在这里
    return proxyClassCache.get(loader, interfaces);
}
```

```java
Object subKey = Objects.requireNonNull(subKeyFactory.apply(key, parameter));

//执行 ProxyClassFactory 的 apply
//生成代理类的字节码
 byte[] proxyClassFile = ProxyGenerator.generateProxyClass(
                proxyName, interfaces, accessFlags);

//最终生成代理类 
defineClass0(loader, proxyName,proxyClassFile, 0, proxyClassFile.length);
```

