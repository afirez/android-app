package com.afirez.binder.api;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;

/**
 * Created by afirez on 18-4-11.
 */

public interface IBookManager extends IInterface {
    void addBook(Book book) throws RemoteException;
    List<Book> getBooks() throws RemoteException;
}
