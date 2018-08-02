package com.afirez.log;

import android.app.Activity;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

public class BaseDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Callback")
                .setMessage("Click Ok")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        if (getTargetFragment() != null) {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, new Intent());
                        }
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                        if (getTargetFragment() != null) {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent());
                        }
                    }
                })
                .create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (getTargetFragment() != null) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, new Intent());
        }
    }
}
