package com.gcml.common.mvvm;

import android.app.Application;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

public class DemoViewModel extends BaseViewModel {
    private ObservableField<String> who = new ObservableField<>();

    public DemoViewModel(@NonNull Application application) {
        super(application);
    }

    public ObservableField<String> getWho() {
        return who;
    }
}
