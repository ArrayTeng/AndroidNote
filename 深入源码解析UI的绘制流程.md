


# View的绘制流程
## setContentView


![View的绘制流程.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/c3990040e5c34717a29a070673bd3afa~tplv-k3u1fbpfcp-watermark.image)
如果你熟悉 Activity 的启动流程的话那么对时序图 ActivityThread 中 handleLaunchActivity 方法不陌生，该方法在启动 Activity 后会被调用执行 performLaunchActivity，在 performLaunchActivity 内部通过反射创建 Activity 对象。

```java
//ActivityThread#performLaunchActivity
private Activity performLaunchActivity(ActivityClientRecord r, Intent customIntent) {
    Activity activity = null;
    java.lang.ClassLoader cl = appContext.getClassLoader();
    //Instrumentation的newActivity方法中通过反射创建Activity对象
    activity = mInstrumentation.newActivity(cl, component.getClassName(), r.intent);
    
    //执行Activity的performCreate方法
    mInstrumentation.callActivityOnCreate(activity, r.state, r.persistentState);
    
    return activity;
}

//Instrumentation#newActivity
public Activity newActivity(ClassLoader cl, String className,Intent intent){
    return (Activity)cl.loadClass(className).newInstance();
}

//Instrumentation#callActivityOnCreate
public void callActivityOnCreate(Activity activity, Bundle icicle) {
    prePerformCreate(activity);
    //调用Activity的performCreate方法，并在内部继续回调到Activity的onCreate方法
    activity.performCreate(icicle);
    postPerformCreate(activity);
}
```
在 Activity 对象被创建好后调用 Instrumentation 的 callActivityOnCreate ，Instrumentation 可以看做是一个“管理类”负责监控 Activity 的创建以及生命周期的回调以及启动等，在此函数内部继续调用 Activity 的 performCreate 方法其实也就是调用到了 onCreate 方法，我们在写布局的时候在 onCreate 中通过  setContentView 传入了布局文件，在 setContentView 内部其实就是调用了 PhoneWindow 的 setContentView。
```java
public void setContentView(int layoutResID) {
    
    if (mContentParent == null) {
        //代码1：初始化顶级父容器 DecorView
        installDecor();
    } else if (!hasFeature(FEATURE_CONTENT_TRANSITIONS)) {
        mContentParent.removeAllViews();
    }
    
    //代码2:将我们自己写的布局文件添加到 mContentParent 中
    mLayoutInflater.inflate(layoutResID, mContentParent);
    
  //...
}

//PhoneWindow#installDecor
private void installDecor(){
    
    if (mDecor == null) {
        //代码3
        mDecor = generateDecor(-1);
    }

    if(mContentParent == null){
        //代码4
        mContentParent = generateLayout(mDecor);
    }

}


```
在 PhoneWindow#setContentView `代码1处` 首先调用了 installDecor 方法，installDecor 中执行了 generateDecor 和 generateLayout ，`代码3处`generateDecor 方法初始化了 DecorView，DecorView 可以看做是Android View体系中最顶级的View ，其实就是一个 ViewGroup 对象，generateLayout 中传入了 DecorView ，generateLayout 中定义了一个变量 layoutResource ，用来保存在不同主题下的基本布局，也就是我们新建一个项目时你在IEDA上看到的第一个布局样式。
```java
//PhoneWindow#generateLayout
protected ViewGroup generateLayout(DecorView decor) {
    //用来保存布局文件
    int layoutResource;
    //省略代码根据不同的主题给 layoutResource 赋值
    //将 layoutResource 布局文件添加到 DecorView上
    mDecor.onResourcesLoaded(mLayoutInflater, layoutResource);
    //返回资源id为 com.android.internal.R.id.content 的View对象
    ViewGroup contentParent = (ViewGroup)findViewById(ID_ANDROID_CONTENT);
    
    return contentParent;
}
```
generateLayout 方法内部首先定义了一个变量 layoutResource 用来保存在不同主题下的布局文件，调用 DecorView#onResourcesLoaded 将 layoutResource 布局文件添加到 DecorView 中，`(ViewGroup)findViewById(ID_ANDROID_CONTENT)` 返回 layoutResource 敲定好后控件id为 `com.android.internal.R.id.content` 的控件对象，截止到目前为止，DecorView 被初始化，layoutResource 被添加到 DecorView 中，回到上面的`代码2`中执行将自己定义的布局文件添加到 mContentParent 中，所以到了这一步你的布局文件已经被解析好了，但是记住在这里没有发现任何一个执行布局测量，布局以及绘制的操作，这也是为什么在onCreate中你直接获取View的宽高获取不到的原因，因为在setContentView中只是执行了解析布局文件以及处理布局层级的操作，并最终得到了下面的结构：

![VIew的布局层级.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/db9528a3275c4f2b8d5894442ee45570~tplv-k3u1fbpfcp-watermark.image)


## View是如何被添加到Window中的
前文中已经分析完了在 setContentView 中我们自己写的布局文件是怎么叠加在 DecorView 中的以及 View 的布局层级结构，但是 onCreate 方法中是不涉及到 View 对象的测量，布局以及绘制的，仅仅只是处理布局的解析而已，那么在什么时候处理这些事务呢，处理这些事务的具体流程是什么？核心代码在 ActivityThread 的handleResumeActivity 方法中，我们跟着一起看下：

```java
 final void handleResumeActivity(IBinder token,
         boolean clearHide, boolean isForward, boolean reallyResume, int seq, String reason) 
     
     //执行Activity的onResume方法
     r = performResumeActivity(token, clearHide, reason);
     if (r != null) {
         final Activity a = r.activity;
         boolean willBeVisible = !a.mStartedActivity;

         if (r.window == null && !a.mFinished && willBeVisible) {
             //获取Activity里的PhoneWindow对象
             r.window = r.activity.getWindow();
             //根据PhoneWindow获取DecorView对象
             View decor = r.window.getDecorView();
             decor.setVisibility(View.INVISIBLE);
             //获取 WindowManagerImpl ，ViewManager是一个接口
             ViewManager wm = a.getWindowManager();
             WindowManager.LayoutParams l = r.window.getAttributes();
             a.mDecor = decor;
             l.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION;
             l.softInputMode |= forwardBit;

             if (a.mVisibleFromClient) {
                 if (!a.mWindowAdded) {
                     a.mWindowAdded = true;
                     //最终调用WindowManagerImpl的addView方法
                     wm.addView(decor, l);
                 } 
             }
         } 

 }
 
 //WindowManagerImpl#addView
  public void addView(@NonNull View view, @NonNull ViewGroup.LayoutParams params) {
     //桥接模式 - WindowManagerGlobal执行具体的addView方法
     mGlobal.addView(view, params, mContext.getDisplay(), mParentWindow);
 }
 
 //WindowManagerGlobal#addView
 public void addView(View view, ViewGroup.LayoutParams params,Display display, Window parentWindow) {
            
      ViewRootImpl root;
      
      synchronized(mLock){
          //每次新建一个Activity的时候都会新建一个ViewRootImpl对象
         root = new ViewRootImpl(view.getContext(), display);
         view.setLayoutParams(wparams);
         mViews.add(view);
         mRoots.add(root);
         mParams.add(wparams);
         
         try{
             root.setView(view, wparams, panelParentView)
         }catch(RuntimeException e){
            throw e;
         }
      }
 }
```
在 handleResumeActivity 方法中执行具体的绘制流程，首先是将View添加到 WindowManagerImpl 中，这中间利用到了桥接模式也就是具体的 addView 的操作是 WindowManagerGlobal 来执行的，WindowManagerGlobal 是一个单例类在它内部维护了三个集合 mViews、mRoots、mParams、分别用来盛放View对象、ViewRootImpl以及一些参数，这里其实没什么重点只是用来保存一下，重点还是在 ViewRootImpl 的 setView 方法，将 DecorView 与 ViewRootImpl 绑定在一起，在setView方法里执行测量、布局、绘制的流程。

**小结**

- setContentView中只是执行了解析布局文件以及处理DecorView层次结构

- 在 handleResumeActivity 中才会去执行View的绘制流程，可以这么理解在 onResume 之后View的绘制流程才会走完

- 通过 WindowManagerImpl 的 addView 方法最终是托管给 WindowManagerGlobal 来处理

- ViewRootImpl是连接WindowManager和DecorView的纽带，View的三大流程均是通过ViewRoot来完成的。在ActivityThread中，当Activity对象被创建完毕后，会将DecorView添加到Window中并将ViewRootImpl对象和DecorView建立关联 root.setView(view, wparams, panelParentView)


## View的绘制顺序
![ViewRootImpl的setView方法流程.png](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/fee066e575854cac8e3656dcbda624c6~tplv-k3u1fbpfcp-watermark.image)
首先是 ViewRootImpl 的 setView 函数，这对我们而言是及其重要重要的方法，View 的具体的绘制流程就在这里，在 setView 方法中会先去调用 requestLayout 方法，在 requestLayout 中首先会去执行 checkThread 方法检查当前线程是不是主线程，所以当你看到在 onCreate 中也能在子线程中更新UI不要大惊小怪，因为只有在 onResume 方法执行的时候才会去检查线程，紧接着就是执行 scheduleTraversals ，下面是核心代码：

```java
void scheduleTraversals() {
    if (!mTraversalScheduled) {
        mTraversalScheduled = true;
        //代码 1 开启同步屏障
        mTraversalBarrier = mHandler.getLooper().getQueue().postSyncBarrier();
        //发送异步消息
        mChoreographer.postCallback(
                Choreographer.CALLBACK_TRAVERSAL, mTraversalRunnable, null);
    }
}
```
首先在`代码1`处开启了同步屏障，紧接着发送一个异步消息 TraversalRunnable ，熟悉Handler
的朋友肯定知道，同步屏障的目的是为了提高优先级，而UI界面作为用户接触到的第一个“事务”自然优先级必须提前，不能被别的任务阻塞，在 TraversalRunnable 的 run 方法中执行 doTraversal ：

```java
void doTraversal() {
    if (mTraversalScheduled) {
        mTraversalScheduled = false;
        //移除同步屏障
        mHandler.getLooper().getQueue().removeSyncBarrier(mTraversalBarrier);
        //执行具体的绘制流程
        performTraversals();
       
    }
}
```
doTraversal 中首先移除同步屏障，这点是必然的，不然别的普通消息也不会被执行，有添加屏障的地方就必须在合理的时机移除屏障，performTraversals 方法中执行具体的绘制流程也就是 performMeasure、performLayout、performDraw三件套，分别执行View的测量、布局以及绘制三步骤，大致流程可以用下图表示。



![performTraversals大致工作流程.png](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/76e7579f50954b91a053226c05e65e23~tplv-k3u1fbpfcp-watermark.image)

在 performTraversals 方法中会依次调用 performMeasure、performLayout、performDraw这三个过程来完成顶级View的 measure、layout、draw 的过程，以measure举例子在measure过程中会调用 onMeasure 方法，在 onMeasure 中将测量的流程传递给子View，依次递归完成 View 的 measure 过程，layout 和 draw 的过程也大同小异，measure过程决定了View的宽/高，Measure完成以后，可以通过getMeasuredWidth和getMeasuredHeight方法来获取到View测量后的宽/高，在几乎所有的情况下它都等同于View最终的宽/高，Layout过程决定了View的四个顶点的坐标和实际的View的宽/高，完成以后，可以通过getTop、getBottom、getLeft和getRight来拿到View的四个顶点的位置，并可以通过getWidth和getHeight方法来拿到View的最终宽/高。Draw过程则决定了View的显示，只有draw方法完成以后View的内容才能呈现在屏幕上。


 
# View.post() 实现原理
在 onCreate 函数中直接获取 View 的宽高你是获取不到的，理由在上面解释过，但是你可以通过 View.post() 的方式来获取View的宽高，下面跟着流程一起看下具体的实现原理。

```java
//View.java
public boolean post(Runnable action) {
    //mAttachInfo在View的dispatchAttachedToWindow方法中被赋值
    final AttachInfo attachInfo = mAttachInfo;
    
    if (attachInfo != null) {
        return attachInfo.mHandler.post(action);
    }
    //HandlerActionQueue的post函数
    getRunQueue().post(action);
    return true;
}

//View.java
void dispatchAttachedToWindow(AttachInfo info, int visibility) {
    //在 dispatchAttachedToWindow 中给 mAttachInfo 赋值
    mAttachInfo = info;
    if (mRunQueue != null) {
        //执行 HandlerActionQueue#executeActions
        mRunQueue.executeActions(info.mHandler);
        mRunQueue = null;
    }
}
```
在 post 方法中会给 attachInfo 赋值为 mAttachInfo，而 mAttachInfo 在View 的 dispatchAttachedToWindow 方法中被赋值，同时执行 HandlerActionQueue 的 executeActions 方法。

```java
public class HandlerActionQueue {
    private HandlerAction[] mActions;
    //代码1
    public void post(Runnable action) {
        postDelayed(action, 0);
    }
    
    public void postDelayed(Runnable action, long delayMillis) {
        final HandlerAction handlerAction = new HandlerAction(action, delayMillis);
        synchronized (this) {
        if (mActions == null) {
            mActions = new HandlerAction[4];
        }
        mActions = GrowingArrayUtils.append(mActions, mCount, handlerAction);
        mCount++;
    }
}
    
    //代码2
    public void executeActions(Handler handler) {
        synchronized (this) {
            final HandlerAction[] actions = mActions;
            for (int i = 0, count = mCount; i < count; i++) {
                final HandlerAction handlerAction = actions[i];
                handler.postDelayed(handlerAction.action, handlerAction.delay);
            }
            mActions = null;
            mCount = 0;
        }
    }
  
    private static class HandlerAction {
        final Runnable action;
        final long delay;
        public HandlerAction(Runnable action, long delay) {
            this.action = action;
            this.delay = delay;
        }
    }
    
}
```
在 View 的 post 方法中如果你的 attachInfo == null 那么会接着执行 HandlerActionQueue  的 post 方法并最终执行 postDelayed 方法，在 postDelayed 中将你的任务也就是 Runnable 对象和 delayMillis延时时间封装到 HandlerAction 中，最终添加保存到 mActions 数组中，这里有个类 GrowingArrayUtils 感兴趣的同学可以自己去看下具体的实现，主要是用来实现数组的动态扩容的，总结下，在 onCreate 中通过 View.post() 来获取 View 的宽高执行post方法时只是先将 runnable 任务添加到一个 mActions 数组中保存起来，mActions 支持动态扩容，截止到目前为止已经知道了在onCreate函数中 attachInfo 为null，同时 attachInfo 是在 View 的 dispatchAttachedToWindow 中被赋值的，那么 View#dispatchAttachedToWindow 被调用的地方在哪里呢？ 在 ViewRootImpl#performTraversals 中 会执行 `host.dispatchAttachedToWindow(mAttachInfo, 0);` 这个host其实就是 DecorView，也就是说在View的绘制流程中会执行 DecorView 的 dispatchAttachedToWindow，但 DecorView 本身是没有这个方法的而是调用它的父类 ViewGroup 的  dispatchAttachedToWindow，下面是具体的代码：

```java
void dispatchAttachedToWindow(AttachInfo info, int visibility) {
    //代码1
    super.dispatchAttachedToWindow(info, visibility);
   
    final int count = mChildrenCount;
    final View[] children = mChildren;
    //代码2
    for (int i = 0; i < count; i++) {
        final View child = children[i];
        child.dispatchAttachedToWindow(info,
                combineVisibility(visibility, child.getVisibility()));
    }

}
```
代码1处首先会调用自身的 dispatchAttachedToWindow 也就是给当前 View 中的 mAttachInfo 赋值，代码2处遍历所有的子View执行它们的 dispatchAttachedToWindow ，而我们View体系中用到的 AttachInfo 初始都是在 ViewRootImpl 的构造函数中初始化的 

```java
mAttachInfo = new View.AttachInfo(mWindowSession, mWindow, display, this, mHandler, this,
        context);
```
在 dispatchAttachedToWindow 的遍历过程中将它的引用赋值给每一个子控件，总结下，在View的绘制流程走到 performTraversals 方法中的时候会执行顶级View ~ DecorView的 dispatchAttachedToWindow 方法，AttachInfo 在 ViewRootImpl 的构造函数中被初始化 ，而在 dispatchAttachedToWindow 中会将这个方法的引用分发给每一个每一个子View。

那么上面解释了那么多其实还没讲到为啥通过View.post可以获取到View的宽高，我们来看下对于View而言它的 dispatchAttachedToWindow 做了什么，代码已经贴在上面了，其实也就是执行 HandlerActionQueue 的 executeActions 将任务通过handler发送到消息队列中等待执行，但是这里发送的是普通消息，在前文中已经讲了在 scheduleTraversals 中已经开启了同步屏障，而绘制流程的 TraversalRunnable 又是一个异步消息，所以只有在绘制流程执行完毕移除同步屏障后才会执行 executeActions 中发送的消息，而这个也是为什么通过View的post方法可以获取View的宽高的原因，因为在post的任务的执行时机就是在绘制流程之后。

通过dispatchAttachedToWindow给每一个View的 AttachInfo 赋值后，等到在执行 post 函数就会直接执行  `attachInfo.mHandler.post(action);` 通过handler将任务发送到主线程的MessageQueue中等待执行，当然到了这一步View的绘制流程肯定已经走完了。
 
# 总结
第一步：Activity的启动流程执行到onCreate方法调用setContentView确定布局结构以及解析XML文件，但是没有执行任何绘制的流程
![image.png](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/0de3d8afad434ee488560309239fe7e9~tplv-k3u1fbpfcp-watermark.image)
第二步：

![1621566696.jpg](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/876db94f49ea4e69ab82f582a6c92558~tplv-k3u1fbpfcp-watermark.image)
# 参考资料
[1、Android开发艺术探索]()

[2、【Android源码解析】View.post()到底干了啥](https://www.cnblogs.com/dasusu/p/8047172.html)

[3、一篇文章看明白 Activity 与 Window 与 View 之间的关系](https://blog.csdn.net/freekiteyu/article/details/79408969)



