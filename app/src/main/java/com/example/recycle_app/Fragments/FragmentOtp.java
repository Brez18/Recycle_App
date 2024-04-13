package com.example.recycle_app.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.recycle_app.Database.Firebase_handler;
import com.example.recycle_app.R;

public class FragmentOtp extends Fragment {

    CountDownTimer Timer = null;
    ProgressBar circle;

    public FragmentOtp() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView txt_Pnumber = view.findViewById(R.id.txt_Pnumber);
        circle = view.findViewById(R.id.progress_circle);
        String str = "Enter the OTP sent to +91-" + getArguments().getString("phone_no");
        Firebase_handler handler = getArguments().getParcelable("handler");
        TextView resend = view.findViewById(R.id.txt_resend);

        txt_Pnumber.setText(str);
        view.findViewById(R.id.progress_circle).setVisibility(View.VISIBLE);

        resend.setEnabled(false);
        resend.setAlpha(0.4f);

        Timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {}

            @Override
            public void onFinish() {
                resend.setEnabled(true);
                resend.setAlpha(1f);
            }
        };

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.sendOTP();
                resend.setEnabled(false);
                resend.setAlpha(0.4f);
                circle.setVisibility(View.VISIBLE);
                Timer.start();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Timer.cancel();
        circle.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Timer.start();
    }

}