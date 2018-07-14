package com.gcml.common.repository.debug;

import com.gcml.common.repository.http.ApiResult;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MusicService {
    @GET("rep/sel_music_danforapp")
    Observable<ApiResult<List<SheetEntity>>> sheets(
            @Query("mname") String name,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("rep/selSomeImitate")
    Observable<ApiResult<List<SongEntity>>> songs(
            @Query("type") int type,
            @Query("wr") String singer,
            @Query("mid") int sheetId,
            @Query("page") int page,
            @Query("limit") int limit
    );
}
