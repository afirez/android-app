package com.afirez.onactivityresult;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import io.reactivex.Observable;


/**
 * Created by afirez on 18-1-2.
 */

public class OnResultHelper {

    private OnResultHelperFragment onResultHelperFragment;

    public OnResultHelper(FragmentActivity activity) {
        onResultHelperFragment = getOnResultHelperFragment(activity);
    }

    public OnResultHelper(Fragment fragment) {
        this(fragment.getActivity());
    }

    public OnResultHelperFragment getOnResultHelperFragment(FragmentActivity activity) {
        return (OnResultHelperFragment) getFragment(
                activity.getSupportFragmentManager(), OnResultHelperFragment.TAG);
    }

    private static Fragment getFragment(FragmentManager fm, String tag) {
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new OnResultHelperFragment();
            fm.beginTransaction()
                    .add(fragment, tag)
                    .commitNowAllowingStateLoss();
            fm.executePendingTransactions();
        }
        return fragment;
    }

    public Observable<ActivityResult> startActivityForResult(Class<?> clazz, int requestCode) {
        Intent intent = new Intent(onResultHelperFragment.getActivity(), clazz);
        return startActivityForResult(intent, requestCode);
    }

    public Observable<ActivityResult> startActivityForResult(Intent intent, int requestCode) {
        return onResultHelperFragment.startForResult(intent, requestCode);
    }

    public void startActivityForResult(Class<?> clazz, int requestCode, Callback callback) {
        Intent intent = new Intent(onResultHelperFragment.getActivity(), clazz);
        startActivityForResult(intent, requestCode, callback);
    }

    public void startActivityForResult(Intent intent, int requestCode, Callback callback) {
        onResultHelperFragment.startForResult(intent, requestCode, callback);
    }

    public interface Callback {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
