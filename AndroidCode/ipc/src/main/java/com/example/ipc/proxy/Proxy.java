package com.example.ipc.proxy;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.example.ipc.Book;
import com.example.ipc.server.BookManager;
import com.example.ipc.server.Stub;

import java.util.List;

public class Proxy implements BookManager {

    private final IBinder remote;

    private static final String DESCRIPTOR = "com.example.ipc.server.BookManager";

    public Proxy(IBinder binder) {
        this.remote = binder;
    }

    public String getInterfaceDescriptor() {
        return DESCRIPTOR;
    }


    @Override
    public List<Book> getBooks() throws RemoteException {
        android.os.Parcel data = Parcel.obtain();//客户端发送的数据
        android.os.Parcel replay = Parcel.obtain();//服务端返回的数据
        List<Book> result;
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            remote.transact(Stub.TRANSACTION_getBooks, data, replay, 0);//会把客户端的线程挂起来
            replay.readException();
            result = replay.createTypedArrayList(Book.CREATOR);
        } finally {
            data.recycle();
            replay.recycle();
        }
        return result;
    }

    @Override
    public void addBook(Book book) throws RemoteException {
        android.os.Parcel data = Parcel.obtain();//客户端发送的数据
        android.os.Parcel replay = Parcel.obtain();//服务端返回的数据
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            if (book != null) {
                data.writeInt(1);
                book.writeToParcel(data, 0);
            } else {
                data.writeInt(0);
            }
            remote.transact(Stub.TRANSACTION_addBook, data, replay, 0);
            replay.readException();
        } finally {
            data.recycle();
            replay.recycle();
        }
    }

    @Override
    public IBinder asBinder() {
        return remote;
    }
}
