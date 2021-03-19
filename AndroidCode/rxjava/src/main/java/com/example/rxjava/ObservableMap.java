package com.example.rxjava;

public class ObservableMap<T,U> extends AbstractObservableWithUpStream<T,U>{

    final Function<? super T,? extends U> function;

    public ObservableMap(ObservableSource<T> source,Function<? super T,? extends U> function) {
        super(source);
        this.function = function;
    }

    @Override
    public void subscribeActual(Observer<? super U> observer) {

        source.subscribe(new MapObserver<T,U>(observer,function));
    }

    static  class MapObserver<T,U> extends BasicFuseableObserver<T,U>{

        final Function<? super T,? extends U> function;

        protected MapObserver(Observer<? super U> actual,Function<? super T,? extends U> function) {
            super(actual);
            this.function = function;
        }


        @Override
        public void onNext(T value) {
            U u = function.apply(value);
            actual.onNext(u);
        }


    }

}
