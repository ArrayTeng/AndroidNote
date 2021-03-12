package com.example.rxjava;

/**
 * 具体的观察者对象
 */
public class ObservableCreate<T> extends Observable<T>{

    private ObservableOnSubscribe<T> source;

    public ObservableCreate(ObservableOnSubscribe<T> source) {
        this.source = source;
    }

    //在具体的观察者对象里重写 subscribeActual 函数
    @Override
    public void subscribeActual(Observer<? super T> observer) {
        //创建发送器
        CreateEmitter<T> emitter = new CreateEmitter<T>(observer);
        //执行观察者对象的 onSubscribe 方法
        observer.onSubscribe();

        /*
          这部分的代码对应
                 Observable.create(new ObservableOnSubscribe<String>() {
                     public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                           //here ObservableCreate 中的 ObservableOnSubscribe 被创建出来具体的执行在 subscribe 的时候
                     }
                 })
         */
        source.subscribe(emitter);
    }


    //创建具体的发送器对象
    static final class CreateEmitter<T> implements ObservableEmitter<T>{

        //发射器中持有了观察者对象，发射器执行的操作都会同步调用到观察者对应的函数方法
        private final  Observer<? super T> observer;

        CreateEmitter(Observer<? super T> observer) {
            this.observer = observer;
        }

        @Override
        public void onNext(T value) {
            observer.onNext(value);
        }

        @Override
        public void onError(Throwable error) {
            observer.onError(error);
        }

        @Override
        public void onComplete() {
            observer.onComplete();
        }
    }
}
