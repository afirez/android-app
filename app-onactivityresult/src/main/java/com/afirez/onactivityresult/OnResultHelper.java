package com.afirez.onactivityresult;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import io.reactivex.Observable;


/**
 * Created by afirez on 18-1-2.
 */

public class OnResultHelper {

    public static OnResultHelper with(FragmentActivity activity) {
        return new OnResultHelper(activity);
    }

    public static OnResultHelper with(Fragment fragment) {
        return new OnResultHelper(fragment);
    }

    private OnResultHelperFragment onResultHelperFragment;

    public OnResultHelper(FragmentActivity activity) {
        onResultHelperFragment = OnResultHelperFragment.getInstance(activity);
    }

    public OnResultHelper(Fragment fragment) {
        this(fragment.getActivity());
    }

    public Observable<Result> startActivityForResult(Class<?> clazz) {
        Intent intent = new Intent(onResultHelperFragment.getActivity(), clazz);
        return startActivityForResult(intent);
    }

    public Observable<Result> startActivityForResult(Intent intent) {
        return onResultHelperFragment.startActivityForResult(intent);
    }

    public void startActivityForResult(Class<?> clazz, Callback callback) {
        Intent intent = new Intent(onResultHelperFragment.getActivity(), clazz);
        startActivityForResult(intent, callback);
    }

    public void startActivityForResult(Intent intent, Callback callback) {
        onResultHelperFragment.startActivityForResult(intent, callback);
    }

    public interface Callback {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    public static class Result {
        private int requestCode;
        private int resultCode;
        private Intent data;

        public Result(int requestCode, int resultCode, Intent data) {
            this.requestCode = requestCode;
            this.resultCode = resultCode;
            this.data = data;
        }

        public int getRequestCode() {
            return requestCode;
        }

        public void setRequestCode(int requestCode) {
            this.requestCode = requestCode;
        }

        public int getResultCode() {
            return resultCode;
        }

        public void setResultCode(int resultCode) {
            this.resultCode = resultCode;
        }

        public Intent getData() {
            return data;
        }

        public void setData(Intent data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "requestCode=" + requestCode +
                    ", resultCode=" + resultCode +
                    ", data=" + data +
                    '}';
        }
    }
}
