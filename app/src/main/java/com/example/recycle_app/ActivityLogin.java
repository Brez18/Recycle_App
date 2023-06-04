package com.example.recycle_app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.recycle_app.Database.APIDataReader;

import org.json.JSONException;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener{

    EditText edt_name;
    EditText edt_password;

    @SuppressLint("SetTextI18n")
    public TranslateAnimation shakeError(int type_error) {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        TextView error_text = findViewById(R.id.text_error);

        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(5));

        error_text.setVisibility(View.VISIBLE);
        if(type_error == 1)
            error_text.setText("Invalid Password");

        return shake;
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        int flag;
        switch (view.getId())
        {
            case R.id.signIn:
                try {
                    flag = APIDataReader.verifyData(edt_name.getText().toString(), edt_password.getText().toString());
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

        Button signIn = findViewById(R.id.signIn);
        ImageView eye = findViewById(R.id.ic_eye);
        edt_name = findViewById(R.id.editText_name);
        edt_password = findViewById(R.id.editText_password);


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
        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_password.getTransformationMethod() != null) {
                    edt_password.setTransformationMethod(null);
                    eye.setImageResource(R.drawable.ic_open_eye_foreground);
                    edt_password.setSelection(edt_password.length());//to change focus of cursor to last element
                }
                else
                {
                    edt_password.setTransformationMethod(new PasswordTransformationMethod());
                    eye.setImageResource(R.drawable.ic_close_eye_foreground);
                    edt_password.setSelection(edt_password.length());//to change focus of cursor to last element
                }
            }
        });

    }

}