package com.example.recycle_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.recycle_app.Database.APIDataReader;
import com.example.recycle_app.Database.DatabaseHelper;

import org.json.JSONException;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener{

    EditText edt_name;
    EditText edt_password;

    private DatabaseHelper db_helper = new DatabaseHelper();

    @SuppressLint("SetTextI18n")
    public TranslateAnimation shakeError(int type_error) {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        TextView error_text = findViewById(R.id.text_error);

        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(5));

        error_text.setVisibility(View.VISIBLE);
        if(type_error == 1)
            error_text.setText("Invalid Password");

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));

        return shake;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        int flag;
        switch (view.getId())
        {
            case R.id.LogIn:
//                flag = db_helper.login(edt_name.getText().toString(), edt_password.getText().toString());
                try {
                    flag = APIDataReader.verifyData(edt_name.getText().toString().trim(), edt_password.getText().toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if(flag == -1) {
                    findViewById(R.id.text_error).setVisibility(View.INVISIBLE);
                    intent = new Intent(this , ActivityMain.class);
                    startActivity(intent);
                } else if (flag == 0) {
                    edt_name.setText("");
                    edt_password.setText("");
                    edt_name.startAnimation(shakeError(0));
                    edt_password.startAnimation(shakeError(0));
                }
                else {//1
                    edt_password.setText("");
                    edt_password.startAnimation(shakeError(1));
                }
                break;

            case R.id.createAcc:
                intent = new Intent(this , ActivityAccountCreate.class);
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
        edt_name = findViewById(R.id.editText_mail);
        edt_password = findViewById(R.id.editText_password);

        TextWatcher(edt_name);
        TextWatcher(edt_password);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here
            APIDataReader.getData();
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

}