package com.afirez.app.cache;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by afirez on 2018/5/8.
 */

public interface GitHubService {
    @GET("users/{user}")
    Call<User> getUser(@Path("user") String user);

    @GET("users/{user}")
    Observable<User> getRxUser(@Path("user") String user);
}
