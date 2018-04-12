package com.afirez.binder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by afirez on 18-4-12.
 */

public class Fragments {

    public static <T extends Fragment> T of(FragmentActivity activity, Class clazz) {
        return of(activity.getSupportFragmentManager(), clazz);
    }

    public static <T extends Fragment> T of(FragmentManager fm, Class clazz) {
        Fragment fragment = fm.findFragmentByTag(clazz.getName());
        if (fragment == null) {
            try {
                fragment = (Fragment) clazz.newInstance();
                fm.beginTransaction()
                        .add(fragment, clazz.getName())
                        .commitAllowingStateLoss();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        if (fragment == null) {
            return null;
        }
        return (T) fragment;
    }

    public static <T extends Fragment> T of(FragmentActivity activity, String name) {
        try {
            return of(activity.getSupportFragmentManager(), Class.forName(name));
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T extends Fragment> T of(FragmentManager fm, String name) {
        try {
            return of(fm, Class.forName(name));
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
