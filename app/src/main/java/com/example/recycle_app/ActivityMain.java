package com.example.recycle_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.recycle_app.Fragments.FragmentDashboard;
import com.example.recycle_app.Fragments.FragmentMaps;
import com.example.recycle_app.Fragments.FragmentMarketPlace;
import com.example.recycle_app.Fragments.FragmentNavigation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ActivityMain extends AppCompatActivity implements View.OnClickListener {
    FragmentNavigation fragmentNavigation = new FragmentNavigation();
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.expanded_menu:
                OpenFragment(fragmentNavigation);
                view.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenFragment(new FragmentDashboard());

        FloatingActionButton menu = findViewById(R.id.expanded_menu);
        menu.setOnClickListener(this);

    }
    private void OpenFragment(Fragment fragment ) {

        FragmentTransaction transaction =  getSupportFragmentManager().beginTransaction();


        if (fragment instanceof FragmentDashboard) {
            transaction.replace(R.id.fragment_container1, fragment);
            transaction.commit();
        } else if (fragment instanceof FragmentNavigation) {
            transaction.setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_left);
            transaction.replace(R.id.fragment_container2, fragment);
            transaction.commit();
        }
    }

}