package com.afirez.dagger2;

import com.afirez.dagger2.demo.MainActivity;
import com.afirez.dagger2.demo.MainModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by afirez on 18-1-4.
 */

@Module
public abstract class UiModule {
    @ActivityScope
    @ContributesAndroidInjector(modules = MainModule.class)
    abstract MainActivity bindMainActivity();
}
