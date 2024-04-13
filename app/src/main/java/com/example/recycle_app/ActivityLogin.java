package com.example.recycle_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serial;


//import com.google.firebase.auth.FirebaseUser;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener{

    EditText edt_mail;
    EditText edt_password;

    FirebaseAuth auth = FirebaseAuth.getInstance();

    @SuppressLint("SetTextI18n")
    public TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        TextView error_text = findViewById(R.id.text_error);

        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(5));

        error_text.setVisibility(View.VISIBLE);

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));

        return shake;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.LogIn:

                String email = edt_mail.getText().toString(),password = edt_password.getText().toString();
                if(email.isEmpty() || password.isEmpty())
                {
                    edt_mail.startAnimation(shakeError());
                    edt_password.startAnimation(shakeError());
                }
                else {
                    ProgressBar circle = findViewById(R.id.progress_circle);
                    circle.setVisibility(View.VISIBLE);


                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    findViewById(R.id.text_error).setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    edt_mail.setText("");
                                    edt_password.setText("");
                                    edt_mail.startAnimation(shakeError());
                                    edt_password.startAnimation(shakeError());
                                }
                                circle.setVisibility(View.INVISIBLE);
                            });
                }
                break;

            case R.id.createAcc:
                Intent intent = new Intent(this , ActivityAccountCreate.class);
                startActivity(intent);
                break;

            case R.id.forgotPass:
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Button signIn = findViewById(R.id.LogIn);
        ImageView eye = findViewById(R.id.ic_eye);
        TextView create_acc = findViewById(R.id.createAcc);
        edt_mail = findViewById(R.id.editText_mail);
        edt_password = findViewById(R.id.editText_password);

        TextWatcher(edt_mail);
        TextWatcher(edt_password);

//        Implement Log Out also
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser != null){
            auth.signOut();
//            Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
//            startActivity(intent);
//            finish();
        }

        signIn.setOnClickListener(this);
        create_acc.setOnClickListener(this);
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

        // hide keyboard on click
        ConstraintLayout cl = findViewById(R.id.layout_login);
        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getCurrentFocus()!=null) {
                    InputMethodManager inm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
            }
        });
    }

    void TextWatcher(EditText edt)
    {
        edt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                TextView txt_view = findViewById(R.id.text_error);
                if(txt_view.getVisibility()==View.VISIBLE)
                    txt_view.setVisibility(View.GONE);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView error_text = findViewById(R.id.text_error);
        error_text.setVisibility(View.INVISIBLE);
    }
}