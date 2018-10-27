package com.afirez.app.cache;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afirez.app.R;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.EvictProvider;

public class RxCacheActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_cache);
        CacheHelper.context = getApplicationContext();
    }

    private CompositeDisposable disposables = new CompositeDisposable();

    public void onSaveCache(View view) {
        Disposable disposable;
        disposable = Observable.<User>create(
                emitter -> {
                    User value = new User();
                    value.name = "user1";
                    value.login = "login";
                    emitter.onNext(value);
                    emitter.onComplete();
                })
                .compose(upstream -> CacheHelper.getUserCache().getUser(upstream, new EvictProvider(true)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            Log.i("user", user.toString());
                            Toast.makeText(this, user.toString(), Toast.LENGTH_SHORT).show();
                        },
                        e -> {
                            Log.i("user", "onSaveCache: " + e);
                            Toast.makeText(this, "onSaveCache: " + e, Toast.LENGTH_SHORT).show();
                        },
                        () -> {
                            Log.i("user", "onSaveCache: complete");
                        }
                );
        disposables.add(disposable);
    }

    public void onDeleteCache(View view) {
        Disposable disposable;
        disposable = Observable.<User>error(new RuntimeException())
                .compose(upstream -> CacheHelper.getUserCache().getUser(upstream, new EvictProvider(true)))
                .onErrorResumeNext(Observable.empty())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            Log.i("user", user.toString());
                            Toast.makeText(this, user.toString(), Toast.LENGTH_SHORT).show();
                        },
                        e -> {
                            Log.i("user", "onDeleteCache: " + e);
                            Toast.makeText(this, "onDeleteCache: " + e, Toast.LENGTH_SHORT).show();
                        },
                        () -> {
                            Log.i("user", "onDeleteCache: complete");
                            Toast.makeText(this, "onDeleteCache: complete", Toast.LENGTH_SHORT).show();
                        }
                );
        disposables.add(disposable);
    }

    public void onUpdateCache(View view) {
        Disposable disposable;
        disposable = Observable.<User>create(
                emitter -> {
                    User value = new User();
                    value.name = "user2";
                    value.login = "login";
                    emitter.onNext(value);
                    emitter.onComplete();
                })
                .compose(upstream -> CacheHelper.getUserCache().getUser(upstream, new EvictProvider(true)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            Log.i("user", user.toString());
                            Toast.makeText(this, user.toString(), Toast.LENGTH_SHORT).show();
                        },
                        e -> {
                            Log.i("user", "onUpdateCache: " + e);
                            Toast.makeText(this, "onUpdateCache: " + e, Toast.LENGTH_SHORT).show();
                        },
                        () -> {
                            Log.i("user", "onUpdateCache: complete");
                        }
                );
        disposables.add(disposable);
    }

    public void onFindCache(View view) {
        Disposable disposable;
        disposable = Observable.<User>empty()
                .compose(upstream -> CacheHelper.getUserCache().getUser(upstream, new EvictProvider(false)))
                .firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        user -> {
                            Log.i("user", user.toString());
                            Toast.makeText(this, user.toString(), Toast.LENGTH_SHORT).show();
                        },
                        e -> {
                            Log.i("user", "onFindCache: " + e);
                            Toast.makeText(this, "onFindCache: " + e, Toast.LENGTH_SHORT).show();
                        }
                );
        disposables.add(disposable);
    }

    @Override
    protected void onResume() {
        File cacheDir = CacheHelper.rxCacheDir;
        if (cacheDir != null) {
            String[] strings = cacheDir.list();
            for (String string : strings) {
                Log.d("cacheFiles", "onResume: " + string);
            }
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposables != null && !disposables.isDisposed()) {
            disposables.dispose();
        }
    }
}
