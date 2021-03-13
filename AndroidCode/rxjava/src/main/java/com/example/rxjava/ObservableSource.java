package com.example.rxjava;

public interface ObservableSource<T> {

    void subscribe(Observer<? super T> observer);
}
