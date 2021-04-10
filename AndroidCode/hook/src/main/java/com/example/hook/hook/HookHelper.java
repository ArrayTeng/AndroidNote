package com.example.hook.hook;


import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;


public class HookHelper {


    public static void hookIActivityManager() {

        try {
            Class<?> activityManagerClass = Class.forName("android.app.ActivityManager");
            Field iActivityManagerSingletonField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
            iActivityManagerSingletonField.setAccessible(true);

            Object singleton = iActivityManagerSingletonField.get(null);

            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object iAMS = mInstanceField.get(singleton);
            Class<?> iActivityManagerClass = Class.forName("android.app.IActivityManager");

            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class<?>[]{iActivityManagerClass},new HookActivityInvocation(iAMS));
            mInstanceField.setAccessible(true);
            mInstanceField.set(singleton,proxy);
        }catch (Exception e){
            e.printStackTrace();
            Log.e("tmd",e.getMessage());
        }



    }


}
