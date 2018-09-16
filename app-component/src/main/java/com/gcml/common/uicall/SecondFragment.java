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

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment {


    public SecondFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.tv_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), SecondActivity.class);
//                UiCall.startActivityForResult(
//                        SecondFragment.this,
//                        intent, new UiCall.ResultCallback() {
//                            @Override
//                            public void onResult(UiCall.Result result) {
//                                Timber.i("ResultCallback.onResult");
//                                String msg = result.isSuccess() ? "成功" : "失败";
//                                tips(msg + "SecondActivity");
//                            }
//                        });
                UiCall.pushFragmentForResult(
                        SecondFragment.this,
                        FirstFragment.class,
                        new UiCall.ResultCallback() {
                            @Override
                            public void onResult(UiCall.Result result) {
                                Timber.i("ResultCallback.onResult");
                                String msg = result.isSuccess() ? "成功" : "失败";
                                tips(msg + "FirstFragment " + result.requestCode);
                            }
                        }
                );
            }
        });

        view.findViewById(R.id.tv_success).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiCall.success(SecondFragment.this);
                UiCall.finish(SecondFragment.this);
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
    public void onDetach() {
        super.onDetach();
    }
}
