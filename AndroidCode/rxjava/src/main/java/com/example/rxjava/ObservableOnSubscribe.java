package com.example.rxjava;


public interface ObservableOnSubscribe<T> {

    //维护了一个发射器对象
    void subscribe(ObservableEmitter<T> e);

}
