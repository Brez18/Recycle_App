package com.example.recycle_app;

import static android.media.MediaRecorder.VideoSource.CAMERA;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import com.example.recycle_app.My_Bin.Bin_Item;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ActivityBin extends AppCompatActivity {

    ArrayList<Bin_Item> bin_itemArrayList = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bin);

        MaterialButton overview  = findViewById(R.id.btn_Overview);
        MaterialButton items  = findViewById(R.id.btn_Items);


        overview.setBackgroundColor(getColor(R.color.grey_green));
        items.setBackgroundColor(getColor(R.color.transparent));
        items.setTextColor(getColor(R.color.grey));

        NavController nav_controller = Navigation.findNavController(this, R.id.fragment_container_bin);

        //get bin items from database
        bin_itemArrayList.add(new Bin_Item("Bottle","#plastic","2","3"));
        bin_itemArrayList.add(new Bin_Item("Nails","#metal","1","4"));
        bin_itemArrayList.add(new Bin_Item("Cardboard","#paper","2","5"));

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("BinItems",bin_itemArrayList);

        overview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.setBackgroundColor(getColor(R.color.transparent));
                items.setTextColor(getColor(R.color.grey));
                overview.setTextColor(getColor(R.color.white));
                overview.setBackgroundColor(getColor(R.color.grey_green));
                nav_controller.navigate(R.id.fragment_Overview);
            }
        });

        items.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overview.setBackgroundColor(getColor(R.color.transparent));
                overview.setTextColor(getColor(R.color.grey));
                items.setTextColor(getColor(R.color.white));
                items.setBackgroundColor(getColor(R.color.grey_green));
                nav_controller.navigate(R.id.fragment_Items,bundle);
            }
        });

    }

}