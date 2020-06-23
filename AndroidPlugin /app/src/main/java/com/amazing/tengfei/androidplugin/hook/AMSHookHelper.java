package com.amazing.tengfei.androidplugin.hook;

import android.annotation.SuppressLint;

import com.amazing.tengfei.androidplugin.utils.RefInvoke;

import java.lang.reflect.Proxy;

/**
 * @author 滕飞
 * date 2020/6/22 10:43 PM
 * email arrayadapter.cn@outlook.com
 * description 本次操作仅仅在 API29 上
 */

public class AMSHookHelper {

    public static final String EXTRA_TARGET_INTENT = "Extra_target_intent";

    /**
     *  在启动一个Activity的时候把待启动的Activity替换为在清单文件中已经注册的"占坑"Activity
     */
    public static void hookAMN() throws ClassNotFoundException {

        Object iActivityTaskManagerSingleton = RefInvoke.getStaticFieldObject(
                "android.app.ActivityTaskManager",
                "IActivityTaskManagerSingleton");

        Object mInstance = RefInvoke.getFieldObject("android.util.Singleton",
                iActivityTaskManagerSingleton,
                "mInstance");

        @SuppressLint("PrivateApi")
        Class<?> iActivityTaskManagerClazz = Class.forName("android.app.IActivityTaskManager");

        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{iActivityTaskManagerClazz},new MockClass1(mInstance));

        RefInvoke.setFieldObject("android.util.Singleton",iActivityTaskManagerSingleton,
                "mInstance",proxy);


    }

}
