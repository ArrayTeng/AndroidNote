package com.amazing.tengfei.androidplugin.hook;

import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author 滕飞
 * date 2020/6/21 12:36 PM
 * email arrayadapter.cn@outlook.com
 * description
 */
public class HookHandler implements InvocationHandler {

    private static final String TAG = "HookHandler_TAG";

    private Object mBase;

    public HookHandler(Object mBase) {
        this.mBase = mBase;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Log.e(TAG, "invoke: "+"HookHandler_TAG");
        return method.invoke(mBase, args);
    }
}
