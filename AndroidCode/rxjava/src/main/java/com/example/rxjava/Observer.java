package com.example.rxjava;

//观察者对象
public interface Observer<T> {

    //订阅成功的回调方法
    void onSubscribe(Disposable d);

    void onNext(T value);

    void onError(Throwable e);

    void onComplete();
}
