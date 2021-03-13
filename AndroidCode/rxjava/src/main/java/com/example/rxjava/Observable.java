package com.example.rxjava;

/**
 * 抽象被观察者角色
 */
public abstract class Observable<T> implements  ObservableSource<T>{

    //创建被观察者对象
    public static <T> Observable<T> create(ObservableOnSubscribe<T> source){
        return new ObservableCreate<T>(source);
    }


    @Override
    public void subscribe(Observer<? super T> observer) {
        //ObservableCreate 调用 subscribe 函数绑定观察者，
        //ObservableCreate 中执行了具体的 subscribeActual 函数
        subscribeActual(observer);

    }

    public abstract void subscribeActual(Observer<? super T> observer);
}
