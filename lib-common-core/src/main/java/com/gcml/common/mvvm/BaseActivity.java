package com.gcml.common.mvvm;

import android.arch.lifecycle.ViewModel;

import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity<B extends ViewDataBinding, VM extends ViewModel> extends AppCompatActivity {

    protected B binding;
    protected VM viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, layoutId());
        initView();
    }

    private void initView() {

    }

    protected abstract int layoutId();

}
