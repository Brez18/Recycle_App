package com.example.recycle_app.Fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.example.recycle_app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigationrail.NavigationRailView;

public class FragmentNavigation extends Fragment implements View.OnClickListener, NavigationBarView.OnItemSelectedListener{

    NavigationRailView nav_rail_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navigation, container, false);
    }

    private void OpenFragment(Fragment fragment)
    {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container1 , fragment);
        transaction.commit();
    }

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
            case R.id.ic_contact_services:
                OpenFragment(new FragmentContactServices());
                break;

        }

        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.nav_back_btn:
                Fragment_dummy dummy = new Fragment_dummy();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.exit_to_left,R.anim.exit_to_left);
                transaction.replace(R.id.fragment_container2,dummy);
                transaction.commit();
                break;

        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton back_btn = view.findViewById(R.id.nav_back_btn);
        back_btn.setOnClickListener(this);

        nav_rail_view = view.findViewById(R.id.nav_rail);
        nav_rail_view.setOnItemSelectedListener(this);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().findViewById(R.id.expanded_menu).setVisibility(View.VISIBLE);
    }
}