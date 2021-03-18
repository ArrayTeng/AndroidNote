package com.example.rxjava;

public class ObservableMap<T,U> extends AbstractObservableWithUpStream<T,U>{

    final Function<? super T,? extends U> function;

    public ObservableMap(ObservableSource<T> source,Function<? super T,? extends U> function) {
        super(source);
        this.function = function;
    }

    @Override
    public void subscribeActual(Observer<? super U> observer) {

    }
}
