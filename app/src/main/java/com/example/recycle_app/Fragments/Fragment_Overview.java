package com.example.recycle_app.Fragments;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.recycle_app.OnSwipeTouchListener;
import com.example.recycle_app.R;
import com.google.android.material.checkbox.MaterialCheckBox;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class Fragment_Overview extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment__overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CircularProgressIndicator circle = view.findViewById(R.id.progress_circle);
        TextView progress = view.findViewById(R.id.txt_progress);
        int num_progress = 34;
        ValueAnimator animator = ValueAnimator.ofInt(0, num_progress);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                progress.setText(animation.getAnimatedValue().toString());
            }
        });
        animator.start();
        circle.setProgress(34,100);

    }
}