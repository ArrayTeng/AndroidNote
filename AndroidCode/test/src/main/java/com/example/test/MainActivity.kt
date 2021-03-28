package com.example.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindService()//

      Observable.create(object :ObservableOnSubscribe<String>{
          override fun subscribe(e: ObservableEmitter<String>) {
              TODO("Not yet implemented")
          }

      }).subscribe(object:Observer<String>{
          override fun onComplete() {
              TODO("Not yet implemented")
          }

          override fun onSubscribe(d: Disposable) {
              TODO("Not yet implemented")
          }

          override fun onNext(t: String) {
              TODO("Not yet implemented")
          }

          override fun onError(e: Throwable) {
              TODO("Not yet implemented")
          }
      })


    }
}



