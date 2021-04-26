package com.example.eventmanager.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    private ClubViewAdapter clubViewAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final RecyclerView clubRecyclerView = root.findViewById(R.id.club_recycler_view);

        dashboardViewModel.getClubs().observe(getViewLifecycleOwner(),clubs -> {
            clubRecyclerView.setHasFixedSize(true);
            clubRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            clubViewAdapter = new ClubViewAdapter(getActivity(), clubs);
            clubRecyclerView.setAdapter(clubViewAdapter);
        });
        return root;

        /*View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;*/
    }
}