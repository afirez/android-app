package com.gcml.common.repository.debug;

import com.gcml.common.repository.IRepositoryHelper;
import com.gcml.common.repository.RepositoryApp;
import com.gcml.common.repository.utils.RxResultUtils;

import java.util.List;

import io.reactivex.Observable;

public class MusicRepository {
    private IRepositoryHelper mRepositoryHelper = RepositoryApp.INSTANCE.repositoryComponent().repositoryHelper();

    private MusicService mMusicService = mRepositoryHelper.retrofitService(MusicService.class);

    public Observable<List<SheetEntity>> sheetList(
            String name,
            int page,
            int limit) {
        return mMusicService.sheets(name, page, limit)
                .compose(RxResultUtils.<List<SheetEntity>>apiResultTransformer());
    }

    public Observable<List<SongEntity>> songList(
            int sheetId,
            int page,
            int limit) {
        return mRepositoryHelper.retrofitService(MusicService.class).songs(3, "", sheetId, page, limit)
                .compose(RxResultUtils.<List<SongEntity>>apiResultTransformer());
    }
}
