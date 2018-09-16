package com.gcml.common.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.AnimRes;
import android.support.annotation.AnimatorRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.gcml.common.demo.R;

public class UiCall {

    @AnimatorRes
    @AnimRes
    private static int enter = R.anim.push_in_left;
    @AnimatorRes
    @AnimRes
    private static int exit = R.anim.push_out_left;
    @AnimatorRes
    @AnimRes
    private static int popEnter = R.anim.push_in_right;
    @AnimatorRes
    @AnimRes
    private static int popExit = R.anim.push_out_right;

    public static int enter() {
        return enter;
    }

    public static void enter(int enter) {
        UiCall.enter = enter;
    }

    public static int exit() {
        return exit;
    }

    public static void exit(int exit) {
        UiCall.exit = exit;
    }

    public static int popEnter() {
        return popEnter;
    }

    public static void popEnter(int popEnter) {
        UiCall.popEnter = popEnter;
    }

    public static int popExit() {
        return popExit;
    }

    public static void popExit(int popExit) {
        UiCall.popExit = popExit;
    }

    public static class Result {
        public int requestCode;
        public int resultCode;
        public Intent data;

        public boolean isSuccess(){
            return resultCode == Activity.RESULT_OK;
        }
    }

    public interface ResultCallback {
        void onResult(Result result);
    }

    public static void pushFragmentForResult(
            FragmentActivity host,
            Class<? extends Fragment> fragmentClass,
           ResultCallback resultCallback) {
        ResultFragment.pushFragmentForResult(host, fragmentClass, resultCallback);
    }

    public static void pushFragmentForResult(
            Fragment host,
            Class<? extends Fragment> fragmentClass,
            UiCall.ResultCallback resultCallback) {
        ResultFragment.pushFragmentForResult(host, fragmentClass, resultCallback);
    }

    public static void finish(Fragment fragment) {
        ResultFragment.popFragment(fragment);
    }

    public static void success(Fragment fragment) {
        success(fragment, new Intent());
    }

    public static void success(Fragment fragment, Intent data) {
        ResultFragment.success(fragment, data);
    }

    public static void error(Fragment fragment) {
        error(fragment, new Intent());
    }

    public static void error(Fragment fragment, Intent data) {
        ResultFragment.error(fragment, data);
    }

    public static void startActivityForResult(
        FragmentActivity host,
        Intent intent,
        UiCall.ResultCallback resultCallback){
        ResultFragment.startActivityForResult(host, intent, resultCallback);
    }

    public static void startActivityForResult(
            Fragment host,
            Intent intent,
            UiCall.ResultCallback resultCallback){
        ResultFragment.startActivityForResult(host, intent, resultCallback);
    }

    public static void success(FragmentActivity activity) {
        success(activity, new Intent());
    }

    public static void success(FragmentActivity activity, Intent data) {
        ResultFragment.success(activity, data);
    }

    public static void error(FragmentActivity activity) {
        error(activity, new Intent());
    }

    public static void error(FragmentActivity activity, Intent data) {
        ResultFragment.error(activity, data);
    }
}
