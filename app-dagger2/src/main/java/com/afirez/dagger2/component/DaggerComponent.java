package com.afirez.dagger2.component;

import android.app.Application;

import com.afirez.dagger2.DaggerDelegate;
import com.afirez.dagger2.module.DaggerModule;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by afirez on 18-2-1.
 */

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        DaggerModule.class
})
public interface DaggerComponent {
    Application application();

    void inject(DaggerDelegate delegate);
}
