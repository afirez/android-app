package com.afirez.binder.proxy;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.afirez.binder.api.Book;
import com.afirez.binder.api.IBookManager;
import com.afirez.binder.server.IBookManagerStub;

import java.util.List;

/**
 * Created by afirez on 18-4-11.
 */

public class IBookManagerProxy implements IBookManager {

    private static final String DESCRIPTOR = "com.afirez.binder.api.IBookManager";

    private IBinder remote;

    public IBookManagerProxy(IBinder remote) {
        this.remote = remote;
    }

    @Override
    public void addBook(Book book) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            if (book != null) {
                data.writeInt(1);
                book.writeToParcel(data, 0);
            } else {
                data.writeInt(0);
            }
            remote.transact(IBookManagerStub.TRANSACTION_addBook, data, reply, 0);
            reply.readException();
        } finally {
            reply.recycle();
            data.recycle();
        }
    }

    @Override
    public List<Book> getBooks() throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        List<Book> result;
        try {
            data.writeInterfaceToken(DESCRIPTOR);
            remote.transact(IBookManagerStub.TRANSACTION_getBooks, data, reply, 0);
            reply.readException();
            result = reply.createTypedArrayList(Book.CREATOR);
        } finally {
            reply.recycle();
            data.recycle();
        }
        return result;
    }

    @Override
    public IBinder asBinder() {
        return remote;
    }
}
