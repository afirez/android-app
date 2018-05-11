package com.afirez.app.cache;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.afirez.app.Utils;

import java.io.File;

import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;

/**
 * Created by afirez on 2018/5/8.
 */

public class CacheHelper {

    private static volatile UserCacheProviders userCacheProviders;

    public static UserCacheProviders getUserCache() {
        if (userCacheProviders == null) {
            synchronized (CacheHelper.class) {
                if (userCacheProviders == null) {
                    File cacheDir = Utils.getCacheDirectory(context, "rxcache");
                    Log.d("cacheDir", "getUserCache: " + cacheDir.getAbsolutePath());
                    userCacheProviders = new RxCache.Builder()
                            .persistence(cacheDir, new GsonSpeaker())
                            .using(UserCacheProviders.class);
                    rxCacheDir = cacheDir;
                }
            }
        }
        return userCacheProviders;
    }

    public static Context context;

    public static File rxCacheDir;

}
