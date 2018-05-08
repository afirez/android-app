package com.afirez.app.cache;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.LifeCache;

/**
 * Created by afirez on 2018/5/8.
 */

public interface UserCacheProviders {
    Observable<User> getUser(
            Observable<User> user,
            DynamicKey userName,
            EvictDynamicKey evictDynamicKey
    );
}
