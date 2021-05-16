package com.example.eventmanager.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.model.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class EventListFragment extends Fragment {

    public static final String ARG_POS = "position";

    private List<Event> upcomingEvents;
    private List<Event> recentEvents;

    private EventRecyclerViewAdapter eventViewAdapter;

    public EventListFragment() {
        this.upcomingEvents = new ArrayList<>();
        this.recentEvents = new ArrayList<>();
    }

    public static EventListFragment newInstance() {
        return new EventListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.event_list_fragment, container, false);
        EventListViewModel mViewModel = new ViewModelProvider(this).get(EventListViewModel.class);

        final RecyclerView eventRecyclerView = root.findViewById(R.id.eventRecyclerView);
        Bundle args = getArguments();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa");
        Date presentDate = new Date();

        mViewModel.getEvents().observe(getViewLifecycleOwner(), eventRowItems -> {
            eventRecyclerView.setHasFixedSize(true);
            eventRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            
            upcomingEvents.clear();
            recentEvents.clear();
            for(Event event : eventRowItems) {
                String dateTime = event.getEventDate() + " " + event.getEventTime();
                try {
                    Date eDate = sdf.parse(dateTime);

                    assert eDate != null;
                    if(eDate.after(presentDate))
                        upcomingEvents.add(event);
                    else
                        recentEvents.add(event);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            switch (Objects.requireNonNull(args).getInt(ARG_POS)) {
                case 0:
                    //upcoming events
                    eventViewAdapter = new EventRecyclerViewAdapter(getActivity(), upcomingEvents);
                    break;
                case 1:
                    //recent events;
                    eventViewAdapter = new EventRecyclerViewAdapter(getActivity(), recentEvents);
                    break;
                default:
                    eventViewAdapter = new EventRecyclerViewAdapter(getActivity(), eventRowItems);
            }

            eventRecyclerView.setAdapter(eventViewAdapter);
        });

        //setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        switch (Objects.requireNonNull(args).getInt(ARG_POS)) {
            case 0:
                //upcoming events
                break;
            case 1:
                //recent events;
                break;
        }
    }

    public EventRecyclerViewAdapter getListViewAdapter() {
        return eventViewAdapter;
    }

}