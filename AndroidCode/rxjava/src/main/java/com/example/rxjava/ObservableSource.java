package com.example.rxjava;

public interface ObservableSource<T> {

    void subscribe(Observable<? super T> observable);
}
