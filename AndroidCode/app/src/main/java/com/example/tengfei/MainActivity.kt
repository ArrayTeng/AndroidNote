package com.example.tengfei

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.tengfei.rxjava.RxJavaTest

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);



    }

    fun rxJavaTest(view: View) {
        val rxJavaTest = RxJavaTest()
        rxJavaTest.rxJavaTest()
    }
}