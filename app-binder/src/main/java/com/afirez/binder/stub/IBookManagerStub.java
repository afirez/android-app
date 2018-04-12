package com.afirez.binder.stub;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import com.afirez.binder.api.Book;
import com.afirez.binder.api.IBookManager;
import com.afirez.binder.proxy.IBookManagerProxy;

import java.util.List;

/**
 * Created by afirez on 18-4-11.
 */

public abstract class IBookManagerStub extends Binder implements IBookManager {

    private static final String TAG = "IBookManagerStub";

    private static final String DESCRIPTOR = "com.afirez.binder.api.IBookManager";


    public static IBookManager asInterface(IBinder binder) {
        if (binder == null) {
            return null;
        }
        IInterface iInterface = binder.queryLocalInterface(DESCRIPTOR);
        if (iInterface != null && iInterface instanceof IBookManager) {
            return (IBookManager) iInterface;
        }
        return new IBookManagerProxy(binder);
    }

    public IBookManagerStub() {
        attachInterface(this, DESCRIPTOR);
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        Log.d(TAG, "onTransact: " + Thread.currentThread().getName());
        Log.d(TAG, "onTransact: " + code);
        switch (code) {
            case INTERFACE_TRANSACTION:
                reply.writeString(DESCRIPTOR);
                return true;
            case TRANSACTION_addBook:
                data.enforceInterface(DESCRIPTOR);
                Book book = null;
                if (data.readInt() != 0) {
                    book = Book.CREATOR.createFromParcel(data);
                }
                addBook(book);
                reply.writeNoException();
                return true;
            case TRANSACTION_getBooks:
                data.enforceInterface(DESCRIPTOR);
                List<Book> result = this.getBooks();
                reply.writeNoException();
                reply.writeTypedList(result);
                return true;
        }
        Log.d(TAG, "onTransact: " + Thread.currentThread().getName());
        return super.onTransact(code, data, reply, flags);
    }

    public static final int TRANSACTION_addBook = IBinder.FIRST_CALL_TRANSACTION;
    public static final int TRANSACTION_getBooks = IBinder.FIRST_CALL_TRANSACTION + 1;
}
