

## ViewGroup 中事件是如何被拦截的

```java
    //ViewGroup#dispatchTouchEvent
    public boolean dispatchTouchEvent(MotionEvent ev){
            //代码 4 
            if (actionMasked == MotionEvent.ACTION_DOWN) {
                cancelAndClearTouchTargets(ev);
                resetTouchState();
            }
            
            //代码 1 判断事件是否有被ViewGroup拦截
            final boolean intercepted;
            if (actionMasked == MotionEvent.ACTION_DOWN || mFirstTouchTarget != null) {
                final boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;
                if (!disallowIntercept) {
                    intercepted = onInterceptTouchEvent(ev);
                    ev.setAction(action); // restore action in case it was changed
                } else {
                    intercepted = false;
                }
            } else {
                intercepted = true;
            }
            //代码 2 如果没有被拦截又该处理什么
            final boolean canceled = resetCancelNextUpFlag(this)
                    || actionMasked == MotionEvent.ACTION_CANCEL;  
                    
            if (!canceled && !intercepted) {
            
            }    
            
            //代码 3 如果ViewGroup拦截了事件后又该如何处理，mFirstTouchTarget是什么？
            if (mFirstTouchTarget == null) {
                handled = dispatchTransformedTouchEvent(ev, canceled, null,
                        TouchTarget.ALL_POINTER_IDS);
            }   
            
   }
```

在 `ViewGroup` 的 `dispatchTouchEvent` 函数中首先会定义一个 `intercepted` 变量用来标记当前事件是否有被 `ViewGroup` 拦截，只有在监听到一个 `ACTION_DOWN` 事件或者 `mFirstTouchTarget != null` 的情况下才会去调用 `onInterceptTouchEvent` 函数来判断是否需要拦截事件，这里发现来两个比较陌生的东西，`mFirstTouchTarget` 和 `disallowIntercept` ，`mFirstTouchTarget` 是一个链表结构，表示是否有子元素消费了事件，当 `ViewGroup` 的子元素成功处理了事件时，`mFirstTouchTarget` 会被赋值并指向子元素，同理如果 `ViewGroup` 决定拦截事件那么 `mFirstTouchTarget == null` 就成立，同时 `ACTION_MOVE` 和 `ACTION_UP` 事件到来的时候由于 `mFirstTouchTarget != null` 的条件不成立接下来的代码块自然执行不了，这也是为什么在 `ViewGroup` 决定拦截后接下来的事件序列中 `onInterceptTouchEvent` 函数不会被继续调用的原因，`disallowIntercept` 是通过标记位 `FLAG_DISALLOW_INTERCEPT` 赋值的，而 `FLAG_DISALLOW_INTERCEPT` 又是通过 `requestDisallowInterceptTouchEvent` 函数方法设置的，一般这个函数用于子 View 中，用来影响父容器的拦截，一旦设置这个标记位 `ViewGroup` 将无法拦截除了 `ACTION_DOWN` 之外的所有事件，之所以除了 `ACTION_DOWN` 之外是因为在按下事件中会重置 
`FLAG_DISALLOW_INTERCEPT` 这个标记，原因在**代码4**中可以看到。


如果ViewGroup决定拦截事件那么 mFirstTouchTarget  必然为null，在**代码3**中可以看到如果决定拦截那么执行 dispatchTransformedTouchEvent 函数，注意它的入参 child 的值为 null。

```java
private boolean dispatchTransformedTouchEvent(MotionEvent event, boolean cancel,
            View child, int desiredPointerIdBits){
        if (child == null) {
            //1
            handled = super.dispatchTouchEvent(transformedEvent);
        } else {
            //......
            //2
            handled = child.dispatchTouchEvent(transformedEvent);
        }
            
}
```
如上代码的 1 处，如果child的入参为 null ，那么执行 `super.dispatchTouchEvent(transformedEvent)` 即View 的 `dispatchTouchEvent`  


```java
//View#dispatchTouchEvent
public boolean dispatchTouchEvent(MotionEvent event) {
   // 1
    boolean result = false;
    if (mInputEventConsistencyVerifier != null) {
        mInputEventConsistencyVerifier.onTouchEvent(event, 0);
    }
    final int actionMasked = event.getActionMasked();
    
    if (onFilterTouchEventForSecurity(event)) {
        //2
        ListenerInfo li = mListenerInfo;
        if (li != null && li.mOnTouchListener != null 
                && (mViewFlags & ENABLED_MASK) == ENABLED
                && li.mOnTouchListener.onTouch(this, event)) {
            result = true;
        }
        //3
        if (!result && onTouchEvent(event)) {
            result = true;
        }
    }
   
    return result;
}
```
如果 ViewGroup 决定拦截事件，那么事件是不是应该交给 ViewGroup 来消费，这个是我们之前经常背的八股文，具体怎么做的呢，前面我们已经分析到如果 ViewGroup 决定拦截事件那么事件最终会交给 View 的 `dispatchTouchEvent` 函数处理，在代码1处定义了一个变量 result 用于标记这个事件是否已经被消费处理，代码2处判断了是否定义了 `mOnTouchListener` 以及 `onTouch` 函数是否返回true ， 如果这些条件满足 result = true 并且 `onTouchEvent` 不会被执行，否则执行 onTouchEvent ，可以看到如果消费了事件 result = true 否则就是默认 false,可以看到 `dispatchTouchEvent` 的返回值是由 `onTouchEvent` 和 `onInterceptTouchEvent` 综合决定的。



## ViewGroup不拦截事件又是如何将事件分发给子View

```java
if(!canceled && !intercepted){
    if (actionMasked == MotionEvent.ACTION_DOWN
        || (split && actionMasked == MotionEvent.ACTION_POINTER_DOWN)
        || actionMasked == MotionEvent.ACTION_HOVER_MOVE) {
        
        if (newTouchTarget == null && childrenCount != 0){
            //排序所有的子控件
            final ArrayList<View> preorderedList = buildTouchDispatchChildList();
            
            for (int i = childrenCount - 1; i >= 0; i--) {
                    //获取子控件的index下标
                   final int childIndex = getAndVerifyPreorderedIndex(
                                    childrenCount, i, customOrder);
                   //获取子控件对象
                   final View child = getAndVerifyPreorderedView(
                                    preorderedList, children, childIndex);
                                    
                   //在dispatchTransformedTouchEvent中执行子控件的dispatchTouchEvent方法
                   if (dispatchTransformedTouchEvent(ev, false, child, idBitsToAssign)){
                   //创建一个 TouchTarget 节点
                     newTouchTarget = addTouchTarget(child, idBitsToAssign);
                     alreadyDispatchedToNewTouchTarget = true;
                     break;
                   }                                
                }   
            }
        }
    }
```
当你手指触摸到屏幕这时候ViewGroup首先接收到的是一个down事件，如果不拦截会执行到上面的代码块中，这里你会发现首先会遍历循环所有的子控件调用 `dispatchTransformedTouchEvent` 函数，这里的 child 入参不在是 null 了所以会执行 `child.dispatchTouchEvent` ，也就是子控件的 `dispatchTouchEvent` 方法，由子控件继续执行事件的分发，这时候如果 child 消费了事件 `dispatchTouchEvent` 会返回true，接着会执行 `addTouchTarget` 函数和将 `alreadyDispatchedToNewTouchTarget` 标记设置为 true，`alreadyDispatchedToNewTouchTarget` 标记表示是否有子控件消费了这个事件，`newTouchTarget` 默认为 null 也就是在有 child 消费事件后才会创建一个节点。

```java
private TouchTarget addTouchTarget(@NonNull View child, int pointerIdBits) {
    final TouchTarget target = TouchTarget.obtain(child, pointerIdBits);
    target.next = mFirstTouchTarget;
    mFirstTouchTarget = target;
    return target;
}
```
在 `addTouchTarget` 函数中首先将 child 封装成一个 target 对象，TouchTarget 是一个单链表的数据结构在这里 ViewGroup 中的 `mFirstTouchTarget` 指向“封装child的target”，从这里可以看出如果有子控件消费了事件那么 mFirstTouchTarget 必然不为 null，同时如果 `mFirstTouchTarget == null` 那么说明没有子控件消费事件

## TouchTarget 
ViewGroup 里 `TouchTarget` 对象可以看做在事件分发序列中第一个消费了事件的控件对象的封装，除了记录消费事件的 View 对象还具备在 move 事件到来时快速定位具体的 子View 来处理事件，当一个子View消费了事件时 `dispatchTransformedTouchEvent` 返回 true ，接着调用 `addTouchTarget` 函数新建一个 `TouchTarget` 节点，

```java
 private TouchTarget addTouchTarget(@NonNull View child, int pointerIdBits) {
     //新建 TouchTarget 节点
     final TouchTarget target = TouchTarget.obtain(child, pointerIdBits);
     //mFirstTouchTarget初始为null，target.next = null
     target.next = mFirstTouchTarget;
     //mFirstTouchTarget被赋值为了一个包装了ViewGroup的子View的（也就是当前点击事件下View层次结构下
     //ViewGroup的child view）TouchTarget.
     mFirstTouchTarget = target;
     return target;
 }
```
通过 `TouchTarget` 可以快速定位到事件序列上直至消费事件的那个View的一条链上的所有View对象，`TouchTarget` 可以看做是一个“伪单链表”，为啥这么说呢，因为 `TouchTarget` 实际上并没有连接在一起，当一个View消费了事件时这个 View 的 `dispatchTouchEvent` 函数返回true，所以它的父容器的 `dispatchTransformedTouchEvent` 的返回值也是true，也就意味这个父容器 会执行 `addTouchTarget` 给 `mFirstTouchTarget` 赋值并且 child 指向这个View，依次递归向上，所以通过 `ViewGroup` 的 `mFirstTouchTarget` 可以形成一条指向最终消费事件的“链表”，通过它的 child 字段找到下一层的 View 执行分发操作依次递归执行上面的步骤直到最终处理事件的 View。

**小结：**

- 在 ViewGroup 中 mFirstTouchTarget 为 null 说明没有 子View 处理事件，事件最终会交给自身处理
- 通过 mFirstTouchTarget 可以快速定位到最终消费事件的 View 对象（如果有的话）
- 如果有 子View 消费了事件那么会执行 addTouchTarget 函数将下一层的View对象包装到 TouchTarget 节点中





## shouldDelayChildPressedState

```java
//ViewGroup#shouldDelayChildPressedState
public boolean shouldDelayChildPressedState() {
    return true;
}

//View#isInScrollingContainer
public boolean isInScrollingContainer() {
    ViewParent p = getParent();
    //遍历所有的父容器只要有一个父容器的 shouldDelayChildPressedState 返回true就判定子View
    //在一个滑动容器里
    while (p != null && p instanceof ViewGroup) {
        if (((ViewGroup) p).shouldDelayChildPressedState()) {
            return true;
        }
        p = p.getParent();
    }
    return false;
}
//View#CheckForTap
private final class CheckForTap implements Runnable {
    public float x;
    public float y;
    @Override
    public void run() {
        mPrivateFlags &= ~PFLAG_PREPRESSED;
        setPressed(true, x, y);
        checkForLongClick(ViewConfiguration.getTapTimeout(), x, y);
    }
}

//View#onTouchEvent#MotionEvent.ACTION_DOWN
public boolean onTouchEvent(MotionEvent event) {
    
    case MotionEvent.ACTION_DOWN:
        //1 检查是否在一个滑动控件里
        boolean isInScrollingContainer = isInScrollingContainer();
        if (isInScrollingContainer) {
            //2 将状态设置为预点击
            mPrivateFlags |= PFLAG_PREPRESSED;
            if (mPendingCheckForTap == null) {
                 mPendingCheckForTap = new CheckForTap();
            }
            //3 延时100ms发送一个消息
            postDelayed(mPendingCheckForTap, ViewConfiguration.getTapTimeout());
        }else{
            //将状态设置为按下状态
            setPressed(true, x, y);
            //检查是否长按
            checkForLongClick(0, x, y);
        }
    break;
    
}
```
之所以将这个函数单独领出来主要是我发现很多人不知道这个函数的用法，但实际上利用好这个函数可以在自定义容器的时候带来100ms的优化，那具体怎么操作呢？

`shouldDelayChildPressedState` 是ViewGroup里的一个函数，你在自定义ViewGroup的时候可以重写这个函数来告诉子View这个父容器是否是一个滑动控件，默认情况下是true，也就是说在默认情况下我们的子View都是定义在一个滑动控件里的（代码意义上的），假设这么一种场景在滑动列表控件里定义一个item，但是Android并不知道你点击的是这个item还是列表本身也就是它不知道要处理哪一个，所以在item接收到down事件的时候会将当前的状态设置为预点击，也就是在代码2处并且创建一个 `CheckForTap` 的任务对象,调用 postDelayed 函数在100ms后执行 `CheckForTap` 的run函数。

`CheckForTap` 在它的 run 函数里首先会将状态设置为点击状态然后检查是否长按，也就是说到这一步流程和普通的down流程一样的，但是这中间经历了100ms的延时，就是说如果你自定义了一个ViewGroup没有重写   `shouldDelayChildPressedState` 返回false的话都要经过100ms才能响应你的down事件，所以这里建议大家如果自定义ViewGroup的时候如果你自定义的不是一个滑动容器都要重写 `shouldDelayChildPressedState` 返回 false。

## down之后的事件如何处理
写到这里的时候我DIY了一下，我在想如果在一个事件序列从down -> move -> up,如果我的 View 的
onTouchEvent 的 down 返回了true，这种情况下事件是怎么分发的呢，艺术探索上的解释是“如果View不消耗除 `ACTION_DOWN` 以外的其他事件，那么这个点击事件会消失，此时父元素的 `onTouchEvent` 并不会被调用，并且当前View可以持续收到后续的事件，最终这些消失的点击事件会传递给Activity处理”，接下来我来一步步的验证这个结论。

首先从 down 开始，在目前的场景下父容器是没有拦截 down 事件的，事件正常分发执行到 `if (!canceled && !intercepted)` 代码块中，上面的代码都有所以我就不贴代码了，既然是正常分发事件那么理所当然的会执行到 `dispatchTransformedTouchEvent` 函数中将事件分发给 子View 处理，由于当前在child View 的 onTouchEvent 的 down 中返回true，down事件被 child 消费了 m`FirstTouchTarget != null`,`alreadyDispatchedToNewTouchTarget = true`(代表这个事件已经被子View消费了) ， 好，现在继续跟流程，我们来看代码

```java
// mFirstTouchTarget == null 表示肯定没有子View消费事件，事件交给自己处理
if(mFirstTouchTarget == null){
    //代码 1
    handled = dispatchTransformedTouchEvent(ev, canceled, null,TouchTarget.ALL_POINTER_IDS);
}else{
    //代码 2
    TouchTarget target = mFirstTouchTarget;
    while (target != null) {
        final TouchTarget next = target.next;
        //判断这个是否已经被消费
        if (alreadyDispatchedToNewTouchTarget && target == newTouchTarget) {
            handled = true;
        } else {
            //代码 3
            if (dispatchTransformedTouchEvent(ev, cancelChild,
                    target.child, target.pointerIdBits)) {
                handled = true;
            }
        }
    }
}
```
既然down事件已经被消费了代码1处的判断肯定是不合法的所以继续执行到代码2处，这里有个 `TouchTarget`  ，看完了上面的内容我想你肯定明白了此时 TouchTarget 这个节点封装了子View的child对象，由于是 move 事件所以 `alreadyDispatchedToNewTouchTarget` 在 `dispatchTouchEvent` 函数里会被重置为 false ，这时候执行到代码3处的 `dispatchTransformedTouchEvent` ， 看到没有，还是熟悉的配方，在 `dispatchTransformedTouchEvent` 内部将事件分发给子 View，如果这个事件被消费了那么返回 true，handled 会被赋值为 true，否则就是 false，但是这里已经不会在执行当前ViewGroup的onTouchEvent函数了，事件会继续向上委托处理直至 Activity，同理，如果子View没有消费down事件那么同一事件序列中的其他事件都不会再交给它来处理，并且事件将重新交由它的父元素去处理，即父元素的onTouchEvent 会被调用，这点在代码中同样可以看出来，你不消费down事件那么在其他事件过来的时候 `mFirstTouchTarget == null` 最终还是执行的代码1处 ViewGroup 的 onTouchEvent。

## ACTION_CANCEL 事件在什么情况下触发
如果上层 View 是一个 `RecyclerView`，它收到了一个 `ACTION_DOWN` 事件，由于这个可能是点击事件，所以它先传递给对应 `ItemView`，询问 ItemView 是否需要这个事件，然而接下来又传递过来了一个 ACTION_MOVE 事件，且移动的方向和 `RecyclerView` 的可滑动方向一致，所以 `RecyclerView` 判断这个事件是滚动事件，于是要收回事件处理权，这时候对应的 ItemView 会收到一个 `ACTION_CANCEL` ，并且不会再收到后续事件，可以这么理解 ItemView 消费了 ACTION_DOWN 事件所以按照之前的理解是后续的事件都会交给这个 ItemView 处理，但这里少了个前提就是子View的父容器没有拦截后续事件（比如说move事件），这里我们先设置一个大前提就是子View消费了down事件并且在父容器中拦截了move事件,看看事件流程是这么走的:


```java
public boolean dispatchTouchEvent(MotionEvent ev) {

    if(actionMasked == MotionEvent.ACTION_DOWN || mFirstTouchTarget != null){
        //因为子View消费了down事件所以 mFirstTouchTarget != null 成立，当move事件来的时候
        // onInterceptTouchEvent 函数肯定会执行到并且返回true， intercepted = true；
        intercepted = onInterceptTouchEvent(ev);
    }
    
    
    TouchTarget predecessor = null;
    TouchTarget target = mFirstTouchTarget;
    while(target != null){
        final TouchTarget next = target.next;
        //在move事件到来的时候 alreadyDispatchedToNewTouchTarget 被重置为 false，if条件不满足
        if (alreadyDispatchedToNewTouchTarget && target == newTouchTarget) {
            handled = true;
        }else{
            //代码 1 ，intercepted 为 true 所以 cancelChild = true;
            final boolean cancelChild = resetCancelNextUpFlag(target.child) || intercepted;
            //在 dispatchTransformedTouchEvent 函数中
            if (dispatchTransformedTouchEvent(ev, cancelChild,
                target.child, target.pointerIdBits)) {
                     handled = true;
                }
           if(cancelChild){
               if (predecessor == null) {
                    mFirstTouchTarget = next;
               } else {
                   predecessor.next = next;
              }
              target.recycle();
              target = next;
              containue;
           }
        }
        predecessor = target;
        target = next;
    }
}
```


# 小结
1. 如果某一个`View`决定拦截这个事件，在down事件的时候 `onInterceptTouchEvent` 返回true，如果它能接收到事件那么在同一个事件序列中后续事件都会交给它处理。

2. 如果View一旦接收到事件，但是如果没有消耗 down 事件，那么后续的事件都不会交给它处理了，而是交给它的父容器处理 ，一旦交给一个View处理，那么它就必须消耗掉，否则同一事件序列中剩下的事件就不再交给它来处理了。

3. 如果View的 `onTouchEvent` 的 down 事件返回true即消费了down事件，但是后续的事件没有消费，那么后续的事件比如说move事件会消失或者交给 `Activity` 的 `onTouchEvent` 处理，并且这个 View 的父容器的 onTouchEvent 并不会被调用。

4. ViewGroup 的 `shouldDelayChildPressedState` 默认返回 true ，用来标记这个 `ViewGroup` 是一个滑动控件，如果View是在一个滑动控件里那么点击它的时候会延时100ms响应，通常的做法是如果不是滑动控件那么 重写 `shouldDelayChildPressedState` 返回false。

5. View 没有拦截事件的函数 `onInterceptTouchEvent` ，`ViewGroup` 的 `onInterceptTouchEvent` 默认返回 false ， View 的 `onTouchEvent` 默认返回 true，即默认消费事件。



# 参考资料
[1、拇指记者深入Android公司，打探事件分发机制背后的秘密](https://juejin.cn/post/6950089742943780894#heading-8)

[2、Android开发艺术探索]()

[3、图解Android事件分发机制](https://www.jianshu.com/p/e99b5e8bd67b)

[4、安卓自定义View进阶-MotionEvent详解](https://www.gcssloop.com/customview/motionevent)

[5、ViewGroup拦截事件子View为何触发子View CANCEL事件以及后续事件如何分发到父容器](https://blog.csdn.net/MoLiao2046/article/details/103737626)
