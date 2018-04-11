package com.afirez.binder.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.afirez.binder.api.Book;

import java.util.ArrayList;
import java.util.List;

public class RemoteService extends Service {

    private static final String TAG = "IBookManager";

    private List<Book> books = new ArrayList<>();

    public RemoteService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Book book = new Book();
        book.setName("三体");
        book.setName("18");
        books.add(book);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return bookManager;
    }

    private final IBookManagerStub bookManager = new IBookManagerStub() {

        @Override
        public void addBook(Book book) throws RemoteException {
            synchronized (this) {
                if (book == null) {
                    return;
                }
                if (books == null) {
                    books = new ArrayList<>();
                }
                book.setPrice(book.getPrice() * 2);
                books.add(book);
                Log.d(TAG, "books: " + book.toString());
            }
        }

        @Override
        public List<Book> getBooks() throws RemoteException {
            synchronized (this) {
                if (books == null) {
                    books = new ArrayList<>();
                }
                return books;
            }
        }
    };
}
