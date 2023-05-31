package com.example.recycle_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener{

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){

            case R.id.signIn:
                intent = new Intent(this , ActivityMain.class);
                startActivity(intent);
                break;
            case R.id.createAcc:
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

        signIn.setOnClickListener(this);

    }
}