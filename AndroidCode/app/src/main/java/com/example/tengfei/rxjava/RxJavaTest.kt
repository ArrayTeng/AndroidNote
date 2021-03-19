package com.example.tengfei.rxjava

import android.util.Log
import com.example.rxjava.*
import com.example.rxjava.Function

class RxJavaTest {

    val tag: String = "RxJavaTest"

    fun rxJavaTest() {
        //创建被观察者对象
        val observable: Observable<String> =
            Observable.create { e ->
                e?.onNext("嗨")
                e?.onNext("Hello")
                e?.onNext("World")

            }

        //观察者订阅被观察者
        observable
            .map(object :Function<String,Int>{
                override fun apply(t: String?): Int {
                    return 1024
                }
            })
            .subscribe(object:Observer<Int>{
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(value: Int?) {
                   Log.i(tag,value.toString())
                }

                override fun onError(e: Throwable?) {
                }

                override fun onComplete() {
                }
            })

    }
}