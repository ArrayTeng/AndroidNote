package com.example.hook.hook;

import android.annotation.SuppressLint;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class HookActivityInvocation implements InvocationHandler {

    private final Object base;

    public HookActivityInvocation(Object base) {
        this.base = base;
    }


    @SuppressLint("LongLogTag")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if("startActivity".equals(method.getName())){
            Log.i("HookActivityInvocationTag","test");
            return method.invoke(base,args);
        }
        return method.invoke(base,args);
    }
}
