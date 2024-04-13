package com.example.recycle_app.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recycle_app.ActivityMyBin;
import com.example.recycle_app.Database.DatabaseHelper;
import com.example.recycle_app.R;
import com.example.recycle_app.Server.MyResponse;
import com.example.recycle_app.Server.RetrofitCaller_Interface;
import com.example.recycle_app.Server.Retrofit_Server;
import com.example.recycle_app.Server.UserInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.protobuf.Any;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FragmentDashboard extends Fragment implements View.OnClickListener {

    private boolean flag = false;
    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.card_bin:
                Intent bin_intent = new Intent(getContext(), ActivityMyBin.class);
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView displayName = view.findViewById(R.id.displayName);

        if(user !=null) {
            Log.e("Tag",user.getEmail());
            Log.e("Tag",user.getDisplayName());
            Log.e("Tag",user.getPhoneNumber());
            displayName.setText(user.getDisplayName());
        }
        else
            displayName.setText("User");

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

                UserInfo userInfo = new UserInfo();
                Thread wait_thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userInfo.collect(FragmentDashboard.this.getContext());
                        while (!userInfo.getUpload()) {}
                        send_to_server(userInfo);
                    }
                });
                wait_thread.start();
            }
        });

    }

    void send_to_server(UserInfo userInfo)
    {
        Retrofit_Server retrofitServer = new Retrofit_Server();
        Retrofit retroInstance = retrofitServer.getRetrofitInstance();
        RetrofitCaller_Interface caller =retroInstance.create(RetrofitCaller_Interface.class);

        Call<MyResponse> responseCall = caller.sendBinDataToSever(userInfo);

        responseCall.enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                Log.e("Tag", String.valueOf(response.code()));
                MyResponse res = response.body();
                Log.e("Tag", res.message);

                Toast.makeText(getContext(), "Your Bin Items have been submitted" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

                Log.e("Tag", "Fail");
                Toast.makeText(getContext(), "Something went wrong" , Toast.LENGTH_SHORT).show();
            }
        });
    }
}