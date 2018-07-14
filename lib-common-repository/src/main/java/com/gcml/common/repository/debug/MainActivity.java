package com.gcml.common.repository.debug;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gcml.common.repository.R;
import com.gcml.common.repository.utils.DefaultObserver;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
        Observable<List<SheetEntity>> listObservable = mMusicRepository.sheetList("", 1, 12);
        mDisposable = listObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
}

