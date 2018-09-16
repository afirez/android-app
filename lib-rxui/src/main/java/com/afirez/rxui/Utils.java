package com.afirez.rxui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public class Utils {
    public static FragmentManager fmForNeted(Object host) {
        if (host instanceof FragmentActivity) {
            return ((FragmentActivity) host).getSupportFragmentManager();
        }

        if (host instanceof Fragment) {
            ((Fragment) host).getChildFragmentManager();
        }

        throw new IllegalArgumentException("Illegal Argument: host");
    }

    public static FragmentManager fm(Object host) {
        if (host instanceof FragmentActivity) {
            return ((FragmentActivity) host).getSupportFragmentManager();
        }

        if (host instanceof Fragment) {
            FragmentActivity activity = ((Fragment) host).getActivity();
            if (activity == null) {
                return null;
            }
            return activity.getSupportFragmentManager();
        }

        throw new IllegalArgumentException("Illegal Argument: host");
    }

    public static <T extends Fragment> T newFragment(Class<T> fragmentClass) {
        try {
            return fragmentClass.newInstance();
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }
}
