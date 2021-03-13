package com.example.rxjava;

//观察者对象
public interface Observer<T> {

    void onSubscribe();

    void onNext(T value);

    void onError(Throwable e);

    void onComplete();
}
