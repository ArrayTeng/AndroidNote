package com.example.tengfei

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexClassLoader
import dalvik.system.PathClassLoader

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        Log.i("tmd",classLoader.toString())

        Log.i("tmd",classLoader.parent.toString())


    }
}