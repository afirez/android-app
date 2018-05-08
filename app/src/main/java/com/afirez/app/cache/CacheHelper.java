package com.afirez.app.cache;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

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
                    File cacheDir = getCacheDir();
                    Log.d("cacheDir", "getUserCache: " + cacheDir.getAbsolutePath());
                    userCacheProviders = new RxCache.Builder()
                            .persistence(cacheDir, new GsonSpeaker())
                            .using(UserCacheProviders.class);
                }
            }
        }
        return userCacheProviders;
    }

    private static final File RX_CACHE_DIR;

    static {
        RX_CACHE_DIR = new File(Environment.getExternalStorageDirectory(), "RxCache");
        if (!RX_CACHE_DIR.exists()) {
            Log.d("rxCacheDir", "instance initializer: " + RX_CACHE_DIR.mkdirs());
        }
    }

    public static Context context;

    private static volatile File rxCacheDir;

    private static File getCacheDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file;
            file = context.getExternalCacheDir();//获取系统管理的sd卡缓存文件
            if (file == null) {//如果获取的文件为空,就使用自己定义的缓存文件夹做缓存路径
                file = new File(getCacheFilePath(context));
                if (!file.exists()) {
                    boolean mkdirs = file.mkdirs();
                    Log.d("mkdirs", "getCacheDir: " + mkdirs);
                }
            }
            return file;
        } else {
            return context.getCacheDir();
        }
    }

    public static String getCacheFilePath(Context context) {
        String packageName = context.getPackageName();
        return Environment.getExternalStorageDirectory().getAbsolutePath() + packageName;
    }

}
