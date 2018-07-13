package com.gcml.common.repository.debug;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gcml.common.repository.R;
import com.gcml.common.repository.debug.repository.MusicRepository;
import com.gcml.common.repository.debug.repository.SheetEntity;
import com.gcml.common.repository.utils.DefaultObserver;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private MusicRepository mMusicRepository;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMusicRepository = new MusicRepository();

    }

    public void onNetwork(View view) {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        Observable<List<SheetEntity>> listObservable = mMusicRepository.sheetListFromApi("", 1, 12);
        mDisposable = listObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Toast.makeText(MainActivity.this, String.valueOf(Looper.myLooper() == Looper.getMainLooper()) + "开始...", Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Toast.makeText(MainActivity.this, String.valueOf(Looper.myLooper() == Looper.getMainLooper()) + "结束！！！", Toast.LENGTH_SHORT).show();
                    }
                })
                .subscribeWith(new DefaultObserver<List<SheetEntity>>() {
                    @Override
                    public void onNext(List<SheetEntity> sheetEntities) {
                        super.onNext(sheetEntities);
                        Toast.makeText(MainActivity.this, sheetEntities.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public void onNetworkAndWriteDb(View view) {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        Observable<List<SheetEntity>> listObservable = mMusicRepository.sheetListFromApiAndSaveDb("", 1, 12);
        mDisposable = listObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Toast.makeText(MainActivity.this, String.valueOf(Looper.myLooper() == Looper.getMainLooper()) + "开始...", Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Toast.makeText(MainActivity.this, String.valueOf(Looper.myLooper() == Looper.getMainLooper()) + "结束！！！", Toast.LENGTH_SHORT).show();
                    }
                })
                .subscribeWith(new DefaultObserver<List<SheetEntity>>() {
                    @Override
                    public void onNext(List<SheetEntity> sheetEntities) {
                        super.onNext(sheetEntities);
                        Toast.makeText(MainActivity.this, sheetEntities.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public void onReadDb(View view) {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        Observable<List<SheetEntity>> listObservable = mMusicRepository.sheetListFromDb();
        mDisposable = listObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Toast.makeText(MainActivity.this, String.valueOf(Looper.myLooper() == Looper.getMainLooper()) + "开始...", Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Toast.makeText(MainActivity.this, String.valueOf(Looper.myLooper() == Looper.getMainLooper()) + "结束！！！", Toast.LENGTH_SHORT).show();
                    }
                })
                .subscribeWith(new DefaultObserver<List<SheetEntity>>() {
                    @Override
                    public void onNext(List<SheetEntity> sheetEntities) {
                        super.onNext(sheetEntities);
                        Toast.makeText(MainActivity.this, sheetEntities.toString(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    public void onDeleteDb(View view) {
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        Observable<Object> listObservable = mMusicRepository.deleteAllSheetsFromDb();
        mDisposable = listObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        Toast.makeText(MainActivity.this, String.valueOf(Looper.myLooper() == Looper.getMainLooper()) + "开始...", Toast.LENGTH_SHORT).show();
                    }
                })
                .doOnTerminate(new Action() {
                    @Override
                    public void run() throws Exception {
                        Toast.makeText(MainActivity.this, String.valueOf(Looper.myLooper() == Looper.getMainLooper()) + "结束！！！", Toast.LENGTH_SHORT).show();
                    }
                })
                .subscribeWith(new DefaultObserver<Object>() {
                    @Override
                    public void onNext(Object o) {
                        super.onNext(o);
                        Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}

