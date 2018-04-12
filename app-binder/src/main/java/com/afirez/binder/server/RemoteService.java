package com.afirez.binder.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.afirez.binder.api.Book;
import com.afirez.binder.stub.IBookManagerStub;

import java.util.ArrayList;
import java.util.List;

public class RemoteService extends Service {

    private static final String TAG = "Remote";

    private volatile List<Book> books = new ArrayList<>();

    public RemoteService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread thread = Thread.currentThread();
        thread.setName("remote:main");
        Log.d(TAG, "onCreate: " + thread.getName());
        Book book = new Book();
        book.setName("三体");
        book.setName("18");
        books.add(book);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: " + Thread.currentThread().getName());
        return bookManager;
    }

    private final IBookManagerStub bookManager = new IBookManagerStub() {

        @Override
        public void addBook(Book book) throws RemoteException {
            Log.d(TAG, "addBook: " + Thread.currentThread().getName());
            synchronized (this) {
                Log.d(TAG, "addBook: ");
                if (book == null) {
                    return;
                }
                if (books == null) {
                    books = new ArrayList<>();
                }
                book.setPrice(book.getPrice() * 2);
                books.add(book);
                Log.d(TAG, "addBook: " + Thread.currentThread().getName());
                Log.d(TAG, "books: " + book.toString());
            }
        }

        @Override
        public List<Book> getBooks() throws RemoteException {
            Log.d(TAG, "getBooks: " + Thread.currentThread().getName());
            synchronized (this) {
                Log.d(TAG, "getBooks: ");
                if (books == null) {
                    books = new ArrayList<>();
                }
                Log.d(TAG, "getBooks: " + Thread.currentThread().getName());
                Log.d(TAG, "getBooks: " + books.toString());
                return books;
            }
        }
    };
}
