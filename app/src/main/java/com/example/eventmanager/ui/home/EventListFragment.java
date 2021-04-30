package com.example.eventmanager.ui.home;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.eventmanager.R;

public class EventListFragment extends Fragment {

    public static final String ARG_POS = "position";

    private EventListViewModel mViewModel;
    private EventRecyclerViewAdapter eventViewAdapter;

    public static EventListFragment newInstance() {
        return new EventListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.event_list_fragment, container, false);
        mViewModel = new ViewModelProvider(this).get(EventListViewModel.class);

        final RecyclerView eventRecyclerView = root.findViewById(R.id.eventRecyclerView);

        mViewModel.getEvents().observe(getViewLifecycleOwner(), eventRowItems -> {
            eventRecyclerView.setHasFixedSize(true);
            eventRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            eventViewAdapter = new EventRecyclerViewAdapter(getActivity(), eventRowItems);
            eventRecyclerView.setAdapter(eventViewAdapter);
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        switch (args.getInt(ARG_POS)) {
            case 0:
                //upcoming events
                break;
            case 1:
                //recent events;
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(EventListViewModel.class);
        // TODO: Use the ViewModel
    }

}