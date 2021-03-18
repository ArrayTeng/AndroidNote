package com.example.tengfei.rxjava

import android.util.Log
import com.example.rxjava.*

class RxJavaTest {

    val tag: String = "RxJavaTest"

    fun rxJavaTest() {
        //创建被观察者对象
        val observable: Observable<String> =
            Observable.create { e ->
                e?.onNext("嗨")
                e?.onNext("Hello")
                e?.onNext("World")
                e?.onError(Throwable("自定义异常"))
            }


        //创建观察者对象
        val observer: Observer<String> = (object : Observer<String> {
            override fun onSubscribe(disposable: Disposable) {
                Log.i(tag,"onSubscribe   ")
            }

            override fun onNext(value: String?) {
                Log.i(tag,"onNext   " + value)

            }

            override fun onError(e: Throwable?) {
                Log.i(tag,"onError   " + e.toString())

            }

            override fun onComplete() {
                Log.i(tag,"onComplete   ")

            }
        })

        //观察者订阅被观察者
        observable.subscribe(observer)

    }
}