package com.afirez.binder.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.afirez.binder.api.Book;
import com.afirez.binder.api.IBookManager;
import com.afirez.binder.server.IBookManagerStub;
import com.afirez.binder.server.RemoteService;

import java.util.List;

/**
 * Created by afirez on 18-4-12.
 */

public class ClientFragment extends Fragment {

    private static final String TAG = "ClientFragment";

    private volatile IBookManager iBookManager;

    public ClientFragment() {
        setRetainInstance(true);
    }

    private Book book;

    public void addBook(Book book) {
        if (book == null) {
            return;
        }
        if (iBookManager == null) {
            this.book = book;
            bindService();
            return;
        }
        try {
            iBookManager.addBook(book);
            Log.d(TAG, "addBook: " + iBookManager.getBooks().toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        this.book = null;
    }

    private void bindService() {
        if (context != null) {
            Log.d(TAG, "bindService: ");
            Intent intent = new Intent(context, RemoteService.class);
            intent.setAction("com.afirez.binder.server");
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            iBookManager = IBookManagerStub.asInterface(service);
            addBook(book);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");
            iBookManager = null;
        }
    };

    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        this.context = context.getApplicationContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
        context = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        if (iBookManager == null) {
            bindService();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if (iBookManager != null) {
            iBookManager = null;
            if (context != null) {
                Log.d(TAG, "unbindService: ");
                context.unbindService(serviceConnection);
            }
        }
    }
}
