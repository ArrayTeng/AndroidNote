package com.amazing.tengfei.androidplugin.hook;

import android.content.Context;

import com.amazing.tengfei.androidplugin.utils.RefInvoke;

import java.lang.reflect.Proxy;
import java.sql.Ref;

/**
 * @author 滕飞
 * date 2020/6/24 4:53 PM
 * email arrayadapter.cn@outlook.com
 * description Hook startActivity 只针对Android10
 */
class GlobalActivityHookHelper {

    public static void hook(Context context) {
        hookAMS(context);
    }

    /**
     * 将要启动的Activity替换为占坑的Activity
     */
    private static void hookAMS(Context context) {
        Object iActivityTaskManagerSingletonField = RefInvoke.getStaticFieldObject(
                "android.app.ActivityTaskManager",
                "IActivityTaskManagerSingleton");

        Object mInstanceField = RefInvoke.getFieldObject("android.util.Singleton", iActivityTaskManagerSingletonField,"mInstance");

        Class<?> iActivityTaskManagerClass = RefInvoke.createClass("android.app.ActivityTaskManager");

        Object amsProxyInvocationHandler = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{iActivityTaskManagerClass},
                new AMSProxyInvocationHandler());

        RefInvoke.setFieldObject();



    }
}
