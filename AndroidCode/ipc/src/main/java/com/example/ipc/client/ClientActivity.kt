package com.example.ipc.client

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.ipc.Book
import com.example.ipc.R
import com.example.ipc.server.BookManager
import com.example.ipc.server.RemoteService
import com.example.ipc.server.Stub

class ClientActivity : AppCompatActivity() {

    var isConnect: Boolean = false

    private lateinit var bookManager: BookManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            bookManager = Stub.asInterface(service)
            isConnect = true
            val books = bookManager.books
            Log.i("tmd", books.toString())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnect = false
        }
    }

    fun addBook(view: View) {
        if (!isConnect) {
            attemptToBindService()
            return
        }
        if (bookManager == null) {
            return
        }
        val book = Book()
        book.name = "feifei"
        book.price = 1
        bookManager.addBook(book);
    }

    fun getBook(view: View) {
        var books = bookManager.books
        for ( value in books){
            Log.i("tmd",value.name)
        }
    }


    private fun attemptToBindService() {
        val intent = Intent(this, RemoteService::class.java)
        intent.action = "com.example.ipc.server.RemoteService"
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStart() {
        super.onStart()
        if (!isConnect) {
            attemptToBindService()
        }
    }

    override fun onStop() {
        super.onStop()
        if (isConnect) {
            unbindService(serviceConnection)
        }
    }

}