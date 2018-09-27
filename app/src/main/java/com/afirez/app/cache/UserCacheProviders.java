package com.afirez.app.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.LifeCache;
import io.rx_cache2.ProviderKey;

/**
 * Created by afirez on 2018/5/8.
 */

public interface UserCacheProviders {
    @ProviderKey("user")
    Observable<User> getUser(
            Observable<User> user,
            EvictProvider evictProvider
    );

    @ProviderKey("users")
    Observable<List<User>> getUsers(
            Observable<List<User>> users,
            EvictProvider evictProvider
    );
}
