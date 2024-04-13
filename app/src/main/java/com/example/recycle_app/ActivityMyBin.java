package com.example.recycle_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager2.widget.ViewPager2;

import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.recycle_app.Database.DatabaseHelper;
import com.example.recycle_app.Fragments.FragmentAdapter_Overview_Items;
import com.example.recycle_app.My_Bin.Bin_Item;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ActivityMyBin extends AppCompatActivity {

    ArrayList<Bin_Item> bin_itemArrayList = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bin);


//        NavController nav_controller = Navigation.findNavController(this, R.id.fragment_container_bin);
        ViewPager2 viewPager_mybin = findViewById(R.id.view_pager_mybin);
        FragmentAdapter_Overview_Items adapter_overview_items = new FragmentAdapter_Overview_Items(getSupportFragmentManager(),getLifecycle());

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        bin_itemArrayList = dbHelper.getBinData();

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("BinItems",bin_itemArrayList);

        adapter_overview_items.setBundle(bundle);
        viewPager_mybin.setAdapter(adapter_overview_items);

        TabLayout tabLayout = findViewById(R.id.btn_container);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager_mybin.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });


        viewPager_mybin.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position
                ));
            }
        });

    }


}