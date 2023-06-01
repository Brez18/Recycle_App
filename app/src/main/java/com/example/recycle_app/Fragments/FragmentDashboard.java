package com.example.recycle_app.Fragments;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;

import com.example.recycle_app.ActivityBin;
import com.example.recycle_app.R;
import com.yangp.ypwaveview.YPWaveView;

import java.util.Objects;

public class FragmentDashboard extends Fragment implements View.OnClickListener {

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.card_bin:
                Intent bin_intent = new Intent(getContext(), ActivityBin.class);
                getActivity().startActivity(bin_intent);
                break;

            case R.id.card_bin_history:
                break;

            case R.id.card_analysis:
                break;

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        YPWaveView progress_circle = view.findViewById(R.id.progress_circle);
        CardView bin = view.findViewById(R.id.card_bin);
        CardView bin_history = view.findViewById(R.id.card_bin_history);
        CardView analysis = view.findViewById(R.id.card_analysis);

        bin.setOnClickListener(this);
        analysis.setOnClickListener(this);


    }
}