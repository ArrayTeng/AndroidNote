
# 关于Dalvik、ART、DEX、JIT、AOT

**Android Runtime**（缩写为ART），是一种在[Android](https://zh.wikipedia.org/wiki/Android)操作系统上的[运行环境](https://zh.wikipedia.org/wiki/运行环境)，由[Google公司](https://zh.wikipedia.org/wiki/Google公司)研发，并在2013年作为[Android 4.4](https://zh.wikipedia.org/wiki/Android_4.4)系统中的一项测试功能正式对外发布，在[Android 5.0](https://zh.wikipedia.org/wiki/Android_5.0)及后续Android版本中作为正式的运行时库取代了以往的[Dalvik虚拟机](https://zh.wikipedia.org/wiki/Dalvik虚拟机)。ART能够把应用程序的[字节码](https://zh.wikipedia.org/wiki/字节码)转换为[机器码](https://zh.wikipedia.org/wiki/機器碼)，是Android所使用的一种新的[虚拟机](https://zh.wikipedia.org/wiki/虚拟机)。它与Dalvik的主要不同在于：Dalvik采用的是[JIT](https://zh.wikipedia.org/wiki/JIT)技术，而ART采用[Ahead-of-time](https://zh.wikipedia.org/w/index.php?title=Ahead-of-time_compilation&action=edit&redlink=1)（AOT）技术。ART同时也改善了性能、[垃圾回收](https://zh.wikipedia.org/wiki/垃圾回收_(計算機科學))（Garbage Collection）、应用程序出错以及性能分析。

JIT最早在[Android 2.2](https://zh.wikipedia.org/wiki/Android_2.2)系统中引进到Dalvik虚拟机中，在应用程序启动时，JIT通过进行连续的[性能分析](https://zh.wikipedia.org/wiki/性能分析)来优化程序代码的执行，在程序运行的过程中，Dalvik虚拟机在不断的进行将字节码编译成机器码的工作。与Dalvik虚拟机不同的是，ART引入了AOT这种预编译技术，在应用程序安装的过程中，ART就已经将所有的字节码重新编译成了机器码。应用程序运行过程中无需进行实时的编译工作，只需要进行直接调用。因此，ART极大的提高了应用程序的运行效率，同时也减少了手机的电量消耗，提高了移动设备的续航能力，在垃圾回收等机制上也有了较大的提升。为了保证[向下兼容](https://zh.wikipedia.org/wiki/向下兼容)，ART使用了相同的Dalvik字节码文件（dex），即在应用程序目录下保留了dex文件供旧程序调用，然而.odex文件则替换成了[可执行与可链接格式](https://zh.wikipedia.org/wiki/可執行與可鏈接格式)（ELF）可执行文件。一旦一个程序被ART的dex2oat命令[编译](https://zh.wikipedia.org/wiki/编译)，那么这个程序将会指通过ELF[可执行文件](https://zh.wikipedia.org/wiki/可执行文件)来运行。因此，相对于Dalvik虚拟机模式，ART模式下Android应用程序的安装需要消耗更多的时间，同时也会占用更大的内部储存空间，用于储存编译后的代码，但节省了很多Dalvik虚拟机用于实时编译的时间。

Google公司在Android 4.4中带来的ART模式仅仅是ART的一个预览版，系统默认仍然使用的是Dalvik虚拟机，4.4上面提供的预览版ART相对于Android 5.0以后的ART运行时库有较大的不同，尤其体现在兼容性上。

摘录自[维基百科 Android Runtime](https://zh.wikipedia.org/wiki/Android_Runtime)





# 双亲委托机制

## PathClassLoader 与 DexClassLoader

Android给我们提供了PathClassLoader和DexClassLoader用于加载dex文件，两者都继承自BaseDexClassLoader，就代码实现和用法上没有任何区别，只不过区别是DexClassLoader可以指定 optimizedDirectory，也就是dex2oat的产物 odex 存放的位置，而 PathClassLoader 只能使用系统默认位置。但是这个 optimizedDirectory 在 Android 8.0（api 26） 以后也被舍弃了，只能使用系统默认的位置了。

## ClassLoader源码分析

某个类加载器在加载类时，如果将加载任务委托给父类加载器，一次递归，如果父类加载器可以完成类加载任务，就成功返回；只有父类加载器无法完成此加载任务或者没有父类加载器的时候才自己去加载。




```java
    protected Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
            // 检查这个 Class 文件是否已经被加载过了
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                try {
                  //如果没有被加载检查是否有父ClassLoader，将它委托给父ClassLoader
                    if (parent != null) {
                        c = parent.loadClass(name, false);
                    } else {
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    //如果父ClassLoader没有找到对应的类执行findClass函数自己去查找
                    c = findClass(name);
                }
            }
            return c;
    }
```



```java
//ClassLoader是一个抽象类， findClass由具体的实现类去处理
//BaseDexClassLoder.java
@Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // First, check whether the class is present in our shared libraries.
        if (sharedLibraryLoaders != null) {
            for (ClassLoader loader : sharedLibraryLoaders) {
                try {
                    return loader.loadClass(name);
                } catch (ClassNotFoundException ignored) {
                }
            }
        }
        
        List<Throwable> suppressedExceptions = new ArrayList<Throwable>();
      //核心代码在 pathList.findClass , pathList是DexPathList 
        Class c = pathList.findClass(name, suppressedExceptions);
        if (c == null) {
            ClassNotFoundException cnfe = new ClassNotFoundException(
                    "Didn't find class \"" + name + "\" on path: " + pathList);
            for (Throwable t : suppressedExceptions) {
                cnfe.addSuppressed(t);
            }
            throw cnfe;
        }
        return c;
    }
```

```java
//执行 DexPathList 的findClass函数 ，DexPathList中维护了Element数组，初始化时机在BaseDexClassLoader的构造函数中
public Class<?> findClass(String name, List<Throwable> suppressed) {
  //核心代码在 dexElements 数组，Element可以看作是dex文件的包装，Element的findClass函数最终会执行 DexFile的loadClassBinaryName 函数，最终的加载操作是交给一个本地函数 loadClassBinaryName 来执行的
        for (Element element : dexElements) {
          //每一个Element你就可以理解为是一个dex，通常情况下一个apk只有一个dex文件，所以你在执行热修复的操作的时候可以将问题代码打包成dex，确保将修复好的dex文件塞入到“问题dex”前面
            Class<?> clazz = element.findClass(name, definingContext, suppressed);
            if (clazz != null) {
                return clazz;
            }
        }

        if (dexElementsSuppressedExceptions != null) {
            suppressed.addAll(Arrays.asList(dexElementsSuppressedExceptions));
        }
        return null;
    }
```

```java
// Element 的 findClass 函数，这里需要注意DexFile这个类，这个类主要用来操作dex文件
public Class<?> findClass(String name, ClassLoader definingContext,
                List<Throwable> suppressed) {
            return dexFile != null ? dexFile.loadClassBinaryName(name, definingContext, suppressed)
                    : null;
        }
//DexFile
    @UnsupportedAppUsage
    public Class loadClassBinaryName(String name, ClassLoader loader, List<Throwable> suppressed) {
        return defineClass(name, loader, mCookie, this, suppressed);
    }

    private static Class defineClass(String name, ClassLoader loader, Object cookie,
                                     DexFile dexFile, List<Throwable> suppressed) {
        Class result = null;
        try {
            //这里执行的就是一个 native 函数
            result = defineClassNative(name, loader, cookie, dexFile);
        } catch (NoClassDefFoundError e) {
            if (suppressed != null) {
                suppressed.add(e);
            }
        } catch (ClassNotFoundException e) {
            if (suppressed != null) {
                suppressed.add(e);
            }
        }
        return result;
    }

```

## 如何修复问题代码

考虑到类加载机制不会重复加载已经加载过的类，所以如果想修复你的问题代码解决方案可以考虑将已经修复好的代码的加载时机安排到问题代码前面，在前面类加载ClassLoader的源码分析中我们其实已经发现对于Android而言它在加载一个类的时候实际上是在遍历一个 **Element 数组**，这个数组维护的Element对象就可以看做是对dex文件的一个包装，那么处理方案其实很简单了，自己造一个Element对象插到数组前面，确保被修复的代码的加载时机在主dex前面就可以了。

```java
//BaseDexClassLoader.java    
public BaseDexClassLoader(String dexPath,
            String librarySearchPath, ClassLoader parent, ClassLoader[] sharedLibraryLoaders,
            boolean isTrusted) {
        super(parent);
       
        this.sharedLibraryLoaders = sharedLibraryLoaders == null
                ? null
                : Arrays.copyOf(sharedLibraryLoaders, sharedLibraryLoaders.length);
    //初始化一个类加载器的时候，在构造函数中构建了 DexPathList 对象
        this.pathList = new DexPathList(this, dexPath, librarySearchPath, null, isTrusted);

        reportClassLoaderChain();
    }

//DexPathList中维护了Element数组，在它的构造函数中调用了 makeDexElements函数来构建Element数组
   this.dexElements = makeDexElements(splitDexPath(dexPath), optimizedDirectory,
                                           suppressedExceptions, definingContext, isTrusted);

```



```java
//ClassLoader在被创建的时候我们APP的dex文件已经包装到Element数组中，通过反射借助makeDexElements函数可以创建我们需要的数组对象并插入到原Element数组中。
    public class EnjoyFix {

    private static final String TAG = "EnjoyFix";

    public static void init(Context context) {
        try {
            File hackDir = context.getDir("hack", Context.MODE_PRIVATE);
            File hackDex = new File(hackDir, "hack.dex");
            if (!hackDex.exists()){
                InputStream is = context.getAssets().open("hack.dex");
                FileOutputStream fos = new FileOutputStream(hackDex);
                byte[] buffer = new byte[2048];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer,0,len);
                }
                is.close();
                fos.close();
            }
            installPatch(context,hackDex);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void installPatch(Context context, File patch) {
        ClassLoader classLoader = context.getClassLoader();
        List<File> files = new ArrayList<>();
        files.add(patch);
        File dexOptDir = context.getCacheDir();

        try {
            if (Build.VERSION.SDK_INT >= 23) {
                V23.install(classLoader, files, dexOptDir);
            } else if (Build.VERSION.SDK_INT >= 19) {

            } else { //   if (Build.VERSION.SDK_INT >= 14)

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Installer for platform versions 23.
     */
    private static final class V23 {

        private static void install(ClassLoader loader, List<File> additionalClassPathEntries,
                                    File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException, IOException {
            //找到 pathList
            Field pathListField = ShareReflectUtil.findField(loader, "pathList");

            Object dexPathList = pathListField.get(loader);
            ArrayList<IOException> suppressedExceptions = new ArrayList<>();

            // 从 pathList找到 makePathElements 方法并执行
            Object[] objects = makePathElements(dexPathList,
                    new ArrayList<>(additionalClassPathEntries), optimizedDirectory,
                    suppressedExceptions);

            //将原本的 dexElements 与 makePathElements生成的数组合并
            ShareReflectUtil.expandFieldArray(dexPathList, "dexElements", objects);
            if (suppressedExceptions.size() > 0) {
                for (IOException e : suppressedExceptions) {
                    Log.w(TAG, "Exception in makePathElement", e);
                    throw e;
                }

            }
        }

        /**
         * A wrapper around
         * {@code private static final dalvik.system.DexPathList#makePathElements}.
         */
        private static Object[] makePathElements(
                Object dexPathList, ArrayList<File> files, File optimizedDirectory,
                ArrayList<IOException> suppressedExceptions)
                throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            Method makePathElements = null;
            try {
                makePathElements = ShareReflectUtil.findMethod(dexPathList, "makePathElements", List.class, File.class,
                        List.class);
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "NoSuchMethodException: makePathElements(List,File,List) failure");
                try {
                    makePathElements = ShareReflectUtil.findMethod(dexPathList, "makePathElements", ArrayList.class, File.class, ArrayList.class);
                } catch (NoSuchMethodException e1) {
                    Log.e(TAG, "NoSuchMethodException: makeDexElements(ArrayList,File,ArrayList) failure");
                }
            }

            if (makePathElements == null){
                throw new RuntimeException("fix error");
            }

            return (Object[]) makePathElements.invoke(dexPathList, files, optimizedDirectory, suppressedExceptions);
        }
    }



}


```



# 参考资料

1、[Android Code Search](https://cs.android.com/)

2、[维基百科 Android Runtime](https://zh.wikipedia.org/wiki/Android_Runtime)

