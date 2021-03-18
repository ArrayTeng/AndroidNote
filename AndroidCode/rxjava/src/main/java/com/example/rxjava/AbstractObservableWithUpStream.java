package com.example.rxjava;

public abstract class AbstractObservableWithUpStream<T,U> extends Observable<U> {

    protected final ObservableSource<T> source;


    protected AbstractObservableWithUpStream(ObservableSource<T> source) {
        this.source = source;
    }
}
