package com.amazing.tengfei.androidplugin.hook;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.amazing.tengfei.androidplugin.utils.RefInvoke;

import java.lang.reflect.Proxy;

/**
 * @author 滕飞
 * date 2020/6/21 11:02 AM
 * email arrayadapter.cn@outlook.com
 * description
 */
public class HookHelper {

    private static final String TAG = "HookHelper_TAG";

    public static void hookActivity() {
        try {
            Object singletonField = RefInvoke.getStaticFieldObject("android.app.ActivityTaskManager",
                    "IActivityTaskManagerSingleton");


            Object mInstanceField = RefInvoke.getFieldObject("android.util.Singleton",
                    singletonField, "mInstance");

            Class<?> iActivityTaskManager = Class.forName("android.app.IActivityTaskManager");

            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class<?>[]{iActivityTaskManager},
                    new HookHandler(mInstanceField));

            RefInvoke.setFieldObject("android.util.Singleton", singletonField, "mInstance", proxy);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


    public static void hookPackageManager(Context mContext) {
        try {
            Object mCurrentActivityThread = RefInvoke.getStaticFieldObject("android.app.ActivityThread",
                    "sCurrentActivityThread");

            Object rawSPackageManager = RefInvoke.getFieldObject("android.app.ActivityThread",
                    mCurrentActivityThread, "sPackageManager");

            Class<?> iPackageManager = Class.forName("android.content.pm.IPackageManager");
            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class<?>[]{iPackageManager}, new HookHandler(rawSPackageManager));

            RefInvoke.setFieldObject(rawSPackageManager, "sPackageManager", proxy);

            PackageManager packageManager = mContext.getPackageManager();
            RefInvoke.setFieldObject(packageManager, "mPM", proxy);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "hookPackageManager: "+e.getMessage());
        }


    }

}
