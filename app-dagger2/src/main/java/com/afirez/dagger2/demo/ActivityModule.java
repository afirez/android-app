package com.afirez.dagger2.demo;

import com.afirez.dagger2.demo.MainActivity;
import com.afirez.dagger2.demo.MainActivityModule;
import com.afirez.dagger2.scope.ActivityScope;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by afirez on 18-1-4.
 */

@Module
public abstract class ActivityModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = MainActivityModule.class)
    abstract MainActivity contributeMainActivity();
}
