package com.gcml.common.repository.debug.repository;

import com.gcml.common.repository.IRepositoryHelper;
import com.gcml.common.repository.RepositoryApp;
import com.gcml.common.repository.debug.repository.local.SheetDao;
import com.gcml.common.repository.debug.repository.local.SheetDb;
import com.gcml.common.repository.debug.repository.remote.MusicService;
import com.gcml.common.repository.utils.RxResultUtils;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class MusicRepository {
    private IRepositoryHelper mRepositoryHelper = RepositoryApp.INSTANCE.repositoryComponent().repositoryHelper();

    private MusicService mMusicService = mRepositoryHelper.retrofitService(MusicService.class);

    private SheetDao mSheetDao = mRepositoryHelper.roomDb(SheetDb.class, SheetDb.class.getName()).sheetDao();

    public Observable<List<SheetEntity>> sheetListFromApi(
            String name,
            int page,
            int limit) {
        return mMusicService.sheets(name, page, limit)
                .compose(RxResultUtils.<List<SheetEntity>>apiResultTransformer());
    }

    public Observable<List<SongEntity>> songListFromApi(
            int sheetId,
            int page,
            int limit) {
        return mRepositoryHelper.retrofitService(MusicService.class).songs(3, "", sheetId, page, limit)
                .compose(RxResultUtils.<List<SongEntity>>apiResultTransformer());
    }

    public Observable<List<SheetEntity>> sheetListFromApiAndSaveDb(
            String name,
            int page,
            int limit) {
        return mMusicService.sheets(name, page, limit)
                .compose(RxResultUtils.<List<SheetEntity>>apiResultTransformer())
                .doOnNext(new Consumer<List<SheetEntity>>() {
                    @Override
                    public void accept(List<SheetEntity> sheetEntities) throws Exception {
                        mSheetDao.insertAll(sheetEntities);
                    }
                });
    }

    public Observable<List<SheetEntity>> sheetListFromDb(){
        return Observable.fromCallable(new Callable<List<SheetEntity>>() {
            @Override
            public List<SheetEntity> call() throws Exception {
                return mSheetDao.getAll();
            }
        });
    }

    public Observable<Object> deleteAllSheetsFromDb(){
        return Observable.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                 mSheetDao.deleteAll();
                 return new Object();
            }
        });
    }
}