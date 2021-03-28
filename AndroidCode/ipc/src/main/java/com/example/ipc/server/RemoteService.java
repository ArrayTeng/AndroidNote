package com.example.ipc.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;
import com.example.ipc.Book;
import java.util.ArrayList;
import java.util.List;

public class RemoteService extends Service {

    private List<Book> books = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Book book = new Book();
        book.setName("滕飞");
        book.setPrice(0);
        books.add(book);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return bookManager;
    }


    private final Stub bookManager = new Stub() {
        @Override
        public List<Book> getBooks() throws RemoteException {

            return books;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            Log.i("tmd","addBook"+book.getName());
            books.add(book);
        }
    };
}
