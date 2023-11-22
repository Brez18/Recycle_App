package com.example.recycle_app.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.method.PasswordTransformationMethod;
import android.transition.TransitionInflater;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recycle_app.R;

import java.util.Objects;

public class Fragment_credentials extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_credentials, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText edt_password = view.findViewById(R.id.editText_password);
        ImageView eye = view.findViewById(R.id.ic_eye);

        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_password.getTransformationMethod() != null) {
                    edt_password.setTransformationMethod(null);
                    eye.setImageResource(R.drawable.login_ic_open_eye_foreground);
                    edt_password.setSelection(edt_password.length());//to change focus of cursor to last element
                }
                else
                {
                    edt_password.setTransformationMethod(new PasswordTransformationMethod());
                    eye.setImageResource(R.drawable.login_ic_close_eye_foreground);
                    edt_password.setSelection(edt_password.length());//to change focus of cursor to last element
                }
            }
        });
    }

}