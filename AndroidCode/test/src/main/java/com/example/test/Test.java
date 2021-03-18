package com.example.test;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class Test {


    void test() {

        Observable.create(new ObservableOnSubscribe<String>() {
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {

            }
        }).map(new Function<String, String>() {
            @NonNull
            public String apply(@NonNull String s) throws Exception {
                return "s";
            }
        }).subscribeOn(Schedulers.io())//ObservableCreate执行subscribeOn，将source替换为了ObservableCreate 返回 ObservableSubscribeOn
                //ObservableSubscribeOn.observeOn，将source替换为了 ObservableSubscribeOn  返回 ObservableObserveOn
                .observeOn(AndroidSchedulers.mainThread())
                //ObservableObserveOn.subscribe
                .subscribe(new Observer<String>() {//最终这里执行的是 ObservableObserveOn 的subscribe函数也就是 ObservableObserveOn 的 subscribeActual 函数
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    public void onNext(@NonNull String s) {

                    }

                    public void onError(@NonNull Throwable e) {

                    }

                    public void onComplete() {

                    }
                });


    }
}
