package com.afirez.rxui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import timber.log.Timber;


public class ResultFragment extends Fragment {

    public static final String TAG = "ResultFragment";

    private Subject<UiResult> rxUiResult = BehaviorSubject.<UiResult>create().toSerialized();

    public Observable<UiResult> rxUiResult() {
        return rxUiResult;
    }

    public static ResultFragment with(FragmentManager fm) {
        ResultFragment rf = (ResultFragment) fm.findFragmentByTag(ResultFragment.TAG);
        if (rf == null) {
            rf = new ResultFragment();
            fm.beginTransaction().add(rf, ResultFragment.TAG).commitNow();
        }
        return rf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public static void popFragment(Fragment fragment) {
        FragmentManager fm = fragment.getFragmentManager();
        if (fm != null) {
            fm.popBackStackImmediate();
        }
    }

    public static int pushFragmentForResult(
            Object host,
            int containerId,
            String tag,
            Class<? extends Fragment> fragmentClass,
            Bundle args,
            int requestCode) {
        FragmentManager fm = Utils.fm(host);
        if (fm == null) {
            return -1;
        }
        return pushFragmentForResultReal(fm, host, containerId, tag, fragmentClass, args, requestCode);
    }

    public static int pushFragmentForResultReal(
            FragmentManager fm,
            Object host,
            int containerId,
            String tag,
            Class<? extends Fragment> fragmentClass,
            Bundle args,
            int requestCode) {
        Fragment fragment = fm.findFragmentByTag(tag);
        if (fragment != null) {
            throw new IllegalStateException(
                    String.format("Fragment %s tagged with %s has added in %s ", fragment, tag, fm));
        }
        fragment = Utils.newFragment(fragmentClass);

        Bundle arguments = fragment.getArguments();
        if (arguments == null) {
            arguments = new Bundle();
            fragment.setArguments(arguments);
        }
        arguments.putAll(args);

        FragmentTransaction transaction = fm.beginTransaction();
        if (fm.getFragments() != null && fm.getFragments().size() != 0) {
            transaction.hide(fm.getFragments().get(fm.getFragments().size() - 1));

        }
        transaction.add(containerId, fragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
        ResultFragment resultFragment = ResultFragment.with(fm);
        fragment.setTargetFragment(resultFragment, requestCode);
        return requestCode;
    }

    public static void success(Fragment fragment) {
        success(fragment, new Intent());
    }

    public static void success(Fragment fragment, Intent data) {
        Fragment targetFragment = fragment.getTargetFragment();
        if (targetFragment != null) {
            int requestCode = fragment.getTargetRequestCode();
            Timber.i("success requestCode: %s, callback to: %s, from: %s ", requestCode, targetFragment, fragment);
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
            Timber.i("error requestCode: %s,callback to: %s, from: %s", requestCode, targetFragment, fragment);
            targetFragment.onActivityResult(requestCode, Activity.RESULT_CANCELED, data);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        rxUiResult.onNext(UiResult.create(requestCode, resultCode, data));
    }

    public static int startActivityForResult(
            Object host,
            Intent intent,
            int requestCode) {
        FragmentManager fm;
        if (host instanceof FragmentActivity) {
            fm = ((FragmentActivity) host).getSupportFragmentManager();
        } else if (host instanceof Fragment) {
            FragmentActivity activity = ((Fragment) host).getActivity();
            if (activity == null) {
                throw new IllegalStateException("Illegal State: activity == null");
            }
            fm = activity.getSupportFragmentManager();
        } else {
            throw new IllegalArgumentException("Illegal Argument: host");
        }

        if (fm != null) {
            return startActivityForResultReal(fm, intent, requestCode);
        }
        throw new IllegalStateException("Illegal State: fm == null");
    }

    public static int startActivityForResultReal(
            FragmentManager fm,
            Intent intent,
            int requestCode) {
        ResultFragment resultFragment = with(fm);
        resultFragment.startActivityForResult(intent, requestCode);
        return requestCode;
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
}