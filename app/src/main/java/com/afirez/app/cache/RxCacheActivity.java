package com.afirez.app.cache;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.afirez.app.R;

import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;

public class RxCacheActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_cache);
        GitHubApiHelper gitHubApiHelper = new GitHubApiHelper();
        String account = "afirez";
        Observable<User> rxUser = gitHubApiHelper.getUser(account).subscribeOn(Schedulers.io());
        rxUser = Observable.create(emitter -> {
            User value = new User();
            value.name = "afirez";
            value.login = "user";
            emitter.onNext(value);
        });
        CacheHelper.context = getApplicationContext();
        disposable = CacheHelper.getUserCache()
                .getUser(rxUser, new DynamicKey(account), new EvictDynamicKey(false))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    Toast.makeText(this, user.toString(), Toast.LENGTH_SHORT).show();
                }, Throwable::printStackTrace);
    }

    private Disposable disposable;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
