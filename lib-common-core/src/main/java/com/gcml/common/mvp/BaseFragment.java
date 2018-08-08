package com.gcml.common.mvp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

import com.gcml.common.repository.utils.Preconditions;
import com.gcml.common.utils.RxUtils;
import com.uber.autodispose.AutoDisposeConverter;

/**
 * Created by afirez on 2017/7/13.
 */
public abstract class BaseFragment<V extends IView, P extends IPresenter>
        extends Fragment
        implements IView, UiFactory<V, P> {
    protected P presenter;
    private V baseView;
    private BaseActivity baseActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            baseView = (V) context;
            baseActivity = ((BaseActivity) context);
        } catch (Exception e) {
            throw new RuntimeException("activity must implement IView");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = providePresenter(provideView());
        presenter.setLifecycleOwner(this);
        getLifecycle().addObserver(presenter);
    }

    @Override
    public void onDestroy() {
        baseView = null;
        baseActivity = null;
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

    protected void addFragment(Fragment fragment, @IdRes int containerId) {
        if (baseActivity != null) {
            baseActivity.addFragment(fragment, containerId);
        }
    }

    protected void removeFragment(Fragment fragment) {
        if (baseActivity != null) {
            baseActivity.removeFragment(fragment);
        }
    }

    protected void replaceFragment(Fragment fragment, @IdRes int containerId) {
        if (baseActivity != null) {
            baseActivity.replaceFragment(fragment, containerId);
        }
    }

    protected void hideFragment(Fragment fragment) {
        if (baseActivity != null) {
            baseActivity.hideFragment(fragment);
        }
    }

    protected void showFragment(Fragment fragment) {
        if (baseActivity != null) {
            baseActivity.showFragment(fragment);
        }
    }

    protected void popFragment() {
        if (baseActivity != null) {
            baseActivity.popFragment();
        }
    }

    public <T> AutoDisposeConverter<T> autoDisposeConverter() {
        return RxUtils.autoDisposeConverter(this);
    }
}