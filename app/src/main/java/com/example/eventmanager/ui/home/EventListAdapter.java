package com.example.eventmanager.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class EventListAdapter extends FragmentStateAdapter {
    public EventListAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment childFragment = new EventListFragment();
        Bundle args = new Bundle();

        args.putInt(EventListFragment.ARG_POS, position);
        childFragment.setArguments(args);
        return childFragment;
    }

    @Override
    public int getItemCount() {
        return HomeFragment.NUM_TABS;
    }
}
