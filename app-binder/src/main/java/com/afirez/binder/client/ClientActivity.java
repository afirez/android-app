package com.afirez.binder.client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.afirez.binder.Fragments;
import com.afirez.binder.R;
import com.afirez.binder.api.Book;

import java.util.List;

public class ClientActivity extends AppCompatActivity {

    private static final String TAG = "ClientActivity";

    private ClientFragment clientFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binder_activity_client);
        clientFragment = Fragments.of(this, ClientFragment.class);
        Log.d(TAG, "onCreate: " + Thread.currentThread().getId());
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        Log.d(TAG, "fragments: " + fragments);

        findViewById(R.id.binder_tv_add_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                book.setName("阅读");
                book.setPrice(18);
                if (clientFragment != null) {
                    clientFragment.addBook(book);
                }
            }
        });
    }

}
