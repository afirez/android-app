package com.gcml.common.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

/**
 * Created by afirez on 2017/7/13.
 */
public abstract class BaseFragment<V extends IView, P extends IPresenter>
        extends Fragment
        implements IView, UiFactory<V, P> {
    protected P presenter;
    private V baseView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            baseView = (V) context;
        } catch (Exception e) {
            throw new RuntimeException("activity must implement IView");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (presenter == null) {
            presenter = providePresenter(provideView());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (presenter != null) {
            presenter.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter.onResume();
        }
    }

    @Override
    public void onPause() {
        if (presenter != null) {
            presenter.onPause();
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (presenter != null) {
            presenter.onStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (presenter != null) {
            presenter.onDestroy();
        }
        baseView = null;
        super.onDestroy();
    }

    @Override
    public void showLoading() {
        baseView.showLoading();
    }

    @Override
    public void hideLoading() {
        baseView.hideLoading();
    }

    @Override
    public void showTip(@StringRes int resId) {
        baseView.showTip(resId);
    }

    @Override
    public void showTip(String tip) {
        baseView.showTip(tip);
    }

    @Override
    public void showError(@StringRes int resId) {
        baseView.showError(resId);
    }

    @Override
    public void showError(String error) {
        baseView.showError(error);
    }
}