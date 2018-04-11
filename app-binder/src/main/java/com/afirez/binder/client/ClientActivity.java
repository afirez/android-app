package com.afirez.binder.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.afirez.binder.api.Book;
import com.afirez.binder.R;
import com.afirez.binder.api.IBookManager;
import com.afirez.binder.server.IBookManagerStub;
import com.afirez.binder.server.RemoteService;

import java.util.List;

public class ClientActivity extends AppCompatActivity {

    private static final String TAG = "ClientActivity";

    private volatile boolean bound;

    private IBookManager iBookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binder_activity_client);
        findViewById(R.id.binder_tv_add_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bound) {
                    bindService();
                    return;
                }
                if (iBookManager == null) {
                    return;
                }
                try {
                    Book book = new Book();
                    book.setName("阅读");
                    book.setPrice(18);
                    iBookManager.addBook(book);
                    Log.d(TAG, "addBook: " + iBookManager.getBooks().toString());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void bindService() {
        Intent intent = new Intent(this, RemoteService.class);
        intent.setAction("com.afirez.binder.server");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iBookManager = IBookManagerStub.asInterface(service);
            if (iBookManager != null) {
                try {
                    List<Book> books = iBookManager.getBooks();
                    Log.d(TAG, "onServiceConnected: " + books.toString());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if (!bound) {
            bindService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(serviceConnection);
        }
    }
}
