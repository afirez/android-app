package com.afirez.rxui;

import android.app.Activity;
import android.content.Intent;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

public class UiResult {
    public int requestCode;
    public int resultCode;
    public Intent data;

    public UiResult() {
    }

    public static UiResult create(int requestCode, int resultCode, Intent data){
        UiResult uiResult = new UiResult();
        uiResult.requestCode = requestCode;
        uiResult.resultCode = resultCode;
        uiResult.data = data;
        return uiResult;
    }

    public static Observable<UiResult> rxSuccess(){
        return Observable.just(create(0, Activity.RESULT_OK, new Intent()));
    }

    public static Observable<UiResult> rxSuccess(Intent data){
        if (data == null) {
            data = new Intent();
        }
        return Observable.just(create(0, Activity.RESULT_OK, data));
    }

    public boolean isSuccessful() {
        return resultCode == Activity.RESULT_OK;
    }

    public static boolean isLogin;

    public static<T> ObservableTransformer<T, UiResult> ensureLogined(final Activity activity) {
        return new ObservableTransformer<T, UiResult>() {
            @Override
            public ObservableSource<UiResult> apply(Observable<T> upstream) {
                if (isLogin) {
                    return rxSuccess();
                }
                return UiCall.startActivityForResult(activity, new Intent());
            }
        };
    }
}
