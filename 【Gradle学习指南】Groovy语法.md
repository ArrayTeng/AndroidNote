
---
theme: cyanosis
highlight: atom-one-light
---

# 前言
公司的项目中也运用到了自定义Gradle插件，不过我本人是全程没有参与，一个是因为不是我负责的另一个原因是我Gradle相关的知识储备是零，正好自己最近也学习到这一块的，本着好记性不如烂笔头的思想我就把自己学习到的东西一股脑的全都整理处理，以周为单位争取每周能整理出一篇文章巩固自己的学习成果，Gradle作为一款基于Apache的Ant和Maven概念的项目自动化构建工具是基于java实现的，所以可以把Gradle看做是一个轻量级的java应用，Gradle使用Groovy、Kotlin等语言编写自定义脚本，取代了Ant和Maven使用XML配置的方式很大程度上简化了对项目构建要做的配置，使用起来更加灵活和强大，本篇文章着重学习Groovy语法，这里我给大家推荐一下 [Groovy教程](https://www.w3cschool.cn/groovy/),本篇文章的知识脑图请看下面。
 
 

![Groovy基本语法.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/13c181b5ecc9489898788d034cee60e3~tplv-k3u1fbpfcp-watermark.image)



# 1、什么是DSL
DSL是Domain-specific language(领域特定语言)的缩写，指的是专注于某个应用程序领域的计算机语言。

# 2、字符串
Groovy中的字符串可以用单引号`（'）`，双引号`（“`）或三引号`（”“”）`括起来。此外，由三重引号括起来的Groovy字符串可以跨越多行，在双引号里可以通过 `${ }` 的方式进行表达式计算，如果 $ 符号后面只需要跟一个变量可以省略 { } 。
```groovy
String a = 'Hello World !'
String b = "Hello Groovy ! ${a}"
String c = """ 
 Hello 
 Groovy !"""
```
执行结果：

```java
Hello World !
Hello Groovy ! Hello World !
 
         Hello 
         Groovy !
```

# 3、数据类型
## 基本数据类型
Groovy提供多种内置数据类型，以下是在Groovy中定义的数据类型的列表，不过你在定义变量的时候同样可以使用 `def` 来定义，也就是类型推导帮助你指定数据类型不用再显示的定义数据类型。

- byte -这是用来表示字节值。例如2。

- short -这是用来表示一个短整型。例如10。

- int -这是用来表示整数。例如1234。

- long -这是用来表示一个长整型。例如10000090。

- float -这是用来表示32位浮点数。例如12.34。

- double -这是用来表示64位浮点数，这些数字是有时可能需要的更长的十进制数表示。例如12.3456565。

- char -这定义了单个字符文字。例如“A”。

- Boolean -这表示一个布尔值，可以是true或false。

- String -这些是以字符串的形式表示的文本。例如，“Hello World”的。

## 数据类型范围

| 类型 | 范围 |
| --- | --- |
| byte| -128到127 |
| short| -32,768到32,767 |
| int| 2,147,483,648 到,147,483,647 |
| long| -9,223,372,036,854,775,808到+9,223,372,036,854,775,807 |
| float| 1.40129846432481707e-45到3.40282346638528860e + 38 |
| double| 4.94065645841246544e-324d 到1.79769313486231570e + 308d |

## 包装器类型
这里和java一样除了基本数据类型之外还有针对基本数据类型的包装类型：
- java.lang.Byte
- java.lang.Short
- java.lang.Integer
- java.lang.Long
- java.lang.Float
- java.lang.Double

可以借助 BigInteger 和 BigDecimal 支持高精度的计算

```groovy
BigDecimal d = 20.48
BigInteger e = 30
```

# 4、方法
## 方法返回值
Groovy方法的返回值可以使用具体的数据类型或者 def 来表示，可以添加修饰符，如 public，private 和 protected。默认情况下，如果未提供可见性修饰符，则该方法为 public，Groovy会默认把执行过程中的最后一行代码当做返回值，所以你也可以把 return 给去掉

```groovy
//def 当做返回值类型（自动类型推导）
def method(){
    return "Groovy方法的返回值"
}

//Groovy会默认把执行过程中的最后一行代码当做返回值
def method(def a){
   if(a == 1){
       1
   }else{
       2
   }
}

```

## 方法可以添加默认值
在groovy方法中参数可以添加默认值，调用method方法时如果没有传参就默认使用定义的默认值

```groovy
//在groovy方法中参数可以添加默认值，调用method方法时如果没有传参就默认使用定义的默认值
def method(def a = 1){
   if(a == 1){
       1
   }else{
       2
   }
```

## 可以省略括号
Groovy方法可以省略括号让代码更加简短点，比如下面的 method 方法

```groovy
//定义method方法
def method(int a){
    print(a)
}

//正常写法
e.method(1)

//省略括号写法
e.method 1


```
如果方法的参数有两个比如说 `method(int a,int b)`

```groovy
def method(int a,int b){
    print("$a $b")
}

e.method 1,3
```


# 5、运算符重载
Groovy同样具备运算符重载的功能，调用运算符其实也就是调用类中的方法，这里简单的操作下 + 号运算符的重载

```groovy
class Example {

    int number

    static void main(String[] args) {

        Example e1 = new Example(number: 1)

        Example e2 = new Example(number: 5)

        print(e1 + e2)

    }
    
    //plus 方法对应运算符 + 号
    def plus(Example e){
        number+=e.number
    }

}
```
运算符重载很简单，也就是重写Groovy中的运算符所映射到的方法，相对应的映射方法可以看下表

![企业微信截图_16219234118134.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/e550ce4287ac4ba8b325e3e9dcd3c072~tplv-k3u1fbpfcp-watermark.image)


# 6、范围
Groovy中范围的定义，由于Range是List的直接子类，所以操作Range的时候可以使用List相关的API

```groovy
class Example {
    
    static void main(String[] args) {

       Range range01 = 1..10//包含范围的示例

        def range02 = 1..<10//独占范围的示例

        def range03 = 'a'..'z'//范围也可以由字符组成

        def range04 = 10..1//范围也可以按降序排列

        def range05 = 'z'..'a'//范围也可以由字符组成并按降序排列

        range01.forEach{
            print(it)
        }
        range02.forEach{
            print(it)
        }
        range03.forEach{
            print(it)
        }
        range04.forEach{
            print(it)
        }
        range05.forEach{
            print(it)
        }
        
    }
}
```

# 7、Map
Groovy中的Map的定义是一种键值对的形式 `def map = ['上海': 'shanghai', '广州': 'guangzhou',]`


```groovy
def map = ['上海': 'shanghai', '广州': 'guangzhou',]
//访问map元素的方式
def a = map.上海
def b = map.get('广州')
def c = map['上海']
println("$a $b $c")
```
执行输出 `shanghai guangzhou shanghai`


```groovy
def map = ['上海': 'shanghai', '广州': 'guangzhou',]
//添加元素
map['合肥'] = 'hefei'
//删除元素
map.remove('上海')
//修改元素
map.广州 = '广州很热'
//遍历元素
map.each {
    print("key的值为${it.key} Value的值为${it.value}")
}
//查找元素
def  res = map.find {
    if (it.value == '广州很热') {
        return '广东需要装空调了'
    }
    return 'no find city'
}
```

# 8、List 和 Array
## List
定义一个空集合 `def list = []` ，或者显示的定义 `ArrayList<Integer> list = []`

```groovy
//定义一个List
ArrayList<Integer> list = []
//添加元素
(1..10).each {
    list.add(it)
}
//遍历List
list.each {
    print(it)
}
//带有下标的遍历
list.eachWithIndex { int entry, int i ->
    print("value${entry} 下标${i}")
}
//通过下标访问元素
def res = list[0]
print(res)
```

## Array
Groovy中的数组必须显示的定义 `Integer[] array = [1,3,9,1,4,6,0,3,6,7,4]` 或者通过 `as` 来标记这是个什么类型的数组 `def array = [1,3,9,1,4,6,0,3,6,7,8] as Integer[]`

```groovy
def array = [1,3,9,1,4,6,0,3,6,7,8] as Integer[]
//遍历所有元素
array.each {
    print(it)
}
//根据下标来获取元素
def res = array[0]
//根据条件查找第一个符合条件的元素
def findRes = array.find {
    it == 3
}
//查找所有符合条件的元素并返回一个集合
def findAllList = array.findAll{
    it == 3
}
//只要有一个条件符合就返回 true
boolean isAny = array.any {
    it == 3
}
//所有条件符合才返回 true
boolean  isEvery = array.every {
    (it % 3) == 0
}
```

# 9、闭包
你可以把闭包看做是一个独立的方法代码块，并且这个方法还是个对象也就是 Closure ，你可以这么理解 Closure 对象就代表一个匿名函数，可以接受参数，返回值并分配给变量。
## 使用闭包
使用闭包其实很简单，我们实现一个小功能来实现一个求和的功能，定义一个方法入参一个数值要求从 0 ~ n 求和，这里拿到值后丢给闭包处理

```groovy
def sum = 0
pickSum(100,{
    sum+=it
})

static def pickSum(n,block){
    for(int i=0;i<=n;i++){
        block(i)
    }
}
```
这里的 pickSum 是一个高阶函数，可以把函数当做一个入参或者把函数当做返回值，在示例代码中遍历 0 ~ n 将数值传递给一个 block 代码块处理，这个 block 代码块就可以看做是一个闭包，同时如果闭包是函数中最后一个参数那么代码也可以写成下面，当闭包是方法调用的最后一个实参的时候可以把闭包附在方法调用上，如果只向闭包中传递一个参数，就可以使用这个变量 `it`来表示这个参数，不过你也可以用其它名字命名 

```groovy
pickSum(100){
    sum+=it
}
```
定义闭包的时候既可以在方法调用的时候即时创建闭包也可以将闭包赋值给变量方便复用，还是上面的例子，这里我们将 block 给抽出来

```groovy
def sum = 0
def block = {
    sum+=it
}
pickSum(100,block)
```
对于单个参数的闭包可以使用 it 来指定变量，但是如果是多个参数就必须使用自己的命名了

```groovy
def block = {
    int a,int b -> a+b
}
 def sum(int a,int b){
    return  block(a,b)
}

print(example.sum(1,5))

//输出结果 6 

```
## 动态闭包
可以确定闭包是否存在，如果存在就使用闭包否则就使用默认的实现,在下面的代码中如果定义了闭包就使用闭包否则就默认使用默认的实现

```Groovy
void sum(block){
   if(block){
       block()
   }else{
       print("使用默认的 default value")
   }
}

 example.sum{
     print("使用了闭包")
 }
 
 example.sum()
 
```
可以通过 `maximumNumberOfParameters` 方法来获取闭包里传递的参数，除此之外还可以通过 `parameterTypes` 来获取闭包入参的数据类型

```Groovy
def res = example.sum(3){
    num,rate -> num * rate
}
def res2 = example.sum(3){
    it * 33
}
print(res2)


def sum(int num,Closure block){
    for (param in block.parameterTypes){
        println(param.name)
    }
   if(block.maximumNumberOfParameters == 2){
       block(num,4)
   }else{
       block(num)
   }
}

```

## 闭包委托
在闭包内部有三个只能在闭包内部使用的属性this、owner、delegate，可以直接使用this、owner、delegate调用，this对应定义闭包的那个类如果是在内部类那么就指向内部类，owner对应于定义闭包的那个类或者闭包，如果在闭包中定义，对应闭包，否则同this一致，delegate和owner一致或者自定义delegate指向，设置delegate的意义就在于将闭包和一个具体的对象关联起来


```Groovy
class Example {

    String name

    int age

    void eat(String food){
        print("like eat ${food}")
    }

    @Override
    String toString() {
        return "name = $name age = $age"
    }

    static void main(String[] args) {
        //定义一个闭包在闭包里修改对象的相关属性，并且可以在闭包里调用对象方法，在闭包中可以访问被代理对象的属性和方法
        def block = {
            name = 'tengfei'
            age = 18
            eat('KFC')
        }

        Example example = new Example(name: 'feifei',age: 28)

        println(example.toString())
        //通过delegate将闭包和具体的对象关联起来
        block.delegate = example
        block.call()

        println(example.toString())
        
    }

}
```





