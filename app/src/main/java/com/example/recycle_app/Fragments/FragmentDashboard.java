package com.example.recycle_app.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recycle_app.ActivityBin;
import com.example.recycle_app.ActivityCreateItem;
import com.example.recycle_app.My_Bin.Bin_Item;
import com.example.recycle_app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.Objects;

public class FragmentDashboard extends Fragment implements View.OnClickListener {

    private boolean flag = false;
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.card_bin:
                Intent bin_intent = new Intent(getContext(), ActivityBin.class);
                startActivity(bin_intent);
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

        RelativeLayout card_reference = view.findViewById(R.id.card_container);

        ViewCompat.setOnApplyWindowInsetsListener(card_reference, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply the insets as a margin to the view. This solution sets only the
            // bottom, left, and right dimensions, but you can apply whichever insets are
            // appropriate to your layout. You can also update the view padding if that's
            // more appropriate.
            if(insets.bottom!=0 && !flag) {
                RelativeLayout.MarginLayoutParams params = (RelativeLayout.MarginLayoutParams) v.getLayoutParams();

                params.bottomMargin = (int) (params.bottomMargin - 10 * getResources().getDisplayMetrics().density);
                v.setLayoutParams(params);
                flag = true;
            }

            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            return WindowInsetsCompat.CONSUMED;
        });

        CardView card_bin = view.findViewById(R.id.card_bin);

        card_bin.setOnClickListener(this);

        FloatingActionButton submit = view.findViewById(R.id.btn_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Your Bin Items have been submitted" , Toast.LENGTH_SHORT).show();
            }
        });



    }
}