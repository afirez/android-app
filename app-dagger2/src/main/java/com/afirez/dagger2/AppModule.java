package com.afirez.dagger2;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by afirez on 18-1-4.
 */

@Module
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(App app) {
        return app;
    }
}