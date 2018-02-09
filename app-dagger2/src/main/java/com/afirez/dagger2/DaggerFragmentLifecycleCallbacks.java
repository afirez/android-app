package com.afirez.dagger2;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;

/**
 * Created by afirez on 18-2-1.
 */

public class DaggerFragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

    @Inject
    public DaggerFragmentLifecycleCallbacks() {
    }

    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
        super.onFragmentAttached(fm, f, context);
//        Timber.i(f.toString() + " ---> onFragmentAttached");
        AndroidSupportInjection.inject(f);
    }

    @Override
    public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentActivityCreated(fm, f, savedInstanceState);
//        Timber.i(f.toString() + " ---> onFragmentActivityCreated");
    }

    @Override
    public void onFragmentDetached(FragmentManager fm, Fragment f) {
        super.onFragmentDetached(fm, f);
//        Timber.i(f.toString() + " ---> onFragmentDetached");
    }
}
