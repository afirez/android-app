package com.afirez.onactivityresult;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.Map;
import java.util.WeakHashMap;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by afirez on 18-1-2.
 */

public class OnResultHelperFragment extends Fragment {

    public static final String TAG = "OnResultHelperFragment";

    public OnResultHelperFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    // WeakHashMap Instance
    private volatile Map<Integer, PublishSubject<OnResultHelper.Result>> mRxResults;

    public Observable<OnResultHelper.Result> startForResult(final Intent intent) {
        PublishSubject<OnResultHelper.Result> rxResult = PublishSubject.create();
        if (mRxResults == null) {
            mRxResults = new WeakHashMap<>();
        }
        final int requestCode = getRequestCode();
        mRxResults.put(requestCode, rxResult);
        return rxResult.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                startActivityForResult(intent, requestCode);
            }
        });
    }

    // WeakHashMap Instance
    private volatile Map<Integer, OnResultHelper.Callback> mCallBacks;

    public void startForResult(Intent intent, OnResultHelper.Callback callback) {
        if (mCallBacks == null) {
            mCallBacks = new WeakHashMap<>();
        }
        final int requestCode = getRequestCode();
        mCallBacks.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mRxResults != null) {
            //RxJava
            PublishSubject<OnResultHelper.Result> rxResult = mRxResults.remove(requestCode);
            if (rxResult != null) {
                rxResult.onNext(new OnResultHelper.Result(requestCode, resultCode, data));
                rxResult.onComplete();
            }
        }
        if (mCallBacks != null) {
            //Callback
            OnResultHelper.Callback callback = mCallBacks.remove(requestCode);
            if (callback != null) {
                callback.onActivityResult(requestCode, resultCode, data);
            }
        }
    }


    // requestCode between [0,32767] here.
    // requestCode need between (-32767 ,32768), within 16 bit.
    private int requestCode = 0;

    public synchronized int getRequestCode() {
        int requestCode = this.requestCode;
        this.requestCode++;
        if (this.requestCode >= 32768) {
            this.requestCode = 0;
        }
        return requestCode;
    }
}
