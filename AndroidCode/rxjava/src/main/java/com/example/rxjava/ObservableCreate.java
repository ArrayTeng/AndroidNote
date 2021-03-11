package com.example.rxjava;

/**
 * 具体的观察者对象
 */
public class ObservableCreate<T> extends Observable<T>{


    //在具体的观察者对象里重写 subscribeActual 函数
    @Override
    public void subscribeActual(Observable<? super T> observable) {



    }
}
