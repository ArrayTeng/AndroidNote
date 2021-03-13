package com.example.test;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class Test {











    void  test(){
       Observable.create(new ObservableOnSubscribe<String>() {
           public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {

           }
       }).subscribe(new Observer<String>() {
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
