package com.afirez.onactivityresult;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.HashMap;
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

    private volatile Map<Integer, PublishSubject<ActivityResult>> mRxResults;

    // WeakHashMap Instance
    private volatile Map<Integer, OnResultHelper.Callback> mCallBacks;

    public OnResultHelperFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    public Observable<ActivityResult> startForResult(final Intent intent, final int requestCode) {
        PublishSubject<ActivityResult> rxResult = PublishSubject.create();
        if (mRxResults == null) {
            mRxResults = new HashMap<>();
        }
        mRxResults.put(requestCode, rxResult);
        return rxResult.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                startActivityForResult(intent, requestCode);
            }
        });
    }

    public void startForResult(Intent intent, int requestCode, OnResultHelper.Callback callback) {
        if (mCallBacks == null) {
            mCallBacks = new WeakHashMap<>();
        }
        mCallBacks.put(requestCode, callback);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mRxResults != null) {
            //RxJava
            PublishSubject<ActivityResult> rxResult = mRxResults.remove(requestCode);
            if (rxResult != null) {
                rxResult.onNext(new ActivityResult(requestCode, resultCode, data));
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
}
