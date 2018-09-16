package com.afirez.rxui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;

public class UiCall {

    public static Observable<UiResult> pushFragmentForResult(
            Object host,
            int containerId,
            String tag,
            Class<? extends Fragment> fragmentClass,
            Bundle args) {
        final int requestCode = requestCode();
        FragmentManager fm = Utils.fm(host);
        try {
            ResultFragment.pushFragmentForResultReal(
                    fm, host, containerId, tag, fragmentClass, args, requestCode);
            return ResultFragment.with(fm)
                    .rxUiResult()
                    .filter(new Predicate<UiResult>() {
                        @Override
                        public boolean test(UiResult uiResult) throws Exception {
                            return uiResult.requestCode == requestCode;
                        }
                    });
        } catch (Throwable e) {
            return Observable.error(e);
        }
    }

    public static void success(Fragment fragment) {
        success(fragment, new Intent());
    }

    public static void success(Fragment fragment, Intent data) {
        ResultFragment.success(fragment, data);
    }

    public static void error(Fragment fragment) {
        error(fragment, new Intent());
    }

    public static void error(Fragment fragment, Intent data) {
        ResultFragment.error(fragment, data);
    }

    public static Observable<UiResult> startActivityForResult(
            Object host,
            Intent intent) {
        final int requestCode = requestCode();
        FragmentManager fm = Utils.fm(host);
        try {
            ResultFragment.startActivityForResultReal(fm, intent, requestCode);
            return ResultFragment.with(fm)
                    .rxUiResult()
                    .filter(new Predicate<UiResult>() {
                        @Override
                        public boolean test(UiResult uiResult) throws Exception {
                            return uiResult.requestCode == requestCode;
                        }
                    });
        } catch (Throwable e) {
            return Observable.error(e);
        }
    }

    public static void success(FragmentActivity activity) {
        success(activity, new Intent());
    }

    public static void success(FragmentActivity activity, Intent data) {
        ResultFragment.success(activity, data);
    }

    public static void error(FragmentActivity activity) {
        error(activity, new Intent());
    }

    public static void error(FragmentActivity activity, Intent data) {
        ResultFragment.error(activity, data);
    }

    private static AtomicInteger requestCode = new AtomicInteger(65535);

    private static int requestCode() {
        requestCode.compareAndSet(0, 65535);
        return requestCode.getAndDecrement();
    }
}
