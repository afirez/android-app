package com.afirez.dagger2;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by afirez on 18-2-1.
 */

public class DaggerActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    @Inject
    DaggerFragmentLifecycleCallbacks mFragmentLifecycleCallbacks;

    @Inject
    public DaggerActivityLifecycleCallbacks() {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//        Timber.w(activity + " ---> onActivityCreated");
        AndroidInjection.inject(activity);//Dagger.Android Inject for Activity
        if ((activity instanceof HasSupportFragmentInjector || activity.getApplication() instanceof HasSupportFragmentInjector)
                && activity instanceof FragmentActivity) {
            ((FragmentActivity) activity).getSupportFragmentManager()
                    .registerFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks, true);
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
//        Timber.w(activity + " ---> onActivityDestroyed");
    }
}
