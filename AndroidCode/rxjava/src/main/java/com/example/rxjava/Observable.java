package com.example.rxjava;

/**
 * 抽象被观察者角色
 */
public abstract class Observable<T> implements  ObservableSource<T>{

//    public static <T> Observable<T> create(){
//        return
//    }


    @Override
    public void subscribe(Observable<? super T> observable) {

        subscribeActual(observable);

    }

    public abstract void subscribeActual(Observable<? super T> observable);
}
