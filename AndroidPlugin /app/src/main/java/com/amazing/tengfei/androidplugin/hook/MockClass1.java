package com.amazing.tengfei.androidplugin.hook;

import android.content.ComponentName;
import android.content.Intent;

import com.amazing.tengfei.androidplugin.StubActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author 滕飞
 * date 2020/6/22 11:30 PM
 * email arrayadapter.cn@outlook.com
 * description
 */
class MockClass1 implements InvocationHandler {

    Object rawObject;

    public MockClass1(Object rawObject) {
        this.rawObject = rawObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if ("startActivity".equals(method.getName())) {
            Intent rawIntent;
            int index = 0;

            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent){
                    index = i;
                    break;
                }
            }

            //获取原始的intent对象
            rawIntent = (Intent) args[index];

            Intent newIntent = new Intent();

            //获取替身Activity的包名
            String stubPack = rawIntent.getComponent().getPackageName();
            ComponentName componentName = new ComponentName(stubPack, StubActivity.class.getName());
            newIntent.setComponent(componentName);

            newIntent.putExtra(AMSHookHelper.EXTRA_TARGET_INTENT,rawIntent);
            args[index] = newIntent;


            return method.invoke(rawObject, args);
        }


        return method.invoke(rawObject, args);
    }
}
