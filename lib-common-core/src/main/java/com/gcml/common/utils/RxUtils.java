package com.gcml.common.utils;


import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;

import com.gcml.common.repository.http.ApiException;
import com.gcml.common.repository.http.ApiResult;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;

/**
 * Created by afirez on 18-2-6.
 */

public class RxUtils {

    public static <T> ObservableTransformer<ApiResult<T>, T> apiResultTransformer() {
        return new ObservableTransformer<ApiResult<T>, T>() {
            @Override
            public ObservableSource<T> apply(Observable<ApiResult<T>> upstream) {
                return upstream.flatMap(
                        new Function<ApiResult<T>, Observable<T>>() {
                            @Override
                            public Observable<T> apply(ApiResult<T> result) {
                                if (result.isSuccessful()) {
                                    return Observable.just(result.getData());
                                } else {
                                    return Observable.error(new ApiException(result.getMessage()));
                                }
                            }
                        }
                );
            }
        };
    }

    public static <T> AutoDisposeConverter<T> autoDisposeConverter(LifecycleOwner owner) {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner));
    }

    public static <T> AutoDisposeConverter<T> autoDisposeConverter(LifecycleOwner owner, Lifecycle.Event event) {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(owner, event));
    }
}
