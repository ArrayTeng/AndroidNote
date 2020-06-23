package com.amazing.tengfei.androidplugin.hook;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

/**
 * @author 滕飞
 * date 2020/6/23 9:45 PM
 * email arrayadapter.cn@outlook.com
 * description
 */
public class MockClass2 implements Handler.Callback {

    private Handler rawHandler;

    public MockClass2(Handler rawHandler) {
        this.rawHandler = rawHandler;
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {

        return true;
    }
}
