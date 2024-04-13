package com.example.recycle_app.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentAdapter_Overview_Items extends FragmentStateAdapter {

    private Bundle bundle;

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public FragmentAdapter_Overview_Items(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0 ) {
            return new Fragment_Overview();
        }
        Fragment_Items items = new Fragment_Items();
        items.setArguments(bundle);
        return items;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
