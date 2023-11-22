package com.example.recycle_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.recycle_app.Fragments.FragmentDashboard;
import com.example.recycle_app.Fragments.FragmentMaps;
import com.example.recycle_app.Fragments.FragmentMarketPlace;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class ActivityMain extends AppCompatActivity{

    private void OpenFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container1 , fragment);
        transaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar);
        bottomNavigationView.setSelectedItemId(R.id.ic_dashboard);

        View hidden_view = findViewById(R.id.hidden_view2);

        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply the insets as a margin to the view. This solution sets only the
            // bottom, left, and right dimensions, but you can apply whichever insets are
            // appropriate to your layout. You can also update the view padding if that's
            // more appropriate.
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            ViewGroup.LayoutParams hidden_viewLayoutParams = (ViewGroup.LayoutParams) hidden_view.getLayoutParams();

            mlp.bottomMargin = (int) (insets.bottom - 5*getResources().getDisplayMetrics().density);
            hidden_viewLayoutParams.height = insets.bottom;

            v.setLayoutParams(mlp);
            hidden_view.setLayoutParams(hidden_viewLayoutParams);

            // Return CONSUMED if you don't want want the window insets to keep passing
            // down to descendant views.
            return WindowInsetsCompat.CONSUMED;
        });

        OpenFragment(new FragmentDashboard());

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemID = item.getItemId();

                switch (itemID)
                {
                    case R.id.ic_dashboard:
                        OpenFragment(new FragmentDashboard());
                        break;
                    case R.id.ic_market:
                        OpenFragment(new FragmentMarketPlace());
                        break;
                    case R.id.ic_maps:
                        OpenFragment(new FragmentMaps());
                        break;

                }
                return true;
            }
        });

    }

}