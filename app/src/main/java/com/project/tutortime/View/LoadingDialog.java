package com.project.tutortime.View;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import com.project.tutortime.R;

// https://stackoverflow.com/questions/55060626/android-how-to-display-transparent-loading-layer-above-the-activity

public class LoadingDialog extends Dialog {
    private Context mContext;

    public LoadingDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View inflateView = inflater.inflate(R.layout.loading_dialog, (ViewGroup) findViewById(R.id.loading_cont));
        setContentView(inflateView);
    }
}