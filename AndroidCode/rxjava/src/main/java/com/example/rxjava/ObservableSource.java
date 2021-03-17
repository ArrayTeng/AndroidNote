package com.example.rxjava;

public interface ObservableSource<T> {

    //订阅方法
    void subscribe(Observer<? super T> observer);
}
