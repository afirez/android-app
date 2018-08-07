package com.gcml.common.mvp;

/**
 * Created by afirez on 2017/7/12.
 */

public interface IPresenter {
    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
