package com.example.rxjava;

public abstract class BasicFuseableObserver<T, R> implements Observer<T> {

    protected final Observer<? super R> actual;


    protected BasicFuseableObserver(Observer<? super R> actual) {
        this.actual = actual;
    }

    @Override
    public void onSubscribe(Disposable d) {
        actual.onSubscribe(d);
    }

    @Override
    public void onError(Throwable e) {
        actual.onError(e);
    }

    @Override
    public void onComplete() {
        actual.onComplete();
    }
}
