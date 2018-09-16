package com.gcml.common.uicall;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gcml.common.demo.R;
import com.gcml.common.utils.UiCall;

import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment {


    private AtomicInteger first = new AtomicInteger(0);
    private boolean hasInflated;
    private Bundle savedInstanceState;


    public FirstFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hasInflated = true;
        this.savedInstanceState = savedInstanceState;
        notifyIfNeeded();
    }

    private void notifyIfNeeded() {
        if (hasInflated) {
            if (getUserVisibleHint() && first.compareAndSet(0, 1)) {
                onInit(savedInstanceState);
            }
            if (getUserVisibleHint()) {
                onActive();
            } else {
                onInactive();
            }
        }
    }

    public void onInactive() {

    }

    public void onActive() {

    }

    private void onInit(Bundle savedInstanceState) {
        View view = getView();
        if (view == null) {
            return;
        }
        view.findViewById(R.id.tv_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiCall.pushFragmentForResult(
                        FirstFragment.this,
                        SecondFragment.class, new UiCall.ResultCallback() {
                            @Override
                            public void onResult(UiCall.Result result) {
                                String msg = result.isSuccess() ? "成功" : "失败";
                                tips(msg + "SecondFragment " + result.requestCode);
                            }
                        });
            }
        });
        view.findViewById(R.id.tv_success).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiCall.success(FirstFragment.this);
                UiCall.finish(FirstFragment.this);
            }
        });
    }

    public void tips(String msg) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Timber.i("setUserVisibleHint: isVisibleToUser = %s", isVisibleToUser);
        notifyIfNeeded();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        setUserVisibleHint(!hidden);
        setMenuVisibility(!hidden);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        savedInstanceState = null;
        hasInflated = false;
        first.set(0);
    }
}
