package com.afirez.dagger2;

import android.app.Application;

import com.afirez.dagger2.component.DaggerComponent;
import com.afirez.dagger2.module.DaggerModule;

import javax.inject.Inject;

/**
 * Created by afirez on 18-2-1.
 */

public class DaggerDelegate {

    @Inject
    DaggerActivityLifecycleCallbacks mActivityLifecycleCallbacks;

    private DaggerComponent mComponent;

    private final Application mApplication;

    @Inject
    public DaggerDelegate(Application application) {
        mApplication = application;
//        onCreate();
    }

    public void onCreate() {
        mComponent = DaggerDaggerComponent.builder()
                .daggerModule(new DaggerModule(mApplication))
                .build();
        mComponent.inject(this);
        mApplication.registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    public DaggerComponent component() {
        return mComponent;
    }
}
