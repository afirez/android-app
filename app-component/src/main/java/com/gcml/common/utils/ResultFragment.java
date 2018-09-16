package com.gcml.common.utils;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

public class ResultFragment extends Fragment {

    public static final String TAG = ResultFragment.class.getName();

    private Map<Integer, UiCall.ResultCallback> resultCallbackMap = new ConcurrentHashMap<>();

    private void setResultCallback(int requestCode, UiCall.ResultCallback resultCallback) {
        resultCallbackMap.put(requestCode, resultCallback);
    }

    private UiCall.ResultCallback getResultCallback(int requestCode) {
        return resultCallbackMap.remove(requestCode);
    }

    private static AtomicInteger requestCode = new AtomicInteger(65535);

    private static int requestCode() {
        requestCode.compareAndSet(0, 65535);
        return requestCode.getAndDecrement();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        UiCall.Result result = new UiCall.Result();
        result.requestCode = requestCode;
        result.resultCode = resultCode;
        result.data = data;
        UiCall.ResultCallback resultCallback = resultCallbackMap.remove(requestCode);
        Timber.i("requestCode: %s, onResult: %s", requestCode, resultCallback);
        if (resultCallback != null) {
            resultCallback.onResult(result);
        }
    }

    public static void pushFragmentForResult(
            Fragment host,
            Class<? extends Fragment> fragmentClass,
            UiCall.ResultCallback resultCallback) {
        FragmentManager fm = host.getFragmentManager();
        if (fm == null) {
            return;
        }
        pushFragmentForResult(fm, host, fragmentClass, resultCallback);
    }

    public static void pushFragmentForResult(
            FragmentActivity host,
            Class<? extends Fragment> fragmentClass,
            UiCall.ResultCallback resultCallback) {
        FragmentManager fm = host.getSupportFragmentManager();
        if (fm == null) {
            return;
        }

        pushFragmentForResult(fm, host, fragmentClass, resultCallback);
    }

    private static void pushFragmentForResult(
            FragmentManager fm,
            Object host,
            Class<? extends Fragment> fragmentClass,
            UiCall.ResultCallback resultCallback) {
        String tag = fragmentClass.getName();

        Fragment fragment = fm.findFragmentByTag(tag);
        FragmentTransaction transaction = fm.beginTransaction();
        if (fragment != null) {
            Timber.e("Fragment %s has added in %s ", fragment, fm);
            transaction.remove(fragment);
        }

        fragment = newFragment(fragmentClass);
        int entryCount = fm.getBackStackEntryCount();
        if (entryCount != 0) {
            transaction.setCustomAnimations(
                    UiCall.enter(),
                    UiCall.exit(),
                    UiCall.popEnter(),
                    UiCall.popExit()
            );
        }
        transaction.add(android.R.id.content, fragment, tag);
        if (fm.getFragments() != null && fm.getFragments().size() != 0) {
            transaction.hide(fm.getFragments().get(fm.getFragments().size() - 1));

        }
        transaction.addToBackStack(tag);
        transaction.commit();

        Timber.i("fragments in fm: %s:", fm);
        List<Fragment> fragments = fm.getFragments();
        for (Fragment fragment1 : fragments) {
            Timber.i("%s", fragment1);
        }

        Timber.i("backStack in fm: %s:", fm);
        int backStackEntryCount = fm.getBackStackEntryCount();
        for (int i = 0; i < backStackEntryCount; i++) {
            Timber.i("%s", fm.getBackStackEntryAt(i));
        }

        ResultFragment resultFragment = ResultFragment.with(fm);
        int requestCode = requestCode();
        resultFragment.setResultCallback(requestCode, resultCallback);
        Timber.i("requestCode: %s, call to : %s, from : %s", requestCode, fragment, fm);
        fragment.setTargetFragment(resultFragment, requestCode);
    }

    private static <T extends Fragment> T newFragment(Class<T> fragmentClass) {
        try {
            return fragmentClass.newInstance();
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    public static ResultFragment with(FragmentActivity host) {
        FragmentManager fm = host.getSupportFragmentManager();
        if (fm == null) {
            return null;
        }
        return with(fm);
    }

    public static ResultFragment with(Fragment host) {
        FragmentManager fm = host.getChildFragmentManager();
        return with(fm);
    }

    public static ResultFragment with(FragmentManager fm) {
        ResultFragment rf = (ResultFragment) fm.findFragmentByTag(ResultFragment.TAG);
        if (rf == null) {
            rf = new ResultFragment();
            Timber.i("add ResultFragment fm: %s", fm);
            fm.beginTransaction().add(rf, ResultFragment.TAG).commitNow();
        }
        return rf;
    }

    public static void success(Fragment fragment) {
        success(fragment, new Intent());
    }

    public static void success(Fragment fragment, Intent data) {
        Fragment targetFragment = fragment.getTargetFragment();
        if (targetFragment != null) {
            int requestCode = fragment.getTargetRequestCode();
            Timber.i("requestCode: %s, callback to: %s, from: %s", requestCode, targetFragment, fragment);
            targetFragment.onActivityResult(requestCode, Activity.RESULT_OK, data);
        }
    }

    public static void error(Fragment fragment) {
        error(fragment, new Intent());
    }

    public static void error(Fragment fragment, Intent data) {
        Fragment targetFragment = fragment.getTargetFragment();
        if (targetFragment != null) {
            int requestCode = fragment.getTargetRequestCode();
            Timber.i("requestCode: %s, targetFragment: %s", requestCode, targetFragment.getClass().getName());
            targetFragment.onActivityResult(requestCode, Activity.RESULT_CANCELED, data);
        }
    }

    public static void startActivityForResult(
            FragmentActivity host,
            Intent intent,
            UiCall.ResultCallback resultCallback) {
        FragmentManager fm = host.getSupportFragmentManager();
        if (fm != null) {
            startActivityForResult(fm, intent, resultCallback);
        }
    }

    public static void startActivityForResult(
            Fragment host,
            Intent intent,
            UiCall.ResultCallback resultCallback) {
        FragmentManager fm = host.getChildFragmentManager();
        startActivityForResult(fm, intent, resultCallback);
    }

    private static void startActivityForResult(
            FragmentManager fm,
            Intent intent,
            UiCall.ResultCallback resultCallback) {
        ResultFragment resultFragment = with(fm);
        int requestCode = requestCode();
        resultFragment.setResultCallback(requestCode, resultCallback);
        resultFragment.startActivityForResult(intent, requestCode);
    }

    public static void success(FragmentActivity activity) {
        success(activity, new Intent());
    }

    public static void success(FragmentActivity activity, Intent data) {
        activity.setResult(Activity.RESULT_OK, data);
    }

    public static void error(FragmentActivity activity) {
        error(activity, new Intent());
    }

    public static void error(FragmentActivity activity, Intent data) {
        activity.setResult(Activity.RESULT_CANCELED, data);
    }

    public static void popFragment(Fragment fragment) {
        FragmentManager fm = fragment.getFragmentManager();
        if (fm != null) {
            fm.popBackStackImmediate();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resultCallbackMap.clear();
    }
}
